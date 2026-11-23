package com.example.fastrestart;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class FastRestart extends JavaPlugin {

    private boolean isRestarting = false;

    @Override
    public void onEnable() {
        getLogger().info("FastRestart enabled!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("fastrestart")) return false;

        if (!sender.hasPermission("fastrestart.use")) {
            sender.sendMessage(Component.text("You don't have permission to use this.", NamedTextColor.RED));
            return true;
        }

        if (isRestarting) {
            sender.sendMessage(Component.text("A restart is already in progress!", NamedTextColor.YELLOW));
            return true;
        }

        int seconds = 5;
        if (args.length > 0) {
            try {
                seconds = Math.max(1, Integer.parseInt(args));
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Please enter a valid number of seconds.", NamedTextColor.RED));
                return true;
            }
        }

        startCountdown(seconds);
        return true;
    }

    private void startCountdown(int startSeconds) {
        isRestarting = true;

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            int timeLeft = startSeconds;

            @Override
            public void run() {
                if (timeLeft > 0) {
                    Bukkit.broadcast(Component.text("⚠ Server restarting in " + timeLeft + "s...", NamedTextColor.GOLD));
                    timeLeft--;
                } else {
                    Component kickMessage = Component.text("Server is restarting! Rejoin in a few seconds.", NamedTextColor.GREEN);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.kick(kickMessage);
                    }
                    Bukkit.savePlayers();
                    Bukkit.shutdown();
                }
            }
        }, 0L, 20L);
    }
}
