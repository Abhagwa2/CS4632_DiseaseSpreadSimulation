# 🧬 CS4632: Disease Spread Simulation (Final Project)
**Author:** Alejandro Bhagwandeen  
**Course:** CS4632 – Modeling and Simulation, Kennesaw State University  
**Semester:** Fall 2025  

---

## 📘 Overview
This project simulates the spread of an infectious disease through a closed population using a **stochastic SIR (Susceptible–Infected–Recovered)** model implemented in **Java**.  
It models disease transmission dynamics under different public-health interventions and collects statistical metrics across multiple simulation runs.

The system demonstrates:
- Probabilistic infection and recovery dynamics  
- Agent-based population interaction  
- Vaccination and quarantine interventions  
- Data collection and statistical validation  
- Multi-scenario batch execution and analysis  

---

## ⚙️ Features
| Feature | Description |
|----------|--------------|
| **Baseline SIR Simulation** | Models random contact-based disease spread with infection probability (β) and recovery rate (γ). |
| **Vaccination Scenario** | 30 % of susceptibles start immune. |
| **Quarantine Scenario** | Contacts per infected reduced by 50 % when prevalence > 5 %. |
| **Combo Scenario** | 20 % vaccination + 50 % contact reduction after threshold. |
| **BatchRunner** | Automates 20 random-seed replications for all scenarios. |
| **MetricsCollector** | Records S/I/R counts per step and exports CSVs. |
| **Aggregator** | Calculates mean, SD, and 95 % CI for metrics (peak I, time to peak, duration, etc.). |

---

## 🧱 Project Structure
CS4632_DiseaseSpreadSimulation/
│
├── src/
│ ├── HealthState.java
│ ├── Person.java
│ ├── Population.java
│ ├── SimParams.java
│ ├── Simulation.java
│ ├── Main.java
│ ├── BatchRunner.java
│ └── Aggregator.java
│
├── runs/
│ ├── baseline/
│ ├── vaccine30/
│ ├── quarantine50/
│ └── combo/
│
├── analysis/
│ └── summaries/
│
├── .gitignore
└── README.md
--------------------------

---

## 🧩 How to Compile & Run

### 1️⃣ Compile all Java files
From the repository root:
```bash
javac -d bin src/**/*.java
java -cp bin Main
java -cp bin BatchRunner
runs/<scenario>/run_seedXX.csv
java -cp bin Aggregator
analysis/summaries/<scenario>_summary.csv
