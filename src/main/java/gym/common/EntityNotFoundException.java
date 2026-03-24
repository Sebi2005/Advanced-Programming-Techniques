package gym.common;

public class EntityNotFoundException extends RepositoryException{
    public EntityNotFoundException(Object id) {
        super("Entity not found: "+id);
    }
}
