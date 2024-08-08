package ru.jgems3d.engine.system.service.exceptions;

public class JGemsRuntimeException extends JGemsException {
    public JGemsRuntimeException() {
    }

    public JGemsRuntimeException(String ex) {
        super(ex);
    }

    public JGemsRuntimeException(String ex, Exception e) {
        super(ex, e);
    }

    public JGemsRuntimeException(Exception ex) {
        super(ex);
    }
}
