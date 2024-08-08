package ru.jgems3d.engine.system.service.exceptions;

public class JGemsNullException extends JGemsException {
    public JGemsNullException() {
    }

    public JGemsNullException(String ex) {
        super(ex);
    }

    public JGemsNullException(String ex, Exception e) {
        super(ex, e);
    }

    public JGemsNullException(Exception ex) {
        super(ex);
    }
}
