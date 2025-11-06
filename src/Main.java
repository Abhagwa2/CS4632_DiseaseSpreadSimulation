// src/Main.java
import metrics.MetricsCollector;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {

        // --- Common parameters (can tweak later) ---
        int N = 50;
        int I0 = 2;
        double beta = 0.1;
        double gamma = 0.05;
        int k = 5;
        int maxSteps = 100;
        long seed = 44L;

        // --- Run three scenarios sequentially ---
        runScenario("baseline", N, I0, beta, gamma, k, maxSteps, seed);
        runScenario("vaccine30", N, I0, beta, gamma, k, maxSteps, seed);
        runScenario("quarantine50", N, I0, beta, gamma, k, maxSteps, seed);
        runScenario("combo", N, I0, beta, gamma, k, maxSteps, seed); 

    }

    /** Helper method: run one scenario and save results */
    private static void runScenario(String scenario, int N, int I0,
                                    double beta, double gamma, int k,
                                    int maxSteps, long seed) throws Exception {

        System.out.println("\n=== Running scenario: " + scenario + " ===");

        // Parameters for this run
        SimParams params = new SimParams(N, I0, beta, gamma, k, maxSteps, seed);

        MetricsCollector mc = new MetricsCollector();
        Simulation sim = new Simulation();
        sim.setMetricsCollector(mc);
        sim.setScenarioFolder(scenario);   // <-- key line
        sim.initialize(params);
        sim.run();

        // (optional) extra export for quick viewing
        Path out = Path.of("runs", scenario, "metrics_summary.csv");
        mc.writeCsv(out);
        System.out.println("Saved metrics summary to: " + out.toString());
    }
}
