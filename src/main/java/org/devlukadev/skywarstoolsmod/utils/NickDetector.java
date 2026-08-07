package org.devlukadev.skywarstoolsmod.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NickDetector {

    /**
     * Checks whether a UUID looks like a nicked/offline-mode player rather
     * than a real Mojang account.
     * <p>
     * Real Mojang UUIDs are version 4 (random).
     * Nicked/offline UUIDs are version 3 (name-based, MD5 of "OfflinePlayer:<name>"),
     * since that's what vanilla/Bukkit-style offline-mode UUID generation produces.
     *
     * @param uuid the player's UUID from GameProfile
     * @return true if likely nicked/offline, false if a real Mojang (v4) UUID
     */

    public static Set<String> mythicalKits;

    static {
        mythicalKits = new HashSet<>();
        mythicalKits.add("minecraft:skins/f868a0df866e7a9dab139b9b67fbd776f266970e541118678dccbdc8719fcc");
        mythicalKits.add("minecraft:skins/e0bbde9dbcf2970faad195d586155fec61c236c2a7d89ab3b1389646e125b520");
        mythicalKits.add("minecraft:skins/c1b2dfe8ed5dffc5b1687bc1c249c39de2d8a6c3d90305c95f6d1a1a330a0b1");
        mythicalKits.add("minecraft:skins/1ff081df8e0de2dc64def9ab946ff0feaebe532884dbab8c5bbba9fe9fb17684");
        mythicalKits.add("minecraft:skins/168af4244eb46b7ce51f53369d6fec8c3b4e2fe57aca852475716152fc9f1c");
        mythicalKits.add("minecraft:skins/984ff79d490f463ff72ab98d325b2640597c16d5bc82d242ad0769b5705279bf");
    }

    public static boolean isLikelyNicked(NetworkPlayerInfo networkPlayerInfo) {
        UUID uuid = networkPlayerInfo.getGameProfile().getId();
        if (uuid == null) return true;
        net.minecraft.util.ResourceLocation skin = networkPlayerInfo.getLocationSkin();
        System.out.println(skin.toString());
        if (mythicalKits.contains(skin.toString())) {
            return false;
        }
        return uuid.version() != 4;
    }
}