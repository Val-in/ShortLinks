package util;

import java.io.*;
import java.util.UUID;

public class FileUtils {
    public static UUID loadOrCreateUUID(String path) throws IOException {
        File f = new File(path);
        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                return UUID.fromString(br.readLine().trim());
            }
        }
        UUID u = UUID.randomUUID();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            bw.write(u.toString());
        }
        return u;
    }
}
