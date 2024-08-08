package ru.jgems3d.engine.system.service.exceptions;

public class JGemsNullObjectException extends JGemsException {
    public JGemsNullObjectException() {
    }

    public JGemsNullObjectException(String ex) {
        super(ex);
    }

    public JGemsNullObjectException(String ex, Exception e) {
        super(ex, e);
    }

    public JGemsNullObjectException(Exception ex) {
        super(ex);
    }
}
