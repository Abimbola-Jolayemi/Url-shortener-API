package africa.semicolon.urlShortener.dtos;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UrlRequest {
    @URL(message = "Invalid URL format")
    private String originalUrl;
}
