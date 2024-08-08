package ru.jgems3d.engine.system.service.exceptions;

public class JGemsValidationException extends JGemsException {
    public JGemsValidationException() {
    }

    public JGemsValidationException(String ex) {
        super(ex);
    }

    public JGemsValidationException(String ex, Exception e) {
        super(ex, e);
    }

    public JGemsValidationException(Exception ex) {
        super(ex);
    }
}
