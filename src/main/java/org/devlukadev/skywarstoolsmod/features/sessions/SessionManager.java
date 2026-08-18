package org.devlukadev.skywarstoolsmod.features.sessions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Loader;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.Fetch;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.OverallResponse;

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

    public synchronized void addLoss() {
        data.losses++;
        save();
    }

    public synchronized void addXp(double amount) {
        data.xpGained += amount;
        save();
    }

    public synchronized void addPlaytime(long amount) {
        data.time_played += amount;
        ChatLib.chat("added " + amount + " to get total of " + data.time_played);
        save();
    }

    public synchronized void resetSession() {
        data = new SessionData();
        save();
        Minecraft.getMinecraft().addScheduledTask(() -> ChatLib.chat("Your session was reset."));

    }

    private volatile boolean syncInProgress = false;

    public void startSession(String playerIdentifier) {
        resetSession();
        sync(playerIdentifier);
    }

    public void sync(String playerIdentifier) {
        if (syncInProgress) return;
        syncInProgress = true;

        String url = SkyWarsToolsMod.SWT_API + "/overall?player=" + playerIdentifier;

        Fetch.getJsonAsync(url, OverallResponse.class)
                .thenAccept(response -> {
                    if (response == null || response.stats == null) {
                        syncInProgress = false;
                        return;
                    }
                    if (response.source != null && response.source.equals("cache")) {
                        Minecraft.getMinecraft().addScheduledTask(() ->
                                ChatLib.chat("Detected cache response; retrieved stats might be slightly outdated."));
                    }
                    reconcile(response.stats);
                    syncInProgress = false;

                    Minecraft.getMinecraft().addScheduledTask(() ->
                            ChatLib.chat("Your stats were synced with the Hypixel API!"));
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    syncInProgress = false;
                    return null;
                });


    }

    private synchronized void reconcile(OverallResponse.Stats fresh) {
        if (data.baseline == null) {
            // First sync of the session - freeze this as the starting point.
            // Session deltas are whatever's already been tracked locally since session start.
            data.baseline = snapshotOf(fresh);
        } else {
            // Re-derive session deltas from the source of truth so drift from
            // any missed/misparsed chat line gets corrected. Baseline itself never moves.
            data.kills = fresh.kills - data.baseline.kills;
            data.deaths = fresh.deaths - data.baseline.deaths;
            data.wins = fresh.wins - data.baseline.wins;
            data.losses = fresh.losses - data.baseline.losses;
            data.time_played = fresh.time_played - data.baseline.time_played;
            data.xpGained = fresh.skywars_experience - data.baseline.xp;
        }
        data.lastSync = System.currentTimeMillis();
        save();
    }

    private SessionData.BaselineSnapshot snapshotOf(OverallResponse.Stats stats) {
        SessionData.BaselineSnapshot snap = new SessionData.BaselineSnapshot();
        snap.kills = stats.kills;
        snap.deaths = stats.deaths;
        snap.wins = stats.wins;
        snap.losses = stats.losses;
        snap.time_played = stats.time_played;
        snap.xp = stats.skywars_experience;
        snap.fetchedAt = System.currentTimeMillis();
        return snap;
    }

    public SessionData.BaselineSnapshot getCurrentStats() {
        if (data.baseline == null) return null; // no baseline yet, all zero
        SessionData.BaselineSnapshot current = new SessionData.BaselineSnapshot();
        current.kills = data.baseline.kills + data.kills;
        current.deaths = data.baseline.deaths + data.deaths;
        current.wins = data.baseline.wins + data.wins;
        current.losses = data.baseline.losses + data.losses;
        current.time_played = data.baseline.losses + data.time_played;
        current.xp = data.baseline.xp + data.xpGained;
        return current;
    }

    private SessionData load() {
        System.out.println("Loading session data file...");
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
        System.out.println("Writing session data...");
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