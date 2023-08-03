package de.petropia.lobby.listener;

import de.dytanic.cloudnet.ext.bridge.bukkit.event.BukkitCloudServiceStopEvent;
import de.petropia.lobby.minigames.Arena;
import de.petropia.lobby.minigames.ArenaManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class ServerShutdownListener implements Listener {

    @EventHandler
    public void onShutdown(BukkitCloudServiceStopEvent event) {
        String serverName = event.getServiceInfoSnapshot().getName();
        List<Arena> invalidArenas = ArenaManager.getAllArenas()
                .stream()
                .filter(arena -> arena.server().equals(serverName))
                .toList();
        invalidArenas.forEach(arena -> ArenaManager.updateArena(arena, true));
    }

}
