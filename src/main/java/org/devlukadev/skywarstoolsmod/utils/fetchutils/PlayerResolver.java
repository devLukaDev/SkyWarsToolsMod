package org.devlukadev.skywarstoolsmod.utils.fetchutils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerResolver {

    // onFound always runs on the main thread
    public static void resolve(String name, Consumer<GameProfile> onFound) {
        if (!name.matches("^\\w{1,16}$")) {
            ChatLib.chat("&cInvalid username.", true);
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getNetHandler() != null) {
            for (NetworkPlayerInfo info : mc.getNetHandler().getPlayerInfoMap()) {
                GameProfile gp = info.getGameProfile();
                if (gp.getName().equalsIgnoreCase(name)) {
                    onFound.accept(gp);
                    return;
                }
            }
        }

        new Thread(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(
                        "https://api.minecraftservices.com/minecraft/profile/lookup/name/" + name).openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                int status = conn.getResponseCode();

                if (status == 200) {
                    JsonObject json = new JsonParser().parse(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
                    UUID uuid = UUID.fromString(json.get("id").getAsString().replaceFirst(
                            "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                            "$1-$2-$3-$4-$5"));
                    GameProfile gp = new GameProfile(uuid, json.get("name").getAsString());
                    mc.addScheduledTask(() -> onFound.accept(gp));
                } else if (status == 404 || status == 204) {
                    mc.addScheduledTask(() ->
                            ChatLib.chat("&cThe player &e" + name + "&c does not exist.", true));
                } else {
                    mc.addScheduledTask(() ->
                            ChatLib.chat("&cLookup failed (HTTP " + status + "). Try again later.", true));
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                mc.addScheduledTask(() ->
                        ChatLib.chat("&cCould not reach Mojang to look up the player.", true));
            }
        }, "SWT-PlayerResolver").start();
    }
}
