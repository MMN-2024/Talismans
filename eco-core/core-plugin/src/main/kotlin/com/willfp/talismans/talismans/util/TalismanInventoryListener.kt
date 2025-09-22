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
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.event.entity.EntityPickupItemEvent

class TalismanInventoryListener(private val plugin: TalismansPlugin) : Listener {
    
    private fun refreshPlayerEffects(player: Player) {
        // Clear talisman cache and update effects immediately
        TalismanChecks.clearCache(player)
        player.toDispatcher().updateEffects()
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        refreshPlayerEffects(player)
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onInventoryDrag(event: InventoryDragEvent) {
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
    fun onEntityPickupItem(event: EntityPickupItemEvent) {
        val player = event.entity as? Player ?: return
        refreshPlayerEffects(player)
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent) {
        refreshPlayerEffects(event.player)
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerItemBreak(event: PlayerItemBreakEvent) {
        refreshPlayerEffects(event.player)
    }
}