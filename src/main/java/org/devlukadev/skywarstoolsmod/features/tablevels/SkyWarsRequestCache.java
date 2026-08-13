package org.devlukadev.skywarstoolsmod.features.tablevels;

import org.devlukadev.skywarstoolsmod.utils.fetchutils.Fetch;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.SkyWarsResponse;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SkyWarsRequestCache {

    private static final Map<UUID, SkyWarsResponse> uuidCache = new ConcurrentHashMap<>();
    private static final Set<UUID> uuidPending = ConcurrentHashMap.newKeySet();
    private static final Map<UUID, Long> uuidLastFetch = new ConcurrentHashMap<>();

    private static final Map<String, SkyWarsResponse> nameCache = new ConcurrentHashMap<>();
    private static final Set<String> namePending = ConcurrentHashMap.newKeySet();
    private static final Map<String, Long> nameLastFetch = new ConcurrentHashMap<>();

    public static int getCacheSize() {
        return uuidCache.size() + nameCache.size();
    }

    public static SkyWarsResponse getStats(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return getStats(
                uuid,
                uuidCache,
                uuidPending,
                uuidLastFetch,
                "https://api.skywarstools.com/api/skywars?player=" + uuid
        );
    }

    public static SkyWarsResponse getStats(String playerName) {
        if (playerName == null || playerName.isEmpty()) {
            return null;
        }
        return getStats(
                playerName,
                nameCache,
                namePending,
                nameLastFetch,
                "https://api.skywarstools.com/api/skywars?player=" + playerName
        );
    }

    private static <K> SkyWarsResponse getStats(K key,
                                       Map<K, SkyWarsResponse> cache,
                                       Set<K> pending,
                                       Map<K, Long> lastFetch,
                                       String url) {
        SkyWarsResponse cached = cache.get(key);
        boolean stale = (cached == null);

        if (stale && pending.add(key)) { // add() returns false if already present
            Fetch.getJsonAsync(url, SkyWarsResponse.class)
                    .thenAccept(response -> {
                        if (response != null) {
                            cache.put(key, response);
                            lastFetch.put(key, System.currentTimeMillis());
                        }
                        pending.remove(key);
                    })
                    .exceptionally(ex -> {
                        System.err.println("Fetch failed: " + ex.getMessage());
                        // We keep it in pending such that it doesn't keep trying
                        return null;
                    });
        }

        return cached;
    }
}