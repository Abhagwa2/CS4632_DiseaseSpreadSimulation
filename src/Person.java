// src/Person.java
import java.util.Objects;

/**
 * Represents one agent in the simulation.
 * Tracks current health state and how long they've been in that state.
 */
public class Person {
    private final int id;
    private HealthState state;
    private int timeInStateSteps; // counts simulation steps spent in current state

    public Person(int id, HealthState initialState) {
        this.id = id;
        this.state = initialState;
        this.timeInStateSteps = 0;
    }

    public int getId() {
        return id;
    }

    public HealthState getState() {
        return state;
    }

    /**
     * Sets the state and resets the time-in-state counter.
     */
    public void setState(HealthState newState) {
        if (this.state != newState) {
            this.state = newState;
            this.timeInStateSteps = 0;
        }
    }

    public int getTimeInStateSteps() {
        return timeInStateSteps;
    }

    /**
     * Advance this person's internal timer by one simulation step.
     * Call this once per step from the Simulation loop.
     */
    public void tick() {
        timeInStateSteps++;
    }

    @Override
    public String toString() {
        return "Person{id=" + id + ", state=" + state + ", t=" + timeInStateSteps + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
