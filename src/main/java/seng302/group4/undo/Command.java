package seng302.group4.undo;

public abstract class Command<T> {

    abstract T execute();

    void redo() {
        this.execute();
    }

    abstract void undo();
}
