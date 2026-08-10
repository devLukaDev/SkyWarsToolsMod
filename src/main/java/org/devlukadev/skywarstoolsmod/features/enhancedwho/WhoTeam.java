package org.devlukadev.skywarstoolsmod.features.enhancedwho;

import java.util.List;

public class WhoTeam {
    private final int teamNumber;
    private final List<Player> players;

    public WhoTeam(int teamNumber, List<Player> players) {
        this.teamNumber = teamNumber;
        this.players = players;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getFormattedMessage() {
        StringBuilder builder = new StringBuilder("§rTeam #" + teamNumber + ": ");

        for (int i = 0; i < players.size(); i++) {
            builder.append("§r").append(players.get(i).getFormattedName());

            if (i < players.size() - 1) {
                builder.append(", ");
            }
        }

        builder.append("§r");
        return builder.toString();
    }

    public static class Player {
        private final String formattedName;
        private final String name;

        public Player(String formattedName, String name) {
            this.formattedName = formattedName;
            this.name = name;
        }

        public String getFormattedName() {
            return formattedName;
        }

        public String getName() {
            return name;
        }
    }
}
