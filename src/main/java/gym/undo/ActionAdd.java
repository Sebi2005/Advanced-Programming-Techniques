package gym.undo;
import gym.domain.Identifiable;
import gym.repository.core.Repository;

public class ActionAdd<ID,T extends Identifiable<ID>> implements IAction {
    private final Repository<ID,T> repository;
    private final T addedEntity;
    public ActionAdd(Repository<ID,T> repository, T addedEntity) {
        this.repository = repository;
        this.addedEntity = addedEntity;
    }
    @Override
    public void executeUndo() {
        repository.delete(addedEntity.getId());
    }

    @Override
    public void executeRedo() {
        repository.create(addedEntity);
    }
}
