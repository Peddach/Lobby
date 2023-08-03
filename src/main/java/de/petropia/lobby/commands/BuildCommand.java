package de.petropia.lobby.commands;

import de.petropia.lobby.Lobby;
import de.petropia.lobby.listener.SpawnProtectionListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BuildCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            return false;
        }
        if(!player.hasPermission("lobby.build")){
            Lobby.getInstance().getMessageUtil().sendMessage(player, Component.text("Dazu hast du keine Rechte!", NamedTextColor.RED));
            return false;
        }
        if(SpawnProtectionListener.getAllowedPlayers().contains(player)){
            SpawnProtectionListener.getAllowedPlayers().remove(player);
            Lobby.getInstance().getMessageUtil().sendMessage(player, Component.text("Du kannst nun nicht mehr bauen", NamedTextColor.RED));
            player.setGameMode(GameMode.SURVIVAL);
            return true;
        }
        SpawnProtectionListener.getAllowedPlayers().add(player);
        player.setGameMode(GameMode.CREATIVE);
        Lobby.getInstance().getMessageUtil().sendMessage(player, Component.text("Du kannst nun bauen", NamedTextColor.GREEN));
        return true;
    }
}
