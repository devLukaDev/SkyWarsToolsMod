package org.devlukadev.skywarstoolsmod.features.autododge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AutododgeStorage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type LIST_TYPE = new TypeToken<List<String>>() {}.getType();

    private static final File SAVE_FILE = new File(Loader.instance().getConfigDir(), "swt/autododge-maps.json");

    public static void save(List<String> todos) {
        SAVE_FILE.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(SAVE_FILE)) {
            GSON.toJson(todos, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> load() {
        if (!SAVE_FILE.exists()) return new ArrayList<>();
        try (FileReader reader = new FileReader(SAVE_FILE)) {
            List<String> result = GSON.fromJson(reader, LIST_TYPE);
            return result != null ? result : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
