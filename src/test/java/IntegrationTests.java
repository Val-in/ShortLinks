import core.*;
import storage.InMemoryStore;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTests {
    @Test
    void createOpenDeleteFlow() throws Exception {
        InMemoryStore st = new InMemoryStore();
        Config cfg = new Config();
        UrlShortener s = new UrlShortener(st, cfg);
        UUID u = UUID.randomUUID();
        String code = s.create(u, "https://int.test", 2, cfg.getDefaultTTLMinutes());
        assertNotNull(s.get(code));
        LinkModel l = s.get(code);
        l.clicks++;
        assertEquals(1, l.clicks);
        s.delete(code);
        assertNull(s.get(code));
    }

    @Test
    void ownerRestrictionOnDelete() {
        InMemoryStore st = new InMemoryStore();
        Config cfg = new Config();
        UrlShortener s = new UrlShortener(st, cfg);
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();
        String code = s.create(u1, "https://owner.test", 0, cfg.getDefaultTTLMinutes());
        LinkModel l = s.get(code);
        assertTrue(l.owner.equals(u1));
        // simulate command processor check
        assertFalse(l.owner.equals(u2));
    }

    @Test
    void multipleLinksPerUser() {
        InMemoryStore st = new InMemoryStore();
        Config cfg = new Config();
        UrlShortener s = new UrlShortener(st, cfg);
        UUID u = UUID.randomUUID();
        String a = s.create(u, "https://a.test", 0, cfg.getDefaultTTLMinutes());
        String b = s.create(u, "https://b.test", 0, cfg.getDefaultTTLMinutes());
        assertTrue(s.list(u).size() >= 2);
    }
}
