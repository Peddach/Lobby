package de.petropia.lobby.listener;

import de.petropia.lobby.minigames.Arena;
import de.petropia.lobby.minigames.ArenaManager;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.events.service.CloudServiceLifecycleChangeEvent;
import eu.cloudnetservice.driver.service.ServiceLifeCycle;

import java.util.List;

public class ServerShutdownListener {

    @EventListener
    public void onShutdown(CloudServiceLifecycleChangeEvent event) {
        if(!event.lastLifeCycle().equals(ServiceLifeCycle.RUNNING)){
            return;
        }
        String serverName = event.serviceInfo().name();
        List<Arena> invalidArenas = ArenaManager.getAllArenas()
                .stream()
                .filter(arena -> arena.server().equals(serverName))
                .toList();
        invalidArenas.forEach(arena -> ArenaManager.updateArena(arena, true));
    }

}
