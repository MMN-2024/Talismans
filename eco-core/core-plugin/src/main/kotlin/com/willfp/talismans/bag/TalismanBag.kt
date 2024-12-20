package com.willfp.talismans.bag

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.eco.util.MenuUtils
import com.willfp.ecomponent.menuStateVar
import com.willfp.talismans.talismans.util.TalismanChecks
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.math.ceil
import kotlin.math.min

private val Menu.talismanBag by menuStateVar<List<ItemStack>>(
    emptyList()
)

object TalismanBag {
    private val menus = mutableMapOf<Int, Menu>()

    // Initialize legacyKey and key as nullable properties
    private var legacyKey: PersistentDataKey<List<String>>? = null
    private var key: PersistentDataKey<List<String>>? = null
    private lateinit var emptyItem: ItemStack

    private val savedItems = mutableMapOf<UUID, List<ItemStack>>()

    private val Player.bagSize: Int
        get() {
            val prefix = "talismans.bagsize."
            var highest = -1
            for (permission in this.effectivePermissions.map { it.permission }) {
                if (!permission.startsWith(prefix)) {
                    continue
                }

                val limit = permission.substring(permission.lastIndexOf(".") + 1).toInt()
                if (limit > highest) {
                    highest = limit
                }
            }
            return if (highest < 0) {
                10000
            } else {
                highest
            }
        }

    internal fun update(plugin: EcoPlugin) {
        // Initialize legacyKey and key only once
        legacyKey = PersistentDataKey(
            plugin.namespacedKeyFactory.create("talisman_bag"),
            PersistentDataKeyType.STRING_LIST,
            emptyList()
        )

        key = PersistentDataKey(
            plugin.namespacedKeyFactory.create("bag"),
            PersistentDataKeyType.STRING_LIST,
            emptyList()
        )

        // Initialize emptyItem
        emptyItem = ItemStackBuilder(Items.lookup(plugin.configYml.getString("bag.blocked-item")))
            .addLoreLines(plugin.configYml.getStrings("bag.blocked-item-lore"))
            .build()

        // Create the menus based on rows
        for (rows in 1..6) {
            menus[rows] = menu(rows) {
                title = plugin.configYml.getFormattedString("bag.title")

                allowChangingHeldItem()

                for (row in 1..rows) {
                    for (column in 1..9) {
                        setSlot(row, column, slot({ player, menu ->
                            val bagSize = player.bagSize
                            val index = MenuUtils.rowColumnToSlot(row, column)

                            if (index >= bagSize) {
                                emptyItem
                            } else {
                                menu.talismanBag[player].getOrNull(index)?.clone() ?: ItemStack(Material.AIR)
                            }
                        }) {
                            setCaptive(true)
                            notCaptiveFor { MenuUtils.rowColumnToSlot(row, column) >= it.bagSize }

                            setCaptiveFilter { _, _, itemStack ->
                                TalismanChecks.getTalismanOnItem(itemStack) != null
                            }
                        })
                    }
                }

                onRender { player, menu ->
                    // Make sure to initialize talismanBag
                    if (menu.talismanBag[player].isEmpty()) {
                        // Safely access legacyKey and key
                        legacyKey?.let { legacy ->
                            menu.talismanBag[player] += player.profile.read(legacy).map { Items.lookup(it).item }
                        }
                        key?.let { k ->
                            menu.talismanBag[player] += player.profile.read(k).mapNotNull { Items.fromSNBT(it) }
                        }
                    }

                    val items = menu.getCaptiveItems(player)
                        .filterNot { EmptyTestableItem().matches(it) }

                    val toWrite = items
                        .filter { TalismanChecks.getTalismanOnItem(it) != null }

                    savedItems[player.uniqueId] = toWrite.toList()

                    key?.let { k ->
                        player.profile.write(k, toWrite.map { Items.toSNBT(it) })
                    }
                }

                onClose { event, menu ->
                    val player = event.player as Player

                    val items = menu.getCaptiveItems(player)
                        .filterNot { EmptyTestableItem().matches(it) }

                    val toWrite = savedItems[player.uniqueId] ?: emptyList()

                    key?.let { k ->
                        player.profile.write(k, toWrite.map { Items.toSNBT(it) })
                    }

                    legacyKey?.let { legacy ->
                        player.profile.write(legacy, emptyList())
                    }

                    val toDrop = items.filter { TalismanChecks.getTalismanOnItem(it) == null }

                    DropQueue(player)
                        .setLocation(player.eyeLocation)
                        .forceTelekinesis()
                        .addItems(toDrop)
                        .push()
                }
            }
        }

        // Register placeholder
        PlaceholderManager.registerPlaceholder(
            PlayerPlaceholder(
                plugin,
                "bagsize"
            ) { it.bagSize.toString() }
        )
    }

    fun open(player: Player) {
        val bagRows = min(6, ceil(player.bagSize / 9.0).toInt())
        menus[bagRows]!!.open(player)
    }

    fun getTalismans(player: Player): List<ItemStack> {
        // Initialize savedItems only if not already initialized
        if (!savedItems.contains(player.uniqueId)) {
            val legacyItems = legacyKey?.let { legacy ->
                player.profile.read(legacy)
                    .map { Items.lookup(it).item }
                    .filterNot { EmptyTestableItem().matches(it) }
                    .filter { TalismanChecks.getTalismanOnItem(it) != null }
            } ?: emptyList()

            val items = key?.let { k ->
                player.profile.read(k)
                    .mapNotNull { Items.fromSNBT(it) }
                    .filter { TalismanChecks.getTalismanOnItem(it) != null }
            } ?: emptyList()

            savedItems[player.uniqueId] = (legacyItems + items).toList()
        }

        return savedItems[player.uniqueId] ?: emptyList()
    }
}