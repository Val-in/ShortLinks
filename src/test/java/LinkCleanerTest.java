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
    void removesExpiredLinks() throws Exception {
        InMemoryStore st = new InMemoryStore();
        Config cfg = new Config();
        // краткий интервал для теста
        cfg = new Config(); // используемый config.properties должен иметь small cleaner.interval.seconds для теста либо оставим sleep
        LinkCleaner cleaner = new LinkCleaner();
        UrlShortener s = new UrlShortener(st, cfg);
        UUID u = UUID.randomUUID();
        String code = s.create(u, "https://ttl.test", 0, 0L); // ttl 0 -> immediate expire if implemented
        cleaner.start(st, cfg);
        // подождём немного, чтобы cleaner сработал
        Thread.sleep(1500);
        assertNull(s.get(code));
        cleaner.stop();
    }

    @Test
    void removesLinksOverMaxClicks() throws Exception {
        InMemoryStore st = new InMemoryStore();
        Config cfg = new Config();
        LinkCleaner cleaner = new LinkCleaner();
        UrlShortener s = new UrlShortener(st, cfg);
        UUID u = UUID.randomUUID();
        String code = s.create(u, "https://limit.test", 1, cfg.getDefaultTTLMinutes());
        LinkModel l = s.get(code);
        l.clicks = 1; // simulate reached
        cleaner.start(st, cfg);
        Thread.sleep(1500);
        assertNull(s.get(code));
        cleaner.stop();
    }
}
