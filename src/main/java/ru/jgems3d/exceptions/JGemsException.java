package ru.jgems3d.exceptions;

public class JGemsException extends RuntimeException {
    public JGemsException() {
    }

    public JGemsException(String ex) {
        super(ex);
    }

    public JGemsException(String ex, Exception e) {
        super(ex, e);
    }

    public JGemsException(Exception ex) {
        super(ex);
    }
}
