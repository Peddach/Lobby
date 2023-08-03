package de.petropia.lobby.commands;

import de.petropia.lobby.Lobby;
import de.petropia.lobby.minigames.Arena;
import de.petropia.lobby.minigames.ArenaManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArenaCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            return false;
        }
        if(!player.hasPermission("lobby.command.arena")){
            return false;
        }
        if(args.length == 0){
            Lobby.getInstance().getMessageUtil().sendMessage(player, Component.text("Gib ein Argument an"));
            return false;
        }
        if(args[0].equalsIgnoreCase("list")){
            for(Arena arena : ArenaManager.getAllArenas()){
                Lobby.getInstance().getMessageUtil().sendMessage(player, Component.text(arena.toString(), NamedTextColor.GRAY));
            }
            return true;
        }
        if(args[0].equalsIgnoreCase("join")){
            if(args.length != 2){
                Lobby.getInstance().getMessageUtil().sendMessage(player, Component.text("Gib eine Arena an!"));
            }
            String id = args[1];
            for(Arena arena : ArenaManager.getAllArenas()){
                if(!arena.id().equals(id)){
                    continue;
                }
                Lobby.getInstance().getCloudNetAdapter().joinPlayerGame(player, arena.id(), arena.server()).thenAccept(success -> {
                    if(!success){
                        Lobby.getInstance().getMessageUtil().sendMessage(player, Component.text("Fehler beim joinen", NamedTextColor.RED));
                        return;
                    }
                    Lobby.getInstance().getMessageUtil().sendMessage(player, Component.text("Trete " + arena.id() + " bei!", NamedTextColor.GREEN));
                });
                return true;
            }
            Lobby.getInstance().getMessageUtil().sendMessage(player, Component.text("Keine Arena gefunden", NamedTextColor.RED));
            return false;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completion = new ArrayList<>();
        if(args.length == 1){
            completion.add("list");
            completion.add("join");
        }
        if(args.length == 2 && args[1].equalsIgnoreCase("join")){
            completion.addAll(ArenaManager.getAllArenas().stream().map(Arena::id).toList());
        }
        return completion;
    }
}
