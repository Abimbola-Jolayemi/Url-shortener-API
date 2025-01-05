package africa.semicolon.urlShortener.exceptions;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException() {
        super("Url not found");
    }

    public UrlNotFoundException(String message) {
        super(message);
    }

    public UrlNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
