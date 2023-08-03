package de.petropia.lobby.portal;

import de.petropia.lobby.Lobby;
import de.petropia.lobby.minigames.Arena;
import de.petropia.lobby.minigames.ArenaManager;
import de.petropia.turtleServer.api.minigame.GameMode;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class MinigamePortal implements Listener {

    private static final List<Player> blackList = new ArrayList<>();

    private final BoundingBox boundingBox;
    private final String game;
    private final GameMode mode;

    public MinigamePortal(BoundingBox boundingBox, String game, GameMode mode){
        this.boundingBox = boundingBox;
        this.game = game;
        this.mode = mode;
        Bukkit.getPluginManager().registerEvents(this, Lobby.getInstance());
    }

    @EventHandler
    public void onPortalEnter(EntityPortalEnterEvent event){
        if(!(event.getEntity() instanceof Player player)){
            return;
        }
        if(blackList.contains(player)){
            return;
        }
        if(!boundingBox.contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())){
            return;
        }
        addPlayerToBlackList(player);
        Arena arena = ArenaManager.getChoosenArenas().get(new ArenaManager.ArenaIdentifier(game.toUpperCase(), mode));
        if(arena == null){
            Lobby.getInstance().getMessageUtil().sendMessage(player, Component.text("Es ist grade keine Arena frei >:(", NamedTextColor.RED));
            return;
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*6, 4, false, false));
        Lobby.getInstance().getCloudNetAdapter().joinPlayerGame(player, arena.id(), arena.server()).thenAccept(success -> {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            if(!success){
                Lobby.getInstance().getMessageUtil().sendMessage(player, Component.text("Das hat leider nicht geklappt! Versuche es später nochmal.", NamedTextColor.RED));
                player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, Sound.Source.MASTER, 1, 1));
                return;
            }
            player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Sound.Source.MASTER, 1, 1));
        });
    }

    private static void addPlayerToBlackList(Player player){
        blackList.add(player);
        Bukkit.getScheduler().runTaskLater(Lobby.getInstance(), () -> blackList.remove(player), 20*6);
    }
}
