package de.petropia.lobby.scoreboard;

import de.petropia.turtleServer.server.prefix.PrefixManager;
import fr.mrmicky.fastboard.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class ScoreboardManager implements Listener {

    private static final HashMap<Player, FastBoard> boards = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        FastBoard board = new FastBoard(event.getPlayer());
        board.updateTitle("§6§lPetropia.net");
        board.updateLines(
                "",
                "§a§lRang",
                getRank(event.getPlayer()),
                ""
        );
        boards.put(event.getPlayer(), board);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        if(!boards.get(event.getPlayer()).isDeleted()){
            boards.remove(event.getPlayer()).delete();
            return;
        }
        boards.remove(event.getPlayer());
    }

    private String getRank(Player player) {
        Component prefix = PrefixManager.getInstance().getPrefixGroup(player).getPrefix();
        String legacyAmpercent = LegacyComponentSerializer.legacySection().serialize(prefix);
        legacyAmpercent = legacyAmpercent.replace("§7|", "");
        legacyAmpercent = legacyAmpercent.trim();
        return legacyAmpercent;
    }
}
