package ru.jgems3d.engine.system.service.exceptions;

public class JGemsIOException extends JGemsException {
    public JGemsIOException() {
    }

    public JGemsIOException(String ex) {
        super(ex);
    }

    public JGemsIOException(String ex, Exception e) {
        super(ex, e);
    }

    public JGemsIOException(Exception ex) {
        super(ex);
    }
}
