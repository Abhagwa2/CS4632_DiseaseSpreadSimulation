// src/Simulation.java
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

// metrics hook (make sure src/metrics/MetricsCollector.java exists)
import metrics.MetricsCollector;

public class Simulation {
    private Population population;
    private SimParams params;
    private Random rng;
    private int step;
    private MetricsCollector metrics;

    // scenario-specific output folder (baseline / vaccine30 / quarantine50)
    private String scenarioFolder = "baseline";

    /** Allow Main (or tests) to attach a metrics collector. */
    public void setMetricsCollector(MetricsCollector metrics) {
        this.metrics = metrics;
    }

    /** Choose which folder to save CSVs into. */
    public void setScenarioFolder(String name) {
        this.scenarioFolder = (name == null || name.isBlank()) ? "baseline" : name;
    }

    public String getScenarioFolder() { return this.scenarioFolder; }

    /** Optional accessor (handy for tests/UI). */
    public int getStep() { return step; }

    public void initialize(SimParams params) {
        this.params = params;
        this.rng = new Random(params.seed);
        this.population = new Population(params.populationSize, HealthState.S);

        // seed initial infections
        this.population.seedInitialInfections(params.initialInfected, rng);
        this.step = 0;

        // ensure we have a metrics sink (even if setMetricsCollector was never called)
        if (this.metrics == null) {
            this.metrics = new MetricsCollector();
        }

        // --- apply scenario-specific setup BEFORE printing/recording step 0 ---
        if ("vaccine30".equals(scenarioFolder)) {
            vaccinate(0.30); // move 30% of susceptibles to Recovered at t=0
            System.out.println("[vaccine30] Applied 30% vaccination at t=0.");
        }
       // else if ("combo".equals(scenarioFolder)) {
   // vaccinate(0.20); // vaccinate 20%
   // System.out.println("[combo] Applied 20% vaccination at t=0.");
//} set up twords the combo 


        System.out.printf(
            "Initialized: N=%d, I0=%d, beta=%.3f, gamma=%.3f, k=%d, maxSteps=%d, scenario=%s%n",
            params.populationSize, params.initialInfected, params.beta, params.gamma,
            params.contactsPerStep, params.maxSteps, scenarioFolder
        );

        // initial counts (step 0)
        printCounts();

        // record initial row at step 0
        if (metrics != null) {
            int s0 = population.count(HealthState.S);
            int i0 = population.count(HealthState.I);
            int r0 = population.count(HealthState.R);
            metrics.record(step, s0, i0, r0);
        }
    }

    /** Execute one simulation step:
     *  1) collect infection targets (don’t mutate mid-scan)
     *  2) collect recoveries
     *  3) apply state changes
     *  4) tick time + increment step
     */
    public void step() {
        // Snapshot who is infected at the start of the step
        List<Person> infectedNow = population.getByState(HealthState.I);

        // Determine effective contacts per infected for this step
        int kEff = params.contactsPerStep;
        if ("quarantine50".equals(scenarioFolder)) {
            double prevalence = (double) population.count(HealthState.I) / population.size();
            if (prevalence > 0.05) { // trigger when >5% infected
                kEff = Math.max(1, (int)Math.round(0.5 * kEff)); // reduce by 50%
            }
        }
      //  else if ("combo".equals(scenarioFolder)) {
   // double prevalence = (double) population.count(HealthState.I) / population.size();
   // if (prevalence > 0.05) {
     //   kEff = Math.max(1, (int)Math.round(0.5 * kEff)); // quarantine effect
   // }
//} same change here 


        // 1) Potential infections this step
        Set<Person> toInfect = new HashSet<>();
        for (Person inf : infectedNow) {
            for (int c = 0; c < kEff; c++) {
                Person other = randomOtherPerson(inf);
                if (other.getState() == HealthState.S) {
                    if (rng.nextDouble() < params.beta) {
                        toInfect.add(other);
                    }
                }
            }
        }

        // 2) Potential recoveries this step
        List<Person> toRecover = new ArrayList<>();
        for (Person inf : infectedNow) {
            if (rng.nextDouble() < params.gamma) {
                toRecover.add(inf);
            }
        }

        // 3) Apply state changes
        for (Person p : toInfect)   p.setState(HealthState.I);
        for (Person p : toRecover)  p.setState(HealthState.R);

        // 4) Advance internal timers and increment step
        population.tickAll();
        step++;

        // print post-step counts
        printCounts();

        // record counts for this step
        if (metrics != null) {
            int s = population.count(HealthState.S);
            int i = population.count(HealthState.I);
            int r = population.count(HealthState.R);
            metrics.record(step, s, i, r);
        }
    }

    public void run() {
        while (step < params.maxSteps && population.count(HealthState.I) > 0) {
            step();
        }
        System.out.println("Simulation finished.");

        // --- Write metrics CSV to runs/<scenarioFolder>/run_seed<seed>.csv ---
        if (metrics != null) {
            try {
                Path out = Paths.get("runs", scenarioFolder, "run_seed" + params.seed + ".csv");
                Files.createDirectories(out.getParent());   // ensure folder exists
                metrics.writeCsv(out);
                System.out.println("Wrote metrics to: " + out);
            } catch (IOException e) {
                System.err.println("Failed to write CSV: " + e.getMessage());
            }
        }
    }

    private Person randomOtherPerson(Person notThisOne) {
        // pick a random index; if it equals notThisOne, pick again (few tries; good enough for classroom scale)
        int n = population.size();
        Person p;
        do {
            p = population.all().get(rng.nextInt(n));
        } while (p == notThisOne && n > 1);
        return p;
    }

    // pretty table printout
    private void printCounts() {
        int s = population.count(HealthState.S);
        int i = population.count(HealthState.I);
        int r = population.count(HealthState.R);

        if (step == 0) {
            // print header once
            System.out.printf("%-6s %-10s %-10s %-10s%n", "Step", "Suscept.", "Infected", "Recovered");
            System.out.println("--------------------------------------------");
        }
        System.out.printf("%-6d %-10d %-10d %-10d%n", step, s, i, r);
    }

    // Move a fraction of current susceptibles to Recovered at t=0
    private void vaccinate(double fraction) {
        if (fraction <= 0) return;
        var susceptibles = population.getByState(HealthState.S);
        if (susceptibles == null || susceptibles.isEmpty()) return;

        // shuffle to pick random susceptibles
        java.util.Collections.shuffle(susceptibles, rng);
        int toVaccinate = (int)Math.round(fraction * susceptibles.size());
        toVaccinate = Math.min(toVaccinate, susceptibles.size());

        for (int i = 0; i < toVaccinate; i++) {
            susceptibles.get(i).setState(HealthState.R);
        }
    }
}
