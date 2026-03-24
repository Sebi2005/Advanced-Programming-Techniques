package gym.undo;

import java.util.ArrayDeque;
import java.util.Deque;

public class UndoRedoService {
    private final Deque<IAction> undoStack = new ArrayDeque<>();
    private final Deque<IAction> redoStack = new ArrayDeque<>();
    private boolean undoRedoInProgress = false;

    public boolean isUndoRedoInProgress() {
        return undoRedoInProgress;
    }

    public void record(IAction action) {
        if (undoRedoInProgress) return;
        undoStack.push(action);
        redoStack.clear();
    }

    public void undo() {
        if (undoStack.isEmpty()) throw new RuntimeException("No more undo.");
        undoRedoInProgress = true;
        try{
            IAction action = undoStack.pop();
            action.executeUndo();
            redoStack.push(action);
        } finally {
            undoRedoInProgress = false;
        }
    }

    public void redo() {
        if (redoStack.isEmpty()) throw new RuntimeException("No more redo.");
        undoRedoInProgress = true;
        try {
            IAction action = redoStack.pop();
            action.executeRedo();
            undoStack.push(action);
        } finally {
            undoRedoInProgress = false;
        }
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
}
