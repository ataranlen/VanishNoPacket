package org.kitteh.vanish.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.LazyMetadataValue.CacheStrategy;
import org.kitteh.vanish.VanishCheck;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;

public final class ListenPlayerQuit implements Listener {
    private final VanishPlugin plugin;

    public ListenPlayerQuit(VanishPlugin instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoinEarly(PlayerJoinEvent event) {
        event.getPlayer().setMetadata("vanished", new LazyMetadataValue(this.plugin, CacheStrategy.NEVER_CACHE, new VanishCheck(this.plugin.getManager(), event.getPlayer().getName())));
        this.plugin.getManager().resetSeeing(event.getPlayer());
        if (VanishPerms.joinVanished(event.getPlayer())) {
            this.plugin.getManager().toggleVanishQuiet(event.getPlayer(), false);
            this.plugin.hooksVanish(event.getPlayer());
        }
        this.plugin.hooksJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (this.plugin.getManager().isVanished(player)) {
            this.plugin.messageStatusUpdate(ChatColor.DARK_AQUA + event.getPlayer().getName() + " has quit vanished");
        }
        this.plugin.getManager().playerQuit(player);
        this.plugin.hooksQuit(player);
        this.plugin.getManager().getAnnounceManipulator().dropDelayedAnnounce(player.getName());
        if (!this.plugin.getManager().getAnnounceManipulator().playerHasQuit(player.getName()) || VanishPerms.silentQuit(player)) {
            event.setQuitMessage(null);
        }
        this.plugin.chestFakeClose(event.getPlayer().getName());
    }
}