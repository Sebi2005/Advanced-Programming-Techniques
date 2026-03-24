package gym.undo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeAction implements IAction {
    private final List<IAction> actions;
    public CompositeAction(List<IAction> actions) {
        this.actions = new ArrayList<>(actions);
    }

    @Override
    public void executeUndo() {
        List<IAction> reversed = new ArrayList<>(actions);
        Collections.reverse(reversed);
        for (IAction action : reversed) {
            action.executeUndo();
        }
    }

    @Override
    public void executeRedo() {
        for (IAction action : actions) {
            action.executeRedo();
        }
    }
}
