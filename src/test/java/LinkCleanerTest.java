import core.Config;
import core.LinkCleaner;
import core.LinkModel;
import core.UrlShortener;
import storage.InMemoryStore;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LinkCleanerTest {
    @Test
    void removesLinksOverMaxClicks() throws Exception {
        InMemoryStore st = new InMemoryStore();
        Config cfg = new Config();
        LinkCleaner cleaner = new LinkCleaner();
        UrlShortener s = new UrlShortener(st, cfg);
        UUID u = UUID.randomUUID();
        String code = s.create(u, "https://limit.test", 1, cfg.getDefaultTTLMinutes());
        LinkModel l = s.get(code);
        l.clicks = 1;
        cleaner.start(st, cfg);
        Thread.sleep(1500);
        assertNull(s.get(code));
        cleaner.stop();
    }
}
