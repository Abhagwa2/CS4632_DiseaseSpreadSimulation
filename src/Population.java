// src/Population.java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Container for all people in the simulation.
 * Provides helpers to seed infections, query by state, and get counts.
 */
public class Population {
    private final List<Person> people = new ArrayList<>();

    public Population() {}

    /** Create N people, all starting in the given state (default S in practice). */
    public Population(int size, HealthState initialState) {
        for (int i = 0; i < size; i++) {
            people.add(new Person(i, initialState));
        }
    }

    /** Add a single person (useful for custom setups / tests). */
    public void addPerson(Person p) {
        people.add(p);
    }

    /** @return immutable view of all people */
    public List<Person> all() {
        return Collections.unmodifiableList(people);
    }

    public int size() {
        return people.size();
    }

    /** Get a new list containing only the people currently in the requested state. */
    public List<Person> getByState(HealthState s) {
        List<Person> out = new ArrayList<>();
        for (Person p : people) {
            if (p.getState() == s) out.add(p);
        }
        return out;
    }

    public int count(HealthState s) {
        int c = 0;
        for (Person p : people) if (p.getState() == s) c++;
        return c;
    }

    /**
     * Randomly select 'count' currently Susceptible people and set them to Infected.
     * If there are fewer than 'count' susceptibles, infect as many as possible.
     */
    public void seedInitialInfections(int count, Random rng) {
        List<Person> susceptibles = getByState(HealthState.S);
        if (susceptibles.isEmpty()) return;

        // Shuffle a copy to avoid modifying original order
        List<Person> pool = new ArrayList<>(susceptibles);
        Collections.shuffle(pool, rng);

        int toInfect = Math.min(count, pool.size());
        for (int i = 0; i < toInfect; i++) {
            pool.get(i).setState(HealthState.I);
        }
    }

    /** Advance internal timers for everyone by one step (call each simulation step). */
    public void tickAll() {
        for (Person p : people) p.tick();
    }
}
