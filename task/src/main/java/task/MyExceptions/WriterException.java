package task.MyExceptions;

public class WriterException extends Exception {
    public WriterException(String message) { super(message); }

    public WriterException(String message, Throwable cause) { super(message, cause); }
}