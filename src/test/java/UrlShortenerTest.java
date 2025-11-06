import core.Config;
import core.LinkModel;
import core.UrlShortener;
import core.ValidationException;
import storage.InMemoryStore;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UrlShortenerTest {
    @Test
    void createGeneratesCode() {
        InMemoryStore st = new InMemoryStore();
        Config cfg = new Config();
        UrlShortener s = new UrlShortener(st, cfg);
        UUID u = UUID.randomUUID();
        String code = s.create(u, "https://example.test", 0, cfg.getDefaultTTLMinutes());
        assertNotNull(code);
        assertNotNull(s.get(code));
    }

    @Test
    void uniqueCodesNoCollision() {
        InMemoryStore st = new InMemoryStore();
        Config cfg = new Config();
        UrlShortener s = new UrlShortener(st, cfg);
        UUID u = UUID.randomUUID();
        String c1 = s.create(u, "https://a.test/1", 0, cfg.getDefaultTTLMinutes());
        String c2 = s.create(u, "https://a.test/2", 0, cfg.getDefaultTTLMinutes());
        assertNotEquals(c1, c2);
    }

    @Test
    void validateUrlRejectsBad() {
        InMemoryStore st = new InMemoryStore();
        Config cfg = new Config();
        UrlShortener s = new UrlShortener(st, cfg);
        UUID u = UUID.randomUUID();
        assertThrows(ValidationException.class, () -> s.create(u, "invalid", 0, cfg.getDefaultTTLMinutes()));
    }

    @Test
    void maxClicksApplied() {
        InMemoryStore st = new InMemoryStore();
        Config cfg = new Config();
        UrlShortener s = new UrlShortener(st, cfg);
        UUID u = UUID.randomUUID();
        String code = s.create(u, "https://limit.test", 2, cfg.getDefaultTTLMinutes());
        LinkModel l = s.get(code);
        assertEquals(2, l.maxClicks);
    }
}
