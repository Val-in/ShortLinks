import org.junit.jupiter.api.*;
import util.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {
    private static File tmp;

    @BeforeAll
    static void setup() throws Exception {
        tmp = Files.createTempFile("uuid_test", ".txt").toFile();
        if (tmp.exists()) tmp.delete();
    }

    @Test
    void createsAndLoadsUuid() throws Exception {
        UUID u1 = FileUtils.loadOrCreateUUID(tmp.getAbsolutePath());
        assertNotNull(u1);
        UUID u2 = FileUtils.loadOrCreateUUID(tmp.getAbsolutePath());
        assertEquals(u1, u2);
        tmp.delete();
    }
}
