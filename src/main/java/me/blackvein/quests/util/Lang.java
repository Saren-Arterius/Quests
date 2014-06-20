package me.blackvein.quests.util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.blackvein.quests.Quests;

import org.bukkit.configuration.file.YamlConfiguration;

public class Lang {

    public static String               lang = "en";

    private static Map<String, String> en   = new LinkedHashMap<String, String>();

    private final Quests               plugin;

    public Lang(Quests plugin) {
        this.plugin = plugin;
    }

    public static String get(String key) {
        return Lang.en.get(key);
    }

    public void initPhrases() {

        // English
        // TODO: If finished, completely check everything.
        // Quests

        Lang.en.put("questFailed", "*QUEST FAILED*");

        // Quest create menu
        Lang.en.put("questEditorHeader", "Create Quest");
        Lang.en.put("questEditorCreate", "Create new Quest");
        Lang.en.put("questEditorEdit", "Edit a Quest");
        Lang.en.put("questEditorDelete", "Delete Quest");
        Lang.en.put("questEditorName", "Set name");

        Lang.en.put("questEditorAskMessage", "Set ask message");
        Lang.en.put("questEditorFinishMessage", "Set finish message");
        Lang.en.put("questEditorRedoDelay", "Set redo delay");
        Lang.en.put("questEditorNPCStart", "Set NPC start");
        Lang.en.put("questEditorBlockStart", "Set Block start");
        Lang.en.put("questEditorInitialEvent", "Set initial Event");
        Lang.en.put("questEditorReqs", "Edit Requirements");
        Lang.en.put("questEditorStages", "Edit Stages");
        Lang.en.put("questEditorRews", "Edit Rewards");

        Lang.en.put("questEditorEnterQuestName", "Enter Quest name (or \"cancel\" to return)");
        Lang.en.put("questEditorEnterAskMessage", "Enter ask message (or \"cancel\" to return)");
        Lang.en.put("questEditorEnterFinishMessage", "Enter finish message (or \"cancel\" to return)");
        Lang.en.put("questEditorEnterRedoDelay",
                "Enter amount of time (in milliseconds), 0 to clear the redo delay or -1 to cancel ");
        Lang.en.put("questEditorEnterNPCStart", "Enter NPC ID, -1 to clear the NPC start or -2 to cancel");
        Lang.en.put("questEditorEnterBlockStart",
                "Right-click on a block to use as a start point, then enter \"done\" to save,\n"
                        + "or enter \"clear\" to clear the block start, or \"cancel\" to return");
        Lang.en.put("questEditorEnterInitialEvent",
                "Enter an Event name, or enter \"clear\" to clear the initial Event, or \"cancel\" to return");

        // Quest create menu errors
        Lang.en.put("questEditorNameExists", "A Quest with that name already exists!");
        Lang.en.put("questEditorBeingEdited", "Someone is creating/editing a Quest with that name!");
        Lang.en.put("questEditorInvalidQuestName", "Name may not contain commas!");
        Lang.en.put("questEditorInvalidEventName", "is not a valid event name!");
        Lang.en.put("questEditorInvalidNPC", "No NPC exists with that id!");
        Lang.en.put("questEditorNoStartBlockSelected", "You must select a block first.");
        Lang.en.put("questEditorPositiveAmount", "Amount must be a positive number.");
        Lang.en.put("questEditorQuestAsRequirement1", "The following Quests have");
        Lang.en.put("questEditorQuestAsRequirement2", "as a requirement:");
        Lang.en.put("questEditorQuestAsRequirement3",
                "You must modify these Quests so that they do not use it before deleting it.");
        Lang.en.put("questEditorQuestNotFound", "Quest not found!");

        Lang.en.put("questEditorEventCleared", "Initial Event cleared.");
        Lang.en.put("questEditorSave", "Finish and save");

        Lang.en.put("questEditorNeedAskMessage", "You must set an ask message!");
        Lang.en.put("questEditorNeedFinishMessage", "You must set a finish message!");
        Lang.en.put("questEditorNeedStages", "Your Quest has no Stages!");
        Lang.en.put("questEditorSaved", "Quest saved! (You will need to perform a Quest reload for it to appear)");
        Lang.en.put("questEditorExited", "Are you sure you want to exit without saving?");
        Lang.en.put("questEditorDeleted", "Are you sure you want to delete the Quest");

        Lang.en.put("questEditorNoPermsCreate", "You do not have permission to create Quests.");
        Lang.en.put("questEditorNoPermsEdit", "You do not have permission to edit Quests.");
        Lang.en.put("questEditorNoPermsDelete", "You do not have permission to delete Quests.");
        //

        // Stages
        // Menu
        Lang.en.put("stageEditorEditStage", "Edit Stage");
        Lang.en.put("stageEditorNewStage", "Add new Stage");
        // create prompt
        Lang.en.put("stageEditorStages", "Stages");
        Lang.en.put("stageEditorStage", "Stage");
        Lang.en.put("stageEditorBreakBlocks", "Break Blocks");
        Lang.en.put("stageEditorDamageBlocks", "Damage Blocks");
        Lang.en.put("stageEditorPlaceBlocks", "Place Blocks");
        Lang.en.put("stageEditorUseBlocks", "Use Blocks");
        Lang.en.put("stageEditorCutBlocks", "Cut Blocks");
        Lang.en.put("stageEditorCatchFish", "Catch Fish");
        Lang.en.put("stageEditorFish", "fish");
        Lang.en.put("stageEditorKillPlayers", "Kill Players");
        Lang.en.put("stageEditorPlayers", "players");
        Lang.en.put("stageEditorEnchantItems", "Enchant Items");
        Lang.en.put("stageEditorDeliverItems", "Deliver Items");
        Lang.en.put("stageEditorTalkToNPCs", "Talk to NPCs");
        Lang.en.put("stageEditorKillNPCs", "Kill NPCs");
        Lang.en.put("stageEditorKillMobs", "Kill Mobs");
        Lang.en.put("stageEditorReachLocs", "Reach locations");
        Lang.en.put("stageEditorReachRadii1", "Reach within");
        Lang.en.put("stageEditorReachRadii2", "blocks of");
        Lang.en.put("stageEditorTameMobs", "Tame Mobs");
        Lang.en.put("stageEditorShearSheep", "Shear Sheep");
        Lang.en.put("stageEditorEvents", "Events");
        Lang.en.put("stageEditorStageEvents", "Stage Events");
        Lang.en.put("stageEditorStartEvent", "Start Event");
        Lang.en.put("stageEditorStartEventCleared", "Start Event cleared.");
        Lang.en.put("stageEditorFinishEvent", "Finish Event");
        Lang.en.put("stageEditorFinishEventCleared", "Finish Event cleared.");
        Lang.en.put("stageEditorChatEvents", "Chat Events");
        Lang.en.put("stageEditorChatTrigger", "Chat Trigger");
        Lang.en.put("stageEditorTriggeredBy", "Triggered by");
        Lang.en.put("stageEditorChatEventsCleared", "Chat Events cleared.");
        Lang.en.put("stageEditorDeathEvent", "Death Event");
        Lang.en.put("stageEditorDeathEventCleared", "Death Event cleared.");
        Lang.en.put("stageEditorDisconnectEvent", "Disconnect Event");
        Lang.en.put("stageEditorDisconnectEventCleared", "Disconnect Event cleared.");
        Lang.en.put("stageEditorDelayMessage", "Delay Message");
        Lang.en.put("stageEditorDenizenScript", "Denizen Script");
        Lang.en.put("stageEditorStartMessage", "Start Message");
        Lang.en.put("stageEditorCompleteMessage", "Complete Message");
        Lang.en.put("stageEditorDelete", "Delete Stage");

        Lang.en.put("stageEditorDamageBlocks", "Damage Blocks");
        Lang.en.put("stageEditorPlaceBlocks", "Place Blocks");
        Lang.en.put("stageEditorSetBlockIds", "Set block IDs");
        Lang.en.put("stageEditorSetBlockAmounts", "Set block amounts");
        Lang.en.put("stageEditorSetDamageAmounts", "Set damage amounts");
        Lang.en.put("stageEditorSetPlaceAmounts", "Set place amounts");
        Lang.en.put("stageEditorSetUseAmounts", "Set use amounts");
        Lang.en.put("stageEditorSetCutAmounts", "Set cut amounts");
        Lang.en.put("stageEditorSetKillAmounts", "Set kill amounts");
        Lang.en.put("stageEditorSetEnchantAmounts", "Set enchant amounts");
        Lang.en.put("stageEditorSetMobAmounts", "Set mob amounts");
        Lang.en.put("stageEditorSetEnchantments", "Set enchantments");
        Lang.en.put("stageEditorSetItemIds", "Set item ids");
        Lang.en.put("stageEditorSetKillIds", "Set NPC IDs");
        Lang.en.put("stageEditorSetMobTypes", "Set mob types");
        Lang.en.put("stageEditorSetKillLocations", "Set kill locations");
        Lang.en.put("stageEditorSetKillLocationRadii", "Set kill location radii");
        Lang.en.put("stageEditorSetKillLocationNames", "Set kill location names");
        Lang.en.put("stageEditorSetLocations", "Set locations");
        Lang.en.put("stageEditorSetLocationRadii", "Set location radii");
        Lang.en.put("stageEditorSetLocationNames", "Set location names");
        Lang.en.put("stageEditorSetTameAmounts", "Set tame amounts");
        Lang.en.put("stageEditorSetShearColors", "Set sheep colors");
        Lang.en.put("stageEditorSetShearAmounts", "Set shear amounts");

        Lang.en.put("stageEditorEnterBlockIds",
                "Enter block IDs, separating each one by a space, or enter \'cancel\' to return.");
        Lang.en.put("stageEditorBreakBlocksPrompt",
                "Enter block amounts (numbers), separating each one by a space, or enter \'cancel\' to return.");
        Lang.en.put("stageEditorDamageBlocksPrompt",
                "Enter damage amounts (numbers), separating each one by a space, or enter \'cancel\' to return.");
        Lang.en.put("stageEditorPlaceBlocksPrompt",
                "Enter place amounts (numbers), separating each one by a space, or enter \'cancel\' to return.");
        Lang.en.put("stageEditorUseBlocksPrompt",
                "Enter use amounts (numbers), separating each one by a space, or enter \'cancel\' to return.");
        Lang.en.put("stageEditorCutBlocksPrompt",
                "Enter cut amounts (numbers), separating each one by a space, or enter \'cancel\' to return.");
        Lang.en.put("stageEditorCatchFishPrompt",
                "Enter number of fish to catch, or 0 to clear the fish catch objective, or -1 to cancel");
        Lang.en.put("stageEditorKillPlayerPrompt",
                "Enter number of players to kill, or 0 to clear the player kill objective, or -1 to cancel");
        Lang.en.put("stageEditorEnchantTypePrompt",
                "Enter enchantment names, separating each one by a \"comma\", or enter \'cancel\' to return.");
        Lang.en.put("stageEditorEnchantAmountsPrompt",
                "Enter enchant amounts (numbers), separating each one by a space, or enter \'cancel\' to return.");
        Lang.en.put("stageEditorItemIDsPrompt",
                "Enter item IDs, separating each one by a space, or enter \'cancel\' to return.");
        Lang.en.put("stageEditorNPCPrompt",
                "Enter NPC ids, separating each one by a space, or enter \'cancel\' to return.");
        Lang.en.put("stageEditorNPCToTalkToPrompt",
                "Enter NPC IDs, separating each one by a space, or enter \'clear\' to clear the NPC ID list, or \'cancel\' to return.");
        Lang.en.put("stageEditorDeliveryMessagesPrompt",
                "Enter delivery messages, separating each one by a semi-colon or enter \'cancel\' to return");
        Lang.en.put("stageEditorKillNPCsPrompt",
                "Enter kill amounts (numbers), separating each one by a space, or enter \'cancel\' to return.");
        Lang.en.put("stageEditorMobsPrompt",
                "Enter mob names separating each one by a space, or enter \"cancel\" to return");
        Lang.en.put("stageEditorMobAmountsPrompt",
                "Enter mob amounts separating each one by a space, or enter \"cancel\" to return");
        Lang.en.put(
                "stageEditorMobLocationPrompt",
                "Right-click on a block to select it, then enter \"add\" to add it to the kill location list, or enter \"cancel\" to return");
        Lang.en.put("stageEditorMobLocationRadiiPrompt",
                "Enter kill location radii (number of blocks) separating each one by a space, or enter \"cancel\" to return");
        Lang.en.put("stageEditorMobLocationNamesPrompt",
                "Enter location names separating each one by a \"comma\", or enter \"cancel\" to return");
        Lang.en.put(
                "stageEditorReachLocationPrompt",
                "Right-click on a block to select it, then enter \"add\" to add it to the reach location list, or enter \"cancel\" to return");
        Lang.en.put("stageEditorReachLocationRadiiPrompt",
                "Enter reach location radii (number of blocks) separating each one by a space, or enter \"cancel\" to return");
        Lang.en.put("stageEditorReachLocationNamesPrompt",
                "Enter location names separating each one by a \"comma\", or enter \"cancel\" to return");
        Lang.en.put("stageEditorTameAmountsPrompt",
                "Enter tame amounts separating each one by a space, or enter \"cancel\" to return");
        Lang.en.put("stageEditorShearColorsPrompt",
                "Enter sheep colors separating each one by a space, or enter \"cancel\" to return");
        Lang.en.put("stageEditorShearAmountsPrompt",
                "Enter shear amounts separating each one by a space, or enter \"cancel\" to return");
        Lang.en.put("stageEditorEventsPrompt",
                "Enter an event name, or enter \"clear\" to clear the event, or \"cancel\" to return");
        Lang.en.put("stageEditorChatEventsPrompt",
                "Enter an event name to add, or enter \"clear\" to clear all chat events, or \"cancel\" to return");
        Lang.en.put("stageEditorChatEventsTriggerPromptA", "Enter a chat trigger for");
        Lang.en.put("stageEditorChatEventsTriggerPromptB", "or enter \"cancel\" to return.");
        Lang.en.put("stageEditorDelayPrompt",
                "Enter time (in milliseconds), or enter \"clear\" to clear the delay, or \"cancel\" to return");
        Lang.en.put("stageEditorDelayMessagePrompt",
                "Enter delay message, or enter \"clear\" to clear the message, or \"cancel\" to return");
        Lang.en.put("stageEditorScriptPrompt",
                "Enter script name, or enter \"clear\" to clear the script, or \"cancel\" to return");
        Lang.en.put("stageEditorStartMessagePrompt",
                "Enter start message, or enter \"clear\" to clear the message, or \"cancel\" to return");
        Lang.en.put("stageEditorCompleteMessagePrompt",
                "Enter complete message, or enter \"clear\" to clear the message, or \"cancel\" to return");

        Lang.en.put("stageEditorDeliveryAddItem", "Add item");
        Lang.en.put("stageEditorDeliveryNPCs", "Set NPC IDs");
        Lang.en.put("stageEditorDeliveryMessages", "Set delivery messages");

        Lang.en.put("stageEditorContainsDuplicates", "List contains duplicates!");
        Lang.en.put("stageEditorInvalidBlockID", "is not a valid block ID!");
        Lang.en.put("stageEditorInvalidEnchantment", "is not a valid enchantment name!");
        Lang.en.put("stageEditorInvalidNPC", "is not a valid NPC ID!");
        Lang.en.put("stageEditorInvalidMob", "is not a valid mob name!");
        Lang.en.put("stageEditorInvalidItemID", "is not a valid item ID!");
        Lang.en.put("stageEditorInvalidNumber", "is not a number!");
        Lang.en.put("stageEditorInvalidDye", "is not a valid dye color!");
        Lang.en.put("stageEditorInvalidEvent", "is not a valid event name!");
        Lang.en.put("stageEditorDuplicateEvent", "Event is already in the list!");
        Lang.en.put("stageEditorInvalidDelay", "Delay must be at least one second!");
        Lang.en.put("stageEditorInvalidScript", "Denizen script not found!");

        Lang.en.put("stageEditorNoCitizens", "Citizens is not installed!");
        Lang.en.put("stageEditorNoDenizen", "Denizen is not installed!");

        Lang.en.put("stageEditorPositiveAmount", "You must enter a positive number!");
        Lang.en.put("stageEditorNoNumber", "Input was not a number!");
        Lang.en.put("stageEditorNotGreaterThanZero", "is not greater than 0!");
        Lang.en.put("stageEditorNotListofNumbers", "Invalid entry, input was not a list of numbers!");
        Lang.en.put("stageEditorNoDelaySet", "You must set a delay first!");
        Lang.en.put("stageEditorNoBlockIds", "You must set Block IDs first!");
        Lang.en.put("stageEditorNoEnchantments", "You must set enchantments first!");
        Lang.en.put("stageEditorNoItems", "You must add items first!");
        Lang.en.put("stageEditorNoDeliveryMessage", "You must set at least one delivery message!");
        Lang.en.put("stageEditorNoNPCs", "You must set NPC IDs first!");
        Lang.en.put("stageEditorNoMobTypes", "You must set mob types first!");
        Lang.en.put("stageEditorNoKillLocations", "You must set kill locations first!");
        Lang.en.put("stageEditorNoBlockSelected", "You must select a block first.");
        Lang.en.put("stageEditorNoColors", "You must set colors first!");

        Lang.en.put("stageEditorNoEnchantmentsSet", "No enchantments set");
        Lang.en.put("stageEditorNoItemsSet", "No items set");
        Lang.en.put("stageEditorNoMobTypesSet", "No mob types set");
        Lang.en.put("stageEditorNoLocationsSet", "No locations set");
        Lang.en.put("stageEditorNoColorsSet", "No colors set");

        Lang.en.put("stageEditorListNotSameSize", "The block IDs list and the amounts list are not the same size!");
        Lang.en.put("stageEditorEnchantmentNotSameSize",
                "The enchantments list, the item id list and the enchant amount list are not the same size!");
        Lang.en.put("stageEditorDeliveriesNotSameSize", "The item list and the NPC list are not equal in size!");
        Lang.en.put("stageEditorNPCKillsNotSameSize",
                "The NPC IDs list and the kill amounts list are not the same size!");
        Lang.en.put("stageEditorAllListsNotSameSize", "All of your lists are not the same size!");
        Lang.en.put("stageEditorMobTypesNotSameSize",
                "The mob types list and the mob amounts list are not the same size!");
        Lang.en.put("stageEditorTameMobsNotSameSize",
                "The mob types list and the tame amounts list are not the same size!");
        Lang.en.put("stageEditorShearNotSameSize",
                "The sheep colors list and the shear amounts list are not the same size!");

        Lang.en.put("stageEditorListContainsDuplicates", " List contains duplicates!");

        Lang.en.put("stageEditorBreakBlocksCleared", "Break blocks objective cleared.");
        Lang.en.put("stageEditorDamageBlocksCleared", "Damage blocks objective cleared.");
        Lang.en.put("stageEditorPlaceBlocksCleared", "Place blocks objective cleared.");
        Lang.en.put("stageEditorUseBlocksCleared", "Use blocks objective cleared.");
        Lang.en.put("stageEditorCutBlocksCleared", "Cut blocks objective cleared.");
        Lang.en.put("stageEditorEnchantmentsCleared", "Enchantment objective cleared.");
        Lang.en.put("stageEditorDeliveriesCleared", "Delivery objective cleared.");
        Lang.en.put("stageEditorKillNPCsCleared", "Kill NPCs objective cleared.");
        Lang.en.put("stageEditorKillMobsCleared", "Kill Mobs objective cleared.");
        Lang.en.put("stageEditorTameCleared", "Tame Mobs objective cleared.");
        Lang.en.put("stageEditorShearCleared", "Shear Sheep objective cleared.");
        Lang.en.put("stageEditorStartMessageCleared", "Start message cleared.");
        Lang.en.put("stageEditorCompleteMessageCleared", "Complete message cleared.");

        Lang.en.put("stageEditorConfirmStageDelete", "Are you sure you want to delete this stage?");
        Lang.en.put("stageEditorConfirmStageNote", "Any Stages after will be shifted back one spot");
        Lang.en.put("stageEditorDeleteSucces", "Stage deleted successfully.");

        Lang.en.put("stageEditorEnchantments", "Enchantments");
        Lang.en.put("stageEditorNPCNote", "Note: You may specify the name of the NPC with <npc>");
        Lang.en.put("stageEditorOptional", "Optional");
        Lang.en.put("stageEditorColors", "Sheep Colors");

        // Events
        Lang.en.put("eventEditorTitle", "Event Editor");
        Lang.en.put("eventEditorCreate", "Create new Event");
        Lang.en.put("eventEditorEdit", "Edit an Event");
        Lang.en.put("eventEditorDelete", "Delete an Event");

        Lang.en.put("eventEditorNoneToEdit", "No Events currently exist to be edited!");
        Lang.en.put("eventEditorNoneToDelete", "No Events currently exist to be deleted!");
        Lang.en.put("eventEditorNotFound", "Event not found!");
        Lang.en.put("eventEditorExists", "Event already exists!");
        Lang.en.put("eventEditorSomeone", "Someone is already creating or editing an Event with that name!");
        Lang.en.put("eventEditorAlpha", "Name must be alphanumeric!");

        Lang.en.put("eventEditorErrorReadingFile", "Error reading Events file.");
        Lang.en.put("eventEditorErrorSaving", "An error occurred while saving.");
        Lang.en.put("eventEditorDeleted", "Event deleted, Quests and Events reloaded.");
        Lang.en.put("eventEditorSaved", "Event saved, Quests and Events reloaded.");

        Lang.en.put("eventEditorEnterEventName", "Enter an Event name, or \"cancel\" to return.");
        Lang.en.put("eventEditorDeletePrompt", "Are you sure you want to delete the Event");
        Lang.en.put("eventEditorQuitWithoutSaving", "Are you sure you want to quit without saving?");
        Lang.en.put("eventEditorFinishAndSave", "Are you sure you want to finish and save the Event");
        Lang.en.put("eventEditorModifiedNote", "Note: You have modified an Event that the following Quests use:");
        Lang.en.put("eventEditorForcedToQuit",
                "If you save the Event, anyone who is actively doing any of these Quests will be forced to quit them.");

        Lang.en.put("eventEditorEventInUse", "The following Quests use the Event");
        Lang.en.put("eventEditorMustModifyQuests", "eventEditorNotFound");
        Lang.en.put("eventEditorListSizeMismatch", "The lists are not the same size!");
        Lang.en.put("eventEditorListDuplicates", "List contains duplicates!");
        Lang.en.put("eventEditorNotANumberList", "Input was not a list of numbers!");
        Lang.en.put("eventEditorInvalidEntry", "Invalid entry");

        Lang.en.put("eventEditorSetName", "Set name");
        Lang.en.put("eventEditorSetMessage", "Set message");

        Lang.en.put("eventEditorClearInv", "Clear player inventory");
        Lang.en.put("eventEditorFailQuest", "Fail the quest");
        Lang.en.put("eventEditorSetExplosions", "Set explosion locations");
        Lang.en.put("eventEditorSetLightning", "Set lightning strike locations");
        Lang.en.put("eventEditorSetEffects", "Set effects");
        Lang.en.put("eventEditorSetStorm", "Set storm");
        Lang.en.put("eventEditorSetThunder", "Set thunder");
        Lang.en.put("eventEditorSetMobSpawns", "Set mob spawns");
        Lang.en.put("eventEditorSetPotionEffects", "Set potion effects");
        Lang.en.put("eventEditorSetHunger", "Set player hunger level");
        Lang.en.put("eventEditorSetSaturation", "Set player saturation level");
        Lang.en.put("eventEditorSetHealth", "Set player health level");
        Lang.en.put("eventEditorSetTeleport", "Set player teleport location");
        Lang.en.put("eventEditorSetCommands", "Set commands to execute");

        Lang.en.put("eventEditorItems", "Event Items");
        Lang.en.put("eventEditorSetItems", "Give items");
        Lang.en.put("eventEditorItemsCleared", "Event items cleared.");
        Lang.en.put("eventEditorSetItemIDs", "Set item IDs");
        Lang.en.put("eventEditorSetItemAmounts", "Set item amounts");
        Lang.en.put("eventEditorNoIDs", "No IDs set");
        Lang.en.put("eventEditorMustSetIDs", "You must set item IDs first!");
        Lang.en.put("eventEditorInvalidID", "___ is not a valid item ID!");
        Lang.en.put("eventEditorNotGreaterThanZero", "___ is not greater than 0!");
        Lang.en.put("eventEditorNotANumber", "___ is not a number!");

        Lang.en.put("eventEditorStorm", "Event Storm");
        Lang.en.put("eventEditorSetWorld", "Set world");
        Lang.en.put("eventEditorSetDuration", "Set duration");
        Lang.en.put("eventEditorNoWorld", "(No world set)");
        Lang.en.put("eventEditorSetWorldFirst", "You must set a world first!");
        Lang.en.put("eventEditorInvalidWorld", "___ is not a valid world name!");
        Lang.en.put("eventEditorMustSetStormDuration", "You must set a storm duration!");
        Lang.en.put("eventEditorStormCleared", "Storm data cleared.");
        Lang.en.put("eventEditorEnterStormWorld",
                "Enter a world name for the storm to occur in, or enter \"cancel\" to return");
        Lang.en.put("eventEditorEnterDuration", "Enter duration (in milliseconds)");
        Lang.en.put("eventEditorAtLeastOneSecond", "Amount must be at least 1 second! (1000 milliseconds)");
        Lang.en.put("eventEditorNotGreaterThanOneSecond", "___ is not greater than 1 second! (1000 milliseconds)");

        Lang.en.put("eventEditorThunder", "Event Thunder");
        Lang.en.put("eventEditorInvalidWorld", "___ is not a valid world name!");
        Lang.en.put("eventEditorMustSetThunderDuration", "You must set a thunder duration!");
        Lang.en.put("eventEditorThunderCleared", "Thunder data cleared.");
        Lang.en.put("eventEditorEnterThunderWorld",
                "Enter a world name for the thunder to occur in, or enter \"cancel\" to return");

        Lang.en.put("eventEditorEffects", "Event Effects");
        Lang.en.put("eventEditorAddEffect", "Add effect");
        Lang.en.put("eventEditorAddEffectLocation", "Add effect location");
        Lang.en.put("eventEditorNoEffects", "No effects set");
        Lang.en.put("eventEditorMustAddEffects", "You must add effects first!");
        Lang.en.put("eventEditorInvalidEffect", "___ is not a valid effect name!");
        Lang.en.put("eventEditorEffectsCleared", "Event effects cleared.");
        Lang.en.put("eventEditorEffectLocationPrompt",
                "Right-click on a block to play an effect at, then enter \"add\" to add it to the list, or enter \"cancel\" to return");

        Lang.en.put("eventEditorMobSpawns", "Event Mob Spawns");
        Lang.en.put("eventEditorAddMobTypes", "Add mob");
        Lang.en.put("eventEditorNoTypesSet", "(No type set)");
        Lang.en.put("eventEditorMustSetMobTypesFirst", "You must set the mob type first!");
        Lang.en.put("eventEditorSetMobAmounts", "Set mob amount");
        Lang.en.put("eventEditorNoAmountsSet", "(No amounts set)");
        Lang.en.put("eventEditorMustSetMobAmountsFirst", "You must set mob amount first!");
        Lang.en.put("eventEditorAddSpawnLocation", "Set spawn location");
        Lang.en.put("eventEditorMobSpawnsCleared", "Mob spawns cleared.");
        Lang.en.put("eventEditorMustSetMobLocationFirst", "You must set a spawn-location first!");
        Lang.en.put("eventEditorInvalidMob", "___ is not a valid mob name!");
        Lang.en.put("eventEditorSetMobName", "Set custom name for mob");
        Lang.en.put("eventEditorSetMobType", "Set mob type");
        Lang.en.put("eventEditorSetMobItemInHand", "Set item in hand");
        Lang.en.put("eventEditorSetMobItemInHandDrop", "Set drop chance of item in hand");
        Lang.en.put("eventEditorSetMobBoots", "Set boots");
        Lang.en.put("eventEditorSetMobBootsDrop", "Set drop chance of boots");
        Lang.en.put("eventEditorSetMobLeggings", "Set leggings");
        Lang.en.put("eventEditorSetMobLeggingsDrop", "Set drop chance of leggings");
        Lang.en.put("eventEditorSetMobChestPlate", "Set chest plate");
        Lang.en.put("eventEditorSetMobChestPlateDrop", "Set drop chance of chest plate");
        Lang.en.put("eventEditorSetMobHelmet", "Set helmet");
        Lang.en.put("eventEditorSetMobHelmetDrop", "Set drop chance of helmet");
        Lang.en.put("eventEditorSetMobSpawnLoc",
                "Right-click on a block to spawn a mob at, then enter \"add\" to the confirm it, or enter \"cancel\" to return");
        Lang.en.put("eventEditorSetMobSpawnAmount", "Set the amount of mobs to spawn");
        Lang.en.put("eventEditorSetDropChance", "Set the drop chance");
        Lang.en.put("eventEditorInvalidDropChance", "Drop chance has to be between 0.0 and 1.0");

        Lang.en.put(
                "eventEditorLightningPrompt",
                "Right-click on a block to spawn a lightning strike at, then enter \"add\" to add it to the list, or enter \"clear\" to clear the locations list, or \"cancel\" to return");

        Lang.en.put("eventEditorPotionEffects", "Event Potion Effects");
        Lang.en.put("eventEditorSetPotionEffects", "Set potion effect types");
        Lang.en.put("eventEditorMustSetPotionTypesFirst", "You must set potion effect types first!");
        Lang.en.put("eventEditorSetPotionDurations", "Set potion effect durations");
        Lang.en.put("eventEditorMustSetPotionDurationsFirst", "You must set potion effect durations first!");
        Lang.en.put("eventEditorMustSetPotionTypesAndDurationsFirst",
                "You must set potion effect types and durations first!");
        Lang.en.put("eventEditorNoDurationsSet", "(No durations set)");
        Lang.en.put("eventEditorSetPotionMagnitudes", "Set potion effect magnitudes");
        Lang.en.put("eventEditorPotionsCleared", "Potion effects cleared.");
        Lang.en.put("eventEditorInvalidPotionType", "___ is not a valid potion effect type!");

        Lang.en.put("eventEditorEnterNPCId", "Enter NPC ID (or -1 to return)");
        Lang.en.put("eventEditorNoNPCExists", "No NPC exists with that id!");
        Lang.en.put(
                "eventEditorExplosionPrompt",
                "Right-click on a block to spawn an explosion at, then enter \"add\" to add it to the list, or enter \"clear\" to clear the explosions list, or \"cancel\" to return");
        Lang.en.put("eventEditorSelectBlockFirst", "You must select a block first.");
        Lang.en.put("eventEditorSetMessagePrompt",
                "Enter message, or enter \'none\' to delete, (or \'cancel\' to return)");
        Lang.en.put("eventEditorSetItemIDsPrompt",
                "Enter item IDs separating each one by a space, or enter \"cancel\" to return.");
        Lang.en.put("eventEditorSetItemAmountsPrompt",
                "Enter item amounts (numbers) separating each one by a space, or enter \"cancel\" to return.");
        Lang.en.put("eventEditorSetMobTypesPrompt", "Enter mob name, or enter \"cancel\" to return");
        Lang.en.put("eventEditorSetMobAmountsPrompt", "Enter mob amount, or enter \"cancel\" to return");
        Lang.en.put("eventEditorSetMobNamePrompt", "Set the name for this mob, or enter \"cancel\" to return");
        Lang.en.put(
                "eventEditorSetMobLocationPrompt",
                "Right-click on a block to select it, then enter \"add\" to add it to the mob spawn location list, or enter \"cancel\" to return");
        Lang.en.put("eventEditorSetPotionEffectsPrompt",
                "Enter potion effect types separating each one by a space, or enter \"cancel\" to return");
        Lang.en.put("eventEditorSetPotionDurationsPrompt",
                "Enter effect durations (in milliseconds) separating each one by a space, or enter \"cancel\" to return");
        Lang.en.put("eventEditorSetPotionMagnitudesPrompt",
                "Enter potion effect magnitudes separating each one by a space, or enter \"cancel\" to return");
        Lang.en.put("eventEditorSetHungerPrompt", "Enter hunger level, or -1 to remove it");
        Lang.en.put("eventEditorHungerLevelAtLeastZero", "Hunger level must be at least 0!");
        Lang.en.put("eventEditorSetSaturationPrompt", "Enter saturation level, or -1 to remove it");
        Lang.en.put("eventEditorSaturationLevelAtLeastZero", "Saturation level must be at least 0!");
        Lang.en.put("eventEditorSetHealthPrompt", "Enter health level, or -1 to remove it");
        Lang.en.put("eventEditorHealthLevelAtLeastZero", "Health level must be at least 0!");
        Lang.en.put(
                "eventEditorSetTeleportPrompt",
                "Right-click on a block to teleport the player to, then enter \"done\" to finish,\nor enter \"clear\" to clear the teleport location, or \"cancel\" to return");
        Lang.en.put("eventEditorCommandsNote", "Note: You may use <player> to refer to the player's name.");
        Lang.en.put("eventEditorSetCommandsPrompt",
                "Enter commands separating each one by a comma, or enter \"clear\" to clear the list, or enter \"cancel\" to return.");
        Lang.en.put("eventEditorSet", "");
        // en.put("eventEditorSet", "");
        // en.put("eventEditorSet", "");

        //

        // Effects
        Lang.en.put("effBlazeShoot", "Sound of a Blaze firing");
        Lang.en.put("effBowFire", "Sound of a bow firing");
        Lang.en.put("effClick1", "A click sound");
        Lang.en.put("effClick2", "A different click sound");
        Lang.en.put("effDoorToggle", "Sound of a door opening or closing");
        Lang.en.put("effExtinguish", "Sound of fire being extinguished");
        Lang.en.put("effGhastShoot", "Sound of a Ghast firing");
        Lang.en.put("effGhastShriek", "Sound of a Ghast shrieking");
        Lang.en.put("effZombieWood", "Sound of a Zombie chewing an iron door");
        Lang.en.put("effZombieIron", "Sound of a Zombie chewing a wooden door");
        Lang.en.put("effEnterName", "Enter an effect name to add it to the list, or enter \"cancel\" to return");

        //

        // Inputs
        Lang.en.put("cmdCancel", "cancel");
        Lang.en.put("cmdAdd", "add");
        Lang.en.put("cmdClear", "clear");
        Lang.en.put("cmdNone", "none");
        Lang.en.put("cmdDone", "done");
        //

        // Misc
        Lang.en.put("event", "Event");
        Lang.en.put("delay", "Delay");
        Lang.en.put("save", "Save");
        Lang.en.put("exit", "Exit");
        Lang.en.put("exited", "Exited");
        Lang.en.put("cancel", "Cancel");
        Lang.en.put("back", "Back");
        Lang.en.put("yes", "Yes");
        Lang.en.put("no", "No");
        Lang.en.put("clear", "Clear");
        Lang.en.put("none", "None");
        Lang.en.put("done", "Done");
        Lang.en.put("quit", "Quit");
        Lang.en.put("noneSet", "None set");
        Lang.en.put("noDelaySet", "No delay set");
        Lang.en.put("noIdsSet", "No IDs set");
        Lang.en.put("worlds", "Worlds");
        Lang.en.put("mobs", "Mobs");
        Lang.en.put("invalidOption", "Invalid option!");
        Lang.en.put("npcHint", "Note: You can left or right click on NPC's to get their ID.");
        //
        //

        final File file = new File(plugin.getDataFolder(), "/lang/" + Lang.lang + ".yml");
        final YamlConfiguration langFile = YamlConfiguration.loadConfiguration(file);

        for (final Entry<String, Object> e: langFile.getValues(true).entrySet()) {
            Lang.en.put(e.getKey(), (String) e.getValue());
        }

    }

    public void save() {
        final File file = new File(plugin.getDataFolder(), "/lang/" + Lang.lang + ".yml");
        final YamlConfiguration langFile = YamlConfiguration.loadConfiguration(file);

        for (final Entry<String, String> e: Lang.en.entrySet()) {
            langFile.set(e.getKey(), e.getValue());
        }

        try {
            langFile.save(file);
        } catch (final IOException e1) {
            e1.printStackTrace();
        }
    }
}
