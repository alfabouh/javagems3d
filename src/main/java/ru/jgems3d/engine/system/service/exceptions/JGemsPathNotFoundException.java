package ru.jgems3d.engine.system.service.exceptions;

public class JGemsPathNotFoundException extends JGemsException {
    public JGemsPathNotFoundException() {
    }

    public JGemsPathNotFoundException(String ex) {
        super(ex);
    }

    public JGemsPathNotFoundException(String ex, Exception e) {
        super(ex, e);
    }

    public JGemsPathNotFoundException(Exception ex) {
        super(ex);
    }
}
