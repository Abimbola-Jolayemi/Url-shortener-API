package africa.semicolon.urlShortener.dtos;

import lombok.Data;

@Data
public class UrlResponse {
    private String originalUrl;
    private String shortenedUrl;
}
