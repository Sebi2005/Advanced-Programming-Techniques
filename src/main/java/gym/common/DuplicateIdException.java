package gym.common;

public class DuplicateIdException extends RepositoryException{
    public DuplicateIdException(Object id) {
        super("ID already exists: "+ id);
    }
}
