# CS4632 Disease Spread Simulation

Semester project for CS4632 (Modeling & Simulation) at Kennesaw State University.  
Implements a **stochastic SIR (Susceptible–Infected–Recovered)** model of infectious disease spread in a population, with optional SEIR extension and interventions (e.g., vaccination, quarantine).  
The simulation is coded in **Java**, following the UML design created in Milestone 1.

---

## 📌 Project Description
- Simulates transmission in a closed population.  
- Individuals can be in one of three health states: **S (Susceptible)**, **I (Infected)**, **R (Recovered)**.  
- At each step:  
  - Infected persons attempt to infect `contactsPerStep` random contacts with probability `beta`.  
  - Infected persons recover with probability `gamma`.  
- Simulation runs until either:  
  - No infected individuals remain, or  
  - `maxSteps` is reached.  
- Metrics tracked: counts of S, I, R at each step.

---

## ⚙️ How to Compile & Run

From the repository root:

```bash
# Compile all source files into /bin
javac -d bin src/*.java

# Run the simulation (Main is the entry point)
java -cp bin Main
