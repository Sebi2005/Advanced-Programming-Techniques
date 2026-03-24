package gym.repository.core;

@FunctionalInterface
public interface AbstractFilter<T> {
    boolean accept(T entity);
}
