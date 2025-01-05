package africa.semicolon.urlShortener.data.repositories;

import africa.semicolon.urlShortener.data.models.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortenedUrl(String shortenedUrl);
    Optional<Url> findByOriginalUrl(String originalUrl);
}
