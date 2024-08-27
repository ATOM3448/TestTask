package task.MyExceptions;

public class ArgumentsHandlerException extends Exception {
    public ArgumentsHandlerException(String message) { super(message); }

    public ArgumentsHandlerException(String message, Throwable cause) { super(message, cause); }
}