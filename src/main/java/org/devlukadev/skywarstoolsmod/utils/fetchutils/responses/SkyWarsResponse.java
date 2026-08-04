package org.devlukadev.skywarstoolsmod.utils.fetchutils.responses;

public class SkyWarsResponse {
    public long took;
    public String player;
    public Display display;
    public long queried;
    public double exp;

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

    public static SkyWarsResponse getNickMock(String name){
        return new SkyWarsResponse(1, name, Display.anonymous(), 1, -1);
    }

    public SkyWarsResponse(long took, String player, Display display, long queried, double exp) {
        this.took = took;
        this.player = player;
        this.display = display;
        this.queried = queried;
        this.exp = exp;
    }


}