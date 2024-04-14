package ru.alfabouh.engine.game.exception;

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
