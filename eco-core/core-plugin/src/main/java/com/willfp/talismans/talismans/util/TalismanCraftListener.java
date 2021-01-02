package com.willfp.talismans.talismans.util;

import com.willfp.talismans.talismans.Talisman;
import com.willfp.talismans.talismans.Talismans;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TalismanCraftListener implements Listener {
    @EventHandler
    public void onCraft(@NotNull final PrepareItemCraftEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe)) {
            return;
        }

        ShapedRecipe recipe = (ShapedRecipe) event.getRecipe();

        Talisman talisman = Talismans.getByKey(recipe.getKey());

        if (talisman == null) {
            return;
        }

        if (event.getViewers().isEmpty()) {
            return;
        }

        Player player = (Player) event.getViewers().get(0);

        if (!player.hasPermission("talismans.fromtable." + recipe.getKey().getKey().replace("_", ""))) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onCraft(@NotNull final CraftItemEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe)) {
            return;
        }

        ShapedRecipe recipe = (ShapedRecipe) event.getRecipe();

        Talisman talisman = Talismans.getByKey(recipe.getKey());

        if (talisman == null) {
            return;
        }

        if (event.getViewers().isEmpty()) {
            return;
        }

        Player player = (Player) event.getViewers().get(0);

        if (!player.hasPermission("talismans.fromtable." + recipe.getKey().getKey().replace("_", ""))) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void prepareCraftTalismanListener(@NotNull final PrepareItemCraftEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe)) {
            return;
        }

        ShapedRecipe recipe = (ShapedRecipe) event.getRecipe();

        Talisman talisman = Talismans.getByKey(recipe.getKey());

        if (talisman == null) {
            return;
        }

        if (event.getViewers().isEmpty()) {
            return;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = event.getInventory().getMatrix()[i];

            if (itemStack == null) {
                continue;
            }

            if (itemStack.getType() != Material.PLAYER_HEAD) {
                continue;
            }

            Talisman matchedTalisman = TalismanChecks.getTalismanOnItem(itemStack);

            if (matchedTalisman == null || !Objects.equals(matchedTalisman, talisman.getRecipeTalismanOverlay()[i])) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
            }
        }
    }

    @EventHandler
    public void craftTalismanListener(@NotNull final CraftItemEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe)) {
            return;
        }

        ShapedRecipe recipe = (ShapedRecipe) event.getRecipe();

        Talisman talisman = Talismans.getByKey(recipe.getKey());

        if (talisman == null) {
            return;
        }

        if (event.getViewers().isEmpty()) {
            return;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = event.getInventory().getMatrix()[i];

            if (itemStack == null) {
                continue;
            }

            if (itemStack.getType() != Material.PLAYER_HEAD) {
                continue;
            }

            Talisman matchedTalisman = TalismanChecks.getTalismanOnItem(itemStack);

            if (matchedTalisman == null || !Objects.equals(matchedTalisman, talisman.getRecipeTalismanOverlay()[i])) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
                event.setCancelled(true);
            }
        }
    }
}
