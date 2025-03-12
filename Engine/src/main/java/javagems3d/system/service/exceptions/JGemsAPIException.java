package javagems3d.system.service.exceptions;

public class JGemsAPIException extends JGemsException {
    public JGemsAPIException() {
    }

    public JGemsAPIException(String ex) {
        super(ex);
    }

    public JGemsAPIException(String ex, Exception e) {
        super(ex, e);
    }

    public JGemsAPIException(Exception ex) {
        super(ex);
    }
}
