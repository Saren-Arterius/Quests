package me.blackvein.quests.prompts;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.blackvein.quests.CustomReward;
import me.blackvein.quests.QuestFactory;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.ColorUtil;
import me.blackvein.quests.util.ItemUtil;
import me.blackvein.quests.util.Lang;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.inventory.ItemStack;

import com.codisimus.plugins.phatloots.PhatLoot;
import com.codisimus.plugins.phatloots.PhatLootsAPI;
import com.herocraftonline.heroes.characters.classes.HeroClass;

public class RewardsPrompt extends FixedSetPrompt implements ColorUtil {

    final Quests       quests;

    final QuestFactory factory;

    public RewardsPrompt(Quests plugin, QuestFactory qf) {

        super("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11");
        quests = plugin;
        factory = qf;

    }

    @Override
    public String getPromptText(ConversationContext context) {

        String text;

        text = ColorUtil.DARKAQUA + "- " + ColorUtil.AQUA + context.getSessionData(CK.Q_NAME) + ColorUtil.AQUA
                + " | Rewards -\n";

        if (context.getSessionData(CK.REW_MONEY) == null) {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set money reward (None set)\n";
        } else {
            final int moneyRew = (Integer) context.getSessionData(CK.REW_MONEY);
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set money reward (" + moneyRew + " "
                    + (moneyRew > 1 ? Quests.getCurrency(true) : Quests.getCurrency(false)) + ")\n";
        }

        if (context.getSessionData(CK.REW_QUEST_POINTS) == null) {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set Quest Points reward (None set)\n";
        } else {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set Quest Points reward (" + context.getSessionData(CK.REW_QUEST_POINTS) + " Quest Points)\n";
        }

        text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW
                + " - Set item rewards\n";

        if (context.getSessionData(CK.REW_EXP) == null) {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set experience reward (None set)\n";
        } else {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set experience reward (" + context.getSessionData(CK.REW_EXP) + " points)\n";
        }

        if (context.getSessionData(CK.REW_COMMAND) == null) {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "5" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set command rewards (None set)\n";
        } else {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "5" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set command rewards\n";
            final List<String> commands = (List<String>) context.getSessionData(CK.REW_COMMAND);

            for (final String cmd: commands) {

                text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + cmd + "\n";

            }
        }

        if (context.getSessionData(CK.REW_PERMISSION) == null) {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "6" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set permission rewards (None set)\n";
        } else {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "6" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set permission rewards\n";
            final List<String> permissions = (List<String>) context.getSessionData(CK.REW_PERMISSION);

            for (final String perm: permissions) {

                text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + perm + "\n";

            }
        }

        if (Quests.mcmmo != null) {

            if (context.getSessionData(CK.REW_MCMMO_SKILLS) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "7" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Set mcMMO skill rewards (None set)\n";
            } else {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "7" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Set mcMMO skill rewards\n";
                final List<String> skills = (List<String>) context.getSessionData(CK.REW_MCMMO_SKILLS);
                final List<Integer> amounts = (List<Integer>) context.getSessionData(CK.REW_MCMMO_AMOUNTS);

                for (final String skill: skills) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + skill + ColorUtil.GRAY + " x "
                            + ColorUtil.DARKAQUA + amounts.get(skills.indexOf(skill)) + "\n";

                }
            }

        } else {

            text += ColorUtil.GRAY + "7 - Set mcMMO skill rewards (mcMMO not installed)\n";

        }

        if (Quests.heroes != null) {

            if (context.getSessionData(CK.REW_HEROES_CLASSES) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "8" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Set Heroes experience rewards (None set)\n";
            } else {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "8" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Set Heroes experience rewards\n";
                final List<String> heroClasses = (List<String>) context.getSessionData(CK.REW_HEROES_CLASSES);
                final List<Double> amounts = (List<Double>) context.getSessionData(CK.REW_HEROES_AMOUNTS);

                for (final String heroClass: heroClasses) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + amounts.get(heroClasses.indexOf(heroClass))
                            + " " + ColorUtil.DARKAQUA + heroClass + " Experience\n";

                }
            }

        } else {

            text += ColorUtil.GRAY + "8 - Set Heroes experience rewards (Heroes not installed)\n";

        }

        if (Quests.phatLoots != null) {

            if (context.getSessionData(CK.REW_PHAT_LOOTS) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "9" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Set PhatLoot rewards (None set)\n";
            } else {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "9" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Set PhatLoot rewards\n";
                final List<String> phatLoots = (List<String>) context.getSessionData(CK.REW_PHAT_LOOTS);

                for (final String phatLoot: phatLoots) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + phatLoot + "\n";

                }
            }

        } else {

            text += ColorUtil.GRAY + "9 - Set PhatLoot rewards (PhatLoots not installed)\n";

        }

        if (context.getSessionData(CK.REW_CUSTOM) == null) {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "10 - " + ColorUtil.RESET + ColorUtil.ITALIC
                    + ColorUtil.PURPLE + "Custom Rewards (None set)\n";
        } else {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "10 - " + ColorUtil.RESET + ColorUtil.ITALIC
                    + ColorUtil.PURPLE + "Custom Rewards\n";
            final LinkedList<String> customRews = (LinkedList<String>) context.getSessionData(CK.REW_CUSTOM);
            for (final String s: customRews) {

                text += ColorUtil.RESET + "" + ColorUtil.PURPLE + "  - " + ColorUtil.PINK + s + "\n";

            }
        }

        text += ColorUtil.GREEN + "" + ColorUtil.BOLD + "11" + ColorUtil.RESET + ColorUtil.YELLOW + " - Done";

        return text;

    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, String input) {

        if (input.equalsIgnoreCase("1")) {
            return new MoneyPrompt();
        } else if (input.equalsIgnoreCase("2")) {
            return new QuestPointsPrompt();
        } else if (input.equalsIgnoreCase("3")) {
            return new ItemListPrompt();
        } else if (input.equalsIgnoreCase("4")) {
            return new ExperiencePrompt();
        } else if (input.equalsIgnoreCase("5")) {
            return new CommandsPrompt();
        } else if (input.equalsIgnoreCase("6")) {
            return new PermissionsPrompt();
        } else if (input.equalsIgnoreCase("7")) {
            if (Quests.mcmmo != null) {
                return new mcMMOListPrompt();
            } else {
                return new RewardsPrompt(quests, factory);
            }
        } else if (input.equalsIgnoreCase("8")) {
            if (Quests.heroes != null) {
                return new HeroesListPrompt();
            } else {
                return new RewardsPrompt(quests, factory);
            }
        } else if (input.equalsIgnoreCase("9")) {
            if (Quests.phatLoots != null) {
                return new PhatLootsPrompt();
            } else {
                return new RewardsPrompt(quests, factory);
            }
        } else if (input.equalsIgnoreCase("10")) {
            return new CustomRewardsPrompt();
        } else if (input.equalsIgnoreCase("11")) {
            return factory.returnToMenu();
        }
        return null;

    }

    private class MoneyPrompt extends NumericPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW + "Enter amount of " + ColorUtil.AQUA
                    + (Quests.economy.currencyNamePlural().isEmpty() ? "Money" : Quests.economy.currencyNamePlural())
                    + ColorUtil.YELLOW + ", or 0 to clear the money reward, or -1 to cancel";

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number input) {

            if (input.intValue() < -1) {
                context.getForWhom().sendRawMessage(ColorUtil.RED + "Amount must be positive!");
                return new MoneyPrompt();
            } else if (input.intValue() == 0) {
                context.setSessionData(CK.REW_MONEY, null);
            } else if (input.intValue() != -1) {
                context.setSessionData(CK.REW_MONEY, input.intValue());
            }

            return new RewardsPrompt(quests, factory);

        }

    }

    private class ExperiencePrompt extends NumericPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW
                    + "Enter amount of experience, or 0 to clear the experience reward, or -1 to cancel";

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number input) {

            if (input.intValue() < -1) {
                context.getForWhom().sendRawMessage(ColorUtil.RED + "Amount must be positive!");
                return new ExperiencePrompt();
            } else if (input.intValue() == -1) {
                context.setSessionData(CK.REW_EXP, null);
            } else if (input.intValue() != 0) {
                context.setSessionData(CK.REW_EXP, input.intValue());
            }

            return new RewardsPrompt(quests, factory);

        }

    }

    private class QuestPointsPrompt extends NumericPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW
                    + "Enter amount of Quest Points, or 0 to clear the Quest Points reward, or -1 to cancel";

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number input) {

            if (input.intValue() < -1) {
                context.getForWhom().sendRawMessage(ColorUtil.RED + "Amount must be positive!");
                return new QuestPointsPrompt();
            } else if (input.intValue() == -1) {
                context.setSessionData(CK.REW_QUEST_POINTS, null);
            } else if (input.intValue() != 0) {
                context.setSessionData(CK.REW_QUEST_POINTS, input.intValue());
            }

            return new RewardsPrompt(quests, factory);

        }

    }

    private class ItemListPrompt extends FixedSetPrompt {

        public ItemListPrompt() {

            super("1", "2", "3");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            // Check/add newly made item
            if (context.getSessionData("newItem") != null) {
                if (context.getSessionData(CK.REW_ITEMS) != null) {
                    final List<ItemStack> itemRews = getItems(context);
                    itemRews.add((ItemStack) context.getSessionData("tempStack"));
                    context.setSessionData(CK.REW_ITEMS, itemRews);
                } else {
                    final LinkedList<ItemStack> itemRews = new LinkedList<ItemStack>();
                    itemRews.add((ItemStack) context.getSessionData("tempStack"));
                    context.setSessionData(CK.REW_ITEMS, itemRews);
                }

                context.setSessionData("newItem", null);
                context.setSessionData("tempStack", null);

            }

            String text = ColorUtil.GOLD + "- Item Rewards -\n";
            if (context.getSessionData(CK.REW_ITEMS) == null) {
                text += ColorUtil.GRAY + " (" + Lang.get("noneSet") + ")\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Add item\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - Clear\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - Done";
            } else {

                for (final ItemStack is: getItems(context)) {

                    text += ColorUtil.GRAY + "- " + ItemUtil.getDisplayString(is) + "\n";

                }
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Add item\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW + " - Clear\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - Done";

            }

            return text;

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new ItemStackPrompt(ItemListPrompt.this);
            } else if (input.equalsIgnoreCase("2")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Item rewards cleared.");
                context.setSessionData(CK.REW_ITEMS, null);
                return new ItemListPrompt();
            } else if (input.equalsIgnoreCase("3")) {
                return new RewardsPrompt(quests, factory);
            }
            return null;

        }

        private List<ItemStack> getItems(ConversationContext context) {
            return (List<ItemStack>) context.getSessionData(CK.REW_ITEMS);
        }

    }

    private class CommandsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            final String note = ColorUtil.GOLD
                    + "\nNote: You may put <player> to specify the player who completed the Quest. e.g. "
                    + ColorUtil.AQUA + ColorUtil.BOLD + ColorUtil.ITALIC + "smite <player>" + ColorUtil.RESET;
            return ColorUtil.YELLOW + "Enter command rewards separating each one by a " + ColorUtil.BOLD + "comma"
                    + ColorUtil.RESET + ColorUtil.YELLOW
                    + ", or enter \'clear\' to clear the list, or enter \'cancel\' to return." + note;
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("cancel") == false && input.equalsIgnoreCase("clear") == false) {

                final String[] args = input.split(",");
                final LinkedList<String> commands = new LinkedList<String>();
                for (String s: args) {

                    if (s.startsWith("/")) {
                        s = s.substring(1);
                    }

                    commands.add(s);

                }

                context.setSessionData(CK.REW_COMMAND, commands);

            } else if (input.equalsIgnoreCase("clear")) {
                context.setSessionData(CK.REW_COMMAND, null);
            }

            return new RewardsPrompt(quests, factory);

        }

    }

    private class PermissionsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW
                    + "Enter permission rewards separating each one by a space, or enter \'clear\' to clear the list, or enter \'cancel\' to return.";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("cancel") == false && input.equalsIgnoreCase("clear") == false) {

                final String[] args = input.split(" ");
                final LinkedList<String> permissions = new LinkedList<String>();
                permissions.addAll(Arrays.asList(args));

                context.setSessionData(CK.REW_PERMISSION, permissions);

            } else if (input.equalsIgnoreCase("clear")) {
                context.setSessionData(CK.REW_PERMISSION, null);
            }

            return new RewardsPrompt(quests, factory);

        }

    }

    // mcMMO
    private class mcMMOListPrompt extends FixedSetPrompt {

        public mcMMOListPrompt() {

            super("1", "2", "3", "4");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- mcMMO Rewards -\n";
            if (context.getSessionData(CK.REW_MCMMO_SKILLS) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Set skills (None set)\n";
                text += ColorUtil.GRAY + "2 - Set skill amounts (No skills set)\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - Clear\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - Done";
            } else {

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Set skills\n";
                for (final String s: getSkills(context)) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + s + "\n";

                }

                if (context.getSessionData(CK.REW_MCMMO_AMOUNTS) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW
                            + " - Set skill amounts (None set)\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW
                            + " - Set skill amounts\n";
                    for (final Integer i: getSkillAmounts(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + i + "\n";

                    }

                }

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - Clear\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - Done";

            }

            return text;

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new mcMMOSkillsPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                if (context.getSessionData(CK.REW_MCMMO_SKILLS) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + "You must set skills first!");
                    return new mcMMOListPrompt();
                } else {
                    return new mcMMOAmountsPrompt();
                }
            } else if (input.equalsIgnoreCase("3")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + "mcMMO rewards cleared.");
                context.setSessionData(CK.REW_MCMMO_SKILLS, null);
                context.setSessionData(CK.REW_MCMMO_AMOUNTS, null);
                return new mcMMOListPrompt();
            } else if (input.equalsIgnoreCase("4")) {

                int one;
                int two;

                if (context.getSessionData(CK.REW_MCMMO_SKILLS) != null) {
                    one = ((List<Integer>) context.getSessionData(CK.REW_MCMMO_SKILLS)).size();
                } else {
                    one = 0;
                }

                if (context.getSessionData(CK.REW_MCMMO_AMOUNTS) != null) {
                    two = ((List<Integer>) context.getSessionData(CK.REW_MCMMO_AMOUNTS)).size();
                } else {
                    two = 0;
                }

                if (one == two) {
                    return new RewardsPrompt(quests, factory);
                } else {
                    context.getForWhom()
                            .sendRawMessage(
                                    ColorUtil.RED + "The " + ColorUtil.GOLD + "skills list " + ColorUtil.RED + "and "
                                            + ColorUtil.GOLD + "skill amounts list " + ColorUtil.RED
                                            + "are not the same size!");
                    return new mcMMOListPrompt();
                }
            }
            return null;

        }

        private List<String> getSkills(ConversationContext context) {
            return (List<String>) context.getSessionData(CK.REW_MCMMO_SKILLS);
        }

        private List<Integer> getSkillAmounts(ConversationContext context) {
            return (List<Integer>) context.getSessionData(CK.REW_MCMMO_AMOUNTS);
        }

    }

    private class mcMMOSkillsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            final String skillList = ColorUtil.GOLD + "-Skill List-\n" + ColorUtil.AQUA + "Acrobatics\n"
                    + ColorUtil.GRAY + "All\n" + ColorUtil.AQUA + "Archery\n" + ColorUtil.AQUA + "Axes\n"
                    + ColorUtil.AQUA + "Excavation\n" + ColorUtil.AQUA + "Fishing\n" + ColorUtil.AQUA + "Herbalism\n"
                    + ColorUtil.AQUA + "Mining\n" + ColorUtil.AQUA + "Repair\n" + ColorUtil.AQUA + "Smelting\n"
                    + ColorUtil.AQUA + "Swords\n" + ColorUtil.AQUA + "Taming\n" + ColorUtil.AQUA + "Unarmed\n"
                    + ColorUtil.AQUA + "Woodcutting\n\n";

            return skillList + ColorUtil.YELLOW
                    + "Enter mcMMO skills, separating each one by a space, or enter \'cancel\' to return." + "\n"
                    + ColorUtil.GOLD + "Note: The \'All\' option will give levels to all skills.";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("cancel") == false) {

                final String[] args = input.split(" ");
                final LinkedList<String> skills = new LinkedList<String>();
                for (final String s: args) {

                    if (Quests.getMcMMOSkill(s) != null) {

                        if (skills.contains(s) == false) {
                            skills.add(Quester.getCapitalized(s));
                        } else {
                            context.getForWhom().sendRawMessage(ColorUtil.RED + "List contains duplicates!");
                            return new mcMMOSkillsPrompt();
                        }

                    } else {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + " is not a valid mcMMO skill!");
                        return new mcMMOSkillsPrompt();
                    }

                }

                context.setSessionData(CK.REW_MCMMO_SKILLS, skills);

            }

            return new mcMMOListPrompt();

        }

    }

    private class mcMMOAmountsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW
                    + "Enter skill amounts (numbers), separating each one by a space, or enter \'cancel\' to return.";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("cancel") == false) {

                final String[] args = input.split(" ");
                final LinkedList<Integer> amounts = new LinkedList<Integer>();
                for (final String s: args) {

                    try {

                        if (Integer.parseInt(s) > 0) {
                            amounts.add(Integer.parseInt(s));
                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.PINK + s + ColorUtil.RED + " is not greater than 0!");
                            return new mcMMOAmountsPrompt();
                        }

                    } catch (final NumberFormatException e) {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.RED + "Invalid entry " + ColorUtil.PINK + s + ColorUtil.RED
                                        + ". Input was not a list of numbers!");
                        return new mcMMOAmountsPrompt();
                    }

                }

                context.setSessionData(CK.REW_MCMMO_AMOUNTS, amounts);

            }

            return new mcMMOListPrompt();

        }

    }

    private class HeroesListPrompt extends FixedSetPrompt {

        public HeroesListPrompt() {

            super("1", "2", "3", "4");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.GOLD + "- Heroes Rewards -\n";
            if (context.getSessionData(CK.REW_HEROES_CLASSES) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Set classes (None set)\n";
                text += ColorUtil.GRAY + "2 - Set experience amounts (No classes set)\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - Clear\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - Done";
            } else {

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Set classes\n";
                for (final String s: getClasses(context)) {

                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + s + "\n";

                }

                if (context.getSessionData(CK.REW_HEROES_AMOUNTS) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW
                            + " - Set experience amounts (None set)\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW
                            + " - Set experience amounts\n";
                    for (final Double d: getClassAmounts(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + d + "\n";

                    }

                }

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - Clear\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - Done";

            }

            return text;

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new HeroesClassesPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                if (context.getSessionData(CK.REW_HEROES_CLASSES) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + "You must set classes first!");
                    return new HeroesListPrompt();
                } else {
                    return new HeroesExperiencePrompt();
                }
            } else if (input.equalsIgnoreCase("3")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Heroes rewards cleared.");
                context.setSessionData(CK.REW_HEROES_CLASSES, null);
                context.setSessionData(CK.REW_HEROES_AMOUNTS, null);
                return new HeroesListPrompt();
            } else if (input.equalsIgnoreCase("4")) {

                int one;
                int two;

                if (context.getSessionData(CK.REW_HEROES_CLASSES) != null) {
                    one = ((List<Integer>) context.getSessionData(CK.REW_HEROES_CLASSES)).size();
                } else {
                    one = 0;
                }

                if (context.getSessionData(CK.REW_HEROES_AMOUNTS) != null) {
                    two = ((List<Double>) context.getSessionData(CK.REW_HEROES_AMOUNTS)).size();
                } else {
                    two = 0;
                }

                if (one == two) {
                    return new RewardsPrompt(quests, factory);
                } else {
                    context.getForWhom().sendRawMessage(
                            ColorUtil.RED + "The " + ColorUtil.GOLD + "classes list " + ColorUtil.RED + "and "
                                    + ColorUtil.GOLD + "experience amounts list " + ColorUtil.RED
                                    + "are not the same size!");
                    return new HeroesListPrompt();
                }
            }
            return null;

        }

        private List<String> getClasses(ConversationContext context) {
            return (List<String>) context.getSessionData(CK.REW_HEROES_CLASSES);
        }

        private List<Double> getClassAmounts(ConversationContext context) {
            return (List<Double>) context.getSessionData(CK.REW_HEROES_AMOUNTS);
        }

    }

    private class HeroesClassesPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext cc) {

            String text = ColorUtil.PURPLE + "- " + ColorUtil.PINK + "Heroes Classes" + ColorUtil.PURPLE + " -\n";
            final LinkedList<String> list = new LinkedList<String>();
            for (final HeroClass hc: Quests.heroes.getClassManager().getClasses()) {
                list.add(hc.getName());
            }

            if (list.isEmpty()) {
                text += ColorUtil.GRAY + "(None)\n";
            } else {

                Collections.sort(list);

                for (final String s: list) {
                    text += ColorUtil.PINK + s + ", ";
                }

                text = text.substring(0, text.length() - 2) + "\n";

            }

            text += ColorUtil.YELLOW
                    + "Enter Heroes classes separating each one by a space, or enter \"cancel\" to return.";

            return text;
        }

        @Override
        public Prompt acceptInput(ConversationContext cc, String input) {

            if (input.equalsIgnoreCase("cancel") == false) {

                final String[] arr = input.split(" ");
                final LinkedList<String> classes = new LinkedList<String>();

                for (final String s: arr) {

                    final HeroClass hc = Quests.heroes.getClassManager().getClass(s);
                    if (hc == null) {
                        cc.getForWhom().sendRawMessage(
                                ColorUtil.RED + "Error: " + ColorUtil.PINK + s + ColorUtil.RED
                                        + " is not a valid Heroes class name!");
                        return new HeroesClassesPrompt();
                    } else {
                        classes.add(hc.getName());
                    }

                }

                cc.setSessionData(CK.REW_HEROES_CLASSES, classes);

                return new HeroesListPrompt();

            } else {
                return new HeroesListPrompt();
            }

        }
    }

    private class HeroesExperiencePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext cc) {

            String text = ColorUtil.PURPLE + "- " + ColorUtil.PINK + "Heroes Experience" + ColorUtil.PURPLE + " -\n";

            text += ColorUtil.YELLOW
                    + "Enter experience amounts (numbers, decimals are allowed) separating each one by a space, or enter \"cancel\" to return.";

            return text;
        }

        @Override
        public Prompt acceptInput(ConversationContext cc, String input) {

            if (input.equalsIgnoreCase("cancel") == false) {

                final String[] arr = input.split(" ");
                final LinkedList<Double> amounts = new LinkedList<Double>();

                for (final String s: arr) {

                    try {

                        final double d = Double.parseDouble(s);
                        if (d > 0) {
                            amounts.add(d);
                        } else {
                            cc.getForWhom().sendRawMessage(
                                    ColorUtil.RED + "Error: " + ColorUtil.PINK + s + ColorUtil.RED
                                            + " is not greater than zero!");
                            return new HeroesExperiencePrompt();
                        }

                    } catch (final NumberFormatException nfe) {
                        cc.getForWhom().sendRawMessage(
                                ColorUtil.RED + "Error: " + ColorUtil.PINK + s + ColorUtil.RED + " is not a number!");
                        return new HeroesExperiencePrompt();
                    }

                }

                cc.setSessionData(CK.REW_HEROES_AMOUNTS, amounts);
                return new HeroesListPrompt();

            } else {
                return new HeroesListPrompt();
            }

        }
    }

    private class PhatLootsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext cc) {

            String text = ColorUtil.DARKAQUA + "- " + ColorUtil.AQUA + "PhatLoots" + ColorUtil.DARKAQUA + " -\n";

            for (final PhatLoot pl: PhatLootsAPI.getAllPhatLoots()) {

                text += ColorUtil.GRAY + "- " + ColorUtil.BLUE + pl.name + "\n";

            }

            text += ColorUtil.YELLOW
                    + "Enter PhatLoots separating each one by a space, or enter \"clear\" to clear the list, or \"cancel\" to return.";

            return text;
        }

        @Override
        public Prompt acceptInput(ConversationContext cc, String input) {

            if (input.equalsIgnoreCase("cancel") == false && input.equalsIgnoreCase("clear") == false) {

                final String[] arr = input.split(" ");
                final LinkedList<String> loots = new LinkedList<String>();

                for (final String s: arr) {

                    if (PhatLootsAPI.getPhatLoot(s) == null) {
                        cc.getForWhom().sendRawMessage(
                                ColorUtil.DARKRED + s + ColorUtil.RED + " is not a valid PhatLoot name!");
                        return new PhatLootsPrompt();
                    }

                }

                loots.addAll(Arrays.asList(arr));
                cc.setSessionData(CK.REW_PHAT_LOOTS, loots);
                return new RewardsPrompt(quests, factory);

            } else if (input.equalsIgnoreCase("clear")) {

                cc.setSessionData(CK.REW_PHAT_LOOTS, null);
                cc.getForWhom().sendRawMessage(ColorUtil.YELLOW + "PhatLoots reward cleared.");
                return new RewardsPrompt(quests, factory);

            } else {
                return new RewardsPrompt(quests, factory);
            }

        }
    }

    private class CustomRewardsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            String text = ColorUtil.PINK + "- Custom Rewards -\n";
            if (quests.customRewards.isEmpty()) {
                text += ColorUtil.BOLD + "" + ColorUtil.PURPLE + "(No modules loaded)";
            } else {
                for (final CustomReward cr: quests.customRewards) {
                    text += ColorUtil.PURPLE + " - " + cr.getName() + "\n";
                }
            }

            return text
                    + ColorUtil.YELLOW
                    + "Enter the name of a custom reward to add, or enter \'clear\' to clear all custom rewards, or \'cancel\' to return.";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("cancel") == false && input.equalsIgnoreCase("clear") == false) {

                CustomReward found = null;
                for (final CustomReward cr: quests.customRewards) {
                    if (cr.getName().equalsIgnoreCase(input)) {
                        found = cr;
                        break;
                    }
                }

                if (found == null) {
                    for (final CustomReward cr: quests.customRewards) {
                        if (cr.getName().toLowerCase().contains(input.toLowerCase())) {
                            found = cr;
                            break;
                        }
                    }
                }

                if (found != null) {

                    if (context.getSessionData(CK.REW_CUSTOM) != null) {
                        final LinkedList<String> list = (LinkedList<String>) context.getSessionData(CK.REW_CUSTOM);
                        final LinkedList<Map<String, Object>> datamapList = (LinkedList<Map<String, Object>>) context
                                .getSessionData(CK.REW_CUSTOM_DATA);
                        if (list.contains(found.getName()) == false) {
                            list.add(found.getName());
                            datamapList.add(found.datamap);
                            context.setSessionData(CK.REW_CUSTOM, list);
                            context.setSessionData(CK.REW_CUSTOM_DATA, datamapList);
                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.YELLOW + "That custom reward has already been added!");
                            return new CustomRewardsPrompt();
                        }
                    } else {
                        final LinkedList<Map<String, Object>> datamapList = new LinkedList<Map<String, Object>>();
                        datamapList.add(found.datamap);
                        final LinkedList<String> list = new LinkedList<String>();
                        list.add(found.getName());
                        context.setSessionData(CK.REW_CUSTOM, list);
                        context.setSessionData(CK.REW_CUSTOM_DATA, datamapList);
                    }

                    // Send user to the custom data prompt if there is any
                    // needed
                    if (found.datamap.isEmpty() == false) {

                        context.setSessionData(CK.REW_CUSTOM_DATA_DESCRIPTIONS, found.descriptions);
                        return new RewardCustomDataListPrompt();

                    }
                    //

                } else {
                    context.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Custom reward module not found.");
                    return new CustomRewardsPrompt();
                }

            } else if (input.equalsIgnoreCase("clear")) {
                context.setSessionData(CK.REW_CUSTOM, null);
                context.setSessionData(CK.REW_CUSTOM_DATA, null);
                context.setSessionData(CK.REW_CUSTOM_DATA_TEMP, null);
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Custom rewards cleared.");
            }

            return new RewardsPrompt(quests, factory);

        }
    }

    private class RewardCustomDataListPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.BOLD + "" + ColorUtil.AQUA + "- ";

            final LinkedList<String> list = (LinkedList<String>) context.getSessionData(CK.REW_CUSTOM);
            final LinkedList<Map<String, Object>> datamapList = (LinkedList<Map<String, Object>>) context
                    .getSessionData(CK.REW_CUSTOM_DATA);

            final String rewName = list.getLast();
            final Map<String, Object> datamap = datamapList.getLast();

            text += rewName + " -\n";
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
                    .getSessionData(CK.REW_CUSTOM_DATA);
            final Map<String, Object> datamap = datamapList.getLast();

            int numInput;

            try {
                numInput = Integer.parseInt(input);
            } catch (final NumberFormatException nfe) {
                return new RewardCustomDataListPrompt();
            }

            if (numInput < 1 || numInput > datamap.size() + 1) {
                return new RewardCustomDataListPrompt();
            }

            if (numInput < datamap.size() + 1) {

                final LinkedList<String> datamapKeys = new LinkedList<String>();
                for (final String key: datamap.keySet()) {
                    datamapKeys.add(key);
                }
                Collections.sort(datamapKeys);

                final String selectedKey = datamapKeys.get(numInput - 1);
                context.setSessionData(CK.REW_CUSTOM_DATA_TEMP, selectedKey);
                return new RewardCustomDataPrompt();

            } else {

                if (datamap.containsValue(null)) {
                    return new RewardCustomDataListPrompt();
                } else {
                    context.setSessionData(CK.REW_CUSTOM_DATA_DESCRIPTIONS, null);
                    return new RewardsPrompt(quests, factory);
                }

            }

        }

    }

    private class RewardCustomDataPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            String text = "";
            final String temp = (String) context.getSessionData(CK.REW_CUSTOM_DATA_TEMP);
            final Map<String, String> descriptions = (Map<String, String>) context
                    .getSessionData(CK.REW_CUSTOM_DATA_DESCRIPTIONS);
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
                    .getSessionData(CK.REW_CUSTOM_DATA);
            final Map<String, Object> datamap = datamapList.getLast();
            datamap.put((String) context.getSessionData(CK.REW_CUSTOM_DATA_TEMP), input);
            context.setSessionData(CK.REW_CUSTOM_DATA_TEMP, null);
            return new RewardCustomDataListPrompt();
        }

    }

}
