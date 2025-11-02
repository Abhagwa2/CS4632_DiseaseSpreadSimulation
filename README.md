CS4632 Disease Spread Simulation

Semester project for CS4632 (Modeling & Simulation) at Kennesaw State University.
Implements a stochastic SIR (Susceptible–Infected–Recovered) model of infectious disease spread in a population, with optional SEIR extension and intervention scenarios (e.g., vaccination, quarantine, and combined strategies).
The simulation is coded in Java, following the UML design created in Milestone 1.

Project Description

Simulates transmission in a closed population of agents.

Each agent can be in one of three health states:
S (Susceptible) → I (Infected) → R (Recovered).

At each time step:

Infected individuals contact kEff random agents and transmit infection with probability betaEff.

Infected individuals recover with probability gamma.

The simulation continues until:

No infected individuals remain, or

The specified maxSteps limit is reached.

Output metrics (S, I, R) are recorded per step in CSV files for analysis.

Supported Scenarios

baseline

Default SIR model (no interventions).

Implementation: betaEff = beta; kEff = k;

vaccine30

30% of the population effectively vaccinated (reduces infection rate).

Implementation: betaEff = 0.7 * beta;

quarantine50

Contact rate cut by 50% once more than 5% of the population is infected.

Implementation: if (prevalence > 0.05) kEff = 0.5 * kEff;

combo

Combines vaccination (beta reduced by 30%) and quarantine (k reduced by 50% when >5% infected).

Implementation: both effects applied together.

How to Compile and Run

From the repository root:

Compile all Java source files into /bin

javac -d bin src/*.java

Then, run the simulation for a chosen scenario:

Run the baseline scenario

java -cp bin Main baseline

Run the vaccination scenario

java -cp bin Main vaccine30

Run the quarantine scenario

java -cp bin Main quarantine50

Run the combined (vaccine + quarantine) scenario

java -cp bin Main combo

Output

Each run generates CSV output in the output/<scenario>/ directory:

output/
├── baseline/
│ ├── run1.csv
│ ├── run2.csv
│ └── run3.csv
├── vaccine30/
├── quarantine50/
└── combo/

Each CSV contains step-by-step values for:
step, S, I, R

These files can be used to plot line charts (for example, Infected vs Step) for Milestone 4 analysis and validation.
