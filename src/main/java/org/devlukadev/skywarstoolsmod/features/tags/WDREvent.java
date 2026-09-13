package org.devlukadev.skywarstoolsmod.features.tags;

import cc.polyfrost.oneconfig.events.event.ChatSendEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WDREvent {

    @Subscribe
    public void onChat(ChatSendEvent event) {
        String msg = event.message;
        Matcher m = Pattern.compile("^/wdr\\s+(\\S+)(?:\\s+(.+))?$").matcher(msg);
        if (m.matches()) {
            String playerName = m.group(1);
            String reasons = m.group(2);
            if (reasons == null || reasons.equalsIgnoreCase("")) {
                reasons = "cheating";
            }

            String finalReasons = reasons;
            new Thread(() -> {
                UUID uuid = getProfileFromMojang(playerName);

                if (uuid == null) {
                    ChatLib.chat("&cCould not find player", true);
                    return;
                }

                boolean created = TagManager.addTag(uuid, finalReasons);
                if (created) {
                    ChatLib.chat("&aTagged &e" + playerName + "&a with reasons: &e" + finalReasons, true);
                } else {
                    ChatLib.chat("&aAdded reason to existing tag for &e" + playerName + "&a: &e" + finalReasons, true);
                }
            }).start();


        }
    }

    private static UUID getProfileFromMojang(String username) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() != 200) return null;

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
            reader.close();

            String idString = json.get("id").getAsString(); // no dashes
            return UUID.fromString(idString.replaceFirst(
                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                    "$1-$2-$3-$4-$5"
            ));
        } catch (Exception e) {
            return null;
        }
    }
}
