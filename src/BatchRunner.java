// src/BatchRunner.java
import metrics.MetricsCollector;
import java.nio.file.Path;

public class BatchRunner {

    // Tweak these to your baseline
    static final int N = 50, I0 = 2, k = 5, maxSteps = 200;
    static final double beta = 0.10, gamma = 0.05;

    // Choose your replication seeds
    static final long[] SEEDS = {
        42,43,44,45,46,47,48,49,50,51,
        52,53,54,55,56,57,58,59,60,61
    };

    static void runScenarioOnce(String scenario, long seed) throws Exception {
        SimParams params = new SimParams(N, I0, beta, gamma, k, maxSteps, seed);

        MetricsCollector mc = new MetricsCollector();
        Simulation sim = new Simulation();
        sim.setScenarioFolder(scenario);
        sim.setMetricsCollector(mc);
        sim.initialize(params);
        sim.run();

        // also write a quick per-run summary CSV (duplicate of run file, but handy)
        Path out = Path.of("runs", scenario, "metrics_summary_seed" + seed + ".csv");
        mc.writeCsv(out);
        System.out.println("Saved " + scenario + " summary to: " + out);
    }

    public static void main(String[] args) throws Exception {
        String[] scenarios = { "baseline", "vaccine30", "quarantine50", "combo" };

        for (String scenario : scenarios) {
            System.out.println("\n=== BATCH: " + scenario + " ===");
            for (long seed : SEEDS) {
                runScenarioOnce(scenario, seed);
            }
        }
        System.out.println("\nBatch complete. All CSVs saved under runs/<scenario>/");
    }
}
