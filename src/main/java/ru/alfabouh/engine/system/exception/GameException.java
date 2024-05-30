package ru.alfabouh.engine.system.exception;

public class GameException extends RuntimeException {
    public GameException() {
    }

    public GameException(String ex) {
        super(ex);
    }

    public GameException(Exception ex) {
        super(ex);
    }
}
