// src/Main.java
import metrics.MetricsCollector;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        // SimParams must match your existing constructor/fields:
        // (populationSize, initialInfected, beta, gamma, contactsPerStep, maxSteps, seed)
        SimParams params = new SimParams(
            50,    // population size
            2,     // initial infected
            0.1,   // beta (infection prob per contact per step)
            0.05,  // gamma (recovery prob per step)
            5,     // contacts per step
            100,   // max steps
            42L    // RNG seed
        );

        MetricsCollector mc = new MetricsCollector();

        Simulation sim = new Simulation();
        sim.setMetricsCollector(mc);   // enable metrics recording
        sim.initialize(params);
        sim.run();

        // Export S/I/R time series for plots/screenshots
        mc.writeCsv(Path.of("metrics.csv"));
        System.out.println("Wrote metrics.csv with " + mc.size() + " rows.");
    }
}
