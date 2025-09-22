package com.willfp.talismans.talismans.util

import com.willfp.talismans.TalismansPlugin
import com.willfp.libreforge.refreshPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class TalismanInventoryListener(private val plugin: TalismansPlugin) : Listener {
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        
        // Clear cache after inventory modification to ensure talisman effects update immediately
        TalismanChecks.clearCache(player)
        refreshPlayer(player)
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerItemHeld(event: PlayerItemHeldEvent) {
        // Clear cache when player switches held item
        TalismanChecks.clearCache(event.player)
        refreshPlayer(event.player)
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        // Clear cache when player drops an item
        TalismanChecks.clearCache(event.player)
        refreshPlayer(event.player)
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
        // Clear cache when player picks up an item
        TalismanChecks.clearCache(event.player)
        refreshPlayer(event.player)
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent) {
        // Clear cache when player swaps items between hands
        TalismanChecks.clearCache(event.player)
        refreshPlayer(event.player)
    }
}