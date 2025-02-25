package cashflow.exception;

public class RedisUpdateException extends RuntimeException {
    public RedisUpdateException(String message) {
        super(message);
    }

    public RedisUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}