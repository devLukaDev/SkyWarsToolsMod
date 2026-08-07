package org.devlukadev.skywarstoolsmod.utils.fetchutils;
// Original from Alexdoru MWE
// https://github.com/Alexdoru/MWE

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClient {


    public static JsonObject getAsJsonObject(String url) throws Error {
        final JsonElement jsonElement = new JsonParser().parse(HttpClient.get(url));
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            throw new Error("Cannot parse Api response to Json Object");
        }
        return jsonElement.getAsJsonObject();
    }

    public static String get(String url) throws Error {

        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.addRequestProperty("User-Agent", "Alexdoru's Mega Walls Enhancements Mod");
            final int status = connection.getResponseCode();

            if (status != 200) {
                throw new Error("Http error code : " + status);
            }

            final StringBuilder sb = new StringBuilder();
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            final String s = sb.toString();
            if (s.isEmpty()) {
                throw new Error("Response is Empty!");
            }
            return s;

        } catch (IOException e) {
            e.printStackTrace();
            throw new Error("An error occured while contacting the Api");
        }

    }

}