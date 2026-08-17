package org.devlukadev.skywarstoolsmod.utils;

import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LocationUtil {

    public static ClientboundLocationPacket currentLocation = null;

    private static final List<Consumer<ClientboundLocationPacket>> listeners = new ArrayList<>();

    public static ClientboundLocationPacket getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Register an additional listener to be notified whenever a new
     * ClientboundLocationPacket is received, after currentLocation is updated.
     */
    public static void addListener(Consumer<ClientboundLocationPacket> listener) {
        listeners.add(listener);
    }

    /**
     * The single handler actually registered with HypixelModAPI.
     * Updates currentLocation, then notifies all registered listeners.
     */
    public static void onLocationReceived(ClientboundLocationPacket packet) {
        currentLocation = packet;
        for (Consumer<ClientboundLocationPacket> listener : listeners) {
            listener.accept(packet);
        }
    }

    public static boolean isInSkyWars(){
        if (currentLocation == null) return false;
        if (!currentLocation.getServerType().isPresent()) return false;
        return currentLocation.getServerType().get().getName().equals("SkyWars");
    }

    public static boolean isInLobby(){
        if (currentLocation == null) return false;
        if (!currentLocation.getServerType().isPresent()) return false;
        return !(getCurrentLocation().getMap().isPresent());
    }
}