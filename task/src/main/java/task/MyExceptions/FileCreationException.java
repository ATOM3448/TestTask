package task.MyExceptions;

public class FileCreationException extends WriterException {
    public FileCreationException(String message) { super(message); }

    public FileCreationException(String message, Throwable cause) { super(message, cause); }
}