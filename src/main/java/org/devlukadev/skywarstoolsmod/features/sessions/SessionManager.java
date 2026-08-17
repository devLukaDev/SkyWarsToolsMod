package org.devlukadev.skywarstoolsmod.features.sessions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.minecraftforge.fml.common.Loader;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SessionManager {



    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path SAVE_DIR = Loader.instance().getConfigDir().toPath().resolve("swt");
    private static final Path SAVE_FILE = SAVE_DIR.resolve("session.json");
    private static final Path TMP_FILE = SAVE_DIR.resolve("session.json.tmp");

    private static SessionManager instance;

    private SessionData data;

    private SessionManager() {
        this.data = load();
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public SessionData getData() {
        return data;
    }

    public synchronized void addKill() {
        data.kills++;
        save();
    }

    public synchronized void addDeath() {
        data.deaths++;
        save();
    }

    public synchronized void addWin() {
        data.wins++;
        save();
    }

    public synchronized void addXp(double amount) {
        data.xpGained += amount;
        save();
    }

    public synchronized void resetSession() {
        data = new SessionData();
        save();
    }

    private SessionData load() {
        if (!Files.exists(SAVE_FILE)) {
            return new SessionData();
        }
        try (Reader reader = Files.newBufferedReader(SAVE_FILE, StandardCharsets.UTF_8)) {
            SessionData loaded = GSON.fromJson(reader, SessionData.class);
            return loaded != null ? loaded : new SessionData();
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
            quarantineCorruptedFile();
            return new SessionData();
        }
    }

    private void quarantineCorruptedFile() {
        try {
            Path quarantined = SAVE_DIR.resolve("session.corrupted-" + System.currentTimeMillis() + ".json");
            Files.move(SAVE_FILE, quarantined, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
        }
    }

    private void save() {
        try {
            Files.createDirectories(SAVE_DIR);
            try (Writer writer = Files.newBufferedWriter(TMP_FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(data, writer);
            }
            try {
                Files.move(TMP_FILE, SAVE_FILE, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(TMP_FILE, SAVE_FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}