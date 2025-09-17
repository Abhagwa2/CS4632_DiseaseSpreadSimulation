// src/SimParams.java
public class SimParams {
    public final int populationSize;
    public final int initialInfected;
    public final double beta;        // infection probability per contact per step
    public final double gamma;       // recovery probability per step
    public final int contactsPerStep;
    public final int maxSteps;
    public final long seed;

    public SimParams(int populationSize, int initialInfected, double beta, double gamma,
                     int contactsPerStep, int maxSteps, long seed) {
        this.populationSize = populationSize;
        this.initialInfected = initialInfected;
        this.beta = beta;
        this.gamma = gamma;
        this.contactsPerStep = contactsPerStep;
        this.maxSteps = maxSteps;
        this.seed = seed;
    }
}
