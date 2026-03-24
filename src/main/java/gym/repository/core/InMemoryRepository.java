package gym.repository.core;
import gym.domain.Identifiable;
import java.util.HashMap;
import java.util.Map;
import gym.common.DuplicateIdException;
import gym.common.EntityNotFoundException;
import gym.common.RepositoryException;
import java.util.Optional;
public class InMemoryRepository<ID, T extends Identifiable<ID>> implements Repository<ID, T>  {
    protected final Map<ID, T> data = new HashMap<>();
    @Override
    public void create(T entity) {
        if (entity == null || entity.getId() == null) {
            throw new RepositoryException("Entity and ID must not be null.");
        }
        if (data.containsKey(entity.getId())) {
            throw new DuplicateIdException(entity.getId());
        }
        data.put(entity.getId(), entity);

    }
    @Override
    public Optional<T> read(ID id) {
        /*T entity = data.get(id);
        if (entity == null) {
            throw new EntityNotFoundException(id);
        }*/
        return Optional.ofNullable(data.get(id));
    }
    @Override
    public Iterable<T> readAll() {
        return data.values();
    }
    @Override
    public void update(T entity) {
        if (entity == null || entity.getId() == null) {
            throw new RepositoryException("Entity and ID must not be null.");
        }
        if (!data.containsKey(entity.getId())) {
            throw new EntityNotFoundException(entity.getId());
        }
        data.put(entity.getId(), entity);
    }
    @Override
    public Optional<T> delete(ID id) {
        return Optional.ofNullable(data.remove(id));
    }
    @Override
    public int count() {
        return data.size();
    }
}
