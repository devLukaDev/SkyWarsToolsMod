package org.devlukadev.skywarstoolsmod.features.tags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tag {

    private final long timestamp;
    private final List<String> reasons;

    public Tag(String reason) {
        this(new ArrayList<>(Collections.singletonList(reason)), System.currentTimeMillis());
    }

    public Tag(List<String> reasons, long timestamp) {
        this.reasons = new ArrayList<>(reasons);
        this.timestamp = timestamp;
    }

    /**
     * @return true if this reason was new and got added
     */
    public boolean addReason(String reason) {
        if (reasons.contains(reason)) return false;
        reasons.add(reason);

        // Send tag to database


        return true;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String reasonsToString() {
        StringBuilder sb = new StringBuilder();
        for (String r : reasons) sb.append(" ").append(r);
        return sb.toString();
    }
}