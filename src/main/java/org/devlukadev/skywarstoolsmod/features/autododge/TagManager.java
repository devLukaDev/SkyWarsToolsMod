package org.devlukadev.skywarstoolsmod.features.autododge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Stores per-UUID player tags (playername -> reason list), persisted as
 * a line-based JSON file, following the same pattern as WdrDataManager - from Alexdoru/MWE.
 */
public final class TagManager {

    private TagManager() {}

    private static final Map<UUID, Tag> tagMap = new ConcurrentHashMap<>();
    private static final AtomicBoolean dirty = new AtomicBoolean(false);
    private static final ScheduledExecutorService ioExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "SWT-TagManager-IO");
        t.setDaemon(true);
        return t;
    });

    private static File tagDataFile;
    private static boolean initialized;

    public static void loadData(File configFolder) {
        if (initialized) {
            throw new IllegalStateException("TagManager already initialized");
        }
        initialized = true;
        tagDataFile = new File(configFolder, "playertags.json");

        ioExecutor.execute(() -> tagMap.putAll(loadDataFromFile()));
        ioExecutor.scheduleAtFixedRate(TagManager::saveIfDirty, 30, 30, TimeUnit.SECONDS);
    }

    public static void onShutdown() {
        saveIfDirty();
    }

    private static void saveIfDirty() {
        if (dirty.compareAndSet(true, false)) {
            Map<UUID, Tag> snapshot = new HashMap<>(tagMap);
            if (!writeDataToFile(snapshot)) {
                dirty.set(true);
            }
        }
    }

    /**
     * Adds a reason to a player's tag, creating the tag if needed.
     * @return true if this was a brand new tag entry (not just a new reason)
     */
    public static boolean addTag(UUID uuid, String reason) {
        if (uuid == null || reason == null || reason.isEmpty()) return false;
        boolean[] created = {false};
        tagMap.compute(uuid, (key, existing) -> {
            if (existing == null) {
                created[0] = true;
                return new Tag(reason);
            }
            existing.addReason(reason);
            return existing;
        });
        dirty.set(true);
        return created[0];
    }

    public static boolean removeTag(UUID uuid) {
        boolean removed = tagMap.remove(uuid) != null;
        if (removed) dirty.set(true);
        return removed;
    }

    /**
     * Free-form interface for querying tags. Returns null if the player isn't tagged.
     */
    public static Tag checkForTags(UUID uuid) {
        return uuid == null ? null : tagMap.get(uuid);
    }

    private static boolean writeDataToFile(Map<UUID, Tag> map) {
        List<String> lines = new ArrayList<>(map.size());
        map.forEach((uuid, tag) -> lines.add(uuid + " " + tag.getTimestamp() + tag.reasonsToString()));

        try {
            File file = tagDataFile;
            if (file.getParentFile() != null) {
                Files.createDirectories(file.getParentFile().toPath());
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                bw.write(gson.toJson(lines));
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Map<UUID, Tag> loadDataFromFile() {
        Map<UUID, Tag> map = new HashMap<>();
        if (tagDataFile == null || !tagDataFile.exists()) return map;

        try (Reader reader = new FileReader(tagDataFile)) {
            List<String> lines = new Gson().fromJson(reader, new TypeToken<List<String>>() {}.getType());
            if (lines != null) {
                for (String line : lines) {
                    parseLine(line, map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private static void parseLine(String line, Map<UUID, Tag> map) {
        // format: uuid timestamp reason1 reason2 ...
        String[] split = line.split(" ");
        if (split.length < 3) return;

        UUID uuid;
        try {
            uuid = UUID.fromString(split[0]);
        } catch (IllegalArgumentException e) {
            return;
        }

        long timestamp;
        try {
            timestamp = Long.parseLong(split[1]);
        } catch (NumberFormatException e) {
            return;
        }

        List<String> reasons = new ArrayList<>(Arrays.asList(split).subList(2, split.length));
        if (reasons.isEmpty()) return;

        map.put(uuid, new Tag(reasons, timestamp));
    }
}