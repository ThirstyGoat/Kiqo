package seng302.group4.undo;

public interface Command<T> {

    T execute();

    void undo();
}
