package gym.undo;

import gym.domain.Identifiable;
import gym.repository.core.Repository;

public class ActionUpdate<ID, T extends Identifiable<ID>> implements IAction {
    private final Repository<ID, T> repository;
    private final T oldEntity;
    private final T newEntity;
    public ActionUpdate(Repository<ID, T> repository, T oldEntity, T newEntity) {
        this.repository = repository;
        this.oldEntity = oldEntity;
        this.newEntity = newEntity;
    }

    @Override
    public void executeUndo() {
        repository.update(oldEntity);
    }

    @Override
    public void executeRedo() {
        repository.update(newEntity);
    }

}
