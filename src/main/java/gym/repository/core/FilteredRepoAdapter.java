package gym.repository.core;
import gym.domain.Identifiable;
import gym.repository.core.FilteredRepository;
import gym.repository.core.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FilteredRepoAdapter<ID, T extends Identifiable<ID>> extends FilteredRepository<ID, T> {
    private final Repository<ID, T> delegate;

    public FilteredRepoAdapter(Repository<ID, T> delegate) {
        this.delegate = delegate;
    }

    @Override public void create(T entity) { delegate.create(entity); }
    @Override public Optional<T> read(ID id) { return delegate.read(id); }
    @Override public Iterable<T> readAll() { return delegate.readAll(); }
    @Override public void update(T entity) { delegate.update(entity); }
    @Override public Optional<T> delete(ID id) { return delegate.delete(id); }
    @Override public int count() { return delegate.count(); }

    @Override
    public Iterable<T> filter(AbstractFilter<T> filter) {
        List<T> filteredList = new ArrayList<>();
        for (T entity : delegate.readAll()) if (filter.accept(entity)) filteredList.add(entity);
        return filteredList;
    }
}
