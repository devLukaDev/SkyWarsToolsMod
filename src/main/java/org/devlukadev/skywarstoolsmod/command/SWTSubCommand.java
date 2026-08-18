package org.devlukadev.skywarstoolsmod.command;


public interface SWTSubCommand {
    String getName();
    void execute(String[] args);
}
