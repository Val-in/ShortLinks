package storage;

import core.LinkModel;
import java.util.*;
import java.util.concurrent.*;

public class InMemoryStore {
  private final ConcurrentMap<String, LinkModel> store = new ConcurrentHashMap<>();
  private final ConcurrentMap<UUID, Set<String>> userIndex = new ConcurrentHashMap<>();

  public void put(String code, LinkModel link) {
    store.put(code, link);
    userIndex.computeIfAbsent(link.owner, k -> ConcurrentHashMap.newKeySet()).add(code);
  }

  public LinkModel get(String code) {
    return store.get(code);
  }

  public void remove(String code) {
    LinkModel l = store.remove(code);
    if (l != null) userIndex.getOrDefault(l.owner, Set.of()).remove(code);
  }

  public Set<String> getUserLinks(UUID owner) {
    return userIndex.getOrDefault(owner, Collections.emptySet());
  }

  public Map<String, LinkModel> getAll() {
    return store;
  }
}
