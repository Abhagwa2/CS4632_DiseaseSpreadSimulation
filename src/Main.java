public class Main {
    public static void main(String[] args) {
        SimParams params = new SimParams(
            50, 2, 0.1, 0.05, 5, 100, 42L
        );
        Simulation sim = new Simulation();
        sim.initialize(params);
        sim.run();
    }
}
