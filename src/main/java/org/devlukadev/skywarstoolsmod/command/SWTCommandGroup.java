package org.devlukadev.skywarstoolsmod.command;

import org.devlukadev.skywarstoolsmod.utils.ChatLib;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class SWTCommandGroup implements SWTSubCommand {

    private final Map<String, SWTSubCommand> children = new HashMap<>();

    protected void register(SWTSubCommand child) {
        children.put(child.getName().toLowerCase(), child);
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            ChatLib.chat("Usage: /swt " + getName() + " <" + String.join("|", children.keySet()) + ">");
            return;
        }

        SWTSubCommand child = children.get(args[0].toLowerCase());
        if (child == null) {
            ChatLib.chat("Unknown subcommand: " + getName() + " " + args[0]);
            return;
        }

        child.execute(Arrays.copyOfRange(args, 1, args.length));
    }
}