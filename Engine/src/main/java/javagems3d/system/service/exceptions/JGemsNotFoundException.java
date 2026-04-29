package javagems3d.system.service.exceptions;

public class JGemsNotFoundException extends JGemsException {
    public JGemsNotFoundException() {
    }

    public JGemsNotFoundException(String ex) {
        super(ex);
    }

    public JGemsNotFoundException(String ex, Exception e) {
        super(ex, e);
    }

    public JGemsNotFoundException(Exception ex) {
        super(ex);
    }
}
