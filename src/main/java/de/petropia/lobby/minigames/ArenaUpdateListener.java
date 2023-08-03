package de.petropia.lobby.minigames;

import de.petropia.lobby.Lobby;
import de.petropia.turtleServer.api.minigame.ArenaDeleteEvent;
import de.petropia.turtleServer.api.minigame.ArenaUpateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class ArenaUpdateListener implements Listener {

    @EventHandler
    public void onArenaUpdate(ArenaUpateEvent event){
        Lobby.getInstance().getLogger().info("RecivedArenaUpdate!"); //TODO remove
        Arena arena = new Arena(
                event.getId(),
                event.getGame(),
                event.getGameState(),
                event.getGameMode(),
                event.getPlayerCount(),
                event.getMaxPlayerCount(),
                event.getServer()
        );
        ArenaManager.updateArena(arena, false);
    }

    @EventHandler
    public void onArenaDelete(ArenaDeleteEvent event){
        for (Arena arena : ArenaManager.getAllArenas()) {
            if(!arena.id().equals(event.getId())){
                continue;
            }
            ArenaManager.updateArena(arena, true);
            return;
        }
    }
}
