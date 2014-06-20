package me.blackvein.quests;

import java.io.File;
import java.util.Iterator;

import me.blackvein.quests.util.ItemUtil;
import net.citizensnpcs.api.CitizensAPI;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class PlayerListener implements Listener, ColorUtil {

    final Quests plugin;

    public PlayerListener(Quests newPlugin) {

        plugin = newPlugin;

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {

        if (plugin.checkQuester(evt.getPlayer().getName()) == false) {

            if (evt.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

                final Quester quester = plugin.getQuester(evt.getPlayer().getName());
                final Player player = evt.getPlayer();

                if (quester.hasObjective("useBlock")) {

                    quester.useBlock(evt.getClickedBlock().getType());

                } else if (plugin.questFactory.selectedBlockStarts.containsKey(evt.getPlayer())) {

                    final Block block = evt.getClickedBlock();
                    final Location loc = block.getLocation();
                    plugin.questFactory.selectedBlockStarts.put(evt.getPlayer(), block);
                    evt.getPlayer().sendMessage(
                            ChatColor.GOLD + "Selected location " + ChatColor.AQUA + loc.getWorld().getName() + ": "
                                    + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ChatColor.GOLD + " ("
                                    + ChatColor.GREEN + Quester.prettyItemString(block.getType().getId())
                                    + ChatColor.GOLD + ")");

                } else if (plugin.eventFactory.selectedExplosionLocations.containsKey(evt.getPlayer())) {

                    final Block block = evt.getClickedBlock();
                    final Location loc = block.getLocation();
                    plugin.eventFactory.selectedExplosionLocations.put(evt.getPlayer(), block);
                    evt.getPlayer().sendMessage(
                            ChatColor.GOLD + "Selected location " + ChatColor.AQUA + loc.getWorld().getName() + ": "
                                    + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ChatColor.GOLD + " ("
                                    + ChatColor.GREEN + Quester.prettyItemString(block.getType().getId())
                                    + ChatColor.GOLD + ")");

                } else if (plugin.eventFactory.selectedEffectLocations.containsKey(evt.getPlayer())) {

                    final Block block = evt.getClickedBlock();
                    final Location loc = block.getLocation();
                    plugin.eventFactory.selectedEffectLocations.put(evt.getPlayer(), block);
                    evt.getPlayer().sendMessage(
                            ChatColor.GOLD + "Selected location " + ChatColor.AQUA + loc.getWorld().getName() + ": "
                                    + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ChatColor.GOLD + " ("
                                    + ChatColor.GREEN + Quester.prettyItemString(block.getType().getId())
                                    + ChatColor.GOLD + ")");

                } else if (plugin.eventFactory.selectedMobLocations.containsKey(evt.getPlayer())) {

                    final Block block = evt.getClickedBlock();
                    final Location loc = block.getLocation();
                    plugin.eventFactory.selectedMobLocations.put(evt.getPlayer(), block);
                    evt.getPlayer().sendMessage(
                            ChatColor.GOLD + "Selected location " + ChatColor.AQUA + loc.getWorld().getName() + ": "
                                    + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ChatColor.GOLD + " ("
                                    + ChatColor.GREEN + Quester.prettyItemString(block.getType().getId())
                                    + ChatColor.GOLD + ")");

                } else if (plugin.eventFactory.selectedLightningLocations.containsKey(evt.getPlayer())) {

                    final Block block = evt.getClickedBlock();
                    final Location loc = block.getLocation();
                    plugin.eventFactory.selectedLightningLocations.put(evt.getPlayer(), block);
                    evt.getPlayer().sendMessage(
                            ChatColor.GOLD + "Selected location " + ChatColor.AQUA + loc.getWorld().getName() + ": "
                                    + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ChatColor.GOLD + " ("
                                    + ChatColor.GREEN + Quester.prettyItemString(block.getType().getId())
                                    + ChatColor.GOLD + ")");

                } else if (plugin.eventFactory.selectedTeleportLocations.containsKey(evt.getPlayer())) {

                    final Block block = evt.getClickedBlock();
                    final Location loc = block.getLocation();
                    plugin.eventFactory.selectedTeleportLocations.put(evt.getPlayer(), block);
                    evt.getPlayer().sendMessage(
                            ChatColor.GOLD + "Selected location " + ChatColor.AQUA + loc.getWorld().getName() + ": "
                                    + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ChatColor.GOLD + " ("
                                    + ChatColor.GREEN + Quester.prettyItemString(block.getType().getId())
                                    + ChatColor.GOLD + ")");

                } else if (plugin.questFactory.selectedKillLocations.containsKey(evt.getPlayer())) {

                    final Block block = evt.getClickedBlock();
                    final Location loc = block.getLocation();
                    plugin.questFactory.selectedKillLocations.put(evt.getPlayer(), block);
                    evt.getPlayer().sendMessage(
                            ChatColor.GOLD + "Selected location " + ChatColor.AQUA + loc.getWorld().getName() + ": "
                                    + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ChatColor.GOLD + " ("
                                    + ChatColor.GREEN + Quester.prettyItemString(block.getType().getId())
                                    + ChatColor.GOLD + ")");

                } else if (plugin.questFactory.selectedReachLocations.containsKey(evt.getPlayer())) {

                    final Block block = evt.getClickedBlock();
                    final Location loc = block.getLocation();
                    plugin.questFactory.selectedReachLocations.put(evt.getPlayer(), block);
                    evt.getPlayer().sendMessage(
                            ChatColor.GOLD + "Selected location " + ChatColor.AQUA + loc.getWorld().getName() + ": "
                                    + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ChatColor.GOLD + " ("
                                    + ChatColor.GREEN + Quester.prettyItemString(block.getType().getId())
                                    + ChatColor.GOLD + ")");

                } else if (player.isConversing() == false) {

                    for (final Quest q: plugin.quests) {

                        if (q.blockStart != null) {

                            if (q.blockStart.equals(evt.getClickedBlock().getLocation())) {

                                if (quester.currentQuest != null) {

                                    player.sendMessage(ChatColor.YELLOW + "You may only have one active Quest.");

                                } else {

                                    if (quester.completedQuests.contains(q.name)) {

                                        if (q.redoDelay > -1 && (quester.getDifference(q)) > 0) {

                                            player.sendMessage(ChatColor.YELLOW + "You may not take " + ChatColor.AQUA
                                                    + q.name + ChatColor.YELLOW + " again for another "
                                                    + ChatColor.DARK_PURPLE + Quests.getTime(quester.getDifference(q))
                                                    + ChatColor.YELLOW + ".");
                                            return;

                                        } else if (quester.completedQuests.contains(q.name) && q.redoDelay < 0) {

                                            player.sendMessage(ChatColor.YELLOW + "You have already completed "
                                                    + ChatColor.AQUA + q.name + ChatColor.YELLOW + ".");
                                            return;

                                        }

                                    }

                                    quester.questToTake = q.name;

                                    final String s = ChatColor.GOLD + "- " + ChatColor.DARK_PURPLE
                                            + quester.questToTake + ChatColor.GOLD + " -\n" + "\n" + ChatColor.RESET
                                            + plugin.getQuest(quester.questToTake).description + "\n";

                                    for (final String msg: s.split("<br>")) {
                                        player.sendMessage(msg);
                                    }

                                    plugin.conversationFactory.buildConversation(player).begin();

                                }

                                break;
                            }

                        }

                    }

                }

            }

        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {

        final Quester quester = plugin.getQuester(evt.getWhoClicked().getName());
        final Player player = (Player) evt.getWhoClicked();

        if (evt.getInventory().getTitle().equals("Quests")) {

            final ItemStack clicked = evt.getCurrentItem();
            if (clicked != null) {

                for (final Quest quest: plugin.quests) {

                    if (quest.guiDisplay != null) {

                        if (ItemUtil.compareItems(clicked, quest.guiDisplay, false) == 0) {

                            if (quester.currentQuest != null) {
                                player.sendMessage(ColorUtil.YELLOW + "You may only have one active Quest.");
                            } else if (quester.completedQuests.contains(quest.name) && quest.redoDelay < 0) {
                                player.sendMessage(ColorUtil.YELLOW + "You have already completed " + ColorUtil.PURPLE
                                        + quest.name + ColorUtil.YELLOW + ".");
                            } else {

                                boolean takeable = true;

                                if (quester.completedQuests.contains(quest.name)) {

                                    if (quester.getDifference(quest) > 0) {
                                        player.sendMessage(ColorUtil.YELLOW + "You may not take " + ColorUtil.AQUA
                                                + quest.name + ColorUtil.YELLOW + " again for another "
                                                + ColorUtil.PURPLE + Quests.getTime(quester.getDifference(quest))
                                                + ColorUtil.YELLOW + ".");
                                        takeable = false;
                                    }

                                }

                                if (quest.region != null) {

                                    boolean inRegion = false;
                                    final Player p = quester.getPlayer();
                                    final RegionManager rm = Quests.worldGuard.getRegionManager(p.getWorld());
                                    final Iterator<ProtectedRegion> it = rm.getApplicableRegions(p.getLocation())
                                            .iterator();
                                    while (it.hasNext()) {
                                        final ProtectedRegion pr = it.next();
                                        if (pr.getId().equalsIgnoreCase(quest.region)) {
                                            inRegion = true;
                                            break;
                                        }
                                    }

                                    if (inRegion == false) {
                                        player.sendMessage(ColorUtil.YELLOW + "You may not take " + ColorUtil.AQUA
                                                + quest.name + ColorUtil.YELLOW + " at this location.");
                                        takeable = false;
                                    }

                                }

                                if (takeable == true) {

                                    quester.takeQuest(quest, false);

                                }

                                evt.getWhoClicked().closeInventory();

                            }

                        }

                    }

                }

                evt.setCancelled(true);

            }

        }

    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent evt) {

        if (plugin.checkQuester(evt.getPlayer().getName()) == false) {

            final Quester quester = plugin.getQuester(evt.getPlayer().getName());

            if (quester.currentStage != null) {

                if (quester.currentStage.chatEvents.isEmpty() == false) {

                    final String chat = evt.getMessage();
                    for (final String s: quester.currentStage.chatEvents.keySet()) {

                        if (s.equalsIgnoreCase(chat)) {

                            if (quester.eventFired.get(s) == null || quester.eventFired.get(s) == false) {

                                quester.currentStage.chatEvents.get(s).fire(quester);
                                quester.eventFired.put(s, true);

                            }

                        }

                    }

                }

                if (quester.hasObjective("password")) {

                    quester.sayPass(evt);

                }

            }

        }

    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent evt) {

        if (plugin.checkQuester(evt.getPlayer().getName()) == false) {

            final Quester quester = plugin.getQuester(evt.getPlayer().getName());
            if (quester.hasObjective("damageBlock")) {

                quester.damageBlock(evt.getBlock().getType());

            }

        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent evt) {

        if (plugin.checkQuester(evt.getPlayer().getName()) == false) {

            final Quester quester = plugin.getQuester(evt.getPlayer().getName());
            if (quester.hasObjective("placeBlock")) {

                if (evt.isCancelled() == false) {
                    quester.placeBlock(evt.getBlock().getType());
                }

            }

        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent evt) {

        if (plugin.checkQuester(evt.getPlayer().getName()) == false) {

            final Quester quester = plugin.getQuester(evt.getPlayer().getName());
            if (quester.hasObjective("breakBlock")) {

                if (evt.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH) == false
                        && evt.isCancelled() == false) {
                    quester.breakBlock(evt.getBlock().getType());
                }

            }

            if (quester.hasObjective("placeBlock")) {

                if (quester.blocksPlaced.containsKey(evt.getBlock().getType())) {

                    if (quester.blocksPlaced.get(evt.getBlock().getType()) > 0) {

                        if (evt.isCancelled() == false) {
                            quester.blocksPlaced.put(evt.getBlock().getType(),
                                    quester.blocksPlaced.get(evt.getBlock().getType()) - 1);
                        }

                    }

                }

            }

            if (evt.getPlayer().getItemInHand().getType().equals(Material.SHEARS) && quester.hasObjective("cutBlock")) {

                quester.cutBlock(evt.getBlock().getType());

            }

        }

    }

    @EventHandler
    public void onPlayerShearEntity(PlayerShearEntityEvent evt) {

        if (plugin.checkQuester(evt.getPlayer().getName()) == false) {

            final Quester quester = plugin.getQuester(evt.getPlayer().getName());
            if (evt.getEntity().getType().equals(EntityType.SHEEP) && quester.hasObjective("shearSheep")) {

                final Sheep sheep = (Sheep) evt.getEntity();
                quester.shearSheep(sheep.getColor());

            }

        }

    }

    @EventHandler
    public void onEntityTame(EntityTameEvent evt) {

        if (evt.getOwner() instanceof Player) {

            final Player p = (Player) evt.getOwner();
            if (plugin.checkQuester(p.getName()) == false) {

                final Quester quester = plugin.getQuester(p.getName());
                if (quester.hasObjective("tameMob")) {

                    quester.tameMob(evt.getEntityType());

                }

            }

        }

    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent evt) {

        if (plugin.checkQuester(evt.getEnchanter().getName()) == false) {

            final Quester quester = plugin.getQuester(evt.getEnchanter().getName());
            if (quester.hasObjective("enchantItem")) {

                for (final Enchantment e: evt.getEnchantsToAdd().keySet()) {

                    quester.enchantItem(e, evt.getItem().getType());

                }

            }

        }

    }

    /*
     * CRAFTING (Player)
     * 0 - Crafted Slot 1 - Top-left Craft Slot 2 - Top-right Craft Slot 3 -
     * Bottom-left Craft Slot 4 - Bottom-right Craft Slot
     * 5 - Head Slot 6 - Body Slot 7 - Leg Slot 8 - Boots Slot
     * 9-35 - Top-left to Bottom-right inventory slots 36-44 - Left to Right
     * hotbar slots
     * -999 - Drop Slot
     * BREWING
     * 0 - Left Potion Slot 1 - Middle Potion Slot 2 - Right Potion Slot 3-
     * Ingredient Slot
     * 4-30 - Top-left to Bottom-right inventory slots 31-39 - Left to Right
     * hotbar slots
     * ENCHANTING
     * 0 - Enchant Slot
     * 1-27 - Top-left to Bottom-right inventory slots 28-36 - Left to Right
     * hotbar slots
     * ENDER CHEST
     * 0-26 - Top-left to Bottom-right chest slots
     * 27-53 - Top-left to Bottom-right inventory slots 54-62 - Left to Right
     * hotbar slots
     * DISPENSER
     * 0-8 - Top-left to Bottom-right dispenser slots
     * 9-35 - Top-left to Bottom-right inventory slots 36-44 - Left to Right
     * hotbar slots
     * FURNACE
     * 0 - Furnace Slot 1 - Fuel Slot 2 - Product Slot
     * 3-29 - Top-left to Bottom-right inventory slots 30-38 - Left to Right
     * hotbar slots
     * WORKBENCH
     * 0 - Product Slot 1-9 - Top-left to Bottom-right crafting slots
     * CHEST
     * 0-26 - Top-left to Bottom-right chest slots
     * 27-53 - Top-left to Bottom-right inventory slots 54-62 - Left to Right
     * hotbar slots
     * CHEST (Double)
     * 0-53 - Top-left to Bottom-right chest slots
     * 54-80 - Top-left to Bottom-right inventory slots 81-89 - Left to Right
     * hotbar slots
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent evt) {

        if (evt.getEntity() instanceof Player == false) {

            if (evt.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {

                final EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) evt.getEntity()
                        .getLastDamageCause();
                final Entity damager = damageEvent.getDamager();

                if (damager != null) {

                    if (damager instanceof Projectile) {

                        final Projectile p = (Projectile) damager;
                        if (p.getShooter() instanceof Player) {

                            final Player player = (Player) p.getShooter();
                            boolean okay = true;

                            if (plugin.citizens != null) {
                                if (CitizensAPI.getNPCRegistry().isNPC(player)) {
                                    okay = false;
                                }
                            }

                            if (okay) {

                                final Quester quester = plugin.getQuester(player.getName());

                                if (quester.hasObjective("killMob")) {
                                    quester.killMob(evt.getEntity().getLocation(), evt.getEntity().getType());
                                }

                            }
                        }

                    } else if (damager instanceof Player) {

                        boolean okay = true;

                        if (plugin.citizens != null) {
                            if (CitizensAPI.getNPCRegistry().isNPC(damager)) {
                                okay = false;
                            }
                        }

                        if (okay) {

                            final Player player = (Player) damager;
                            final Quester quester = plugin.getQuester(player.getName());
                            if (quester.hasObjective("killMob")) {
                                quester.killMob(evt.getEntity().getLocation(), evt.getEntity().getType());
                            }

                        }
                    }

                }

            }

        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent evt) {

        if (evt.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {

            final EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) evt.getEntity()
                    .getLastDamageCause();
            final Entity damager = damageEvent.getDamager();

            if (damager != null) {

                if (damager instanceof Projectile) {

                    final Projectile p = (Projectile) damager;
                    if (p.getShooter() instanceof Player) {

                        final Player player = (Player) p.getShooter();

                        if (plugin.checkQuester(player.getName()) == false) {

                            boolean okay = true;

                            if (plugin.citizens != null) {
                                if (CitizensAPI.getNPCRegistry().isNPC(player)
                                        || CitizensAPI.getNPCRegistry().isNPC(evt.getEntity())) {
                                    okay = false;
                                }
                            }

                            if (okay) {

                                final Quester quester = plugin.getQuester(player.getName());

                                if (quester.hasObjective("killPlayer")) {
                                    quester.killPlayer(evt.getEntity().getName());
                                }

                            }

                        }

                    }

                } else if (damager instanceof Player) {

                    final Player player = (Player) damager;

                    if (plugin.checkQuester(player.getName()) == false) {

                        boolean okay = true;

                        if (plugin.citizens != null) {

                            if (CitizensAPI.getNPCRegistry().isNPC(player)
                                    || CitizensAPI.getNPCRegistry().isNPC(evt.getEntity())) {
                                okay = false;
                            }

                        }

                        if (okay) {

                            final Quester quester = plugin.getQuester(player.getName());
                            if (quester.hasObjective("killPlayer")) {
                                quester.killPlayer(evt.getEntity().getName());
                            }

                        }

                    }
                }

            }

        }

        final Player player = evt.getEntity();
        if (plugin.checkQuester(player.getName()) == false) {

            final Quester quester = plugin.getQuester(player.getName());
            if (quester.currentStage != null) {
                if (quester.currentStage.deathEvent != null) {
                    quester.currentStage.deathEvent.fire(quester);
                }
            }

        }

    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent evt) {

        final Player player = evt.getPlayer();
        if (plugin.checkQuester(player.getName()) == false) {

            final Quester quester = plugin.getQuester(player.getName());
            if (quester.hasObjective("catchFish") && evt.getState().equals(State.CAUGHT_FISH)) {
                quester.catchFish();
            }

        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {

        if (plugin.checkQuester(evt.getPlayer().getName()) == false) {

            final Quester quester = new Quester(plugin);
            quester.name = evt.getPlayer().getName();
            if (new File(plugin.getDataFolder(), "data/" + quester.name + ".yml").exists()) {
                quester.loadData();
            } else if (Quests.genFilesOnJoin) {
                quester.saveData();
            }

            plugin.questers.put(evt.getPlayer().getName(), quester);

            for (final String s: quester.completedQuests) {

                final Quest q = plugin.getQuest(s);

                if (q != null) {

                    if (quester.completedTimes.containsKey(q.name) == false && q.redoDelay > -1) {
                        quester.completedTimes.put(q.name, System.currentTimeMillis());
                    }

                }

            }

            quester.checkQuest();

            if (quester.currentQuest != null) {

                if (quester.currentStage.delay > -1) {

                    quester.startStageTimer();

                }

            }

        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {

        if (plugin.checkQuester(evt.getPlayer().getName()) == false) {

            final Quester quester = plugin.getQuester(evt.getPlayer().getName());
            if (quester.currentQuest != null) {

                if (quester.currentStage.delay > -1) {
                    quester.stopStageTimer();
                }

                if (quester.currentStage.disconnectEvent != null) {
                    quester.currentStage.disconnectEvent.fire(quester);
                }

            }

            if (quester.hasData()) {
                quester.saveData();
            }

            if (plugin.questFactory.selectingNPCs.contains(evt.getPlayer())) {
                plugin.questFactory.selectingNPCs.remove(evt.getPlayer());
            }
            plugin.questers.remove(quester.name);

        }

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt) {

        if (plugin.checkQuester(evt.getPlayer().getName()) == false) {

            boolean isPlayer = true;
            if (plugin.citizens != null) {
                if (CitizensAPI.getNPCRegistry().isNPC(evt.getPlayer())) {
                    isPlayer = false;
                }
            }

            if (isPlayer) {

                final Quester quester = plugin.getQuester(evt.getPlayer().getName());

                if (quester.hasObjective("reachLocation")) {

                    quester.reachLocation(evt.getTo());

                }

            }

        }

    }
}
