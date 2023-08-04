package de.petropia.lobby.listener;

import de.petropia.lobby.Lobby;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class SpawnProtectionListener implements Listener {

    private static final List<Player> allowedPlayers = new ArrayList<>();

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event){
        if(allowedPlayers.contains(event.getPlayer())){
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event){
        event.message(null);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if(allowedPlayers.contains(event.getPlayer())){
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if(allowedPlayers.contains(event.getPlayer())){
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemMoveInInventory(InventoryMoveItemEvent event){
        for(HumanEntity entity : event.getSource().getViewers()){
            if(!(entity instanceof Player player)){
                continue;
            }
            if(allowedPlayers.contains(player)){
                return;
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onOffHandSwap(InventoryClickEvent event){
        if(allowedPlayers.contains((Player) event.getWhoClicked())){
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event){
        event.setCancelled(true);
        if(event.getEntity().getType() == EntityType.PLAYER && event.getCause().equals(EntityDamageEvent.DamageCause.VOID)){
            event.getEntity().teleport(Lobby.getSpawn());
            return;
        }
        if(event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) { //Allow kill command
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        allowedPlayers.remove(event.getPlayer());
    }

    public static List<Player> getAllowedPlayers(){
        return allowedPlayers;
    }
}
