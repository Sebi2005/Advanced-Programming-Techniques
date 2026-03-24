package gym.undo;
import gym.domain.Identifiable;
import gym.repository.core.Repository;

public class ActionRemove<ID, T extends Identifiable<ID>> implements IAction {
    private final Repository<ID,T> repository;
    private final T removedEntity;
    public ActionRemove(Repository<ID,T> repository, T removedEntity) {
        this.repository = repository;
        this.removedEntity = removedEntity;
    }

    @Override
    public void executeUndo() {
        repository.create(removedEntity);
    }

    @Override
    public void executeRedo() {
        repository.delete(removedEntity.getId());
    }

}
