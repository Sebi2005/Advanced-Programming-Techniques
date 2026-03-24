package gym.repository.core;

import gym.domain.Identifiable;
import java.util.ArrayList;
import java.util.List;
public class FilteredRepository<ID, T extends Identifiable<ID>>
        extends InMemoryRepository<ID, T> {

    public Iterable<T> filter(AbstractFilter<T> filter) {
        List<T> filteredEntities = new ArrayList<>();
        for (T entity : data.values()) {
            if (filter.accept(entity)) filteredEntities.add(entity);
        }
        return filteredEntities;
    }
}
