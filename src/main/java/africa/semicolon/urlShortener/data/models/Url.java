package africa.semicolon.urlShortener.data.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Entity
@Data
@Table(name = "urls")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @URL(message = "Invalid URL format")
    private String originalUrl;
    private String shortenedUrl;
}
