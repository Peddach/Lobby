package de.petropia.lobby.minigames;

import de.petropia.turtleServer.api.minigame.GameMode;
import de.petropia.turtleServer.api.minigame.GameState;

public record Arena(
        String id,
        String game,
        GameState state,
        GameMode mode,
        int playerCount,
        int maxPlayers,
        String server
) {}
