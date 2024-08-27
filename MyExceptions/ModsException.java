package MyExceptions;

public class ModsException extends Exception {
    public ModsException(String message) {
        super(message);
    }

    public ModsException(String message, Throwable cause) {
        super(message, cause);
    }
}