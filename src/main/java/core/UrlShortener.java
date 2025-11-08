package core;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import storage.InMemoryStore;

public class UrlShortener {
  private static final long DEFAULT_TTL = TimeUnit.HOURS.toMillis(1);
  private final InMemoryStore store;
  private final Random random = new Random();

  private final Config config;

  public UrlShortener(InMemoryStore store, Config config) {
    this.store = store;
    this.config = config;
  }

  public String create(UUID owner, String url, Integer maxClicks, Long ttlMinutes) {
    validateUrl(owner, url);

    long ttl =
        (ttlMinutes != null && ttlMinutes > 0)
            ? TimeUnit.MINUTES.toMillis(ttlMinutes)
            : TimeUnit.MINUTES.toMillis(config.getDefaultTTLMinutes());

    int max = (maxClicks != null) ? maxClicks : config.getMaxClicksDefault();

    String code = generateShortCode();
    while (store.get(code) != null) {
      code = generateShortCode();
    }

    long now = Instant.now().toEpochMilli();
    LinkModel link = new LinkModel(owner, url, code, max, now, now + ttl);
    store.put(code, link);
    return code;
  }

  private String generateShortCode() {
    String chars = config.getShortCodeCharset();
    int length = config.getShortCodeLength();
    StringBuilder sb = new StringBuilder(length);
    Random random = new Random();
    for (int i = 0; i < length; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
  }

  private void validateUrl(UUID owner, String url) {
    if (url == null || url.isBlank()) throw new ValidationException("URL не может быть пустым");
    if (url.length() > config.getMaxUrlLength())
      throw new ValidationException("URL слишком длинный (макс. " + config.getMaxUrlLength() + ")");
    if (!url.matches("^(https?://).+"))
      throw new ValidationException("URL должен начинаться с http:// или https://");

    for (String code : store.getUserLinks(owner)) {
      LinkModel l = store.get(code);
      if (l != null && l.original.equalsIgnoreCase(url)) {
        throw new ValidationException("Такая ссылка уже существует: " + code);
      }
    }
  }

  public LinkModel get(String code) {
    return store.get(code);
  }

  public void delete(String code) {
    store.remove(code);
  }

  public Set<String> list(UUID owner) {
    return store.getUserLinks(owner);
  }

  public void update(LinkModel link) {
    store.put(link.shortCode, link);
  }
}
