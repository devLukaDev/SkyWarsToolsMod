package org.devlukadev.skywarstoolsmod.utils.fetchutils.responses;

import java.util.List;

public class NamesResponse {
    public String player;
    public List<NameEntry> data;

    public static class NameEntry {
        public String player;
    }
}
