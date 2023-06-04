package me.guswnserver.startier;




import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.block.Action;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;








public class Startier extends JavaPlugin implements Listener {




    private final String GUI_TITLE = ChatColor.GOLD + "Enhance Your Gear";
    private final String ENCHANTED_ANVIL_NAME = ChatColor.GREEN + "Enhance";




    private Set<Material> allowedItems;
    private Map<Player, ItemStack> insertedItems;




    private final String ENHANCEMENT_SHARD_TAG = "enhancement_shard";




    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        allowedItems = initializeAllowedItems();
        insertedItems = new HashMap<>();
        createEnhancementShardRecipe();
    }








    private Set<Material> initializeAllowedItems() {
        Set<Material> items = new HashSet<>();
        items.add(Material.WOODEN_SWORD);
        items.add(Material.WOODEN_AXE);
        items.add(Material.WOODEN_PICKAXE);
        items.add(Material.LEATHER_HELMET);
        items.add(Material.LEATHER_CHESTPLATE);
        items.add(Material.LEATHER_LEGGINGS);
        items.add(Material.LEATHER_BOOTS);
        items.add(Material.STONE_SWORD);
        items.add(Material.STONE_AXE);
        items.add(Material.STONE_PICKAXE);
        items.add(Material.CHAINMAIL_HELMET);
        items.add(Material.CHAINMAIL_CHESTPLATE);
        items.add(Material.CHAINMAIL_LEGGINGS);
        items.add(Material.CHAINMAIL_BOOTS);
        items.add(Material.IRON_SWORD);
        items.add(Material.IRON_AXE);
        items.add(Material.IRON_PICKAXE);
        items.add(Material.IRON_HELMET);
        items.add(Material.IRON_CHESTPLATE);
        items.add(Material.IRON_LEGGINGS);
        items.add(Material.IRON_BOOTS);
        items.add(Material.GOLDEN_SWORD);
        items.add(Material.GOLDEN_AXE);
        items.add(Material.GOLDEN_PICKAXE);
        items.add(Material.GOLDEN_HELMET);
        items.add(Material.GOLDEN_CHESTPLATE);
        items.add(Material.GOLDEN_LEGGINGS);
        items.add(Material.GOLDEN_BOOTS);
        items.add(Material.DIAMOND_SWORD);
        items.add(Material.DIAMOND_AXE);
        items.add(Material.DIAMOND_PICKAXE);
        items.add(Material.DIAMOND_HELMET);
        items.add(Material.DIAMOND_CHESTPLATE);
        items.add(Material.DIAMOND_LEGGINGS);
        items.add(Material.DIAMOND_BOOTS);
        items.add(Material.NETHERITE_SWORD);
        items.add(Material.NETHERITE_AXE);
        items.add(Material.NETHERITE_PICKAXE);
        items.add(Material.NETHERITE_HELMET);
        items.add(Material.NETHERITE_CHESTPLATE);
        items.add(Material.NETHERITE_LEGGINGS);
        items.add(Material.NETHERITE_BOOTS);
        items.add(Material.TRIDENT);
        items.add(Material.BOW);
        items.add(Material.SHIELD);
        return items;
    }




    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null &&
                event.getClickedBlock().getType() == Material.ANVIL) {
            event.setCancelled(true);
            openCustomGUI(event.getPlayer());
        }
    }




    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(GUI_TITLE) && event.getClickedInventory() != null) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();


            if (slot == 4) {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                    ItemStack insertedItem = insertedItems.get(player);
                    insertedItems.put(player, clickedItem);
                    player.getOpenInventory().setItem(4, clickedItem);
                    event.setCurrentItem(insertedItem);
                    player.sendMessage(ChatColor.GREEN + "Items swapped successfully.");
                }
            } else if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() &&
                    event.getCurrentItem().getItemMeta().hasDisplayName() &&
                    event.getCurrentItem().getItemMeta().getDisplayName().equals(ENCHANTED_ANVIL_NAME)) {
                consumeEnhancementShard(player);
            } else {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && allowedItems.contains(clickedItem.getType())) {
                    ItemStack copiedItem = clickedItem.clone();
                    copiedItem.setAmount(1);
                    ItemStack insertedItem = insertedItems.get(player);
                    if (insertedItem != null) {
                        player.getInventory().addItem(insertedItem);
                    }
                    insertedItems.put(player, copiedItem);
                    player.getOpenInventory().setItem(4, copiedItem);
                    event.setCurrentItem(null);
                    player.sendMessage(ChatColor.GREEN + "Item inserted successfully.");
                } else {
                    player.sendMessage(ChatColor.RED + "This item cannot be enhanced. Please insert another item.");
                }
            }
        }
    }




    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            Player player = (Player) event.getPlayer();
            if (insertedItems.containsKey(player)) {
                player.getInventory().addItem(insertedItems.get(player));
                insertedItems.remove(player);
            }
        }
    }




    private void openCustomGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, GUI_TITLE);




        ItemStack enchantedAnvil = createEnchantedAnvil();




// Set items in the GUI
        for (int i = 0; i < 9; i++) {
            if (i != 4) {
                gui.setItem(i, enchantedAnvil);
            }
        }




        player.openInventory(gui);
    }




    private ItemStack createEnchantedAnvil() {
        ItemStack enchantedAnvil = new ItemStack(Material.ANVIL);
        ItemMeta meta = enchantedAnvil.getItemMeta();
        meta.setDisplayName(ENCHANTED_ANVIL_NAME);
        enchantedAnvil.setItemMeta(meta);
        return enchantedAnvil;
    }
    private ItemStack createEnhancementShard() {
        ItemStack enhancementShard = new ItemStack(Material.ECHO_SHARD);
        ItemMeta meta = enhancementShard.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Enhancement Shard");
        NamespacedKey key = new NamespacedKey(this, ENHANCEMENT_SHARD_TAG);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "enhancement_shard");
        enhancementShard.setItemMeta(meta);
        return enhancementShard;
    }




    private void createEnhancementShardRecipe() {
        ItemStack enhancementShard = createEnhancementShard();




        NamespacedKey key = new NamespacedKey(this, "enhancement_shard");
        ShapedRecipe recipe = new ShapedRecipe(key, enhancementShard);
        recipe.shape(
                "IGI",
                "GBG",
                "IGI"
        );
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('G', Material.GOLD_NUGGET);
        recipe.setIngredient('B', Material.BOOK);




        Bukkit.addRecipe(recipe);
    }


    private boolean isEnhancementShard(ItemStack item) {
        if (item == null || item.getType() != Material.ECHO_SHARD) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }
        String displayName = meta.getDisplayName();
        return displayName.equals(ChatColor.GOLD + "Enhancement Shard");
    }


    private int getItemTier(ItemStack item) {
// Implement your logic to determine the item tier based on the item stack
// This can be based on item lore, item name, custom item tags, or any other criteria specific to your implementation


// Example: Check if the item has a custom lore indicating the tier
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasLore()) {
            List<String> lore = meta.getLore();
            for (String line : lore) {
                if (line.contains("Tier ")) {
                    String tierString = line.replace("Tier ", "");
                    try {
                        return Integer.parseInt(tierString);
                    } catch (NumberFormatException e) {
// Invalid tier format, handle the error or continue checking other lines
                    }
                }
            }
        }


// If no tier information is found, return a default value or handle the case accordingly
        return 0;
    }
    private double getFailureChance(int tier) {
        switch (tier) {
            case 1:
                return 0.0; // 0% failure chance for Tier 1
            case 2:
                return 0.18; // 18% failure chance for Tier 2
            case 3:
                return 0.3; // 30% failure chance for Tier 3
            case 4:
                return 0.4; // 40% failure chance for Tier 4
            case 5:
                return 0.53; // 53% failure chance for Tier 5
            case 6:
                return 0.53; // 53% failure chance for Tier 6
            case 7:
                return 0.6; // 60% failure chance for Tier 7
            case 8:
                return 0.62; // 62% failure chance for Tier 8
            case 9:
                return 0.57; // 57% failure chance for Tier 9
            case 10:
                return 0.58; // 58% failure chance for Tier 10
            default:
                return 0.0; // 0% failure chance for invalid tiers
        }
    }


    private double getDestructionChance(int tier, boolean hasFailed) {
        if (hasFailed) {
            switch (tier) {
                case 5:
                    return 0.04; // 4% destruction chance for Tier 5 if failed
                case 6:
                    return 0.12; // 12% destruction chance for Tier 6 if failed
                case 7:
                    return 0.15; // 15% destruction chance for Tier 7 if failed
                case 8:
                    return 0.18; // 18% destruction chance for Tier 8 if failed
                case 9:
                    return 0.25; // 25% destruction chance for Tier 9 if failed
                case 10:
                    return 0.5; // 50% destruction chance for Tier 10 if failed
                default:
                    return 0.0; // 0% destruction chance for other tiers or invalid tiers
            }
        } else {
            return 0.0; // 0% destruction chance if the upgrade was successful
        }
    }


    private double getDegradationChance(int tier) {
        switch (tier) {
            case 1:
                return 0.0; // 0% degradation chance for Tier 1
            case 2:
                return 0.01; // 1% degradation chance for Tier 2
            case 3:
                return 0.06; // 6% degradation chance for Tier 3
            case 4:
                return 0.1; // 10% degradation chance for Tier 4
            case 5:
                return 0.12; // 12% degradation chance for Tier 5
            case 6:
                return 0.21; // 21% degradation chance for Tier 6
            case 7:
                return 0.25; // 25% degradation chance for Tier 7
            case 8:
                return 0.31; // 31% degradation chance for Tier 8
            case 9:
                return 0.39; // 39% degradation chance for Tier 9
            case 10:
                return 0.4; // 40% degradation chance for Tier 10
            default:
                return 0.0; // 0% degradation chance for invalid tiers
        }
    }


    private String getUpgradedDescription(int tier) {
        StringBuilder description = new StringBuilder();
        int numStars = 10 - (tier - 1);
        for (int i = 0; i < numStars; i++) {
            description.append("\u2606");
        }
        return description.toString();
    }


    private String upgradeStarSymbol(String displayName, int tier) {
        return displayName.replaceFirst("\u2606", "\u2605");
    }


    private String degradeStarSymbol(String displayName, int tier) {
        return displayName.replaceFirst("\u2605", "\u2606");
    }


    private double getSuccessChance(int tier) {
        switch (tier) {
            case 1:
                return 1.0; // 100% success chance for Tier 1
            case 2:
                return 0.81; // 81% success chance for Tier 2
            case 3:
                return 0.64; // 64% success chance for Tier 3
            case 4:
                return 0.5; // 50% success chance for Tier 4
            case 5:
                return 0.35; // 35% success chance for Tier 5
            case 6:
                return 0.26; // 26% success chance for Tier 6
            case 7:
                return 0.15; // 15% success chance for Tier 7
            case 8:
                return 0.07; // 7% success chance for Tier 8
            case 9:
                return 0.04; // 4% success chance for Tier 9
            case 10:
                return 0.02; // 2% success chance for Tier 10
            default:
                return 0.0; // 0% success chance for invalid tiers
        }
    }
    private void consumeEnhancementShard(Player player) {
        ItemStack enhancementShard = player.getInventory().getItemInMainHand();
        boolean hasFailed = false; // Initialize hasFailed as false
        if (enhancementShard != null && isEnhancementShard(enhancementShard)) {
// Perform enhancement logic


// Remove enhancement shard from inventory
            int shardAmount = enhancementShard.getAmount();
            if (shardAmount > 1) {
                enhancementShard.setAmount(shardAmount - 1);
            } else {
                player.getInventory().removeItem(enhancementShard);
            }


// Retrieve the inserted item
            ItemStack insertedItem = insertedItems.get(player);
            if (insertedItem != null) {
                ItemMeta itemMeta = insertedItem.getItemMeta();
                if (itemMeta != null) {
// Get the current tier of the item
                    int currentTier = getItemTier(insertedItem);


// Perform the upgrade possibilities
                    double successChance = getSuccessChance(currentTier);
                    double failureChance = getFailureChance(currentTier);
                    double destructionChance = getDestructionChance(currentTier, hasFailed);


                    double randomValue = Math.random();


// Check if the item is successfully upgraded
                    if (randomValue <= successChance) {
// Upgrade the tier of the item
                        int upgradedTier = currentTier + 1;


// Update the item description
                        String upgradedDescription = getUpgradedDescription(upgradedTier);
                        itemMeta.setLore(Collections.singletonList(upgradedDescription));


// Update the very left U+2606 with U+2605 based on the upgraded tier
                        itemMeta.setDisplayName(upgradeStarSymbol(itemMeta.getDisplayName(), upgradedTier));


// Update the item meta
                        insertedItem.setItemMeta(itemMeta);


                        player.sendMessage(ChatColor.GREEN + "Enhancement successful. Your item has been upgraded to Tier " + upgradedTier + ".");
                    } else if (randomValue <= successChance + failureChance) {
// Check if the item is destroyed
                        if (randomValue <= successChance + failureChance + destructionChance) {
// Destroy the item
                            player.getInventory().removeItem(insertedItem);
                            insertedItems.remove(player);


                            player.sendMessage(ChatColor.RED + "Enhancement failed. Your item has been destroyed.");


                            hasFailed = true; // Set hasFailed to true
                        } else {
                            player.sendMessage(ChatColor.RED + "Enhancement failed. Your item remains at its current tier.");


                            hasFailed = true; // Set hasFailed to true
                        }
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "You need an enhancement shard to enhance your gear.");
            }
        }
    }
}



