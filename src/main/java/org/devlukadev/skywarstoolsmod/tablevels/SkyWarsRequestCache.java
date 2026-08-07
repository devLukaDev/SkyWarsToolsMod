package org.devlukadev.skywarstoolsmod.tablevels;

import org.devlukadev.skywarstoolsmod.utils.fetchutils.Fetch;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.SkyWarsResponse;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SkyWarsRequestCache {
    private static final Map<UUID, SkyWarsResponse> cache = new ConcurrentHashMap<>();
    private static final Set<UUID> pending = ConcurrentHashMap.newKeySet();
    private static final Map<UUID, Long> lastFetch = new ConcurrentHashMap<>();

    public static int getCacheSize() {
        return cache.size();
    }

    public static String getPrefix(UUID uuid) {
        if (uuid == null) {
            return "";
        }

        SkyWarsResponse cached = cache.get(uuid);
        boolean stale = (cached == null);

        if (stale && pending.add(uuid)) { // add() returns false if already present
            Fetch.getJsonAsync("https://api.skywarstools.com/api/skywars?player=" + uuid, SkyWarsResponse.class)
                    .thenAccept(response -> {
                        if (response != null) {
                            cache.put(uuid, response);
                            lastFetch.put(uuid, System.currentTimeMillis());
                        }
                        pending.remove(uuid);
                    })
                    .exceptionally(ex -> {
                        System.err.println("Fetch failed: " + ex.getMessage());
                        pending.remove(uuid); // don't get stuck forever
                        return null;
                    });
        }

        if (cached == null || cached.display == null || cached.display.levelFormattedWithBrackets == null) {
            return "§c[?] ";
        }

        return cached.display.levelFormattedWithBrackets;
    }
}
