package org.devlukadev.skywarstoolsmod.features.sessions;

public class SessionData {
    public int kills = 0;
    public int deaths = 0;
    public int wins = 0;
    public int losses = 0;
    public long time_played = 0;
    public double xpGained = 0;
    public long sessionStartMillis = System.currentTimeMillis();
    public long lastSync = -1;

    public BaselineSnapshot baseline;

    public static class BaselineSnapshot {
        public int kills;
        public int deaths;
        public int wins;
        public int losses;
        public long time_played;
        public double xp;
        public long fetchedAt;
    }
}

