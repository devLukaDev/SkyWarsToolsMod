package org.devlukadev.skywarstoolsmod.utils.fetchutils.responses;

public class SkyWarsResponse {
    public long took;
    public String player;
    public Display display;
    public long queried;
    public Stats stats;

    public static class Display {
        public String levelFormattedWithBrackets;
        public String levelFormatted;
        public String newPackageRank;
        public String monthlyPackageRank;
        public String rankPlusColor;
        public String monthlyRankColor;
        public String active_scheme;

        public Display() {}

        public static Display anonymous() {
            Display d = new Display();
            d.levelFormatted = "§c[?]";
            d.levelFormattedWithBrackets = "§c[?]";
            return d;
        }

    }
    public static class Stats {
        public int wins;
        public int losses;
        public int kills;
        public int deaths;
        public int skywars_experience;

        public Stats(){}

        public static Stats anonymous(){
            Stats s = new Stats();
            return s;
        }
    }

    public static SkyWarsResponse getNickMock(String name){
        return new SkyWarsResponse(1, name, Display.anonymous(), Stats.anonymous(), -1);
    }

    public SkyWarsResponse(long took, String player, Display display, Stats stats, long queried) {
        this.took = took;
        this.player = player;
        this.display = display;
        this.stats = stats;
        this.queried = queried;


    }
}