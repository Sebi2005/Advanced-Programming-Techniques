package gym.repository.core;
import gym.domain.Identifiable;
import java.util.Optional;
public interface Repository<ID, T extends Identifiable<ID>> {
    void create(T entity);
    Optional<T> read(ID id);
    Iterable<T> readAll();
    void update(T entity);
    Optional<T> delete(ID id);
    int count();
}
