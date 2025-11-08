package cli;

import core.*;
import java.awt.Desktop;
import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import storage.InMemoryStore;
import util.FileUtils;

public class CommandProcessor {
  private final Config config = new Config();
  private InMemoryStore store;
  private UUID currentUser;

  public void start() throws Exception {

    String uuidPath = config.getUuidStorePath();
    currentUser = FileUtils.loadOrCreateUUID(uuidPath);

    store = new InMemoryStore();
    UrlShortener shortener = new UrlShortener(store, config);
    LinkCleaner cleaner = new LinkCleaner();
    cleaner.start(store, config);

    System.out.println("====================================");
    System.out.println("   URL Shortener CLI");
    System.out.println("====================================");
    System.out.println("Ваш UUID: " + currentUser);
    System.out.println("Введите команду (help - список команд)");
    System.out.println("------------------------------------");

    Scanner sc = new Scanner(System.in);
    while (true) {
      System.out.print("cmd> ");
      String line = sc.nextLine().trim();
      if (line.isEmpty()) continue;

      String[] parts = line.split("\\s+");
      String cmd = parts[0].toLowerCase(Locale.ROOT);
      String arg = parts.length > 1 ? parts[1].trim() : "";

      try {
        switch (cmd) {
          case "create" -> handleCreate(shortener, currentUser, arg);
          case "list" -> handleList(shortener, currentUser);
          case "edit" -> handleEdit(parts);
          case "open" -> handleOpen(shortener, arg);
          case "delete" -> handleDelete(shortener, currentUser, arg);
          case "help" -> printHelp();
          case "exit" -> {
            cleaner.stop();
            System.out.println("До свидания!");
            return;
          }
          default -> System.out.println("Неизвестная команда. Введите 'help' для справки.");
        }
      } catch (ValidationException e) {
        System.out.println("Ошибка: " + e.getMessage());
      } catch (Exception e) {
        System.out.println("Ошибка выполнения: " + e.getMessage());
      }
    }
  }

  private void printHelp() {
    System.out.println(
        """
            Доступные команды:
              create <url> [maxClicks] [ttlMinutes] - создать короткую ссылку
              list                                 - показать все ваши ссылки
              edit <code> [maxClicks] [ttlMinutes] - редактировать короткую ссылку
              open <shortCode>                     - открыть оригинальный URL
              delete <shortCode>                   - удалить ссылку
              exit                                 - выйти из программы
            """);
  }

  private void handleCreate(UrlShortener s, UUID u, String arg) {
    String[] p = arg.split("\\s+");
    if (p.length == 0) {
      System.out.println("Использование: create <url> [maxClicks] [ttlMinutes]");
      return;
    }

    String url = p[0];
    int max = p.length > 1 ? Integer.parseInt(p[1]) : config.getMaxClicksDefault();
    long ttl = p.length > 2 ? Long.parseLong(p[2]) : config.getDefaultTTLMinutes();

    String code = s.create(u, url, max, ttl);
    System.out.println("Создано: " + code + " -> " + url);
  }

  private void handleList(UrlShortener s, UUID u) {
    for (String c : s.list(u)) {
      LinkModel l = s.get(c);
      System.out.printf(
          "%-10s %-50s %-8d %-10s %-20s\n",
          c,
          l.original,
          l.clicks,
          l.maxClicks == 0 ? "unlimited" : l.maxClicks,
          Instant.ofEpochMilli(l.expiresAt));
    }
  }

  private void handleEdit(String[] parts) {
    if (parts.length < 4) {
      System.out.println("Использование: edit <code> [maxClicks] [ttlMinutes]");
      return;
    }

    String code = parts[1];
    int newMaxClicks = Integer.parseInt(parts[2]);
    int newTtlMinutes = Integer.parseInt(parts[3]);

    LinkModel link = store.get(code);
    if (link == null) {
      System.out.println("Ссылка не найдена");
      return;
    }

    if (!link.owner.equals(currentUser)) {
      System.out.println("⛔ Только владелец может изменять параметры ссылки!");
      return;
    }

    long newExpiresAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(newTtlMinutes);
    link.maxClicks = newMaxClicks;
    link.expiresAt = newExpiresAt;

    store.put(code, link);
    System.out.println(
        "✅ Параметры обновлены: лимит = " + newMaxClicks + ", TTL = " + newTtlMinutes + " мин.");
  }

  private void handleOpen(UrlShortener s, String code) throws Exception {
    LinkModel l = s.get(code);
    if (l == null) {
      System.out.println("Ссылка не найдена");
      return;
    }

    long now = System.currentTimeMillis();

    if (l.expiresAt > 0 && now > l.expiresAt) {
      System.out.println("⛔ Срок действия ссылки истёк!");
      s.delete(code);
      return;
    }

    l.clicks++;
    s.update(l);
    System.out.println("Переход #" + l.clicks);

    if (l.maxClicks > 0 && l.clicks == l.maxClicks) {
      System.out.println("⛔ Достигнут лимит переходов, ссылка больше не активна!");
    }

    if (Desktop.isDesktopSupported()) {
      Desktop.getDesktop().browse(new URI(l.original));
    } else {
      System.out.println("URL: " + l.original);
    }
  }

  private void handleDelete(UrlShortener s, UUID currentUser, String code) {
    LinkModel l = s.get(code);
    if (l == null) {
      System.out.println("Ссылка не найдена");
      return;
    }

    if (!l.owner.equals(currentUser)) {
      System.out.println("Ошибка: вы не являетесь владельцем этой ссылки");
      return;
    }

    s.delete(code);
    System.out.println("Удалено: " + code);
  }
}
