package me.blackvein.quests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import me.blackvein.quests.util.ItemUtil;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

public class Quester {

    String                                   name;
    boolean                                  editorMode                  = false;
    boolean                                  holdingQuestItemFromStorage = false;
    boolean                                  delayOver                   = true;
    public Quest                             currentQuest;
    public String                            questToTake;
    public Stage                             currentStage;
    public int                               currentStageIndex           = 0;
    int                                      questPoints                 = 0;
    Quests                                   plugin;
    public LinkedList<String>                completedQuests             = new LinkedList<String>();
    Map<String, Long>                        completedTimes              = new HashMap<String, Long>();
    Map<String, Integer>                     amountsCompleted            = new HashMap<String, Integer>();
    Map<Material, Integer>                   blocksDamaged               = new EnumMap<Material, Integer>(
                                                                                 Material.class);
    Map<Material, Integer>                   blocksBroken                = new EnumMap<Material, Integer>(
                                                                                 Material.class);
    Map<Material, Integer>                   blocksPlaced                = new EnumMap<Material, Integer>(
                                                                                 Material.class);
    Map<Material, Integer>                   blocksUsed                  = new EnumMap<Material, Integer>(
                                                                                 Material.class);
    Map<Material, Integer>                   blocksCut                   = new EnumMap<Material, Integer>(
                                                                                 Material.class);
    Map<Integer, Integer>                    potionsBrewed               = new HashMap<Integer, Integer>();
    Map<ItemStack, Integer>                  itemsDelivered              = new HashMap<ItemStack, Integer>();
    int                                      fishCaught                  = 0;
    int                                      playersKilled               = 0;
    long                                     delayStartTime              = 0;
    long                                     delayTimeLeft               = -1;
    Map<String, Long>                        playerKillTimes             = new HashMap<String, Long>();
    Map<Map<Enchantment, Material>, Integer> itemsEnchanted              = new HashMap<Map<Enchantment, Material>, Integer>();
    LinkedList<EntityType>                   mobsKilled                  = new LinkedList<EntityType>();
    LinkedList<Integer>                      mobNumKilled                = new LinkedList<Integer>();
    LinkedList<Location>                     locationsToKillWithin       = new LinkedList<Location>();
    LinkedList<Integer>                      radiiToKillWithin           = new LinkedList<Integer>();
    Map<Integer, Boolean>                    citizensInteracted          = new HashMap<Integer, Boolean>();
    LinkedList<Integer>                      citizensKilled              = new LinkedList<Integer>();
    LinkedList<Integer>                      citizenNumKilled            = new LinkedList<Integer>();
    LinkedList<Location>                     locationsReached            = new LinkedList<Location>();
    LinkedList<Boolean>                      hasReached                  = new LinkedList<Boolean>();
    LinkedList<Integer>                      radiiToReachWithin          = new LinkedList<Integer>();
    Map<EntityType, Integer>                 mobsTamed                   = new EnumMap<EntityType, Integer>(
                                                                                 EntityType.class);
    Map<DyeColor, Integer>                   sheepSheared                = new EnumMap<DyeColor, Integer>(
                                                                                 DyeColor.class);
    Map<String, Boolean>                     passwordsSaid               = new HashMap<String, Boolean>();
    public Map<String, Integer>              customObjectiveCounts       = new HashMap<String, Integer>();
    public Map<String, Boolean>              eventFired                  = new HashMap<String, Boolean>();
    final Random                             random                      = new Random();

    public Quester(Quests newPlugin) {

        plugin = newPlugin;

    }

    public Player getPlayer() {

        return plugin.getServer().getPlayerExact(name);

    }

    public void takeQuest(Quest q, boolean override) {

        final Player player = plugin.getServer().getPlayer(name);

        if (q.testRequirements(player) == true || override) {

            currentQuest = q;
            currentStage = q.orderedStages.getFirst();
            addEmpties();

            if (!override) {

                if (q.moneyReq > 0) {
                    Quests.economy.withdrawPlayer(name, q.moneyReq);
                }

                for (final ItemStack is: q.items) {
                    if (q.removeItems.get(q.items.indexOf(is)) == true) {
                        Quests.removeItem(player.getInventory(), is);
                    }
                }

                player.sendMessage(ChatColor.GREEN + "Quest accepted: " + q.name);
                player.sendMessage("");

            }

            player.sendMessage(ChatColor.GOLD + "---(Objectives)---");

            for (final String s: getObjectivesReal()) {
                player.sendMessage(s);
            }

            final String stageStartMessage = currentStage.startMessage;
            if (stageStartMessage != null) {
                getPlayer().sendMessage(Quests.parseString(stageStartMessage, currentQuest));
            }

            if (currentStage.chatEvents.isEmpty() == false) {

                for (final String chatTrigger: currentStage.chatEvents.keySet()) {

                    eventFired.put(chatTrigger, false);

                }

            }

            if (q.initialEvent != null) {
                q.initialEvent.fire(this);
            }
            if (currentStage.startEvent != null) {
                currentStage.startEvent.fire(this);
            }

            saveData();

        } else {

            player.sendMessage(q.failRequirements);

        }

    }

    public LinkedList<String> getObjectivesReal() {

        if (currentStage.objectiveOverride != null) {
            final LinkedList<String> objectives = new LinkedList<String>();
            objectives.add(ChatColor.GREEN + currentStage.objectiveOverride);
            return objectives;
        } else {
            return getObjectives();
        }

    }

    public LinkedList<String> getObjectives() {

        final LinkedList<String> unfinishedObjectives = new LinkedList<String>();
        final LinkedList<String> finishedObjectives = new LinkedList<String>();
        final LinkedList<String> objectives = new LinkedList<String>();

        for (final Entry<Material, Integer> e: currentStage.blocksToDamage.entrySet()) {

            for (final Entry<Material, Integer> e2: blocksDamaged.entrySet()) {

                if (e2.getKey().equals(e.getKey())) {

                    if (e2.getValue() < e.getValue()) {

                        unfinishedObjectives.add(ChatColor.GREEN + "Damage "
                                + Quester.prettyItemString(e2.getKey().getId()) + ": " + e2.getValue() + "/"
                                + e.getValue());

                    } else {

                        finishedObjectives.add(ChatColor.GRAY + "Damage "
                                + Quester.prettyItemString(e2.getKey().getId()) + ": " + e2.getValue() + "/"
                                + e.getValue());

                    }

                }

            }

        }

        for (final Entry<Material, Integer> e: currentStage.blocksToBreak.entrySet()) {

            for (final Entry<Material, Integer> e2: blocksBroken.entrySet()) {

                if (e2.getKey().equals(e.getKey())) {

                    if (e2.getValue() < e.getValue()) {

                        unfinishedObjectives.add(ChatColor.GREEN + "Break "
                                + Quester.prettyItemString(e2.getKey().getId()) + ": " + e2.getValue() + "/"
                                + e.getValue());

                    } else {

                        finishedObjectives.add(ChatColor.GRAY + "Break "
                                + Quester.prettyItemString(e2.getKey().getId()) + ": " + e2.getValue() + "/"
                                + e.getValue());

                    }

                }

            }

        }

        for (final Entry<Material, Integer> e: currentStage.blocksToPlace.entrySet()) {

            for (final Entry<Material, Integer> e2: blocksPlaced.entrySet()) {

                if (e2.getKey().equals(e.getKey())) {

                    if (e2.getValue() < e.getValue()) {

                        unfinishedObjectives.add(ChatColor.GREEN + "Place "
                                + Quester.prettyItemString(e2.getKey().getId()) + ": " + e2.getValue() + "/"
                                + e.getValue());

                    } else {

                        finishedObjectives.add(ChatColor.GRAY + "Place "
                                + Quester.prettyItemString(e2.getKey().getId()) + ": " + e2.getValue() + "/"
                                + e.getValue());

                    }

                }

            }

        }

        for (final Entry<Material, Integer> e: currentStage.blocksToUse.entrySet()) {

            for (final Entry<Material, Integer> e2: blocksUsed.entrySet()) {

                if (e2.getKey().equals(e.getKey())) {

                    if (e2.getValue() < e.getValue()) {

                        unfinishedObjectives.add(ChatColor.GREEN + "Use "
                                + Quester.prettyItemString(e2.getKey().getId()) + ": " + e2.getValue() + "/"
                                + e.getValue());

                    } else {

                        finishedObjectives.add(ChatColor.GRAY + "Use " + Quester.prettyItemString(e2.getKey().getId())
                                + ": " + e2.getValue() + "/" + e.getValue());

                    }

                }

            }

        }

        for (final Entry<Material, Integer> e: currentStage.blocksToCut.entrySet()) {

            for (final Entry<Material, Integer> e2: blocksCut.entrySet()) {

                if (e2.getKey().equals(e.getKey())) {

                    if (e2.getValue() < e.getValue()) {

                        unfinishedObjectives.add(ChatColor.GREEN + "Cut "
                                + Quester.prettyItemString(e2.getKey().getId()) + ": " + e2.getValue() + "/"
                                + e.getValue());

                    } else {

                        finishedObjectives.add(ChatColor.GRAY + "Cut " + Quester.prettyItemString(e2.getKey().getId())
                                + ": " + e2.getValue() + "/" + e.getValue());

                    }

                }

            }

        }

        if (currentStage.fishToCatch != null) {

            if (fishCaught < currentStage.fishToCatch) {

                unfinishedObjectives
                        .add(ChatColor.GREEN + "Catch Fish: " + fishCaught + "/" + currentStage.fishToCatch);

            } else {

                finishedObjectives.add(ChatColor.GRAY + "Catch Fish: " + fishCaught + "/" + currentStage.fishToCatch);

            }

        }

        Map<Enchantment, Material> set;
        Map<Enchantment, Material> set2;
        Set<Enchantment> enchantSet;
        Set<Enchantment> enchantSet2;
        Collection<Material> matSet;
        Enchantment enchantment = null;
        Enchantment enchantment2 = null;
        Material mat = null;
        int num1;
        int num2;

        for (final Entry<Map<Enchantment, Material>, Integer> e: currentStage.itemsToEnchant.entrySet()) {

            for (final Entry<Map<Enchantment, Material>, Integer> e2: itemsEnchanted.entrySet()) {

                set = e2.getKey();
                set2 = e.getKey();
                enchantSet = set.keySet();
                enchantSet2 = set2.keySet();
                for (final Object o: enchantSet.toArray()) {

                    enchantment = (Enchantment) o;

                }
                for (final Object o: enchantSet2.toArray()) {

                    enchantment2 = (Enchantment) o;

                }
                num1 = e2.getValue();
                num2 = e.getValue();

                matSet = set.values();

                for (final Object o: matSet.toArray()) {

                    mat = (Material) o;

                }

                if (enchantment2 == enchantment) {

                    if (num1 < num2) {

                        unfinishedObjectives.add(ChatColor.GREEN + "Enchant " + Quester.prettyItemString(mat.getId())
                                + " with " + Quester.prettyEnchantmentString(enchantment) + ": " + num1 + "/" + num2);

                    } else {

                        finishedObjectives.add(ChatColor.GRAY + "Enchant " + Quester.prettyItemString(mat.getId())
                                + " with " + Quester.prettyEnchantmentString(enchantment) + ": " + num1 + "/" + num2);

                    }

                }

            }

        }

        for (final EntityType e: currentStage.mobsToKill) {

            for (final EntityType e2: mobsKilled) {

                if (e == e2) {

                    if (mobNumKilled.get(mobsKilled.indexOf(e2)) < currentStage.mobNumToKill
                            .get(currentStage.mobsToKill.indexOf(e))) {

                        if (currentStage.locationsToKillWithin.isEmpty()) {
                            unfinishedObjectives.add(ChatColor.GREEN + "Kill " + Quester.prettyMobString(e) + ": "
                                    + (mobNumKilled.get(mobsKilled.indexOf(e2))) + "/"
                                    + (currentStage.mobNumToKill.get(currentStage.mobsToKill.indexOf(e))));
                        } else {
                            unfinishedObjectives.add(ChatColor.GREEN + "Kill " + Quester.prettyMobString(e) + " at "
                                    + currentStage.areaNames.get(currentStage.mobsToKill.indexOf(e)) + ": "
                                    + (mobNumKilled.get(mobsKilled.indexOf(e2))) + "/"
                                    + (currentStage.mobNumToKill.get(currentStage.mobsToKill.indexOf(e))));
                        }
                    } else {

                        if (currentStage.locationsToKillWithin.isEmpty()) {
                            finishedObjectives.add(ChatColor.GRAY + "Kill " + Quester.prettyMobString(e) + ": "
                                    + (mobNumKilled.get(mobsKilled.indexOf(e2))) + "/"
                                    + (currentStage.mobNumToKill.get(currentStage.mobsToKill.indexOf(e))));
                        } else {
                            finishedObjectives.add(ChatColor.GRAY + "Kill " + Quester.prettyMobString(e) + " at "
                                    + currentStage.areaNames.get(currentStage.mobsToKill.indexOf(e)) + ": "
                                    + (mobNumKilled.get(mobsKilled.indexOf(e2))) + "/"
                                    + (currentStage.mobNumToKill.get(currentStage.mobsToKill.indexOf(e))));
                        }

                    }

                }

            }

        }

        if (currentStage.playersToKill != null) {

            if (playersKilled < currentStage.playersToKill) {

                unfinishedObjectives.add(ChatColor.GREEN + "Kill a Player: " + playersKilled + "/"
                        + currentStage.playersToKill);

            } else {

                finishedObjectives.add(ChatColor.GRAY + "Kill a Player: " + playersKilled + "/"
                        + currentStage.playersToKill);

            }

        }

        for (final ItemStack is: currentStage.itemsToDeliver) {

            final int delivered = itemsDelivered.get(is);
            final int amt = is.getAmount();
            final Integer npc = currentStage.itemDeliveryTargets.get(currentStage.itemsToDeliver.indexOf(is));

            if (delivered < amt) {

                unfinishedObjectives.add(ChatColor.GREEN + "Deliver " + ItemUtil.getName(is) + " to "
                        + plugin.getNPCName(npc) + ": " + delivered + "/" + amt);

            } else {

                finishedObjectives.add(ChatColor.GRAY + "Deliver " + ItemUtil.getName(is) + " to "
                        + plugin.getNPCName(npc) + ": " + delivered + "/" + amt);

            }

        }

        for (final Integer n: currentStage.citizensToInteract) {

            for (final Entry<Integer, Boolean> e: citizensInteracted.entrySet()) {

                if (e.getKey().equals(n)) {

                    if (e.getValue() == false) {

                        unfinishedObjectives.add(ChatColor.GREEN + "Talk to " + plugin.getNPCName(n));

                    } else {

                        finishedObjectives.add(ChatColor.GRAY + "Talk to " + plugin.getNPCName(n));

                    }

                }

            }

        }

        for (final Integer n: currentStage.citizensToKill) {

            for (final Integer n2: citizensKilled) {

                if (n.equals(n2)) {

                    if (citizenNumKilled.get(citizensKilled.indexOf(n2)) < currentStage.citizenNumToKill
                            .get(currentStage.citizensToKill.indexOf(n))) {

                        unfinishedObjectives.add(ChatColor.GREEN + "Kill " + plugin.getNPCName(n) + ChatColor.GREEN
                                + " " + citizenNumKilled.get(currentStage.citizensToKill.indexOf(n)) + "/"
                                + currentStage.citizenNumToKill.get(currentStage.citizensToKill.indexOf(n)));

                    } else {

                        finishedObjectives.add(ChatColor.GRAY + "Kill " + plugin.getNPCName(n) + " "
                                + currentStage.citizenNumToKill.get(currentStage.citizensToKill.indexOf(n)) + "/"
                                + currentStage.citizenNumToKill.get(currentStage.citizensToKill.indexOf(n)));

                    }

                }

            }

        }

        for (final Entry<EntityType, Integer> e: currentStage.mobsToTame.entrySet()) {

            for (final Entry<EntityType, Integer> e2: mobsTamed.entrySet()) {

                if (e.getKey().equals(e2.getKey())) {

                    if (e2.getValue() < e.getValue()) {

                        unfinishedObjectives.add(ChatColor.GREEN + "Tame "
                                + Quester.getCapitalized(e.getKey().getName()) + ": " + e2.getValue() + "/"
                                + e.getValue());

                    } else {

                        finishedObjectives.add(ChatColor.GRAY + "Tame " + Quester.getCapitalized(e.getKey().getName())
                                + ": " + e2.getValue() + "/" + e.getValue());

                    }

                }

            }

        }

        for (final Entry<DyeColor, Integer> e: currentStage.sheepToShear.entrySet()) {

            for (final Entry<DyeColor, Integer> e2: sheepSheared.entrySet()) {

                if (e.getKey().equals(e2.getKey())) {

                    if (e2.getValue() < e.getValue()) {

                        unfinishedObjectives.add(ChatColor.GREEN + "Shear "
                                + e.getKey().name().toString().toLowerCase() + " sheep: " + e2.getValue() + "/"
                                + e.getValue());

                    } else {

                        finishedObjectives.add(ChatColor.GRAY + "Shear " + e.getKey().name().toString().toLowerCase()
                                + " sheep: " + e2.getValue() + "/" + e.getValue());

                    }

                }

            }

        }

        for (final Location l: currentStage.locationsToReach) {

            for (final Location l2: locationsReached) {

                if (l.equals(l2)) {

                    if (hasReached.get(locationsReached.indexOf(l2)) == false) {

                        unfinishedObjectives.add(ChatColor.GREEN + "Go to "
                                + currentStage.locationNames.get(currentStage.locationsToReach.indexOf(l)));

                    } else {

                        finishedObjectives.add(ChatColor.GRAY + "Go to "
                                + currentStage.locationNames.get(currentStage.locationsToReach.indexOf(l)));

                    }

                }

            }

        }

        for (final String s: currentStage.passwordDisplays) {

            if (passwordsSaid.get(s) == false) {

                unfinishedObjectives.add(ChatColor.GREEN + s);

            } else {

                finishedObjectives.add(ChatColor.GRAY + s);

            }

        }

        int index = 0;
        for (final CustomObjective co: currentStage.customObjectives) {

            for (final Entry<String, Integer> entry: customObjectiveCounts.entrySet()) {

                if (co.getName().equals(entry.getKey())) {

                    String display = co.getDisplay();

                    final Map<String, Object> datamap = currentStage.customObjectiveData.get(index);
                    for (final String key: co.datamap.keySet()) {
                        display = display.replaceAll("%" + (key) + "%", ((String) datamap.get(key)));
                    }

                    if (entry.getValue() < currentStage.customObjectiveCounts.get(index)) {
                        if (co.isCountShown() && co.isEnableCount()) {
                            display = display.replaceAll("%count%", entry.getValue() + "/"
                                    + currentStage.customObjectiveCounts.get(index));
                        }
                        unfinishedObjectives.add(ChatColor.GREEN + display);
                    } else {
                        if (co.isCountShown() && co.isEnableCount()) {
                            display = display.replaceAll("%count%", currentStage.customObjectiveCounts.get(index) + "/"
                                    + currentStage.customObjectiveCounts.get(index));
                        }
                        finishedObjectives.add(ChatColor.GRAY + display);
                    }

                }

            }

            index++;

        }

        objectives.addAll(unfinishedObjectives);
        objectives.addAll(finishedObjectives);

        return objectives;

    }

    public boolean hasObjective(String s) {

        if (currentStage == null) {
            return false;
        }

        if (s.equalsIgnoreCase("damageBlock")) {
            return !currentStage.blocksToDamage.isEmpty();

        } else if (s.equalsIgnoreCase("breakBlock")) {
            return !currentStage.blocksToBreak.isEmpty();

        } else if (s.equalsIgnoreCase("placeBlock")) {
            return !currentStage.blocksToPlace.isEmpty();

        } else if (s.equalsIgnoreCase("useBlock")) {
            return !currentStage.blocksToUse.isEmpty();

        } else if (s.equalsIgnoreCase("cutBlock")) {
            return !currentStage.blocksToCut.isEmpty();

        } else if (s.equalsIgnoreCase("catchFish")) {
            return currentStage.fishToCatch != null;

        } else if (s.equalsIgnoreCase("enchantItem")) {
            return !currentStage.itemsToEnchant.isEmpty();

        } else if (s.equalsIgnoreCase("killMob")) {
            return !currentStage.mobsToKill.isEmpty();

        } else if (s.equalsIgnoreCase("deliverItem")) {
            return !currentStage.itemsToDeliver.isEmpty();

        } else if (s.equalsIgnoreCase("killPlayer")) {
            return currentStage.playersToKill != null;

        } else if (s.equalsIgnoreCase("talkToNPC")) {
            return !currentStage.citizensToInteract.isEmpty();

        } else if (s.equalsIgnoreCase("killNPC")) {
            return !currentStage.citizensToKill.isEmpty();

        } else if (s.equalsIgnoreCase("tameMob")) {
            return !currentStage.mobsToTame.isEmpty();

        } else if (s.equalsIgnoreCase("shearSheep")) {
            return !currentStage.sheepToShear.isEmpty();

        } else if (s.equalsIgnoreCase("craftItem")) {
            return !currentStage.itemsToCraft.isEmpty();

        } else if (s.equalsIgnoreCase("password")) {
            return !currentStage.passwordPhrases.isEmpty();

        } else {
            return !currentStage.locationsToReach.isEmpty();

        }

    }

    public boolean hasCustomObjective(String s) {

        if (customObjectiveCounts.containsKey(s)) {

            final int count = customObjectiveCounts.get(s);

            int index = -1;
            for (int i = 0; i < currentStage.customObjectives.size(); i++) {
                if (currentStage.customObjectives.get(i).getName().equals(s)) {
                    index = i;
                    break;
                }
            }

            final int count2 = currentStage.customObjectiveCounts.get(index);

            return count <= count2;

        }

        return false;

    }

    public void damageBlock(Material m) {

        if (blocksDamaged.containsKey(m)) {

            if (blocksDamaged.get(m) < currentStage.blocksToDamage.get(m)) {
                final int i = blocksDamaged.get(m);
                blocksDamaged.put(m, (i + 1));

                if (blocksDamaged.get(m).equals(currentStage.blocksToDamage.get(m))) {
                    finishObjective("damageBlock", m, null, null, null, null, null, null, null, null, null);
                }

            }

        }

    }

    public void breakBlock(Material m) {

        if (blocksBroken.containsKey(m)) {

            if (blocksBroken.get(m) < currentStage.blocksToBreak.get(m)) {
                final int i = blocksBroken.get(m);
                blocksBroken.put(m, (i + 1));

                if (blocksBroken.get(m).equals(currentStage.blocksToBreak.get(m))) {
                    finishObjective("breakBlock", m, null, null, null, null, null, null, null, null, null);
                }
            }

        }

    }

    public void placeBlock(Material m) {

        if (blocksPlaced.containsKey(m)) {

            if (blocksPlaced.get(m) < currentStage.blocksToPlace.get(m)) {
                final int i = blocksPlaced.get(m);
                blocksPlaced.put(m, (i + 1));

                if (blocksPlaced.get(m).equals(currentStage.blocksToPlace.get(m))) {
                    finishObjective("placeBlock", m, null, null, null, null, null, null, null, null, null);
                }
            }

        }

    }

    public void useBlock(Material m) {

        if (blocksUsed.containsKey(m)) {

            if (blocksUsed.get(m) < currentStage.blocksToUse.get(m)) {
                final int i = blocksUsed.get(m);
                blocksUsed.put(m, (i + 1));

                if (blocksUsed.get(m).equals(currentStage.blocksToUse.get(m))) {
                    finishObjective("useBlock", m, null, null, null, null, null, null, null, null, null);
                }

            }

        }

    }

    public void cutBlock(Material m) {

        if (blocksCut.containsKey(m)) {

            if (blocksCut.get(m) < currentStage.blocksToCut.get(m)) {
                final int i = blocksCut.get(m);
                blocksCut.put(m, (i + 1));

                if (blocksCut.get(m).equals(currentStage.blocksToCut.get(m))) {
                    finishObjective("cutBlock", m, null, null, null, null, null, null, null, null, null);
                }

            }

        }

    }

    public void catchFish() {

        if (fishCaught < currentStage.fishToCatch) {
            fishCaught++;

            if (((Integer) fishCaught).equals(currentStage.fishToCatch)) {
                finishObjective("catchFish", null, null, null, null, null, null, null, null, null, null);
            }

        }

    }

    public void enchantItem(Enchantment e, Material m) {

        for (final Entry<Map<Enchantment, Material>, Integer> entry: itemsEnchanted.entrySet()) {

            if (entry.getKey().containsKey(e) && entry.getKey().containsValue(m)) {

                for (final Entry<Map<Enchantment, Material>, Integer> entry2: currentStage.itemsToEnchant.entrySet()) {

                    if (entry2.getKey().containsKey(e) && entry2.getKey().containsValue(m)) {

                        if (entry.getValue() < entry2.getValue()) {

                            final Integer num = entry.getValue() + 1;
                            itemsEnchanted.put(entry.getKey(), num);

                            if (num.equals(entry2.getValue())) {
                                finishObjective("enchantItem", m, null, e, null, null, null, null, null, null, null);
                            }

                        }
                        break;

                    }

                }

                break;

            }

        }

    }

    public void killMob(Location l, EntityType e) {

        if (mobsKilled.contains(e)) {

            if (locationsToKillWithin.isEmpty() == false) {

                final int index = mobsKilled.indexOf(e);
                final Location locationToKillWithin = locationsToKillWithin.get(index);
                final double radius = radiiToKillWithin.get(index);
                final int numKilled = mobNumKilled.get(index);
                if (l.getX() < (locationToKillWithin.getX() + radius)
                        && l.getX() > (locationToKillWithin.getX() - radius)) {

                    if (l.getZ() < (locationToKillWithin.getZ() + radius)
                            && l.getZ() > (locationToKillWithin.getZ() - radius)) {

                        if (l.getY() < (locationToKillWithin.getY() + radius)
                                && l.getY() > (locationToKillWithin.getY() - radius)) {

                            if (numKilled < currentStage.mobNumToKill.get(index)) {

                                final Integer numKilledInteger = numKilled + 1;

                                mobNumKilled.set(index, numKilledInteger);

                                if ((numKilledInteger).equals(currentStage.mobNumToKill.get(index))) {
                                    finishObjective("killMob", null, null, null, e, null, null, null, null, null, null);
                                }

                            }

                        }

                    }

                }

            } else {

                if (mobNumKilled.get(mobsKilled.indexOf(e)) < currentStage.mobNumToKill.get(mobsKilled.indexOf(e))) {

                    mobNumKilled.set(mobsKilled.indexOf(e), mobNumKilled.get(mobsKilled.indexOf(e)) + 1);

                    if ((mobNumKilled.get(mobsKilled.indexOf(e))).equals(currentStage.mobNumToKill.get(mobsKilled
                            .indexOf(e)))) {
                        finishObjective("killMob", null, null, null, e, null, null, null, null, null, null);
                    }

                }

            }

        }

    }

    public void killPlayer(String player) {

        if (playerKillTimes.containsKey(player)) {

            final long killTime = playerKillTimes.get(player);
            final long comparator = plugin.killDelay * 1000;
            final long currentTime = System.currentTimeMillis();

            if ((currentTime - killTime) < comparator) {

                plugin.getServer()
                        .getPlayer(name)
                        .sendMessage(
                                ChatColor.RED + "[Quests] Kill did not count. You must wait " + ChatColor.DARK_PURPLE
                                        + Quests.getTime(comparator - (currentTime - killTime)) + ChatColor.RED
                                        + " before you can kill " + ChatColor.DARK_PURPLE + player + ChatColor.RED
                                        + " again.");
                return;

            }

        }

        playerKillTimes.put(player, System.currentTimeMillis());

        if (playersKilled < currentStage.playersToKill) {
            playersKilled++;

            if (((Integer) playersKilled).equals(currentStage.playersToKill)) {
                finishObjective("killPlayer", null, null, null, null, null, null, null, null, null, null);
            }

        }

    }

    public void interactWithNPC(NPC n) {

        if (citizensInteracted.containsKey(n.getId())) {

            if (citizensInteracted.get(n.getId()) == false) {
                citizensInteracted.put(n.getId(), true);
                finishObjective("talkToNPC", null, null, null, null, null, n, null, null, null, null);
            }

        }

    }

    public void killNPC(NPC n) {

        if (citizensKilled.contains(n.getId())) {

            final int index = citizensKilled.indexOf(n.getId());
            if (citizenNumKilled.get(index) < currentStage.citizenNumToKill.get(index)) {
                citizenNumKilled.set(index, citizenNumKilled.get(index) + 1);
                if (citizenNumKilled.get(index) == currentStage.citizenNumToKill.get(index)) {
                    finishObjective("killNPC", null, null, null, null, null, n, null, null, null, null);
                }
            }

        }

    }

    public void reachLocation(Location l) {

        for (final Location location: locationsReached) {

            final int index = locationsReached.indexOf(location);
            final Location locationToReach = currentStage.locationsToReach.get(index);
            final double radius = radiiToReachWithin.get(index);
            if (l.getX() < (locationToReach.getX() + radius) && l.getX() > (locationToReach.getX() - radius)) {

                if (l.getZ() < (locationToReach.getZ() + radius) && l.getZ() > (locationToReach.getZ() - radius)) {

                    if (l.getY() < (locationToReach.getY() + radius) && l.getY() > (locationToReach.getY() - radius)) {

                        if (hasReached.get(index) == false) {

                            hasReached.set(index, true);
                            finishObjective("reachLocation", null, null, null, null, null, null, location, null, null,
                                    null);

                        }

                    }

                }

            }

        }

    }

    public void tameMob(EntityType entity) {

        if (mobsTamed.containsKey(entity)) {

            mobsTamed.put(entity, (mobsTamed.get(entity) + 1));

            if (mobsTamed.get(entity).equals(currentStage.mobsToTame.get(entity))) {
                finishObjective("tameMob", null, null, null, entity, null, null, null, null, null, null);
            }

        }

    }

    public void shearSheep(DyeColor color) {

        if (sheepSheared.containsKey(color)) {

            sheepSheared.put(color, (sheepSheared.get(color) + 1));

            if (sheepSheared.get(color).equals(currentStage.sheepToShear.get(color))) {
                finishObjective("shearSheep", null, null, null, null, null, null, null, color, null, null);
            }

        }

    }

    public void deliverItem(ItemStack i) {

        final Player player = plugin.getServer().getPlayer(name);

        ItemStack found = null;

        for (final ItemStack is: itemsDelivered.keySet()) {

            if (ItemUtil.compareItems(i, is, true) == 0) {
                found = is;
                break;
            }

        }
        if (found != null) {

            final int amount = itemsDelivered.get(found);
            final int req = currentStage.itemsToDeliver.get(currentStage.itemsToDeliver.indexOf(found)).getAmount();

            if (amount < req) {

                if ((i.getAmount() + amount) > req) {

                    itemsDelivered.put(found, req);
                    final int index = player.getInventory().first(i);
                    i.setAmount(i.getAmount() - (req - amount)); // Take away
                                                                 // the
                                                                 // remaining
                                                                 // amount
                                                                 // needed to be
                                                                 // delivered
                                                                 // from the
                                                                 // item stack
                    player.getInventory().setItem(index, i);
                    player.updateInventory();
                    finishObjective("deliverItem", null, found, null, null, null, null, null, null, null, null);

                } else if ((i.getAmount() + amount) == req) {

                    itemsDelivered.put(found, req);
                    player.getInventory().setItem(player.getInventory().first(i), null);
                    player.updateInventory();
                    finishObjective("deliverItem", null, found, null, null, null, null, null, null, null, null);

                } else {

                    itemsDelivered.put(found, (amount + i.getAmount()));
                    player.getInventory().setItem(player.getInventory().first(i), null);
                    player.updateInventory();
                    final String message = Quests.parseString(
                            currentStage.deliverMessages.get(random.nextInt(currentStage.deliverMessages.size())),
                            plugin.citizens.getNPCRegistry().getById(
                                    currentStage.itemDeliveryTargets.get(currentStage.itemsToDeliver.indexOf(found))));
                    player.sendMessage(message);

                }

            }

        }

    }

    public void sayPass(AsyncPlayerChatEvent evt) {

        boolean done;
        for (final LinkedList<String> passes: currentStage.passwordPhrases) {

            done = false;

            for (final String pass: passes) {

                if (pass.equalsIgnoreCase(evt.getMessage())) {

                    evt.setCancelled(true);
                    final String display = currentStage.passwordDisplays.get(currentStage.passwordPhrases
                            .indexOf(passes));
                    passwordsSaid.put(display, true);
                    done = true;
                    finishObjective("password", null, null, null, null, null, null, null, null, display, null);
                    break;

                }

            }

            if (done) {
                break;
            }

        }

    }

    public void finishObjective(String objective, Material material, ItemStack itemstack, Enchantment enchantment,
            EntityType mob, String player, NPC npc, Location location, DyeColor color, String pass, CustomObjective co) {

        final Player p = plugin.getServer().getPlayerExact(name);

        if (currentStage.objectiveOverride != null) {

            if (testComplete()) {
                final String message = ChatColor.GREEN + "(Completed) " + currentStage.objectiveOverride;
                p.sendMessage(message);
                currentQuest.nextStage(this);
            }
            return;

        }

        if (objective.equalsIgnoreCase("password")) {

            final String message = ChatColor.GREEN + "(Completed) " + pass;
            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        } else if (objective.equalsIgnoreCase("damageBlock")) {

            String message = ChatColor.GREEN + "(Completed) Damage " + Quester.prettyItemString(material.getId());
            message = message + " " + currentStage.blocksToDamage.get(material) + "/"
                    + currentStage.blocksToDamage.get(material);
            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        } else if (objective.equalsIgnoreCase("breakBlock")) {

            String message = ChatColor.GREEN + "(Completed) Break " + Quester.prettyItemString(material.getId());
            message = message + " " + currentStage.blocksToBreak.get(material) + "/"
                    + currentStage.blocksToBreak.get(material);
            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        } else if (objective.equalsIgnoreCase("placeBlock")) {

            String message = ChatColor.GREEN + "(Completed) Place " + Quester.prettyItemString(material.getId());
            message = message + " " + currentStage.blocksToPlace.get(material) + "/"
                    + currentStage.blocksToPlace.get(material);
            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        } else if (objective.equalsIgnoreCase("useBlock")) {

            String message = ChatColor.GREEN + "(Completed) Use " + Quester.prettyItemString(material.getId());
            message = message + " " + currentStage.blocksToUse.get(material) + "/"
                    + currentStage.blocksToUse.get(material);
            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        } else if (objective.equalsIgnoreCase("cutBlock")) {

            String message = ChatColor.GREEN + "(Completed) Cut " + Quester.prettyItemString(material.getId());
            message = message + " " + currentStage.blocksToCut.get(material) + "/"
                    + currentStage.blocksToCut.get(material);
            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        } else if (objective.equalsIgnoreCase("catchFish")) {

            String message = ChatColor.GREEN + "(Completed) Catch Fish ";
            message = message + " " + currentStage.fishToCatch + "/" + currentStage.fishToCatch;
            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        } else if (objective.equalsIgnoreCase("enchantItem")) {

            String message = ChatColor.GREEN + "(Completed) Enchant " + Quester.prettyItemString(material.getId())
                    + " with " + Quester.prettyEnchantmentString(enchantment);
            for (final Map<Enchantment, Material> map: currentStage.itemsToEnchant.keySet()) {

                if (map.containsKey(enchantment)) {

                    message = message + " " + currentStage.itemsToEnchant.get(map) + "/"
                            + currentStage.itemsToEnchant.get(map);
                    break;

                }

            }

            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        } else if (objective.equalsIgnoreCase("deliverItem")) {

            final String message = ChatColor.GREEN
                    + "(Completed) Deliver "
                    + ItemUtil
                            .getString(currentStage.itemsToDeliver.get(currentStage.itemsToDeliver.indexOf(itemstack)))
                    + " to "
                    + plugin.getNPCName(currentStage.itemDeliveryTargets.get(currentStage.itemsToDeliver
                            .indexOf(itemstack)));
            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        } else if (objective.equalsIgnoreCase("killMob")) {

            String message = ChatColor.GREEN + "(Completed) Kill " + mob.getName();
            message = message + " " + currentStage.mobNumToKill.get(currentStage.mobsToKill.indexOf(mob)) + "/"
                    + currentStage.mobNumToKill.get(currentStage.mobsToKill.indexOf(mob));
            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        } else if (objective.equalsIgnoreCase("killPlayer")) {

            String message = ChatColor.GREEN + "(Completed) Kill a Player";
            message = message + " " + currentStage.playersToKill + "/" + currentStage.playersToKill;
            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        } else if (objective.equalsIgnoreCase("talkToNPC")) {

            final String message = ChatColor.GREEN + "(Completed) Talk to " + npc.getName();
            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        } else if (objective.equalsIgnoreCase("killNPC")) {

            String message = ChatColor.GREEN + "(Completed) Kill " + npc.getName();
            message = message + " "
                    + currentStage.citizenNumToKill.get(currentStage.citizensToKill.indexOf(npc.getId())) + "/"
                    + currentStage.citizenNumToKill.get(currentStage.citizensToKill.indexOf(npc.getId()));
            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        } else if (objective.equalsIgnoreCase("tameMob")) {

            String message = ChatColor.GREEN + "(Completed) Tame " + Quester.getCapitalized(mob.getName());
            message = message + " " + currentStage.mobsToTame.get(mob) + "/" + currentStage.mobsToTame.get(mob);
            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        } else if (objective.equalsIgnoreCase("shearSheep")) {

            String message = ChatColor.GREEN + "(Completed) Shear " + color.name().toString().toLowerCase() + " sheep";
            message = message + " " + currentStage.sheepToShear.get(color) + "/" + currentStage.sheepToShear.get(color);
            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        } else if (objective.equalsIgnoreCase("reachLocation")) {

            final String message = ChatColor.GREEN + "(Completed) Go to "
                    + currentStage.locationNames.get(currentStage.locationsToReach.indexOf(location));
            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        } else if (co != null) {

            String message = ChatColor.GREEN + "(Completed) " + co.getDisplay();

            int index = -1;
            for (int i = 0; i < currentStage.customObjectives.size(); i++) {
                if (currentStage.customObjectives.get(i).getName().equals(co.getName())) {
                    index = i;
                    break;
                }
            }

            final Map<String, Object> datamap = currentStage.customObjectiveData.get(index);
            for (final String key: co.datamap.keySet()) {
                message = message.replaceAll("%" + (key) + "%", (String) datamap.get(key));
            }

            if (co.isCountShown() && co.isEnableCount()) {
                message = message.replaceAll("%count%", currentStage.customObjectiveCounts.get(index) + "/"
                        + currentStage.customObjectiveCounts.get(index));
            }
            p.sendMessage(message);
            if (testComplete()) {
                currentQuest.nextStage(this);
            }

        }

    }

    public boolean testComplete() {

        for (final String s: getObjectives()) {

            if (s.contains(ChatColor.GREEN.toString())) {
                return false;
            }

        }
        return true;

    }

    public void addEmpties() {

        if (currentStage.blocksToDamage.isEmpty() == false) {
            for (final Material m: currentStage.blocksToDamage.keySet()) {

                blocksDamaged.put(m, 0);

            }
        }

        if (currentStage.blocksToBreak.isEmpty() == false) {
            for (final Material m: currentStage.blocksToBreak.keySet()) {

                blocksBroken.put(m, 0);

            }
        }

        if (currentStage.blocksToPlace.isEmpty() == false) {
            for (final Material m: currentStage.blocksToPlace.keySet()) {

                blocksPlaced.put(m, 0);

            }
        }

        if (currentStage.blocksToUse.isEmpty() == false) {
            for (final Material m: currentStage.blocksToUse.keySet()) {

                blocksUsed.put(m, 0);

            }
        }

        if (currentStage.blocksToCut.isEmpty() == false) {
            for (final Material m: currentStage.blocksToCut.keySet()) {

                blocksCut.put(m, 0);

            }
        }

        fishCaught = 0;

        if (currentStage.itemsToEnchant.isEmpty() == false) {
            for (final Entry<Map<Enchantment, Material>, Integer> e: currentStage.itemsToEnchant.entrySet()) {

                final Map<Enchantment, Material> map = e.getKey();
                itemsEnchanted.put(map, 0);

            }
        }

        if (currentStage.mobsToKill.isEmpty() == false) {
            for (final EntityType e: currentStage.mobsToKill) {

                mobsKilled.add(e);
                mobNumKilled.add(0);
                if (currentStage.locationsToKillWithin.isEmpty() == false) {
                    locationsToKillWithin.add(currentStage.locationsToKillWithin.get(mobsKilled.indexOf(e)));
                }
                if (currentStage.radiiToKillWithin.isEmpty() == false) {
                    radiiToKillWithin.add(currentStage.radiiToKillWithin.get(mobsKilled.indexOf(e)));
                }

            }
        }

        playersKilled = 0;

        if (currentStage.itemsToDeliver.isEmpty() == false) {
            for (final ItemStack is: currentStage.itemsToDeliver) {

                itemsDelivered.put(is, 0);

            }
        }

        if (currentStage.citizensToInteract.isEmpty() == false) {
            for (final Integer n: currentStage.citizensToInteract) {

                citizensInteracted.put(n, false);

            }
        }

        if (currentStage.citizensToKill.isEmpty() == false) {
            for (final Integer n: currentStage.citizensToKill) {

                citizensKilled.add(n);
                citizenNumKilled.add(0);

            }
        }

        if (currentStage.blocksToCut.isEmpty() == false) {
            for (final Material m: currentStage.blocksToCut.keySet()) {

                blocksCut.put(m, 0);

            }
        }

        if (currentStage.locationsToReach.isEmpty() == false) {
            for (final Location l: currentStage.locationsToReach) {

                locationsReached.add(l);
                hasReached.add(false);
                radiiToReachWithin.add(currentStage.radiiToReachWithin.get(locationsReached.indexOf(l)));

            }
        }

        if (currentStage.mobsToTame.isEmpty() == false) {
            for (final EntityType e: currentStage.mobsToTame.keySet()) {

                mobsTamed.put(e, 0);

            }
        }

        if (currentStage.sheepToShear.isEmpty() == false) {
            for (final DyeColor d: currentStage.sheepToShear.keySet()) {

                sheepSheared.put(d, 0);

            }
        }

        if (currentStage.passwordDisplays.isEmpty() == false) {
            for (final String display: currentStage.passwordDisplays) {
                passwordsSaid.put(display, false);
            }
        }

        if (currentStage.customObjectives.isEmpty() == false) {
            for (final CustomObjective co: currentStage.customObjectives) {
                customObjectiveCounts.put(co.getName(), 0);
            }
        }

    }

    public void resetObjectives() {

        blocksDamaged.clear();
        blocksBroken.clear();
        blocksPlaced.clear();
        blocksUsed.clear();
        blocksCut.clear();
        fishCaught = 0;
        itemsEnchanted.clear();
        mobsKilled.clear();
        mobNumKilled.clear();
        locationsToKillWithin.clear();
        radiiToKillWithin.clear();
        playersKilled = 0;
        itemsDelivered.clear();
        citizensInteracted.clear();
        citizensKilled.clear();
        citizenNumKilled.clear();
        locationsReached.clear();
        hasReached.clear();
        radiiToReachWithin.clear();
        mobsTamed.clear();
        sheepSheared.clear();
        customObjectiveCounts.clear();
        passwordsSaid.clear();

    }

    public static String getCapitalized(String target) {
        final String firstLetter = target.substring(0, 1);
        final String remainder = target.substring(1);
        final String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();

        return capitalized;
    }

    public static String prettyItemString(int itemID) {
        final String baseString = Material.getMaterial(itemID).toString();
        final String[] substrings = baseString.split("_");
        String prettyString = "";
        int size = 1;

        for (final String s: substrings) {
            prettyString = prettyString.concat(Quester.getCapitalized(s));

            if (size < substrings.length) {
                prettyString = prettyString.concat(" ");
            }

            size++;
        }

        return prettyString;
    }

    public static String fullPotionString(short dv) {

        final Potion potion = Potion.fromDamage(dv);
        String potionName = "";
        boolean isPrimary = false;

        try {

            potionName = "Potion of " + potion.getType().getEffectType().getName();

        } catch (final NullPointerException e) { // Potion is primary

            isPrimary = true;

            if (dv == 0) {
                potionName = "Water Bottle";
            } else if (dv == 16) {
                potionName = "Awkward Potion";
            } else if (dv == 32) {
                potionName = "Thick Potion";
            } else if (dv == 64) {
                potionName = "Mundane Potion (Extended)";
            } else if (dv == 8192) {
                potionName = "Mundane Potion";
            }

        }

        if (isPrimary == false) {

            if (potion.hasExtendedDuration()) {
                potionName = potionName + " (Extended)";
            } else if (potion.getLevel() == 2) {
                potionName = potionName + " II";
            }

            if (potion.isSplash()) {
                potionName = "Splash " + potionName;
            }

        }

        return potionName;

    }

    public static String prettyMobString(EntityType type) {

        final String baseString = type.toString();
        final String[] substrings = baseString.split("_");
        String prettyString = "";
        int size = 1;

        for (final String s: substrings) {
            prettyString = prettyString.concat(Quester.getCapitalized(s));

            if (size < substrings.length) {
                prettyString = prettyString.concat(" ");
            }

            size++;
        }

        if (type.equals((EntityType.OCELOT))) {
            prettyString = "Ocelot";
        }

        return prettyString;
    }

    public static String prettyString(String s) {

        final String[] substrings = s.split("_");
        String prettyString = "";
        int size = 1;

        for (final String sb: substrings) {
            prettyString = prettyString.concat(Quester.getCapitalized(sb));

            if (size < substrings.length) {
                prettyString = prettyString.concat(" ");
            }

            size++;
        }

        return prettyString;

    }

    public static String prettyEnchantmentString(Enchantment e) {

        String prettyString = "";

        if (e.equals(Enchantment.ARROW_DAMAGE)) {
            prettyString = "Power";
        } else if (e.equals(Enchantment.ARROW_FIRE)) {
            prettyString = "Flame";
        } else if (e.equals(Enchantment.ARROW_INFINITE)) {
            prettyString = "Infinity";
        } else if (e.equals(Enchantment.ARROW_KNOCKBACK)) {
            prettyString = "Punch";
        } else if (e.equals(Enchantment.DAMAGE_ALL)) {
            prettyString = "Sharpness";
        } else if (e.equals(Enchantment.DAMAGE_ARTHROPODS)) {
            prettyString = "Bane of Arthropods";
        } else if (e.equals(Enchantment.DAMAGE_UNDEAD)) {
            prettyString = "Smite";
        } else if (e.equals(Enchantment.DIG_SPEED)) {
            prettyString = "Efficiency";
        } else if (e.equals(Enchantment.DURABILITY)) {
            prettyString = "Unbreaking";
        } else if (e.equals(Enchantment.FIRE_ASPECT)) {
            prettyString = "Fire Aspect";
        } else if (e.equals(Enchantment.KNOCKBACK)) {
            prettyString = "Knockback";
        } else if (e.equals(Enchantment.LOOT_BONUS_BLOCKS)) {
            prettyString = "Fortune";
        } else if (e.equals(Enchantment.LOOT_BONUS_MOBS)) {
            prettyString = "Looting";
        } else if (e.equals(Enchantment.OXYGEN)) {
            prettyString = "Respiration";
        } else if (e.equals(Enchantment.PROTECTION_ENVIRONMENTAL)) {
            prettyString = "Protection";
        } else if (e.equals(Enchantment.PROTECTION_EXPLOSIONS)) {
            prettyString = "Blast Protection";
        } else if (e.equals(Enchantment.PROTECTION_FALL)) {
            prettyString = "Feather Falling";
        } else if (e.equals(Enchantment.PROTECTION_FIRE)) {
            prettyString = "Fire Protection";
        } else if (e.equals(Enchantment.PROTECTION_PROJECTILE)) {
            prettyString = "Projectile Protection";
        } else if (e.equals(Enchantment.SILK_TOUCH)) {
            prettyString = "Silk Touch";
        } else if (e.equals(Enchantment.THORNS)) {
            prettyString = "Thorns";
        } else if (e.equals(Enchantment.WATER_WORKER)) {
            prettyString = "Aqua Affinity";
        }

        return prettyString;

    }

    public static String enchantmentString(Enchantment e) {

        String string = "";

        if (e.equals(Enchantment.ARROW_DAMAGE)) {
            string = "Power";
        } else if (e.equals(Enchantment.ARROW_FIRE)) {
            string = "Flame";
        } else if (e.equals(Enchantment.ARROW_INFINITE)) {
            string = "Infinity";
        } else if (e.equals(Enchantment.ARROW_KNOCKBACK)) {
            string = "Punch";
        } else if (e.equals(Enchantment.DAMAGE_ALL)) {
            string = "Sharpness";
        } else if (e.equals(Enchantment.DAMAGE_ARTHROPODS)) {
            string = "BaneOfArthropods";
        } else if (e.equals(Enchantment.DAMAGE_UNDEAD)) {
            string = "Smite";
        } else if (e.equals(Enchantment.DIG_SPEED)) {
            string = "Efficiency";
        } else if (e.equals(Enchantment.DURABILITY)) {
            string = "Unbreaking";
        } else if (e.equals(Enchantment.FIRE_ASPECT)) {
            string = "FireAspect";
        } else if (e.equals(Enchantment.KNOCKBACK)) {
            string = "Knockback";
        } else if (e.equals(Enchantment.LOOT_BONUS_BLOCKS)) {
            string = "Fortune";
        } else if (e.equals(Enchantment.LOOT_BONUS_MOBS)) {
            string = "Looting";
        } else if (e.equals(Enchantment.OXYGEN)) {
            string = "Respiration";
        } else if (e.equals(Enchantment.PROTECTION_ENVIRONMENTAL)) {
            string = "Protection";
        } else if (e.equals(Enchantment.PROTECTION_EXPLOSIONS)) {
            string = "BlastProtection";
        } else if (e.equals(Enchantment.PROTECTION_FALL)) {
            string = "FeatherFalling";
        } else if (e.equals(Enchantment.PROTECTION_FIRE)) {
            string = "FireProtection";
        } else if (e.equals(Enchantment.PROTECTION_PROJECTILE)) {
            string = "ProjectileProtection";
        } else if (e.equals(Enchantment.SILK_TOUCH)) {
            string = "SilkTouch";
        } else if (e.equals(Enchantment.THORNS)) {
            string = "Thorns";
        } else if (e.equals(Enchantment.WATER_WORKER)) {
            string = "AquaAffinity";
        }

        return string;

    }

    public static String prettyColorString(DyeColor color) {

        if (color.equals(DyeColor.BLACK)) {
            return "Black";
        } else if (color.equals(DyeColor.BLUE)) {
            return "Blue";
        } else if (color.equals(DyeColor.BROWN)) {
            return "Brown";
        } else if (color.equals(DyeColor.CYAN)) {
            return "Cyan";
        } else if (color.equals(DyeColor.GRAY)) {
            return "Gray";
        } else if (color.equals(DyeColor.GREEN)) {
            return "Green";
        } else if (color.equals(DyeColor.LIGHT_BLUE)) {
            return "LightBlue";
        } else if (color.equals(DyeColor.LIME)) {
            return "Lime";
        } else if (color.equals(DyeColor.MAGENTA)) {
            return "Magenta";
        } else if (color.equals(DyeColor.ORANGE)) {
            return "Orange";
        } else if (color.equals(DyeColor.PINK)) {
            return "Pink";
        } else if (color.equals(DyeColor.PURPLE)) {
            return "Purple";
        } else if (color.equals(DyeColor.RED)) {
            return "Red";
        } else if (color.equals(DyeColor.SILVER)) {
            return "Silver";
        } else if (color.equals(DyeColor.WHITE)) {
            return "White";
        } else {
            return "Yellow";
        }

    }

    public void saveData() {

        final FileConfiguration data = getBaseData();
        try {
            data.save(new File(plugin.getDataFolder(), "data/" + name + ".yml"));
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    public long getDifference(Quest q) {

        final long currentTime = System.currentTimeMillis();
        long lastTime;
        if (completedTimes.containsKey(q.name) == false) {
            lastTime = System.currentTimeMillis();
            completedTimes.put(q.name, System.currentTimeMillis());
        } else {
            lastTime = completedTimes.get(q.name);
        }
        final long comparator = q.redoDelay;
        final long difference = (comparator - (currentTime - lastTime));

        return difference;

    }

    public FileConfiguration getBaseData() {

        final FileConfiguration data = new YamlConfiguration();

        if (currentQuest != null) {

            data.set("currentQuest", currentQuest.name);
            data.set("currentStage", currentStageIndex);
            data.set("quest-points", questPoints);

            if (blocksDamaged.isEmpty() == false) {

                final LinkedList<Integer> blockIds = new LinkedList<Integer>();
                final LinkedList<Integer> blockAmounts = new LinkedList<Integer>();

                for (final Material m: blocksDamaged.keySet()) {
                    blockIds.add(m.getId());
                    blockAmounts.add(blocksDamaged.get(m));
                }

                data.set("blocks-damaged-ids", blockIds);
                data.set("blocks-damaged-amounts", blockAmounts);

            }

            if (blocksBroken.isEmpty() == false) {

                final LinkedList<Integer> blockIds = new LinkedList<Integer>();
                final LinkedList<Integer> blockAmounts = new LinkedList<Integer>();

                for (final Material m: blocksBroken.keySet()) {
                    blockIds.add(m.getId());
                    blockAmounts.add(blocksBroken.get(m));
                }

                data.set("blocks-broken-ids", blockIds);
                data.set("blocks-broken-amounts", blockAmounts);

            }

            if (blocksPlaced.isEmpty() == false) {

                final LinkedList<Integer> blockIds = new LinkedList<Integer>();
                final LinkedList<Integer> blockAmounts = new LinkedList<Integer>();

                for (final Material m: blocksPlaced.keySet()) {
                    blockIds.add(m.getId());
                    blockAmounts.add(blocksPlaced.get(m));
                }

                data.set("blocks-placed-ids", blockIds);
                data.set("blocks-placed-amounts", blockAmounts);

            }

            if (blocksUsed.isEmpty() == false) {

                final LinkedList<Integer> blockIds = new LinkedList<Integer>();
                final LinkedList<Integer> blockAmounts = new LinkedList<Integer>();

                for (final Material m: blocksUsed.keySet()) {
                    blockIds.add(m.getId());
                    blockAmounts.add(blocksUsed.get(m));
                }

                data.set("blocks-used-ids", blockIds);
                data.set("blocks-used-amounts", blockAmounts);

            }

            if (blocksCut.isEmpty() == false) {

                final LinkedList<Integer> blockIds = new LinkedList<Integer>();
                final LinkedList<Integer> blockAmounts = new LinkedList<Integer>();

                for (final Material m: blocksCut.keySet()) {
                    blockIds.add(m.getId());
                    blockAmounts.add(blocksCut.get(m));
                }

                data.set("blocks-cut-ids", blockIds);
                data.set("blocks-cut-amounts", blockAmounts);

            }

            if (currentStage.fishToCatch != null) {
                data.set("fish-caught", fishCaught);
            }

            if (currentStage.playersToKill != null) {
                data.set("players-killed", playersKilled);
            }

            if (itemsEnchanted.isEmpty() == false) {

                final LinkedList<String> enchantments = new LinkedList<String>();
                final LinkedList<Integer> itemIds = new LinkedList<Integer>();
                final LinkedList<Integer> enchAmounts = new LinkedList<Integer>();

                for (final Entry<Map<Enchantment, Material>, Integer> e: itemsEnchanted.entrySet()) {

                    final Map<Enchantment, Material> enchMap = e.getKey();
                    enchAmounts.add(itemsEnchanted.get(enchMap));
                    for (final Entry<Enchantment, Material> e2: enchMap.entrySet()) {

                        enchantments.add(Quester.prettyEnchantmentString(e2.getKey()));
                        itemIds.add(e2.getValue().getId());

                    }

                }

                data.set("enchantments", enchantments);
                data.set("enchantment-item-ids", itemIds);
                data.set("times-enchanted", enchAmounts);

            }

            if (mobsKilled.isEmpty() == false) {

                final LinkedList<String> mobNames = new LinkedList<String>();
                final LinkedList<Integer> mobAmounts = new LinkedList<Integer>();
                final LinkedList<String> locations = new LinkedList<String>();
                final LinkedList<Integer> radii = new LinkedList<Integer>();

                for (final EntityType e: mobsKilled) {

                    mobNames.add(Quester.prettyMobString(e));

                }

                for (final int i: mobNumKilled) {

                    mobAmounts.add(i);

                }

                data.set("mobs-killed", mobNames);
                data.set("mobs-killed-amounts", mobAmounts);

                if (locationsToKillWithin.isEmpty() == false) {

                    for (final Location l: locationsToKillWithin) {

                        locations.add(l.getWorld().getName() + " " + l.getX() + " " + l.getY() + " " + l.getZ());

                    }

                    for (final int i: radiiToKillWithin) {

                        radii.add(i);

                    }

                    data.set("mob-kill-locations", locations);
                    data.set("mob-kill-location-radii", radii);

                }

            }

            if (itemsDelivered.isEmpty() == false) {

                final LinkedList<Integer> deliveryAmounts = new LinkedList<Integer>();

                for (final Entry<ItemStack, Integer> e: itemsDelivered.entrySet()) {

                    deliveryAmounts.add(e.getValue());

                }

                data.set("item-delivery-amounts", deliveryAmounts);

            }

            if (citizensInteracted.isEmpty() == false) {

                final LinkedList<Integer> npcIds = new LinkedList<Integer>();
                final LinkedList<Boolean> hasTalked = new LinkedList<Boolean>();

                for (final Integer n: citizensInteracted.keySet()) {

                    npcIds.add(n);
                    hasTalked.add(citizensInteracted.get(n));

                }

                data.set("citizen-ids-to-talk-to", npcIds);
                data.set("has-talked-to", hasTalked);

            }

            if (citizensKilled.isEmpty() == false) {

                final LinkedList<Integer> npcIds = new LinkedList<Integer>();

                for (final Integer n: citizensKilled) {

                    npcIds.add(n);

                }

                data.set("citizen-ids-killed", npcIds);
                data.set("citizen-amounts-killed", citizenNumKilled);

            }

            if (locationsReached.isEmpty() == false) {

                final LinkedList<String> locations = new LinkedList<String>();
                final LinkedList<Boolean> has = new LinkedList<Boolean>();
                final LinkedList<Integer> radii = new LinkedList<Integer>();

                for (final Location l: locationsReached) {

                    locations.add(l.getWorld().getName() + " " + l.getX() + " " + l.getY() + " " + l.getZ());

                }

                for (final boolean b: hasReached) {
                    has.add(b);
                }

                for (final int i: radiiToReachWithin) {
                    radii.add(i);
                }

                data.set("locations-to-reach", locations);
                data.set("has-reached-location", has);
                data.set("radii-to-reach-within", radii);

            }

            if (potionsBrewed.isEmpty() == false) {

                final LinkedList<Integer> potionIds = new LinkedList<Integer>();
                final LinkedList<Integer> potionAmounts = new LinkedList<Integer>();

                for (final Entry<Integer, Integer> entry: potionsBrewed.entrySet()) {

                    potionIds.add(entry.getKey());
                    potionAmounts.add(entry.getValue());

                }

                data.set("potions-brewed-ids", potionIds);
                data.set("potions-brewed-amounts", potionAmounts);

            }

            if (mobsTamed.isEmpty() == false) {

                final LinkedList<String> mobNames = new LinkedList<String>();
                final LinkedList<Integer> mobAmounts = new LinkedList<Integer>();

                for (final EntityType e: mobsTamed.keySet()) {

                    mobNames.add(Quester.prettyMobString(e));
                    mobAmounts.add(mobsTamed.get(e));

                }

                data.set("mobs-to-tame", mobNames);
                data.set("mob-tame-amounts", mobAmounts);

            }

            if (sheepSheared.isEmpty() == false) {

                final LinkedList<String> colors = new LinkedList<String>();
                final LinkedList<Integer> shearAmounts = new LinkedList<Integer>();

                for (final DyeColor d: sheepSheared.keySet()) {

                    colors.add(Quester.prettyColorString(d));
                    shearAmounts.add(sheepSheared.get(d));

                }

                data.set("sheep-to-shear", colors);
                data.set("sheep-sheared", shearAmounts);

            }

            if (passwordsSaid.isEmpty() == false) {

                final LinkedList<String> passwords = new LinkedList<String>();
                final LinkedList<Boolean> said = new LinkedList<Boolean>();

                for (final Entry<String, Boolean> entry: passwordsSaid.entrySet()) {

                    passwords.add(entry.getKey());
                    said.add(entry.getValue());

                }

                data.set("passwords", passwords);
                data.set("passwords-said", said);

            }

            if (customObjectiveCounts.isEmpty() == false) {

                final LinkedList<String> customObj = new LinkedList<String>();
                final LinkedList<Integer> customObjCounts = new LinkedList<Integer>();

                for (final Entry<String, Integer> entry: customObjectiveCounts.entrySet()) {

                    customObj.add(entry.getKey());
                    customObjCounts.add(entry.getValue());

                }

                data.set("custom-objectives", customObj);
                data.set("custom-objective-counts", customObjCounts);

            }

            if (delayTimeLeft > 0) {
                data.set("stage-delay", delayTimeLeft);
            }

            if (eventFired.isEmpty() == false) {

                final LinkedList<String> triggers = new LinkedList<String>();
                for (final String trigger: eventFired.keySet()) {

                    if (eventFired.get(trigger) == true) {
                        triggers.add(trigger);
                    }

                }

                if (triggers.isEmpty() == false) {
                    data.set("chat-triggers", triggers);
                }

            }

        } else {

            data.set("currentQuest", "none");
            data.set("currentStage", "none");
            data.set("quest-points", questPoints);

        }

        if (completedQuests.isEmpty()) {

            data.set("completed-Quests", "none");

        } else {

            final String[] completed = new String[completedQuests.size()];
            for (final String s: completedQuests) {

                completed[completedQuests.indexOf(s)] = s;

            }
            data.set("completed-Quests", completed);

        }

        if (completedTimes.isEmpty() == false) {

            final List<String> questTimeNames = new LinkedList<String>();
            final List<Long> questTimes = new LinkedList<Long>();

            for (final String s: completedTimes.keySet()) {

                questTimeNames.add(s);
                questTimes.add(completedTimes.get(s));

            }

            data.set("completedRedoableQuests", questTimeNames);
            data.set("completedQuestTimes", questTimes);

        }

        if (amountsCompleted.isEmpty() == false) {

            final List<String> list1 = new LinkedList<String>();
            final List<Integer> list2 = new LinkedList<Integer>();

            for (final Entry<String, Integer> entry: amountsCompleted.entrySet()) {

                list1.add(entry.getKey());
                list2.add(entry.getValue());

            }

            data.set("amountsCompletedQuests", list1);
            data.set("amountsCompleted", list2);

        }

        return data;

    }

    public boolean loadData() {

        final FileConfiguration data = new YamlConfiguration();
        try {
            data.load(new File(plugin.getDataFolder(), "data/" + name + ".yml"));
        } catch (final IOException e) {
            return false;
        } catch (final InvalidConfigurationException e) {
            return false;
        }

        if (data.contains("completedRedoableQuests")) {

            for (final String s: data.getStringList("completedRedoableQuests")) {

                for (final Object o: data.getList("completedQuestTimes")) {

                    for (final Quest q: plugin.quests) {

                        if (q.name.equalsIgnoreCase(s)) {
                            completedTimes.put(q.name, (Long) o);
                            break;
                        }

                    }

                }

            }

        }

        amountsCompleted.clear();

        if (data.contains("amountsCompletedQuests")) {

            final List<String> list1 = data.getStringList("amountsCompletedQuests");
            final List<Integer> list2 = data.getIntegerList("amountsCompleted");

            for (int i = 0; i < list1.size(); i++) {

                amountsCompleted.put(list1.get(i), list2.get(i));

            }

        }

        questPoints = data.getInt("quest-points");

        if (data.isList("completed-Quests")) {

            for (final String s: data.getStringList("completed-Quests")) {

                for (final Quest q: plugin.quests) {

                    if (q.name.equalsIgnoreCase(s)) {
                        completedQuests.add(q.name);
                        break;
                    }

                }

            }

        } else {
            completedQuests.clear();
        }

        if (data.getString("currentQuest").equalsIgnoreCase("none") == false) {

            Quest quest = null;
            Stage stage = null;

            for (final Quest q: plugin.quests) {

                if (q.name.equalsIgnoreCase(data.getString("currentQuest"))) {
                    quest = q;
                    break;
                }

            }

            if (quest == null) {
                return true;
            }

            currentStageIndex = data.getInt("currentStage");

            for (final Stage s: quest.orderedStages) {

                if (quest.orderedStages.indexOf(s) == (currentStageIndex)) {
                    stage = s;
                    break;
                }

            }

            if (stage == null) {
                currentQuest = quest;
                currentQuest.completeQuest(this);
                Quests.log.log(Level.SEVERE, "[Quests] Invalid stage for player: \"" + name + "\". Quest ended.");
                return true;
            }

            currentQuest = quest;
            currentStage = stage;

            addEmpties();

            if (data.contains("blocks-damaged-ids")) {

                final List<Integer> ids = data.getIntegerList("blocks-damaged-ids");
                final List<Integer> amounts = data.getIntegerList("blocks-damaged-amounts");

                for (final int i: ids) {

                    blocksDamaged.put(Material.getMaterial(i), amounts.get(ids.indexOf(i)));

                }

            }

            if (data.contains("blocks-broken-ids")) {

                final List<Integer> ids = data.getIntegerList("blocks-broken-ids");
                final List<Integer> amounts = data.getIntegerList("blocks-broken-amounts");

                for (final int i: ids) {

                    blocksBroken.put(Material.getMaterial(i), amounts.get(ids.indexOf(i)));

                }

            }

            if (data.contains("blocks-placed-ids")) {

                final List<Integer> ids = data.getIntegerList("blocks-placed-ids");
                final List<Integer> amounts = data.getIntegerList("blocks-placed-amounts");

                for (final int i: ids) {

                    blocksPlaced.put(Material.getMaterial(i), amounts.get(ids.indexOf(i)));

                }

            }

            if (data.contains("blocks-used-ids")) {

                final List<Integer> ids = data.getIntegerList("blocks-used-ids");
                final List<Integer> amounts = data.getIntegerList("blocks-used-amounts");

                for (final int i: ids) {

                    blocksUsed.put(Material.getMaterial(i), amounts.get(ids.indexOf(i)));

                }

            }

            if (data.contains("blocks-cut-ids")) {

                final List<Integer> ids = data.getIntegerList("blocks-cut-ids");
                final List<Integer> amounts = data.getIntegerList("blocks-cut-amounts");

                for (final int i: ids) {

                    blocksCut.put(Material.getMaterial(i), amounts.get(ids.indexOf(i)));

                }

            }

            if (data.contains("fish-caught")) {
                fishCaught = data.getInt("fish-caught");
            }

            if (data.contains("players-killed")) {

                playersKilled = data.getInt("players-killed");

                final List<String> playerNames = data.getStringList("player-killed-names");
                final List<Long> killTimes = data.getLongList("kill-times");

                for (final String s: playerNames) {

                    playerKillTimes.put(s, killTimes.get(playerNames.indexOf(s)));

                }

            }

            if (data.contains("enchantments")) {

                final LinkedList<Enchantment> enchantments = new LinkedList<Enchantment>();
                final LinkedList<Material> materials = new LinkedList<Material>();
                final LinkedList<Integer> amounts = new LinkedList<Integer>();

                final List<String> enchantNames = data.getStringList("enchantments");
                final List<Integer> ids = data.getIntegerList("enchantment-item-ids");

                for (final String s: enchantNames) {

                    if (s.equalsIgnoreCase("Power")) {

                        enchantments.add(Enchantment.ARROW_DAMAGE);

                    } else if (s.equalsIgnoreCase("Flame")) {

                        enchantments.add(Enchantment.ARROW_FIRE);

                    } else if (s.equalsIgnoreCase("Infinity")) {

                        enchantments.add(Enchantment.ARROW_INFINITE);

                    } else if (s.equalsIgnoreCase("Punch")) {

                        enchantments.add(Enchantment.ARROW_KNOCKBACK);

                    } else if (s.equalsIgnoreCase("Sharpness")) {

                        enchantments.add(Enchantment.DAMAGE_ALL);

                    } else if (s.equalsIgnoreCase("BaneOfArthropods")) {

                        enchantments.add(Enchantment.DAMAGE_ARTHROPODS);

                    } else if (s.equalsIgnoreCase("Smite")) {

                        enchantments.add(Enchantment.DAMAGE_UNDEAD);

                    } else if (s.equalsIgnoreCase("Efficiency")) {

                        enchantments.add(Enchantment.DIG_SPEED);

                    } else if (s.equalsIgnoreCase("Unbreaking")) {

                        enchantments.add(Enchantment.DURABILITY);

                    } else if (s.equalsIgnoreCase("FireAspect")) {

                        enchantments.add(Enchantment.FIRE_ASPECT);

                    } else if (s.equalsIgnoreCase("Knockback")) {

                        enchantments.add(Enchantment.KNOCKBACK);

                    } else if (s.equalsIgnoreCase("Fortune")) {

                        enchantments.add(Enchantment.LOOT_BONUS_BLOCKS);

                    } else if (s.equalsIgnoreCase("Looting")) {

                        enchantments.add(Enchantment.LOOT_BONUS_MOBS);

                    } else if (s.equalsIgnoreCase("Respiration")) {

                        enchantments.add(Enchantment.OXYGEN);

                    } else if (s.equalsIgnoreCase("Protection")) {

                        enchantments.add(Enchantment.PROTECTION_ENVIRONMENTAL);

                    } else if (s.equalsIgnoreCase("BlastProtection")) {

                        enchantments.add(Enchantment.PROTECTION_EXPLOSIONS);

                    } else if (s.equalsIgnoreCase("FeatherFalling")) {

                        enchantments.add(Enchantment.PROTECTION_FALL);

                    } else if (s.equalsIgnoreCase("FireProtection")) {

                        enchantments.add(Enchantment.PROTECTION_FIRE);

                    } else if (s.equalsIgnoreCase("ProjectileProtection")) {

                        enchantments.add(Enchantment.PROTECTION_PROJECTILE);

                    } else if (s.equalsIgnoreCase("SilkTouch")) {

                        enchantments.add(Enchantment.SILK_TOUCH);

                    } else if (s.equalsIgnoreCase("AquaAffinity")) {

                        enchantments.add(Enchantment.WATER_WORKER);

                    }

                    materials.add(Material.getMaterial(ids.get(enchantNames.indexOf(s))));
                    amounts.add(enchantNames.indexOf(s));

                }

                for (final Enchantment e: enchantments) {

                    final Map<Enchantment, Material> map = new HashMap<Enchantment, Material>();
                    map.put(e, materials.get(enchantments.indexOf(e)));

                    itemsEnchanted.put(map, amounts.get(enchantments.indexOf(e)));

                }

            }

            if (data.contains("mobs-killed")) {

                final LinkedList<EntityType> mobs = new LinkedList<EntityType>();
                final List<Integer> amounts = data.getIntegerList("mobs-killed-amounts");

                for (final String s: data.getStringList("mobs-killed")) {

                    final EntityType mob = Quests.getMobType(s);
                    if (mob != null) {
                        mobs.add(mob);
                    }

                    mobsKilled.clear();
                    mobNumKilled.clear();

                    for (final EntityType e: mobs) {

                        mobsKilled.add(e);
                        mobNumKilled.add(amounts.get(mobs.indexOf(e)));

                    }

                    if (data.contains("mob-kill-locations")) {

                        final LinkedList<Location> locations = new LinkedList<Location>();
                        final List<Integer> radii = data.getIntegerList("mob-kill-location-radii");

                        for (final String loc: data.getStringList("mob-kill-locations")) {

                            final String[] info = loc.split(" ");
                            final double x = Double.parseDouble(info[1]);
                            final double y = Double.parseDouble(info[2]);
                            final double z = Double.parseDouble(info[3]);
                            final Location finalLocation = new Location(plugin.getServer().getWorld(info[0]), x, y, z);
                            locations.add(finalLocation);

                        }

                        locationsToKillWithin = locations;
                        radiiToKillWithin.clear();
                        for (final int i: radii) {
                            radiiToKillWithin.add(i);
                        }

                    }

                }

            }

            if (data.contains("item-delivery-amounts")) {

                final List<Integer> deliveryAmounts = data.getIntegerList("item-delivery-amounts");

                for (int i = 0; i < deliveryAmounts.size(); i++) {

                    itemsDelivered.put(currentStage.itemsToDeliver.get(i), deliveryAmounts.get(i));

                }

            }

            if (data.contains("citizen-ids-to-talk-to")) {

                final List<Integer> ids = data.getIntegerList("citizen-ids-to-talk-to");
                final List<Boolean> has = data.getBooleanList("has-talked-to");

                for (final int i: ids) {

                    citizensInteracted.put(i, has.get(ids.indexOf(i)));

                }

            }

            if (data.contains("citizen-ids-killed")) {

                final List<Integer> ids = data.getIntegerList("citizen-ids-killed");
                final List<Integer> num = data.getIntegerList("citizen-amounts-killed");

                citizensKilled.clear();
                citizenNumKilled.clear();

                for (final int i: ids) {

                    citizensKilled.add(i);
                    citizenNumKilled.add(num.get(ids.indexOf(i)));

                }

            }

            if (data.contains("locations-to-reach")) {

                final LinkedList<Location> locations = new LinkedList<Location>();
                final List<Boolean> has = data.getBooleanList("has-reached-location");
                final List<Integer> radii = data.getIntegerList("radii-to-reach-within");

                for (final String loc: data.getStringList("locations-to-reach")) {

                    final String[] info = loc.split(" ");
                    final double x = Double.parseDouble(info[1]);
                    final double y = Double.parseDouble(info[2]);
                    final double z = Double.parseDouble(info[3]);
                    final Location finalLocation = new Location(plugin.getServer().getWorld(info[0]), x, y, z);
                    locations.add(finalLocation);

                }

                locationsReached = locations;
                hasReached.clear();
                radiiToReachWithin.clear();

                for (final boolean b: has) {
                    hasReached.add(b);
                }

                for (final int i: radii) {
                    radiiToReachWithin.add(i);
                }

            }

            if (data.contains("potions-brewed-ids")) {

                final List<Integer> ids = data.getIntegerList("potions-brewed-ids");
                final List<Integer> amounts = data.getIntegerList("potions-brewed-amounts");

                for (final int i: ids) {

                    potionsBrewed.put(i, amounts.get(ids.indexOf(i)));

                }

            }

            if (data.contains("mobs-to-tame")) {

                final List<String> mobs = data.getStringList("mobs-to-tame");
                final List<Integer> amounts = data.getIntegerList("mob-tame-amounts");

                for (final String mob: mobs) {

                    if (mob.equalsIgnoreCase("Wolf")) {

                        mobsTamed.put(EntityType.WOLF, amounts.get(mobs.indexOf(mob)));

                    } else {

                        mobsTamed.put(EntityType.OCELOT, amounts.get(mobs.indexOf(mob)));

                    }

                }

            }

            if (data.contains("sheep-to-shear")) {

                final List<String> colors = data.getStringList("sheep-to-shear");
                final List<Integer> amounts = data.getIntegerList("sheep-sheared");

                for (final String color: colors) {

                    if (color.equalsIgnoreCase("Black")) {

                        sheepSheared.put(DyeColor.BLACK, amounts.get(colors.indexOf(color)));

                    } else if (color.equalsIgnoreCase("Blue")) {

                        sheepSheared.put(DyeColor.BLUE, amounts.get(colors.indexOf(color)));

                    }
                    if (color.equalsIgnoreCase("Brown")) {

                        sheepSheared.put(DyeColor.BROWN, amounts.get(colors.indexOf(color)));

                    }
                    if (color.equalsIgnoreCase("Cyan")) {

                        sheepSheared.put(DyeColor.CYAN, amounts.get(colors.indexOf(color)));

                    }
                    if (color.equalsIgnoreCase("Gray")) {

                        sheepSheared.put(DyeColor.GRAY, amounts.get(colors.indexOf(color)));

                    }
                    if (color.equalsIgnoreCase("Green")) {

                        sheepSheared.put(DyeColor.GREEN, amounts.get(colors.indexOf(color)));

                    }
                    if (color.equalsIgnoreCase("LightBlue")) {

                        sheepSheared.put(DyeColor.LIGHT_BLUE, amounts.get(colors.indexOf(color)));

                    }
                    if (color.equalsIgnoreCase("Lime")) {

                        sheepSheared.put(DyeColor.LIME, amounts.get(colors.indexOf(color)));

                    }
                    if (color.equalsIgnoreCase("Magenta")) {

                        sheepSheared.put(DyeColor.MAGENTA, amounts.get(colors.indexOf(color)));

                    }
                    if (color.equalsIgnoreCase("Orange")) {

                        sheepSheared.put(DyeColor.ORANGE, amounts.get(colors.indexOf(color)));

                    }
                    if (color.equalsIgnoreCase("Pink")) {

                        sheepSheared.put(DyeColor.PINK, amounts.get(colors.indexOf(color)));

                    }
                    if (color.equalsIgnoreCase("Purple")) {

                        sheepSheared.put(DyeColor.PURPLE, amounts.get(colors.indexOf(color)));

                    }
                    if (color.equalsIgnoreCase("Red")) {

                        sheepSheared.put(DyeColor.RED, amounts.get(colors.indexOf(color)));

                    }
                    if (color.equalsIgnoreCase("Silver")) {

                        sheepSheared.put(DyeColor.SILVER, amounts.get(colors.indexOf(color)));

                    }
                    if (color.equalsIgnoreCase("White")) {

                        sheepSheared.put(DyeColor.WHITE, amounts.get(colors.indexOf(color)));

                    }
                    if (color.equalsIgnoreCase("Yellow")) {

                        sheepSheared.put(DyeColor.YELLOW, amounts.get(colors.indexOf(color)));

                    }

                }

            }

            if (data.contains("passwords")) {

                final List<String> passwords = data.getStringList("passwords");
                final List<Boolean> said = data.getBooleanList("passwords-said");
                for (int i = 0; i < passwords.size(); i++) {
                    passwordsSaid.put(passwords.get(i), said.get(i));
                }

            }

            if (data.contains("custom-objectives")) {

                final List<String> customObj = data.getStringList("custom-objectives");
                final List<Integer> customObjCount = data.getIntegerList("custom-objective-counts");

                for (int i = 0; i < customObj.size(); i++) {
                    customObjectiveCounts.put(customObj.get(i), customObjCount.get(i));
                }

            }

            if (data.contains("stage-delay")) {

                delayTimeLeft = data.getLong("stage-delay");

            }

            if (currentStage.chatEvents.isEmpty() == false) {

                for (final String trig: currentStage.chatEvents.keySet()) {

                    eventFired.put(trig, false);

                }

            }

            if (data.contains("chat-triggers")) {

                final List<String> triggers = data.getStringList("chat-triggers");
                for (final String s: triggers) {

                    eventFired.put(s, true);

                }

            }

        }

        return true;

    }

    public void startStageTimer() {

        if (delayTimeLeft > -1) {
            plugin.getServer().getScheduler()
                    .scheduleSyncDelayedTask(plugin, new StageTimer(plugin, this), (long) (delayTimeLeft * 0.02));
        } else {
            plugin.getServer().getScheduler()
                    .scheduleSyncDelayedTask(plugin, new StageTimer(plugin, this), (long) (currentStage.delay * 0.02));
            if (currentStage.delayMessage != null) {
                plugin.getServer().getPlayer(name)
                        .sendMessage(Quests.parseString((currentStage.delayMessage), currentQuest));
            }
        }

        delayStartTime = System.currentTimeMillis();

    }

    public void stopStageTimer() {

        if (delayTimeLeft > -1) {
            delayTimeLeft = delayTimeLeft - (System.currentTimeMillis() - delayStartTime);
        } else {
            delayTimeLeft = currentStage.delay - (System.currentTimeMillis() - delayStartTime);
        }

        delayOver = false;

    }

    public long getStageTime() {

        if (delayTimeLeft > -1) {
            return delayTimeLeft - (System.currentTimeMillis() - delayStartTime);
        } else {
            return currentStage.delay - (System.currentTimeMillis() - delayStartTime);
        }

    }

    public boolean hasData() {

        if (currentQuest != null || currentStage != null) {
            return true;
        }

        if (questPoints > 1) {
            return true;
        }

        return completedQuests.isEmpty() == false;

    }

    public void checkQuest() {

        if (currentQuest != null) {

            boolean exists = false;

            for (final Quest q: plugin.quests) {

                if (q.name.equalsIgnoreCase(currentQuest.name)) {

                    exists = true;
                    if (q.equals(currentQuest) == false) {

                        currentStage = null;
                        currentStageIndex = 0;
                        resetObjectives();
                        if (plugin.getServer().getPlayer(name) != null) {
                            plugin.getServer()
                                    .getPlayer(name)
                                    .sendMessage(
                                            ChatColor.GOLD + "[Quests] " + ChatColor.RED + "Your active Quest "
                                                    + ChatColor.DARK_PURPLE + currentQuest.name + ChatColor.RED
                                                    + " has been modified. You have been forced to quit the Quest.");
                        }
                        currentQuest = null;

                    }

                    break;

                }

            }

            if (exists == false) {

                currentStage = null;
                currentStageIndex = 0;
                resetObjectives();
                if (plugin.getServer().getPlayer(name) != null) {
                    plugin.getServer()
                            .getPlayer(name)
                            .sendMessage(
                                    ChatColor.GOLD + "[Quests] " + ChatColor.RED + "Your active Quest "
                                            + ChatColor.DARK_PURPLE + currentQuest.name + ChatColor.RED
                                            + " no longer exists. You have been forced to quit the Quest.");
                }
                currentQuest = null;

            }

        }

    }

    public static String checkPlacement(Inventory inv, int rawSlot) {

        if (rawSlot < 0) {
            return "You may not drop Quest items.";
        }

        final InventoryType type = inv.getType();

        if (type.equals(InventoryType.BREWING)) {

            if (rawSlot < 4) {
                return "You may not brew using Quest items.";
            }

        } else if (type.equals(InventoryType.CHEST)) {

            if (inv.getContents().length == 27) {
                if (rawSlot < 27) {
                    return "You may not store Quest items.";
                }

            } else {
                if (rawSlot < 54) {
                    return "You may not store Quest items.";
                }

            }

        } else if (type.equals(InventoryType.CRAFTING)) {

            if (rawSlot < 5) {
                return "You may not craft using Quest items.";
            } else if (rawSlot < 9) {
                return "You may not equip Quest items.";
            }

        } else if (type.equals(InventoryType.DISPENSER)) {

            if (rawSlot < 9) {
                return "You may not put Quest items in dispensers.";
            }

        } else if (type.equals(InventoryType.ENCHANTING)) {

            if (rawSlot == 0) {
                return "You may not enchant Quest items.";
            }

        } else if (type.equals(InventoryType.ENDER_CHEST)) {

            if (rawSlot < 27) {
                return "You may not store Quest items.";
            }

        } else if (type.equals(InventoryType.FURNACE)) {

            if (rawSlot < 3) {
                return "You may not smelt using Quest items.";
            }

        } else if (type.equals(InventoryType.WORKBENCH)) {

            if (rawSlot < 10) {
                return "You may not craft using Quest items.";
            }

        }
        return null;

    }

    public static List<Integer> getChangedSlots(Inventory inInv, ItemStack inNew) {
        final List<Integer> changed = new ArrayList<Integer>();
        if (inInv.contains(inNew.getType())) {
            int amount = inNew.getAmount();
            final HashMap<Integer, ? extends ItemStack> items = inInv.all(inNew.getType());
            for (int i = 0; i < inInv.getSize(); i++) {
                if (!items.containsKey(i)) {
                    continue;
                }

                final ItemStack item = items.get(i);
                final int slotamount = item.getMaxStackSize() - item.getAmount();
                if (slotamount > 1) {
                    if (amount > slotamount) {
                        final int toAdd = slotamount - amount;
                        amount = amount - toAdd;
                        changed.add(i);
                    } else {
                        changed.add(i);
                        amount = 0;
                        break;
                    }
                }
            }

            if (amount > 0) {
                if (inInv.firstEmpty() != -1) {
                    changed.add(inInv.firstEmpty());
                }
            }
        } else {
            if (inInv.firstEmpty() != -1) {
                changed.add(inInv.firstEmpty());
            }
        }
        return changed;
    }

    public void showGUIDisplay(LinkedList<Quest> quests) {

        final Player player = getPlayer();
        final int size = ((quests.size() / 9) + 1) * 9;

        final Inventory inv = Bukkit.getServer().createInventory(player, size, "Quests");

        int inc = 0;
        for (int i = 0; i < quests.size(); i++) {

            if (quests.get(i).guiDisplay != null) {
                inv.setItem(inc, quests.get(i).guiDisplay);
                inc++;
            }

        }

        player.openInventory(inv);

    }

}
