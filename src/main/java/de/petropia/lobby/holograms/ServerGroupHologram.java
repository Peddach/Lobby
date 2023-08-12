package de.petropia.lobby.holograms;

import de.petropia.lobby.Lobby;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.cloudnetservice.modules.bridge.BridgeDocProperties;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class ServerGroupHologram {

    private final TextDisplay textDisplay;
    private final ItemDisplay itemDisplay;
    private final String group;
    private final String description;

    public ServerGroupHologram(String group, String description, Location location, Material material, double itemOffset){
        textDisplay = location.getWorld().spawn(location, TextDisplay.class);
        textDisplay.setBillboard(Display.Billboard.VERTICAL);
        textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
        textDisplay.setLineWidth(200);
        Component text = MiniMessage.miniMessage().deserialize(description)
                .append(Component.newline())
                .append(Component.text("Spielerzahl wird geladen...", NamedTextColor.RED));
        textDisplay.text(text);
        textDisplay.setBrightness(new Display.Brightness(15, 15));
        textDisplay.setDefaultBackground(false);
        textDisplay.setShadowed(false);
        textDisplay.setBackgroundColor(Color.fromARGB(0, 100,100 ,100));

        location = location.clone();
        location.setY(textDisplay.getBoundingBox().getMaxY() + itemOffset);
        itemDisplay= location.getWorld().spawn(location, ItemDisplay.class);
        itemDisplay.setBillboard(Display.Billboard.VERTICAL);
        itemDisplay.setBrightness(new Display.Brightness(15, 15));
        itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GUI);
        itemDisplay.setItemStack(new ItemStack(material));

        this.group = group;
        this.description = description;

        tick();
    }

    private void tick(){
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Lobby.getInstance(), () -> {
            int playerCount;
            Collection<ServiceInfoSnapshot> services = Lobby.getInstance().getCloudNetAdapter().cloudServiceProviderInstance().servicesByGroup(group);
            playerCount = services.stream().mapToInt(service -> service.readProperty(BridgeDocProperties.PLAYERS).size()).sum();
            Bukkit.getScheduler().runTask(Lobby.getInstance(), () -> {
                Component text = MiniMessage.miniMessage().deserialize(description)
                        .append(Component.newline())
                        .append(Component.text("Spieler online: " + playerCount, NamedTextColor.GREEN));
                textDisplay.text(text);
            });
        }, 20, 3*20);
    }
}
