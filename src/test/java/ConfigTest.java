import core.Config;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {
    @Test
    void loadsDefaultsWhenMissing() {
        Config c = new Config(); // использует resources/config.properties
        assertTrue(c.getMaxUrlLength() > 0);
        assertTrue(c.getShortCodeLength() > 0);
    }

    @Test
    void uuidPathContainsUserHome() {
        Config c = new Config();
        String path = c.getUuidStorePath();
        assertTrue(path.contains(System.getProperty("user.home")));
    }
}
