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
        private static final long TTL_MS = 60_000; // re-fetch after 1 min, tune as needed

        public static String getSuffix(UUID uuid, String playerName) {
            SkyWarsResponse cached = cache.get(uuid);

            boolean stale = cached == null ||
                    System.currentTimeMillis() - lastFetch.getOrDefault(uuid, 0L) > TTL_MS;

            if (stale && pending.add(uuid)) { // add() returns false if already present
                Fetch.getJsonAsync("https://api.skywarstools.com/api/skywars?player=" + playerName, SkyWarsResponse.class)
                        .thenAccept(response -> {
                            cache.put(uuid, response);
                            lastFetch.put(uuid, System.currentTimeMillis());
                            pending.remove(uuid);
                        })
                        .exceptionally(ex -> {
                            System.err.println("Fetch failed: " + ex.getMessage());
                            pending.remove(uuid); // allow retry later
                            return null;
                        });
            }

            return cached != null ? " " + cached.display.levelFormattedWithBrackets : "";
        }

}
