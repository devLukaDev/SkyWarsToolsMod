package org.devlukadev.skywarstoolsmod.utils.fetchutils.responses;

import java.util.List;
import java.util.Map;

public class OverallResponse {
    public long took;
    public NextSave nextSave;
    public String player;
    public long queried;
    public String uuid;
    public Stats stats;
    public String source;

    public static class NextSave {
        public boolean saved;
        public long lastSaved;
    }

    public static class Stats {
        public Brewery brewery;
        public String brewery_active;
        public Perkslot perkslot;
        public int angel_of_death_level;
        public List<String> packages;
        public double skywars_experience;
        public long coins;
        public int heads;
        public int kills;
        public int deaths;
        public int wins;
        public int losses;
        public long time_played;
    }

    public static class Brewery {
        public int level_up_energy;
        public int corrupting_brew;
        public int gilded_tonic;
        public int ender_elixir;
        public int brawlers_refreshment;
    }

    public static class Perkslot {
        public Map<String, String> normal;
        public Map<String, String> insane;
        public Map<String, String> mega;
    }

}
