
// src/Simulation.java
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Simulation {
    private Population population;
    private SimParams params;
    private Random rng;
    private int step;

    public void initialize(SimParams params) {
        this.params = params;
        this.rng = new Random(params.seed);
        this.population = new Population(params.populationSize, HealthState.S);
        this.population.seedInitialInfections(params.initialInfected, rng);
        this.step = 0;

        System.out.printf("Initialized: N=%d, I0=%d, beta=%.3f, gamma=%.3f, k=%d, maxSteps=%d%n",
                params.populationSize, params.initialInfected, params.beta, params.gamma,
                params.contactsPerStep, params.maxSteps);
        printCounts();
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

        // 1) Potential infections this step
        Set<Person> toInfect = new HashSet<>();

        for (Person inf : infectedNow) {
            for (int c = 0; c < params.contactsPerStep; c++) {
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

        printCounts();
    }

    public void run() {
        while (step < params.maxSteps && population.count(HealthState.I) > 0) {
            step();
        }
        System.out.println("Simulation finished.");
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

    private void printCounts() {
        int s = population.count(HealthState.S);
        int i = population.count(HealthState.I);
        int r = population.count(HealthState.R);
        System.out.printf("Step %d: S=%d I=%d R=%d%n", step, s, i, r);
    }
}
