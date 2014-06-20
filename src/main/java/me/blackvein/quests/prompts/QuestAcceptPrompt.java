package me.blackvein.quests.prompts;

import java.text.MessageFormat;
import java.util.LinkedList;

import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;
import me.blackvein.quests.util.ColorUtil;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class QuestAcceptPrompt extends StringPrompt implements ColorUtil {

    final Quests      plugin;
    Quester           quester;
    LinkedList<Quest> quests;

    public QuestAcceptPrompt(Quests plugin) {

        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext cc) {

        quests = (LinkedList<Quest>) cc.getSessionData("quests");
        quester = plugin.getQuester(((Player) cc.getForWhom()).getName());

        final String npc = (String) cc.getSessionData("npc");
        String menu = ColorUtil.YELLOW + "- " + ColorUtil.GOLD + "Quests" + " | " + npc + ColorUtil.YELLOW + " -\n";
        for (int i = 1; i <= quests.size(); i++) {

            final Quest quest = quests.get(i - 1);
            if (quester.completedQuests.contains(quest.getName())) {
                menu += ColorUtil.DARKGREEN + "" + ColorUtil.BOLD + "" + i + ". " + ColorUtil.RESET + ""
                        + ColorUtil.GREEN + "" + ColorUtil.ITALIC + quest.getName() + ColorUtil.RESET + ""
                        + ColorUtil.GREEN + " (Completed)\n";
            } else {
                menu += ColorUtil.GOLD + "" + ColorUtil.BOLD + "" + i + ". " + ColorUtil.RESET + "" + ColorUtil.YELLOW
                        + "" + ColorUtil.ITALIC + quest.getName() + "\n";
            }

        }

        menu += ColorUtil.GOLD + "" + ColorUtil.BOLD + "" + (quests.size() + 1) + ". " + ColorUtil.RESET + ""
                + ColorUtil.GRAY + "Cancel\n";
        menu += ColorUtil.WHITE + "Enter an option";

        return menu;
    }

    @Override
    public Prompt acceptInput(ConversationContext cc, String input) {

        int numInput = -1;
        try {
            numInput = Integer.parseInt(input);
        } catch (final NumberFormatException e) {
            // Continue
        }

        if (input.equalsIgnoreCase("Cancel") || numInput == (quests.size() + 1)) {
            cc.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Cancelled.");
            return Prompt.END_OF_CONVERSATION;
        } else {

            Quest q = null;

            for (final Quest quest: quests) {

                if (quest.getName().equalsIgnoreCase(input)) {
                    q = quest;
                    break;
                }

            }

            if (q == null) {
                for (final Quest quest: quests) {

                    if (numInput == (quests.indexOf(quest) + 1)) {
                        q = quest;
                        break;
                    }

                }
            }

            if (q == null) {
                for (final Quest quest: quests) {

                    if (StringUtils.containsIgnoreCase(quest.getName(), input)) {
                        q = quest;
                        break;
                    }

                }
            }

            if (q == null) {
                cc.getForWhom().sendRawMessage(ColorUtil.RED + "Invalid Selection!");
                return new QuestAcceptPrompt(plugin);
            } else {

                final Player player = quester.getPlayer();

                if (!quester.completedQuests.contains(q.name)) {

                    if (quester.currentQuest == null) {

                        if (q.testRequirements(quester)) {

                            quester.questToTake = q.name;

                            final String s = extracted(quester);

                            for (final String msg: s.split("<br>")) {
                                player.sendMessage(msg);
                            }

                            plugin.conversationFactory.buildConversation(player).begin();

                        } else {
                            player.sendMessage(q.failRequirements);
                        }

                    } else if (quester.currentQuest.equals(q) == false) {

                        player.sendMessage(ChatColor.YELLOW + "You may only have one active Quest.");

                    }

                } else if (quester.completedQuests.contains(q.name)) {

                    if (quester.currentQuest == null) {

                        if (quester.getDifference(q) > 0) {
                            player.sendMessage(ChatColor.YELLOW + "You may not take " + ChatColor.AQUA + q.name
                                    + ChatColor.YELLOW + " again for another " + ChatColor.DARK_PURPLE
                                    + Quests.getTime(quester.getDifference(q)) + ChatColor.YELLOW + ".");
                        } else if (q.redoDelay < 0) {
                            player.sendMessage(ChatColor.YELLOW + "You have already completed " + ChatColor.AQUA
                                    + q.name + ChatColor.YELLOW + ".");
                        } else {
                            quester.questToTake = q.name;
                            final String s = extracted(quester);

                            for (final String msg: s.split("<br>")) {
                                player.sendMessage(msg);
                            }

                            plugin.conversationFactory.buildConversation(player).begin();
                        }

                    } else if (quester.currentQuest.equals(q) == false) {

                        player.sendMessage(ChatColor.YELLOW + "You may only have one active Quest.");

                    }

                }

                return Prompt.END_OF_CONVERSATION;

            }

        }

    }

    private String extracted(final Quester quester) {
        return MessageFormat.format("{0}- {1}{2}{3} -\n\n{4}{5}\n", ChatColor.GOLD, ChatColor.DARK_PURPLE,
                quester.questToTake, ChatColor.GOLD, ChatColor.RESET, plugin.getQuest(quester.questToTake).description);
    }
}
