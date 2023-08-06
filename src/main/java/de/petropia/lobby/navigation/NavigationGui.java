package de.petropia.lobby.navigation;

import de.petropia.lobby.Lobby;
import de.petropia.lobby.minigames.ArenaManager;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public class NavigationGui {

    private final Player player;
    private final Gui gui;

    public NavigationGui(Player player) {
        this.player = player;
        gui = Gui.gui()
                .rows(4)
                .disableAllInteractions()
                .title(Component.text("Navigator", NamedTextColor.GOLD).decorate(TextDecoration.ITALIC, TextDecoration.BOLD))
                .create();
        gui.setItem(4, createSpacelifeItem());
        gui.setItem(11, createBingoItem());
        gui.setItem(20, createChickenLeagueItem());
        gui.setItem(15, creatWebsiteItem());
        gui.setItem(24, createFriendsItem());
        gui.setItem(31, createSpawnItem());
        gui.open(player);
    }

    private GuiItem createSpawnItem() {
        return ItemBuilder.from(Material.NETHER_STAR)
                .name(Component.text("Spawn"))
                .lore(
                        Component.empty(),
                        Component.text("Teleportiere dich zum Spawn", NamedTextColor.GRAY),
                        Component.empty()
                )
                .asGuiItem(event -> {
                    gui.close(player);
                    player.teleport(Lobby.getSpawn());
                });
    }

    private GuiItem createFriendsItem() {
        return ItemBuilder.skull()
                .owner(player)
                .name(Component.text("Freunde", NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                .lore(
                        Component.empty(),
                        Component.text("Zeige deine Freunde an", NamedTextColor.GRAY),
                        Component.text("--In Arbeit--", NamedTextColor.RED),
                        Component.empty(),
                        leftClickAction("Öffnen"),
                        Component.empty()
                )
                .asGuiItem(event -> gui.close(player));
    }

    private GuiItem creatWebsiteItem() {
        return ItemBuilder.from(Material.END_CRYSTAL)
                .name(Component.text("Website", NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                .lore(
                        Component.empty(),
                        Component.text("Besuche unsere Website", NamedTextColor.GRAY),
                        Component.empty(),
                        leftClickAction("Öffnen"),
                        Component.empty()
                )
                .asGuiItem(event -> {
                    gui.close(player);
                    Lobby.getInstance().getMessageUtil().sendMessage(player, Component.text("Klicke ", NamedTextColor.GRAY)
                            .append(Component.text("hier", NamedTextColor.GOLD).decorate(TextDecoration.BOLD).clickEvent(
                                    ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, Lobby.getInstance().getConfig().getString("Website"))
                            ))
                            .append(Component.text(" um auf unsere Website zu gelangen", NamedTextColor.GRAY)));
                });
    }



    private GuiItem createSpacelifeItem(){
        return ItemBuilder.from(Material.GOLDEN_APPLE)
                .name(Component.text("Spacelife", NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                .lore(
                        Component.empty(),
                        Component.text("Ein Survivalmodus mit Wirschaft-, Freebuild- und SkyBlock", NamedTextColor.GRAY),
                        Component.text("aspekten. Levele deine Jobs, verdiene Geld, Miete deinen", NamedTextColor.GRAY),
                        Component.text("eigenen Shop, farme in der Farmwelt oder errichte das schönste", NamedTextColor.GRAY),
                        Component.text("Grundstück des Servers in der Bauwelt!", NamedTextColor.GRAY),
                        Component.empty(),
                        leftClickAction("Teleportieren"),
                        rightClickAction("Spacelife betreten"),
                        Component.empty()
                )
                .asGuiItem(event -> {
                    if(event.isLeftClick()){
                        Location location = Lobby.getInstance().readLocationFromConfig("Locations.Spacelife");
                        gui.close(player);
                        player.teleport(location);
                        return;
                    }
                    if(event.isRightClick()){
                        gui.close(player);
                        Lobby.getInstance().getCloudNetAdapter().sendPlayerToServer(player, "SL_Spawn-1");
                    }
                });
    }

    private GuiItem createBingoItem(){
        return ItemBuilder.from(Material.PAPER)
                .name(Component.text("Bingo", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .lore(
                        Component.empty(),
                        Component.text("Suche auf einer zufällig generierten Welt, alleine", NamedTextColor.GRAY),
                        Component.text("oder im Team, nach 5 Items in einer Reihe auf deiner", NamedTextColor.GRAY),
                        Component.text("Bingokarte.", NamedTextColor.GRAY),
                        Component.empty(),
                        leftClickAction("Teleportieren"),
                        rightClickAction("Minigame beitreten"),
                        Component.empty()
                ).asGuiItem(event -> {
                    if(event.isLeftClick()){
                        Location location = Lobby.getInstance().readLocationFromConfig("Locations.Bingo");
                        gui.close(player);
                        player.teleport(location);
                        return;
                    }
                    if(event.isRightClick()){
                        gui.close(player);
                        if(ArenaManager.getBingoSigle() == null){
                            Lobby.getInstance().getMessageUtil().sendMessage(player, Component.text("Akutell ist keine Runde frei", NamedTextColor.RED));
                            return;
                        }
                        Lobby.getInstance().getCloudNetAdapter().joinPlayerGame(player, ArenaManager.getBingoSigle().id(), ArenaManager.getBingoSigle().server());
                    }
                });
    }

    private GuiItem createChickenLeagueItem(){
        return ItemBuilder.from(Material.WOODEN_SHOVEL)
                .name(Component.text("ChickenLeague", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .lore(
                        Component.empty(),
                        Component.text("Spiele alleine oder im Team mit einem Huhn Fußball.", NamedTextColor.GRAY),
                        Component.text("Wähle zwischen drei verschiedenen Schlägern um das", NamedTextColor.GRAY),
                        Component.text("Huhn in das gegnerische Tor zu befördern oder halte", NamedTextColor.GRAY),
                        Component.text("deinen Gegenspieler mit diversen Specialitems auf.", NamedTextColor.GRAY),
                        Component.empty(),
                        leftClickAction("Teleportieren"),
                        rightClickAction("Minigame beitreten"),
                        Component.empty()
                ).flags(ItemFlag.HIDE_ATTRIBUTES)
                .asGuiItem(event -> {
                    if(event.isLeftClick()){
                        Location location = Lobby.getInstance().readLocationFromConfig("Locations.ChickenLeague");
                        gui.close(player);
                        player.teleport(location);
                        return;
                    }
                    if(event.isRightClick()){
                        gui.close(player);
                        if(ArenaManager.getChickenLeagueSigle() == null){
                            Lobby.getInstance().getMessageUtil().sendMessage(player, Component.text("Akutell ist keine Runde frei", NamedTextColor.RED));
                            return;
                        }
                        Lobby.getInstance().getCloudNetAdapter().joinPlayerGame(player, ArenaManager.getChickenLeagueSigle().id(), ArenaManager.getChickenLeagueSigle().server());
                    }
                });
    }

    private Component leftClickAction(String description){
        return Component.text("Linksklick", NamedTextColor.GOLD).append(Component.text(" >> ", NamedTextColor.DARK_GRAY)).append(Component.text(description, NamedTextColor.GRAY));
    }
    private Component rightClickAction(String description){
        return Component.text("Rechtsklick", NamedTextColor.GOLD).append(Component.text(" >> ", NamedTextColor.DARK_GRAY)).append(Component.text(description, NamedTextColor.GRAY));
    }
}
