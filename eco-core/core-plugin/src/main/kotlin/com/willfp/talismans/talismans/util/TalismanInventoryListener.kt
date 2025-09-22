package com.willfp.talismans.talismans.util

import com.willfp.libreforge.updateEffects
import com.willfp.libreforge.toDispatcher
import com.willfp.talismans.TalismansPlugin
import org.bukkit.Bukkit
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
    
    private fun refreshPlayerEffects(player: Player) {
        // Clear talisman cache immediately
        TalismanChecks.clearCache(player)
        
        // Schedule a delayed task to force complete refresh
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            // Clear cache again
            TalismanChecks.clearCache(player)
            
            // Force libreforge to update all effects for this player
            try {
                player.toDispatcher().updateEffects()
            } catch (e: Exception) {
                // Fallback: try to force refresh by getting talismans
                TalismanChecks.getTalismansOnPlayer(player)
                
                // Additional fallback: try to simulate what happens on reload
                Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                    TalismanChecks.clearCache(player)
                    TalismanChecks.getTalismansOnPlayer(player)
                }, 2L)
            }
        }, 1L)
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        refreshPlayerEffects(player)
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerItemHeld(event: PlayerItemHeldEvent) {
        refreshPlayerEffects(event.player)
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        refreshPlayerEffects(event.player)
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
        refreshPlayerEffects(event.player)
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent) {
        refreshPlayerEffects(event.player)
    }
}