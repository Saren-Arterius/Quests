package me.blackvein.quests.prompts;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.blackvein.quests.CustomRequirement;
import me.blackvein.quests.Quest;
import me.blackvein.quests.QuestFactory;
import me.blackvein.quests.Quests;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.ColorUtil;
import me.blackvein.quests.util.ItemUtil;
import me.blackvein.quests.util.Lang;
import me.blackvein.quests.util.MiscUtil;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.inventory.ItemStack;

import com.herocraftonline.heroes.characters.classes.HeroClass;

public class RequirementsPrompt extends FixedSetPrompt implements ColorUtil {

    Quests             quests;
    final QuestFactory factory;

    public RequirementsPrompt(Quests plugin, QuestFactory qf) {

        super("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11");
        quests = plugin;
        factory = qf;

    }

    @Override
    public String getPromptText(ConversationContext context) {

        String text;

        text = ColorUtil.DARKAQUA + "- " + ColorUtil.AQUA + context.getSessionData(CK.Q_NAME) + ColorUtil.AQUA
                + " | Requirements -\n";

        if (context.getSessionData(CK.REQ_MONEY) == null) {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set money requirement " + ColorUtil.GRAY + " (" + Lang.get("noneSet") + ")\n";
        } else {
            final int moneyReq = (Integer) context.getSessionData(CK.REQ_MONEY);
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set money requirement (" + moneyReq + " "
                    + (moneyReq > 1 ? Quests.getCurrency(true) : Quests.getCurrency(false)) + ")\n";
        }

        if (context.getSessionData(CK.REQ_QUEST_POINTS) == null) {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set Quest Points requirement " + ColorUtil.GRAY + " (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set Quest Points requirement " + ColorUtil.GRAY + "(" + ColorUtil.AQUA
                    + context.getSessionData(CK.REQ_QUEST_POINTS) + " Quest Points" + ColorUtil.GRAY + ")\n";
        }

        text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW
                + " - Set item requirements\n";

        if (context.getSessionData(CK.REQ_PERMISSION) == null) {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set permission requirements " + ColorUtil.GRAY + " (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set permission requirements\n";
            final List<String> perms = (List<String>) context.getSessionData(CK.REQ_PERMISSION);

            for (final String s: perms) {

                text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + s + "\n";

            }
        }

        if (context.getSessionData(CK.REQ_QUEST) == null) {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "5" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set Quest requirements " + ColorUtil.GRAY + " (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "5" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set Quest requirements\n";
            final List<String> qs = (List<String>) context.getSessionData(CK.REQ_QUEST);

            for (final String s: qs) {

                text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + s + "\n";

            }
        }

        if (context.getSessionData(CK.REQ_QUEST_BLOCK) == null) {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "6" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set Quest blocks " + ColorUtil.GRAY + " (" + Lang.get("noneSet") + ")\n";
        } else {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "6" + ColorUtil.RESET + ColorUtil.YELLOW
                    + " - Set Quest blocks\n";
            final List<String> qs = (List<String>) context.getSessionData(CK.REQ_QUEST_BLOCK);

            for (final String s: qs) {

                text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + s + "\n";

            }
        }

        if (Quests.mcmmo != null) {

            if (context.getSessionData(CK.REQ_MCMMO_SKILLS) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "7" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Set mcMMO requirements " + ColorUtil.GRAY + " (" + Lang.get("noneSet") + ")\n";
            } else {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "7" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Set mcMMO requirements\n";
                final List<String> skills = (List<String>) context.getSessionData(CK.REQ_MCMMO_SKILLS);
                final List<Integer> amounts = (List<Integer>) context.getSessionData(CK.REQ_MCMMO_SKILL_AMOUNTS);

                for (final String s: skills) {
                    text += ColorUtil.GRAY + "    - " + ColorUtil.DARKGREEN + s + ColorUtil.RESET + ColorUtil.YELLOW
                            + " level " + ColorUtil.GREEN + amounts.get(skills.indexOf(s)) + "\n";
                }
            }

        } else {
            text += ColorUtil.GRAY + "6 - Set mcMMO requirements (mcMMO not installed)\n";
        }

        if (Quests.heroes != null) {

            if (context.getSessionData(CK.REQ_HEROES_PRIMARY_CLASS) == null
                    && context.getSessionData(CK.REQ_HEROES_SECONDARY_CLASS) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "8" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Set Heroes requirements " + ColorUtil.GRAY + " (" + Lang.get("noneSet") + ")\n";
            } else {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "8" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Set Heroes requirements\n";

                if (context.getSessionData(CK.REQ_HEROES_PRIMARY_CLASS) != null) {
                    text += ColorUtil.AQUA + "    Primary Class: " + ColorUtil.BLUE
                            + (String) context.getSessionData(CK.REQ_HEROES_PRIMARY_CLASS) + "\n";
                }

                if (context.getSessionData(CK.REQ_HEROES_SECONDARY_CLASS) != null) {
                    text += ColorUtil.AQUA + "    Secondary Class: " + ColorUtil.BLUE
                            + (String) context.getSessionData(CK.REQ_HEROES_SECONDARY_CLASS) + "\n";
                }
            }

        } else {
            text += ColorUtil.GRAY + "8 - Set Heroes requirements (Heroes not installed)\n";
        }

        if (context.getSessionData(CK.REQ_CUSTOM) == null) {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "9 - " + ColorUtil.RESET + ColorUtil.ITALIC
                    + ColorUtil.PURPLE + "Custom Requirements (None set)\n";
        } else {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "9 - " + ColorUtil.RESET + ColorUtil.ITALIC
                    + ColorUtil.PURPLE + "Custom Requirements\n";
            final LinkedList<String> customReqs = (LinkedList<String>) context.getSessionData(CK.REQ_CUSTOM);
            for (final String s: customReqs) {

                text += ColorUtil.RESET + "" + ColorUtil.PURPLE + "  - " + ColorUtil.PINK + s + "\n";

            }
        }

        if (context.getSessionData(CK.REQ_MONEY) == null && context.getSessionData(CK.REQ_QUEST_POINTS) == null
                && context.getSessionData(CK.REQ_QUEST_BLOCK) == null && context.getSessionData(CK.REQ_ITEMS) == null
                && context.getSessionData(CK.REQ_PERMISSION) == null && context.getSessionData(CK.REQ_QUEST) == null
                && context.getSessionData(CK.REQ_QUEST_BLOCK) == null
                && context.getSessionData(CK.REQ_MCMMO_SKILLS) == null
                && context.getSessionData(CK.REQ_HEROES_PRIMARY_CLASS) == null
                && context.getSessionData(CK.REQ_HEROES_SECONDARY_CLASS) == null
                && context.getSessionData(CK.REQ_CUSTOM) == null) {
            text += ColorUtil.GRAY + "" + ColorUtil.BOLD + "10 - " + ColorUtil.RESET + ColorUtil.GRAY
                    + "Set fail requirements message (No requirements set)\n";
        } else if (context.getSessionData(CK.Q_FAIL_MESSAGE) == null) {
            text += ColorUtil.RED + "" + ColorUtil.BOLD + "10 - " + ColorUtil.RESET + ColorUtil.RED
                    + "Set fail requirements message (Required)\n";
        } else {
            text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "10 - " + ColorUtil.RESET + ColorUtil.YELLOW
                    + "Set fail requirements message" + ColorUtil.GRAY + "(" + ColorUtil.AQUA + "\""
                    + context.getSessionData(CK.Q_FAIL_MESSAGE) + "\"" + ColorUtil.GRAY + ")\n";
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
            return new PermissionsPrompt();
        } else if (input.equalsIgnoreCase("5")) {
            return new QuestListPrompt(true);
        } else if (input.equalsIgnoreCase("6")) {
            return new QuestListPrompt(false);
        } else if (input.equalsIgnoreCase("7")) {
            if (Quests.mcmmo != null) {
                return new mcMMOPrompt();
            } else {
                return new RequirementsPrompt(quests, factory);
            }
        } else if (input.equalsIgnoreCase("8")) {
            if (Quests.heroes != null) {
                return new HeroesPrompt();
            } else {
                return new RequirementsPrompt(quests, factory);
            }
        } else if (input.equalsIgnoreCase("9")) {
            return new CustomRequirementsPrompt();
        } else if (input.equalsIgnoreCase("10")) {
            return new FailMessagePrompt();
        } else if (input.equalsIgnoreCase("11")) {
            if (context.getSessionData(CK.REQ_MONEY) != null || context.getSessionData(CK.REQ_QUEST_POINTS) != null
                    || context.getSessionData(CK.REQ_ITEMS) != null
                    || context.getSessionData(CK.REQ_PERMISSION) != null
                    || context.getSessionData(CK.REQ_QUEST) != null
                    || context.getSessionData(CK.REQ_QUEST_BLOCK) != null
                    || context.getSessionData(CK.REQ_MCMMO_SKILLS) != null
                    || context.getSessionData(CK.REQ_HEROES_PRIMARY_CLASS) != null
                    || context.getSessionData(CK.REQ_HEROES_SECONDARY_CLASS) != null
                    || context.getSessionData(CK.REQ_CUSTOM) != null) {

                if (context.getSessionData(CK.Q_FAIL_MESSAGE) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + "You must set a fail requirements message!");
                    return new RequirementsPrompt(quests, factory);
                }

            }

            return factory.returnToMenu();
        }
        return null;

    }

    private class MoneyPrompt extends NumericPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW + "Enter amount of " + ColorUtil.PURPLE
                    + ((Quests.economy.currencyNamePlural().isEmpty() ? "Money" : Quests.economy.currencyNamePlural()))
                    + ColorUtil.YELLOW + ", or 0 to clear the money requirement, or -1 to cancel";

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number input) {

            if (input.intValue() < -1) {
                context.getForWhom().sendRawMessage(ColorUtil.RED + "Amount must be greater than 0!");
                return new MoneyPrompt();
            } else if (input.intValue() == -1) {
                return new RequirementsPrompt(quests, factory);
            } else if (input.intValue() == 0) {
                context.setSessionData(CK.REQ_MONEY, null);
                return new RequirementsPrompt(quests, factory);
            }

            context.setSessionData(CK.REQ_MONEY, input.intValue());
            return new RequirementsPrompt(quests, factory);

        }
    }

    private class QuestPointsPrompt extends NumericPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW
                    + "Enter amount of Quest Points, or 0 to clear the Quest Point requirement,\nor -1 to cancel";

        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number input) {

            if (input.intValue() < -1) {
                context.getForWhom().sendRawMessage(ColorUtil.RED + "Amount must be greater than 0!");
                return new QuestPointsPrompt();
            } else if (input.intValue() == -1) {
                return new RequirementsPrompt(quests, factory);
            } else if (input.intValue() == 0) {
                context.setSessionData(CK.REQ_QUEST_POINTS, null);
                return new RequirementsPrompt(quests, factory);
            }

            context.setSessionData(CK.REQ_QUEST_POINTS, input.intValue());
            return new RequirementsPrompt(quests, factory);

        }
    }

    private class QuestListPrompt extends StringPrompt {

        private final boolean isRequiredQuest;

        /*
         * public QuestListPrompt() {
         * this.isRequiredQuest = true;
         * }
         */
        public QuestListPrompt(boolean isRequired) {
            isRequiredQuest = isRequired;
        }

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.PINK + "- Quests -\n" + ColorUtil.PURPLE;

            boolean none = true;
            for (final Quest q: quests.getQuests()) {

                text += q.getName() + ", ";
                none = false;

            }

            if (none) {
                text += "(None)\n";
            } else {
                text = text.substring(0, (text.length() - 2));
                text += "\n";
            }

            text += ColorUtil.YELLOW + "Enter a list of Quest names separating each one by a " + ColorUtil.RED
                    + ColorUtil.BOLD + "comma" + ColorUtil.RESET + ColorUtil.YELLOW
                    + ", or enter \'clear\' to clear the list, or \'cancel\' to return.";

            return text;

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("cancel") == false && input.equalsIgnoreCase("clear") == false) {

                final String[] args = input.split(",");
                final LinkedList<String> questNames = new LinkedList<String>();

                for (final String s: args) {

                    if (quests.getQuest(s) == null) {

                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + " " + ColorUtil.RED + "is not a Quest name!");
                        return new QuestListPrompt(isRequiredQuest);

                    }

                    if (questNames.contains(s)) {

                        context.getForWhom().sendRawMessage(ColorUtil.RED + "List contains duplicates!");
                        return new QuestListPrompt(isRequiredQuest);

                    }

                    questNames.add(s);

                }

                Collections.sort(questNames, new Comparator<String>() {
                    @Override
                    public int compare(String one, String two) {

                        return one.compareTo(two);

                    }
                });

                if (isRequiredQuest) {
                    context.setSessionData(CK.REQ_QUEST, questNames);
                } else {
                    context.setSessionData(CK.REQ_QUEST_BLOCK, questNames);
                }

            } else if (input.equalsIgnoreCase("clear")) {

                if (isRequiredQuest) {
                    context.setSessionData(CK.REQ_QUEST, null);
                } else {
                    context.setSessionData(CK.REQ_QUEST_BLOCK, null);
                }

            }

            return new RequirementsPrompt(quests, factory);

        }
    }

    private class ItemListPrompt extends FixedSetPrompt {

        public ItemListPrompt() {

            super("1", "2", "3", "4");

        }

        @Override
        public String getPromptText(ConversationContext context) {

            // Check/add newly made item
            if (context.getSessionData("newItem") != null) {
                if (context.getSessionData(CK.REQ_ITEMS) != null) {
                    final List<ItemStack> itemRews = getItems(context);
                    itemRews.add((ItemStack) context.getSessionData("tempStack"));
                    context.setSessionData(CK.REQ_ITEMS, itemRews);
                } else {
                    final LinkedList<ItemStack> itemRews = new LinkedList<ItemStack>();
                    itemRews.add((ItemStack) context.getSessionData("tempStack"));
                    context.setSessionData(CK.REQ_ITEMS, itemRews);
                }

                context.setSessionData("newItem", null);
                context.setSessionData("tempStack", null);

            }

            String text = ColorUtil.GOLD + "- Item Requirements -\n";
            if (context.getSessionData(CK.REQ_ITEMS) == null) {
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Add item\n";
                text += ColorUtil.GRAY + "2 - Set remove items (No items set)\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "3" + ColorUtil.RESET + ColorUtil.YELLOW + " - Clear\n";
                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "4" + ColorUtil.RESET + ColorUtil.YELLOW + " - Done";
            } else {

                for (final ItemStack is: getItems(context)) {

                    text += ColorUtil.GRAY + "    - " + ItemUtil.getDisplayString(is) + "\n";

                }

                text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "1" + ColorUtil.RESET + ColorUtil.YELLOW
                        + " - Add item\n";

                if (context.getSessionData(CK.REQ_ITEMS_REMOVE) == null) {
                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW
                            + " - Set remove items (No values set)\n";
                } else {

                    text += ColorUtil.BLUE + "" + ColorUtil.BOLD + "2" + ColorUtil.RESET + ColorUtil.YELLOW
                            + " - Set remove items\n";
                    for (final Boolean b: getRemoveItems(context)) {

                        text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + b.toString().toLowerCase() + "\n";

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
                return new ItemStackPrompt(ItemListPrompt.this);
            } else if (input.equalsIgnoreCase("2")) {
                if (context.getSessionData(CK.REQ_ITEMS) == null) {
                    context.getForWhom().sendRawMessage(ColorUtil.RED + "You must add at least one item first!");
                    return new ItemListPrompt();
                } else {
                    return new RemoveItemsPrompt();
                }
            } else if (input.equalsIgnoreCase("3")) {
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Item requirements cleared.");
                context.setSessionData(CK.REQ_ITEMS, null);
                context.setSessionData(CK.REQ_ITEMS_REMOVE, null);
                return new ItemListPrompt();
            } else if (input.equalsIgnoreCase("4")) {

                int one;
                int two;

                if (context.getSessionData(CK.REQ_ITEMS) != null) {
                    one = ((List<ItemStack>) context.getSessionData(CK.REQ_ITEMS)).size();
                } else {
                    one = 0;
                }

                if (context.getSessionData(CK.REQ_ITEMS_REMOVE) != null) {
                    two = ((List<Boolean>) context.getSessionData(CK.REQ_ITEMS_REMOVE)).size();
                } else {
                    two = 0;
                }

                if (one == two) {
                    return new RequirementsPrompt(quests, factory);
                } else {
                    context.getForWhom().sendRawMessage(
                            ColorUtil.RED + "The " + ColorUtil.GOLD + "items list " + ColorUtil.RED + "and "
                                    + ColorUtil.GOLD + "remove items list " + ColorUtil.RED + "are not the same size!");
                    return new ItemListPrompt();
                }
            }
            return null;

        }

        private List<ItemStack> getItems(ConversationContext context) {
            return (List<ItemStack>) context.getSessionData(CK.REQ_ITEMS);
        }

        private List<Boolean> getRemoveItems(ConversationContext context) {
            return (List<Boolean>) context.getSessionData(CK.REQ_ITEMS_REMOVE);
        }
    }

    private class RemoveItemsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW
                    + "Enter a list of true/false values, separating each one by a space, or enter \'cancel\' to return.";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("cancel") == false) {

                final String[] args = input.split(" ");
                final LinkedList<Boolean> booleans = new LinkedList<Boolean>();

                for (final String s: args) {

                    if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes")) {
                        booleans.add(true);
                    } else if (s.equalsIgnoreCase("false") || s.equalsIgnoreCase("no")) {
                        booleans.add(false);
                    } else {
                        context.getForWhom().sendRawMessage(
                                ColorUtil.PINK + s + ColorUtil.RED + " is not a true or false value!\n "
                                        + ColorUtil.GOLD + "Example: true false true true");
                        return new RemoveItemsPrompt();
                    }

                }

                context.setSessionData(CK.REQ_ITEMS_REMOVE, booleans);

            }

            return new ItemListPrompt();

        }
    }

    private class PermissionsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW
                    + "Enter permission requirements separating each one by a space, or enter \'clear\' to clear the list, or enter \'cancel\' to return.";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("cancel") == false && input.equalsIgnoreCase("clear") == false) {

                final String[] args = input.split(" ");
                final LinkedList<String> permissions = new LinkedList<String>();
                permissions.addAll(Arrays.asList(args));

                context.setSessionData(CK.REQ_PERMISSION, permissions);

            } else if (input.equalsIgnoreCase("clear")) {
                context.setSessionData(CK.REQ_PERMISSION, null);
            }

            return new RequirementsPrompt(quests, factory);

        }
    }

    private class CustomRequirementsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            String text = ColorUtil.PINK + "- Custom Requirements -\n";
            if (quests.customRequirements.isEmpty()) {
                text += ColorUtil.BOLD + "" + ColorUtil.PURPLE + "(No modules loaded)";
            } else {
                for (final CustomRequirement cr: quests.customRequirements) {
                    text += ColorUtil.PURPLE + " - " + cr.getName() + "\n";
                }
            }

            return text
                    + ColorUtil.YELLOW
                    + "Enter the name of a custom requirement to add, or enter \'clear\' to clear all custom requirements, or \'cancel\' to return.";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("cancel") == false && input.equalsIgnoreCase("clear") == false) {

                CustomRequirement found = null;
                for (final CustomRequirement cr: quests.customRequirements) {
                    if (cr.getName().equalsIgnoreCase(input)) {
                        found = cr;
                        break;
                    }
                }

                if (found == null) {
                    for (final CustomRequirement cr: quests.customRequirements) {
                        if (cr.getName().toLowerCase().contains(input.toLowerCase())) {
                            found = cr;
                            break;
                        }
                    }
                }

                if (found != null) {

                    if (context.getSessionData(CK.REQ_CUSTOM) != null) {
                        final LinkedList<String> list = (LinkedList<String>) context.getSessionData(CK.REQ_CUSTOM);
                        final LinkedList<Map<String, Object>> datamapList = (LinkedList<Map<String, Object>>) context
                                .getSessionData(CK.REQ_CUSTOM_DATA);
                        if (list.contains(found.getName()) == false) {
                            list.add(found.getName());
                            datamapList.add(found.datamap);
                            context.setSessionData(CK.REQ_CUSTOM, list);
                            context.setSessionData(CK.REQ_CUSTOM_DATA, datamapList);
                        } else {
                            context.getForWhom().sendRawMessage(
                                    ColorUtil.YELLOW + "That custom requirement has already been added!");
                            return new CustomRequirementsPrompt();
                        }
                    } else {
                        final LinkedList<Map<String, Object>> datamapList = new LinkedList<Map<String, Object>>();
                        datamapList.add(found.datamap);
                        final LinkedList<String> list = new LinkedList<String>();
                        list.add(found.getName());
                        context.setSessionData(CK.REQ_CUSTOM, list);
                        context.setSessionData(CK.REQ_CUSTOM_DATA, datamapList);
                    }

                    // Send user to the custom data prompt if there is any
                    // needed
                    if (found.datamap.isEmpty() == false) {

                        context.setSessionData(CK.REQ_CUSTOM_DATA_DESCRIPTIONS, found.descriptions);
                        return new RequirementCustomDataListPrompt();

                    }
                    //

                } else {
                    context.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Custom requirement module not found.");
                    return new CustomRequirementsPrompt();
                }

            } else if (input.equalsIgnoreCase("clear")) {
                context.setSessionData(CK.REQ_CUSTOM, null);
                context.setSessionData(CK.REQ_CUSTOM_DATA, null);
                context.setSessionData(CK.REQ_CUSTOM_DATA_TEMP, null);
                context.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Custom requirements cleared.");
            }

            return new RequirementsPrompt(quests, factory);

        }
    }

    private class RequirementCustomDataListPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            String text = ColorUtil.BOLD + "" + ColorUtil.AQUA + "- ";

            final LinkedList<String> list = (LinkedList<String>) context.getSessionData(CK.REQ_CUSTOM);
            final LinkedList<Map<String, Object>> datamapList = (LinkedList<Map<String, Object>>) context
                    .getSessionData(CK.REQ_CUSTOM_DATA);

            final String reqName = list.getLast();
            final Map<String, Object> datamap = datamapList.getLast();

            text += reqName + " -\n";
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
                    .getSessionData(CK.REQ_CUSTOM_DATA);
            final Map<String, Object> datamap = datamapList.getLast();

            int numInput;

            try {
                numInput = Integer.parseInt(input);
            } catch (final NumberFormatException nfe) {
                return new RequirementCustomDataListPrompt();
            }

            if (numInput < 1 || numInput > datamap.size() + 1) {
                return new RequirementCustomDataListPrompt();
            }

            if (numInput < datamap.size() + 1) {

                final LinkedList<String> datamapKeys = new LinkedList<String>();
                for (final String key: datamap.keySet()) {
                    datamapKeys.add(key);
                }
                Collections.sort(datamapKeys);

                final String selectedKey = datamapKeys.get(numInput - 1);
                context.setSessionData(CK.REQ_CUSTOM_DATA_TEMP, selectedKey);
                return new RequirementCustomDataPrompt();

            } else {

                if (datamap.containsValue(null)) {
                    return new RequirementCustomDataListPrompt();
                } else {
                    context.setSessionData(CK.REQ_CUSTOM_DATA_DESCRIPTIONS, null);
                    return new RequirementsPrompt(quests, factory);
                }

            }

        }

    }

    private class RequirementCustomDataPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            String text = "";
            final String temp = (String) context.getSessionData(CK.REQ_CUSTOM_DATA_TEMP);
            final Map<String, String> descriptions = (Map<String, String>) context
                    .getSessionData(CK.REQ_CUSTOM_DATA_DESCRIPTIONS);
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
                    .getSessionData(CK.REQ_CUSTOM_DATA);
            final Map<String, Object> datamap = datamapList.getLast();
            datamap.put((String) context.getSessionData(CK.REQ_CUSTOM_DATA_TEMP), input);
            context.setSessionData(CK.REQ_CUSTOM_DATA_TEMP, null);
            return new RequirementCustomDataListPrompt();
        }

    }

    private class mcMMOPrompt extends FixedSetPrompt {

        public mcMMOPrompt() {
            super("1", "2", "3");
        }

        @Override
        public String getPromptText(ConversationContext cc) {

            String text = ColorUtil.DARKGREEN + "- mcMMO Requirements -\n";
            if (cc.getSessionData(CK.REQ_MCMMO_SKILLS) == null) {
                text += ColorUtil.BOLD + "" + ColorUtil.GREEN + "1" + ColorUtil.RESET + ColorUtil.GREEN
                        + " - Set skills (None set)\n";
            } else {
                text += ColorUtil.BOLD + "" + ColorUtil.GREEN + "1" + ColorUtil.RESET + ColorUtil.GREEN
                        + " - Set skills\n";
                final LinkedList<String> skills = (LinkedList<String>) cc.getSessionData(CK.REQ_MCMMO_SKILLS);
                for (final String skill: skills) {
                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + skill + "\n";
                }
            }

            if (cc.getSessionData(CK.REQ_MCMMO_SKILL_AMOUNTS) == null) {
                text += ColorUtil.BOLD + "" + ColorUtil.GREEN + "2" + ColorUtil.RESET + ColorUtil.GREEN
                        + " - Set skill amounts (None set)\n";
            } else {
                text += ColorUtil.BOLD + "" + ColorUtil.GREEN + "2" + ColorUtil.RESET + ColorUtil.GREEN
                        + " - Set skill amounts\n";
                final LinkedList<Integer> amounts = (LinkedList<Integer>) cc.getSessionData(CK.REQ_MCMMO_SKILL_AMOUNTS);
                for (final int i: amounts) {
                    text += ColorUtil.GRAY + "    - " + ColorUtil.AQUA + i + "\n";
                }
            }

            text += ColorUtil.BOLD + "" + ColorUtil.GREEN + "3" + ColorUtil.RESET + ColorUtil.GREEN + " - Done";

            return text;
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext cc, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new mcMMOSkillsPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                return new mcMMOAmountsPrompt();
            } else if (input.equalsIgnoreCase("3")) {
                return new RequirementsPrompt(quests, factory);
            }

            return null;

        }
    }

    private class mcMMOSkillsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            final String skillList = ColorUtil.DARKGREEN + "-Skill List-\n" + ColorUtil.GREEN + "Acrobatics\n"
                    + ColorUtil.GREEN + "All\n" + ColorUtil.GREEN + "Archery\n" + ColorUtil.GREEN + "Axes\n"
                    + ColorUtil.GREEN + "Excavation\n" + ColorUtil.GREEN + "Fishing\n" + ColorUtil.GREEN
                    + "Herbalism\n" + ColorUtil.GREEN + "Mining\n" + ColorUtil.GREEN + "Repair\n" + ColorUtil.GREEN
                    + "Smelting\n" + ColorUtil.GREEN + "Swords\n" + ColorUtil.GREEN + "Taming\n" + ColorUtil.GREEN
                    + "Unarmed\n" + ColorUtil.GREEN + "Woodcutting\n\n";

            return skillList + ColorUtil.YELLOW
                    + "Enter mcMMO skills, separating each one by a space, or enter \'clear\' to clear the list, "
                    + "or \'cancel\' to return.\n";
        }

        @Override
        public Prompt acceptInput(ConversationContext cc, String input) {

            if (input.equalsIgnoreCase("cancel") == false && input.equalsIgnoreCase("clear") == false) {

                final LinkedList<String> skills = new LinkedList<String>();

                for (final String s: input.split(" ")) {

                    final String formatted = MiscUtil.getCapitalized(s);

                    if (Quests.getMcMMOSkill(formatted) != null) {
                        skills.add(formatted);
                    } else if (skills.contains(formatted)) {
                        cc.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Error: List contains duplicates!");
                        return new mcMMOSkillsPrompt();
                    } else {
                        cc.getForWhom().sendRawMessage(
                                ColorUtil.YELLOW + "Error: " + ColorUtil.RED + s + ColorUtil.YELLOW
                                        + " is not an mcMMO skill name!");
                        return new mcMMOSkillsPrompt();
                    }

                }

                cc.setSessionData(CK.REQ_MCMMO_SKILLS, skills);
                return new mcMMOPrompt();

            } else if (input.equalsIgnoreCase("clear")) {
                cc.getForWhom().sendRawMessage(ColorUtil.YELLOW + "mcMMO skill requirements cleared.");
                cc.setSessionData(CK.REQ_MCMMO_SKILLS, null);
                return new mcMMOPrompt();
            } else if (input.equalsIgnoreCase("cancel")) {
                return new mcMMOPrompt();
            }

            return new mcMMOSkillsPrompt();

        }

    }

    private class mcMMOAmountsPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW
                    + "Enter mcMMO skill amounts, separating each one by a space, or enter \'clear\' to clear the list, "
                    + "or \'cancel\' to return.\n";
        }

        @Override
        public Prompt acceptInput(ConversationContext cc, String input) {

            if (input.equalsIgnoreCase("cancel") == false && input.equalsIgnoreCase("clear") == false) {

                final LinkedList<Integer> amounts = new LinkedList<Integer>();

                for (final String s: input.split(" ")) {

                    try {

                        final int i = Integer.parseInt(s);
                        amounts.add(i);

                    } catch (final NumberFormatException nfe) {
                        cc.getForWhom().sendRawMessage(
                                ColorUtil.YELLOW + "Error: " + ColorUtil.RED + s + ColorUtil.YELLOW
                                        + " is not a number!");
                        return new mcMMOAmountsPrompt();
                    }

                }

                cc.setSessionData(CK.REQ_MCMMO_SKILL_AMOUNTS, amounts);
                return new mcMMOPrompt();

            } else if (input.equalsIgnoreCase("clear")) {
                cc.getForWhom().sendRawMessage(ColorUtil.YELLOW + "mcMMO skill amount requirements cleared.");
                cc.setSessionData(CK.REQ_MCMMO_SKILL_AMOUNTS, null);
                return new mcMMOPrompt();
            } else if (input.equalsIgnoreCase("cancel")) {
                return new mcMMOPrompt();
            }

            return new mcMMOAmountsPrompt();

        }

    }

    private class HeroesPrompt extends FixedSetPrompt {

        public HeroesPrompt() {
            super("1", "2", "3");
        }

        @Override
        public String getPromptText(ConversationContext cc) {

            String text = ColorUtil.DARKGREEN + "- Heroes Requirements -\n";
            if (cc.getSessionData(CK.REQ_HEROES_PRIMARY_CLASS) == null) {
                text += ColorUtil.BOLD + "" + ColorUtil.GREEN + "1" + ColorUtil.RESET + ColorUtil.GREEN
                        + " - Set Primary Class (None set)\n";
            } else {
                text += ColorUtil.BOLD + "" + ColorUtil.GREEN + "1" + ColorUtil.RESET + ColorUtil.GREEN
                        + " - Set Primary Class (" + ColorUtil.AQUA
                        + (String) cc.getSessionData(CK.REQ_HEROES_PRIMARY_CLASS) + ColorUtil.GREEN + ")\n";
            }

            if (cc.getSessionData(CK.REQ_HEROES_SECONDARY_CLASS) == null) {
                text += ColorUtil.BOLD + "" + ColorUtil.GREEN + "2" + ColorUtil.RESET + ColorUtil.GREEN
                        + " - Set Secondary Class (None set)\n";
            } else {
                text += ColorUtil.BOLD + "" + ColorUtil.GREEN + "2" + ColorUtil.RESET + ColorUtil.GREEN
                        + " - Set Secondary Class (" + ColorUtil.AQUA
                        + (String) cc.getSessionData(CK.REQ_HEROES_SECONDARY_CLASS) + ColorUtil.GREEN + ")\n";
            }

            text += ColorUtil.BOLD + "" + ColorUtil.GREEN + "3" + ColorUtil.RESET + ColorUtil.GREEN + " - Done";

            return text;
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext cc, String input) {

            if (input.equalsIgnoreCase("1")) {
                return new HeroesPrimaryPrompt();
            } else if (input.equalsIgnoreCase("2")) {
                return new HeroesSecondaryPrompt();
            } else if (input.equalsIgnoreCase("3")) {
                return new RequirementsPrompt(quests, factory);
            }

            return null;

        }
    }

    private class HeroesPrimaryPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext cc) {

            String text = ColorUtil.PURPLE + "- " + ColorUtil.PINK + "Primary Classes" + ColorUtil.PURPLE + " -\n";
            final LinkedList<String> list = new LinkedList<String>();
            for (final HeroClass hc: Quests.heroes.getClassManager().getClasses()) {
                if (hc.isPrimary()) {
                    list.add(hc.getName());
                }
            }

            if (list.isEmpty()) {
                text += ColorUtil.GRAY + "(None)\n";
            } else {

                Collections.sort(list);

                for (final String s: list) {
                    text += ColorUtil.PURPLE + "- " + ColorUtil.PINK + s + "\n";
                }

            }

            text += ColorUtil.YELLOW
                    + "Enter a Heroes Primary Class name, or enter \"clear\" to clear the requirement, or \"cancel\" to return.";

            return text;
        }

        @Override
        public Prompt acceptInput(ConversationContext cc, String input) {

            if (input.equalsIgnoreCase("clear") == false && input.equalsIgnoreCase("cancel") == false) {

                final HeroClass hc = Quests.heroes.getClassManager().getClass(input);
                if (hc != null) {

                    if (hc.isPrimary()) {

                        cc.setSessionData(CK.REQ_HEROES_PRIMARY_CLASS, hc.getName());
                        return new HeroesPrompt();

                    } else {
                        cc.getForWhom().sendRawMessage(
                                ColorUtil.RED + "The " + ColorUtil.PINK + hc.getName() + ColorUtil.RED
                                        + " class is not primary!");
                        return new HeroesPrimaryPrompt();
                    }

                } else {
                    cc.getForWhom().sendRawMessage(ColorUtil.RED + "Class not found!");
                    return new HeroesPrimaryPrompt();
                }

            } else if (input.equalsIgnoreCase("clear")) {

                cc.setSessionData(CK.REQ_HEROES_PRIMARY_CLASS, null);
                cc.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Heroes Primary Class requirement cleared.");
                return new HeroesPrompt();

            } else {

                return new HeroesPrompt();

            }

        }
    }

    private class HeroesSecondaryPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext cc) {

            String text = ColorUtil.PURPLE + "- " + ColorUtil.PINK + "Secondary Classes" + ColorUtil.PURPLE + " -\n";
            final LinkedList<String> list = new LinkedList<String>();
            for (final HeroClass hc: Quests.heroes.getClassManager().getClasses()) {
                if (hc.isSecondary()) {
                    list.add(hc.getName());
                }
            }

            if (list.isEmpty()) {
                text += ColorUtil.GRAY + "(None)\n";
            } else {

                Collections.sort(list);

                for (final String s: list) {
                    text += ColorUtil.PURPLE + "- " + ColorUtil.PINK + s + "\n";
                }

            }

            text += ColorUtil.YELLOW
                    + "Enter a Heroes Secondary Class name, or enter \"clear\" to clear the requirement, or \"cancel\" to return.";

            return text;
        }

        @Override
        public Prompt acceptInput(ConversationContext cc, String input) {

            if (input.equalsIgnoreCase("clear") == false && input.equalsIgnoreCase("cancel") == false) {

                final HeroClass hc = Quests.heroes.getClassManager().getClass(input);
                if (hc != null) {

                    if (hc.isSecondary()) {

                        cc.setSessionData(CK.REQ_HEROES_SECONDARY_CLASS, hc.getName());
                        return new HeroesPrompt();

                    } else {
                        cc.getForWhom().sendRawMessage(
                                ColorUtil.RED + "The " + ColorUtil.PINK + hc.getName() + ColorUtil.RED
                                        + " class is not secondary!");
                        return new HeroesSecondaryPrompt();
                    }

                } else {
                    cc.getForWhom().sendRawMessage(ColorUtil.RED + "Class not found!");
                    return new HeroesSecondaryPrompt();
                }

            } else if (input.equalsIgnoreCase("clear")) {

                cc.setSessionData(CK.REQ_HEROES_SECONDARY_CLASS, null);
                cc.getForWhom().sendRawMessage(ColorUtil.YELLOW + "Heroes Secondary Class requirement cleared.");
                return new HeroesPrompt();

            } else {

                return new HeroesPrompt();

            }

        }
    }

    private class FailMessagePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ColorUtil.YELLOW + "Enter fail requirements message, or enter \'cancel\' to return.";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (input.equalsIgnoreCase("cancel") == false) {
                context.setSessionData(CK.Q_FAIL_MESSAGE, input);
            }

            return new RequirementsPrompt(quests, factory);

        }
    }
}
