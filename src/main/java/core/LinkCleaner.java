package core;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import storage.InMemoryStore;

public class LinkCleaner {
  private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

  public void start(InMemoryStore store, Config config) {
    long interval = config.getCleanerIntervalSeconds();

    cleaner.scheduleAtFixedRate(
        () -> {
          long now = Instant.now().toEpochMilli();
          List<String> toRemove = new ArrayList<>();
          for (var e : store.getAll().entrySet()) {
            if (e.getValue().isExpired(now)) toRemove.add(e.getKey());
          }
          toRemove.forEach(
              code -> {
                store.remove(code);
                if (config.isNotifyEnabled() && "console".equals(config.getNotifyType())) {
                  System.out.println("Удалена просроченная ссылка: " + code);
                }
              });
        },
        1,
        interval,
        TimeUnit.SECONDS);
  }

  public void stop() {
    cleaner.shutdownNow();
  }
}
