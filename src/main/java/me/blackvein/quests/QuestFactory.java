package me.blackvein.quests;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.blackvein.quests.prompts.ItemStackPrompt;
import me.blackvein.quests.prompts.RequirementsPrompt;
import me.blackvein.quests.prompts.RewardsPrompt;
import me.blackvein.quests.prompts.StagesPrompt;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.ColorUtil;
import me.blackvein.quests.util.ItemUtil;
import me.blackvein.quests.util.Lang;
import net.citizensnpcs.api.CitizensAPI;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.protection.managers.RegionManager;

public class QuestFactory implements ConversationAbandonedListener, ColorUtil {

    public Quests             quests;
    Map<Player, Quest>        editSessions           = new HashMap<Player, Quest>();
    Map<Player, Block>        selectedBlockStarts    = new HashMap<Player, Block>();
    public Map<Player, Block> selectedKillLocations  = new HashMap<Player, Block>();
    public Map<Player, Block> selectedReachLocations = new HashMap<Player, Block>();
    public HashSet<Player>    selectingNPCs          = new HashSet<Player>();
    public List<String>       names                  = new LinkedList<String>();
    ConversationFactory       convoCreator;
    File                      questsFile;

    @SuppressWarnings("LeakingThisInConstructor")
    public QuestFactory(Quests plugin) {

        quests = plugin;
        questsFile = new File(plugin.getDataFolder(), "quests.yml");

        // Ensure to initialize convoCreator last, to ensure that 'this' is
        // fully initialized before it is passed
        convoCreator = new ConversationFactory(plugin).withModality(false).withLocalEcho(false)
                .withFirstPrompt(new MenuPrompt()).withTimeout(3600)
                .thatExcludesNonPlayersWithMessage("Console may not perform this operation!")
                .addConversationAbandonedListener(this);

    }

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {

        if (abandonedEvent.getContext().getSessionData(CK.Q_NAME) != null) {
            names.remove(abandonedEvent.getContext().getSessionData(CK.Q_NAME));
        }

        final Player player = (Player) abandonedEvent.getContext().getForWhom();
        selectedBlockStarts.remove(player);
        selectedKillLocations.remove(player);
        selectedReachLocations.remove(player);

    }

    private class MenuPrompt extends FixedSetPrompt {

        public MenuPrompt() {

            super("1", "2", "3", "4");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            final String text = ColorUtil.GOLD + "- Quest Editor -\n" + ColorUtil.BLUE + "" + ColorUtil.BOLD + "1"
                    + ColorUtil.RESET + ColorUtil.YELLOW + " - " + Lang.get("questEditorCreate") + "\n"
                    + ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                    + Lang.get("questEditorEdit") + "\n" + ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET
                    + ColorUtil.YELLOW + " - " + Lang.get("questEditorDelete") + "\n" + ColorUtil.GOLD + ""
                    + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - " + Lang.get("exit");

            return text;

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase("1")) {

                if (player.hasPermission("quests.editor.create")) {
                    return new QuestNamePrompt();
                } else {
                    player.sendMessage(ColorUtil.RED + Lang.get("questEditorNoPermsCreate"));
                    return new MenuPrompt();
                }

            } else if (input.equalsIgnoreCase("2")) {

                if (player.hasPermission("quests.editor.edit")) {
                    return new SelectEditPrompt();
                } else {
                    player.sendMessage(ColorUtil.RED + Lang.get("questEditorNoPermsCreate"));
                    return new MenuPrompt();
                }

            } else if (input.equalsIgnoreCase("3")) {

                if (player.hasPermission("quests.editor.delete")) {
                    return new SelectDeletePrompt();
                } else {
                    player.sendMessage(ColorUtil.RED + Lang.get("questEditorNoPermsDelete"));
                    return new MenuPrompt();
                }

            } else if (input.equalsIgnoreCase("4")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + Lang.get("exited"));
                return Prompt.END_OF_CONVERSATION;
            }

            return null;

        }
    }

    public Prompt returnToMenu() {

        return new CreateMenuPrompt();

    }

    private class CreateMenuPrompt extends FixedSetPrompt {

        public CreateMenuPrompt() {

            super("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- Quest: " + ColorUtil.AQUA + context.getSessionData(CK.Q_NAME)
                    + ColorUtil.GOLD + " -\n";

            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                    + Lang.get("questEditorName") + "\n";

            if (context.getSessionData(CK.Q_ASK_MESSAGE) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.RED + " - "
                        + Lang.get("questEditorAskMessage") + " " + ColorUtil.DARKRED + "(Required, none set)\n";
            } else {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("questEditorAskMessage") + " (\"" + context.getSessionData(CK.Q_ASK_MESSAGE)
                        + "\")\n";
            }

            if (context.getSessionData(CK.Q_FINISH_MESSAGE) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.RED + " - "
                        + Lang.get("questEditorFinishMessage") + " " + ColorUtil.DARKRED + "(Required, none set)\n";
            } else {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("questEditorFinishMessage") + " (\"" + context.getSessionData(CK.Q_FINISH_MESSAGE)
                        + "\")\n";
            }

            if (context.getSessionData(CK.Q_REDO_DELAY) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("questEditorRedoDelay") + " (None set)\n";
            } else {

                // something here is throwing an exception
                try {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("questEditorRedoDelay") + " ("
                            + Quests.getTime((Long) context.getSessionData(CK.Q_REDO_DELAY)) + ")\n";
                } catch (final Exception e) {
                    e.printStackTrace();
                }

                //
            }

            if (context.getSessionData(CK.Q_START_NPC) == null && quests.citizens != null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "5" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                        + Lang.get("questEditorNPCStart") + " (None set)\n";
            } else if (quests.citizens != null) {
                text += ColorUtil.BLUE
                        + ""
                        + ColorUtil.BOLD
                        + "5"
                        + ColorUtil.RESET
                        + ColorUtil.YELLOW
                        + " - "
                        + Lang.get("questEditorNPCStart")
                        + " ("
                        + CitizensAPI.getNPCRegistry().getById((Integer) context.getSessionData(CK.Q_START_NPC))
                                .getName() + ")\n";
            }

            if (context.getSessionData(CK.Q_START_BLOCK) == null) {

                if (quests.citizens != null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "6" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("questEditorBlockStart") + " (None set)\n";
                } else {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "5" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("questEditorBlockStart") + " (None set)\n";
                }

            } else {

                if (quests.citizens != null) {
                    final Location l = (Location) context.getSessionData(CK.Q_START_BLOCK);
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "6" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("questEditorBlockStart") + " (" + l.getWorld().getName() + ", " + l.getBlockX()
                            + ", " + l.getBlockY() + ", " + l.getBlockZ() + ")\n";
                } else {
                    final Location l = (Location) context.getSessionData(CK.Q_START_BLOCK);
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "5" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("questEditorBlockStart") + " (" + l.getWorld().getName() + ", " + l.getBlockX()
                            + ", " + l.getBlockY() + ", " + l.getBlockZ() + ")\n";
                }

            }

            if (Quests.worldGuard != null) {

                if (context.getSessionData(CK.Q_REGION) == null) {

                    if (quests.citizens != null) {
                        text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "7" + ColorUtil.RESET + ColorUtil.YELLOW
                                + " - Set Region (None set)\n";
                    } else {
                        text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "6" + ColorUtil.RESET + ColorUtil.YELLOW
                                + " - Set Region (None set)\n";
                    }

                } else {

                    if (quests.citizens != null) {
                        final String s = (String) context.getSessionData(CK.Q_REGION);
                        text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "7" + ColorUtil.RESET + ColorUtil.YELLOW
                                + " - Set Region (" + ColorUtil.GREEN + s + ColorUtil.YELLOW + ")\n";
                    } else {
                        final String s = (String) context.getSessionData(CK.Q_REGION);
                        text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "6" + ColorUtil.RESET + ColorUtil.YELLOW
                                + " - Set Region (" + ColorUtil.GREEN + s + ColorUtil.YELLOW + ")\n";
                    }

                }

            } else {

                if (quests.citizens != null) {
                    text += ColorUtil.GRAY + "7 - Set Region (WorldGuard not installed)\n";
                } else {
                    text += ColorUtil.GRAY + "6 - Set Region (WorldGuard not installed)\n";
                }

            }

            if (context.getSessionData(CK.Q_INITIAL_EVENT) == null) {

                if (quests.citizens != null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "8" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("questEditorInitialEvent") + " (None set)\n";
                } else {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "7" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("questEditorInitialEvent") + " (None set)\n";
                }

            } else {

                if (quests.citizens != null) {
                    final String s = (String) context.getSessionData(CK.Q_INITIAL_EVENT);
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "8" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("questEditorInitialEvent") + " (" + s + ")\n";
                } else {
                    final String s = (String) context.getSessionData(CK.Q_INITIAL_EVENT);
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "7" + ColorUtil.RESET + ColorUtil.YELLOW + " - "
                            + Lang.get("questEditorInitialEvent") + " (" + s + ")\n";
                }

            }

            if (quests.citizens != null) {

                if (context.getSessionData(CK.Q_GUIDISPLAY) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "9" + ColorUtil.RESET + ColorUtil.YELLOW
                            + " - Set GUI Item Display (None set)\n";
                } else {

                    final ItemStack stack = (ItemStack) context.getSessionData(CK.Q_GUIDISPLAY);
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "9" + ColorUtil.RESET + ColorUtil.YELLOW
                            + " - Set GUI Item Display (" + ItemUtil.getDisplayString(stack) + ColorUtil.RESET
                            + ColorUtil.YELLOW + ")\n";

                }

            } else {
                text += ColorUtil.GRAY + "8 - Set GUI Item Display (Citizens not installed) \n";
            }

            if (quests.citizens != null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "10" + ColorUtil.RESET + ColorUtil.DARKAQUA + " - "
                        + Lang.get("questEditorReqs") + "\n";
            } else {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "9" + ColorUtil.RESET + ColorUtil.DARKAQUA + " - "
                        + Lang.get("questEditorReqs") + "\n";
            }

            if (quests.citizens != null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "11" + ColorUtil.RESET + ColorUtil.PINK + " - "
                        + Lang.get("questEditorStages") + "\n";
            } else {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "10" + ColorUtil.RESET + ColorUtil.PINK + " - "
                        + Lang.get("questEditorStages") + "\n";
            }

            if (quests.citizens != null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "12" + ColorUtil.RESET + ColorUtil.GREEN + " - "
                        + Lang.get("questEditorRews") + "\n";
            } else {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "11" + ColorUtil.RESET + ColorUtil.GREEN + " - "
                        + Lang.get("questEditorRews") + "\n";
            }

            if (quests.citizens != null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "13" + ColorUtil.RESET + ColorUtil.GOLD + " - "
                        + Lang.get("save") + "\n";
            } else {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "12" + ColorUtil.RESET + ColorUtil.GOLD + " - "
                        + Lang.get("save") + "\n";
            }

            if (quests.citizens != null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "14" + ColorUtil.RESET + ColorUtil.RED + " - "
                        + Lang.get("exit") + "\n";
            } else {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "13" + ColorUtil.RESET + ColorUtil.RED + " - "
                        + Lang.get("exit") + "\n";
            }

            return text;

        }

        @Override
        public Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {

                return new SetNamePrompt();

            } else if (input.equalsIgnoreCase("2")) {

                return new AskMessagePrompt();

            } else if (input.equalsIgnoreCase("3")) {

                return new FinishMessagePrompt();

            } else if (input.equalsIgnoreCase("4")) {

                return new RedoDelayPrompt();

            } else if (input.equalsIgnoreCase("5")) {

                if (quests.citizens != null) {
                    return new SetNpcStartPrompt();
                } else {
                    selectedBlockStarts.put((Player) context.getForWhom(), null);
                    return new BlockStartPrompt();
                }

            } else if (input.equalsIgnoreCase("6")) {

                if (quests.citizens != null) {
                    selectedBlockStarts.put((Player) context.getForWhom(), null);
                    return new BlockStartPrompt();
                } else if (Quests.worldGuard != null) {
                    return new RegionPrompt();
                } else {
                    return new CreateMenuPrompt();
                }

            } else if (input.equalsIgnoreCase("7")) {

                if (quests.citizens != null && Quests.worldGuard != null) {
                    return new RegionPrompt();
                } else if (quests.citizens != null) {
                    return new CreateMenuPrompt();
                } else {
                    return new InitialEventPrompt();
                }

            } else if (input.equalsIgnoreCase("8")) {

                if (quests.citizens != null) {
                    return new InitialEventPrompt();
                } else {
                    return new GUIDisplayPrompt();
                }

            } else if (input.equalsIgnoreCase("9")) {

                if (quests.citizens != null) {
                    return new GUIDisplayPrompt();
                } else {
                    return new RequirementsPrompt(quests, QuestFactory.this);
                }

            } else if (input.equalsIgnoreCase("10")) {

                if (quests.citizens != null) {
                    return new RequirementsPrompt(quests, QuestFactory.this);
                } else {
                    return new StagesPrompt(QuestFactory.this);
                }

            } else if (input.equalsIgnoreCase("11")) {

                if (quests.citizens != null) {
                    return new StagesPrompt(QuestFactory.this);
                } else {
                    return new RewardsPrompt(quests, QuestFactory.this);
                }

            } else if (input.equalsIgnoreCase("12")) {

                if (quests.citizens != null) {
                    return new RewardsPrompt(quests, QuestFactory.this);
                } else {
                    return new SavePrompt();
                }

            } else if (input.equalsIgnoreCase("13")) {

                if (quests.citizens != null) {
                    return new SavePrompt();
                } else {
                    return new ExitPrompt();
                }

            } else if (input.equalsIgnoreCase("14")) {

                if (quests.citizens != null) {
                    return new ExitPrompt();
                } else {
                    return new CreateMenuPrompt();
                }

            }

            return null;

        }
    }

    private class SelectEditPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String s = ColorUtil.GOLD + "- Edit Quest -\n";
            for (final Quest q: quests.getQuests()) {
                s += ColorUtil.GRAY + "- " + ColorUtil.YELLOW + q.getName() + "\n";
            }

            return s + ColorUtil.GOLD + "Enter a Quest to edit, or \"cancel\" to return.";

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                for (final Quest q: quests.getQuests()) {

                    if (q.getName().equalsIgnoreCase(input)) {
                        QuestFactory.loadQuest(context, q);
                        return new CreateMenuPrompt();
                    }

                }

                for (final Quest q: quests.getQuests()) {

                    if (q.getName().toLowerCase().startsWith(input.toLowerCase())) {
                        QuestFactory.loadQuest(context, q);
                        return new CreateMenuPrompt();
                    }

                }

                for (final Quest q: quests.getQuests()) {

                    if (q.getName().toLowerCase().contains(input.toLowerCase())) {
                        QuestFactory.loadQuest(context, q);
                        return new CreateMenuPrompt();
                    }

                }

                return new SelectEditPrompt();

            } else {
                return new MenuPrompt();
            }

        }
    }

    private class QuestNamePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- " + Lang.get("questEditorHeader") + " -\n";
            text += ColorUtil.AQUA + Lang.get("questEditorCreate") + " " + ColorUtil.GOLD + "- "
                    + Lang.get("questEditorEnterQuestName");

            return text;

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                for (final Quest q: quests.quests) {

                    if (q.name.equalsIgnoreCase(input)) {

                        context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("questEditorNameExists"));
                        return new QuestNamePrompt();

                    }

                }

                if (names.contains(input)) {

                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("questEditorBeingEdited"));
                    return new QuestNamePrompt();

                }

                if (input.contains(",")) {

                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("questEditorInvalidQuestName"));
                    return new QuestNamePrompt();

                }

                context.setSessionData(CK.Q_NAME, input);
                names.add(input);
                return new CreateMenuPrompt();

            } else {

                return new MenuPrompt();

            }

        }
    }

    private class SetNpcStartPrompt extends NumericPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            selectingNPCs.add((Player) context.getForWhom());
            return ChatColor.YELLOW + Lang.get("questEditorEnterNPCStart") + "\n" + ChatColor.GOLD
                    + Lang.get("npcHint");

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number input) {

            if (input.intValue() > -1) {

                if (CitizensAPI.getNPCRegistry().getById(input.intValue()) == null) {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("questEditorInvalidNPC"));
                    return new SetNpcStartPrompt();
                }

                context.setSessionData(CK.Q_START_NPC, input.intValue());
                selectingNPCs.remove(context.getForWhom());
                return new CreateMenuPrompt();

            } else if (input.intValue() == -1) {
                context.setSessionData(CK.Q_START_NPC, null);
                selectingNPCs.remove(context.getForWhom());
                return new CreateMenuPrompt();
            } else if (input.intValue() == -2) {
                selectingNPCs.remove(context.getForWhom());
                return new CreateMenuPrompt();
            } else {
                context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("questEditorInvalidNPC"));
                return new SetNpcStartPrompt();
            }

        }
    }

    private class BlockStartPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ChatColor.YELLOW + Lang.get("questEditorEnterBlockStart");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();
            if (input.equalsIgnoreCase(Lang.get("cmdDone")) || input.equalsIgnoreCase(Lang.get("cmdCancel"))) {

                if (input.equalsIgnoreCase(Lang.get("cmdDone"))) {

                    final Block block = selectedBlockStarts.get(player);
                    if (block != null) {
                        final Location loc = block.getLocation();
                        context.setSessionData(CK.Q_START_BLOCK, loc);
                        selectedBlockStarts.remove(player);
                    } else {
                        player.sendMessage(ChatColor.RED + Lang.get("questEditorNoStartBlockSelected"));
                        return new BlockStartPrompt();
                    }

                } else {
                    selectedBlockStarts.remove(player);
                }

                return new CreateMenuPrompt();

            } else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {

                selectedBlockStarts.remove(player);
                context.setSessionData(CK.Q_START_BLOCK, null);
                return new CreateMenuPrompt();

            }

            return new BlockStartPrompt();

        }
    }

    private class SetNamePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ChatColor.YELLOW + Lang.get("questEditorEnterQuestName");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                for (final Quest q: quests.quests) {

                    if (q.name.equalsIgnoreCase(input)) {
                        String s = null;
                        if (context.getSessionData(CK.ED_QUEST_EDIT) != null) {
                            s = (String) context.getSessionData(CK.ED_QUEST_EDIT);
                        }

                        if (s != null && s.equalsIgnoreCase(input) == false) {
                            context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("questEditorNameExists"));
                            return new SetNamePrompt();
                        }
                    }

                }

                if (names.contains(input)) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("questEditorBeingEdited"));
                    return new SetNamePrompt();
                }

                if (input.contains(",")) {

                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("questEditorInvalidQuestName"));
                    return new QuestNamePrompt();

                }

                names.remove(context.getSessionData(CK.Q_NAME));
                context.setSessionData(CK.Q_NAME, input);
                names.add(input);

            }

            return new CreateMenuPrompt();

        }
    }

    private class AskMessagePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ChatColor.YELLOW + Lang.get("questEditorEnterAskMessage");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {
                if (input.startsWith("++")) {
                    if (context.getSessionData(CK.Q_ASK_MESSAGE) != null) {
                        context.setSessionData(CK.Q_ASK_MESSAGE,
                                context.getSessionData(CK.Q_ASK_MESSAGE) + " " + input.substring(2));
                        return new CreateMenuPrompt();
                    }
                }
                context.setSessionData(CK.Q_ASK_MESSAGE, input);
            }

            return new CreateMenuPrompt();

        }
    }

    private class FinishMessagePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ChatColor.YELLOW + Lang.get("questEditorEnterFinishMessage");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {
                if (input.startsWith("++")) {
                    if (context.getSessionData(CK.Q_FINISH_MESSAGE) != null) {
                        context.setSessionData(CK.Q_FINISH_MESSAGE, context.getSessionData(CK.Q_FINISH_MESSAGE) + " "
                                + input.substring(2));
                        return new CreateMenuPrompt();
                    }
                }
                context.setSessionData(CK.Q_FINISH_MESSAGE, input);
            }

            return new CreateMenuPrompt();

        }
    }

    private class InitialEventPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.DARKGREEN + "- " + Lang.get("event") + " -\n";
            if (quests.events.isEmpty()) {
                text += ColorUtil.RED + "- None";
            } else {
                for (final Event e: quests.events) {
                    text += ColorUtil.GREEN + "- " + e.getName() + "\n";
                }
            }

            return text + ColorUtil.YELLOW + Lang.get("questEditorEnterInitialEvent");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
                    && input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {

                Event found = null;

                for (final Event e: quests.events) {

                    if (e.getName().equalsIgnoreCase(input)) {
                        found = e;
                        break;
                    }

                }

                if (found == null) {
                    player.sendMessage(ColorUtil.RED + input + ColorUtil.YELLOW + " "
                            + Lang.get("questEditorInvalidEventName"));
                    return new InitialEventPrompt();
                } else {
                    context.setSessionData(CK.Q_INITIAL_EVENT, found.getName());
                    return new CreateMenuPrompt();
                }

            } else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
                context.setSessionData(CK.Q_INITIAL_EVENT, null);
                player.sendMessage(ColorUtil.YELLOW + Lang.get("questEditorEventCleared"));
                return new CreateMenuPrompt();
            } else {
                return new CreateMenuPrompt();
            }

        }
    }

    private class GUIDisplayPrompt extends FixedSetPrompt {

        public GUIDisplayPrompt() {

            super("1", "2", "3");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            if (context.getSessionData("tempStack") != null) {

                final ItemStack stack = (ItemStack) context.getSessionData("tempStack");
                boolean failed = false;

                for (final Quest quest: quests.quests) {

                    if (quest.guiDisplay != null) {

                        if (ItemUtil.compareItems(stack, quest.guiDisplay, false) != 0) {

                            context.getForWhom()
                                    .sendRawMessage(
                                            ColorUtil.RED
                                                    + "Error: That item is already being used as the GUI Display for the Quest "
                                                    + ColorUtil.PURPLE + quest.name);
                            failed = true;
                            break;

                        }

                    }

                }

                if (!failed) {
                    context.setSessionData(CK.Q_GUIDISPLAY, context.getSessionData("tempStack"));
                }

                context.setSessionData("tempStack", null);

            }

            String text = ColorUtil.GREEN + "- GUI Item Display -\n";
            if (context.getSessionData(CK.Q_GUIDISPLAY) != null) {
                final ItemStack stack = (ItemStack) context.getSessionData(CK.Q_GUIDISPLAY);
                text += ColorUtil.DARKGREEN + "Current item: " + ColorUtil.RESET + ItemUtil.getDisplayString(stack)
                        + "\n\n";
            } else {
                text += ColorUtil.DARKGREEN + "Current item: " + ColorUtil.GRAY + "(None)\n\n";
            }
            text += ColorUtil.GREEN + "" + ColorUtil.BOLD + "1 -" + ColorUtil.RESET + ColorUtil.DARKGREEN
                    + " Set Item\n";
            text += ColorUtil.GREEN + "" + ColorUtil.BOLD + "2 -" + ColorUtil.RESET + ColorUtil.DARKGREEN
                    + " Clear Item\n";
            text += ColorUtil.GREEN + "" + ColorUtil.BOLD + "3 -" + ColorUtil.RESET + ColorUtil.GREEN + " Done\n";

            return text;

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {

                return new ItemStackPrompt(GUIDisplayPrompt.this);

            } else if (input.equalsIgnoreCase("2")) {

                context.setSessionData(CK.Q_GUIDISPLAY, null);
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Quest GUI Item Display cleared.");
                return new GUIDisplayPrompt();

            } else {

                return new CreateMenuPrompt();

            }

        }

    }

    private class RegionPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.DARKGREEN + "- Quest Region -\n";
            boolean any = false;

            for (final World world: quests.getServer().getWorlds()) {

                final RegionManager rm = Quests.worldGuard.getRegionManager(world);
                for (final String region: rm.getRegions().keySet()) {

                    any = true;
                    text += ColorUtil.GREEN + region + ", ";

                }

            }

            if (any) {
                text = text.substring(0, text.length() - 2);
                text += "\n\n";
            } else {
                text += ColorUtil.GRAY + "(None)\n\n";
            }

            return text + ColorUtil.YELLOW
                    + "Enter WorldGuard region, or enter \"clear\" to clear the region, or \"cancel\" to return.";

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            final Player player = (Player) context.getForWhom();

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
                    && input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {

                String found = null;
                boolean done = false;

                for (final World world: quests.getServer().getWorlds()) {

                    final RegionManager rm = Quests.worldGuard.getRegionManager(world);
                    for (final String region: rm.getRegions().keySet()) {

                        if (region.equalsIgnoreCase(input)) {
                            found = region;
                            done = true;
                            break;
                        }

                    }

                    if (done) {
                        break;
                    }
                }

                if (found == null) {
                    player.sendMessage(ColorUtil.RED + input + ColorUtil.YELLOW + " is not a valid WorldGuard region!");
                    return new RegionPrompt();
                } else {
                    context.setSessionData(CK.Q_REGION, found);
                    return new CreateMenuPrompt();
                }

            } else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
                context.setSessionData(CK.Q_REGION, null);
                player.sendMessage(ColorUtil.YELLOW + "Quest region cleared.");
                return new CreateMenuPrompt();
            } else {
                return new CreateMenuPrompt();
            }

        }
    }

    private class RedoDelayPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ChatColor.YELLOW + Lang.get("questEditorEnterRedoDelay");

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
                return new CreateMenuPrompt();
            }
            if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
                context.setSessionData(CK.Q_REDO_DELAY, null);
            }
            long delay;
            try {
                delay = Long.parseLong(input);
            } catch (final NumberFormatException e) {
                context.getForWhom().sendRawMessage(
                        ColorUtil.ITALIC + "" + ColorUtil.RED + input + ColorUtil.RESET + ColorUtil.RED + " "
                                + Lang.get("stageEditorInvalidNumber"));
                // delay = MiscUtil.getTimeFromString(input);
                return new RedoDelayPrompt();
            }

            if (delay < -1) {
                context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("questEditorPositiveAmount"));
            } else if (delay == 0) {
                context.setSessionData(CK.Q_REDO_DELAY, null);
            } else if (delay != -1) {
                context.setSessionData(CK.Q_REDO_DELAY, delay);
            }

            return new CreateMenuPrompt();

        }
    }

    private class SavePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            final String text = ColorUtil.GREEN + "1 - Yes\n" + "2 - No";
            return ChatColor.YELLOW + Lang.get("questEditorSave") + " \"" + ColorUtil.AQUA
                    + context.getSessionData(CK.Q_NAME) + ColorUtil.YELLOW + "\"?\n" + text;

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1") || input.equalsIgnoreCase(Lang.get("yes"))) {

                if (context.getSessionData(CK.Q_ASK_MESSAGE) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("questEditorNeedAskMessage"));
                    return new CreateMenuPrompt();
                } else if (context.getSessionData(CK.Q_FINISH_MESSAGE) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("questEditorNeedFinishMessage"));
                    return new CreateMenuPrompt();
                } else if (StagesPrompt.getStages(context) == 0) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + Lang.get("questEditorNeedStages"));
                    return new CreateMenuPrompt();
                }

                final FileConfiguration data = new YamlConfiguration();
                try {
                    data.load(new File(quests.getDataFolder(), "quests.yml"));
                    final ConfigurationSection questSection = data.getConfigurationSection("quests");

                    int customNum = 1;
                    while (true) {

                        if (questSection.contains("custom" + customNum)) {
                            customNum++;
                        } else {
                            break;
                        }

                    }

                    final ConfigurationSection newSection = questSection.createSection("custom" + customNum);
                    QuestFactory.saveQuest(context, newSection);
                    data.save(new File(quests.getDataFolder(), "quests.yml"));
                    context.getForWhom().sendRawMessage(ColorUtil.BOLD + Lang.get("questEditorSaved"));

                } catch (final IOException e) {
                    e.printStackTrace();
                } catch (final InvalidConfigurationException e) {
                    e.printStackTrace();
                }

                return Prompt.END_OF_CONVERSATION;

            } else if (input.equalsIgnoreCase("2") || input.equalsIgnoreCase(Lang.get("no"))) {
                return new CreateMenuPrompt();
            } else {
                return new SavePrompt();
            }

        }
    }

    private class ExitPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            final String text = ColorUtil.GREEN + "1 - " + Lang.get("yes") + "\n" + "2 - " + Lang.get("no");
            return ChatColor.YELLOW + Lang.get("questEditorExited") + "\n" + text;

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1") || input.equalsIgnoreCase(Lang.get("yes"))) {

                context.getForWhom().sendRawMessage(ColorUtil.BOLD + "" + ColorUtil.YELLOW + Lang.get("exited"));
                return Prompt.END_OF_CONVERSATION;

            } else if (input.equalsIgnoreCase("2") || input.equalsIgnoreCase(Lang.get("no"))) {
                return new CreateMenuPrompt();
            } else {
                return new ExitPrompt();
            }

        }
    }

    public static void saveQuest(ConversationContext cc, ConfigurationSection cs) {

        String edit = null;
        if (cc.getSessionData(CK.ED_QUEST_EDIT) != null) {
            edit = (String) cc.getSessionData(CK.ED_QUEST_EDIT);
        }

        if (edit != null) {

            final ConfigurationSection questList = cs.getParent();

            for (final String key: questList.getKeys(false)) {

                final String name = questList.getString(key + ".name");
                if (name.equalsIgnoreCase(edit)) {

                    questList.set(key, null);
                    break;

                }

            }

        }

        final String name = (String) cc.getSessionData(CK.Q_NAME);
        final String desc = (String) cc.getSessionData(CK.Q_ASK_MESSAGE);
        final String finish = (String) cc.getSessionData(CK.Q_FINISH_MESSAGE);
        Long redo = null;
        Integer npcStart = null;
        String blockStart = null;
        String initialEvent = null;
        String region = null;
        ItemStack guiDisplay = null;

        Integer moneyReq = null;
        Integer questPointsReq = null;
        LinkedList<ItemStack> itemReqs = null;
        LinkedList<Boolean> removeItemReqs = null;
        LinkedList<String> permReqs = null;
        LinkedList<String> questReqs = null;
        LinkedList<String> questBlocks = null;
        LinkedList<String> mcMMOSkillReqs = null;
        LinkedList<Integer> mcMMOAmountReqs = null;
        String heroesPrimaryReq = null;
        String heroesSecondaryReq = null;
        LinkedList<String> customReqs = null;
        LinkedList<Map<String, Object>> customReqsData = null;
        String failMessage = null;

        Integer moneyRew = null;
        Integer questPointsRew = null;
        LinkedList<String> itemRews = null;
        final LinkedList<Integer> RPGItemRews = null;
        final LinkedList<Integer> RPGItemAmounts = null;
        Integer expRew = null;
        LinkedList<String> commandRews = null;
        LinkedList<String> permRews = null;
        LinkedList<String> mcMMOSkillRews = null;
        LinkedList<Integer> mcMMOSkillAmounts = null;
        LinkedList<String> heroesClassRews = null;
        LinkedList<Double> heroesExpRews = null;
        LinkedList<String> phatLootRews = null;
        LinkedList<String> customRews = null;
        LinkedList<Map<String, Object>> customRewsData = null;

        if (cc.getSessionData(CK.Q_REDO_DELAY) != null) {
            redo = (Long) cc.getSessionData(CK.Q_REDO_DELAY);
        }

        if (cc.getSessionData(CK.Q_START_NPC) != null) {
            npcStart = (Integer) cc.getSessionData(CK.Q_START_NPC);
        }

        if (cc.getSessionData(CK.Q_START_BLOCK) != null) {
            blockStart = Quests.getLocationInfo((Location) cc.getSessionData(CK.Q_START_BLOCK));
        }

        if (cc.getSessionData(CK.REQ_MONEY) != null) {
            moneyReq = (Integer) cc.getSessionData(CK.REQ_MONEY);
        }

        if (cc.getSessionData(CK.REQ_QUEST_POINTS) != null) {
            questPointsReq = (Integer) cc.getSessionData(CK.REQ_QUEST_POINTS);
        }

        if (cc.getSessionData(CK.REQ_ITEMS) != null) {
            itemReqs = (LinkedList<ItemStack>) cc.getSessionData(CK.REQ_ITEMS);
            removeItemReqs = (LinkedList<Boolean>) cc.getSessionData(CK.REQ_ITEMS_REMOVE);
        }

        if (cc.getSessionData(CK.REQ_PERMISSION) != null) {
            permReqs = (LinkedList<String>) cc.getSessionData(CK.REQ_PERMISSION);
        }

        if (cc.getSessionData(CK.REQ_QUEST) != null) {
            questReqs = (LinkedList<String>) cc.getSessionData(CK.REQ_QUEST);
        }

        if (cc.getSessionData(CK.REQ_QUEST_BLOCK) != null) {
            questBlocks = (LinkedList<String>) cc.getSessionData(CK.REQ_QUEST_BLOCK);
        }

        if (cc.getSessionData(CK.REQ_MCMMO_SKILLS) != null) {
            mcMMOSkillReqs = (LinkedList<String>) cc.getSessionData(CK.REQ_MCMMO_SKILLS);
            mcMMOAmountReqs = (LinkedList<Integer>) cc.getSessionData(CK.REQ_MCMMO_SKILL_AMOUNTS);
        }

        if (cc.getSessionData(CK.REQ_HEROES_PRIMARY_CLASS) != null) {
            heroesPrimaryReq = (String) cc.getSessionData(CK.REQ_HEROES_PRIMARY_CLASS);
        }

        if (cc.getSessionData(CK.REQ_HEROES_SECONDARY_CLASS) != null) {
            heroesSecondaryReq = (String) cc.getSessionData(CK.REQ_HEROES_SECONDARY_CLASS);
        }

        if (cc.getSessionData(CK.REQ_CUSTOM) != null) {
            customReqs = (LinkedList<String>) cc.getSessionData(CK.REQ_CUSTOM);
            customReqsData = (LinkedList<Map<String, Object>>) cc.getSessionData(CK.REQ_CUSTOM_DATA);
        }

        if (cc.getSessionData(CK.Q_FAIL_MESSAGE) != null) {
            failMessage = (String) cc.getSessionData(CK.Q_FAIL_MESSAGE);
        }

        if (cc.getSessionData(CK.Q_INITIAL_EVENT) != null) {
            initialEvent = (String) cc.getSessionData(CK.Q_INITIAL_EVENT);
        }

        if (cc.getSessionData(CK.Q_REGION) != null) {
            region = (String) cc.getSessionData(CK.Q_REGION);
        }

        if (cc.getSessionData(CK.Q_GUIDISPLAY) != null) {
            guiDisplay = (ItemStack) cc.getSessionData(CK.Q_GUIDISPLAY);
        }

        if (cc.getSessionData(CK.REW_MONEY) != null) {
            moneyRew = (Integer) cc.getSessionData(CK.REW_MONEY);
        }

        if (cc.getSessionData(CK.REW_QUEST_POINTS) != null) {
            questPointsRew = (Integer) cc.getSessionData(CK.REW_QUEST_POINTS);
        }

        if (cc.getSessionData(CK.REW_ITEMS) != null) {
            itemRews = new LinkedList<String>();
            for (final ItemStack is: (LinkedList<ItemStack>) cc.getSessionData(CK.REW_ITEMS)) {
                itemRews.add(ItemUtil.serialize(is));
            }
        }

        if (cc.getSessionData(CK.REW_EXP) != null) {
            expRew = (Integer) cc.getSessionData(CK.REW_EXP);
        }

        if (cc.getSessionData(CK.REW_COMMAND) != null) {
            commandRews = (LinkedList<String>) cc.getSessionData(CK.REW_COMMAND);
        }

        if (cc.getSessionData(CK.REW_PERMISSION) != null) {
            permRews = (LinkedList<String>) cc.getSessionData(CK.REW_PERMISSION);
        }

        if (cc.getSessionData(CK.REW_MCMMO_SKILLS) != null) {
            mcMMOSkillRews = (LinkedList<String>) cc.getSessionData(CK.REW_MCMMO_SKILLS);
            mcMMOSkillAmounts = (LinkedList<Integer>) cc.getSessionData(CK.REW_MCMMO_AMOUNTS);
        }

        if (cc.getSessionData(CK.REW_HEROES_CLASSES) != null) {
            heroesClassRews = (LinkedList<String>) cc.getSessionData(CK.REW_HEROES_CLASSES);
            heroesExpRews = (LinkedList<Double>) cc.getSessionData(CK.REW_HEROES_AMOUNTS);
        }

        if (cc.getSessionData(CK.REW_PHAT_LOOTS) != null) {
            phatLootRews = (LinkedList<String>) cc.getSessionData(CK.REW_PHAT_LOOTS);
        }

        if (cc.getSessionData(CK.REW_CUSTOM) != null) {
            customRews = (LinkedList<String>) cc.getSessionData(CK.REW_CUSTOM);
            customRewsData = (LinkedList<Map<String, Object>>) cc.getSessionData(CK.REW_CUSTOM_DATA);
        }

        cs.set("name", name);
        cs.set("npc-giver-id", npcStart);
        cs.set("block-start", blockStart);
        cs.set("redo-delay", redo);
        cs.set("ask-message", desc);
        cs.set("finish-message", finish);
        cs.set("initial-event", initialEvent);
        cs.set("region", region);
        cs.set("guiDisplay", ItemUtil.serialize(guiDisplay));

        if (moneyReq != null || questPointsReq != null || itemReqs != null && itemReqs.isEmpty() == false
                || permReqs != null && permReqs.isEmpty() == false
                || (questReqs != null && questReqs.isEmpty() == false)
                || (questBlocks != null && questBlocks.isEmpty() == false)
                || (mcMMOSkillReqs != null && mcMMOSkillReqs.isEmpty() == false) || heroesPrimaryReq != null
                || heroesSecondaryReq != null || customReqs != null) {

            final ConfigurationSection reqs = cs.createSection("requirements");
            final List<String> items = new LinkedList<String>();
            if (itemReqs != null) {

                for (final ItemStack is: itemReqs) {
                    items.add(ItemUtil.serialize(is));
                }

            }

            reqs.set("items", (items.isEmpty() == false) ? items : null);
            reqs.set("remove-items", removeItemReqs);
            reqs.set("money", moneyReq);
            reqs.set("quest-points", questPointsReq);
            reqs.set("permissions", permReqs);
            reqs.set("quests", questReqs);
            reqs.set("quest-blocks", questBlocks);
            reqs.set("mcmmo-skills", mcMMOSkillReqs);
            reqs.set("mcmmo-amounts", mcMMOAmountReqs);
            reqs.set("heroes-primary-class", heroesPrimaryReq);
            reqs.set("heroes-secondary-class", heroesSecondaryReq);
            if (customReqs != null) {
                final ConfigurationSection customReqsSec = reqs.createSection("custom-requirements");
                for (int i = 0; i < customReqs.size(); i++) {
                    final ConfigurationSection customReqSec = customReqsSec.createSection("req" + (i + 1));
                    customReqSec.set("name", customReqs.get(i));
                    customReqSec.set("data", customReqsData.get(i));
                }
            }
            reqs.set("fail-requirement-message", failMessage);

        } else {
            cs.set("requirements", null);
        }

        final ConfigurationSection stages = cs.createSection("stages");
        final ConfigurationSection ordered = stages.createSection("ordered");

        String pref;

        LinkedList<Integer> breakIds;
        LinkedList<Integer> breakAmounts;

        LinkedList<Integer> damageIds;
        LinkedList<Integer> damageAmounts;

        LinkedList<Integer> placeIds;
        LinkedList<Integer> placeAmounts;

        LinkedList<Integer> useIds;
        LinkedList<Integer> useAmounts;

        LinkedList<Integer> cutIds;
        LinkedList<Integer> cutAmounts;

        Integer fish;
        Integer players;

        LinkedList<String> enchantments;
        LinkedList<Integer> enchantmentIds;
        LinkedList<Integer> enchantmentAmounts;

        LinkedList<ItemStack> deliveryItems;
        LinkedList<Integer> deliveryNPCIds;
        LinkedList<String> deliveryMessages;

        LinkedList<Integer> npcTalkIds;

        LinkedList<Integer> npcKillIds;
        LinkedList<Integer> npcKillAmounts;

        LinkedList<String> mobs;
        LinkedList<Integer> mobAmounts;
        LinkedList<String> mobLocs;
        LinkedList<Integer> mobRadii;
        LinkedList<String> mobLocNames;

        LinkedList<String> reachLocs;
        LinkedList<Integer> reachRadii;
        LinkedList<String> reachNames;

        LinkedList<String> tames;
        LinkedList<Integer> tameAmounts;

        LinkedList<String> shearColors;
        LinkedList<Integer> shearAmounts;

        LinkedList<String> passDisplays;
        LinkedList<LinkedList<String>> passPhrases;

        LinkedList<String> customObjs;
        LinkedList<Integer> customObjCounts;
        LinkedList<Map<String, Object>> customObjsData;

        String script;
        String startEvent;
        String finishEvent;
        String deathEvent;
        String disconnectEvent;
        LinkedList<String> chatEvents;
        LinkedList<String> chatEventTriggers;
        Long delay;
        String overrideDisplay;
        String delayMessage;
        String startMessage;
        String completeMessage;

        for (int i = 1; i <= StagesPrompt.getStages(cc); i++) {

            pref = "stage" + i;
            final ConfigurationSection stage = ordered.createSection("" + i);

            breakIds = null;
            breakAmounts = null;

            damageIds = null;
            damageAmounts = null;

            placeIds = null;
            placeAmounts = null;

            useIds = null;
            useAmounts = null;

            cutIds = null;
            cutAmounts = null;

            fish = null;
            players = null;

            enchantments = null;
            enchantmentIds = null;
            enchantmentAmounts = null;

            deliveryItems = null;
            deliveryNPCIds = null;
            deliveryMessages = null;

            npcTalkIds = null;

            npcKillIds = null;
            npcKillAmounts = null;

            mobs = null;
            mobAmounts = null;
            mobLocs = null;
            mobRadii = null;
            mobLocNames = null;

            reachLocs = null;
            reachRadii = null;
            reachNames = null;

            tames = null;
            tameAmounts = null;

            shearColors = null;
            shearAmounts = null;

            passDisplays = null;
            passPhrases = null;

            customObjs = null;
            customObjCounts = null;
            customObjsData = null;

            script = null;
            startEvent = null;
            finishEvent = null;
            deathEvent = null;
            disconnectEvent = null;
            chatEvents = null;
            chatEventTriggers = null;
            delay = null;
            overrideDisplay = null;
            delayMessage = null;
            startMessage = null;
            completeMessage = null;

            if (cc.getSessionData(pref + CK.S_BREAK_IDS) != null) {
                breakIds = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_BREAK_IDS);
                breakAmounts = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_BREAK_AMOUNTS);
            }

            if (cc.getSessionData(pref + CK.S_DAMAGE_IDS) != null) {
                damageIds = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_DAMAGE_IDS);
                damageAmounts = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_DAMAGE_AMOUNTS);
            }

            if (cc.getSessionData(pref + CK.S_PLACE_IDS) != null) {
                placeIds = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_PLACE_IDS);
                placeAmounts = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_PLACE_AMOUNTS);
            }

            if (cc.getSessionData(pref + CK.S_USE_IDS) != null) {
                useIds = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_USE_IDS);
                useAmounts = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_USE_AMOUNTS);
            }

            if (cc.getSessionData(pref + CK.S_CUT_IDS) != null) {
                cutIds = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_CUT_IDS);
                cutAmounts = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_CUT_AMOUNTS);
            }

            if (cc.getSessionData(pref + CK.S_FISH) != null) {
                fish = (Integer) cc.getSessionData(pref + CK.S_FISH);
            }

            if (cc.getSessionData(pref + CK.S_PLAYER_KILL) != null) {
                players = (Integer) cc.getSessionData(pref + CK.S_PLAYER_KILL);
            }

            if (cc.getSessionData(pref + CK.S_ENCHANT_TYPES) != null) {
                enchantments = (LinkedList<String>) cc.getSessionData(pref + CK.S_ENCHANT_TYPES);
                enchantmentIds = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_ENCHANT_IDS);
                enchantmentAmounts = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_ENCHANT_AMOUNTS);
            }

            if (cc.getSessionData(pref + CK.S_DELIVERY_ITEMS) != null) {
                deliveryItems = (LinkedList<ItemStack>) cc.getSessionData(pref + CK.S_DELIVERY_ITEMS);
                deliveryNPCIds = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_DELIVERY_NPCS);
                deliveryMessages = (LinkedList<String>) cc.getSessionData(pref + CK.S_DELIVERY_MESSAGES);
            }

            if (cc.getSessionData(pref + CK.S_NPCS_TO_TALK_TO) != null) {
                npcTalkIds = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_NPCS_TO_TALK_TO);
            }

            if (cc.getSessionData(pref + CK.S_NPCS_TO_KILL) != null) {
                npcKillIds = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_NPCS_TO_KILL);
                npcKillAmounts = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_NPCS_TO_KILL_AMOUNTS);
            }

            if (cc.getSessionData(pref + CK.S_MOB_TYPES) != null) {
                mobs = (LinkedList<String>) cc.getSessionData(pref + CK.S_MOB_TYPES);
                mobAmounts = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_MOB_AMOUNTS);
                if (cc.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS) != null) {
                    mobLocs = (LinkedList<String>) cc.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS);
                    mobRadii = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS_RADIUS);
                    mobLocNames = (LinkedList<String>) cc.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS_NAMES);
                }
            }

            if (cc.getSessionData(pref + CK.S_REACH_LOCATIONS) != null) {
                reachLocs = (LinkedList<String>) cc.getSessionData(pref + CK.S_REACH_LOCATIONS);
                reachRadii = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_REACH_LOCATIONS_RADIUS);
                reachNames = (LinkedList<String>) cc.getSessionData(pref + CK.S_REACH_LOCATIONS_NAMES);
            }

            if (cc.getSessionData(pref + CK.S_TAME_TYPES) != null) {
                tames = (LinkedList<String>) cc.getSessionData(pref + CK.S_TAME_TYPES);
                tameAmounts = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_TAME_AMOUNTS);
            }

            if (cc.getSessionData(pref + CK.S_SHEAR_COLORS) != null) {
                shearColors = (LinkedList<String>) cc.getSessionData(pref + CK.S_SHEAR_COLORS);
                shearAmounts = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_SHEAR_AMOUNTS);
            }

            if (cc.getSessionData(pref + CK.S_PASSWORD_DISPLAYS) != null) {
                passDisplays = (LinkedList<String>) cc.getSessionData(pref + CK.S_PASSWORD_DISPLAYS);
                passPhrases = (LinkedList<LinkedList<String>>) cc.getSessionData(pref + CK.S_PASSWORD_PHRASES);
            }

            if (cc.getSessionData(pref + CK.S_CUSTOM_OBJECTIVES) != null) {
                customObjs = (LinkedList<String>) cc.getSessionData(pref + CK.S_CUSTOM_OBJECTIVES);
                customObjCounts = (LinkedList<Integer>) cc.getSessionData(pref + CK.S_CUSTOM_OBJECTIVES_COUNT);
                customObjsData = (LinkedList<Map<String, Object>>) cc
                        .getSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA);
            }

            if (cc.getSessionData(pref + CK.S_START_EVENT) != null) {
                startEvent = (String) cc.getSessionData(pref + CK.S_START_EVENT);
            }

            if (cc.getSessionData(pref + CK.S_FINISH_EVENT) != null) {
                finishEvent = (String) cc.getSessionData(pref + CK.S_FINISH_EVENT);
            }

            if (cc.getSessionData(pref + CK.S_DEATH_EVENT) != null) {
                deathEvent = (String) cc.getSessionData(pref + CK.S_DEATH_EVENT);
            }

            if (cc.getSessionData(pref + CK.S_DISCONNECT_EVENT) != null) {
                disconnectEvent = (String) cc.getSessionData(pref + CK.S_DISCONNECT_EVENT);
            }

            if (cc.getSessionData(pref + CK.S_CHAT_EVENTS) != null) {
                chatEvents = (LinkedList<String>) cc.getSessionData(pref + CK.S_CHAT_EVENTS);
                chatEventTriggers = (LinkedList<String>) cc.getSessionData(pref + CK.S_CHAT_EVENT_TRIGGERS);
            }

            if (cc.getSessionData(pref + CK.S_DELAY) != null) {
                delay = (Long) cc.getSessionData(pref + CK.S_DELAY);
                delayMessage = (String) cc.getSessionData(pref + CK.S_DELAY_MESSAGE);
            }

            if (cc.getSessionData(pref + CK.S_DENIZEN) != null) {
                script = (String) cc.getSessionData(pref + CK.S_DENIZEN);
            }

            if (cc.getSessionData(pref + CK.S_OVERRIDE_DISPLAY) != null) {
                overrideDisplay = (String) cc.getSessionData(pref + CK.S_OVERRIDE_DISPLAY);
            }

            if (cc.getSessionData(pref + CK.S_START_MESSAGE) != null) {
                startMessage = (String) cc.getSessionData(pref + CK.S_START_MESSAGE);
            }

            if (cc.getSessionData(pref + CK.S_COMPLETE_MESSAGE) != null) {
                completeMessage = (String) cc.getSessionData(pref + CK.S_COMPLETE_MESSAGE);
            }

            if (breakIds != null && breakIds.isEmpty() == false) {
                stage.set("break-block-ids", breakIds);
                stage.set("break-block-amounts", breakAmounts);
            }

            if (damageIds != null && damageIds.isEmpty() == false) {
                stage.set("damage-block-ids", damageIds);
                stage.set("damage-block-amounts", damageAmounts);
            }

            if (placeIds != null && placeIds.isEmpty() == false) {
                stage.set("place-block-ids", placeIds);
                stage.set("place-block-amounts", placeAmounts);
            }

            if (useIds != null && useIds.isEmpty() == false) {
                stage.set("use-block-ids", useIds);
                stage.set("use-block-amounts", useAmounts);
            }

            if (cutIds != null && cutIds.isEmpty() == false) {
                stage.set("cut-block-ids", cutIds);
                stage.set("cut-block-amounts", cutAmounts);
            }

            stage.set("fish-to-catch", fish);
            stage.set("players-to-kill", players);
            stage.set("enchantments", enchantments);
            stage.set("enchantment-item-ids", enchantmentIds);
            stage.set("enchantment-amounts", enchantmentAmounts);
            if (deliveryItems != null && deliveryItems.isEmpty() == false) {
                final LinkedList<String> items = new LinkedList<String>();
                for (final ItemStack is: deliveryItems) {
                    items.add(ItemUtil.serialize(is));
                }
                stage.set("items-to-deliver", items);
            } else {
                stage.set("items-to-deliver", null);
            }
            stage.set("npc-delivery-ids", deliveryNPCIds);
            stage.set("delivery-messages", deliveryMessages);
            stage.set("npc-ids-to-talk-to", npcTalkIds);
            stage.set("npc-ids-to-kill", npcKillIds);
            stage.set("npc-kill-amounts", npcKillAmounts);
            stage.set("mobs-to-kill", mobs);
            stage.set("mob-amounts", mobAmounts);
            stage.set("locations-to-kill", mobLocs);
            stage.set("kill-location-radii", mobRadii);
            stage.set("kill-location-names", mobLocNames);
            stage.set("locations-to-reach", reachLocs);
            stage.set("reach-location-radii", reachRadii);
            stage.set("reach-location-names", reachNames);
            stage.set("mobs-to-tame", tames);
            stage.set("mob-tame-amounts", tameAmounts);
            stage.set("sheep-to-shear", shearColors);
            stage.set("sheep-amounts", shearAmounts);
            stage.set("password-displays", passDisplays);
            if (passPhrases != null) {
                final LinkedList<String> toPut = new LinkedList<String>();

                for (final LinkedList<String> list: passPhrases) {

                    String combine = "";
                    for (final String s: list) {
                        if (list.getLast().equals(s) == false) {
                            combine += s + "|";
                        } else {
                            combine += s;
                        }
                    }
                    toPut.add(combine);

                }

                stage.set("password-phrases", toPut);
            }
            if (customObjs != null && customObjs.isEmpty() == false) {

                final ConfigurationSection sec = stage.createSection("custom-objectives");
                for (int index = 0; index < customObjs.size(); index++) {

                    final ConfigurationSection sec2 = sec.createSection("custom" + (index + 1));
                    sec2.set("name", customObjs.get(index));
                    sec2.set("count", customObjCounts.get(index));
                    if (customObjsData.get(index).isEmpty() == false) {
                        sec2.set("data", customObjsData.get(index));
                    }

                }

            }
            stage.set("script-to-run", script);
            stage.set("start-event", startEvent);
            stage.set("finish-event", finishEvent);
            stage.set("death-event", deathEvent);
            stage.set("disconnect-event", disconnectEvent);
            if (chatEvents != null && chatEvents.isEmpty() == false) {
                stage.set("chat-events", chatEvents);
                stage.set("chat-event-triggers", chatEventTriggers);
            }
            stage.set("delay", delay);
            stage.set("delay-message", delayMessage);
            stage.set("objective-override", overrideDisplay);
            stage.set("start-message", startMessage);
            stage.set("complete-message", completeMessage);

        }

        if (moneyRew != null || questPointsRew != null || itemRews != null && itemRews.isEmpty() == false
                || permRews != null && permRews.isEmpty() == false || expRew != null || commandRews != null
                && commandRews.isEmpty() == false || mcMMOSkillRews != null || RPGItemRews != null
                || heroesClassRews != null && heroesClassRews.isEmpty() == false || phatLootRews != null
                && phatLootRews.isEmpty() == false || customRews != null && customRews.isEmpty() == false) {

            final ConfigurationSection rews = cs.createSection("rewards");

            rews.set("items", (itemRews != null && itemRews.isEmpty() == false) ? itemRews : null);
            rews.set("money", moneyRew);
            rews.set("quest-points", questPointsRew);
            rews.set("exp", expRew);
            rews.set("permissions", permRews);
            rews.set("commands", commandRews);
            rews.set("mcmmo-skills", mcMMOSkillRews);
            rews.set("mcmmo-levels", mcMMOSkillAmounts);
            rews.set("rpgitem-ids", RPGItemRews);
            rews.set("rpgitem-amounts", RPGItemAmounts);
            rews.set("heroes-exp-classes", heroesClassRews);
            rews.set("heroes-exp-amounts", heroesExpRews);
            rews.set("phat-loots", phatLootRews);

            if (customRews != null) {
                final ConfigurationSection customRewsSec = rews.createSection("custom-rewards");
                for (int i = 0; i < customRews.size(); i++) {
                    final ConfigurationSection customRewSec = customRewsSec.createSection("req" + (i + 1));
                    customRewSec.set("name", customRews.get(i));
                    customRewSec.set("data", customRewsData.get(i));
                }
            }

        } else {
            cs.set("rewards", null);
        }

    }

    public static void loadQuest(ConversationContext cc, Quest q) {

        cc.setSessionData(CK.ED_QUEST_EDIT, q.name);
        cc.setSessionData(CK.Q_NAME, q.name);
        if (q.npcStart != null) {
            cc.setSessionData(CK.Q_START_NPC, q.npcStart.getId());
        }
        cc.setSessionData(CK.Q_START_BLOCK, q.blockStart);
        if (q.redoDelay != -1) {
            cc.setSessionData(CK.Q_REDO_DELAY, q.redoDelay);
        }
        cc.setSessionData(CK.Q_ASK_MESSAGE, q.description);
        cc.setSessionData(CK.Q_FINISH_MESSAGE, q.finished);
        if (q.initialEvent != null) {
            cc.setSessionData(CK.Q_INITIAL_EVENT, q.initialEvent.getName());
        }

        if (q.region != null) {
            cc.setSessionData(CK.Q_REGION, q.region);
        }

        // Requirements
        if (q.moneyReq != 0) {
            cc.setSessionData(CK.REQ_MONEY, q.moneyReq);
        }
        if (q.questPointsReq != 0) {
            cc.setSessionData(CK.REQ_QUEST_POINTS, q.questPointsReq);
        }

        if (q.items.isEmpty() == false) {

            cc.setSessionData(CK.REQ_ITEMS, q.items);
            cc.setSessionData(CK.REQ_ITEMS_REMOVE, q.removeItems);

        }

        if (q.neededQuests.isEmpty() == false) {
            cc.setSessionData(CK.REQ_QUEST, q.neededQuests);
        }

        if (q.blockQuests.isEmpty() == false) {
            cc.setSessionData(CK.REQ_QUEST_BLOCK, q.blockQuests);
        }

        if (q.mcMMOSkillReqs.isEmpty() == false) {
            cc.setSessionData(CK.REQ_MCMMO_SKILLS, q.mcMMOSkillReqs);
            cc.setSessionData(CK.REQ_MCMMO_SKILL_AMOUNTS, q.mcMMOAmountReqs);
        }

        if (q.permissionReqs.isEmpty() == false) {
            cc.setSessionData(CK.REQ_PERMISSION, q.permissionReqs);
        }

        if (q.heroesPrimaryClassReq != null) {
            cc.setSessionData(CK.REQ_HEROES_PRIMARY_CLASS, q.heroesPrimaryClassReq);
        }

        if (q.heroesSecondaryClassReq != null) {
            cc.setSessionData(CK.REQ_HEROES_SECONDARY_CLASS, q.heroesSecondaryClassReq);
        }

        if (q.mcMMOSkillReqs.isEmpty() == false) {
            cc.setSessionData(CK.REQ_MCMMO_SKILLS, q.mcMMOSkillReqs);
            cc.setSessionData(CK.REQ_MCMMO_SKILL_AMOUNTS, q.mcMMOAmountReqs);
        }

        if (q.failRequirements != null) {
            cc.setSessionData(CK.Q_FAIL_MESSAGE, q.failRequirements);
        }

        if (q.customRequirements.isEmpty() == false) {

            final LinkedList<String> list = new LinkedList<String>();
            final LinkedList<Map<String, Object>> datamapList = new LinkedList<Map<String, Object>>();

            for (final Entry<String, Map<String, Object>> entry: q.customRequirements.entrySet()) {

                list.add(entry.getKey());
                datamapList.add(entry.getValue());

            }

            cc.setSessionData(CK.REQ_CUSTOM, list);
            cc.setSessionData(CK.REQ_CUSTOM_DATA, datamapList);

        }
        //

        // Rewards
        if (q.moneyReward != 0) {
            cc.setSessionData(CK.REW_MONEY, q.moneyReward);
        }

        if (q.questPoints != 0) {
            cc.setSessionData(CK.REW_QUEST_POINTS, q.questPoints);
        }

        if (q.exp != 0) {
            cc.setSessionData(CK.REW_EXP, q.exp);
        }

        if (q.itemRewards.isEmpty() == false) {
            cc.setSessionData(CK.REW_ITEMS, q.itemRewards);
        }

        if (q.commands.isEmpty() == false) {
            cc.setSessionData(CK.REW_COMMAND, q.commands);
        }

        if (q.permissions.isEmpty() == false) {
            cc.setSessionData(CK.REW_PERMISSION, q.permissions);
        }

        if (q.mcmmoSkills.isEmpty() == false) {
            cc.setSessionData(CK.REW_MCMMO_SKILLS, q.mcmmoSkills);
            cc.setSessionData(CK.REW_MCMMO_AMOUNTS, q.mcmmoAmounts);
        }

        if (q.heroesClasses.isEmpty() == false) {
            cc.setSessionData(CK.REW_HEROES_CLASSES, q.heroesClasses);
            cc.setSessionData(CK.REW_HEROES_AMOUNTS, q.heroesAmounts);
        }

        if (q.heroesClasses.isEmpty() == false) {
            cc.setSessionData(CK.REW_HEROES_CLASSES, q.heroesClasses);
            cc.setSessionData(CK.REW_HEROES_AMOUNTS, q.heroesAmounts);
        }

        if (q.phatLootRewards.isEmpty() == false) {
            cc.setSessionData(CK.REW_PHAT_LOOTS, q.phatLootRewards);
        }

        if (q.customRewards.isEmpty() == false) {
            cc.setSessionData(CK.REW_CUSTOM, q.customRewards);
        }
        //

        // Stages
        for (final Stage stage: q.orderedStages) {
            final String pref = "stage" + (q.orderedStages.indexOf(stage) + 1);
            cc.setSessionData(pref, Boolean.TRUE);

            if (stage.blocksToBreak != null) {

                final LinkedList<Integer> ids = new LinkedList<Integer>();
                final LinkedList<Integer> amnts = new LinkedList<Integer>();

                for (final Entry<Material, Integer> e: stage.blocksToBreak.entrySet()) {

                    ids.add(e.getKey().getId());
                    amnts.add(e.getValue());

                }

                cc.setSessionData(pref + CK.S_BREAK_IDS, ids);
                cc.setSessionData(pref + CK.S_BREAK_AMOUNTS, amnts);

            }

            if (stage.blocksToDamage != null) {

                final LinkedList<Integer> ids = new LinkedList<Integer>();
                final LinkedList<Integer> amnts = new LinkedList<Integer>();

                for (final Entry<Material, Integer> e: stage.blocksToDamage.entrySet()) {

                    ids.add(e.getKey().getId());
                    amnts.add(e.getValue());

                }

                cc.setSessionData(pref + CK.S_DAMAGE_IDS, ids);
                cc.setSessionData(pref + CK.S_DAMAGE_AMOUNTS, amnts);

            }

            if (stage.blocksToPlace != null) {

                final LinkedList<Integer> ids = new LinkedList<Integer>();
                final LinkedList<Integer> amnts = new LinkedList<Integer>();

                for (final Entry<Material, Integer> e: stage.blocksToPlace.entrySet()) {

                    ids.add(e.getKey().getId());
                    amnts.add(e.getValue());

                }

                cc.setSessionData(pref + CK.S_PLACE_IDS, ids);
                cc.setSessionData(pref + CK.S_PLACE_AMOUNTS, amnts);

            }

            if (stage.blocksToUse != null) {

                final LinkedList<Integer> ids = new LinkedList<Integer>();
                final LinkedList<Integer> amnts = new LinkedList<Integer>();

                for (final Entry<Material, Integer> e: stage.blocksToUse.entrySet()) {

                    ids.add(e.getKey().getId());
                    amnts.add(e.getValue());

                }

                cc.setSessionData(pref + CK.S_USE_IDS, ids);
                cc.setSessionData(pref + CK.S_USE_AMOUNTS, amnts);

            }

            if (stage.blocksToCut != null) {

                final LinkedList<Integer> ids = new LinkedList<Integer>();
                final LinkedList<Integer> amnts = new LinkedList<Integer>();

                for (final Entry<Material, Integer> e: stage.blocksToCut.entrySet()) {

                    ids.add(e.getKey().getId());
                    amnts.add(e.getValue());

                }

                cc.setSessionData(pref + CK.S_CUT_IDS, ids);
                cc.setSessionData(pref + CK.S_CUT_AMOUNTS, amnts);

            }

            if (stage.fishToCatch != null) {
                cc.setSessionData(pref + CK.S_FISH, stage.fishToCatch);
            }

            if (stage.playersToKill != null) {
                cc.setSessionData(pref + CK.S_PLAYER_KILL, stage.playersToKill);
            }

            if (stage.itemsToEnchant.isEmpty() == false) {

                final LinkedList<String> enchants = new LinkedList<String>();
                final LinkedList<Integer> ids = new LinkedList<Integer>();
                final LinkedList<Integer> amounts = new LinkedList<Integer>();

                for (final Entry<Map<Enchantment, Material>, Integer> e: stage.itemsToEnchant.entrySet()) {

                    amounts.add(e.getValue());
                    for (final Entry<Enchantment, Material> e2: e.getKey().entrySet()) {

                        ids.add(e2.getValue().getId());
                        enchants.add(Quester.prettyEnchantmentString(e2.getKey()));

                    }

                }

                cc.setSessionData(pref + CK.S_ENCHANT_TYPES, enchants);
                cc.setSessionData(pref + CK.S_ENCHANT_IDS, ids);
                cc.setSessionData(pref + CK.S_ENCHANT_AMOUNTS, amounts);

            }

            if (stage.itemsToDeliver.isEmpty() == false) {

                final LinkedList<ItemStack> items = new LinkedList<ItemStack>();
                final LinkedList<Integer> npcs = new LinkedList<Integer>();

                for (final ItemStack is: stage.itemsToDeliver) {
                    items.add(is);
                }

                for (final Integer n: stage.itemDeliveryTargets) {
                    npcs.add(n);
                }

                cc.setSessionData(pref + CK.S_DELIVERY_ITEMS, items);
                cc.setSessionData(pref + CK.S_DELIVERY_NPCS, npcs);
                cc.setSessionData(pref + CK.S_DELIVERY_MESSAGES, stage.deliverMessages);

            }

            if (stage.citizensToInteract.isEmpty() == false) {

                final LinkedList<Integer> npcs = new LinkedList<Integer>();
                for (final Integer n: stage.citizensToInteract) {
                    npcs.add(n);
                }

                cc.setSessionData(pref + CK.S_NPCS_TO_TALK_TO, npcs);

            }

            if (stage.citizensToKill.isEmpty() == false) {

                final LinkedList<Integer> npcs = new LinkedList<Integer>();
                for (final Integer n: stage.citizensToKill) {
                    npcs.add(n);
                }

                cc.setSessionData(pref + CK.S_NPCS_TO_KILL, npcs);
                cc.setSessionData(pref + CK.S_NPCS_TO_KILL_AMOUNTS, stage.citizenNumToKill);

            }

            if (stage.mobsToKill.isEmpty() == false) {

                final LinkedList<String> mobs = new LinkedList<String>();
                for (final EntityType et: stage.mobsToKill) {
                    mobs.add(Quester.prettyMobString(et));
                }

                cc.setSessionData(pref + CK.S_MOB_TYPES, mobs);
                cc.setSessionData(pref + CK.S_MOB_AMOUNTS, stage.mobNumToKill);

                if (stage.locationsToKillWithin.isEmpty() == false) {

                    final LinkedList<String> locs = new LinkedList<String>();
                    for (final Location l: stage.locationsToKillWithin) {
                        locs.add(Quests.getLocationInfo(l));
                    }

                    cc.setSessionData(pref + CK.S_MOB_KILL_LOCATIONS, locs);
                    cc.setSessionData(pref + CK.S_MOB_KILL_LOCATIONS_RADIUS, stage.radiiToKillWithin);
                    cc.setSessionData(pref + CK.S_MOB_KILL_LOCATIONS_NAMES, stage.areaNames);

                }

            }

            if (stage.locationsToReach.isEmpty() == false) {

                final LinkedList<String> locs = new LinkedList<String>();
                for (final Location l: stage.locationsToReach) {
                    locs.add(Quests.getLocationInfo(l));
                }

                cc.setSessionData(pref + CK.S_REACH_LOCATIONS, locs);
                cc.setSessionData(pref + CK.S_REACH_LOCATIONS_RADIUS, stage.radiiToReachWithin);
                cc.setSessionData(pref + CK.S_REACH_LOCATIONS_NAMES, stage.locationNames);

            }

            if (stage.mobsToTame.isEmpty() == false) {

                final LinkedList<String> mobs = new LinkedList<String>();
                final LinkedList<Integer> amnts = new LinkedList<Integer>();

                for (final Entry<EntityType, Integer> e: stage.mobsToTame.entrySet()) {

                    mobs.add(Quester.prettyMobString(e.getKey()));
                    amnts.add(e.getValue());

                }

                cc.setSessionData(pref + CK.S_TAME_TYPES, mobs);
                cc.setSessionData(pref + CK.S_TAME_AMOUNTS, amnts);

            }

            if (stage.sheepToShear.isEmpty() == false) {

                final LinkedList<String> colors = new LinkedList<String>();
                final LinkedList<Integer> amnts = new LinkedList<Integer>();

                for (final Entry<DyeColor, Integer> e: stage.sheepToShear.entrySet()) {
                    colors.add(Quester.prettyColorString(e.getKey()));
                    amnts.add(e.getValue());
                }

                cc.setSessionData(pref + CK.S_SHEAR_COLORS, colors);
                cc.setSessionData(pref + CK.S_SHEAR_AMOUNTS, amnts);

            }

            if (stage.passwordDisplays.isEmpty() == false) {

                cc.setSessionData(pref + CK.S_PASSWORD_DISPLAYS, stage.passwordDisplays);
                cc.setSessionData(pref + CK.S_PASSWORD_PHRASES, stage.passwordPhrases);

            }

            if (stage.customObjectives.isEmpty() == false) {

                final LinkedList<String> list = new LinkedList<String>();
                final LinkedList<Integer> countList = new LinkedList<Integer>();
                final LinkedList<Map<String, Object>> datamapList = new LinkedList<Map<String, Object>>();

                for (int i = 0; i < stage.customObjectives.size(); i++) {

                    list.add(stage.customObjectives.get(i).getName());
                    countList.add(stage.customObjectiveCounts.get(i));
                    datamapList.add(stage.customObjectiveData.get(i));

                }

                cc.setSessionData(pref + CK.S_CUSTOM_OBJECTIVES, list);
                cc.setSessionData(pref + CK.S_CUSTOM_OBJECTIVES_COUNT, countList);
                cc.setSessionData(pref + CK.S_CUSTOM_OBJECTIVES_DATA, datamapList);

            }

            if (stage.startEvent != null) {
                cc.setSessionData(pref + CK.S_START_EVENT, stage.startEvent.getName());
            }

            if (stage.finishEvent != null) {
                cc.setSessionData(pref + CK.S_FINISH_EVENT, stage.finishEvent.getName());
            }

            if (stage.deathEvent != null) {
                cc.setSessionData(pref + CK.S_DEATH_EVENT, stage.deathEvent.getName());
            }
            if (stage.disconnectEvent != null) {
                cc.setSessionData(pref + CK.S_DISCONNECT_EVENT, stage.disconnectEvent.getName());
            }
            if (stage.chatEvents != null) {

                final LinkedList<String> chatEvents = new LinkedList<String>();
                final LinkedList<String> chatEventTriggers = new LinkedList<String>();

                for (final String s: stage.chatEvents.keySet()) {
                    chatEventTriggers.add(s);
                    chatEvents.add(stage.chatEvents.get(s).getName());
                }

                cc.setSessionData(pref + CK.S_CHAT_EVENTS, chatEvents);
                cc.setSessionData(pref + CK.S_CHAT_EVENT_TRIGGERS, chatEventTriggers);

            }

            if (stage.delay != -1) {
                cc.setSessionData(pref + CK.S_DELAY, stage.delay);
                if (stage.delayMessage != null) {
                    cc.setSessionData(pref + CK.S_DELAY_MESSAGE, stage.delayMessage);
                }
            }

            if (stage.script != null) {
                cc.setSessionData(pref + CK.S_DENIZEN, stage.script);
            }

            if (stage.objectiveOverride != null) {
                cc.setSessionData(pref + CK.S_OVERRIDE_DISPLAY, stage.objectiveOverride);
            }

            if (stage.completeMessage != null) {
                cc.setSessionData(pref + CK.S_COMPLETE_MESSAGE, stage.completeMessage);
            }

            if (stage.startMessage != null) {
                cc.setSessionData(pref + CK.S_START_MESSAGE, stage.startMessage);
            }

        }
        //

    }

    private class SelectDeletePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- " + Lang.get("questEditorDelete") + " -\n";

            for (final Quest quest: quests.quests) {
                text += ColorUtil.AQUA + quest.name + ColorUtil.YELLOW + ",";
            }

            text = text.substring(0, text.length() - 1) + "\n";
            text += ColorUtil.YELLOW + Lang.get("questEditorEnterQuestName");

            return text;

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {

                final LinkedList<String> used = new LinkedList<String>();

                final Quest found = quests.findQuest(input);

                if (found != null) {

                    for (final Quest q: quests.quests) {

                        if (q.neededQuests.contains(q.name) || q.blockQuests.contains(q.name)) {
                            used.add(q.name);
                        }

                    }

                    if (used.isEmpty()) {
                        context.setSessionData(CK.ED_QUEST_DELETE, found.name);
                        return new DeletePrompt();
                    } else {
                        ((Player) context.getForWhom()).sendMessage(ColorUtil.RED
                                + Lang.get("questEditorQuestAsRequirement1") + " \"" + ColorUtil.PURPLE
                                + context.getSessionData(CK.ED_QUEST_DELETE) + ColorUtil.RED + "\" "
                                + Lang.get("questEditorQuestAsRequirement2"));
                        for (final String s: used) {
                            ((Player) context.getForWhom()).sendMessage(ColorUtil.RED + "- " + ColorUtil.DARKRED + s);
                        }
                        ((Player) context.getForWhom()).sendMessage(ColorUtil.RED
                                + Lang.get("questEditorQuestAsRequirement3"));
                        return new SelectDeletePrompt();
                    }

                }

                ((Player) context.getForWhom()).sendMessage(ColorUtil.RED + Lang.get("questEditorQuestNotFound"));
                return new SelectDeletePrompt();

            } else {
                return new MenuPrompt();
            }

        }
    }

    private class DeletePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.RED + Lang.get("questEditorDeleted") + " \"" + ColorUtil.GOLD
                    + (String) context.getSessionData(CK.ED_QUEST_DELETE) + ColorUtil.RED + "\"?\n";
            text += ColorUtil.YELLOW + Lang.get("yes") + "/" + Lang.get("no");

            return text;

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase(Lang.get("yes"))) {
                deleteQuest(context);
                return Prompt.END_OF_CONVERSATION;
            } else if (input.equalsIgnoreCase(Lang.get("no"))) {
                return new MenuPrompt();
            } else {
                return new DeletePrompt();
            }

        }
    }

    private void deleteQuest(ConversationContext context) {

        final YamlConfiguration data = new YamlConfiguration();

        try {
            data.load(questsFile);
        } catch (final IOException e) {
            e.printStackTrace();
            ((Player) context.getForWhom()).sendMessage(ChatColor.RED + "Error reading Quests file.");
            return;
        } catch (final InvalidConfigurationException e) {
            e.printStackTrace();
            ((Player) context.getForWhom()).sendMessage(ChatColor.RED + "Error reading Quests file.");
            return;
        }

        final String quest = (String) context.getSessionData(CK.ED_QUEST_DELETE);
        final ConfigurationSection sec = data.getConfigurationSection("quests");
        for (final String key: sec.getKeys(false)) {

            if (sec.getString(key + ".name").equalsIgnoreCase(quest)) {
                sec.set(key, null);
                break;
            }

        }

        try {
            data.save(questsFile);
        } catch (final IOException e) {
            ((Player) context.getForWhom()).sendMessage(ChatColor.RED + "An error occurred while saving.");
            return;
        }

        quests.reloadQuests();

        context.getForWhom().sendRawMessage(
                ColorUtil.WHITE + "" + ColorUtil.BOLD + "Quest deleted! Quests and Events have been reloaded.");

    }
}
