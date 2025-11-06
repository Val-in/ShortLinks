import core.LinkModel;
import org.junit.jupiter.api.Test;
import storage.InMemoryStore;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryStoreTest {
    @Test
    void putAndGetAndRemove() {
        InMemoryStore store = new InMemoryStore();
        UUID u = UUID.randomUUID();
        LinkModel l = new LinkModel(u, "https://a", "code1", 0, System.currentTimeMillis(), System.currentTimeMillis() + 10000);
        store.put("code1", l);
        assertEquals(l, store.get("code1"));
        store.remove("code1");
        assertNull(store.get("code1"));
    }

    @Test
    void userIndexTracksCodes() {
        InMemoryStore store = new InMemoryStore();
        UUID u = UUID.randomUUID();
        LinkModel l1 = new LinkModel(u, "https://a", "c1", 0, System.currentTimeMillis(), System.currentTimeMillis()+10000);
        LinkModel l2 = new LinkModel(u, "https://b", "c2", 0, System.currentTimeMillis(), System.currentTimeMillis()+10000);
        store.put("c1", l1);
        store.put("c2", l2);
        Set<String> s = store.getUserLinks(u);
        assertTrue(s.contains("c1"));
        assertTrue(s.contains("c2"));
    }

    @Test
    void removeCleansUserIndex() {
        InMemoryStore store = new InMemoryStore();
        UUID u = UUID.randomUUID();
        LinkModel l = new LinkModel(u, "https://a", "c1", 0, System.currentTimeMillis(), System.currentTimeMillis()+10000);
        store.put("c1", l);
        store.remove("c1");
        assertFalse(store.getUserLinks(u).contains("c1"));
    }
}
