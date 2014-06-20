package me.blackvein.quests.prompts;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Event;
import me.blackvein.quests.QuestFactory;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.ColorUtil;
import me.blackvein.quests.util.ItemUtil;
import me.blackvein.quests.util.Lang;
import me.blackvein.quests.util.MiscUtil;
import net.aufdemrand.denizen.scripts.ScriptRegistry;
import net.citizensnpcs.api.CitizensPlugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateStagePrompt extends FixedSetPrompt implements ColorUtil {

    private final int            stageNum;
    private final String         pref;
    private final CitizensPlugin citizens;
    private final QuestFactory   questFactory;

    public CreateStagePrompt(int stageNum, QuestFactory qf, CitizensPlugin cit) {

        super("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
                "20", "21", "22", "23", "24", "25", "26");
        this.stageNum = stageNum;
        pref = "stage" + stageNum;
        citizens = cit;
        questFactory = qf;

    }

    @SuppressWarnings("unchecked")
    @Override
    public String getPromptText(ConversationContext context) {

        context.setSessionData(pref, Boolean.TRUE);

        String text = ColorUtil.PINK + "- " + ColorUtil.AQUA + (String) context.getSessionData(CK.Q_NAME)
                + ColorUtil.PINK + " | " + Lang.get("stageEditorStage") + " " + ColorUtil.PURPLE + stageNum
                + ColorUtil.PINK + " -\n";

        if (context.getSessionData(pref + CK.S_BREAK_IDS) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "1 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorBreakBlocks") + ColorUtil.GRAY + "  (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "1 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorBreakBlocks") + "\n";

            final LinkedList<Integer> ids = (LinkedList<Integer>) context.getSessionData(pref + CK.S_BREAK_IDS);
            final LinkedList<Integer> amnts = (LinkedList<Integer>) context.getSessionData(pref + CK.S_BREAK_AMOUNTS);

            for (int i = 0; i < ids.size(); i++) {
                text += ColorUtil.GRAY + "    - " + ColorUtil.BLUE + Quester.prettyItemString(ids.get(i))
                        + ColorUtil.GRAY + " x " + ColorUtil.DARKAQUA + amnts.get(i) + "\n";
            }

        }

        if (context.getSessionData(pref + CK.S_DAMAGE_IDS) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "2 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorDamageBlocks") + ColorUtil.GRAY + "  (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "2 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorDamageBlocks") + "\n";

            final LinkedList<Integer> ids = (LinkedList<Integer>) context.getSessionData(pref + CK.S_DAMAGE_IDS);
            final LinkedList<Integer> amnts = (LinkedList<Integer>) context.getSessionData(pref + CK.S_DAMAGE_AMOUNTS);

            for (int i = 0; i < ids.size(); i++) {
                text += ColorUtil.GRAY + "    - " + ColorUtil.BLUE + Quester.prettyItemString(ids.get(i))
                        + ColorUtil.GRAY + " x " + ColorUtil.DARKAQUA + amnts.get(i) + "\n";
            }

        }

        if (context.getSessionData(pref + CK.S_PLACE_IDS) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "3 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorPlaceBlocks") + ColorUtil.GRAY + "  (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "3 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorPlaceBlocks") + "\n";

            final LinkedList<Integer> ids = (LinkedList<Integer>) context.getSessionData(pref + CK.S_PLACE_IDS);
            final LinkedList<Integer> amnts = (LinkedList<Integer>) context.getSessionData(pref + CK.S_PLACE_AMOUNTS);

            for (int i = 0; i < ids.size(); i++) {
                text += ColorUtil.GRAY + "    - " + ColorUtil.BLUE + Quester.prettyItemString(ids.get(i))
                        + ColorUtil.GRAY + " x " + ColorUtil.DARKAQUA + amnts.get(i) + "\n";
            }

        }

        if (context.getSessionData(pref + CK.S_USE_IDS) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "4 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorUseBlocks") + ColorUtil.GRAY + "  (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "4 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorUseBlocks") + "\n";

            final LinkedList<Integer> ids = (LinkedList<Integer>) context.getSessionData(pref + CK.S_USE_IDS);
            final LinkedList<Integer> amnts = (LinkedList<Integer>) context.getSessionData(pref + CK.S_USE_AMOUNTS);

            for (int i = 0; i < ids.size(); i++) {
                text += ColorUtil.GRAY + "    - " + ColorUtil.BLUE + Quester.prettyItemString(ids.get(i))
                        + ColorUtil.GRAY + " x " + ColorUtil.DARKAQUA + amnts.get(i) + "\n";
            }

        }

        if (context.getSessionData(pref + CK.S_CUT_IDS) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "5 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorCutBlocks") + ColorUtil.GRAY + "  (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "5 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorCutBlocks") + "\n";

            final LinkedList<Integer> ids = (LinkedList<Integer>) context.getSessionData(pref + CK.S_CUT_IDS);
            final LinkedList<Integer> amnts = (LinkedList<Integer>) context.getSessionData(pref + CK.S_CUT_AMOUNTS);

            for (int i = 0; i < ids.size(); i++) {
                text += ColorUtil.GRAY + "    - " + ColorUtil.BLUE + Quester.prettyItemString(ids.get(i))
                        + ColorUtil.GRAY + " x " + ColorUtil.DARKAQUA + amnts.get(i) + "\n";
            }

        }

        if (context.getSessionData(pref + CK.S_FISH) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "6 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorCatchFish") + ColorUtil.GRAY + "  (" + Lang.get("noneSet") + ")\n";
        } else {
            final Integer fish = (Integer) context.getSessionData(pref + CK.S_FISH);
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "6 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorCatchFish") + " " + ColorUtil.GRAY + "(" + ColorUtil.AQUA + fish + " "
                    + Lang.get("stageEditorFish") + ColorUtil.GRAY + ")\n";
        }

        if (context.getSessionData(pref + CK.S_PLAYER_KILL) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "7 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorKillPlayers") + ColorUtil.GRAY + "  (" + Lang.get("noneSet") + ")\n";
        } else {
            final Integer players = (Integer) context.getSessionData(pref + CK.S_PLAYER_KILL);
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "7 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorKillPlayers") + ColorUtil.GRAY + " (" + ColorUtil.AQUA + players + " "
                    + Lang.get("stageEditorPlayers") + ColorUtil.GRAY + ")\n";
        }

        if (context.getSessionData(pref + CK.S_ENCHANT_TYPES) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "8 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorEnchantItems") + ColorUtil.GRAY + "  (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "8 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorEnchantItems") + "\n";

            final LinkedList<String> enchants = (LinkedList<String>) context.getSessionData(pref + CK.S_ENCHANT_TYPES);
            final LinkedList<Integer> items = (LinkedList<Integer>) context.getSessionData(pref + CK.S_ENCHANT_IDS);
            final LinkedList<Integer> amnts = (LinkedList<Integer>) context.getSessionData(pref + CK.S_ENCHANT_AMOUNTS);

            for (int i = 0; i < enchants.size(); i++) {
                text += ColorUtil.GRAY + "    - " + ColorUtil.BLUE + Quester.prettyItemString(items.get(i))
                        + ColorUtil.GRAY + " with " + ColorUtil.AQUA + Quester.prettyString(enchants.get(i))
                        + ColorUtil.GRAY + " x " + ColorUtil.DARKAQUA + amnts.get(i) + "\n";
            }

        }

        if (questFactory.quests.citizens != null) {

            if (context.getSessionData(pref + CK.S_DELIVERY_ITEMS) == null) {
                text += ColorUtil.PINK + "" + ColorUtil.BOLD + "9 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                        + Lang.get("stageEditorDeliverItems") + ColorUtil.GRAY + "  (" + Lang.get("noneSet") + ")\n";
            } else {
                text += ColorUtil.PINK + "" + ColorUtil.BOLD + "9 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                        + Lang.get("stageEditorDeliverItems") + "\n";

                final LinkedList<Integer> npcs = (LinkedList<Integer>) context
                        .getSessionData(pref + CK.S_DELIVERY_NPCS);
                final LinkedList<ItemStack> items = (LinkedList<ItemStack>) context.getSessionData(pref
                        + CK.S_DELIVERY_ITEMS);

                for (int i = 0; i < npcs.size(); i++) {
                    text += ColorUtil.GRAY + "    - " + ColorUtil.BLUE + ItemUtil.getName(items.get(i))
                            + ColorUtil.GRAY + " x " + ColorUtil.AQUA + items.get(i).getAmount() + ColorUtil.GRAY
                            + " to " + ColorUtil.DARKAQUA + citizens.getNPCRegistry().getById(npcs.get(i)).getName()
                            + "\n";
                }

            }

        } else {
            text += ColorUtil.GRAY + "" + ColorUtil.BOLD + "9 " + ColorUtil.RESET + ColorUtil.GRAY + "- "
                    + Lang.get("stageEditorDeliverItems") + ColorUtil.GRAY + " (Citizens not installed)\n";
        }

        if (questFactory.quests.citizens != null) {

            if (context.getSessionData(pref + CK.S_NPCS_TO_TALK_TO) == null) {
                text += ColorUtil.PINK + "" + ColorUtil.BOLD + "10 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                        + Lang.get("stageEditorTalkToNPCs") + ColorUtil.GRAY + "  (" + Lang.get("noneSet") + ")\n";
            } else {
                text += ColorUtil.PINK + "" + ColorUtil.BOLD + "10 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                        + Lang.get("stageEditorTalkToNPCs") + "\n";

                final LinkedList<Integer> npcs = (LinkedList<Integer>) context.getSessionData(pref
                        + CK.S_NPCS_TO_TALK_TO);

                for (int i = 0; i < npcs.size(); i++) {
                    text += ColorUtil.GRAY + "    - " + ColorUtil.BLUE
                            + citizens.getNPCRegistry().getById(npcs.get(i)).getName() + "\n";
                }

            }

        } else {
            text += ColorUtil.GRAY + "" + ColorUtil.BOLD + "10 " + ColorUtil.RESET + ColorUtil.GRAY + "- "
                    + Lang.get("stageEditorTalkToNPCs") + ColorUtil.GRAY + " (Citizens not installed)\n";
        }

        if (questFactory.quests.citizens != null) {

            if (context.getSessionData(pref + CK.S_NPCS_TO_KILL) == null) {
                text += ColorUtil.PINK + "" + ColorUtil.BOLD + "11 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                        + Lang.get("stageEditorKillNPCs") + ColorUtil.GRAY + "  (" + Lang.get("noneSet") + ")\n";
            } else {
                text += ColorUtil.PINK + "" + ColorUtil.BOLD + "11 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                        + Lang.get("stageEditorKillNPCs") + "\n";

                final LinkedList<Integer> npcs = (LinkedList<Integer>) context.getSessionData(pref + CK.S_NPCS_TO_KILL);
                final LinkedList<Integer> amounts = (LinkedList<Integer>) context.getSessionData(pref
                        + CK.S_NPCS_TO_KILL_AMOUNTS);

                for (int i = 0; i < npcs.size(); i++) {
                    text += ColorUtil.GRAY + "    - " + ColorUtil.BLUE
                            + citizens.getNPCRegistry().getById(npcs.get(i)).getName() + ColorUtil.GRAY + " x "
                            + ColorUtil.AQUA + amounts.get(i) + "\n";
                }

            }

        } else {
            text += ColorUtil.GRAY + "" + ColorUtil.BOLD + "11 " + ColorUtil.RESET + ColorUtil.GRAY + "- "
                    + Lang.get("stageEditorKillNPCs") + ColorUtil.GRAY + " (Citizens not installed)\n";
        }

        if (context.getSessionData(pref + CK.S_MOB_TYPES) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "12 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorKillMobs") + ColorUtil.GRAY + "  (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "12 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorKillMobs") + "\n";

            final LinkedList<String> mobs = (LinkedList<String>) context.getSessionData(pref + CK.S_MOB_TYPES);
            final LinkedList<Integer> amnts = (LinkedList<Integer>) context.getSessionData(pref + CK.S_MOB_AMOUNTS);

            if (context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS) == null) {

                for (int i = 0; i < mobs.size(); i++) {
                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + Quester.prettyString(mobs.get(i))
                            + ColorUtil.GRAY + " x " + ColorUtil.DARKAQUA + amnts.get(i) + "\n";
                }

            } else {

                final LinkedList<String> locs = (LinkedList<String>) context.getSessionData(pref
                        + CK.S_MOB_KILL_LOCATIONS);
                final LinkedList<Integer> radii = (LinkedList<Integer>) context.getSessionData(pref
                        + CK.S_MOB_KILL_LOCATIONS_RADIUS);
                final LinkedList<String> names = (LinkedList<String>) context.getSessionData(pref
                        + CK.S_MOB_KILL_LOCATIONS_NAMES);

                for (int i = 0; i < mobs.size(); i++) {
                    text += ColorUtil.GRAY + "    - " + ColorUtil.BLUE + Quester.prettyString(mobs.get(i))
                            + ColorUtil.GRAY + " x " + ColorUtil.DARKAQUA + amnts.get(i) + ColorUtil.GRAY + " within "
                            + ColorUtil.PURPLE + radii.get(i) + ColorUtil.GRAY + " blocks of " + ColorUtil.YELLOW
                            + names.get(i) + " (" + locs.get(i) + ")\n";
                }

            }

        }

        if (context.getSessionData(pref + CK.S_REACH_LOCATIONS) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "13 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorReachLocs") + ColorUtil.GRAY + "  (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "13 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorReachLocs") + "\n";

            final LinkedList<String> locations = (LinkedList<String>) context.getSessionData(pref
                    + CK.S_REACH_LOCATIONS);
            final LinkedList<Integer> radii = (LinkedList<Integer>) context.getSessionData(pref
                    + CK.S_REACH_LOCATIONS_RADIUS);
            final LinkedList<String> names = (LinkedList<String>) context.getSessionData(pref
                    + CK.S_REACH_LOCATIONS_NAMES);

            for (int i = 0; i < locations.size(); i++) {
                text += ColorUtil.GRAY + "    - " + Lang.get("stageEditorReachRadii1") + " " + ColorUtil.BLUE
                        + radii.get(i) + ColorUtil.GRAY + " " + Lang.get("stageEditorReachRadii2") + " "
                        + ColorUtil.AQUA + names.get(i) + ColorUtil.GRAY + " (" + ColorUtil.DARKAQUA + locations.get(i)
                        + ColorUtil.GRAY + ")\n";
            }

        }

        if (context.getSessionData(pref + CK.S_TAME_TYPES) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "14 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorTameMobs") + ColorUtil.GRAY + "  (" + Lang.get("noneSet") + ")\n";
        } else {

            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "14 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorTameMobs") + "\n";

            final LinkedList<String> mobs = (LinkedList<String>) context.getSessionData(pref + CK.S_TAME_TYPES);
            final LinkedList<Integer> amounts = (LinkedList<Integer>) context.getSessionData(pref + CK.S_TAME_AMOUNTS);

            for (int i = 0; i < mobs.size(); i++) {
                text += ColorUtil.GRAY + "    - " + ColorUtil.BLUE + mobs.get(i) + ColorUtil.GRAY + " x "
                        + ColorUtil.AQUA + amounts.get(i) + "\n";
            }

        }

        if (context.getSessionData(pref + CK.S_SHEAR_COLORS) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "15 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorShearSheep") + ColorUtil.GRAY + "  (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "15 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorShearSheep") + "\n";

            final LinkedList<String> colors = (LinkedList<String>) context.getSessionData(pref + CK.S_SHEAR_COLORS);
            final LinkedList<Integer> amounts = (LinkedList<Integer>) context.getSessionData(pref + CK.S_SHEAR_AMOUNTS);

            for (int i = 0; i < colors.size(); i++) {
                text += ColorUtil.GRAY + "    - " + ColorUtil.BLUE + colors.get(i) + ColorUtil.GRAY + " x "
                        + ColorUtil.AQUA + amounts.get(i) + "\n";
            }

        }

        text += ColorUtil.PINK + "" + ColorUtil.BOLD + "16 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                + Lang.get("stageEditorEvents") + "\n";

        if (context.getSessionData(pref + CK.S_DELAY) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "17 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("delay") + ColorUtil.GRAY + "  (" + Lang.get("noneSet") + ")\n";
        } else {
            final long time = (Long) context.getSessionData(pref + CK.S_DELAY);
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "17 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("delay") + ColorUtil.GRAY + "(" + ColorUtil.AQUA + Quests.getTime(time) + ColorUtil.GRAY
                    + ")\n";
        }

        if (context.getSessionData(pref + CK.S_DELAY) == null) {
            text += ColorUtil.GRAY + "" + ColorUtil.BOLD + "18 " + ColorUtil.RESET + ColorUtil.GRAY + "- "
                    + Lang.get("stageEditorDelayMessage") + ColorUtil.GRAY + " (" + Lang.get("noDelaySet") + ")\n";
        } else if (context.getSessionData(pref + CK.S_DELAY_MESSAGE) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "18 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorDelayMessage") + ColorUtil.GRAY + " (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "18 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorDelayMessage") + ColorUtil.GRAY + " (" + ColorUtil.AQUA + "\""
                    + context.getSessionData(pref + CK.S_DELAY_MESSAGE) + "\"" + ColorUtil.GRAY + ")\n";
        }

        if (questFactory.quests.denizen == null) {
            text += ColorUtil.GRAY + "" + ColorUtil.BOLD + "19 " + ColorUtil.RESET + ColorUtil.GRAY + "- "
                    + Lang.get("stageEditorDenizenScript") + ColorUtil.GRAY + " (Denizen not installed)\n";
        } else {

            if (context.getSessionData(pref + CK.S_DENIZEN) == null) {
                text += ColorUtil.PINK + "" + ColorUtil.BOLD + "19 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                        + Lang.get("stageEditorDenizenScript") + ColorUtil.GRAY + " (" + Lang.get("noneSet") + ")\n";
            } else {
                text += ColorUtil.PINK + "" + ColorUtil.BOLD + "19 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                        + Lang.get("stageEditorDenizenScript") + ColorUtil.GRAY + " (" + ColorUtil.AQUA
                        + context.getSessionData(pref + CK.S_DENIZEN) + ColorUtil.GRAY + "\n";
            }

        }

        if (context.getSessionData(pref + CK.S_PASSWORD_PHRASES) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "20 " + ColorUtil.RESET + ColorUtil.PURPLE
                    + "- Password Objectives" + ColorUtil.GRAY + " (" + Lang.get("noneSet") + ")\n";
        } else {
            final LinkedList<LinkedList<String>> passPhrases = (LinkedList<LinkedList<String>>) context
                    .getSessionData(pref + CK.S_PASSWORD_PHRASES);
            final LinkedList<String> passDisplays = (LinkedList<String>) context.getSessionData(pref
                    + CK.S_PASSWORD_DISPLAYS);
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "20 " + ColorUtil.RESET + ColorUtil.PURPLE
                    + "- Password Objectives\n";
            for (int i = 0; i < passPhrases.size(); i++) {
                text += ColorUtil.AQUA + "    - \"" + passDisplays.get(i) + "\"\n";
                final LinkedList<String> phrases = passPhrases.get(i);
                for (final String phrase: phrases) {
                    text += ColorUtil.DARKAQUA + "      - " + phrase + "\n";
                }
            }
        }

        if (context.getSessionData(pref + CK.S_CUSTOM_OBJECTIVES) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "21 " + ColorUtil.RESET + ColorUtil.PINK
                    + "- Custom Objectives" + ColorUtil.GRAY + " (" + Lang.get("noneSet") + ")\n";
        } else {
            final LinkedList<String> customObjs = (LinkedList<String>) context.getSessionData(pref
                    + CK.S_CUSTOM_OBJECTIVES);
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "21 " + ColorUtil.RESET + ColorUtil.PINK
                    + "- Custom Objectives\n";
            for (final String s: customObjs) {
                text += ColorUtil.PINK + "    - " + ColorUtil.GOLD + s + "\n";
            }
        }

        if (context.getSessionData(pref + CK.S_START_MESSAGE) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "22 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorStartMessage") + ColorUtil.GRAY + " (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "22 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorStartMessage") + ColorUtil.GRAY + "(" + ColorUtil.AQUA + "\""
                    + context.getSessionData(pref + CK.S_START_MESSAGE) + "\"" + ColorUtil.GRAY + ")\n";
        }

        if (context.getSessionData(pref + CK.S_COMPLETE_MESSAGE) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "23 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorCompleteMessage") + ColorUtil.GRAY + " (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "23 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                    + Lang.get("stageEditorCompleteMessage") + ColorUtil.GRAY + "(" + ColorUtil.AQUA + "\""
                    + context.getSessionData(pref + CK.S_COMPLETE_MESSAGE) + "\"" + ColorUtil.GRAY + ")\n";
        }

        if (context.getSessionData(pref + CK.S_OVERRIDE_DISPLAY) == null) {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "24 " + ColorUtil.RESET + ColorUtil.PURPLE
                    + "- Objective Display Override " + ColorUtil.GRAY + " (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.PINK + "" + ColorUtil.BOLD + "24 " + ColorUtil.RESET + ColorUtil.PURPLE
                    + "- Objective Display Override " + ColorUtil.GRAY + "(" + ColorUtil.DARKAQUA + "\""
                    + context.getSessionData(pref + CK.S_OVERRIDE_DISPLAY) + "\"" + ColorUtil.GRAY + ")\n";
        }

        text += ColorUtil.RED + "" + ColorUtil.BOLD + "25 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                + Lang.get("stageEditorDelete") + "\n";
        text += ColorUtil.GREEN + "" + ColorUtil.BOLD + "26 " + ColorUtil.RESET + ColorUtil.PURPLE + "- "
                + Lang.get("done") + "\n";

        return text;

    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, String input) {

        if (input.equalsIgnoreCase("1")) {
            return new BreakBlockListPrompt();
        } else if (input.equalsIgnoreCase("2")) {
            return new DamageBlockListPrompt();
        } else if (input.equalsIgnoreCase("3")) {
            return new PlaceBlockListPrompt();
        } else if (input.equalsIgnoreCase("4")) {
            return new UseBlockListPrompt();
        } else if (input.equalsIgnoreCase("5")) {
            return new CutBlockListPrompt();
        } else if (input.equalsIgnoreCase("6")) {
            return new FishPrompt();
        } else if (input.equalsIgnoreCase("7")) {
            return new KillPlayerPrompt();
        } else if (input.equalsIgnoreCase("8")) {
            return new EnchantmentListPrompt();
        } else if (input.equalsIgnoreCase("9")) {
            if (questFactory.quests.citizens != null) {
                return new DeliveryListPrompt();
            }
            context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoCitizens"));
            return new CreateStagePrompt(stageNum, questFactory, citizens);
        } else if (input.equalsIgnoreCase("10")) {
            if (questFactory.quests.citizens != null) {
                return new NPCIDsToTalkToPrompt();
            }
            context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoCitizens"));
            return new CreateStagePrompt(stageNum, questFactory, citizens);
        } else if (input.equalsIgnoreCase("11")) {
            if (questFactory.quests.citizens != null) {
                return new NPCKillListPrompt();
            }
            context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoCitizens"));
            return new CreateStagePrompt(stageNum, questFactory, citizens);
        } else if (input.equalsIgnoreCase("12")) {
            return new MobListPrompt();
        } else if (input.equalsIgnoreCase("13")) {
            return new ReachListPrompt();
        } else if (input.equalsIgnoreCase("14")) {
            return new TameListPrompt();
        } else if (input.equalsIgnoreCase("15")) {
            return new ShearListPrompt();
        } else if (input.equalsIgnoreCase("16")) {
            return new EventListPrompt();
        } else if (input.equalsIgnoreCase("17")) {
            return new DelayPrompt();
        } else if (input.equalsIgnoreCase("18")) {
            if (context.getSessionData(pref + CK.S_DELAY) == null) {
                context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoDelaySet"));
                return new CreateStagePrompt(stageNum, questFactory, citizens);
            }
            return new DelayMessagePrompt();
        } else if (input.equalsIgnoreCase("19")) {
            if (questFactory.quests.denizen == null) {
                context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoDenizen"));
                return new CreateStagePrompt(stageNum, questFactory, citizens);
            }
            return new DenizenPrompt();
        } else if (input.equalsIgnoreCase("20")) {
            return new PasswordListPrompt();
        } else if (input.equalsIgnoreCase("21")) {
            return new CustomObjectivesPrompt();
        } else if (input.equalsIgnoreCase("22")) {
            return new StartMessagePrompt();
        } else if (input.equalsIgnoreCase("23")) {
            return new CompleteMessagePrompt();
        } else if (input.equalsIgnoreCase("24")) {
            return new OverrideDisplayPrompt();
        } else if (input.equalsIgnoreCase("25")) {
            return new DeletePrompt();
        } else if (input.equalsIgnoreCase("26")) {
            return new StagesPrompt(questFactory);
        } else {
            return new CreateStagePrompt(stageNum, questFactory, citizens);
        }

    }

    private class PasswordListPrompt extends FixedSetPrompt {

        public PasswordListPrompt() {

            super("1", "2", "3", "4");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- Password Objectives -\n";
            if (context.getSessionData(pref + CK.S_PASSWORD_DISPLAYS) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Add password display (" + Lang.get("noneSet") + ")\n";
                text += ColorUtil.GRAY + "2 - " + "Add password phrase(s) (No password displays set)\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");
            } else {

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Add password display\n";
                for (final String display: getPasswordDisplays(context)) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + display + "\n";

                }

                if (context.getSessionData(pref + CK.S_PASSWORD_PHRASES) == null) {
                    text += ColorUtil.YELLOW + "2 - " + "Add password phrase(s) (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.YELLOW + "2 - " + "Add password phrase(s)\n";
                    for (final LinkedList<String> phraseList: getPasswordPhrases(context)) {

                        text += ColorUtil.GRAY + "    - ";
                        for (final String s: phraseList) {
                            if (phraseList.getLast().equals(s) == false) {
                                text += ColorUtil.DARKAQUA + s + ColorUtil.GRAY + "|";
                            } else {
                                text += ColorUtil.DARKAQUA + s + "\n";
                            }
                        }

                    }

                }

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");

            }

            return text;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new PasswordDisplayPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                if (context.getSessionData(pref + CK.S_PASSWORD_DISPLAYS) == null) {
                    context.getForWhom().sendRawMessage(
                            ColorUtil.RED + "You must add at least one password display first!");
                    return new PasswordListPrompt();
                }
                return new PasswordPhrasePrompt();
            } else if (input.equalsIgnoreCase("3")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Password Objectives cleared.");
                context.setSessionData(pref + CK.S_PASSWORD_DISPLAYS, null);
                context.setSessionData(pref + CK.S_PASSWORD_PHRASES, null);
                return new PasswordListPrompt();
            } else if (input.equalsIgnoreCase("4")) {

                int one;
                int two;

                if (context.getSessionData(pref + CK.S_PASSWORD_DISPLAYS) != null) {
                    one = ((List<String>) context.getSessionData(pref + CK.S_PASSWORD_DISPLAYS)).size();
                } else {
                    one = 0;
                }

                if (context.getSessionData(pref + CK.S_PASSWORD_PHRASES) != null) {
                    two = ((LinkedList<LinkedList<String>>) context.getSessionData(pref + CK.S_PASSWORD_PHRASES))
                            .size();
                } else {
                    two = 0;
                }

                if (one == two) {
                    return new CreateStagePrompt(stageNum, questFactory, citizens);
                }
                context.getForWhom().sendRawMessage(
                        ColorUtil.RED + "The password display and password phrase lists are not the same size!");
                return new PasswordListPrompt();
            }
            return null;

        }

        @SuppressWarnings("unchecked")
        private List<String> getPasswordDisplays(ConversationContext context) {
            return (List<String>) context.getSessionData(pref + CK.S_PASSWORD_DISPLAYS);
        }

        @SuppressWarnings("unchecked")
        private LinkedList<LinkedList<String>> getPasswordPhrases(ConversationContext context) {
            return (LinkedList<LinkedList<String>>) context.getSessionData(pref + CK.S_PASSWORD_PHRASES);
        }

    }

    private class PasswordDisplayPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.YELLOW + "Enter a password display, or 'cancel' to return\n";
            text += ColorUtil.ITALIC + "" + ColorUtil.GOLD
                    + "(This is the text that will be displayed to the player as their objective)";

            return text;

        }

        @SuppressWarnings("unchecked")
        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("cancel") == false) {

                if (context.getSessionData(pref + CK.S_PASSWORD_DISPLAYS) != null) {

                    final List<String> displays = (List<String>) context.getSessionData(pref + CK.S_PASSWORD_DISPLAYS);
                    displays.add(input);
                    context.setSessionData(pref + CK.S_PASSWORD_DISPLAYS, displays);

                } else {

                    final List<String> displays = new LinkedList<String>();
                    displays.add(input);
                    context.setSessionData(pref + CK.S_PASSWORD_DISPLAYS, displays);

                }

            }

            return new PasswordListPrompt();

        }

    }

    private class PasswordPhrasePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.YELLOW + "Enter a password phrase, or 'cancel' to return\n";
            text += ColorUtil.ITALIC + "" + ColorUtil.GOLD
                    + "(This is the text that a player has to say to complete the objective)\n";
            text += ColorUtil.RESET + "" + ColorUtil.YELLOW
                    + "If you want multiple password phrases, seperate them by a | (pipe)";

            return text;

        }

        @SuppressWarnings("unchecked")
        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("cancel") == false) {

                if (context.getSessionData(pref + CK.S_PASSWORD_PHRASES) != null) {

                    final LinkedList<LinkedList<String>> phrases = (LinkedList<LinkedList<String>>) context
                            .getSessionData(pref + CK.S_PASSWORD_PHRASES);
                    final LinkedList<String> newPhrases = new LinkedList<String>();
                    newPhrases.addAll(Arrays.asList(input.split("\\|")));
                    phrases.add(newPhrases);
                    context.setSessionData(pref + CK.S_PASSWORD_PHRASES, phrases);

                } else {

                    final LinkedList<LinkedList<String>> phrases = new LinkedList<LinkedList<String>>();
                    final LinkedList<String> newPhrases = new LinkedList<String>();
                    newPhrases.addAll(Arrays.asList(input.split("\\|")));
                    phrases.add(newPhrases);
                    context.setSessionData(pref + CK.S_PASSWORD_PHRASES, phrases);

                }

            }

            return new PasswordListPrompt();

        }

    }

    private class OverrideDisplayPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.YELLOW
                    + "Enter objective display override, or 'clear' to clear the override, or 'cancel' to return.\n";
            text += ColorUtil.ITALIC + "" + ColorUtil.GOLD
                    + "(The objective display override will show up as the players current objective)";

            return text;

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("clear") == false && input.equalsIgnoreCase("cancel") == false) {

                context.setSessionData(pref + CK.S_OVERRIDE_DISPLAY, input);

            } else if (input.equalsIgnoreCase("clear")) {

                context.setSessionData(pref + CK.S_OVERRIDE_DISPLAY, null);
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Objective display override cleared.");

            }

            return new CreateStagePrompt(stageNum, questFactory, citizens);

        }

    }

    private class BreakBlockListPrompt extends FixedSetPrompt {

        public BreakBlockListPrompt() {

            super("1", "2", "3", "4");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- " + Lang.get("stageEditorBreakBlocks") + " -\n";
            if (context.getSessionData(pref + CK.S_BREAK_IDS) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetBlockIds") + " (" + Lang.get("noneSet") + ")\n";
                text += ColorUtil.GRAY + "2 - " + Lang.get("stageEditorSetBlockAmounts") + " (" + Lang.get("noIdsSet")
                        + ")\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");
            } else {

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetBlockIds") + "\n";
                for (final Integer i: getBlockIds(context)) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + Quester.prettyItemString(i) + "\n";

                }

                if (context.getSessionData(pref + CK.S_BREAK_AMOUNTS) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetBlockAmounts") + " (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetBlockAmounts") + "\n";
                    for (final Integer i: getBlockAmounts(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + i + "\n";

                    }

                }

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");

            }

            return text;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new BreakBlockIdsPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                if (context.getSessionData(pref + CK.S_BREAK_IDS) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoBlockIds"));
                    return new BreakBlockListPrompt();
                }
                return new BreakBlockAmountsPrompt();
            } else if (input.equalsIgnoreCase("3")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + Lang.get("stageEditorBreakBlocksCleared"));
                context.setSessionData(pref + CK.S_BREAK_IDS, null);
                context.setSessionData(pref + CK.S_BREAK_AMOUNTS, null);
                return new BreakBlockListPrompt();
            } else if (input.equalsIgnoreCase("4")) {

                int one;
                int two;

                if (context.getSessionData(pref + CK.S_BREAK_IDS) != null) {
                    one = ((List<Integer>) context.getSessionData(pref + CK.S_BREAK_IDS)).size();
                } else {
                    one = 0;
                }

                if (context.getSessionData(pref + CK.S_BREAK_AMOUNTS) != null) {
                    two = ((List<Integer>) context.getSessionData(pref + CK.S_BREAK_AMOUNTS)).size();
                } else {
                    two = 0;
                }

                if (one == two) {
                    return new CreateStagePrompt(stageNum, questFactory, citizens);
                }
                context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorListNotSameSize"));
                return new BreakBlockListPrompt();
            }
            return null;

        }

        @SuppressWarnings("unchecked")
        private List<Integer> getBlockIds(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_BREAK_IDS);
        }

        @SuppressWarnings("unchecked")
        private List<Integer> getBlockAmounts(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_BREAK_AMOUNTS);
        }
    }

    private class BreakBlockIdsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW + Lang.get("stageEditorEnterBlockIds");
        }

        @SuppressWarnings("deprecation")
        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> ids = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        if (Material.getMaterial(Integer.parseInt(s)) != null) {

                            if (ids.contains(Integer.parseInt(s)) == false) {
                                ids.add(Integer.parseInt(s));
                            } else {
                                context.getForWhom().sendRawMessage(
                                        ColorUtil.RED + Lang.get("stageEditorContainsDuplicates"));
                                return new BreakBlockIdsPrompt();
                            }

                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + s + ColorUtil.RED + " " + Lang.get("stageEditorInvalidBlockID"));
                            return new BreakBlockIdsPrompt();
                        }

                    } catch (final NumberFormatException e) {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.RED + Lang.get("stageEditorNotListofNumbers") + "\n" + ColorUtil.PINK + s);
                        return new BreakBlockIdsPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_BREAK_IDS, ids);

            }

            return new BreakBlockListPrompt();

        }
    }

    private class BreakBlockAmountsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW + Lang.get("stageEditorBreakBlocksPrompt");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> amounts = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        if (Integer.parseInt(s) > 0) {
                            amounts.add(Integer.parseInt(s));
                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + s + ColorUtil.RED + " "
                                            + Lang.get("stageEditortNotGreaterThanZero"));
                            return new BreakBlockAmountsPrompt();
                        }

                    } catch (final NumberFormatException e) {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + Lang.get("stageEditorNotListofNumbers"));
                        return new BreakBlockAmountsPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_BREAK_AMOUNTS, amounts);

            }

            return new BreakBlockListPrompt();

        }
    }

    private class DamageBlockListPrompt extends FixedSetPrompt {

        public DamageBlockListPrompt() {

            super("1", "2", "3", "4");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- " + Lang.get("stageEditorDamageBlocks") + " -\n";
            if (context.getSessionData(pref + CK.S_DAMAGE_IDS) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetBlockIds") + " (" + Lang.get("noneSet") + ")\n";
                text += ColorUtil.GRAY + "2 - " + Lang.get("stageEditorSetDamageAmounts") + " (" + Lang.get("noIdsSet")
                        + ")\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");
            } else {

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetBlockIds") + "\n";
                for (final Integer i: getBlockIds(context)) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + Quester.prettyItemString(i) + "\n";

                }

                if (context.getSessionData(pref + CK.S_DAMAGE_AMOUNTS) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetDamageAmounts") + " (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetDamageAmounts") + "\n";
                    for (final Integer i: getBlockAmounts(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + i + "\n";

                    }

                }

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");

            }

            return text;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new DamageBlockIdsPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                if (context.getSessionData(pref + CK.S_DAMAGE_IDS) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoBlockIds"));
                    return new DamageBlockListPrompt();
                }
                return new DamageBlockAmountsPrompt();
            } else if (input.equalsIgnoreCase("3")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + Lang.get("stageEditorDamageBlocksCleared"));
                context.setSessionData(pref + CK.S_DAMAGE_IDS, null);
                context.setSessionData(pref + CK.S_DAMAGE_AMOUNTS, null);
                return new DamageBlockListPrompt();
            } else if (input.equalsIgnoreCase("4")) {

                int one;
                int two;

                if (context.getSessionData(pref + CK.S_DAMAGE_IDS) != null) {
                    one = ((List<Integer>) context.getSessionData(pref + CK.S_DAMAGE_IDS)).size();
                } else {
                    one = 0;
                }

                if (context.getSessionData(pref + CK.S_DAMAGE_AMOUNTS) != null) {
                    two = ((List<Integer>) context.getSessionData(pref + CK.S_DAMAGE_AMOUNTS)).size();
                } else {
                    two = 0;
                }

                if (one == two) {
                    return new CreateStagePrompt(stageNum, questFactory, citizens);
                }
                context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorListNotSameSize"));
                return new DamageBlockListPrompt();
            }
            return null;

        }

        @SuppressWarnings("unchecked")
        private List<Integer> getBlockIds(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_DAMAGE_IDS);
        }

        @SuppressWarnings("unchecked")
        private List<Integer> getBlockAmounts(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_DAMAGE_AMOUNTS);
        }
    }

    private class DamageBlockIdsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW + Lang.get("stageEditorEnterBlockIds");
        }

        @SuppressWarnings("deprecation")
        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> ids = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        if (Material.getMaterial(Integer.parseInt(s)) != null) {

                            if (ids.contains(Integer.parseInt(s)) == false) {
                                ids.add(Integer.parseInt(s));
                            } else {
                                context.getForWhom().sendRawMessage(
                                        ColorUtil.RED + Lang.get("stageEditorListContainsDuplicates"));
                                return new DamageBlockIdsPrompt();
                            }

                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + s + ColorUtil.RED + " " + Lang.get("stageEditorInvalidBlockID"));
                            return new DamageBlockIdsPrompt();
                        }

                    } catch (final NumberFormatException e) {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + Lang.get("stageEditorNotListofNumbers"));
                        return new DamageBlockIdsPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_DAMAGE_IDS, ids);

            }

            return new DamageBlockListPrompt();

        }
    }

    private class DamageBlockAmountsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW + Lang.get("stageEditorDamageBlocksPrompt");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> amounts = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        if (Integer.parseInt(s) > 0) {
                            amounts.add(Integer.parseInt(s));
                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + s + ColorUtil.RED + " "
                                            + Lang.get("stageEditorNotGreaterThanZero"));
                            return new DamageBlockAmountsPrompt();
                        }

                    } catch (final NumberFormatException e) {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + Lang.get("stageEditorNotListofNumbers"));
                        return new DamageBlockAmountsPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_DAMAGE_AMOUNTS, amounts);

            }

            return new DamageBlockListPrompt();

        }
    }

    private class PlaceBlockListPrompt extends FixedSetPrompt {

        public PlaceBlockListPrompt() {

            super("1", "2", "3", "4");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- " + Lang.get("stageEditorPlaceBlocks") + " -\n";
            if (context.getSessionData(pref + CK.S_PLACE_IDS) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetBlockIds") + " (" + Lang.get("noneSet") + ")\n";
                text += ColorUtil.GRAY + "2 - " + Lang.get("stageEditorSetPlaceAmounts") + " (" + Lang.get("noIdsSet")
                        + ")\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");
            } else {

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetBlockIds") + "\n";
                for (final Integer i: getBlockIds(context)) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + Quester.prettyItemString(i) + "\n";

                }

                if (context.getSessionData(pref + CK.S_PLACE_AMOUNTS) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetPlaceAmounts") + " (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + "stageEditorSetPlaceAmounts" + "\n";
                    for (final Integer i: getBlockAmounts(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + i + "\n";

                    }

                }

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");

            }

            return text;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new PlaceBlockIdsPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                if (context.getSessionData(pref + CK.S_PLACE_IDS) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoBlockIds"));
                    return new PlaceBlockListPrompt();
                } else {
                    return new PlaceBlockAmountsPrompt();
                }
            } else if (input.equalsIgnoreCase("3")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + Lang.get("stageEditorPlaceBlocksCleared"));
                context.setSessionData(pref + CK.S_PLACE_IDS, null);
                context.setSessionData(pref + CK.S_PLACE_AMOUNTS, null);
                return new PlaceBlockListPrompt();
            } else if (input.equalsIgnoreCase("4")) {

                int one;
                int two;

                if (context.getSessionData(pref + CK.S_PLACE_IDS) != null) {
                    one = ((List<Integer>) context.getSessionData(pref + CK.S_PLACE_IDS)).size();
                } else {
                    one = 0;
                }

                if (context.getSessionData(pref + CK.S_PLACE_AMOUNTS) != null) {
                    two = ((List<Integer>) context.getSessionData(pref + CK.S_PLACE_AMOUNTS)).size();
                } else {
                    two = 0;
                }

                if (one == two) {
                    return new CreateStagePrompt(stageNum, questFactory, citizens);
                }
                context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorListNotSameSize"));
                return new PlaceBlockListPrompt();
            }
            return null;

        }

        @SuppressWarnings("unchecked")
        private List<Integer> getBlockIds(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_PLACE_IDS);
        }

        @SuppressWarnings("unchecked")
        private List<Integer> getBlockAmounts(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_PLACE_AMOUNTS);
        }
    }

    private class PlaceBlockIdsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW + Lang.get("stageEditorEnterBlockIds");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> ids = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        if (Material.getMaterial(Integer.parseInt(s)) != null) {

                            if (ids.contains(Integer.parseInt(s)) == false) {
                                ids.add(Integer.parseInt(s));
                            } else {
                                context.getForWhom().sendRawMessage(
                                        ColorUtil.RED + Lang.get("stageEditorListContainsDuplicates"));
                                return new PlaceBlockIdsPrompt();
                            }

                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + s + ColorUtil.RED + Lang.get("stageEditorInvalidBlockID"));
                            return new PlaceBlockIdsPrompt();
                        }

                    } catch (final NumberFormatException e) {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + Lang.get("stageEditorNotListofNumbers"));
                        return new PlaceBlockIdsPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_PLACE_IDS, ids);

            }

            return new PlaceBlockListPrompt();

        }
    }

    private class PlaceBlockAmountsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW + Lang.get("stageEditorPlaceBlocksPrompt");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> amounts = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        if (Integer.parseInt(s) > 0) {
                            amounts.add(Integer.parseInt(s));
                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + s + ColorUtil.RED + " "
                                            + Lang.get("stageEditorNotGreaterThanZero"));
                            return new PlaceBlockAmountsPrompt();
                        }

                    } catch (final NumberFormatException e) {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + Lang.get("stageEditorNotListofNumbers"));
                        return new PlaceBlockAmountsPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_PLACE_AMOUNTS, amounts);

            }

            return new PlaceBlockListPrompt();

        }
    }

    private class UseBlockListPrompt extends FixedSetPrompt {

        public UseBlockListPrompt() {

            super("1", "2", "3", "4");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- Use Blocks -\n";
            if (context.getSessionData(pref + CK.S_USE_IDS) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetBlockIds") + " (" + Lang.get("noneSet") + ")\n";
                text += ColorUtil.GRAY + "2 - " + Lang.get("stageEditorSetUseAmounts") + " (" + Lang.get("noIdsSet")
                        + ")\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");
            } else {

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetBlockIds") + "\n";
                for (final Integer i: getBlockIds(context)) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + Quester.prettyItemString(i) + "\n";

                }

                if (context.getSessionData(pref + CK.S_USE_AMOUNTS) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetUseAmounts") + " (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetUseAmounts") + "\n";
                    for (final Integer i: getBlockAmounts(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + i + "\n";

                    }

                }

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");

            }

            return text;

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new UseBlockIdsPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                if (context.getSessionData(pref + CK.S_USE_IDS) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoBlockIds"));
                    return new UseBlockListPrompt();
                } else {
                    return new UseBlockAmountsPrompt();
                }
            } else if (input.equalsIgnoreCase("3")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + Lang.get("stageEditorUseBlocksCleared"));
                context.setSessionData(pref + CK.S_USE_IDS, null);
                context.setSessionData(pref + CK.S_USE_AMOUNTS, null);
                return new UseBlockListPrompt();
            } else if (input.equalsIgnoreCase("4")) {

                int one;
                int two;

                if (context.getSessionData(pref + CK.S_USE_IDS) != null) {
                    one = ((List<Integer>) context.getSessionData(pref + CK.S_USE_IDS)).size();
                } else {
                    one = 0;
                }

                if (context.getSessionData(pref + CK.S_USE_AMOUNTS) != null) {
                    two = ((List<Integer>) context.getSessionData(pref + CK.S_USE_AMOUNTS)).size();
                } else {
                    two = 0;
                }

                if (one == two) {
                    return new CreateStagePrompt(stageNum, questFactory, citizens);
                } else {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorListNotSameSize"));
                    return new UseBlockListPrompt();
                }
            }
            return null;

        }

        private List<Integer> getBlockIds(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_USE_IDS);
        }

        private List<Integer> getBlockAmounts(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_USE_AMOUNTS);
        }
    }

    private class UseBlockIdsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW + Lang.get("stageEditorEnterBlockIds");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> ids = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        if (Material.getMaterial(Integer.parseInt(s)) != null) {

                            if (ids.contains(Integer.parseInt(s)) == false) {
                                ids.add(Integer.parseInt(s));
                            } else {
                                context.getForWhom().sendRawMessage(
                                        ColorUtil.RED + " " + Lang.get("stageEditorContainsDuplicates"));
                                return new UseBlockIdsPrompt();
                            }

                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + s + ColorUtil.RED + " " + Lang.get("stageEditorInvalidBlockID"));
                            return new UseBlockIdsPrompt();
                        }

                    } catch (final NumberFormatException e) {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + Lang.get("stageEditorNotListofNumbers"));
                        return new UseBlockIdsPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_USE_IDS, ids);

            }

            return new UseBlockListPrompt();

        }
    }

    private class UseBlockAmountsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW + Lang.get("stageEditorUseBlocksPrompt");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> amounts = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        if (Integer.parseInt(s) > 0) {
                            amounts.add(Integer.parseInt(s));
                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + s + ColorUtil.RED + " "
                                            + Lang.get("stageEditorNotGreaterThanZero"));
                            return new UseBlockAmountsPrompt();
                        }

                    } catch (final NumberFormatException e) {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + Lang.get("stageEditorNotListofNumbers"));
                        return new UseBlockAmountsPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_USE_AMOUNTS, amounts);

            }

            return new UseBlockListPrompt();

        }
    }

    private class CutBlockListPrompt extends FixedSetPrompt {

        public CutBlockListPrompt() {

            super("1", "2", "3", "4");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- " + Lang.get("stageEditorCutBlocks") + " -\n";
            if (context.getSessionData(pref + CK.S_CUT_IDS) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetBlockIds") + " (" + Lang.get("noneSet") + ")\n";
                text += ColorUtil.GRAY + "2 - " + Lang.get("stageEditorSetCutAmounts") + " (" + Lang.get("noIdsSet")
                        + ")\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");
            } else {

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetBlockIds") + "\n";
                for (final Integer i: getBlockIds(context)) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + Quester.prettyItemString(i) + "\n";

                }

                if (context.getSessionData(pref + CK.S_CUT_AMOUNTS) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetCutAmounts") + " (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetCutAmounts") + "\n";
                    for (final Integer i: getBlockAmounts(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + i + "\n";

                    }

                }

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");

            }

            return text;

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new CutBlockIdsPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                if (context.getSessionData(pref + CK.S_CUT_IDS) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoBlockIds"));
                    return new CutBlockListPrompt();
                } else {
                    return new CutBlockAmountsPrompt();
                }
            } else if (input.equalsIgnoreCase("3")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + Lang.get("stageEditorCutBlocksCleared"));
                context.setSessionData(pref + CK.S_CUT_IDS, null);
                context.setSessionData(pref + CK.S_CUT_AMOUNTS, null);
                return new CutBlockListPrompt();
            } else if (input.equalsIgnoreCase("4")) {

                int one;
                int two;

                if (context.getSessionData(pref + CK.S_CUT_IDS) != null) {
                    one = ((List<Integer>) context.getSessionData(pref + CK.S_CUT_IDS)).size();
                } else {
                    one = 0;
                }

                if (context.getSessionData(pref + CK.S_CUT_AMOUNTS) != null) {
                    two = ((List<Integer>) context.getSessionData(pref + CK.S_CUT_AMOUNTS)).size();
                } else {
                    two = 0;
                }

                if (one == two) {
                    return new CreateStagePrompt(stageNum, questFactory, citizens);
                } else {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorListNotSameSize"));
                    return new CutBlockListPrompt();
                }
            }
            return null;

        }

        private List<Integer> getBlockIds(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_CUT_IDS);
        }

        private List<Integer> getBlockAmounts(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_CUT_AMOUNTS);
        }
    }

    private class CutBlockIdsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW + Lang.get("stageEditorEnterBlockIds");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> ids = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        if (Material.getMaterial(Integer.parseInt(s)) != null) {

                            if (ids.contains(Integer.parseInt(s)) == false) {
                                ids.add(Integer.parseInt(s));
                            } else {
                                context.getForWhom().sendRawMessage(
                                        ColorUtil.RED + " " + Lang.get("stageEditorListContainsDuplicates"));
                                return new CutBlockIdsPrompt();
                            }

                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + s + ColorUtil.RED + " " + Lang.get("stageEditorInvalidBlockID"));
                            return new CutBlockIdsPrompt();
                        }

                    } catch (final NumberFormatException e) {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + Lang.get("stageEditorNotListofNumbers"));
                        return new CutBlockIdsPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_CUT_IDS, ids);

            }

            return new CutBlockListPrompt();

        }
    }

    private class CutBlockAmountsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW + Lang.get("stageEditorCutBlocksPrompt");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> amounts = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        if (Integer.parseInt(s) > 0) {
                            amounts.add(Integer.parseInt(s));
                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + s + ColorUtil.RED + " "
                                            + Lang.get("stageEditorNotGreaterThanZero"));
                            return new CutBlockAmountsPrompt();
                        }

                    } catch (final NumberFormatException e) {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + Lang.get("stageEditorNotListofNumbers"));
                        return new CutBlockAmountsPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_CUT_AMOUNTS, amounts);

            }

            return new CutBlockListPrompt();

        }
    }

    private class FishPrompt extends NumericPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW + Lang.get("stageEditorCatchFishPrompt");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number number) {

            final int num = number.intValue();
            final Player player = (Player) context.getForWhom();

            if (num < -1) {
                player.sendMessage(ColorUtil.RED + Lang.get("stageEditorPositiveAmount"));
                return new FishPrompt();
            } else if (num == 0) {
                context.setSessionData(pref + CK.S_FISH, null);
            } else if (num > 0) {
                context.setSessionData(pref + CK.S_FISH, num);
            }

            return new CreateStagePrompt(stageNum, questFactory, citizens);

        }
    }

    private class KillPlayerPrompt extends NumericPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW + Lang.get("stageEditorKillPlayerPrompt");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number number) {

            final int num = number.intValue();
            final Player player = (Player) context.getForWhom();

            if (num < -1) {
                player.sendMessage(ColorUtil.RED + Lang.get("stageEditorPositiveAmount"));
                return new KillPlayerPrompt();
            } else if (num == 0) {
                context.setSessionData(pref + CK.S_PLAYER_KILL, null);
            } else if (num > 0) {
                context.setSessionData(pref + CK.S_PLAYER_KILL, num);
            }

            return new CreateStagePrompt(stageNum, questFactory, citizens);

        }
    }

    private class EnchantmentListPrompt extends FixedSetPrompt {

        public EnchantmentListPrompt() {

            super("1", "2", "3", "4", "5");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- " + Lang.get("stageEditorEnchantItems") + " -\n";
            if (context.getSessionData(pref + CK.S_ENCHANT_TYPES) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetEnchantments") + " (" + Lang.get("noneSet") + ")\n";
                text += ColorUtil.GRAY + "2 - " + Lang.get("stageEditorSetItemIds") + " ("
                        + Lang.get("stageEditorNoEnchantmentsSet") + ")\n";
                text += ColorUtil.GRAY + "3 - " + Lang.get("stageEditorSetEnchantAmounts") + " ("
                        + Lang.get("stageEditorNoEnchantmentsSet") + ")\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "5" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");
            } else {

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetEnchantments") + "\n";
                for (final String s: getEnchantTypes(context)) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + s + "\n";

                }

                if (context.getSessionData(pref + CK.S_ENCHANT_IDS) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetItemIds") + " (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetItemIds") + "\n";
                    for (final Integer i: getEnchantItems(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + Quester.prettyItemString(i) + "\n";

                    }

                }

                if (context.getSessionData(pref + CK.S_ENCHANT_AMOUNTS) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetEnchantAmounts") + " (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetEnchantAmounts") + "\n";
                    for (final int i: getEnchantAmounts(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + i + "\n";

                    }

                }

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "5" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");

            }

            return text;

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new EnchantTypesPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                if (context.getSessionData(pref + CK.S_ENCHANT_TYPES) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoEnchantments"));
                    return new EnchantmentListPrompt();
                } else {
                    return new EnchantItemsPrompt();
                }
            } else if (input.equalsIgnoreCase("3")) {
                if (context.getSessionData(pref + CK.S_ENCHANT_TYPES) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoEnchantments"));
                    return new EnchantmentListPrompt();
                } else {
                    return new EnchantAmountsPrompt();
                }
            } else if (input.equalsIgnoreCase("4")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + Lang.get("stageEditorEnchantmentsCleared"));
                context.setSessionData(pref + CK.S_ENCHANT_TYPES, null);
                context.setSessionData(pref + CK.S_ENCHANT_IDS, null);
                context.setSessionData(pref + CK.S_ENCHANT_AMOUNTS, null);
                return new EnchantmentListPrompt();
            } else if (input.equalsIgnoreCase("5")) {

                int one;
                int two;
                int three;

                if (context.getSessionData(pref + CK.S_ENCHANT_TYPES) != null) {
                    one = ((List<Integer>) context.getSessionData(pref + CK.S_ENCHANT_TYPES)).size();
                } else {
                    one = 0;
                }

                if (context.getSessionData(pref + CK.S_ENCHANT_IDS) != null) {
                    two = ((List<Integer>) context.getSessionData(pref + CK.S_ENCHANT_IDS)).size();
                } else {
                    two = 0;
                }

                if (context.getSessionData(pref + CK.S_ENCHANT_AMOUNTS) != null) {
                    three = ((List<Integer>) context.getSessionData(pref + CK.S_ENCHANT_AMOUNTS)).size();
                } else {
                    three = 0;
                }

                if (one == two && two == three) {
                    return new CreateStagePrompt(stageNum, questFactory, citizens);
                } else {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorEnchantmentNotSameSize"));
                    return new EnchantmentListPrompt();
                }
            }

            return null;

        }

        private List<String> getEnchantTypes(ConversationContext context) {
            return (List<String>) context.getSessionData(pref + CK.S_ENCHANT_TYPES);
        }

        private List<Integer> getEnchantItems(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_ENCHANT_IDS);
        }

        private List<Integer> getEnchantAmounts(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_ENCHANT_AMOUNTS);
        }
    }

    private class EnchantTypesPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.PINK + "- " + ColorUtil.PURPLE + Lang.get("stageEditorEnchantments")
                    + ColorUtil.PINK + " -\n";
            for (final Enchantment e: Enchantment.values()) {

                text += ColorUtil.GREEN + Quester.prettyEnchantmentString(e) + ", ";

            }
            text = text.substring(0, text.length() - 1);

            return text + "\n" + ColorUtil.YELLOW + Lang.get("stageEditorEnchantTypePrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(",");
                final LinkedList<String> enchs = new LinkedList<String>();
                boolean valid;
                for (String s: args) {

                    s = s.trim();
                    valid = false;
                    for (final Enchantment e: Enchantment.values()) {

                        if (Quester.prettyEnchantmentString(e).equalsIgnoreCase(s)) {

                            if (enchs.contains(s) == false) {
                                enchs.add(Quester.prettyEnchantmentString(e));
                                valid = true;
                                break;
                            } else {
                                context.getForWhom().sendRawMessage(
                                        ColorUtil.RED + " " + Lang.get("stageEditorListContainsDuplicates"));
                                return new EnchantTypesPrompt();
                            }

                        }

                    }
                    if (valid == false) {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + " " + Lang.get("stageEditorInvalidEnchantment"));
                        return new EnchantTypesPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_ENCHANT_TYPES, enchs);

            }

            return new EnchantmentListPrompt();

        }
    }

    private class EnchantItemsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW + Lang.get("stageEditorItemIDsPrompt");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> ids = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        if (Material.getMaterial(Integer.parseInt(s)) != null) {

                            if (ids.contains(Integer.parseInt(s)) == false) {
                                ids.add(Integer.parseInt(s));
                            } else {
                                context.getForWhom().sendRawMessage(
                                        ColorUtil.RED + " " + Lang.get("stageEditorListContainsDuplicates"));
                                return new EnchantItemsPrompt();
                            }

                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + s + ColorUtil.RED + " " + Lang.get("stageEditorInvalidItemID"));
                            return new EnchantItemsPrompt();
                        }

                    } catch (final NumberFormatException e) {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + Lang.get("stageEditorNotListofNumbers"));
                        return new EnchantItemsPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_ENCHANT_IDS, ids);

            }

            return new EnchantmentListPrompt();

        }
    }

    private class EnchantAmountsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW + Lang.get("stageEditorEnchantAmountsPrompt");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> amounts = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        if (Integer.parseInt(s) > 0) {
                            amounts.add(Integer.parseInt(s));
                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + s + ColorUtil.RED + " "
                                            + Lang.get("stageEditorNotGreaterThanZero"));
                            return new EnchantAmountsPrompt();
                        }

                    } catch (final NumberFormatException e) {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + Lang.get("stageEditorNotListofNumbers"));
                        return new EnchantAmountsPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_ENCHANT_AMOUNTS, amounts);

            }

            return new EnchantmentListPrompt();

        }
    }

    private class DeliveryListPrompt extends FixedSetPrompt {

        public DeliveryListPrompt() {

            super("1", "2", "3", "4", "5");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            // Check/add newly made item
            if (context.getSessionData("newItem") != null) {
                if (context.getSessionData(pref + CK.S_DELIVERY_ITEMS) != null) {
                    final List<ItemStack> itemRews = getItems(context);
                    itemRews.add((ItemStack) context.getSessionData("tempStack"));
                    context.setSessionData(pref + CK.S_DELIVERY_ITEMS, itemRews);
                } else {
                    final LinkedList<ItemStack> itemRews = new LinkedList<ItemStack>();
                    itemRews.add((ItemStack) context.getSessionData("tempStack"));
                    context.setSessionData(pref + CK.S_DELIVERY_ITEMS, itemRews);
                }

                context.setSessionData("newItem", null);
                context.setSessionData("tempStack", null);

            }

            String text = ColorUtil.GOLD + "- " + Lang.get("stageEditorDeliverItems") + " -\n";
            if (context.getSessionData(pref + CK.S_DELIVERY_ITEMS) == null) {
                text += ColorUtil.GRAY + " (" + Lang.get("noneSet") + ")\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorDeliveryAddItem") + "\n";
                text += ColorUtil.GRAY + "2 - " + Lang.get("stageEditorDeliveryNPCs") + " ("
                        + Lang.get("stageEditorNoItemsSet") + ")\n";
                if (context.getSessionData(pref + CK.S_DELIVERY_MESSAGES) == null) {
                    text += ColorUtil.BLUE + "3 - " + Lang.get("stageEditorDeliveryMessages") + " ("
                            + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "3 - " + Lang.get("stageEditorDeliveryMessages") + "\n";
                    for (final String s: getDeliveryMessages(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + "\"" + s + "\"";

                    }

                }

            } else {

                for (final ItemStack is: getItems(context)) {

                    text += ColorUtil.GRAY + "    - " + ItemUtil.getDisplayString(is) + "\n";

                }

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorDeliveryAddItem") + "\n";

                if (context.getSessionData(pref + CK.S_DELIVERY_NPCS) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorDeliveryNPCs") + " (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorDeliveryNPCs") + "\n";
                    for (final int i: getDeliveryNPCs(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + i + " ("
                                + citizens.getNPCRegistry().getById(i).getName() + ")\n";

                    }

                }

                if (context.getSessionData(pref + CK.S_DELIVERY_MESSAGES) == null) {
                    text += ColorUtil.BLUE + "3 - Set delivery messages (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "3 - " + Lang.get("stageEditorDeliveryMessages") + "\n";
                    for (final String s: getDeliveryMessages(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + "\"" + s + "\"\n";

                    }

                }

            }

            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                    + Lang.get("clear") + "\n";
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "5" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                    + Lang.get("done");

            return text;

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new ItemStackPrompt(DeliveryListPrompt.this);
            } else if (input.equalsIgnoreCase("2")) {
                if (context.getSessionData(pref + CK.S_DELIVERY_ITEMS) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoItems"));
                    return new DeliveryListPrompt();
                } else {
                    return new DeliveryNPCsPrompt();
                }
            } else if (input.equalsIgnoreCase("3")) {
                return new DeliveryMessagesPrompt();
            } else if (input.equalsIgnoreCase("4")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + Lang.get("stageEditorDeliveriesCleared"));
                context.setSessionData(pref + CK.S_DELIVERY_ITEMS, null);
                context.setSessionData(pref + CK.S_DELIVERY_NPCS, null);
                context.setSessionData(pref + CK.S_DELIVERY_MESSAGES, null);
                return new DeliveryListPrompt();
            } else if (input.equalsIgnoreCase("5")) {

                int one;
                int two;

                if (context.getSessionData(pref + CK.S_DELIVERY_ITEMS) != null) {
                    one = ((List<ItemStack>) context.getSessionData(pref + CK.S_DELIVERY_ITEMS)).size();
                } else {
                    one = 0;
                }

                if (context.getSessionData(pref + CK.S_DELIVERY_NPCS) != null) {
                    two = ((List<Integer>) context.getSessionData(pref + CK.S_DELIVERY_NPCS)).size();
                } else {
                    two = 0;
                }

                if (one == two) {

                    if (context.getSessionData(pref + CK.S_DELIVERY_MESSAGES) == null && one != 0) {
                        context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoDeliveryMessage"));
                        return new DeliveryListPrompt();
                    } else {
                        return new CreateStagePrompt(stageNum, questFactory, citizens);
                    }

                } else {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorDeliveriesNotSameSize"));
                    return new DeliveryListPrompt();
                }
            }

            return null;

        }

        private List<ItemStack> getItems(ConversationContext context) {
            return (List<ItemStack>) context.getSessionData(pref + CK.S_DELIVERY_ITEMS);
        }

        private List<Integer> getDeliveryNPCs(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_DELIVERY_NPCS);
        }

        private List<String> getDeliveryMessages(ConversationContext context) {
            return (List<String>) context.getSessionData(pref + CK.S_DELIVERY_MESSAGES);
        }
    }

    private class DeliveryNPCsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            questFactory.selectingNPCs.add((Player) context.getForWhom());
            return ColorUtil.YELLOW + Lang.get("stageEditorNPCPrompt") + "\n" + ColorUtil.GOLD + Lang.get("npcHint");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> npcs = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        final Integer i = Integer.parseInt(s);

                        if (citizens.getNPCRegistry().getById(i) != null) {
                            npcs.add(i);
                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + "" + i + ColorUtil.RED + " " + Lang.get("stageEditorInvalidNPC"));
                            return new DeliveryNPCsPrompt();
                        }

                    } catch (final NumberFormatException e) {

                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + Lang.get("stageEditorNotListofNumbers"));
                        return new DeliveryNPCsPrompt();

                    }

                }

                context.setSessionData(pref + CK.S_DELIVERY_NPCS, npcs);

            }

            questFactory.selectingNPCs.remove(context.getForWhom());
            return new DeliveryListPrompt();

        }
    }

    private class DeliveryMessagesPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            final String note = ColorUtil.GOLD + Lang.get("stageEditorNPCNote");
            return ColorUtil.YELLOW + Lang.get("stageEditorDeliveryMessagesPrompt") + ".\n" + note;
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(";");
                final LinkedList<String> messages = new LinkedList<String>();
                messages.addAll(Arrays.asList(args));

                context.setSessionData(pref + CK.S_DELIVERY_MESSAGES, messages);

            }

            return new DeliveryListPrompt();

        }
    }

    private class NPCIDsToTalkToPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            questFactory.selectingNPCs.add((Player) context.getForWhom());
            return ColorUtil.YELLOW + Lang.get("stageEditorNPCToTalkToPrompt") + "\n" + ColorUtil.GOLD
                    + Lang.get("npcHint");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
                    && input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> npcs = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        final Integer i = Integer.parseInt(s);

                        if (citizens.getNPCRegistry().getById(i) != null) {
                            npcs.add(i);
                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + "" + i + ColorUtil.RED + " " + Lang.get("stageEditorInvalidNPC"));
                            return new NPCIDsToTalkToPrompt();
                        }

                    } catch (final NumberFormatException e) {

                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + Lang.get("stageEditorNotListofNumbers"));
                        return new NPCIDsToTalkToPrompt();

                    }

                }

                questFactory.selectingNPCs.remove(context.getForWhom());
                context.setSessionData(pref + CK.S_NPCS_TO_TALK_TO, npcs);

            } else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {

                context.setSessionData(pref + CK.S_NPCS_TO_TALK_TO, null);

            }

            return new CreateStagePrompt(stageNum, questFactory, citizens);

        }
    }

    private class NPCKillListPrompt extends FixedSetPrompt {

        public NPCKillListPrompt() {

            super("1", "2", "3", "4");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- " + Lang.get("stageEditorKillNPCs") + " -\n";
            if (context.getSessionData(pref + CK.S_NPCS_TO_KILL) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetKillIds") + " (" + Lang.get("noneSet") + ")\n";
                text += ColorUtil.GRAY + "2 - " + Lang.get("stageEditorSetKillAmounts") + " (" + Lang.get("noIdsSet")
                        + ")\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("cancel") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");
            } else {

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetKillIds") + "\n";
                for (final Integer i: getNPCIds(context)) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + citizens.getNPCRegistry().getById(i).getName()
                            + ColorUtil.DARKAQUA + " (" + i + ")\n";

                }

                if (context.getSessionData(pref + CK.S_NPCS_TO_KILL_AMOUNTS) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetKillAmounts") + " (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetKillAmounts") + "\n";
                    for (final Integer i: getKillAmounts(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.BLUE + i + "\n";

                    }

                }

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("cancel") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");

            }

            return text;

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new NpcIdsToKillPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                if (context.getSessionData(pref + CK.S_NPCS_TO_KILL) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoNPCs"));
                    return new NPCKillListPrompt();
                } else {
                    return new NpcAmountsToKillPrompt();
                }
            } else if (input.equalsIgnoreCase("3")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + Lang.get("stageEditorKillNPCsCleared"));
                context.setSessionData(pref + CK.S_NPCS_TO_KILL, null);
                context.setSessionData(pref + CK.S_NPCS_TO_KILL_AMOUNTS, null);
                return new NPCKillListPrompt();
            } else if (input.equalsIgnoreCase("4")) {

                int one;
                int two;

                if (context.getSessionData(pref + CK.S_NPCS_TO_KILL) != null) {
                    one = ((List<Integer>) context.getSessionData(pref + CK.S_NPCS_TO_KILL)).size();
                } else {
                    one = 0;
                }

                if (context.getSessionData(pref + CK.S_NPCS_TO_KILL_AMOUNTS) != null) {
                    two = ((List<Integer>) context.getSessionData(pref + CK.S_NPCS_TO_KILL_AMOUNTS)).size();
                } else {
                    two = 0;
                }

                if (one == two) {
                    return new CreateStagePrompt(stageNum, questFactory, citizens);
                } else {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNPCKillsNotSameSize"));
                    return new NPCKillListPrompt();
                }
            }
            return null;

        }

        private List<Integer> getNPCIds(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_NPCS_TO_KILL);
        }

        private List<Integer> getKillAmounts(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_NPCS_TO_KILL_AMOUNTS);
        }
    }

    private class NpcIdsToKillPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            questFactory.selectingNPCs.add((Player) context.getForWhom());
            return ColorUtil.YELLOW + Lang.get("stageEditorNPCPrompt") + "\n" + ColorUtil.GOLD + Lang.get("npcHint");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> npcs = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        final Integer i = Integer.parseInt(s);

                        if (citizens.getNPCRegistry().getById(i) != null) {
                            npcs.add(i);
                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + "" + i + ColorUtil.RED + " " + Lang.get("stageEditorInvalidNPC"));
                            return new NpcIdsToKillPrompt();
                        }

                    } catch (final NumberFormatException e) {

                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + Lang.get("stageEditorNotListofNumbers"));
                        return new NpcIdsToKillPrompt();

                    }

                }

                context.setSessionData(pref + CK.S_NPCS_TO_KILL, npcs);

            }

            questFactory.selectingNPCs.remove(context.getForWhom());
            return new NPCKillListPrompt();

        }
    }

    private class NpcAmountsToKillPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW + Lang.get("stageEditorKillNPCsPrompt");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> amounts = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        if (Integer.parseInt(s) > 0) {
                            amounts.add(Integer.parseInt(s));
                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + s + ColorUtil.RED + " "
                                            + Lang.get("stageEditorNotGreaterThanZero"));
                            return new NpcAmountsToKillPrompt();
                        }

                    } catch (final NumberFormatException e) {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + Lang.get("stageEditorNotListofNumbers"));
                        return new NpcAmountsToKillPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_NPCS_TO_KILL_AMOUNTS, amounts);

            }

            return new NPCKillListPrompt();

        }
    }

    private class MobListPrompt extends FixedSetPrompt {

        public MobListPrompt() {

            super("1", "2", "3", "4", "5", "6", "7");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- " + Lang.get("stageEditorKillMobs") + " -\n";
            if (context.getSessionData(pref + CK.S_MOB_TYPES) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetMobTypes") + " (" + Lang.get("noneSet") + ")\n";
                text += ColorUtil.GRAY + "2 - " + Lang.get("stageEditorSetMobAmounts") + " ("
                        + Lang.get("stageEditorNoMobTypesSet") + ")\n";
                text += ColorUtil.DARKGRAY + "|---------" + Lang.get("stageEditorOptional") + "---------|\n";
                text += ColorUtil.GRAY + "3 - " + Lang.get("stageEditorSetKillLocations") + " ("
                        + Lang.get("stageEditorNoMobTypesSet") + ")\n";
                text += ColorUtil.GRAY + "4 - " + Lang.get("stageEditorSetKillLocationRadii") + " ("
                        + Lang.get("stageEditorNoMobTypesSet") + ")\n";
                text += ColorUtil.GRAY + "5 - " + Lang.get("stageEditorSetKillLocationNames") + " ("
                        + Lang.get("stageEditorNoMobTypesSet") + ")\n";
                text += ColorUtil.DARKGRAY + "|--------------------------|\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "6" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "7" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");
            } else {

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetMobTypes") + "\n";
                for (final String s: getMobTypes(context)) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + s + "\n";

                }

                if (context.getSessionData(pref + CK.S_MOB_AMOUNTS) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetMobAmounts") + " (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetMobAmounts") + "\n";
                    for (final Integer i: getMobAmounts(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + i + "\n";

                    }

                }

                text += ColorUtil.DARKGRAY + "|---------" + Lang.get("stageEditorOptional") + "---------|\n";

                if (context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetKillLocations") + " (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetKillLocations") + "\n";
                    for (final String s: getKillLocations(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + s + "\n";

                    }

                }

                if (context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS_RADIUS) == null) {
                    text += ColorUtil.BLUE + "4 - " + Lang.get("stageEditorSetKillLocationRadii") + " ("
                            + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "4 - " + Lang.get("stageEditorSetKillLocationRadii") + "\n";
                    for (final int i: getKillRadii(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + i + "\n";

                    }

                }

                if (context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS_NAMES) == null) {
                    text += ColorUtil.BLUE + "5 - " + Lang.get("stageEditorSetKillLocationNames") + " ("
                            + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "5 - " + Lang.get("stageEditorSetKillLocationNames") + "\n";
                    for (final String s: getKillLocationNames(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + s + "\n";

                    }

                }

                text += ColorUtil.DARKGRAY + "|--------------------------|\n";

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "6" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "7" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");

            }

            return text;

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new MobTypesPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                if (context.getSessionData(pref + CK.S_MOB_TYPES) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoMobTypes"));
                    return new MobListPrompt();
                } else {
                    return new MobAmountsPrompt();
                }
            } else if (input.equalsIgnoreCase("3")) {
                if (context.getSessionData(pref + CK.S_MOB_TYPES) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoMobTypes"));
                    return new MobListPrompt();
                } else {
                    questFactory.selectedKillLocations.put((Player) context.getForWhom(), null);
                    return new MobLocationPrompt();
                }
            } else if (input.equalsIgnoreCase("4")) {
                if (context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoKillLocations"));
                    return new MobListPrompt();
                } else {
                    return new MobRadiiPrompt();
                }
            } else if (input.equalsIgnoreCase("5")) {
                if (context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoKillLocations"));
                    return new MobListPrompt();
                } else {
                    return new MobLocationNamesPrompt();
                }
            } else if (input.equalsIgnoreCase("6")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + Lang.get("stageEditorKillMobsCleared"));
                context.setSessionData(pref + CK.S_MOB_TYPES, null);
                context.setSessionData(pref + CK.S_MOB_AMOUNTS, null);
                context.setSessionData(pref + CK.S_MOB_KILL_LOCATIONS, null);
                context.setSessionData(pref + CK.S_MOB_KILL_LOCATIONS_RADIUS, null);
                context.setSessionData(pref + CK.S_MOB_KILL_LOCATIONS_NAMES, null);
                return new MobListPrompt();
            } else if (input.equalsIgnoreCase("7")) {

                int one;
                int two;

                int three;
                int four;
                int five;

                if (context.getSessionData(pref + CK.S_MOB_TYPES) != null) {
                    one = ((List<String>) context.getSessionData(pref + CK.S_MOB_TYPES)).size();
                } else {
                    one = 0;
                }

                if (context.getSessionData(pref + CK.S_MOB_AMOUNTS) != null) {
                    two = ((List<Integer>) context.getSessionData(pref + CK.S_MOB_AMOUNTS)).size();
                } else {
                    two = 0;
                }

                if (context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS) != null) {
                    three = ((List<String>) context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS)).size();
                } else {
                    three = 0;
                }

                if (context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS_RADIUS) != null) {
                    four = ((List<Integer>) context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS_RADIUS)).size();
                } else {
                    four = 0;
                }

                if (context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS_NAMES) != null) {
                    five = ((List<String>) context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS_NAMES)).size();
                } else {
                    five = 0;
                }

                if (one == two) {

                    if (three != 0 || four != 0 || five != 0) {

                        if (two == three && three == four && four == five) {
                            return new CreateStagePrompt(stageNum, questFactory, citizens);
                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.RED + Lang.get("stageEditorAllListsNotSameSize"));
                            return new MobListPrompt();
                        }

                    } else {
                        return new CreateStagePrompt(stageNum, questFactory, citizens);
                    }

                } else {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorMobTypesNotSameSize"));
                    return new MobListPrompt();
                }

            }

            return null;

        }

        private List<String> getMobTypes(ConversationContext context) {
            return (List<String>) context.getSessionData(pref + CK.S_MOB_TYPES);
        }

        private List<Integer> getMobAmounts(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_MOB_AMOUNTS);
        }

        private List<String> getKillLocations(ConversationContext context) {
            return (List<String>) context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS);
        }

        private List<Integer> getKillRadii(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS_RADIUS);
        }

        private List<String> getKillLocationNames(ConversationContext context) {
            return (List<String>) context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS_NAMES);
        }
    }

    private class MobTypesPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String mobs = ColorUtil.PINK + "- Mobs - \n";
            mobs += ColorUtil.PURPLE + "Bat, ";
            mobs += ColorUtil.PURPLE + "Blaze, ";
            mobs += ColorUtil.PURPLE + "CaveSpider, ";
            mobs += ColorUtil.PURPLE + "Chicken, ";
            mobs += ColorUtil.PURPLE + "Cow, ";
            mobs += ColorUtil.PURPLE + "Creeper, ";
            mobs += ColorUtil.PURPLE + "Enderman, ";
            mobs += ColorUtil.PURPLE + "EnderDragon, ";
            mobs += ColorUtil.PURPLE + "Ghast, ";
            mobs += ColorUtil.PURPLE + "Giant, ";
            mobs += ColorUtil.PURPLE + "Horse, ";
            mobs += ColorUtil.PURPLE + "IronGolem, ";
            mobs += ColorUtil.PURPLE + "MagmaCube, ";
            mobs += ColorUtil.PURPLE + "MushroomCow, ";
            mobs += ColorUtil.PURPLE + "Ocelot, ";
            mobs += ColorUtil.PURPLE + "Pig, ";
            mobs += ColorUtil.PURPLE + "PigZombie, ";
            mobs += ColorUtil.PURPLE + "Sheep, ";
            mobs += ColorUtil.PURPLE + "Silverfish, ";
            mobs += ColorUtil.PURPLE + "Skeleton, ";
            mobs += ColorUtil.PURPLE + "Slime, ";
            mobs += ColorUtil.PURPLE + "Snowman, ";
            mobs += ColorUtil.PURPLE + "Spider, ";
            mobs += ColorUtil.PURPLE + "Squid, ";
            mobs += ColorUtil.PURPLE + "Villager, ";
            mobs += ColorUtil.PURPLE + "Witch, ";
            mobs += ColorUtil.PURPLE + "Wither, ";
            mobs += ColorUtil.PURPLE + "Wolf, ";
            mobs += ColorUtil.PURPLE + "Zombie\n";

            return mobs + ColorUtil.YELLOW + Lang.get("stageEditorMobsPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final LinkedList<String> mobTypes = new LinkedList<String>();
                for (final String s: input.split(" ")) {

                    if (Quests.getMobType(s) != null) {

                        mobTypes.add(s);
                        context.setSessionData(pref + CK.S_MOB_TYPES, mobTypes);

                    } else {
                        player.sendMessage(ColorUtil.PINK + s + " " + ColorUtil.RED + Lang.get("stageEditorInvalidMob"));
                        return new MobTypesPrompt();
                    }

                }

            }

            return new MobListPrompt();

        }
    }

    private class MobAmountsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW + Lang.get("stageEditorMobAmountsPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final LinkedList<Integer> mobAmounts = new LinkedList<Integer>();
                for (final String s: input.split(" ")) {

                    try {

                        final int i = Integer.parseInt(s);

                        if (i < 1) {
                            player.sendMessage(ColorUtil.PINK + input + " " + ColorUtil.RED
                                    + Lang.get("stageEditorNotGreaterThanZero"));
                            return new MobAmountsPrompt();
                        }

                        mobAmounts.add(i);

                    } catch (final NumberFormatException e) {
                        player.sendMessage(ColorUtil.PINK + input + " " + ColorUtil.RED
                                + Lang.get("stageEditorInvalidNumber"));
                        return new MobAmountsPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_MOB_AMOUNTS, mobAmounts);

            }

            return new MobListPrompt();

        }
    }

    private class MobLocationPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW + Lang.get("stageEditorMobLocationPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdAdd"))) {

                final Block block = questFactory.selectedKillLocations.get(player);
                if (block != null) {

                    final Location loc = block.getLocation();

                    LinkedList<String> locs;
                    if (context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS) != null) {
                        locs = (LinkedList<String>) context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS);
                    } else {
                        locs = new LinkedList<String>();
                    }

                    locs.add(Quests.getLocationInfo(loc));
                    context.setSessionData(pref + CK.S_MOB_KILL_LOCATIONS, locs);
                    questFactory.selectedKillLocations.remove(player);

                } else {
                    player.sendMessage(ColorUtil.RED + Lang.get("stageEditorNoBlock"));
                    return new MobLocationPrompt();
                }

                return new MobListPrompt();

            } else if (input.equalsIgnoreCase(Lang.get("cmdCancel"))) {

                questFactory.selectedKillLocations.remove(player);
                return new MobListPrompt();

            } else {
                return new MobLocationPrompt();
            }

        }
    }

    private class MobRadiiPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW + Lang.get("stageEditorMobLocationRadiiPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final LinkedList<Integer> radii = new LinkedList<Integer>();
                for (final String s: input.split(" ")) {

                    try {

                        final int i = Integer.parseInt(s);

                        if (i < 1) {
                            player.sendMessage(ColorUtil.PINK + input + " " + ColorUtil.RED
                                    + Lang.get("stageEditorNotGreaterThanZero"));
                            return new MobRadiiPrompt();
                        }

                        radii.add(i);

                    } catch (final NumberFormatException e) {
                        player.sendMessage(ColorUtil.PINK + input + " " + ColorUtil.RED
                                + Lang.get("stageEditorInvalidItemID"));
                        return new MobRadiiPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_MOB_KILL_LOCATIONS_RADIUS, radii);

            }

            return new MobListPrompt();

        }
    }

    private class MobLocationNamesPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW + Lang.get("stageEditorMobLocationNamesPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final LinkedList<String> locNames = new LinkedList<String>();
                locNames.addAll(Arrays.asList(input.split(",")));

                context.setSessionData(pref + CK.S_MOB_KILL_LOCATIONS_NAMES, locNames);

            }

            return new MobListPrompt();

        }
    }

    private class ReachListPrompt extends FixedSetPrompt {

        public ReachListPrompt() {

            super("1", "2", "3", "4", "5");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- " + Lang.get("stageEditorReachLocs") + " -\n";
            if (context.getSessionData(pref + CK.S_REACH_LOCATIONS) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetLocations") + " (" + Lang.get("noneSet") + ")\n";
                text += ColorUtil.GRAY + "2 - " + Lang.get("stageEditorSetLocationRadii") + " ("
                        + Lang.get("stageEditorNoLocationsSet") + ")\n";
                text += ColorUtil.GRAY + "3 - " + Lang.get("stageEditorSetLocationNames") + " ("
                        + Lang.get("stageEditorNoLocationsSet") + ")\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "5" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");
            } else {

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetLocations") + "\n";
                for (final String s: getLocations(context)) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.DARKAQUA + s + "\n";

                }

                if (context.getSessionData(pref + CK.S_REACH_LOCATIONS_RADIUS) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetLocationRadii") + " (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetLocationRadii") + "\n";
                    for (final Integer i: getLocationRadii(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + i + "\n";

                    }

                }

                if (context.getSessionData(pref + CK.S_REACH_LOCATIONS_NAMES) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetLocationNames") + " (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetLocationNames") + "\n";
                    for (final String s: getLocationNames(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + s + "\n";

                    }

                }

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "5" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");

            }

            return text;

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                questFactory.selectedReachLocations.put((Player) context.getForWhom(), null);
                return new ReachLocationPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                if (context.getSessionData(pref + CK.S_REACH_LOCATIONS) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + "You must set locations first!");
                    return new ReachListPrompt();
                } else {
                    return new ReachRadiiPrompt();
                }
            } else if (input.equalsIgnoreCase("3")) {
                if (context.getSessionData(pref + CK.S_REACH_LOCATIONS) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + "You must set locations first!");
                    return new ReachListPrompt();
                } else {
                    return new ReachNamesPrompt();
                }
            } else if (input.equalsIgnoreCase("4")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Reach Locations objective cleared.");
                context.setSessionData(pref + CK.S_REACH_LOCATIONS, null);
                context.setSessionData(pref + CK.S_REACH_LOCATIONS_RADIUS, null);
                context.setSessionData(pref + CK.S_REACH_LOCATIONS_NAMES, null);
                return new ReachListPrompt();
            } else if (input.equalsIgnoreCase("5")) {

                int one;
                int two;
                int three;

                if (context.getSessionData(pref + CK.S_REACH_LOCATIONS) != null) {
                    one = ((List<String>) context.getSessionData(pref + CK.S_REACH_LOCATIONS)).size();
                } else {
                    one = 0;
                }

                if (context.getSessionData(pref + CK.S_REACH_LOCATIONS_RADIUS) != null) {
                    two = ((List<Integer>) context.getSessionData(pref + CK.S_REACH_LOCATIONS_RADIUS)).size();
                } else {
                    two = 0;
                }

                if (context.getSessionData(pref + CK.S_REACH_LOCATIONS_NAMES) != null) {
                    three = ((List<String>) context.getSessionData(pref + CK.S_REACH_LOCATIONS_NAMES)).size();
                } else {
                    three = 0;
                }

                if (one == two && two == three) {
                    return new CreateStagePrompt(stageNum, questFactory, citizens);
                } else {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + "All of your lists are not the same size!");
                    return new ReachListPrompt();
                }

            } else {
                return new ReachListPrompt();
            }

        }

        private List<String> getLocations(ConversationContext context) {
            return (List<String>) context.getSessionData(pref + CK.S_REACH_LOCATIONS);
        }

        private List<Integer> getLocationRadii(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_REACH_LOCATIONS_RADIUS);
        }

        private List<String> getLocationNames(ConversationContext context) {
            return (List<String>) context.getSessionData(pref + CK.S_REACH_LOCATIONS_NAMES);
        }
    }

    private class ReachLocationPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW + Lang.get("stageEditorReachLocationPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdAdd"))) {

                final Block block = questFactory.selectedReachLocations.get(player);
                if (block != null) {

                    final Location loc = block.getLocation();

                    LinkedList<String> locs;
                    if (context.getSessionData(pref + CK.S_REACH_LOCATIONS) != null) {
                        locs = (LinkedList<String>) context.getSessionData(pref + CK.S_REACH_LOCATIONS);
                    } else {
                        locs = new LinkedList<String>();
                    }

                    locs.add(Quests.getLocationInfo(loc));
                    context.setSessionData(pref + CK.S_REACH_LOCATIONS, locs);
                    questFactory.selectedReachLocations.remove(player);

                } else {
                    player.sendMessage(ColorUtil.RED + Lang.get("stageEditorNoBlockSelected"));
                    return new ReachLocationPrompt();
                }

                return new ReachListPrompt();

            } else if (input.equalsIgnoreCase(Lang.get("cmdCancel"))) {

                questFactory.selectedReachLocations.remove(player);
                return new ReachListPrompt();

            } else {
                return new ReachLocationPrompt();
            }

        }
    }

    private class ReachRadiiPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW + Lang.get("stageEditorReachLocationRadiiPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final LinkedList<Integer> radii = new LinkedList<Integer>();
                for (final String s: input.split(" ")) {

                    try {

                        final int i = Integer.parseInt(s);

                        if (i < 1) {
                            player.sendMessage(ColorUtil.PINK + input + " " + ColorUtil.RED
                                    + Lang.get("stageEditorNotGreaterThanZero"));
                            return new ReachRadiiPrompt();
                        }

                        radii.add(i);

                    } catch (final NumberFormatException e) {
                        player.sendMessage(ColorUtil.PINK + input + " " + ColorUtil.RED
                                + Lang.get("stageEditorInvalidNumber"));
                        return new ReachRadiiPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_REACH_LOCATIONS_RADIUS, radii);

            }

            return new ReachListPrompt();

        }
    }

    private class ReachNamesPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW + Lang.get("stageEditorReachLocationNamesPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final LinkedList<String> locNames = new LinkedList<String>();
                locNames.addAll(Arrays.asList(input.split(",")));

                context.setSessionData(pref + CK.S_REACH_LOCATIONS_NAMES, locNames);

            }

            return new ReachListPrompt();

        }
    }

    private class TameListPrompt extends FixedSetPrompt {

        public TameListPrompt() {

            super("1", "2", "3", "4");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- " + Lang.get("stageEditorTameMobs") + " -\n";
            if (context.getSessionData(pref + CK.S_TAME_TYPES) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetMobTypes") + " (" + Lang.get("noneSet") + ")\n";
                text += ColorUtil.GRAY + "2 - " + Lang.get("stageEditorSetTameAmounts") + " ("
                        + Lang.get("stageEditorNoMobTypesSet") + ")\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");
            } else {

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetMobTypes") + "\n";
                for (final String s: getTameTypes(context)) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + s + "\n";

                }

                if (context.getSessionData(pref + CK.S_TAME_AMOUNTS) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetTameAmounts") + " (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetTameAmounts") + "\n";
                    for (final Integer i: getTameAmounts(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + i + "\n";

                    }

                }

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");

            }

            return text;

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new TameTypesPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                if (context.getSessionData(pref + CK.S_TAME_TYPES) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoMobTypes"));
                    return new TameListPrompt();
                } else {
                    return new TameAmountsPrompt();
                }
            } else if (input.equalsIgnoreCase("3")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + Lang.get("stageEditorTameCleared"));
                context.setSessionData(pref + CK.S_TAME_TYPES, null);
                context.setSessionData(pref + CK.S_TAME_AMOUNTS, null);
                return new TameListPrompt();
            } else if (input.equalsIgnoreCase("4")) {

                int one;
                int two;

                if (context.getSessionData(pref + CK.S_TAME_TYPES) != null) {
                    one = ((List<String>) context.getSessionData(pref + CK.S_TAME_TYPES)).size();
                } else {
                    one = 0;
                }

                if (context.getSessionData(pref + CK.S_TAME_AMOUNTS) != null) {
                    two = ((List<Integer>) context.getSessionData(pref + CK.S_TAME_AMOUNTS)).size();
                } else {
                    two = 0;
                }

                if (one == two) {
                    return new CreateStagePrompt(stageNum, questFactory, citizens);
                } else {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorTameMobsNotSameSize"));
                    return new TameListPrompt();
                }

            }

            return null;

        }

        private List<String> getTameTypes(ConversationContext context) {
            return (List<String>) context.getSessionData(pref + CK.S_TAME_TYPES);
        }

        private List<Integer> getTameAmounts(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_TAME_AMOUNTS);
        }
    }

    private class TameTypesPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String mobs = ColorUtil.PINK + "- Mobs - \n";
            mobs += ColorUtil.PURPLE + "Bat, ";
            mobs += ColorUtil.PURPLE + "Blaze, ";
            mobs += ColorUtil.PURPLE + "CaveSpider, ";
            mobs += ColorUtil.PURPLE + "Chicken, ";
            mobs += ColorUtil.PURPLE + "Cow, ";
            mobs += ColorUtil.PURPLE + "Creeper, ";
            mobs += ColorUtil.PURPLE + "Enderman, ";
            mobs += ColorUtil.PURPLE + "EnderDragon, ";
            mobs += ColorUtil.PURPLE + "Ghast, ";
            mobs += ColorUtil.PURPLE + "Giant, ";
            mobs += ColorUtil.PURPLE + "IronGolem, ";
            mobs += ColorUtil.PURPLE + "MagmaCube, ";
            mobs += ColorUtil.PURPLE + "MushroomCow, ";
            mobs += ColorUtil.PURPLE + "Ocelot, ";
            mobs += ColorUtil.PURPLE + "Pig, ";
            mobs += ColorUtil.PURPLE + "PigZombie, ";
            mobs += ColorUtil.PURPLE + "Sheep, ";
            mobs += ColorUtil.PURPLE + "Silverfish, ";
            mobs += ColorUtil.PURPLE + "Skeleton, ";
            mobs += ColorUtil.PURPLE + "Slime, ";
            mobs += ColorUtil.PURPLE + "Snowman, ";
            mobs += ColorUtil.PURPLE + "Spider, ";
            mobs += ColorUtil.PURPLE + "Squid, ";
            mobs += ColorUtil.PURPLE + "Villager, ";
            mobs += ColorUtil.PURPLE + "Witch, ";
            mobs += ColorUtil.PURPLE + "Wither, ";
            mobs += ColorUtil.PURPLE + "Wolf, ";
            mobs += ColorUtil.PURPLE + "Zombie\n";

            return mobs + ColorUtil.YELLOW + Lang.get("stageEditorMobsPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final LinkedList<String> mobTypes = new LinkedList<String>();
                for (final String s: input.split(" ")) {

                    if (Quests.getMobType(s) != null) {

                        mobTypes.add(Quester.prettyMobString(Quests.getMobType(s)));
                        context.setSessionData(pref + CK.S_TAME_TYPES, mobTypes);

                    } else {
                        player.sendMessage(ColorUtil.PINK + s + " " + ColorUtil.RED + Lang.get("stageEditorInvalidMob"));
                        return new TameTypesPrompt();
                    }

                }

            }

            return new TameListPrompt();

        }
    }

    private class TameAmountsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW + Lang.get("stageEditorTameAmountsPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final LinkedList<Integer> mobAmounts = new LinkedList<Integer>();
                for (final String s: input.split(" ")) {

                    try {

                        final int i = Integer.parseInt(s);

                        if (i < 1) {
                            player.sendMessage(ColorUtil.PINK + input + " " + ColorUtil.RED
                                    + Lang.get("stageEditorNotGreaterThanZero"));
                            return new TameAmountsPrompt();
                        }

                        mobAmounts.add(i);

                    } catch (final NumberFormatException e) {
                        player.sendMessage(ColorUtil.PINK + input + " " + ColorUtil.RED
                                + Lang.get("stageEditorInvalidNumber"));
                        return new TameAmountsPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_TAME_AMOUNTS, mobAmounts);

            }

            return new TameListPrompt();

        }
    }

    private class ShearListPrompt extends FixedSetPrompt {

        public ShearListPrompt() {

            super("1", "2", "3", "4");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- " + Lang.get("stageEditorShearSheep") + " -\n";
            if (context.getSessionData(pref + CK.S_SHEAR_COLORS) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetShearColors") + " (" + Lang.get("noneSet") + ")\n";
                text += ColorUtil.GRAY + "2 - " + Lang.get("stageEditorSetShearAmounts") + " ("
                        + Lang.get("stageEditorNoColorsSet") + ")\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");
            } else {

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorSetShearColors") + "\n";
                for (final String s: getShearColors(context)) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + s + "\n";

                }

                if (context.getSessionData(pref + CK.S_SHEAR_AMOUNTS) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetShearAmounts") + " (" + Lang.get("noneSet") + ")\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("stageEditorSetShearAmounts") + "\n";
                    for (final Integer i: getShearAmounts(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + i + "\n";

                    }

                }

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("clear") + "\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("done");

            }

            return text;

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new ShearColorsPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                if (context.getSessionData(pref + CK.S_SHEAR_COLORS) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorNoColors"));
                    return new ShearListPrompt();
                } else {
                    return new ShearAmountsPrompt();
                }
            } else if (input.equalsIgnoreCase("3")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + Lang.get("stageEditorShearCleared"));
                context.setSessionData(pref + CK.S_SHEAR_COLORS, null);
                context.setSessionData(pref + CK.S_SHEAR_AMOUNTS, null);
                return new ShearListPrompt();
            } else if (input.equalsIgnoreCase("4")) {

                int one;
                int two;

                if (context.getSessionData(pref + CK.S_SHEAR_COLORS) != null) {
                    one = ((List<String>) context.getSessionData(pref + CK.S_SHEAR_COLORS)).size();
                } else {
                    one = 0;
                }

                if (context.getSessionData(pref + CK.S_SHEAR_AMOUNTS) != null) {
                    two = ((List<Integer>) context.getSessionData(pref + CK.S_SHEAR_AMOUNTS)).size();
                } else {
                    two = 0;
                }

                if (one == two) {
                    return new CreateStagePrompt(stageNum, questFactory, citizens);
                } else {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("stageEditorShearNotSameSize"));
                    return new ShearListPrompt();
                }

            }

            return null;

        }

        private List<String> getShearColors(ConversationContext context) {
            return (List<String>) context.getSessionData(pref + CK.S_SHEAR_COLORS);
        }

        private List<Integer> getShearAmounts(ConversationContext context) {
            return (List<Integer>) context.getSessionData(pref + CK.S_SHEAR_AMOUNTS);
        }
    }

    private class ShearColorsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String mobs = ColorUtil.PINK + "- " + Lang.get("stageEditorColors") + " - \n";
            mobs += ColorUtil.PURPLE + "Black, ";
            mobs += ColorUtil.PURPLE + "Blue, ";
            mobs += ColorUtil.PURPLE + "Brown, ";
            mobs += ColorUtil.PURPLE + "Cyan, ";
            mobs += ColorUtil.PURPLE + "Gray, ";
            mobs += ColorUtil.PURPLE + "Green, ";
            mobs += ColorUtil.PURPLE + "LightBlue, ";
            mobs += ColorUtil.PURPLE + "Lime, ";
            mobs += ColorUtil.PURPLE + "Magenta, ";
            mobs += ColorUtil.PURPLE + "Orange, ";
            mobs += ColorUtil.PURPLE + "Pink, ";
            mobs += ColorUtil.PURPLE + "Purple, ";
            mobs += ColorUtil.PURPLE + "Red, ";
            mobs += ColorUtil.PURPLE + "Silver, ";
            mobs += ColorUtil.PURPLE + "White, ";
            mobs += ColorUtil.PURPLE + "Yellow\n";

            return mobs + ColorUtil.YELLOW + Lang.get("stageEditorShearColorsPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final LinkedList<String> colors = new LinkedList<String>();
                for (final String s: input.split(" ")) {

                    if (Quests.getDyeColor(s) != null) {

                        colors.add(Quests.getDyeString(Quests.getDyeColor(s)));
                        context.setSessionData(pref + CK.S_SHEAR_COLORS, colors);

                    } else {
                        player.sendMessage(ColorUtil.PINK + s + " " + ColorUtil.RED + Lang.get("stageEditorInvalidDye"));
                        return new ShearColorsPrompt();
                    }

                }

            }

            return new ShearListPrompt();

        }
    }

    private class ShearAmountsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW + Lang.get("stageEditorShearAmountsPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final LinkedList<Integer> shearAmounts = new LinkedList<Integer>();
                for (final String s: input.split(" ")) {

                    try {

                        final int i = Integer.parseInt(s);

                        if (i < 1) {
                            player.sendMessage(ColorUtil.PINK + input + " " + ColorUtil.RED
                                    + Lang.get("stageEditorNotGreaterThanZero"));
                            return new ShearAmountsPrompt();
                        }

                        shearAmounts.add(i);

                    } catch (final NumberFormatException e) {
                        player.sendMessage(ColorUtil.PINK + input + " " + ColorUtil.RED
                                + Lang.get("stageEditorInvalidNumber"));
                        return new ShearAmountsPrompt();
                    }

                }

                context.setSessionData(pref + CK.S_SHEAR_AMOUNTS, shearAmounts);

            }

            return new ShearListPrompt();

        }
    }

    private class EventListPrompt extends FixedSetPrompt {

        public EventListPrompt() {

            super("1", "2", "3", "4", "5", "6");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GREEN + "- " + Lang.get("stageEditorStageEvents") + " -\n";

            if (context.getSessionData(pref + CK.S_START_EVENT) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorStartEvent") + " (" + Lang.get("noneSet") + ")\n";
            } else {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorStartEvent") + " (" + ColorUtil.AQUA
                        + ((String) context.getSessionData(pref + CK.S_START_EVENT)) + ColorUtil.YELLOW + ")\n";
            }

            if (context.getSessionData(pref + CK.S_FINISH_EVENT) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorFinishEvent") + " (" + Lang.get("noneSet") + ")\n";
            } else {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorFinishEvent") + " (" + ColorUtil.AQUA
                        + ((String) context.getSessionData(pref + CK.S_FINISH_EVENT)) + ColorUtil.YELLOW + ")\n";
            }

            if (context.getSessionData(pref + CK.S_DEATH_EVENT) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorDeathEvent") + " (" + Lang.get("noneSet") + ")\n";
            } else {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorDeathEvent") + " (" + ColorUtil.AQUA
                        + ((String) context.getSessionData(pref + CK.S_DEATH_EVENT)) + ColorUtil.YELLOW + ")\n";
            }

            if (context.getSessionData(pref + CK.S_DISCONNECT_EVENT) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorDisconnectEvent") + " (" + Lang.get("noneSet") + ")\n";
            } else {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorDisconnectEvent") + " (" + ColorUtil.AQUA
                        + ((String) context.getSessionData(pref + CK.S_DISCONNECT_EVENT)) + ColorUtil.YELLOW + ")\n";
            }

            if (context.getSessionData(pref + CK.S_CHAT_EVENTS) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "5" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorChatEvents") + " (" + Lang.get("noneSet") + ")\n";
            } else {

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "5" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("stageEditorChatEvents") + "\n";
                final LinkedList<String> chatEvents = (LinkedList<String>) context.getSessionData(pref
                        + CK.S_CHAT_EVENTS);
                final LinkedList<String> chatEventTriggers = (LinkedList<String>) context.getSessionData(pref
                        + CK.S_CHAT_EVENT_TRIGGERS);

                for (final String event: chatEvents) {
                    text += ColorUtil.AQUA + "    - " + event + ColorUtil.BLUE + " ("
                            + Lang.get("stageEditorTriggeredBy") + ": \""
                            + chatEventTriggers.get(chatEvents.indexOf(event)) + "\")\n";
                }

            }

            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "6" + ColorUtil.RESET + ColorUtil.BLUE + " - "
                    + Lang.get("back");

            return text;
        }

        /*
         * en.put("stageEditorStageEvents", "Stage Events");
         * en.put("stageEditorStartEvent", "Start Event");
         * en.put("stageEditorFinishEvent", "Finish Event");
         * en.put("stageEditorChatEvents", "Chat Events");
         * en.put("stageEditorDeathEvent", "Death Event");
         * en.put("stageEditorDisconnectEvent", "Disconnect Event");
         */
        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new StartEventPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                return new FinishEventPrompt();
            } else if (input.equalsIgnoreCase("3")) {
                return new DeathEventPrompt();
            } else if (input.equalsIgnoreCase("4")) {
                return new DisconnectEventPrompt();
            } else if (input.equalsIgnoreCase("5")) {
                return new ChatEventPrompt();
            } else if (input.equalsIgnoreCase("6")) {
                return new CreateStagePrompt(stageNum, questFactory, citizens);
            } else {
                return new EventListPrompt();
            }

        }

    }

    private class StartEventPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.DARKGREEN + "- " + Lang.get("stageEditorStartEvent") + " -\n";
            if (questFactory.quests.events.isEmpty()) {
                text += ColorUtil.RED + "- None";
            } else {
                for (final Event e: questFactory.quests.events) {
                    text += ColorUtil.GREEN + "- " + e.getName() + "\n";
                }
            }

            return text + ColorUtil.YELLOW + Lang.get("stageEditorEventsPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
                    && input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {

                Event found = null;

                for (final Event e: questFactory.quests.events) {

                    if (e.getName().equalsIgnoreCase(input)) {
                        found = e;
                        break;
                    }

                }

                if (found == null) {
                    player.sendMessage(ColorUtil.RED + input + ColorUtil.YELLOW + " "
                            + Lang.get("stageEditorInvalidEvent"));
                    return new StartEventPrompt();
                } else {
                    context.setSessionData(pref + CK.S_START_EVENT, found.getName());
                    return new EventListPrompt();
                }

            } else if (input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
                return new EventListPrompt();
            } else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
                context.setSessionData(pref + CK.S_START_EVENT, null);
                player.sendMessage(ColorUtil.YELLOW + Lang.get("stageEditorStartEventCleared"));
                return new EventListPrompt();
            } else {
                return new StartEventPrompt();
            }

        }
    }

    private class FinishEventPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.DARKGREEN + "- " + Lang.get("stageEditorFinishEvent") + " -\n";
            if (questFactory.quests.events.isEmpty()) {
                text += ColorUtil.RED + "- None";
            } else {
                for (final Event e: questFactory.quests.events) {
                    text += ColorUtil.GREEN + "- " + e.getName() + "\n";
                }
            }

            return text + ColorUtil.YELLOW + Lang.get("stageEditorEventsPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
                    && input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {

                Event found = null;

                for (final Event e: questFactory.quests.events) {

                    if (e.getName().equalsIgnoreCase(input)) {
                        found = e;
                        break;
                    }

                }

                if (found == null) {
                    player.sendMessage(ColorUtil.RED + input + ColorUtil.YELLOW + " "
                            + Lang.get("stageEditorInvalidEvent"));
                    return new FinishEventPrompt();
                } else {
                    context.setSessionData(pref + CK.S_FINISH_EVENT, found.getName());
                    return new EventListPrompt();
                }

            } else if (input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
                return new EventListPrompt();
            } else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
                context.setSessionData(pref + CK.S_FINISH_EVENT, null);
                player.sendMessage(ColorUtil.YELLOW + Lang.get("stageEditorFinishEventCleared"));
                return new EventListPrompt();
            } else {
                return new FinishEventPrompt();
            }

        }
    }

    private class DeathEventPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.DARKGREEN + "- " + Lang.get("stageEditorDeathEvent") + " -\n";
            if (questFactory.quests.events.isEmpty()) {
                text += ColorUtil.RED + "- None";
            } else {
                for (final Event e: questFactory.quests.events) {
                    text += ColorUtil.GREEN + "- " + e.getName() + "\n";
                }
            }

            return text + ColorUtil.YELLOW + Lang.get("stageEditorEventsPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
                    && input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {

                Event found = null;

                for (final Event e: questFactory.quests.events) {

                    if (e.getName().equalsIgnoreCase(input)) {
                        found = e;
                        break;
                    }

                }

                if (found == null) {
                    player.sendMessage(ColorUtil.RED + input + ColorUtil.YELLOW + " "
                            + Lang.get("stageEditorInvalidEvent"));
                    return new DeathEventPrompt();
                } else {
                    context.setSessionData(pref + CK.S_DEATH_EVENT, found.getName());
                    return new EventListPrompt();
                }

            } else if (input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
                return new EventListPrompt();
            } else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
                context.setSessionData(pref + CK.S_DEATH_EVENT, null);
                player.sendMessage(ColorUtil.YELLOW + Lang.get("stageEditorDeathEventCleared"));
                return new EventListPrompt();
            } else {
                return new DeathEventPrompt();
            }

        }
    }

    private class DisconnectEventPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.DARKGREEN + "- " + Lang.get("stageEditorDisconnectEvent") + " -\n";
            if (questFactory.quests.events.isEmpty()) {
                text += ColorUtil.RED + "- None";
            } else {
                for (final Event e: questFactory.quests.events) {
                    text += ColorUtil.GREEN + "- " + e.getName() + "\n";
                }
            }

            return text + ColorUtil.YELLOW + Lang.get("stageEditorEventsPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
                    && input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {

                Event found = null;

                for (final Event e: questFactory.quests.events) {

                    if (e.getName().equalsIgnoreCase(input)) {
                        found = e;
                        break;
                    }

                }

                if (found == null) {
                    player.sendMessage(ColorUtil.RED + input + ColorUtil.YELLOW + " "
                            + Lang.get("stageEditorInvalidEvent"));
                    return new DisconnectEventPrompt();
                } else {
                    context.setSessionData(pref + CK.S_DISCONNECT_EVENT, found.getName());
                    return new EventListPrompt();
                }

            } else if (input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
                return new EventListPrompt();
            } else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
                context.setSessionData(pref + CK.S_DISCONNECT_EVENT, null);
                player.sendMessage(ColorUtil.YELLOW + Lang.get("stageEditorDisconnectEventCleared"));
                return new EventListPrompt();
            } else {
                return new DisconnectEventPrompt();
            }

        }
    }

    private class ChatEventPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.DARKGREEN + "- " + Lang.get("stageEditorChatEvents") + " -\n";
            if (questFactory.quests.events.isEmpty()) {
                text += ColorUtil.RED + "- None";
            } else {
                for (final Event e: questFactory.quests.events) {
                    text += ColorUtil.GREEN + "- " + e.getName() + "\n";
                }
            }

            return text + ColorUtil.YELLOW + Lang.get("stageEditorChatEventsPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
                    && input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {

                Event found = null;

                for (final Event e: questFactory.quests.events) {

                    if (e.getName().equalsIgnoreCase(input)) {
                        found = e;
                        break;
                    }

                }

                if (found == null) {
                    player.sendMessage(ColorUtil.RED + input + ColorUtil.YELLOW + " "
                            + Lang.get("stageEditorInvalidEvent"));
                    return new ChatEventPrompt();
                } else {
                    context.setSessionData(pref + CK.S_CHAT_TEMP_EVENT, found.getName());
                    return new ChatEventTriggerPrompt();
                }

            } else if (input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
                return new EventListPrompt();
            } else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
                context.setSessionData(pref + CK.S_CHAT_EVENTS, null);
                context.setSessionData(pref + CK.S_CHAT_EVENT_TRIGGERS, null);
                player.sendMessage(ColorUtil.YELLOW + Lang.get("stageEditorChatEventsCleared"));
                return new EventListPrompt();
            } else {
                return new ChatEventPrompt();
            }

        }
    }

    private class ChatEventTriggerPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            final String tempEvent = (String) context.getSessionData(pref + CK.S_CHAT_TEMP_EVENT);

            String text = ColorUtil.GOLD + "- " + Lang.get("stageEditorChatTrigger") + " -\n";
            text += ColorUtil.YELLOW + Lang.get("stageEditorChatEventsTriggerPromptA") + " " + ColorUtil.AQUA
                    + tempEvent + " " + ColorUtil.YELLOW + Lang.get("stageEditorChatEventsTriggerPromptB");

            return text;
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                if (context.getSessionData(pref + CK.S_CHAT_EVENTS) == null) {

                    final LinkedList<String> chatEvents = new LinkedList<String>();
                    final LinkedList<String> chatEventTriggers = new LinkedList<String>();

                    final String event = (String) context.getSessionData(pref + CK.S_CHAT_TEMP_EVENT);

                    chatEvents.add(event);
                    chatEventTriggers.add(input.trim());

                    context.setSessionData(pref + CK.S_CHAT_EVENTS, chatEvents);
                    context.setSessionData(pref + CK.S_CHAT_EVENT_TRIGGERS, chatEventTriggers);

                    return new EventListPrompt();

                } else {

                    final LinkedList<String> chatEvents = (LinkedList<String>) context.getSessionData(pref
                            + CK.S_CHAT_EVENTS);
                    final LinkedList<String> chatEventTriggers = (LinkedList<String>) context.getSessionData(pref
                            + CK.S_CHAT_EVENT_TRIGGERS);

                    final String event = (String) context.getSessionData(pref + CK.S_CHAT_TEMP_EVENT);

                    chatEvents.add(event);
                    chatEventTriggers.add(input.trim());

                    context.setSessionData(pref + CK.S_CHAT_EVENTS, chatEvents);
                    context.setSessionData(pref + CK.S_CHAT_EVENT_TRIGGERS, chatEventTriggers);

                    return new EventListPrompt();

                }

            } else if (input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
                return new EventListPrompt();
            } else {
                return new ChatEventTriggerPrompt();
            }

        }
    }

    private class DelayPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW + Lang.get("stageEditorDelayPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
                return new CreateStagePrompt(stageNum, questFactory, citizens);
            }
            if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
                context.setSessionData(pref + CK.S_DELAY, null);
                player.sendMessage(ColorUtil.GREEN + "Delay cleared.");
                return new CreateStagePrompt(stageNum, questFactory, citizens);
            }

            long l;

            try {

                l = Long.parseLong(input);

            } catch (final NumberFormatException e) {
                // returns -1 if incorrect input
                l = MiscUtil.getTimeFromString(input);

                if (l == -1) {
                    player.sendMessage(ColorUtil.RED + Lang.get("stageEditorNoNumber"));
                    return new DelayPrompt();
                }
            }

            if (l < 1000) {
                player.sendMessage(ColorUtil.RED + Lang.get("stageEditorInvalidDelay"));
                return new DelayPrompt();
            } else {
                context.setSessionData(pref + CK.S_DELAY, l);
                return new CreateStagePrompt(stageNum, questFactory, citizens);
            }

        }
    }

    private class DelayMessagePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW + Lang.get("stageEditorDelayMessagePrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
                    && input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {

                context.setSessionData(pref + CK.S_DELAY_MESSAGE, input);
                return new CreateStagePrompt(stageNum, questFactory, citizens);

            } else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
                context.setSessionData(pref + CK.S_DELAY_MESSAGE, null);
                player.sendMessage(ColorUtil.YELLOW + "Delay message cleared.");
                return new CreateStagePrompt(stageNum, questFactory, citizens);
            } else {
                return new DelayMessagePrompt();
            }

        }
    }

    private class DenizenPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            for (final String s: ScriptRegistry._getScriptNames()) {}

            return ColorUtil.YELLOW + Lang.get("stageEditorScriptPrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
                    && input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {

                if (ScriptRegistry.containsScript(input)) {

                    context.setSessionData(pref + CK.S_DENIZEN, ScriptRegistry.getScriptContainer(input).getName());
                    return new CreateStagePrompt(stageNum, questFactory, citizens);

                } else {

                    player.sendMessage(ColorUtil.RED + Lang.get("stageEditorInvalidScript"));
                    return new DenizenPrompt();

                }

            } else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
                context.setSessionData(pref + CK.S_DENIZEN, null);
                player.sendMessage(ColorUtil.YELLOW + "Denizen script cleared.");
                return new CreateStagePrompt(stageNum, questFactory, citizens);
            } else {
                return new CreateStagePrompt(stageNum, questFactory, citizens);
            }

        }
    }

    private class DeletePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GREEN + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + "" + ColorUtil.GREEN + " - "
                    + Lang.get("yes") + "\n";
            text += ColorUtil.GREEN + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + "" + ColorUtil.GREEN + " - "
                    + Lang.get("no");
            return ColorUtil.RED + Lang.get("stageEditorConfirmStageDelete") + "\n" + ColorUtil.YELLOW
                    + Lang.get("stageEditorStage") + " " + stageNum + ": " + context.getSessionData(CK.Q_NAME)
                    + ColorUtil.RED + "\n(" + Lang.get("stageEditorConfirmStageNote") + ")\n" + text;

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase("1") || input.equalsIgnoreCase("Yes")) {
                StagesPrompt.deleteStage(context, stageNum);
                player.sendMessage(ColorUtil.YELLOW + Lang.get("stageEditorDeleteSucces"));
                return new StagesPrompt(questFactory);
            } else if (input.equalsIgnoreCase("2") || input.equalsIgnoreCase("No")) {
                return new CreateStagePrompt(stageNum, questFactory, citizens);
            } else {
                player.sendMessage(ColorUtil.RED + Lang.get("invalidOption"));
                return new DeletePrompt();
            }

        }
    }

    private class StartMessagePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW + Lang.get("stageEditorStartMessagePrompt");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
                    && input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {

                context.setSessionData(pref + CK.S_START_MESSAGE, input);
                return new CreateStagePrompt(stageNum, questFactory, citizens);

            } else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
                context.setSessionData(pref + CK.S_START_MESSAGE, null);
                player.sendMessage(ColorUtil.YELLOW + Lang.get("stageEditorStartMessageCleared"));
                return new CreateStagePrompt(stageNum, questFactory, citizens);
            } else {
                return new CreateStagePrompt(stageNum, questFactory, citizens);
            }
        }

    }

    private class CompleteMessagePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW
                    + Lang.get("Enter complete message, or enter \"clear\" to clear the message, or \"cancel\" to return");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
                    && input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {

                context.setSessionData(pref + CK.S_COMPLETE_MESSAGE, input);
                return new CreateStagePrompt(stageNum, questFactory, citizens);

            } else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
                context.setSessionData(pref + CK.S_COMPLETE_MESSAGE, null);
                player.sendMessage(ColorUtil.YELLOW + Lang.get("stageEditorCompleteMessageCleared"));
                return new CreateStagePrompt(stageNum, questFactory, citizens);
            } else {
                return new CreateStagePrompt(stageNum, questFactory, citizens);
            }
        }

    }

    private class CustomObjectivesPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            String text = ColorUtil.PINK + "- Custom Objectives -\n";
            if (questFactory.quests.customObjectives.isEmpty()) {
                text += ColorUtil.BOLD + "" + ColorUtil.PURPLE + "(No modules loaded)";
            } else {
                for (final CustomObjective co: questFactory.quests.customObjectives) {
                    text += ColorUtil.PURPLE + " - " + co.getName() + "\n";
                }
            }

            return text
                    + ColorUtil.YELLOW
                    + "Enter the name of a custom objective to add, or enter \'clear\' to clear all custom objectives, or \'cancel\' to return.";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("cancel") == false && input.equalsIgnoreCase("clear") == false) {

                CustomObjective found = null;
                for (final CustomObjective co: questFactory.quests.customObjectives) {
                    if (co.getName().equalsIgnoreCase(input)) {
                        found = co;
                        break;
                    }
                }

                if (found == null) {
                    for (final CustomObjective co: questFactory.quests.customObjectives) {
                        if (co.getName().toLowerCase().contains(input.toLowerCase())) {
                            found = co;
                            break;
                        }
                    }
                }

                if (found != null) {

                    if (context.getSessionData(pref + CK.S_CUSTOM_OBJECTIVES) != null) {
                        final LinkedList<String> list = (LinkedList<String>) context.getSessionData(pref
                                + CK.S_CUSTOM_OBJECTIVES);
                        final LinkedList<Map<String, Object>> datamapList = (LinkedList<Map<String, Object>>) context
                                .getSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA);
                        final LinkedList<Integer> countList = (LinkedList<Integer>) context.getSessionData(pref
                                + CK.S_CUSTOM_OBJECTIVES_COUNT);
                        if (list.contains(found.getName()) == false) {
                            list.add(found.getName());
                            datamapList.add(found.datamap);
                            countList.add(-999);
                            context.setSessionData(pref + CK.S_CUSTOM_OBJECTIVES, list);
                            context.setSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA, datamapList);
                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.YELLOW + "That custom objective has already been added!");
                            return new CustomObjectivesPrompt();
                        }
                    } else {
                        final LinkedList<Map<String, Object>> datamapList = new LinkedList<Map<String, Object>>();
                        final LinkedList<Integer> countList = new LinkedList<Integer>();
                        datamapList.add(found.datamap);
                        countList.add(-999);
                        final LinkedList<String> list = new LinkedList<String>();
                        list.add(found.getName());
                        context.setSessionData(pref + CK.S_CUSTOM_OBJECTIVES, list);
                        context.setSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA, datamapList);
                        context.setSessionData(pref + CK.S_CUSTOM_OBJECTIVES_COUNT, countList);
                    }

                    // Send user to the count prompt / custom data prompt if
                    // there is any needed

                    if (found.isEnableCount()) {
                        return new CustomObjectiveCountPrompt();
                    }

                    if (found.datamap.isEmpty() == false) {

                        context.setSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA_DESCRIPTIONS, found.descriptions);
                        return new ObjectiveCustomDataListPrompt();

                    }
                    //

                } else {
                    context.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Custom objective module not found.");
                    return new CustomObjectivesPrompt();
                }

            } else if (input.equalsIgnoreCase("clear")) {
                context.setSessionData(pref + CK.S_CUSTOM_OBJECTIVES, null);
                context.setSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA, null);
                context.setSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA_TEMP, null);
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Custom objectives cleared.");
            }

            return new CreateStagePrompt(stageNum, questFactory, citizens);

        }

    }

    private class CustomObjectiveCountPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.BOLD + "" + ColorUtil.AQUA + "- ";

            final LinkedList<String> list = (LinkedList<String>) context.getSessionData(pref + CK.S_CUSTOM_OBJECTIVES);

            final String objName = list.getLast();

            text += objName + " -\n";

            CustomObjective found = null;
            for (final CustomObjective co: questFactory.quests.customObjectives) {

                if (co.getName().equals(objName)) {
                    found = co;
                    break;
                }

            }

            text += ColorUtil.BLUE + found.getCountPrompt() + "\n\n";

            return text;

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            try {

                final int num = Integer.parseInt(input);
                final LinkedList<Integer> counts = (LinkedList<Integer>) context.getSessionData(pref
                        + CK.S_CUSTOM_OBJECTIVES_COUNT);
                counts.set(counts.size() - 1, num);

                final LinkedList<String> list = (LinkedList<String>) context.getSessionData(pref
                        + CK.S_CUSTOM_OBJECTIVES);
                final String objName = list.getLast();

                CustomObjective found = null;
                for (final CustomObjective co: questFactory.quests.customObjectives) {

                    if (co.getName().equals(objName)) {
                        found = co;
                        break;
                    }

                }

                if (found.datamap.isEmpty() == false) {
                    context.setSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA_DESCRIPTIONS, found.descriptions);
                    return new ObjectiveCustomDataListPrompt();
                } else {
                    return new CreateStagePrompt(stageNum, questFactory, citizens);
                }

            } catch (final NumberFormatException e) {
                context.getForWhom().sendRawMessage(ColorUtil.RED + "Input was not a number!");
                return new CustomObjectiveCountPrompt();
            }

        }

    }

    private class ObjectiveCustomDataListPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.BOLD + "" + ColorUtil.AQUA + "- ";

            final LinkedList<String> list = (LinkedList<String>) context.getSessionData(pref + CK.S_CUSTOM_OBJECTIVES);
            final LinkedList<Map<String, Object>> datamapList = (LinkedList<Map<String, Object>>) context
                    .getSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA);

            final String objName = list.getLast();
            final Map<String, Object> datamap = datamapList.getLast();

            text += objName + " -\n";
            int index = 1;

            final LinkedList<String> datamapKeys = new LinkedList<String>();
            for (final String key: datamap.keySet()) {
                datamapKeys.add(key);
            }
            Collections.sort(datamapKeys);

            for (final String dataKey: datamapKeys) {

                text += ColorUtil.BOLD + "" + ColorUtil.DARKBLUE + index + " - " + ColorUtil.RESET + ColorUtil.BLUE
                        + dataKey;
                if (datamap.get(dataKey) != null) {
                    text += ColorUtil.GREEN + " (" + (String) datamap.get(dataKey) + ")\n";
                } else {
                    text += ColorUtil.RED + " (Value required)\n";
                }

                index++;

            }

            text += ColorUtil.BOLD + "" + ColorUtil.DARKBLUE + index + " - " + ColorUtil.AQUA + "Finish";

            return text;
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final LinkedList<Map<String, Object>> datamapList = (LinkedList<Map<String, Object>>) context
                    .getSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA);
            final Map<String, Object> datamap = datamapList.getLast();

            int numInput;

            try {
                numInput = Integer.parseInt(input);
            } catch (final NumberFormatException nfe) {
                return new ObjectiveCustomDataListPrompt();
            }

            if (numInput < 1 || numInput > datamap.size() + 1) {
                return new ObjectiveCustomDataListPrompt();
            }

            if (numInput < datamap.size() + 1) {

                final LinkedList<String> datamapKeys = new LinkedList<String>();
                for (final String key: datamap.keySet()) {
                    datamapKeys.add(key);
                }
                Collections.sort(datamapKeys);

                final String selectedKey = datamapKeys.get(numInput - 1);
                context.setSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA_TEMP, selectedKey);
                return new ObjectiveCustomDataPrompt();

            } else {

                if (datamap.containsValue(null)) {
                    return new ObjectiveCustomDataListPrompt();
                } else {
                    context.setSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA_DESCRIPTIONS, null);
                    return new CreateStagePrompt(stageNum, questFactory, citizens);
                }

            }

        }

    }

    private class ObjectiveCustomDataPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            String text = "";
            final String temp = (String) context.getSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA_TEMP);
            final Map<String, String> descriptions = (Map<String, String>) context.getSessionData(pref
                    + CK.S_CUSTOM_OBJECTIVES_DATA_DESCRIPTIONS);
            if (descriptions.get(temp) != null) {
                text += ColorUtil.GOLD + descriptions.get(temp) + "\n";
            }

            text += ColorUtil.YELLOW + "Enter value for ";
            text += ColorUtil.BOLD + temp + ColorUtil.RESET + ColorUtil.YELLOW + ":";
            return text;
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            final LinkedList<Map<String, Object>> datamapList = (LinkedList<Map<String, Object>>) context
                    .getSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA);
            final Map<String, Object> datamap = datamapList.getLast();
            datamap.put((String) context.getSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA_TEMP), input);
            context.setSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA_TEMP, null);
            return new ObjectiveCustomDataListPrompt();
        }

    }

}
