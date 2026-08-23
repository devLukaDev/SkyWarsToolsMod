package org.devlukadev.skywarstoolsmod.features.tablevels;

import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.Fetch;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.SkyWarsResponse;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
                SkyWarsToolsMod.SWT_API + "/skywars?player=" + uuid
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
                SkyWarsToolsMod.SWT_API + "/skywars?player=" + playerName
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

    private static final Map<UUID, CompletableFuture<SkyWarsResponse>> uuidFutures = new ConcurrentHashMap<>();
    private static final Map<String, CompletableFuture<SkyWarsResponse>> nameFutures = new ConcurrentHashMap<>();

    public static CompletableFuture<SkyWarsResponse> getStatsAsync(UUID uuid) {
        if (uuid == null) return CompletableFuture.completedFuture(null);
        return getStatsAsync(
                uuid,
                uuidCache,
                uuidPending,
                uuidLastFetch,
                uuidFutures,
                SkyWarsToolsMod.SWT_API + "/skywars?player=" + uuid
        );
    }

    public static CompletableFuture<SkyWarsResponse> getStatsAsync(String playerName) {
        if (playerName == null || playerName.isEmpty()) return CompletableFuture.completedFuture(null);
        return getStatsAsync(
                playerName,
                nameCache,
                namePending,
                nameLastFetch,
                nameFutures,
                SkyWarsToolsMod.SWT_API + "/skywars?player=" + playerName
        );
    }

    private static <K> CompletableFuture<SkyWarsResponse> getStatsAsync(K key,
                                                                        Map<K, SkyWarsResponse> cache,
                                                                        Set<K> pending,
                                                                        Map<K, Long> lastFetch,
                                                                        Map<K, CompletableFuture<SkyWarsResponse>> futures,
                                                                        String url) {
        SkyWarsResponse cached = cache.get(key);
        if (cached != null) return CompletableFuture.completedFuture(cached);

        if (pending.add(key)) { // we're the one kicking off the fetch
            CompletableFuture<SkyWarsResponse> future = Fetch.getJsonAsync(url, SkyWarsResponse.class)
                    .thenApply(response -> {
                        if (response != null) {
                            cache.put(key, response);
                            lastFetch.put(key, System.currentTimeMillis());
                        }
                        pending.remove(key);
                        futures.remove(key);
                        return response;
                    })
                    .exceptionally(ex -> {
                        System.err.println("Fetch failed: " + ex.getMessage());
                        futures.remove(key);
                        return null;
                    });
            futures.put(key, future);
            return future;
        }

        // Already pending elsewhere - return that in-flight future, or empty if it just finished
        CompletableFuture<SkyWarsResponse> inFlight = futures.get(key);
        return inFlight != null ? inFlight : CompletableFuture.completedFuture(cache.get(key));
    }
}