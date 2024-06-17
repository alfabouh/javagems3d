package ru.alfabouh.jgems3d.engine.system.exception;

public class JGemsException extends RuntimeException {
    public JGemsException() {
    }

    public JGemsException(String ex) {
        super(ex);
    }

    public JGemsException(Exception ex) {
        super(ex);
    }
}
