package de.petropia.lobby.navigation;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.bukkit.event.BukkitBridgeProxyPlayerDisconnectEvent;
import de.dytanic.cloudnet.ext.bridge.bukkit.event.BukkitBridgeProxyPlayerLoginSuccessEvent;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.petropia.lobby.Lobby;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

public class HotbarListener implements Listener {

    private final ItemStack navigator = createItem();

    private ItemStack createItem() {
        ItemStack item = new ItemStack(Material.COMPASS);
        CompassMeta compassMeta = (CompassMeta) item.getItemMeta();
        compassMeta.displayName(Component.text("Navigator", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        compassMeta.setLodestone(Lobby.getSpawn());
        compassMeta.setLodestoneTracked(true);
        item.setItemMeta(compassMeta);
        item.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        return item;
    }

    @EventHandler
    public void onItemClick(PlayerInteractEvent event){
        if(event.getItem() != null && event.getItem().equals(navigator)){
            new NavigationGui(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        event.getPlayer().getInventory().clear();
        event.getPlayer().getActivePotionEffects().forEach(e -> event.getPlayer().removePotionEffect(e.getType()));
        event.getPlayer().setExp(0);
        event.getPlayer().setLevel(getNetworkCount());
        event.getPlayer().getInventory().setItem(4, navigator);
    }

    @EventHandler
    public void onPlayerLeaveNetwork(BukkitBridgeProxyPlayerDisconnectEvent event){
        Bukkit.getOnlinePlayers().forEach(p -> p.setLevel(getNetworkCount()));
    }

    @EventHandler
    public void onPlayerJoinNetwork(BukkitBridgeProxyPlayerLoginSuccessEvent event){
        Bukkit.getOnlinePlayers().forEach(p -> p.setLevel(getNetworkCount()));
    }

    private int getNetworkCount(){
        return CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).getOnlineCount();
    }

}
