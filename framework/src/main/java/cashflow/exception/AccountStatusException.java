package cashflow.exception;

/**
 * @author Cail Gainey
 * @since 2025/1/25 15:04
 **/
public class AccountStatusException extends RuntimeException {
    public AccountStatusException(String message) {
        super(message);
    }
}