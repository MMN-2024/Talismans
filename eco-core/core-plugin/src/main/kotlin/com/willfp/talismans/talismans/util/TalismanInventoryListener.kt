package com.willfp.talismans.talismans.util

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
    
    private fun refreshPlayerDelayed(player: Player) {
        // Clear cache immediately
        TalismanChecks.clearCache(player)
        
        // Schedule a delayed task to ensure effects are re-evaluated
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            // Clear cache again to ensure it's fresh
            TalismanChecks.clearCache(player)
            
            // Force re-evaluation by getting talismans (this forces the system to re-read)
            TalismanChecks.getTalismansOnPlayer(player)
            
            // Trigger libreforge refresh by calling the registered refresh function
            // This mimics what happens during reload
            try {
                // Use reflection to call the refresh function that was registered
                val refreshMethod = plugin.javaClass.getMethod("refreshPlayer", Player::class.java)
                refreshMethod.invoke(plugin, player)
            } catch (e: Exception) {
                // If reflection fails, try alternative approach
                // Force a mini "reload" for this player by clearing and re-registering
                plugin.logger.info("Using alternative refresh method for player ${player.name}")
                
                // Clear all caches
                TalismanChecks.clearCache(player)
                
                // Force a complete re-evaluation by simulating what happens on join
                Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                    TalismanChecks.clearCache(player)
                    TalismanChecks.getTalismansOnPlayer(player)
                }, 2L)
            }
        }, 1L) // 1 tick delay
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        
        // Clear cache after inventory modification to ensure talisman effects update immediately
        refreshPlayerDelayed(player)
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerItemHeld(event: PlayerItemHeldEvent) {
        // Clear cache when player switches held item
        refreshPlayerDelayed(event.player)
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        // Clear cache when player drops an item
        refreshPlayerDelayed(event.player)
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
        // Clear cache when player picks up an item
        refreshPlayerDelayed(event.player)
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent) {
        // Clear cache when player swaps items between hands
        refreshPlayerDelayed(event.player)
    }
}