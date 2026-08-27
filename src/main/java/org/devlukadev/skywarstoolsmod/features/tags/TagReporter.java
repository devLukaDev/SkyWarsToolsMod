package org.devlukadev.skywarstoolsmod.features.tags;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.UUID;

public class TagReporter {
    private static final String API_URL = "http://localhost:3001/api/tags/";

    public static void reportTag(UUID taggedPlayerUUID, String tagText) {
        System.out.println("Adding report to global DB");
        new Thread(() -> {
            try {
                Minecraft mc = Minecraft.getMinecraft();
                Session session = mc.getSession();
                String accessToken = session.getToken();
                String reporterUUID = session.getPlayerID();
                String reporterUsername = session.getUsername();

                String serverId = generateServerId();
                joinMojangSession(accessToken, reporterUUID, serverId);

                JsonObject payload = new JsonObject();
                payload.addProperty("reporterUsername", reporterUsername);
                payload.addProperty("reporterUUID", reporterUUID);
                payload.addProperty("serverId", serverId);
                payload.addProperty("taggedPlayerUUID", taggedPlayerUUID.toString());
                payload.addProperty("tagText", tagText);
                payload.addProperty("timestamp", System.currentTimeMillis());

                postJson(API_URL + taggedPlayerUUID.toString(), payload.toString());
                System.out.println("Sent!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static String generateServerId() {
        return Long.toHexString(new SecureRandom().nextLong());
    }

    private static void joinMojangSession(String accessToken, String uuid, String serverId) throws IOException {
        JsonObject joinPayload = new JsonObject();
        joinPayload.addProperty("accessToken", accessToken);
        joinPayload.addProperty("selectedProfile", uuid.replace("-", ""));
        joinPayload.addProperty("serverId", serverId);

        int status = postJson("https://sessionserver.mojang.com/session/minecraft/join", joinPayload.toString());
        if (status != 204) {
            throw new IOException("Mojang join failed: " + status);
        }
    }

    private static int postJson(String urlStr, String body) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        conn.disconnect();
        return status;
    }
}