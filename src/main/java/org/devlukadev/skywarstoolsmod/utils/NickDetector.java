package org.devlukadev.skywarstoolsmod.utils;

import java.util.UUID;

public class NickDetector {

    /**
     * Checks whether a UUID looks like a nicked/offline-mode player rather
     * than a real Mojang account.
     *
     * Real Mojang UUIDs are version 4 (random).
     * Nicked/offline UUIDs are version 3 (name-based, MD5 of "OfflinePlayer:<name>"),
     * since that's what vanilla/Bukkit-style offline-mode UUID generation produces.
     *
     * @param uuid the player's UUID from GameProfile
     * @return true if likely nicked/offline, false if a real Mojang (v4) UUID
     */
    public static boolean isLikelyNicked(UUID uuid) {
        if (uuid == null) return false;
        return uuid.version() != 4;
    }
}