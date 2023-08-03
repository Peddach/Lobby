package de.petropia.lobby.listener;

import de.dytanic.cloudnet.ext.bridge.bukkit.event.BukkitBridgeProxyPlayerLoginSuccessEvent;
import de.petropia.lobby.Lobby;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.time.Duration;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        event.getPlayer().teleport(Lobby.getSpawn());
        if(!event.getPlayer().hasPermission("lobby.fly")){
            return;
        }
        event.getPlayer().setAllowFlight(true);
    }

    @EventHandler
    public void onNetworkJoin(BukkitBridgeProxyPlayerLoginSuccessEvent event){
        Bukkit.getScheduler().runTaskLater(Lobby.getInstance(), () -> {
           Player player = Bukkit.getPlayer(event.getNetworkConnectionInfo().getName());
           if(player == null){
               Lobby.getInstance().getLogger().info("No Bukkit Player found for " + event.getNetworkConnectionInfo().getName());
               return;
           }
           player.showTitle(Title.title(
                   Component.text("Willkommen", NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                   Component.text("auf dem Petropia.net Server", NamedTextColor.GRAY),
                   Title.Times.times(
                           Duration.ofMillis(500),
                           Duration.ofSeconds(4),
                           Duration.ofMillis(800)
                   )
           ));
           player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Sound.Source.MASTER, 1, 0.8F));
           Location location = Lobby.getSpawn().clone();
           location.setY(location.getY() + 15);
           Firework firework = location.getWorld().spawn(location, Firework.class);
           FireworkMeta fireworkMeta = firework.getFireworkMeta();
           FireworkEffect effect = FireworkEffect.builder()
                   .withColor(Color.PURPLE)
                   .withFade(Color.YELLOW)
                   .trail(true)
                   .with(FireworkEffect.Type.BALL_LARGE)
                   .build();
            fireworkMeta.addEffect(effect);
            firework.setFireworkMeta(fireworkMeta);
            firework.detonate();
        }, 20);
    }
}
