package ru.BouH.engine.game.exception;

public class GameException extends RuntimeException {
    public GameException(String ex) {
        super(ex);
    }

    public GameException(Exception ex) {
        super(ex);
    }
}
