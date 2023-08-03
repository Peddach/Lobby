package de.petropia.lobby.minigames;

import de.petropia.turtleServer.api.minigame.GameMode;
import de.petropia.turtleServer.api.minigame.GameState;
import de.petropia.turtleServer.api.minigame.MinigameNames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArenaManager {

    private static final List<Arena> ALL_ARENAS = new ArrayList<>();
    private static final HashMap<ArenaIdentifier, Arena> choosenArenas = new HashMap<>();

    public static void updateArena(Arena arena, boolean remove) {
        for (Arena i : ALL_ARENAS) {
            if (i.id().equals(arena.id())) {
                ALL_ARENAS.remove(i);
                break;
            }
        }
        if(!remove){
            ALL_ARENAS.add(arena);
        }
        List<Arena> filteredArenas = new ArrayList<>(ALL_ARENAS.stream()
                .filter(a -> a.state() == GameState.STARTING || a.state() == GameState.WAITING)
                .filter(a -> a.game().equals(arena.game()))
                .filter(a -> a.mode() == arena.mode())
                .filter(a -> a.playerCount() < a.maxPlayers())
                .toList());
        if(filteredArenas.size() == 0){
            choosenArenas.put(new ArenaIdentifier(arena.game(), arena.mode()), null);
            return;
        }
        if(filteredArenas.size() == 1){
            choosenArenas.put(new ArenaIdentifier(arena.game(), arena.mode()), filteredArenas.get(0));
            return;
        }
        filteredArenas.sort((record1, record2) -> {
            int playerCountComparison = Integer.compare(record2.playerCount(), record1.playerCount());
            if (playerCountComparison != 0) {
                return playerCountComparison;
            }
            return record1.id().compareTo(record2.id());
        });
        choosenArenas.put(new ArenaIdentifier(arena.game(), arena.mode()), filteredArenas.get(0));
    }

    public static Arena getChickenLeagueSigle() {
        return choosenArenas.get(new ArenaIdentifier(MinigameNames.CHICKEN_LEAGUE.name(), GameMode.SINGLE));
    }

    public static Arena getChickenLeagueTeam() {
        return choosenArenas.get(new ArenaIdentifier(MinigameNames.CHICKEN_LEAGUE.name(), GameMode.MULTI));
    }

    public static Arena getBingoSigle() {
        return choosenArenas.get(new ArenaIdentifier(MinigameNames.BINGO.name(), GameMode.SINGLE));
    }

    public static Arena getBingoTeam() {
        return choosenArenas.get(new ArenaIdentifier(MinigameNames.BINGO.name(), GameMode.DUO));
    }

    public static HashMap<ArenaIdentifier, Arena> getChoosenArenas(){
        return choosenArenas;
    }

    public static List<Arena> getAllArenas(){
        return ALL_ARENAS;
    }

    public static class ArenaIdentifier {
        private final String game;
        private final GameMode mode;

        public ArenaIdentifier(String game, GameMode mode) {
            this.game = game.toUpperCase();
            this.mode = mode;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            ArenaIdentifier other = (ArenaIdentifier) obj;
            // Compare the game and mode fields for equality (case-insensitive)
            boolean sameGame = (game == null && other.game == null) || (game != null && game.equalsIgnoreCase(other.game));
            boolean sameMode = (mode == null && other.mode == null) || (mode != null && mode.equals(other.mode));

            return sameGame && sameMode;
        }

        @Override
        public int hashCode() {
            int result = game != null ? game.toLowerCase().hashCode() : 0;
            result = 31 * result + (mode != null ? mode.hashCode() : 0);
            return result;
        }
    }
}
