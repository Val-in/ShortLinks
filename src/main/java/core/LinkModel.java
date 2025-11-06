package core;

import java.util.UUID;

public class LinkModel {
    public final UUID owner;
    public final String original;
    public final String shortCode;
    public volatile int clicks;
    public volatile int maxClicks;
    public final long createdAt;
    public long expiresAt;

    public LinkModel(UUID owner, String original, String shortCode, int maxClicks, long createdAt, long expiresAt) {
        this.owner = owner;
        this.original = original;
        this.shortCode = shortCode;
        this.clicks = 0;
        this.maxClicks = maxClicks;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired(long now) {
        return now >= expiresAt || (maxClicks > 0 && clicks >= maxClicks);
    }
}
