package me.blackvein.quests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.blackvein.quests.exceptions.InvalidStageException;
import me.blackvein.quests.prompts.QuestAcceptPrompt;
import me.blackvein.quests.util.ColorUtil;
import me.blackvein.quests.util.ItemUtil;
import me.blackvein.quests.util.Lang;
import me.blackvein.quests.util.MiscUtil;
import net.aufdemrand.denizen.Denizen;
import net.aufdemrand.denizen.scripts.ScriptRegistry;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.CitizensPlugin;
import net.citizensnpcs.api.npc.NPC;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.codisimus.plugins.phatloots.PhatLoots;
import com.codisimus.plugins.phatloots.PhatLootsAPI;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.player.UserManager;
import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Quests extends JavaPlugin implements ConversationAbandonedListener, ColorUtil {

    public final static Logger           log                       = Logger.getLogger("Minecraft");
    public static Economy                economy                   = null;
    public static Permission             permission                = null;
    public static WorldGuardPlugin       worldGuard                = null;
    public static mcMMO                  mcmmo                     = null;
    public static Heroes                 heroes                    = null;
    public static PhatLoots              phatLoots                 = null;
    public static boolean                snoop                     = true;
    public static boolean                npcEffects                = true;
    public static boolean                broadcastPartyCreation    = true;
    public static boolean                ignoreLockedQuests        = false;
    public static boolean                genFilesOnJoin            = true;
    public static int                    maxPartySize              = 0;
    public static int                    acceptTimeout             = 20;
    public static int                    inviteTimeout             = 20;
    public static String                 effect                    = "note";
    public final Map<String, Quester>    questers                  = new HashMap<String, Quester>();
    public final List<String>            questerBlacklist          = new LinkedList<String>();
    public final List<CustomRequirement> customRequirements        = new LinkedList<CustomRequirement>();
    public final List<CustomReward>      customRewards             = new LinkedList<CustomReward>();
    public final List<CustomObjective>   customObjectives          = new LinkedList<CustomObjective>();
    public final LinkedList<Quest>       quests                    = new LinkedList<Quest>();
    public final LinkedList<Event>       events                    = new LinkedList<Event>();
    public final LinkedList<NPC>         questNPCs                 = new LinkedList<NPC>();
    public final LinkedList<Integer>     questNPCGUIs              = new LinkedList<Integer>();
    public ConversationFactory           conversationFactory;
    public ConversationFactory           NPCConversationFactory;
    public QuestFactory                  questFactory;
    public EventFactory                  eventFactory;
    public Vault                         vault                     = null;
    public CitizensPlugin                citizens;
    public PlayerListener                pListener;
    public NpcListener                   npcListener;
    public NpcEffectThread               effListener;
    public Denizen                       denizen                   = null;
    public QuestTaskTrigger              trigger;
    public boolean                       allowCommands             = true;
    public boolean                       allowCommandsForNpcQuests = false;
    public boolean                       showQuestReqs             = true;
    public boolean                       allowQuitting             = true;
    public boolean                       debug                     = false;
    public boolean                       load                      = false;
    public int                           killDelay                 = 0;
    public int                           totalQuestPoints          = 0;
    public Lang                          lang;
    private static Quests                instance                  = null;
    public static final String           validVersion              = "1.7.9-R0.2";

    @Override
    public void onEnable() {

        if (getServer().getBukkitVersion().equalsIgnoreCase(Quests.validVersion) == false) {

            Quests.log.severe("[Quests] Your current version of CraftBukkit is " + getServer().getBukkitVersion()
                    + ", this version of Quests is built for version " + Quests.validVersion);
            Quests.log.severe("[Quests] Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;

        }

        pListener = new PlayerListener(this);
        effListener = new NpcEffectThread(this);
        npcListener = new NpcListener(this);
        Quests.instance = this;

        conversationFactory = new ConversationFactory(this).withModality(false).withPrefix(new QuestsPrefix())
                .withFirstPrompt(new QuestPrompt()).withTimeout(Quests.acceptTimeout)
                .thatExcludesNonPlayersWithMessage("Console may not perform this conversation!")
                .addConversationAbandonedListener(this);

        NPCConversationFactory = new ConversationFactory(this).withModality(false)
                .withFirstPrompt(new QuestAcceptPrompt(this)).withTimeout(Quests.acceptTimeout).withLocalEcho(false)
                .addConversationAbandonedListener(this);

        questFactory = new QuestFactory(this);
        eventFactory = new EventFactory(this);

        try {
            if (getServer().getPluginManager().getPlugin("Citizens") != null) {
                citizens = (CitizensPlugin) getServer().getPluginManager().getPlugin("Citizens");
            }
            if (citizens != null) {
                getServer().getPluginManager().registerEvents(npcListener, this);
            }
        } catch (final Exception e) {
            Quests.printWarning("[Quests] Legacy version of Citizens found. Citizens in Quests not enabled.");
        }

        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            Quests.worldGuard = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
        }

        if (getServer().getPluginManager().getPlugin("Denizen") != null) {
            denizen = (Denizen) getServer().getPluginManager().getPlugin("Denizen");
        }

        if (getServer().getPluginManager().getPlugin("mcMMO") != null) {
            Quests.mcmmo = (mcMMO) getServer().getPluginManager().getPlugin("mcMMO");
        }

        if (getServer().getPluginManager().getPlugin("Heroes") != null) {
            Quests.heroes = (Heroes) getServer().getPluginManager().getPlugin("Heroes");
        }

        if (getServer().getPluginManager().getPlugin("PhatLoots") != null) {
            Quests.phatLoots = (PhatLoots) getServer().getPluginManager().getPlugin("PhatLoots");
        }

        if (!setupEconomy()) {
            Quests.printWarning("[Quests] Economy not found.");
        }

        if (!setupPermissions()) {
            Quests.printWarning("[Quests] Permissions not found.");
        }

        vault = (Vault) getServer().getPluginManager().getPlugin("Vault");

        if (new File(getDataFolder(), "config.yml").exists() == false) {
            Quests.printInfo("[Quests] Config not found, writing default to file.");
            final FileConfiguration config = new YamlConfiguration();
            try {
                config.load(getResource("config.yml"));
                config.save(new File(getDataFolder(), "config.yml"));
            } catch (final IOException e) {
                e.printStackTrace();
            } catch (final InvalidConfigurationException e) {
                e.printStackTrace();
            }

        }

        loadConfig();
        loadModules();
        lang = new Lang(this);
        lang.initPhrases();
        lang.save();

        if (new File(getDataFolder(), "quests.yml").exists() == false) {

            Quests.printInfo("[Quests] Quest data not found, writing default to file.");
            final FileConfiguration data = new YamlConfiguration();
            try {
                data.load(getResource("quests.yml"));
                data.set("events", null);
                data.save(new File(getDataFolder(), "quests.yml"));
            } catch (final IOException e) {
                e.printStackTrace();
            } catch (final InvalidConfigurationException e) {
                e.printStackTrace();
            }

        }

        if (new File(getDataFolder(), "events.yml").exists() == false) {
            Quests.printInfo("[Quests] Events data not found, writing default to file.");
            final FileConfiguration data = new YamlConfiguration();
            data.options().copyHeader(true);
            data.options().copyDefaults(true);
            try {
                data.load(getResource("events.yml"));
                data.save(new File(getDataFolder(), "events.yml"));
            } catch (final IOException e) {
                e.printStackTrace();
            } catch (final InvalidConfigurationException e) {
                e.printStackTrace();
            }

        }

        if (new File(getDataFolder(), "data.yml").exists() == false) {
            Quests.printInfo("[Quests] Data file not found, writing default to file.");
            final FileConfiguration data = new YamlConfiguration();
            data.options().copyHeader(true);
            data.options().copyDefaults(true);
            try {
                data.save(new File(getDataFolder(), "data.yml"));
            } catch (final IOException e) {
                e.printStackTrace();
            }

        } else {
            loadData();
        }

        getServer().getPluginManager().registerEvents(pListener, this);
        if (Quests.npcEffects) {
            getServer().getScheduler().scheduleSyncRepeatingTask(this, effListener, 20, 20);
        }
        Quests.printInfo("[Quests] Enabled.");

        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                loadQuests();
                loadEvents();
                Quests.log.log(Level.INFO, "[Quests] " + quests.size() + " Quest(s) loaded.");
                Quests.log.log(Level.INFO, "[Quests] " + events.size() + " Event(s) loaded.");
                questers.putAll(getOnlineQuesters());
                if (Quests.snoop) {
                    snoop();
                }
            }
        }, 5L);

    }

    @Override
    public void onDisable() {

        Quests.printInfo("[Quests] Saving Quester data.");
        for (final Player p: getServer().getOnlinePlayers()) {

            final Quester quester = getQuester(p.getName());
            quester.saveData();

        }
        Quests.printInfo("[Quests] Disabled.");

    }

    public LinkedList<Quest> getQuests() {
        return quests;
    }

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {

        if (abandonedEvent.gracefulExit() == false) {

            if (abandonedEvent.getContext().getForWhom() != null) {

                try {
                    abandonedEvent.getContext().getForWhom().sendRawMessage(ColorUtil.YELLOW + "Cancelled.");
                } catch (final Exception e) {}

            }

        }

    }

    public static Quests getInstance() {
        return Quests.instance;
    }

    private class QuestPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {

            return ColorUtil.YELLOW + "Accept Quest?  " + ColorUtil.GREEN + "Yes / No";

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String s) {

            final Player player = (Player) context.getForWhom();

            if (s.equalsIgnoreCase("Yes")) {

                getQuester(player.getName()).takeQuest(getQuest(getQuester(player.getName()).questToTake), false);
                return Prompt.END_OF_CONVERSATION;

            } else if (s.equalsIgnoreCase("No")) {

                player.sendMessage(ColorUtil.YELLOW + "Cancelled.");
                return Prompt.END_OF_CONVERSATION;

            } else {

                player.sendMessage(ColorUtil.RED + "Invalid choice. Type \'Yes\' or \'No\'");
                return new QuestPrompt();

            }

        }
    }

    private class QuestsPrefix implements ConversationPrefix {

        @Override
        public String getPrefix(ConversationContext context) {

            return "" + ColorUtil.GRAY;

        }
    }

    public void loadConfig() {

        final FileConfiguration config = getConfig();

        allowCommands = config.getBoolean("allow-command-questing", true);
        allowCommandsForNpcQuests = config.getBoolean("allow-command-quests-with-npcs", false);
        showQuestReqs = config.getBoolean("show-requirements", true);
        allowQuitting = config.getBoolean("allow-quitting", true);
        Quests.genFilesOnJoin = config.getBoolean("generate-files-on-join", true);
        Quests.snoop = config.getBoolean("snoop", true);
        Quests.npcEffects = config.getBoolean("show-npc-effects", true);
        Quests.effect = config.getString("npc-effect", "note");
        debug = config.getBoolean("debug-mode", false);
        killDelay = config.getInt("kill-delay", 600);
        Quests.acceptTimeout = config.getInt("accept-timeout", 20);

        if (config.contains("language")) {
            Lang.lang = config.getString("language");
        } else {
            config.set("language", "en");
        }

        if (config.contains("ignore-locked-quests")) {
            Quests.ignoreLockedQuests = config.getBoolean("ignore-locked-quests");
        } else {
            config.set("ignore-locked-quests", false);
        }

        if (config.contains("broadcast-party-creation")) {
            Quests.broadcastPartyCreation = config.getBoolean("broadcast-party-creation");
        } else {
            config.set("broadcast-party-creation", true);
        }

        if (config.contains("max-party-size")) {
            Quests.maxPartySize = config.getInt("max-party-size");
        } else {
            config.set("max-party-size", 0);
        }

        if (config.contains("party-invite-timeout")) {
            Quests.inviteTimeout = config.getInt("party-invite-timeout");
        } else {
            config.set("party-invite-timeout", 20);
        }

        for (final String s: config.getStringList("quester-blacklist")) {
            questerBlacklist.add(s);
        }

        try {
            config.save(new File(getDataFolder(), "config.yml"));
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    public void loadData() {

        final YamlConfiguration config = new YamlConfiguration();
        final File dataFile = new File(getDataFolder(), "data.yml");

        try {
            config.load(dataFile);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        if (config.contains("npc-gui")) {

            final List<Integer> ids = config.getIntegerList("npc-gui");
            questNPCGUIs.clear();
            questNPCGUIs.addAll(ids);

        }

    }

    public void loadModules() {

        final File f = new File(getDataFolder(), "modules");
        if (f.exists() && f.isDirectory()) {

            final File[] modules = f.listFiles();
            for (final File module: modules) {

                loadModule(module);

            }

        } else {

            f.mkdir();

        }

    }

    public void loadModule(File jar) {

        try {

            final JarFile jarFile = new JarFile(jar);
            final Enumeration<JarEntry> e = jarFile.entries();

            final URL[] urls = {new URL("jar:file:" + jar.getPath() + "!/")};

            final ClassLoader cl = URLClassLoader.newInstance(urls, getClassLoader());

            while (e.hasMoreElements()) {

                final JarEntry je = e.nextElement();
                if (je.isDirectory() || !je.getName().endsWith(".class")) {
                    continue;
                }

                String className = je.getName().substring(0, je.getName().length() - 6);
                className = className.replace('/', '.');
                final Class<?> c = Class.forName(className, true, cl);

                if (CustomRequirement.class.isAssignableFrom(c)) {

                    final Class<? extends CustomRequirement> requirementClass = c.asSubclass(CustomRequirement.class);
                    final Constructor<? extends CustomRequirement> cstrctr = requirementClass.getConstructor();
                    final CustomRequirement requirement = cstrctr.newInstance();
                    customRequirements.add(requirement);
                    final String name = requirement.getName() == null ? "[" + jar.getName() + "]" : requirement
                            .getName();
                    final String author = requirement.getAuthor() == null ? "[Unknown]" : requirement.getAuthor();
                    Quests.printInfo("[Quests] Loaded Module: " + name + " by " + author);

                } else if (CustomReward.class.isAssignableFrom(c)) {

                    final Class<? extends CustomReward> rewardClass = c.asSubclass(CustomReward.class);
                    final Constructor<? extends CustomReward> cstrctr = rewardClass.getConstructor();
                    final CustomReward reward = cstrctr.newInstance();
                    customRewards.add(reward);
                    final String name = reward.getName() == null ? "[" + jar.getName() + "]" : reward.getName();
                    final String author = reward.getAuthor() == null ? "[Unknown]" : reward.getAuthor();
                    Quests.printInfo("[Quests] Loaded Module: " + name + " by " + author);

                } else if (CustomObjective.class.isAssignableFrom(c)) {

                    final Class<? extends CustomObjective> objectiveClass = c.asSubclass(CustomObjective.class);
                    final Constructor<? extends CustomObjective> cstrctr = objectiveClass.getConstructor();
                    final CustomObjective objective = cstrctr.newInstance();
                    customObjectives.add(objective);
                    final String name = objective.getName() == null ? "[" + jar.getName() + "]" : objective.getName();
                    final String author = objective.getAuthor() == null ? "[Unknown]" : objective.getAuthor();
                    Quests.printInfo("[Quests] Loaded Module: " + name + " by " + author);

                } else {
                    Quests.printSevere("[Quests] Error: Unable to load module from file: " + jar.getName()
                            + ", jar file is not a valid module!");
                }
            }
            jarFile.close();

        } catch (final Exception e) {
            Quests.printSevere("[Quests] Error: Unable to load module from file: " + jar.getName());
            if (debug) {
                Quests.printSevere("[Quests] Error log:");
                e.printStackTrace();
            }
        }

    }

    public void printHelp(Player player) {

        player.sendMessage(ColorUtil.GOLD + "- Quests -");
        player.sendMessage(ColorUtil.YELLOW + "/quests - Display this help");
        if (player.hasPermission("quests.list")) {
            player.sendMessage(ColorUtil.YELLOW + "/quests list <page> - List available Quests");
        }
        if (player.hasPermission("quests.take")) {
            player.sendMessage(ColorUtil.YELLOW + "/quests take <quest name> - Accept a Quest");
        }
        if (player.hasPermission("quests.quit")) {
            player.sendMessage(ColorUtil.YELLOW + "/quests quit - Quit your current Quest");
        }
        if (player.hasPermission("quests.editor.editor")) {
            player.sendMessage(ColorUtil.YELLOW + "/quests editor - Create/Edit Quests");
        }
        if (player.hasPermission("quests.editor.events.editor")) {
            player.sendMessage(ColorUtil.YELLOW + "/quests events - Create/Edit Events");
        }
        if (player.hasPermission("quests.stats")) {
            player.sendMessage(ColorUtil.YELLOW + "/quests stats - View your Questing stats");
        }
        if (player.hasPermission("quests.top")) {
            player.sendMessage(ColorUtil.YELLOW + "/quests top <number> - View top Questers");
        }
        // player.sendMessage(GOLD + "/quests party - Quest Party commands");
        player.sendMessage(ColorUtil.YELLOW + "/quests info - Display plugin information");
        player.sendMessage(" ");
        player.sendMessage(ColorUtil.YELLOW + "/quest - Display current Quest objectives");
        if (player.hasPermission("quests.questinfo")) {
            player.sendMessage(ColorUtil.YELLOW + "/quest <quest name> - Display Quest information");
        }

        if (player.hasPermission("quests.admin")) {
            player.sendMessage(ColorUtil.DARKRED + "/questadmin " + ColorUtil.RED + "- Questadmin help");
        }

    }

    public void printPartyHelp(Player player) {

        player.sendMessage(ColorUtil.PURPLE + "- Quest Parties -");
        player.sendMessage(ColorUtil.PINK + "/quests party create - Create new party");
        player.sendMessage(ColorUtil.PINK + "/quests party leave - Leave your party");
        player.sendMessage(ColorUtil.PINK + "/quests party info - Info about your party");
        player.sendMessage(ColorUtil.PURPLE + "- (Leader only) -");
        player.sendMessage(ColorUtil.PINK + "/quests party invite <player> - Invite a player to your party");
        player.sendMessage(ColorUtil.PINK + "/quests party kick <player> - Kick a member from the party");
        player.sendMessage(ColorUtil.PINK + "/quests party setleader <player> - Set a party member as the new leader");

    }

    @Override
    public boolean onCommand(final CommandSender cs, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("quest")) {

            if (cs instanceof Player) {

                if (((Player) cs).hasPermission("quests.quest")) {

                    if (args.length == 0) {

                        if (getQuester(cs.getName()).currentQuest != null) {

                            if (getQuester(cs.getName()).delayStartTime == 0) {
                                cs.sendMessage(ColorUtil.GOLD + "---(Objectives)---");
                            }

                            for (final String s: getQuester(cs.getName()).getObjectivesReal()) {

                                cs.sendMessage(s);

                            }

                        } else {

                            cs.sendMessage(ColorUtil.YELLOW + "You do not currently have an active Quest.");
                            return true;

                        }

                    } else {

                        if (((Player) cs).hasPermission("quests.questinfo")) {

                            String name = "";

                            if (args.length == 1) {
                                name = args[0].toLowerCase();
                            } else {

                                int index = 0;
                                for (final String s: args) {

                                    if (index == (args.length - 1)) {
                                        name = name + s.toLowerCase();
                                    } else {
                                        name = name + s.toLowerCase() + " ";
                                    }

                                    index++;

                                }
                            }

                            final Quest quest = findQuest(name);

                            if (quest != null) {

                                final Player player = (Player) cs;
                                final Quester quester = getQuester(player.getName());

                                cs.sendMessage(ColorUtil.GOLD + "- " + quest.name + " -");
                                cs.sendMessage(" ");
                                if (quest.redoDelay > -1) {

                                    if (quest.redoDelay == 0) {
                                        cs.sendMessage(ColorUtil.DARKAQUA + "Redoable");
                                    } else {
                                        cs.sendMessage(ColorUtil.DARKAQUA + "Redoable every " + ColorUtil.AQUA
                                                + Quests.getTime(quest.redoDelay) + ColorUtil.DARKAQUA + ".");
                                    }

                                }
                                if (quest.npcStart != null) {
                                    cs.sendMessage(ColorUtil.YELLOW + "Start: Speak to " + quest.npcStart.getName());
                                } else {
                                    cs.sendMessage(ColorUtil.YELLOW + quest.description);
                                }

                                cs.sendMessage(" ");

                                if (showQuestReqs == true) {

                                    cs.sendMessage(ColorUtil.GOLD + "Requirements");

                                    if (quest.permissionReqs.isEmpty() == false) {

                                        for (final String perm: quest.permissionReqs) {

                                            if (Quests.permission.has(player, perm)) {
                                                cs.sendMessage(ColorUtil.GREEN + "Permission: " + perm);
                                            } else {
                                                cs.sendMessage(ColorUtil.RED + "Permission: " + perm);
                                            }

                                        }

                                    }

                                    if (quest.heroesPrimaryClassReq != null) {

                                        if (testPrimaryHeroesClass(quest.heroesPrimaryClassReq, player.getName())) {
                                            cs.sendMessage(ColorUtil.BOLD + "" + ColorUtil.GREEN
                                                    + quest.heroesPrimaryClassReq + ColorUtil.RESET + ""
                                                    + ColorUtil.DARKGREEN + " class");
                                        } else {
                                            cs.sendMessage(ColorUtil.BOLD + "" + ColorUtil.DARKRED
                                                    + quest.heroesPrimaryClassReq + ColorUtil.RESET + ""
                                                    + ColorUtil.RED + " class");
                                        }

                                    }

                                    if (quest.heroesSecondaryClassReq != null) {

                                        if (testSecondaryHeroesClass(quest.heroesSecondaryClassReq, player.getName())) {
                                            cs.sendMessage(ColorUtil.BOLD + "" + ColorUtil.DARKRED
                                                    + quest.heroesSecondaryClassReq + ColorUtil.RESET + ""
                                                    + ColorUtil.RED + " class");
                                        } else {
                                            cs.sendMessage(ColorUtil.BOLD + "" + ColorUtil.GREEN
                                                    + quest.heroesSecondaryClassReq + ColorUtil.RESET + ""
                                                    + ColorUtil.DARKGREEN + " class");
                                        }

                                    }

                                    if (quest.mcMMOSkillReqs.isEmpty() == false) {

                                        for (final String skill: quest.mcMMOSkillReqs) {

                                            final int level = Quests.getMCMMOSkillLevel(Quests.getMcMMOSkill(skill),
                                                    player.getName());
                                            final int req = quest.mcMMOAmountReqs.get(quest.mcMMOSkillReqs
                                                    .indexOf(skill));
                                            final String skillName = MiscUtil.getCapitalized(skill);

                                            if (level >= req) {
                                                cs.sendMessage(ColorUtil.GREEN + skillName + " level " + req);
                                            } else {
                                                cs.sendMessage(ColorUtil.RED + skillName + " level " + req);
                                            }

                                        }

                                    }

                                    if (quest.questPointsReq != 0) {

                                        if (quester.questPoints >= quest.questPointsReq) {
                                            cs.sendMessage(ColorUtil.GRAY + "- " + ColorUtil.GREEN
                                                    + quest.questPointsReq + " Quest Points");
                                        } else {
                                            cs.sendMessage(ColorUtil.GRAY + "- " + ColorUtil.RED + quest.questPointsReq
                                                    + " Quest Points");
                                        }

                                    }

                                    if (quest.moneyReq != 0) {

                                        if (Quests.economy.getBalance(quester.name) >= quest.moneyReq) {
                                            if (quest.moneyReq == 1) {
                                                cs.sendMessage(ColorUtil.GRAY + "- " + ColorUtil.GREEN + quest.moneyReq
                                                        + " " + Quests.getCurrency(false));
                                            } else {
                                                cs.sendMessage(ColorUtil.GRAY + "- " + ColorUtil.GREEN + quest.moneyReq
                                                        + " " + Quests.getCurrency(true));
                                            }
                                        } else {
                                            if (quest.moneyReq == 1) {
                                                cs.sendMessage(ColorUtil.GRAY + "- " + ColorUtil.RED + quest.moneyReq
                                                        + " " + Quests.getCurrency(false));
                                            } else {
                                                cs.sendMessage(ColorUtil.GRAY + "- " + ColorUtil.RED + quest.moneyReq
                                                        + " " + Quests.getCurrency(true));
                                            }
                                        }

                                    }

                                    if (quest.items.isEmpty() == false) {

                                        for (final ItemStack is: quest.items) {

                                            if (hasItem(player, is) == true) {
                                                cs.sendMessage(ColorUtil.GRAY + "- " + ColorUtil.GREEN
                                                        + ItemUtil.getString(is));
                                            } else {
                                                cs.sendMessage(ColorUtil.GRAY + "- " + ColorUtil.RED
                                                        + ItemUtil.getString(is));
                                            }

                                        }

                                    }

                                    if (quest.neededQuests.isEmpty() == false) {

                                        for (final String s: quest.neededQuests) {

                                            if (quester.completedQuests.contains(s)) {
                                                cs.sendMessage(ColorUtil.GRAY + "- " + ColorUtil.GREEN + "Complete "
                                                        + ColorUtil.ITALIC + s);
                                            } else {
                                                cs.sendMessage(ColorUtil.GRAY + "- " + ColorUtil.RED + "Complete "
                                                        + ColorUtil.ITALIC + s);
                                            }

                                        }

                                    }

                                    if (quest.blockQuests.isEmpty() == false) {

                                        for (final String s: quest.blockQuests) {

                                            if (quester.completedQuests.contains(s)) {
                                                cs.sendMessage(ColorUtil.GRAY + "- " + ColorUtil.RED
                                                        + "You have already Completed " + ColorUtil.ITALIC + s);
                                            } else {
                                                cs.sendMessage(ColorUtil.GRAY + "- " + ColorUtil.GREEN
                                                        + "Still able to complete " + ColorUtil.ITALIC + s);
                                            }

                                        }

                                    }

                                }

                            } else {

                                cs.sendMessage(ColorUtil.YELLOW + "Quest not found.");
                                return true;

                            }

                        } else {

                            cs.sendMessage(ColorUtil.RED + "You do not have permission to view a Quest's information.");
                            return true;

                        }

                    }

                } else {

                    cs.sendMessage(ColorUtil.RED + "You do not have access to that command.");
                    return true;

                }

            } else {

                cs.sendMessage(ColorUtil.YELLOW + "This command may only be performed in-game.");
                return true;

            }

            return true;

        } else if (cmd.getName().equalsIgnoreCase("quests")) {

            if (cs instanceof Player) {

                if (args.length == 0) {

                    if (((Player) cs).hasPermission("quests.quests")) {

                        final Player p = (Player) cs;
                        printHelp(p);

                    } else {

                        cs.sendMessage(ColorUtil.RED + "You do not have access to that command.");
                        return true;

                    }

                } else {

                    if (args[0].equalsIgnoreCase("list")) {

                        if (((Player) cs).hasPermission("quests.list")) {

                            if (args.length == 1) {
                                listQuests((Player) cs, 1);
                            } else if (args.length == 2) {

                                int page;

                                try {

                                    page = Integer.parseInt(args[1]);
                                    if (page < 1) {

                                        cs.sendMessage(ColorUtil.YELLOW + "Page selection must be a positive number.");
                                        return true;

                                    }

                                } catch (final NumberFormatException e) {

                                    cs.sendMessage(ColorUtil.YELLOW + "Page selection must be a number.");
                                    return true;

                                }

                                listQuests((Player) cs, page);
                                return true;

                            }

                        } else {

                            cs.sendMessage(ColorUtil.RED + "You do not have permission to view the Quests list.");
                            return true;

                        }

                    } else if (args[0].equalsIgnoreCase("take")) {

                        if (allowCommands == true) {

                            if (((Player) cs).hasPermission("quests.take")) {

                                if (args.length == 1) {

                                    cs.sendMessage(ColorUtil.YELLOW + "Usage: /quests take <quest>");
                                    return true;

                                } else {

                                    String name = null;

                                    if (args.length == 2) {
                                        name = args[1].toLowerCase();
                                    } else {

                                        boolean first = true;
                                        final int lastIndex = (args.length - 1);
                                        int index = 0;

                                        for (final String s: args) {

                                            if (index != 0) {

                                                if (first) {

                                                    first = false;
                                                    if (args.length > 2) {
                                                        name = s.toLowerCase() + " ";
                                                    } else {
                                                        name = s.toLowerCase();
                                                    }

                                                } else if (index == lastIndex) {
                                                    name = name + s.toLowerCase();
                                                } else {
                                                    name = name + s.toLowerCase() + " ";
                                                }

                                            }

                                            index++;

                                        }

                                    }

                                    final Quest questToFind = findQuest(name);

                                    if (questToFind != null) {

                                        final Quest quest = questToFind;
                                        final Quester quester = getQuester(cs.getName());

                                        if (quester.currentQuest != null) {
                                            cs.sendMessage(ColorUtil.YELLOW + "You may only have one active Quest.");
                                        } else if (quester.completedQuests.contains(quest.name) && quest.redoDelay < 0) {
                                            cs.sendMessage(ColorUtil.YELLOW + "You have already completed "
                                                    + ColorUtil.PURPLE + quest.name + ColorUtil.YELLOW + ".");
                                        } else if (quest.npcStart != null && allowCommandsForNpcQuests == false) {
                                            cs.sendMessage(ColorUtil.YELLOW + "You must speak to " + ColorUtil.PURPLE
                                                    + quest.npcStart.getName() + ColorUtil.YELLOW
                                                    + " to start this Quest.");
                                        } else if (quest.blockStart != null) {
                                            cs.sendMessage(ColorUtil.PURPLE + quest.name + ColorUtil.YELLOW
                                                    + " may not be started via command.");
                                        } else {

                                            boolean takeable = true;

                                            if (quester.completedQuests.contains(quest.name)) {

                                                if (quester.getDifference(quest) > 0) {
                                                    cs.sendMessage(ColorUtil.YELLOW + "You may not take "
                                                            + ColorUtil.AQUA + quest.name + ColorUtil.YELLOW
                                                            + " again for another " + ColorUtil.PURPLE
                                                            + Quests.getTime(quester.getDifference(quest))
                                                            + ColorUtil.YELLOW + ".");
                                                    takeable = false;
                                                }

                                            }

                                            if (quest.region != null) {

                                                boolean inRegion = false;
                                                final Player p = quester.getPlayer();
                                                final RegionManager rm = Quests.worldGuard.getRegionManager(p
                                                        .getWorld());
                                                final Iterator<ProtectedRegion> it = rm.getApplicableRegions(
                                                        p.getLocation()).iterator();
                                                while (it.hasNext()) {
                                                    final ProtectedRegion pr = it.next();
                                                    if (pr.getId().equalsIgnoreCase(quest.region)) {
                                                        inRegion = true;
                                                        break;
                                                    }
                                                }

                                                if (inRegion == false) {
                                                    cs.sendMessage(ColorUtil.YELLOW + "You may not take "
                                                            + ColorUtil.AQUA + quest.name + ColorUtil.YELLOW
                                                            + " at this location.");
                                                    takeable = false;
                                                }

                                            }

                                            if (takeable == true) {

                                                if (cs instanceof Conversable) {

                                                    if (((Player) cs).isConversing() == false) {

                                                        quester.questToTake = quest.name;

                                                        final String s = ColorUtil.GOLD + "- " + ColorUtil.PURPLE
                                                                + quester.questToTake + ColorUtil.GOLD + " -\n" + "\n"
                                                                + ColorUtil.RESET
                                                                + getQuest(quester.questToTake).description + "\n";

                                                        for (final String msg: s.split("<br>")) {
                                                            cs.sendMessage(msg);
                                                        }

                                                        conversationFactory.buildConversation((Conversable) cs).begin();

                                                    } else {

                                                        cs.sendMessage(ColorUtil.YELLOW
                                                                + "You are already in a conversation!");

                                                    }

                                                    return true;
                                                } else {
                                                    return false;
                                                }

                                            }

                                        }

                                    } else {
                                        cs.sendMessage(ColorUtil.YELLOW + "Quest not found.");
                                        return true;
                                    }

                                }

                            } else {

                                cs.sendMessage(ColorUtil.RED
                                        + "You do not have permission to take Quests via commands.");
                                return true;

                            }

                        } else {

                            cs.sendMessage(ColorUtil.YELLOW + "Taking Quests via commands has been disabled.");
                            return true;

                        }

                    } else if (args[0].equalsIgnoreCase("quit")) {

                        if (allowQuitting == true) {

                            if (((Player) cs).hasPermission("quests.quit")) {

                                final Quester quester = getQuester(cs.getName());
                                if (quester.currentQuest != null) {

                                    quester.resetObjectives();
                                    quester.currentStage = null;
                                    quester.currentStageIndex = 0;
                                    cs.sendMessage(ColorUtil.YELLOW + "You have quit " + ColorUtil.PURPLE
                                            + quester.currentQuest.name + ColorUtil.YELLOW + ".");
                                    quester.currentQuest = null;
                                    quester.saveData();
                                    quester.loadData();
                                    return true;

                                }
                                cs.sendMessage(ColorUtil.YELLOW + "You do not currently have an active Quest.");
                                return true;

                            }
                            return extracted(cs);

                        } else {

                            cs.sendMessage(ColorUtil.YELLOW + "Quitting Quests has been disabled.");
                            return true;

                        }

                    } else if (args[0].equalsIgnoreCase("stats")) {

                        final Quester quester = getQuester(cs.getName());
                        cs.sendMessage(ColorUtil.GOLD + "- " + cs.getName() + " -");
                        cs.sendMessage(ColorUtil.YELLOW + "Quest points: " + ColorUtil.PURPLE + quester.questPoints
                                + "/" + totalQuestPoints);
                        if (quester.currentQuest == null) {
                            cs.sendMessage(ColorUtil.YELLOW + "Current Quest: " + ColorUtil.PURPLE + "None");
                        } else {
                            cs.sendMessage(ColorUtil.YELLOW + "Current Quest: " + ColorUtil.PURPLE
                                    + quester.currentQuest.name);
                        }

                        String completed;

                        if (quester.completedQuests.isEmpty()) {
                            completed = ColorUtil.PURPLE + "None";
                        } else {

                            completed = ColorUtil.PURPLE + "";
                            for (final String s: quester.completedQuests) {

                                completed += s;

                                if (quester.amountsCompleted.containsKey(s) && quester.amountsCompleted.get(s) > 1) {
                                    completed += ColorUtil.PINK + " (x" + quester.amountsCompleted.get(s) + ")";
                                }

                                if (quester.completedQuests.indexOf(s) < (quester.completedQuests.size() - 1)) {
                                    completed += ", ";
                                }

                            }

                        }

                        cs.sendMessage(ColorUtil.YELLOW + "- Completed Quests -");
                        cs.sendMessage(completed);

                    } else if (args[0].equalsIgnoreCase("top")) {

                        if (args.length == 1 || args.length > 2) {

                            cs.sendMessage(ColorUtil.YELLOW + "Usage: /quests top <number>");

                        } else {

                            int topNumber;

                            try {

                                topNumber = Integer.parseInt(args[1]);

                            } catch (final NumberFormatException e) {

                                cs.sendMessage(ColorUtil.YELLOW + "Input must be a number.");
                                return true;

                            }

                            if (topNumber < 1) {

                                cs.sendMessage(ColorUtil.YELLOW + "Input must be a positive number.");
                                return true;

                            }

                            final File folder = new File(getDataFolder(), "data");
                            final File[] playerFiles = folder.listFiles();

                            final Map<String, Integer> questPoints = new HashMap<String, Integer>();

                            for (final File f: playerFiles) {

                                final FileConfiguration data = new YamlConfiguration();
                                try {

                                    data.load(f);

                                } catch (final IOException e) {

                                    e.printStackTrace();

                                } catch (final InvalidConfigurationException e) {
                                    e.printStackTrace();
                                }

                                final String name = f.getName().substring(0, (f.getName().indexOf(".")));
                                questPoints.put(name, data.getInt("quest-points"));

                            }

                            final LinkedHashMap<String, Integer> sortedMap = (LinkedHashMap<String, Integer>) Quests
                                    .sort(questPoints);

                            int numPrinted = 0;

                            cs.sendMessage(ColorUtil.GOLD + "- Top " + ColorUtil.PURPLE + topNumber + ColorUtil.GOLD
                                    + " Questers -");
                            for (final String s: sortedMap.keySet()) {

                                final int i = sortedMap.get(s);
                                numPrinted++;
                                cs.sendMessage(ColorUtil.YELLOW + String.valueOf(numPrinted) + ". " + s + " - "
                                        + ColorUtil.PURPLE + i + ColorUtil.YELLOW + " Quest points");

                                if (numPrinted == topNumber) {
                                    break;
                                }

                            }

                        }

                        return true;

                    } else if (args[0].equalsIgnoreCase("editor")) {

                        if (cs.hasPermission("quests.editor.editor")) {
                            questFactory.convoCreator.buildConversation((Conversable) cs).begin();
                        } else {
                            cs.sendMessage(ColorUtil.RED + "You do not have permission to use the Quests Editor.");
                        }
                        return true;

                    } else if (args[0].equalsIgnoreCase("events")) {

                        if (cs.hasPermission("quests.editor.events.editor")) {
                            eventFactory.convoCreator.buildConversation((Conversable) cs).begin();
                        } else {
                            cs.sendMessage(ColorUtil.RED + "You do not have permission to use the Events editor.");
                        }
                        return true;

                    } else if (args[0].equalsIgnoreCase("info")) {

                        cs.sendMessage(ColorUtil.GOLD + "Quests " + getDescription().getVersion());
                        cs.sendMessage(ColorUtil.GOLD + "Made by " + ColorUtil.DARKRED + "Blackvein");
                        return true;

                    } else {

                        cs.sendMessage(ColorUtil.YELLOW + "Unknown Quests command. Type /quests for help.");
                        return true;

                    }

                }

            } else {

                cs.sendMessage(ColorUtil.YELLOW + "This command may only be performed in-game.");
                return true;

            }

            return true;

        } else if (cmd.getName().equalsIgnoreCase("questadmin")) {

            if (args.length == 0) {

                if (cs.hasPermission("quests.admin")) {
                    printAdminHelp(cs);
                } else {
                    cs.sendMessage(ColorUtil.RED + "You do not have access to that command.");
                }

            } else if (args.length == 1) {

                if (args[0].equalsIgnoreCase("reload")) {

                    if (cs.hasPermission("quests.admin.reload")) {
                        reloadQuests();
                        cs.sendMessage(ColorUtil.GOLD + "Quests reloaded.");
                        cs.sendMessage(ColorUtil.PURPLE + String.valueOf(quests.size()) + ColorUtil.GOLD
                                + " Quests loaded.");
                    } else {
                        cs.sendMessage(ColorUtil.RED + "You do not have access to that command.");
                    }

                } else {

                    cs.sendMessage(ColorUtil.YELLOW + "Unknown Questadmin command. Type /questadmin for help.");

                }

            } else if (args.length >= 2) {

                if (args[0].equalsIgnoreCase("quit")) {

                    if (cs.hasPermission("quests.admin.quit")) {

                        Player target = null;

                        for (final Player p: getServer().getOnlinePlayers()) {

                            if (p.getName().toLowerCase().contains(args[1].toLowerCase())) {
                                target = p;
                                break;
                            }

                        }

                        if (target == null) {

                            cs.sendMessage(ColorUtil.YELLOW + "Player not found.");

                        } else {

                            final Quester quester = getQuester(target.getName());
                            if (quester.currentQuest == null) {

                                cs.sendMessage(ColorUtil.YELLOW + target.getName()
                                        + " does not currently have an active Quest.");

                            } else {

                                quester.resetObjectives();
                                quester.currentStage = null;
                                quester.currentStageIndex = 0;
                                cs.sendMessage(ColorUtil.GREEN + target.getName() + ColorUtil.GOLD
                                        + " has forcibly quit the Quest " + ColorUtil.PURPLE
                                        + quester.currentQuest.name + ColorUtil.GOLD + ".");
                                target.sendMessage(ColorUtil.GREEN + cs.getName() + ColorUtil.GOLD
                                        + " has forced you to quit the Quest " + ColorUtil.PURPLE
                                        + quester.currentQuest.name + ColorUtil.GOLD + ".");
                                quester.currentQuest = null;

                                quester.saveData();

                            }

                        }

                    } else {

                        cs.sendMessage(ColorUtil.RED + "You do not have access to that command.");

                    }

                } else if (args[0].equalsIgnoreCase("nextstage")) {

                    if (cs.hasPermission("quests.admin.nextstage")) {

                        Player target = null;

                        for (final Player p: getServer().getOnlinePlayers()) {

                            if (p.getName().toLowerCase().contains(args[1].toLowerCase())) {
                                target = p;
                                break;
                            }

                        }

                        if (target == null) {

                            cs.sendMessage(ColorUtil.YELLOW + "Player not found.");

                        } else {

                            final Quester quester = getQuester(target.getName());
                            if (quester.currentQuest == null) {

                                cs.sendMessage(ColorUtil.YELLOW + target.getName()
                                        + " does not currently have an active Quest.");

                            } else {

                                cs.sendMessage(ColorUtil.GREEN + target.getName() + ColorUtil.GOLD
                                        + " has advanced to the next Stage in the Quest " + ColorUtil.PURPLE
                                        + quester.currentQuest.name + ColorUtil.GOLD + ".");
                                target.sendMessage(ColorUtil.GREEN + cs.getName() + ColorUtil.GOLD
                                        + " has advanced you to the next Stage in your Quest " + ColorUtil.PURPLE
                                        + quester.currentQuest.name + ColorUtil.GOLD + ".");
                                quester.currentQuest.nextStage(quester);

                                quester.saveData();

                            }

                        }

                    } else {

                        cs.sendMessage(ColorUtil.RED + "You do not have access to that command.");

                    }

                } else if (args[0].equalsIgnoreCase("setstage")) {

                    if (cs.hasPermission("quests.admin.setstage")) {

                        Player target = null;

                        for (final Player p: getServer().getOnlinePlayers()) {

                            // To ensure the correct player is selected
                            if (p.getName().equalsIgnoreCase(args[1])) {
                                target = p;
                                break;
                            }

                        }

                        if (target == null) {
                            //
                            for (final Player p: getServer().getOnlinePlayers()) {

                                if (p.getName().toLowerCase().contains(args[1].toLowerCase())) {
                                    target = p;
                                    break;
                                }
                            }
                        }
                        int stage = -1;
                        if (args.length > 2) {
                            try {
                                stage = Integer.parseInt(args[2]);
                            } catch (final NumberFormatException e) {
                                cs.sendMessage(ColorUtil.YELLOW + "Invalid number");
                            }
                        } else {
                            cs.sendMessage(ColorUtil.YELLOW + "Enter a stage");
                            return true;
                        }

                        if (target == null) {

                            cs.sendMessage(ColorUtil.YELLOW + "Player not found.");

                        } else {

                            final Quester quester = getQuester(target.getName());
                            if (quester.currentQuest == null) {

                                cs.sendMessage(ColorUtil.YELLOW + target.getName()
                                        + " does not currently have an active Quest.");

                            } else {

                                try {
                                    quester.currentQuest.setStage(quester, stage);
                                } catch (final InvalidStageException e) {
                                    cs.sendMessage(ChatColor.RED + "Advancing " + target.getName() + " to Stage: "
                                            + stage + ", has failed.");
                                    cs.sendMessage(ChatColor.RED + "Not enough stages.");
                                }

                                quester.saveData();

                            }

                        }

                    } else {

                        cs.sendMessage(ColorUtil.RED + "You do not have access to that command.");

                    }

                } else if (args[0].equalsIgnoreCase("finish")) {

                    if (cs.hasPermission("quests.admin.finish")) {

                        Player target = null;

                        for (final Player p: getServer().getOnlinePlayers()) {

                            if (p.getName().toLowerCase().contains(args[1].toLowerCase())) {
                                target = p;
                                break;
                            }

                        }

                        if (target == null) {

                            cs.sendMessage(ColorUtil.YELLOW + "Player not found.");

                        } else {

                            final Quester quester = getQuester(target.getName());
                            if (quester.currentQuest == null) {

                                cs.sendMessage(ColorUtil.YELLOW + target.getName()
                                        + " does not currently have an active Quest.");

                            } else {

                                cs.sendMessage(ColorUtil.GREEN + target.getName() + ColorUtil.GOLD
                                        + " has advanced to the next Stage in the Quest " + ColorUtil.PURPLE
                                        + quester.currentQuest.name + ColorUtil.GOLD + ".");
                                target.sendMessage(ColorUtil.GREEN + cs.getName() + ColorUtil.GOLD
                                        + " has advanced you to the next Stage in your Quest " + ColorUtil.PURPLE
                                        + quester.currentQuest.name + ColorUtil.GOLD + ".");
                                quester.currentQuest.completeQuest(quester);

                                quester.saveData();

                            }

                        }

                    } else {

                        cs.sendMessage(ColorUtil.RED + "You do not have access to that command.");

                    }

                } else if (args[0].equalsIgnoreCase("pointsall")) {

                    if (cs.hasPermission("quests.admin.points.all")) {

                        final int amount;

                        try {

                            amount = Integer.parseInt(args[1]);

                            if (amount < 0) {
                                cs.sendMessage(ColorUtil.RED + "Error: Amount cannot be less than zero!");
                                return true;
                            }

                        } catch (final NumberFormatException e) {
                            cs.sendMessage(ColorUtil.RED + "Error: Input was not a number!");
                            return true;
                        }

                        final Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final File questerFolder = new File(Quests.this.getDataFolder(), "data");
                                if (questerFolder.exists() && questerFolder.isDirectory()) {

                                    final FileConfiguration data = new YamlConfiguration();
                                    int failCount = 0;
                                    boolean suppressed = false;

                                    for (final File f: questerFolder.listFiles()) {

                                        try {

                                            data.load(f);
                                            data.set("quest-points", amount);
                                            data.save(f);

                                        } catch (final IOException e) {

                                            if (failCount < 10) {
                                                cs.sendMessage(ColorUtil.RED + "Error reading " + ColorUtil.DARKAQUA
                                                        + f.getName() + ColorUtil.RED + ", skipping..");
                                                failCount++;
                                            } else if (suppressed == false) {
                                                cs.sendMessage(ColorUtil.RED + "Error reading " + ColorUtil.DARKAQUA
                                                        + f.getName() + ColorUtil.RED + ", suppressing further errors.");
                                                suppressed = true;
                                            }

                                        } catch (final InvalidConfigurationException e) {
                                            if (failCount < 10) {
                                                cs.sendMessage(ColorUtil.RED + "Error reading " + ColorUtil.DARKAQUA
                                                        + f.getName() + ColorUtil.RED + ", skipping..");
                                                failCount++;
                                            } else if (suppressed == false) {
                                                cs.sendMessage(ColorUtil.RED + "Error reading " + ColorUtil.DARKAQUA
                                                        + f.getName() + ColorUtil.RED + ", suppressing further errors.");
                                                suppressed = true;
                                            }
                                        }

                                    }

                                    cs.sendMessage(ColorUtil.GREEN + "Done.");
                                    getServer().broadcastMessage(
                                            ColorUtil.YELLOW + "[Quests] " + ColorUtil.GOLD
                                                    + "All players' Quest Points have been set to " + ColorUtil.AQUA
                                                    + amount + ColorUtil.GOLD + "!");

                                } else {
                                    cs.sendMessage(ColorUtil.RED + "Error: Unable to read Quests data folder!");
                                }

                            }
                        });

                        cs.sendMessage(ColorUtil.YELLOW + "Setting all players' Quest Points...");
                        for (final Quester q: questers.values()) {

                            q.questPoints = amount;

                        }
                        thread.start();

                    } else {

                        cs.sendMessage(ColorUtil.RED + "You do not have access to that command.");

                    }

                } else if (args[0].equalsIgnoreCase("give")) {

                    if (cs.hasPermission("quests.admin.give")) {

                        Player target = null;

                        for (final Player p: getServer().getOnlinePlayers()) {

                            if (p.getName().toLowerCase().contains(args[1].toLowerCase())) {
                                target = p;
                                break;
                            }

                        }

                        if (target == null) {

                            cs.sendMessage(ColorUtil.YELLOW + "Player not found.");

                        } else {

                            Quest questToGive;

                            String name = null;

                            if (args.length == 3) {
                                name = args[2].toLowerCase();
                            } else {

                                boolean first = true;
                                final int lastIndex = (args.length - 1);
                                int index = 0;

                                for (final String s: args) {

                                    if (index != 0) {

                                        if (first) {

                                            first = false;
                                            if (args.length > 2) {
                                                name = s.toLowerCase() + " ";
                                            } else {
                                                name = s.toLowerCase();
                                            }

                                        } else if (index == lastIndex) {
                                            name = name + s.toLowerCase();
                                        } else {
                                            name = name + s.toLowerCase() + " ";
                                        }

                                    }

                                    index++;

                                }

                            }

                            questToGive = findQuest(name);

                            if (questToGive == null) {

                                cs.sendMessage(ColorUtil.YELLOW + "Quest not found.");

                            } else {

                                final Quester quester = getQuester(target.getName());
                                quester.resetObjectives();

                                cs.sendMessage(ColorUtil.GREEN + target.getName() + ColorUtil.GOLD
                                        + " has forcibly started the Quest " + ColorUtil.PURPLE + questToGive.name
                                        + ColorUtil.GOLD + ".");
                                target.sendMessage(ColorUtil.GREEN + "You have been forced to take the Quest "
                                        + ColorUtil.PURPLE + questToGive.name + ColorUtil.GOLD + ".");
                                quester.takeQuest(questToGive, true);

                            }

                        }

                    } else {

                        cs.sendMessage(ColorUtil.RED + "You do not have access to that command.");

                    }

                } else if (args[0].equalsIgnoreCase("points")) {

                    if (cs.hasPermission("quests.admin.points")) {

                        Player target = null;

                        for (final Player p: getServer().getOnlinePlayers()) {

                            if (p.getName().equalsIgnoreCase(args[1])) {
                                target = p;
                                break;
                            }

                        }

                        if (target == null) {

                            cs.sendMessage(ColorUtil.YELLOW + "Player not found.");

                        } else {

                            int points;

                            try {

                                points = Integer.parseInt(args[2]);

                            } catch (final NumberFormatException e) {

                                cs.sendMessage(ColorUtil.YELLOW + "Amount must be a number.");
                                return true;

                            }

                            final Quester quester = getQuester(target.getName());
                            quester.questPoints = points;
                            cs.sendMessage(ColorUtil.GREEN + target.getName() + ColorUtil.GOLD
                                    + "\'s Quest Points have been set to " + ColorUtil.PURPLE + points + ColorUtil.GOLD
                                    + ".");
                            target.sendMessage(ColorUtil.GREEN + cs.getName() + ColorUtil.GOLD
                                    + " has set your Quest Points to " + ColorUtil.PURPLE + points + ColorUtil.GOLD
                                    + ".");

                            quester.saveData();

                        }

                    } else {

                        cs.sendMessage(ColorUtil.RED + "You do not have access to that command.");

                    }

                } else if (args[0].equalsIgnoreCase("takepoints")) {

                    if (cs.hasPermission("quests.admin.takepoints")) {

                        Player target = null;

                        for (final Player p: getServer().getOnlinePlayers()) {

                            if (p.getName().equalsIgnoreCase(args[1])) {
                                target = p;
                                break;
                            }

                        }

                        if (target == null) {

                            cs.sendMessage(ColorUtil.YELLOW + "Player not found.");

                        } else {

                            int points;

                            try {

                                points = Integer.parseInt(args[2]);

                            } catch (final NumberFormatException e) {

                                cs.sendMessage(ColorUtil.YELLOW + "Amount must be a number.");
                                return true;

                            }
                            final Quester quester = getQuester(target.getName());
                            quester.questPoints -= Math.abs(points);
                            cs.sendMessage(ColorUtil.GOLD + "Took away " + ColorUtil.PURPLE + points + ColorUtil.GOLD
                                    + " Quest Points from " + ColorUtil.GREEN + target.getName() + ColorUtil.GOLD
                                    + "\'s.");
                            target.sendMessage(ColorUtil.GREEN + cs.getName() + ColorUtil.GOLD + " took away "
                                    + ColorUtil.PURPLE + points + ColorUtil.GOLD + " Quest Points.");

                            quester.saveData();

                        }

                    } else {

                        cs.sendMessage(ColorUtil.RED + "You do not have access to that command.");

                    }
                } else if (args[0].equalsIgnoreCase("givepoints")) {

                    if (cs.hasPermission("quests.admin.givepoints")) {

                        Player target = null;

                        for (final Player p: getServer().getOnlinePlayers()) {

                            if (p.getName().equalsIgnoreCase(args[1])) {
                                target = p;
                                break;
                            }

                        }

                        if (target == null) {

                            cs.sendMessage(ColorUtil.YELLOW + "Player not found.");

                        } else {

                            int points;

                            try {

                                points = Integer.parseInt(args[2]);

                                final Quester quester = getQuester(target.getName());
                                quester.questPoints += Math.abs(points);
                                cs.sendMessage(ColorUtil.GOLD + "Gave " + ColorUtil.PURPLE + points + ColorUtil.GOLD
                                        + " Quest Points to " + ColorUtil.GREEN + target.getName() + ColorUtil.GOLD
                                        + ".");
                                target.sendMessage(ColorUtil.GREEN + cs.getName() + ColorUtil.GOLD + " gave you "
                                        + ColorUtil.PURPLE + points + ColorUtil.GOLD + " Quest Points.");

                                quester.saveData();

                                return true;

                            } catch (final NumberFormatException e) {

                                cs.sendMessage(ColorUtil.YELLOW + "Amount must be a number.");

                            }

                        }

                    } else {

                        cs.sendMessage(ColorUtil.RED + "You do not have access to that command.");

                    }
                } else if (args[0].equalsIgnoreCase("togglegui")) {

                    if (cs.hasPermission("quests.admin.togglegui")) {

                        try {

                            final int i = Integer.parseInt(args[1]);
                            if (citizens.getNPCRegistry().getById(i) == null) {
                                cs.sendMessage(ColorUtil.RED + "Error: There is no NPC with ID " + ColorUtil.PURPLE + i);
                            } else if (questNPCGUIs.contains(i)) {
                                questNPCGUIs.remove(questNPCGUIs.indexOf(i));
                                updateData();
                                cs.sendMessage(ColorUtil.PURPLE + citizens.getNPCRegistry().getById(i).getName()
                                        + ColorUtil.YELLOW + " will no longer provide a GUI Quest Display.");
                            } else {
                                questNPCGUIs.add(i);
                                updateData();
                                cs.sendMessage(ColorUtil.PURPLE + citizens.getNPCRegistry().getById(i).getName()
                                        + ColorUtil.YELLOW + " will now provide a GUI Quest Display.");
                            }

                        } catch (final NumberFormatException nfe) {
                            cs.sendMessage(ColorUtil.RED
                                    + "Input was not a number! Usage: /questadmin togglegui <npc id>");
                        } catch (final Exception ex) {
                            ex.printStackTrace();
                            cs.sendMessage(ColorUtil.RED + "An unknown error occurred. See console output.");
                        }

                    } else {

                        cs.sendMessage(ColorUtil.RED + "You do not have access to that command.");

                    }
                } else {

                    cs.sendMessage(ColorUtil.YELLOW + "Unknown Questadmin command. Type /questadmin for help.");

                }

            }

            return true;

        }

        return false;

    }

    private boolean extracted(final CommandSender cs) {
        cs.sendMessage(ColorUtil.RED + "You do not have permission to quit Quests.");
        return true;
    }

    public void printAdminHelp(CommandSender cs) {

        cs.sendMessage(ColorUtil.RED + "- " + ColorUtil.DARKRED + "Questadmin" + ColorUtil.RED + " -");
        cs.sendMessage("");
        cs.sendMessage(ColorUtil.DARKRED + "/questadmin" + ColorUtil.RED + " - View Questadmin help");
        if (cs.hasPermission("quests.admin.give")) {
            cs.sendMessage(ColorUtil.DARKRED + "/questadmin give <player> <quest>" + ColorUtil.RED
                    + " - Force a player to take a Quest");
        }
        if (cs.hasPermission("quests.admin.quit")) {
            cs.sendMessage(ColorUtil.DARKRED + "/questadmin quit <player>" + ColorUtil.RED
                    + " - Force a player to quit their Quest");
        }
        if (cs.hasPermission("quests.admin.points")) {
            cs.sendMessage(ColorUtil.DARKRED + "/questadmin points <player> <amount>" + ColorUtil.RED
                    + " - Set a players Quest Points");
        }
        if (cs.hasPermission("quests.admin.takepoints")) {
            cs.sendMessage(ColorUtil.DARKRED + "/questadmin takepoints <player> <amount>" + ColorUtil.RED
                    + " - Take a players Quest Points");
        }
        if (cs.hasPermission("quests.admin.givepoints")) {
            cs.sendMessage(ColorUtil.DARKRED + "/questadmin givepoints <player> <amount>" + ColorUtil.RED
                    + " - Give a player Quest Points");
        }
        if (cs.hasPermission("quests.admin.pointsall")) {
            cs.sendMessage(ColorUtil.DARKRED + "/questadmin pointsall <amount>" + ColorUtil.RED
                    + " - Set ALL players' Quest Points");
        }
        if (cs.hasPermission("quests.admin.finish")) {
            cs.sendMessage(ColorUtil.DARKRED + "/questadmin finish <player>" + ColorUtil.RED
                    + " - Immediately force Quest completion for a player");
        }
        if (cs.hasPermission("quests.admin.nextstage")) {
            cs.sendMessage(ColorUtil.DARKRED + "/questadmin nextstage <player>" + ColorUtil.RED
                    + " - Immediately force Stage completion for a player");
        }
        if (citizens != null && cs.hasPermission("quests.admin.togglegui")) {
            cs.sendMessage(ColorUtil.DARKRED + "/questadmin togglegui <npc id>" + ColorUtil.RED
                    + " - Toggle GUI Quest Display on an NPC");
        }
        if (cs.hasPermission("quests.admin.reload")) {
            cs.sendMessage(ColorUtil.DARKRED + "/questadmin reload" + ColorUtil.RED + " - Reload all Quests");
        }

    }

    public void listQuests(Player player, int page) {

        if (quests.size() < ((page * 8) - 7)) {
            player.sendMessage(ColorUtil.YELLOW + "Page does not exist.");
        } else {
            player.sendMessage(ColorUtil.GOLD + "- Quests -");

            int numOrder = (page - 1) * 8;

            if (numOrder == 0) {
                numOrder = 1;
            }

            List<Quest> subQuests;

            if (numOrder > 1) {
                if (quests.size() >= (numOrder + 7)) {
                    subQuests = quests.subList((numOrder), (numOrder + 8));
                } else {
                    subQuests = quests.subList((numOrder), quests.size());
                }
            } else if (quests.size() >= (numOrder + 7)) {
                subQuests = quests.subList((numOrder - 1), (numOrder + 7));
            } else {
                subQuests = quests.subList((numOrder - 1), quests.size());
            }

            if (numOrder != 1) {
                numOrder++;
            }

            for (final Quest quest: subQuests) {

                player.sendMessage(ColorUtil.YELLOW + Integer.toString(numOrder) + ". " + quest.name);
                numOrder++;

            }

            int numPages = quests.size() / 8;
            if ((quests.size() % 8) > 0 || numPages == 0) {
                numPages++;
            }

            player.sendMessage(ColorUtil.GOLD + "- Page " + page + " of " + numPages + " -");

        }

    }

    public void reloadQuests() {

        quests.clear();
        events.clear();
        loadQuests();
        loadEvents();
        loadConfig();

        for (final Quester quester: questers.values()) {
            quester.checkQuest();
        }

    }

    public Quester getQuester(String player) {

        Quester quester = null;

        if (questers.containsKey(player)) {
            quester = questers.get(player);
        }

        if (quester == null) {

            if (debug == true) {
                Quests.log.log(Level.WARNING, "[Quests] Quester data for player \"" + player
                        + "\" not stored. Attempting manual data retrieval..");
            }

            quester = new Quester(this);
            quester.name = player;
            if (quester.loadData() == false) {
                Quests.log.severe("[Quests] Quester not found for player \"" + player
                        + "\". Consider adding them to the Quester blacklist.");
            } else {
                if (debug == true) {
                    Quests.log
                            .log(Level.INFO, "[Quests] Manual data retrieval succeeded for player \"" + player + "\"");
                }
                questers.put(player, quester);
            }
        }

        return quester;

    }

    public Map<String, Quester> getOnlineQuesters() {

        final Map<String, Quester> qs = new HashMap<String, Quester>();

        for (final Player p: getServer().getOnlinePlayers()) {

            final Quester quester = new Quester(this);
            quester.name = p.getName();
            if (quester.loadData() == false) {
                quester.saveData();
            }
            qs.put(p.getName(), quester);

        }

        return qs;

    }

    public void loadQuests() {

        boolean failedToLoad = false;
        totalQuestPoints = 0;
        boolean needsSaving = false;

        final FileConfiguration config = new YamlConfiguration();
        final File file = new File(getDataFolder(), "quests.yml");

        try {

            config.load(file);

        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final InvalidConfigurationException e) {
            e.printStackTrace();
        }

        ConfigurationSection section1;
        if (config.contains("quests")) {
            section1 = config.getConfigurationSection("quests");
        } else {
            section1 = config.createSection("quests");
            needsSaving = true;
        }

        for (final String s: section1.getKeys(false)) {

            try {

                final Quest quest = new Quest();
                failedToLoad = false;

                if (config.contains("quests." + s + ".name")) {
                    quest.name = Quests.parseString(config.getString("quests." + s + ".name"), quest);
                } else {
                    Quests.printSevere("[Quests] Quest block \'" + s + "\' is missing " + ColorUtil.RED + "name:");
                    continue;
                }

                if (config.contains("quests." + s + ".npc-giver-id")) {

                    if (CitizensAPI.getNPCRegistry().getById(config.getInt("quests." + s + ".npc-giver-id")) != null) {

                        quest.npcStart = CitizensAPI.getNPCRegistry().getById(
                                config.getInt("quests." + s + ".npc-giver-id"));
                        questNPCs.add(CitizensAPI.getNPCRegistry().getById(
                                config.getInt("quests." + s + ".npc-giver-id")));

                    } else {
                        Quests.printSevere("[Quests] npc-giver-id: for Quest " + quest.name + " is not a valid NPC id!");
                        continue;
                    }

                }

                if (config.contains("quests." + s + ".block-start")) {

                    final Location location = Quests.getLocation(config.getString("quests." + s + ".block-start"));
                    if (location != null) {
                        quest.blockStart = location;
                    } else {
                        Quests.printSevere("[Quests] block-start: for Quest " + quest.name
                                + " is not in proper location format!");
                        Quests.printSevere("[Quests] Proper location format is: \"WorldName x y z\"");
                        continue;
                    }

                }

                if (config.contains("quests." + s + ".region")) {

                    final String region = config.getString("quests." + s + ".region");
                    boolean exists = false;
                    for (final World world: getServer().getWorlds()) {

                        final RegionManager rm = Quests.worldGuard.getRegionManager(world);
                        if (rm != null) {
                            final ProtectedRegion pr = rm.getRegionExact(region);
                            if (pr != null) {
                                quest.region = region;
                                exists = true;
                                break;
                            }
                        }

                    }

                    if (!exists) {
                        Quests.printSevere("[Quests] region: for Quest " + quest.name
                                + " is not a valid WorldGuard region!");
                        continue;
                    }

                }

                if (config.contains("quests." + s + ".guiDisplay")) {

                    final String item = config.getString("quests." + s + ".guiDisplay");
                    quest.guiDisplay = ItemUtil.readItemStack(item);

                }

                if (config.contains("quests." + s + ".redo-delay")) {

                    if (config.getInt("quests." + s + ".redo-delay", -999) != -999) {
                        quest.redoDelay = config.getInt("quests." + s + ".redo-delay");
                    } else {
                        Quests.printSevere("[Quests] redo-delay: for Quest " + quest.name + " is not a number!");
                        continue;
                    }

                }

                if (config.contains("quests." + s + ".finish-message")) {
                    quest.finished = Quests.parseString(config.getString("quests." + s + ".finish-message"), quest);
                } else {
                    Quests.printSevere("[Quests] Quest " + quest.name + " is missing finish-message:");
                    continue;
                }

                if (config.contains("quests." + s + ".ask-message")) {
                    quest.description = Quests.parseString(config.getString("quests." + s + ".ask-message"), quest);
                } else {
                    Quests.printSevere("[Quests] Quest " + quest.name + " is missing ask-message:");
                    continue;
                }

                if (config.contains("quests." + s + ".event")) {

                    final Event evt = Event.loadEvent(config.getString("quests." + s + ".event"), this);

                    if (evt != null) {
                        quest.initialEvent = evt;
                    } else {
                        Quests.printSevere("[Quests] Initial Event in Quest " + quest.name + " failed to load.");
                        continue;
                    }

                }

                if (config.contains("quests." + s + ".requirements")) {

                    if (config.contains("quests." + s + ".requirements.fail-requirement-message")) {
                        quest.failRequirements = Quests.parseString(
                                config.getString("quests." + s + ".requirements.fail-requirement-message"), quest);
                    } else {
                        Quests.printSevere("[Quests] Requirements for Quest " + quest.name
                                + " is missing fail-requirement-message:");
                        continue;
                    }

                    if (config.contains("quests." + s + ".requirements.items")) {

                        if (Quests.checkList(config.getList("quests." + s + ".requirements.items"), String.class)) {
                            final List<String> itemReqs = config.getStringList("quests." + s + ".requirements.items");
                            boolean failed = false;
                            for (final String item: itemReqs) {

                                final ItemStack stack = ItemUtil.readItemStack(item);
                                if (stack != null) {
                                    quest.items.add(stack);
                                } else {
                                    failed = true;
                                    break;
                                }

                            }

                            if (failed == true) {
                                Quests.printSevere("[Quests] items: Requirement for Quest " + quest.name
                                        + " is not formatted correctly!");
                                continue;
                            }

                        } else {
                            Quests.printSevere("[Quests] items: Requirement for Quest " + quest.name
                                    + " is not formatted correctly!");
                            continue;
                        }

                        if (config.contains("quests." + s + ".requirements.remove-items")) {

                            if (Quests.checkList(config.getList("quests." + s + ".requirements.remove-items"),
                                    Boolean.class)) {
                                quest.removeItems.clear();
                                quest.removeItems.addAll(config.getBooleanList("quests." + s
                                        + ".requirements.remove-items"));
                            } else {
                                Quests.printSevere("[Quests] remove-items: Requirement for Quest " + quest.name
                                        + " is not a list of true/false values!");
                                continue;
                            }

                        } else {
                            Quests.printSevere("[Quests] Requirements for Quest " + quest.name
                                    + " is missing remove-items:");
                            continue;
                        }
                    }

                    if (config.contains("quests." + s + ".requirements.money")) {

                        if (config.getInt("quests." + s + ".requirements.money", -999) != -999) {
                            quest.moneyReq = config.getInt("quests." + s + ".requirements.money");
                        } else {
                            Quests.printSevere("[Quests] money: Requirement for Quest " + quest.name
                                    + " is not a number!");
                            continue;
                        }

                    }

                    if (config.contains("quests." + s + ".requirements.quest-points")) {

                        if (config.getInt("quests." + s + ".requirements.quest-points", -999) != -999) {
                            quest.questPointsReq = config.getInt("quests." + s + ".requirements.quest-points");
                        } else {
                            Quests.printSevere("[Quests] quest-points: Requirement for Quest " + quest.name
                                    + " is not a number!");
                            continue;
                        }

                    }

                    if (config.contains("quests." + s + ".requirements.quest-blocks")) {

                        if (Quests
                                .checkList(config.getList("quests." + s + ".requirements.quest-blocks"), String.class)) {

                            final List<String> names = config.getStringList("quests." + s
                                    + ".requirements.quest-blocks");

                            boolean failed = false;
                            String failedQuest = "NULL";

                            for (final String name: names) {

                                boolean done = false;
                                for (final String string: section1.getKeys(false)) {

                                    if (config.getString("quests." + string + ".name").equalsIgnoreCase(name)) {
                                        quest.blockQuests.add(name);
                                        done = true;
                                        break;
                                    }

                                }

                                if (!done) {
                                    failed = true;
                                    failedQuest = name;
                                    break;
                                }

                            }

                            if (failed) {
                                Quests.printSevere("[Quests] " + ColorUtil.PINK + failedQuest
                                        + " inside quests: Requirement for Quest " + quest.name
                                        + " is not a valid Quest name!");
                                Quests.printSevere("Make sure you are using the Quest name: value, and not the block name.");
                                continue;
                            }

                        } else {
                            Quests.printSevere("[Quests] quest-blocks: Requirement for Quest " + quest.name
                                    + " is not a list of Quest names!");
                            continue;
                        }

                    }

                    if (config.contains("quests." + s + ".requirements.quests")) {

                        if (Quests.checkList(config.getList("quests." + s + ".requirements.quests"), String.class)) {

                            final List<String> names = config.getStringList("quests." + s + ".requirements.quests");

                            boolean failed = false;
                            String failedQuest = "NULL";

                            for (final String name: names) {

                                boolean done = false;
                                for (final String string: section1.getKeys(false)) {

                                    if (config.getString("quests." + string + ".name").equalsIgnoreCase(name)) {
                                        quest.neededQuests.add(name);
                                        done = true;
                                        break;
                                    }

                                }

                                if (!done) {
                                    failed = true;
                                    failedQuest = name;
                                    break;
                                }

                            }

                            if (failed) {
                                Quests.printSevere("[Quests] " + failedQuest + " inside quests: Requirement for Quest "
                                        + quest.name + " is not a valid Quest name!");
                                Quests.printSevere("Make sure you are using the Quest name: value, and not the block name.");
                                continue;
                            }

                        } else {
                            Quests.printSevere("[Quests] quests: Requirement for Quest " + quest.name
                                    + " is not a list of Quest names!");
                            continue;
                        }

                    }

                    if (config.contains("quests." + s + ".requirements.permissions")) {

                        if (Quests.checkList(config.getList("quests." + s + ".requirements.permissions"), String.class)) {
                            quest.permissionReqs.clear();
                            quest.permissionReqs.addAll(config.getStringList("quests." + s
                                    + ".requirements.permissions"));
                        } else {
                            Quests.printSevere("[Quests] permissions: Requirement for Quest " + quest.name
                                    + " is not a list of permissions!");
                            continue;
                        }

                    }

                    if (config.contains("quests." + s + ".requirements.mcmmo-skills")) {

                        if (Quests
                                .checkList(config.getList("quests." + s + ".requirements.mcmmo-skills"), String.class)) {

                            if (config.contains("quests." + s + ".requirements.mcmmo-amounts")) {

                                if (Quests.checkList(config.getList("quests." + s + ".requirements.mcmmo-amounts"),
                                        Integer.class)) {

                                    final List<String> skills = config.getStringList("quests." + s
                                            + ".requirements.mcmmo-skills");
                                    final List<Integer> amounts = config.getIntegerList("quests." + s
                                            + ".requirements.mcmmo-amounts");

                                    if (skills.size() != amounts.size()) {
                                        Quests.printSevere("[Quests] mcmmo-skills: and mcmmo-amounts: in requirements: for Quest "
                                                + quest.name + " are not the same size!");
                                        continue;
                                    }

                                    quest.mcMMOSkillReqs.addAll(skills);
                                    quest.mcMMOAmountReqs.addAll(amounts);

                                } else {
                                    Quests.printSevere("[Quests] mcmmo-amounts: Requirement for Quest " + quest.name
                                            + " is not a list of numbers!");
                                    continue;
                                }

                            } else {
                                Quests.printSevere("[Quests] Requirements for Quest " + quest.name
                                        + " is missing mcmmo-amounts:");
                                continue;
                            }

                        } else {
                            Quests.printSevere("[Quests] mcmmo-skills: Requirement for Quest " + quest.name
                                    + " is not a list of skills!");
                            continue;
                        }

                    }

                    if (config.contains("quests." + s + ".requirements.heroes-primary-class")) {

                        final String className = config.getString("quests." + s + ".requirements.heroes-primary-class");
                        final HeroClass hc = Quests.heroes.getClassManager().getClass(className);
                        if (hc != null && hc.isPrimary()) {
                            quest.heroesPrimaryClassReq = hc.getName();
                        } else if (hc != null) {
                            Quests.printSevere("[Quests] heroes-primary-class: Requirement for Quest " + quest.name
                                    + " is not a primary Heroes class!");
                            continue;
                        } else {
                            Quests.printSevere("[Quests] heroes-primary-class: Requirement for Quest " + quest.name
                                    + " is not a valid Heroes class!");
                            continue;
                        }

                    }

                    if (config.contains("quests." + s + ".requirements.heroes-secondary-class")) {

                        final String className = config.getString("quests." + s
                                + ".requirements.heroes-secondary-class");
                        final HeroClass hc = Quests.heroes.getClassManager().getClass(className);
                        if (hc != null && hc.isSecondary()) {
                            quest.heroesSecondaryClassReq = hc.getName();
                        } else if (hc != null) {
                            Quests.printSevere("[Quests] heroes-secondary-class: Requirement for Quest " + quest.name
                                    + " is not a secondary Heroes class!");
                            continue;
                        } else {
                            Quests.printSevere("[Quests] heroes-secondary-class: Requirement for Quest " + quest.name
                                    + " is not a valid Heroes class!");
                            continue;
                        }

                    }

                    if (config.contains("quests." + s + ".requirements.custom-requirements")) {

                        final ConfigurationSection sec = config.getConfigurationSection("quests." + s
                                + ".requirements.custom-requirements");
                        for (final String path: sec.getKeys(false)) {

                            final String name = sec.getString(path + ".name");
                            boolean found = false;

                            for (final CustomRequirement cr: customRequirements) {
                                if (cr.getName().equalsIgnoreCase(name)) {
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) {
                                Quests.printWarning("[Quests] Custom requirement \"" + name + "\" for Quest \""
                                        + quest.name + "\" could not be found!");
                                continue;
                            }

                            final Map<String, Object> data = new HashMap<String, Object>();
                            final ConfigurationSection sec2 = sec.getConfigurationSection(path + ".data");
                            if (sec2 != null) {
                                for (final String dataPath: sec2.getKeys(false)) {
                                    data.put(dataPath, sec2.get(dataPath));
                                }
                            }

                            quest.customRequirements.put(name, data);

                        }

                    }

                }

                quest.plugin = this;

                final ConfigurationSection section2 = config.getConfigurationSection("quests." + s + ".stages.ordered");

                int index = 1;
                boolean stageFailed = false;

                for (final String s2: section2.getKeys(false)) {

                    final Stage oStage = new Stage();

                    final LinkedList<EntityType> mobsToKill = new LinkedList<EntityType>();
                    final LinkedList<Integer> mobNumToKill = new LinkedList<Integer>();
                    final LinkedList<Location> locationsToKillWithin = new LinkedList<Location>();
                    final LinkedList<Integer> radiiToKillWithin = new LinkedList<Integer>();
                    final LinkedList<String> areaNames = new LinkedList<String>();

                    final LinkedList<Enchantment> enchantments = new LinkedList<Enchantment>();
                    final LinkedList<Material> itemsToEnchant = new LinkedList<Material>();
                    List<Integer> amountsToEnchant = new LinkedList<Integer>();

                    List<Integer> breakids = new LinkedList<Integer>();
                    List<Integer> breakamounts = new LinkedList<Integer>();

                    List<Integer> damageids = new LinkedList<Integer>();
                    List<Integer> damageamounts = new LinkedList<Integer>();

                    List<Integer> placeids = new LinkedList<Integer>();
                    List<Integer> placeamounts = new LinkedList<Integer>();

                    List<Integer> useids = new LinkedList<Integer>();
                    List<Integer> useamounts = new LinkedList<Integer>();

                    List<Integer> cutids = new LinkedList<Integer>();
                    List<Integer> cutamounts = new LinkedList<Integer>();

                    // Denizen script load
                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".script-to-run")) {

                        if (ScriptRegistry.containsScript(config.getString("quests." + s + ".stages.ordered." + s2
                                + ".script-to-run"))) {
                            trigger = new QuestTaskTrigger();
                            oStage.script = config
                                    .getString("quests." + s + ".stages.ordered." + s2 + ".script-to-run");
                        } else {
                            Quests.printSevere("[Quests] script-to-run: in Stage " + s2 + " of Quest " + quest.name
                                    + " is not a Denizen script!");
                            stageFailed = true;
                            break;
                        }

                    }

                    //
                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".break-block-ids")) {

                        if (Quests.checkList(
                                config.getList("quests." + s + ".stages.ordered." + s2 + ".break-block-ids"),
                                Integer.class)) {
                            breakids = config.getIntegerList("quests." + s + ".stages.ordered." + s2
                                    + ".break-block-ids");
                        } else {
                            Quests.printSevere("[Quests] break-block-ids: in Stage " + s2 + " of Quest " + quest.name
                                    + " is not a list of numbers!");
                            stageFailed = true;
                            break;
                        }

                        if (config.contains("quests." + s + ".stages.ordered." + s2 + ".break-block-amounts")) {

                            if (Quests.checkList(
                                    config.getList("quests." + s + ".stages.ordered." + s2 + ".break-block-amounts"),
                                    Integer.class)) {
                                breakamounts = config.getIntegerList("quests." + s + ".stages.ordered." + s2
                                        + ".break-block-amounts");
                            } else {
                                Quests.printSevere("[Quests] break-block-amounts: in Stage " + s2 + " of Quest "
                                        + quest.name + " is not a list of numbers!");
                                stageFailed = true;
                                break;
                            }

                        } else {
                            Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                    + " is missing break-block-amounts:");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".damage-block-ids")) {

                        if (Quests.checkList(
                                config.getList("quests." + s + ".stages.ordered." + s2 + ".damage-block-ids"),
                                Integer.class)) {
                            damageids = config.getIntegerList("quests." + s + ".stages.ordered." + s2
                                    + ".damage-block-ids");
                        } else {
                            Quests.printSevere("[Quests] damage-block-ids: in Stage " + s2 + " of Quest " + quest.name
                                    + " is not a list of numbers!");
                            stageFailed = true;
                            break;
                        }

                        if (config.contains("quests." + s + ".stages.ordered." + s2 + ".damage-block-amounts")) {

                            if (Quests.checkList(
                                    config.getList("quests." + s + ".stages.ordered." + s2 + ".damage-block-amounts"),
                                    Integer.class)) {
                                damageamounts = config.getIntegerList("quests." + s + ".stages.ordered." + s2
                                        + ".damage-block-amounts");
                            } else {
                                Quests.printSevere("[Quests] damage-block-amounts: in Stage " + s2 + " of Quest "
                                        + quest.name + " is not a list of numbers!");
                                stageFailed = true;
                                break;
                            }

                        } else {
                            Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                    + " is missing damage-block-amounts:");
                            stageFailed = true;
                            break;
                        }

                    }

                    for (final int i: damageids) {

                        if (Material.getMaterial(i) != null) {
                            oStage.blocksToDamage.put(Material.getMaterial(i), damageamounts.get(damageids.indexOf(i)));
                        } else {
                            Quests.printSevere("[Quests] " + i + " inside damage-block-ids: inside Stage " + s2
                                    + " of Quest " + quest.name + " is not a valid item ID!");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".place-block-ids")) {

                        if (Quests.checkList(
                                config.getList("quests." + s + ".stages.ordered." + s2 + ".place-block-ids"),
                                Integer.class)) {
                            placeids = config.getIntegerList("quests." + s + ".stages.ordered." + s2
                                    + ".place-block-ids");
                        } else {
                            Quests.printSevere("[Quests] place-block-ids: in Stage " + s2 + " of Quest " + quest.name
                                    + " is not a list of numbers!");
                            stageFailed = true;
                            break;
                        }

                        if (config.contains("quests." + s + ".stages.ordered." + s2 + ".place-block-amounts")) {

                            if (Quests.checkList(
                                    config.getList("quests." + s + ".stages.ordered." + s2 + ".place-block-amounts"),
                                    Integer.class)) {
                                placeamounts = config.getIntegerList("quests." + s + ".stages.ordered." + s2
                                        + ".place-block-amounts");
                            } else {
                                Quests.printSevere("[Quests] place-block-amounts: in Stage " + s2 + " of Quest "
                                        + quest.name + " is not a list of numbers!");
                                stageFailed = true;
                                break;
                            }

                        } else {
                            Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                    + " is missing place-block-amounts:");
                            stageFailed = true;
                            break;
                        }

                    }

                    for (final int i: placeids) {

                        if (Material.getMaterial(i) != null) {
                            oStage.blocksToPlace.put(Material.getMaterial(i), placeamounts.get(placeids.indexOf(i)));
                        } else {
                            Quests.printSevere("[Quests] " + +i + " inside place-block-ids: inside Stage " + s2
                                    + " of Quest " + quest.name + " is not a valid item ID!");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".use-block-ids")) {

                        if (Quests.checkList(
                                config.getList("quests." + s + ".stages.ordered." + s2 + ".use-block-ids"),
                                Integer.class)) {
                            useids = config.getIntegerList("quests." + s + ".stages.ordered." + s2 + ".use-block-ids");
                        } else {
                            Quests.printSevere("[Quests] use-block-ids: in Stage " + s2 + " of Quest " + quest.name
                                    + " is not a list of numbers!");
                            stageFailed = true;
                            break;
                        }

                        if (config.contains("quests." + s + ".stages.ordered." + s2 + ".use-block-amounts")) {

                            if (Quests.checkList(
                                    config.getList("quests." + s + ".stages.ordered." + s2 + ".use-block-amounts"),
                                    Integer.class)) {
                                useamounts = config.getIntegerList("quests." + s + ".stages.ordered." + s2
                                        + ".use-block-amounts");
                            } else {
                                Quests.printSevere("[Quests] use-block-amounts: in Stage " + s2 + " of Quest "
                                        + quest.name + " is not a list of numbers!");
                                stageFailed = true;
                                break;
                            }

                        } else {
                            Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                    + " is missing use-block-amounts:");
                            stageFailed = true;
                            break;
                        }

                    }

                    for (final int i: useids) {

                        if (Material.getMaterial(i) != null) {
                            oStage.blocksToUse.put(Material.getMaterial(i), useamounts.get(useids.indexOf(i)));
                        } else {
                            Quests.printSevere("[Quests] " + ColorUtil.RED + i + " inside use-block-ids: inside Stage "
                                    + s2 + " of Quest " + quest.name + " is not a valid item ID!");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".cut-block-ids")) {

                        if (Quests.checkList(
                                config.getList("quests." + s + ".stages.ordered." + s2 + ".cut-block-ids"),
                                Integer.class)) {
                            cutids = config.getIntegerList("quests." + s + ".stages.ordered." + s2 + ".cut-block-ids");
                        } else {
                            Quests.printSevere("[Quests] cut-block-ids: in Stage " + s2 + " of Quest " + quest.name
                                    + " is not a list of numbers!");
                            stageFailed = true;
                            break;
                        }

                        if (config.contains("quests." + s + ".stages.ordered." + s2 + ".cut-block-amounts")) {

                            if (Quests.checkList(
                                    config.getList("quests." + s + ".stages.ordered." + s2 + ".cut-block-amounts"),
                                    Integer.class)) {
                                cutamounts = config.getIntegerList("quests." + s + ".stages.ordered." + s2
                                        + ".cut-block-amounts");
                            } else {
                                Quests.printSevere("[Quests] cut-block-amounts: in Stage " + s2 + " of Quest "
                                        + quest.name + " is not a list of numbers!");
                                stageFailed = true;
                                break;
                            }

                        } else {
                            Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                    + " is missing cut-block-amounts:");
                            stageFailed = true;
                            break;
                        }

                    }

                    for (final int i: cutids) {

                        if (Material.getMaterial(i) != null) {
                            oStage.blocksToCut.put(Material.getMaterial(i), cutamounts.get(cutids.indexOf(i)));
                        } else {
                            Quests.printSevere("[Quests] " + i + " inside cut-block-ids: inside Stage " + s2
                                    + " of Quest " + quest.name + " is not a valid item ID!");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".fish-to-catch")) {

                        if (config.getInt("quests." + s + ".stages.ordered." + s2 + ".fish-to-catch", -999) != -999) {
                            oStage.fishToCatch = config.getInt("quests." + s + ".stages.ordered." + s2
                                    + ".fish-to-catch");
                        } else {
                            Quests.printSevere("[Quests] fish-to-catch: inside Stage " + s2 + " of Quest " + quest.name
                                    + " is not a number!");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".players-to-kill")) {

                        if (config.getInt("quests." + s + ".stages.ordered." + s2 + ".players-to-kill", -999) != -999) {
                            oStage.playersToKill = config.getInt("quests." + s + ".stages.ordered." + s2
                                    + ".players-to-kill");
                        } else {
                            Quests.printSevere("[Quests] players-to-kill: inside Stage " + s2 + " of Quest "
                                    + quest.name + " is not a number!");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".enchantments")) {

                        if (Quests.checkList(config.getList("quests." + s + ".stages.ordered." + s2 + ".enchantments"),
                                String.class)) {

                            for (final String enchant: config.getStringList("quests." + s + ".stages.ordered." + s2
                                    + ".enchantments")) {

                                final Enchantment e = Quests.getEnchantment(enchant);

                                if (e != null) {

                                    enchantments.add(e);

                                } else {

                                    Quests.printSevere("[Quests] " + enchant + " inside enchantments: inside Stage "
                                            + s2 + " of Quest " + quest.name + " is not a valid enchantment!");
                                    stageFailed = true;
                                    break;

                                }

                            }

                        } else {
                            Quests.printSevere("[Quests] enchantments: in Stage " + s2 + " of Quest " + quest.name
                                    + " is not a list of enchantment names!");
                            stageFailed = true;
                            break;
                        }

                        if (config.contains("quests." + s + ".stages.ordered." + s2 + ".enchantment-item-ids")) {

                            if (Quests.checkList(
                                    config.getList("quests." + s + ".stages.ordered." + s2 + ".enchantment-item-ids"),
                                    Integer.class)) {

                                for (final int item: config.getIntegerList("quests." + s + ".stages.ordered." + s2
                                        + ".enchantment-item-ids")) {

                                    if (Material.getMaterial(item) != null) {
                                        itemsToEnchant.add(Material.getMaterial(item));
                                    } else {
                                        Quests.printSevere("[Quests] " + item
                                                + " inside enchantment-item-ids: inside Stage " + s2 + " of Quest "
                                                + quest.name + " is not a valid item id!");
                                        stageFailed = true;
                                        break;
                                    }

                                }

                            } else {

                                Quests.printSevere("[Quests] enchantment-item-ids: in Stage " + s2 + " of Quest "
                                        + quest.name + " is not a list of numbers!");
                                stageFailed = true;
                                break;

                            }

                        } else {
                            Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                    + " is missing enchantment-item-ids:");
                            stageFailed = true;
                            break;
                        }

                        if (config.contains("quests." + s + ".stages.ordered." + s2 + ".enchantment-amounts")) {

                            if (Quests.checkList(
                                    config.getList("quests." + s + ".stages.ordered." + s2 + ".enchantment-amounts"),
                                    Integer.class)) {

                                amountsToEnchant = config.getIntegerList("quests." + s + ".stages.ordered." + s2
                                        + ".enchantment-amounts");

                            } else {

                                Quests.printSevere("[Quests] enchantment-amounts: in Stage " + s2 + " of Quest "
                                        + quest.name + " is not a list of numbers!");
                                stageFailed = true;
                                break;

                            }

                        } else {
                            Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                    + " is missing enchantment-amounts:");
                            stageFailed = true;
                            break;
                        }

                    }

                    List<Integer> npcIdsToTalkTo = null;

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".npc-ids-to-talk-to")) {

                        if (Quests.checkList(
                                config.getList("quests." + s + ".stages.ordered." + s2 + ".npc-ids-to-talk-to"),
                                Integer.class)) {

                            npcIdsToTalkTo = config.getIntegerList("quests." + s + ".stages.ordered." + s2
                                    + ".npc-ids-to-talk-to");
                            for (final int i: npcIdsToTalkTo) {

                                if (CitizensAPI.getNPCRegistry().getById(i) != null) {

                                    questNPCs.add(CitizensAPI.getNPCRegistry().getById(i));

                                } else {
                                    Quests.printSevere("[Quests] " + i + " inside npc-ids-to-talk-to: inside Stage "
                                            + s2 + " of Quest " + quest.name + " is not a valid NPC id!");
                                    stageFailed = true;
                                    break;
                                }

                            }

                        } else {
                            Quests.printSevere("[Quests] npc-ids-to-talk-to: in Stage " + s2 + " of Quest "
                                    + quest.name + " is not a list of numbers!");
                            stageFailed = true;
                            break;
                        }

                    }

                    List<String> itemsToDeliver;
                    List<Integer> itemDeliveryTargetIds;
                    final LinkedList<String> deliveryMessages = new LinkedList<String>();

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".items-to-deliver")) {

                        if (Quests.checkList(
                                config.getList("quests." + s + ".stages.ordered." + s2 + ".items-to-deliver"),
                                String.class)) {

                            if (config.contains("quests." + s + ".stages.ordered." + s2 + ".npc-delivery-ids")) {

                                if (Quests.checkList(
                                        config.getList("quests." + s + ".stages.ordered." + s2 + ".npc-delivery-ids"),
                                        Integer.class)) {

                                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".delivery-messages")) {

                                        itemsToDeliver = config.getStringList("quests." + s + ".stages.ordered." + s2
                                                + ".items-to-deliver");
                                        itemDeliveryTargetIds = config.getIntegerList("quests." + s
                                                + ".stages.ordered." + s2 + ".npc-delivery-ids");
                                        deliveryMessages.addAll(config.getStringList("quests." + s + ".stages.ordered."
                                                + s2 + ".delivery-messages"));

                                        for (final String item: itemsToDeliver) {

                                            final ItemStack is = ItemUtil.readItemStack(item);

                                            if (is != null) {

                                                final int npcId = itemDeliveryTargetIds.get(itemsToDeliver
                                                        .indexOf(item));
                                                final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);

                                                if (npc != null) {

                                                    oStage.itemsToDeliver.add(is);
                                                    oStage.itemDeliveryTargets.add(npcId);
                                                    oStage.deliverMessages = deliveryMessages;

                                                } else {
                                                    Quests.printSevere("[Quests] " + npcId
                                                            + " inside npc-delivery-ids: inside Stage " + s2
                                                            + " of Quest " + quest.name + " is not a valid NPC id!");
                                                    stageFailed = true;
                                                    break;
                                                }

                                            } else {
                                                Quests.printSevere("[Quests] " + item
                                                        + " inside items-to-deliver: inside Stage " + s2 + " of Quest "
                                                        + quest.name + " is not formatted properly!");
                                                stageFailed = true;
                                                break;
                                            }

                                        }

                                    } else {
                                        Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                                + " is missing delivery-messages:");
                                        stageFailed = true;
                                        break;
                                    }

                                } else {
                                    Quests.printSevere("[Quests] npc-delivery-ids: in Stage " + s2 + " of Quest "
                                            + ColorUtil.PURPLE + quest.name + " is not a list of NPC ids!");
                                    stageFailed = true;
                                    break;
                                }

                            } else {
                                Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                        + " is missing npc-delivery-ids:");
                                stageFailed = true;
                                break;
                            }

                        } else {
                            Quests.printSevere("[Quests] items-to-deliver: in Stage " + s2 + " of Quest " + quest.name
                                    + " is not formatted properly!");
                            stageFailed = true;
                            break;
                        }

                    }

                    List<Integer> npcIds;
                    List<Integer> npcAmounts;

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".npc-ids-to-kill")) {

                        if (Quests.checkList(
                                config.getList("quests." + s + ".stages.ordered." + s2 + ".npc-ids-to-kill"),
                                Integer.class)) {

                            if (config.contains("quests." + s + ".stages.ordered." + s2 + ".npc-kill-amounts")) {

                                if (Quests.checkList(
                                        config.getList("quests." + s + ".stages.ordered." + s2 + ".npc-kill-amounts"),
                                        Integer.class)) {

                                    npcIds = config.getIntegerList("quests." + s + ".stages.ordered." + s2
                                            + ".npc-ids-to-kill");
                                    npcAmounts = config.getIntegerList("quests." + s + ".stages.ordered." + s2
                                            + ".npc-kill-amounts");
                                    for (final int i: npcIds) {

                                        if (CitizensAPI.getNPCRegistry().getById(i) != null) {

                                            if (npcAmounts.get(npcIds.indexOf(i)) > 0) {
                                                oStage.citizensToKill.add(i);
                                                oStage.citizenNumToKill.add(npcAmounts.get(npcIds.indexOf(i)));
                                                questNPCs.add(CitizensAPI.getNPCRegistry().getById(i));
                                            } else {
                                                Quests.printSevere("[Quests] " + npcAmounts.get(npcIds.indexOf(i))
                                                        + " inside npc-kill-amounts: inside Stage " + s2 + " of Quest "
                                                        + quest.name + " is not a positive number!");
                                                stageFailed = true;
                                                break;
                                            }

                                        } else {
                                            Quests.printSevere("[Quests] " + i
                                                    + " inside npc-ids-to-kill: inside Stage " + s2 + " of Quest "
                                                    + quest.name + " is not a valid NPC id!");
                                            stageFailed = true;
                                            break;
                                        }

                                    }

                                } else {
                                    Quests.printSevere("[Quests] npc-kill-amounts: in Stage " + s2 + " of Quest "
                                            + quest.name + " is not a list of numbers!");
                                    stageFailed = true;
                                    break;
                                }

                            } else {
                                Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                        + " is missing npc-kill-amounts:");
                                stageFailed = true;
                                break;
                            }

                        } else {
                            Quests.printSevere("[Quests] npc-ids-to-kill: in Stage " + s2 + " of Quest " + quest.name
                                    + " is not a list of numbers!");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".mobs-to-kill")) {

                        if (Quests.checkList(config.getList("quests." + s + ".stages.ordered." + s2 + ".mobs-to-kill"),
                                String.class)) {

                            final List<String> mobNames = config.getStringList("quests." + s + ".stages.ordered." + s2
                                    + ".mobs-to-kill");
                            for (final String mob: mobNames) {

                                final EntityType type = Quests.getMobType(mob);

                                if (type != null) {

                                    mobsToKill.add(type);

                                } else {

                                    Quests.printSevere("[Quests] " + mob + " inside mobs-to-kill: inside Stage " + s2
                                            + " of Quest " + quest.name + " is not a valid mob name!");
                                    stageFailed = true;
                                    break;

                                }

                            }

                        } else {
                            Quests.printSevere("[Quests] mobs-to-kill: in Stage " + s2 + " of Quest " + quest.name
                                    + " is not a list of mob names!");
                            stageFailed = true;
                            break;
                        }

                        if (config.contains("quests." + s + ".stages.ordered." + s2 + ".mob-amounts")) {

                            if (Quests.checkList(
                                    config.getList("quests." + s + ".stages.ordered." + s2 + ".mob-amounts"),
                                    Integer.class)) {

                                for (final int i: config.getIntegerList("quests." + s + ".stages.ordered." + s2
                                        + ".mob-amounts")) {

                                    mobNumToKill.add(i);

                                }

                            } else {

                                Quests.printSevere("[Quests] mob-amounts: in Stage " + s2 + " of Quest " + quest.name
                                        + " is not a list of numbers!");
                                stageFailed = true;
                                break;

                            }

                        } else {
                            Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + ColorUtil.PURPLE + quest.name
                                    + " is missing mob-amounts:");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".locations-to-kill")) {

                        if (Quests.checkList(
                                config.getList("quests." + s + ".stages.ordered." + s2 + ".locations-to-kill"),
                                String.class)) {

                            final List<String> locations = config.getStringList("quests." + s + ".stages.ordered." + s2
                                    + ".locations-to-kill");

                            for (final String loc: locations) {

                                final String[] info = loc.split(" ");
                                if (info.length == 4) {
                                    double x;
                                    double y;
                                    double z;
                                    try {
                                        x = Double.parseDouble(info[1]);
                                        y = Double.parseDouble(info[2]);
                                        z = Double.parseDouble(info[3]);
                                    } catch (final NumberFormatException e) {
                                        Quests.printSevere("[Quests] " + loc + " inside mobs-to-kill: inside Stage "
                                                + s2 + " of Quest " + quest.name + " is not in proper location format!");
                                        Quests.printSevere("[Quests] Proper location format is: \"WorldName x y z\"");
                                        stageFailed = true;
                                        break;
                                    }

                                    if (getServer().getWorld(info[0]) != null) {
                                        final Location finalLocation = new Location(getServer().getWorld(info[0]), x,
                                                y, z);
                                        locationsToKillWithin.add(finalLocation);
                                    } else {
                                        Quests.printSevere("[Quests] " + info[0]
                                                + " inside mobs-to-kill: inside Stage " + s2 + " of Quest "
                                                + quest.name + " is not a valid world name!");
                                        stageFailed = true;
                                        break;
                                    }

                                } else {
                                    Quests.printSevere("[Quests] " + loc + " inside mobs-to-kill: inside Stage " + s2
                                            + " of Quest " + quest.name + " is not in proper location format!");
                                    Quests.printSevere("[Quests] Proper location format is: \"WorldName x y z\"");
                                    stageFailed = true;
                                    break;
                                }

                            }

                        } else {
                            Quests.printSevere("[Quests] locations-to-kill: in Stage " + s2 + " of Quest " + quest.name
                                    + " is not a list of locations!");
                            stageFailed = true;
                            break;
                        }

                        if (config.contains("quests." + s + ".stages.ordered." + s2 + ".kill-location-radii")) {

                            if (Quests.checkList(
                                    config.getList("quests." + s + ".stages.ordered." + s2 + ".kill-location-radii"),
                                    Integer.class)) {

                                final List<Integer> radii = config.getIntegerList("quests." + s + ".stages.ordered."
                                        + s2 + ".kill-location-radii");
                                for (final int i: radii) {

                                    radiiToKillWithin.add(i);

                                }

                            } else {
                                Quests.printSevere("[Quests] kill-location-radii: in Stage " + s2 + " of Quest "
                                        + quest.name + " is not a list of numbers!");
                                stageFailed = true;
                                break;
                            }

                        } else {
                            Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                    + " is missing kill-location-radii:");
                            stageFailed = true;
                            break;
                        }

                        if (config.contains("quests." + s + ".stages.ordered." + s2 + ".kill-location-names")) {

                            if (Quests.checkList(
                                    config.getList("quests." + s + ".stages.ordered." + s2 + ".kill-location-names"),
                                    String.class)) {

                                final List<String> locationNames = config.getStringList("quests." + s
                                        + ".stages.ordered." + s2 + ".kill-location-names");
                                for (final String name: locationNames) {

                                    areaNames.add(name);

                                }

                            } else {
                                Quests.printSevere("[Quests] kill-location-names: in Stage " + s2 + " of Quest "
                                        + quest.name + " is not a list of names!");
                                stageFailed = true;
                                break;
                            }

                        } else {
                            Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                    + " is missing kill-location-names:");
                            stageFailed = true;
                            break;
                        }

                    }

                    oStage.mobsToKill = mobsToKill;
                    oStage.mobNumToKill = mobNumToKill;
                    oStage.locationsToKillWithin = locationsToKillWithin;
                    oStage.radiiToKillWithin = radiiToKillWithin;
                    oStage.areaNames = areaNames;

                    final Map<Map<Enchantment, Material>, Integer> enchants = new HashMap<Map<Enchantment, Material>, Integer>();

                    for (final Enchantment e: enchantments) {

                        final Map<Enchantment, Material> map = new HashMap<Enchantment, Material>();
                        map.put(e, itemsToEnchant.get(enchantments.indexOf(e)));
                        enchants.put(map, amountsToEnchant.get(enchantments.indexOf(e)));

                    }

                    oStage.itemsToEnchant = enchants;

                    final Map<Material, Integer> breakMap = new EnumMap<Material, Integer>(Material.class);

                    for (final int i: breakids) {

                        breakMap.put(Material.getMaterial(i), breakamounts.get(breakids.indexOf(i)));

                    }

                    oStage.blocksToBreak = breakMap;

                    if (index < section2.getKeys(false).size()) {
                        index++;
                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".locations-to-reach")) {

                        if (Quests.checkList(
                                config.getList("quests." + s + ".stages.ordered." + s2 + ".locations-to-reach"),
                                String.class)) {

                            final List<String> locations = config.getStringList("quests." + s + ".stages.ordered." + s2
                                    + ".locations-to-reach");

                            for (final String loc: locations) {

                                final String[] info = loc.split(" ");
                                if (info.length == 4) {
                                    double x;
                                    double y;
                                    double z;
                                    try {
                                        x = Double.parseDouble(info[1]);
                                        y = Double.parseDouble(info[2]);
                                        z = Double.parseDouble(info[3]);
                                    } catch (final NumberFormatException e) {
                                        Quests.printSevere("[Quests] " + loc
                                                + " inside locations-to-reach: inside Stage " + s2 + " of Quest "
                                                + quest.name + " is not in proper location format!");
                                        Quests.printSevere("[Quests] Proper location format is: \"WorldName x y z\"");
                                        stageFailed = true;
                                        break;
                                    }

                                    if (getServer().getWorld(info[0]) != null) {
                                        final Location finalLocation = new Location(getServer().getWorld(info[0]), x,
                                                y, z);
                                        oStage.locationsToReach.add(finalLocation);
                                    } else {
                                        Quests.printSevere("[Quests] " + info[0]
                                                + " inside locations-to-reach: inside Stage " + s2 + " of Quest "
                                                + quest.name + " is not a valid world name!");
                                        stageFailed = true;
                                        break;
                                    }

                                } else {
                                    Quests.printSevere("[Quests] " + loc + " inside mobs-to-kill: inside Stage " + s2
                                            + " of Quest " + quest.name + " is not in proper location format!");
                                    Quests.printSevere("[Quests] Proper location format is: \"WorldName x y z\"");
                                    stageFailed = true;
                                    break;
                                }

                            }

                        } else {
                            Quests.printSevere("[Quests] locations-to-reach: in Stage " + s2 + " of Quest "
                                    + quest.name + " is not a list of locations!");
                            stageFailed = true;
                            break;
                        }

                        if (config.contains("quests." + s + ".stages.ordered." + s2 + ".reach-location-radii")) {

                            if (Quests.checkList(
                                    config.getList("quests." + s + ".stages.ordered." + s2 + ".reach-location-radii"),
                                    Integer.class)) {

                                final List<Integer> radii = config.getIntegerList("quests." + s + ".stages.ordered."
                                        + s2 + ".reach-location-radii");
                                for (final int i: radii) {

                                    oStage.radiiToReachWithin.add(i);

                                }

                            } else {
                                Quests.printSevere("[Quests] reach-location-radii: in Stage " + s2 + " of Quest "
                                        + quest.name + " is not a list of numbers!");
                                stageFailed = true;
                                break;
                            }

                        } else {
                            Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                    + " is missing reach-location-radii:");
                            stageFailed = true;
                            break;
                        }

                        if (config.contains("quests." + s + ".stages.ordered." + s2 + ".reach-location-names")) {

                            if (Quests.checkList(
                                    config.getList("quests." + s + ".stages.ordered." + s2 + ".reach-location-names"),
                                    String.class)) {

                                final List<String> locationNames = config.getStringList("quests." + s
                                        + ".stages.ordered." + s2 + ".reach-location-names");
                                for (final String name: locationNames) {

                                    oStage.locationNames.add(name);

                                }

                            } else {
                                Quests.printSevere("[Quests] reach-location-names: in Stage " + s2 + " of Quest "
                                        + quest.name + " is not a list of names!");
                                stageFailed = true;
                                break;
                            }

                        } else {
                            Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                    + " is missing reach-location-names:");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".mobs-to-tame")) {

                        if (Quests.checkList(config.getList("quests." + s + ".stages.ordered." + s2 + ".mobs-to-tame"),
                                String.class)) {

                            if (config.contains("quests." + s + ".stages.ordered." + s2 + ".mob-tame-amounts")) {

                                if (Quests.checkList(
                                        config.getList("quests." + s + ".stages.ordered." + s2 + ".mob-tame-amounts"),
                                        Integer.class)) {

                                    final List<String> mobs = config.getStringList("quests." + s + ".stages.ordered."
                                            + s2 + ".mobs-to-tame");
                                    final List<Integer> mobAmounts = config.getIntegerList("quests." + s
                                            + ".stages.ordered." + s2 + ".mob-tame-amounts");

                                    for (final String mob: mobs) {

                                        if (mob.equalsIgnoreCase("Wolf")) {

                                            oStage.mobsToTame.put(EntityType.WOLF, mobAmounts.get(mobs.indexOf(mob)));

                                        } else if (mob.equalsIgnoreCase("Ocelot")) {

                                            oStage.mobsToTame.put(EntityType.OCELOT, mobAmounts.get(mobs.indexOf(mob)));

                                        } else {
                                            Quests.printSevere("[Quests] " + mob
                                                    + " inside mobs-to-tame: inside Stage " + s2 + " of Quest "
                                                    + quest.name + " is not a valid tameable mob!");
                                            stageFailed = true;
                                            break;
                                        }

                                    }

                                } else {
                                    Quests.printSevere("[Quests] mob-tame-amounts: in Stage " + s2 + " of Quest "
                                            + quest.name + " is not a list of numbers!");
                                    stageFailed = true;
                                    break;
                                }

                            } else {
                                Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                        + " is missing mob-tame-amounts:");
                                stageFailed = true;
                                break;
                            }

                        } else {
                            Quests.printSevere("[Quests] mobs-to-tame: in Stage " + s2 + " of Quest " + quest.name
                                    + " is not a list of mob names!");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".sheep-to-shear")) {

                        if (Quests.checkList(
                                config.getList("quests." + s + ".stages.ordered." + s2 + ".sheep-to-shear"),
                                String.class)) {

                            if (config.contains("quests." + s + ".stages.ordered." + s2 + ".sheep-amounts")) {

                                if (Quests.checkList(
                                        config.getList("quests." + s + ".stages.ordered." + s2 + ".sheep-amounts"),
                                        Integer.class)) {

                                    final List<String> sheep = config.getStringList("quests." + s + ".stages.ordered."
                                            + s2 + ".sheep-to-shear");
                                    final List<Integer> shearAmounts = config.getIntegerList("quests." + s
                                            + ".stages.ordered." + s2 + ".sheep-amounts");

                                    for (final String color: sheep) {

                                        if (color.equalsIgnoreCase("Black")) {

                                            oStage.sheepToShear.put(DyeColor.BLACK,
                                                    shearAmounts.get(sheep.indexOf(color)));

                                        } else if (color.equalsIgnoreCase("Blue")) {

                                            oStage.sheepToShear.put(DyeColor.BLUE,
                                                    shearAmounts.get(sheep.indexOf(color)));

                                        } else if (color.equalsIgnoreCase("Brown")) {

                                            oStage.sheepToShear.put(DyeColor.BROWN,
                                                    shearAmounts.get(sheep.indexOf(color)));

                                        } else if (color.equalsIgnoreCase("Cyan")) {

                                            oStage.sheepToShear.put(DyeColor.CYAN,
                                                    shearAmounts.get(sheep.indexOf(color)));

                                        } else if (color.equalsIgnoreCase("Gray")) {

                                            oStage.sheepToShear.put(DyeColor.GRAY,
                                                    shearAmounts.get(sheep.indexOf(color)));

                                        } else if (color.equalsIgnoreCase("Green")) {

                                            oStage.sheepToShear.put(DyeColor.GREEN,
                                                    shearAmounts.get(sheep.indexOf(color)));

                                        } else if (color.equalsIgnoreCase("LightBlue")) {

                                            oStage.sheepToShear.put(DyeColor.LIGHT_BLUE,
                                                    shearAmounts.get(sheep.indexOf(color)));

                                        } else if (color.equalsIgnoreCase("Lime")) {

                                            oStage.sheepToShear.put(DyeColor.LIME,
                                                    shearAmounts.get(sheep.indexOf(color)));

                                        } else if (color.equalsIgnoreCase("Magenta")) {

                                            oStage.sheepToShear.put(DyeColor.MAGENTA,
                                                    shearAmounts.get(sheep.indexOf(color)));

                                        } else if (color.equalsIgnoreCase("Orange")) {

                                            oStage.sheepToShear.put(DyeColor.ORANGE,
                                                    shearAmounts.get(sheep.indexOf(color)));

                                        } else if (color.equalsIgnoreCase("Pink")) {

                                            oStage.sheepToShear.put(DyeColor.PINK,
                                                    shearAmounts.get(sheep.indexOf(color)));

                                        } else if (color.equalsIgnoreCase("Purple")) {

                                            oStage.sheepToShear.put(DyeColor.PURPLE,
                                                    shearAmounts.get(sheep.indexOf(color)));

                                        } else if (color.equalsIgnoreCase("Red")) {

                                            oStage.sheepToShear.put(DyeColor.RED,
                                                    shearAmounts.get(sheep.indexOf(color)));

                                        } else if (color.equalsIgnoreCase("Silver")) {

                                            oStage.sheepToShear.put(DyeColor.SILVER,
                                                    shearAmounts.get(sheep.indexOf(color)));

                                        } else if (color.equalsIgnoreCase("White")) {

                                            oStage.sheepToShear.put(DyeColor.WHITE,
                                                    shearAmounts.get(sheep.indexOf(color)));

                                        } else if (color.equalsIgnoreCase("Yellow")) {

                                            oStage.sheepToShear.put(DyeColor.YELLOW,
                                                    shearAmounts.get(sheep.indexOf(color)));

                                        } else {

                                            Quests.printSevere("[Quests] " + color
                                                    + " inside sheep-to-shear: inside Stage " + s2 + " of Quest "
                                                    + quest.name + " is not a valid color!");
                                            stageFailed = true;
                                            break;

                                        }

                                    }

                                } else {
                                    Quests.printSevere("[Quests] sheep-amounts: in Stage " + s2 + " of Quest "
                                            + quest.name + " is not a list of numbers!");
                                    stageFailed = true;
                                    break;
                                }

                            } else {
                                Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                        + " is missing sheep-amounts:");
                                stageFailed = true;
                                break;
                            }

                        } else {
                            Quests.printSevere("[Quests] sheep-to-shear: in Stage " + s2 + " of Quest " + quest.name
                                    + " is not a list of colors!");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".password-displays")) {

                        final List<String> displays = config.getStringList("quests." + s + ".stages.ordered." + s2
                                + ".password-displays");

                        if (config.contains("quests." + s + ".stages.ordered." + s2 + ".password-phrases")) {

                            final List<String> phrases = config.getStringList("quests." + s + ".stages.ordered." + s2
                                    + ".password-phrases");
                            if (displays.size() == phrases.size()) {

                                for (int passIndex = 0; passIndex < displays.size(); passIndex++) {

                                    oStage.passwordDisplays.add(displays.get(passIndex));
                                    final LinkedList<String> answers = new LinkedList<String>();
                                    answers.addAll(Arrays.asList(phrases.get(passIndex).split("\\|")));
                                    oStage.passwordPhrases.add(answers);

                                }

                            } else {
                                Quests.printSevere("[Quests] password-displays and password-phrases in Stage " + s2
                                        + " of Quest " + quest.name + " are not the same size!");
                                stageFailed = true;
                                break;
                            }

                        } else {
                            Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                    + " is missing password-phrases!");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".custom-objectives")) {

                        final ConfigurationSection sec = config.getConfigurationSection("quests." + s
                                + ".stages.ordered." + s2 + ".custom-objectives");
                        for (final String path: sec.getKeys(false)) {

                            final String name = sec.getString(path + ".name");
                            final int count = sec.getInt(path + ".count");
                            CustomObjective found = null;

                            for (final CustomObjective cr: customObjectives) {
                                if (cr.getName().equalsIgnoreCase(name)) {
                                    found = cr;
                                    break;
                                }
                            }

                            if (found == null) {
                                Quests.printWarning("[Quests] Custom objective \"" + name + "\" for Stage " + s2
                                        + " of Quest \"" + quest.name + "\" could not be found!");
                                continue;
                            }

                            final Map<String, Object> data = new HashMap<String, Object>();
                            final ConfigurationSection sec2 = sec.getConfigurationSection(path + ".data");
                            if (sec2 != null) {
                                for (final String dataPath: sec2.getKeys(false)) {
                                    data.put(dataPath, sec2.get(dataPath));
                                }
                            }

                            oStage.customObjectives.add(found);
                            oStage.customObjectiveCounts.add(count);
                            oStage.customObjectiveData.add(data);

                            try {

                                getServer().getPluginManager().registerEvents(found, this);

                            } catch (final Exception e) {
                                Quests.printWarning("[Quests] Failed to register events for custom objective \"" + name
                                        + "\" in Stage " + s2 + " of Quest \"" + quest.name
                                        + "\". Does the objective class listen for events?");
                                if (debug) {
                                    Quests.printWarning("[Quests] Error log:");
                                    e.printStackTrace();
                                }
                            }

                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".objective-override")) {

                        oStage.objectiveOverride = config.getString("quests." + s + ".stages.ordered." + s2
                                + ".objective-override");

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".start-event")) {

                        final Event evt = Event.loadEvent(
                                config.getString("quests." + s + ".stages.ordered." + s2 + ".start-event"), this);

                        if (evt != null) {
                            oStage.startEvent = evt;
                        } else {
                            Quests.printSevere("[Quests] start-event: in Stage " + s2 + " of Quest " + quest.name
                                    + " failed to load.");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".finish-event")) {

                        final Event evt = Event.loadEvent(
                                config.getString("quests." + s + ".stages.ordered." + s2 + ".finish-event"), this);

                        if (evt != null) {
                            oStage.finishEvent = evt;
                        } else {
                            Quests.printSevere("[Quests] finish-event: in Stage " + s2 + " of Quest " + quest.name
                                    + " failed to load.");
                            stageFailed = true;
                            break;
                        }

                    }

                    // Legacy support
                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".event")) {

                        final Event evt = Event.loadEvent(
                                config.getString("quests." + s + ".stages.ordered." + s2 + ".event"), this);

                        if (evt != null) {
                            oStage.finishEvent = evt;
                            Quests.printInfo("[Quests] Converting event: in Stage " + s2 + " of Quest " + quest.name
                                    + " to finish-event:");
                            final String old = config.getString("quests." + s + ".stages.ordered." + s2 + ".event");
                            config.set("quests." + s + ".stages.ordered." + s2 + ".finish-event", old);
                            config.set("quests." + s + ".stages.ordered." + s2 + ".event", null);
                            needsSaving = true;
                        } else {
                            Quests.printSevere("[Quests] event: in Stage " + s2 + " of Quest " + quest.name
                                    + " failed to load.");
                            stageFailed = true;
                            break;
                        }

                    }
                    //

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".death-event")) {

                        final Event evt = Event.loadEvent(
                                config.getString("quests." + s + ".stages.ordered." + s2 + ".death-event"), this);

                        if (evt != null) {
                            oStage.deathEvent = evt;
                        } else {
                            Quests.printSevere("[Quests] death-event: in Stage " + s2 + " of Quest " + quest.name
                                    + " failed to load.");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".disconnect-event")) {

                        final Event evt = Event.loadEvent(
                                config.getString("quests." + s + ".stages.ordered." + s2 + ".disconnect-event"), this);

                        if (evt != null) {
                            oStage.disconnectEvent = evt;
                        } else {
                            Quests.printSevere("[Quests] disconnect-event: in Stage " + s2 + " of Quest " + quest.name
                                    + " failed to load.");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".chat-events")) {

                        if (config.isList("quests." + s + ".stages.ordered." + s2 + ".chat-events")) {

                            if (config.contains("quests." + s + ".stages.ordered." + s2 + ".chat-event-triggers")) {

                                if (config.isList("quests." + s + ".stages.ordered." + s2 + ".chat-event-triggers")) {

                                    final List<String> chatEvents = config.getStringList("quests." + s
                                            + ".stages.ordered." + s2 + ".chat-events");
                                    final List<String> chatEventTriggers = config.getStringList("quests." + s
                                            + ".stages.ordered." + s2 + ".chat-event-triggers");
                                    boolean loadEventFailed = false;

                                    for (int i = 0; i < chatEvents.size(); i++) {

                                        final Event evt = Event.loadEvent(chatEvents.get(i), this);

                                        if (evt != null) {
                                            oStage.chatEvents.put(chatEventTriggers.get(i), evt);
                                        } else {
                                            Quests.printSevere("[Quests] " + chatEvents.get(i)
                                                    + " inside of chat-events: in Stage " + s2 + " of Quest "
                                                    + quest.name + " failed to load.");
                                            stageFailed = true;
                                            loadEventFailed = true;
                                            break;
                                        }

                                    }

                                    if (loadEventFailed) {
                                        break;
                                    }

                                } else {
                                    Quests.printSevere("[Quests] chat-event-triggers in Stage " + s2 + " of Quest "
                                            + quest.name + " is not in list format!");
                                    stageFailed = true;
                                    break;
                                }

                            } else {
                                Quests.printSevere("[Quests] Stage " + s2 + " of Quest " + quest.name
                                        + " is missing chat-event-triggers!");
                                stageFailed = true;
                                break;
                            }

                        } else {
                            Quests.printSevere("[Quests] chat-events in Stage " + s2 + " of Quest " + quest.name
                                    + " is not in list format!");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".delay")) {

                        if (config.getLong("quests." + s + ".stages.ordered." + s2 + ".delay", -999) != -999) {
                            oStage.delay = config.getLong("quests." + s + ".stages.ordered." + s2 + ".delay");
                        } else {
                            Quests.printSevere("[Quests] delay: in Stage " + s2 + " of Quest " + quest.name
                                    + " is not a number!");
                            stageFailed = true;
                            break;
                        }

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".delay-message")) {

                        oStage.delayMessage = config.getString("quests." + s + ".stages.ordered." + s2
                                + ".delay-message");

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".start-message")) {

                        oStage.startMessage = config.getString("quests." + s + ".stages.ordered." + s2
                                + ".start-message");

                    }

                    if (config.contains("quests." + s + ".stages.ordered." + s2 + ".complete-message")) {

                        oStage.completeMessage = config.getString("quests." + s + ".stages.ordered." + s2
                                + ".complete-message");

                    }

                    final LinkedList<Integer> ids = new LinkedList<Integer>();
                    if (npcIdsToTalkTo != null) {
                        ids.addAll(npcIdsToTalkTo);
                    }
                    oStage.citizensToInteract = ids;

                    if (stageFailed) {
                        break;
                    }

                    quest.orderedStages.add(oStage);

                }

                if (stageFailed) {
                    continue;
                }

                // Load rewards
                if (config.contains("quests." + s + ".rewards.items")) {

                    if (Quests.checkList(config.getList("quests." + s + ".rewards.items"), String.class)) {

                        boolean failed = false;
                        for (final String item: config.getStringList("quests." + s + ".rewards.items")) {

                            try {
                                final ItemStack stack = ItemUtil.readItemStack(item);
                                if (stack != null) {
                                    quest.itemRewards.add(stack);
                                }
                            } catch (final Exception e) {
                                Quests.printSevere("[Quests] " + item + " in items: Reward in Quest " + quest.name
                                        + " is not properly formatted!");
                                failed = true;
                                break;
                            }

                        }

                        if (failed) {
                            continue;
                        }

                    } else {
                        Quests.printSevere("[Quests] items: Reward in Quest " + quest.name
                                + " is not a list of strings!");
                        continue;
                    }

                }

                if (config.contains("quests." + s + ".rewards.money")) {

                    if (config.getInt("quests." + s + ".rewards.money", -999) != -999) {
                        quest.moneyReward = config.getInt("quests." + s + ".rewards.money");
                    } else {
                        Quests.printSevere("[Quests] money: Reward in Quest " + quest.name + " is not a number!");
                        continue;
                    }

                }

                if (config.contains("quests." + s + ".rewards.exp")) {

                    if (config.getInt("quests." + s + ".rewards.exp", -999) != -999) {
                        quest.exp = config.getInt("quests." + s + ".rewards.exp");
                    } else {
                        Quests.printSevere("[Quests] exp: Reward in Quest " + quest.name + " is not a number!");
                        continue;
                    }

                }

                if (config.contains("quests." + s + ".rewards.commands")) {

                    if (Quests.checkList(config.getList("quests." + s + ".rewards.commands"), String.class)) {
                        quest.commands.clear();
                        quest.commands.addAll(config.getStringList("quests." + s + ".rewards.commands"));
                    } else {
                        Quests.printSevere("[Quests] commands: Reward in Quest " + quest.name
                                + " is not a list of commands!");
                        continue;
                    }

                }

                if (config.contains("quests." + s + ".rewards.permissions")) {

                    if (Quests.checkList(config.getList("quests." + s + ".rewards.permissions"), String.class)) {
                        quest.permissions.clear();
                        quest.permissions.addAll(config.getStringList("quests." + s + ".rewards.permissions"));
                    } else {
                        Quests.printSevere("[Quests] permissions: Reward in Quest " + quest.name
                                + " is not a list of permissions!");
                        continue;
                    }

                }

                if (config.contains("quests." + s + ".rewards.quest-points")) {

                    if (config.getInt("quests." + s + ".rewards.quest-points", -999) != -999) {
                        quest.questPoints = config.getInt("quests." + s + ".rewards.quest-points");
                        totalQuestPoints += quest.questPoints;
                    } else {
                        Quests.printSevere("[Quests] quest-points: Reward in Quest " + quest.name + " is not a number!");
                        continue;
                    }

                }

                if (config.contains("quests." + s + ".rewards.mcmmo-skills")) {

                    if (Quests.checkList(config.getList("quests." + s + ".rewards.mcmmo-skills"), String.class)) {

                        if (config.contains("quests." + s + ".rewards.mcmmo-levels")) {

                            if (Quests
                                    .checkList(config.getList("quests." + s + ".rewards.mcmmo-levels"), Integer.class)) {

                                boolean failed = false;
                                for (final String skill: config.getStringList("quests." + s + ".rewards.mcmmo-skills")) {

                                    if (Quests.getMcMMOSkill(skill) == null) {
                                        Quests.printSevere("[Quests] " + skill + " in mcmmo-skills: Reward in Quest "
                                                + quest.name + " is not a valid mcMMO skill name!");
                                        failed = true;
                                        break;
                                    }

                                }
                                if (failed) {
                                    continue;
                                }

                                quest.mcmmoSkills.clear();
                                quest.mcmmoAmounts.clear();

                                quest.mcmmoSkills.addAll(config.getStringList("quests." + s + ".rewards.mcmmo-skills"));
                                quest.mcmmoAmounts.addAll(config
                                        .getIntegerList("quests." + s + ".rewards.mcmmo-levels"));

                            } else {
                                Quests.printSevere("[Quests] mcmmo-levels: Reward in Quest " + quest.name
                                        + " is not a list of numbers!");
                                continue;
                            }

                        } else {
                            Quests.printSevere("[Quests] Rewards for Quest " + quest.name + " is missing mcmmo-levels:");
                            continue;
                        }

                    } else {
                        Quests.printSevere("[Quests] mcmmo-skills: Reward in Quest " + quest.name
                                + " is not a list of mcMMO skill names!");
                        continue;
                    }
                }

                if (config.contains("quests." + s + ".rewards.heroes-exp-classes")) {

                    if (Quests.checkList(config.getList("quests." + s + ".rewards.heroes-exp-classes"), String.class)) {

                        if (config.contains("quests." + s + ".rewards.heroes-exp-amounts")) {

                            if (Quests.checkList(config.getList("quests." + s + ".rewards.heroes-exp-amounts"),
                                    Double.class)) {

                                boolean failed = false;
                                for (final String heroClass: config.getStringList("quests." + s
                                        + ".rewards.heroes-exp-classes")) {

                                    if (Quests.heroes.getClassManager().getClass(heroClass) == null) {
                                        Quests.printSevere("[Quests] " + heroClass
                                                + " in heroes-exp-classes: Reward in Quest " + quest.name
                                                + " is not a valid Heroes class name!");
                                        failed = true;
                                        break;
                                    }

                                }
                                if (failed) {
                                    continue;
                                }

                                quest.heroesClasses.clear();
                                quest.heroesAmounts.clear();

                                quest.heroesClasses.addAll(config.getStringList("quests." + s
                                        + ".rewards.heroes-exp-classes"));
                                quest.heroesAmounts.addAll(config.getDoubleList("quests." + s
                                        + ".rewards.heroes-exp-amounts"));

                            } else {
                                Quests.printSevere("[Quests] heroes-exp-amounts: Reward in Quest " + quest.name
                                        + " is not a list of experience amounts (decimal numbers)!");
                                continue;
                            }

                        } else {
                            Quests.printSevere("[Quests] Rewards for Quest " + quest.name
                                    + " is missing heroes-exp-amounts:");
                            continue;
                        }

                    } else {
                        Quests.printSevere("[Quests] heroes-exp-classes: Reward in Quest " + quest.name
                                + " is not a list of Heroes classes!");
                        continue;
                    }
                }

                if (config.contains("quests." + s + ".rewards.phat-loots")) {

                    if (Quests.checkList(config.getList("quests." + s + ".rewards.phat-loots"), String.class)) {

                        boolean failed = false;
                        for (final String loot: config.getStringList("quests." + s + ".rewards.phat-loots")) {

                            if (PhatLootsAPI.getPhatLoot(loot) == null) {
                                Quests.printSevere("[Quests] " + loot + " in phat-loots: Reward in Quest " + quest.name
                                        + " is not a valid PhatLoot name!");
                                failed = true;
                                break;
                            }

                        }
                        if (failed) {
                            continue;
                        }

                        quest.phatLootRewards.clear();
                        quest.phatLootRewards.addAll(config.getStringList("quests." + s + ".rewards.phat-loots"));

                    } else {
                        Quests.printSevere("[Quests] phat-loots: Reward in Quest " + quest.name
                                + " is not a list of PhatLoots!");
                        continue;
                    }
                }

                if (config.contains("quests." + s + ".rewards.custom-rewards")) {

                    final ConfigurationSection sec = config.getConfigurationSection("quests." + s
                            + ".rewards.custom-rewards");
                    for (final String path: sec.getKeys(false)) {

                        final String name = sec.getString(path + ".name");
                        boolean found = false;

                        for (final CustomReward cr: customRewards) {
                            if (cr.getName().equalsIgnoreCase(name)) {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            Quests.printWarning("[Quests] Custom reward \"" + name + "\" for Quest \"" + quest.name
                                    + "\" could not be found!");
                            continue;
                        }

                        final Map<String, Object> data = new HashMap<String, Object>();
                        final ConfigurationSection sec2 = sec.getConfigurationSection(path + ".data");
                        if (sec2 != null) {
                            for (final String dataPath: sec2.getKeys(false)) {
                                data.put(dataPath, sec2.get(dataPath));
                            }
                        }

                        quest.customRewards.put(name, data);

                    }

                }

                //
                quests.add(quest);

                if (needsSaving) {
                    config.save(file);
                }

            } catch (final IOException e) {

                if (debug == false) {
                    Quests.log.log(Level.SEVERE, "[Quests] Failed to load Quest \"" + s + "\". Skipping.");
                } else {
                    Quests.log.log(Level.SEVERE, "[Quests] Failed to load Quest \"" + s + "\". Error log:");
                    e.printStackTrace();
                }

            }

            if (failedToLoad == true) {
                Quests.log.log(Level.SEVERE, "[Quests] Failed to load Quest \"" + s + "\". Skipping.");
            }

        }

    }

    public void loadEvents() {

        final YamlConfiguration config = new YamlConfiguration();
        final File eventsFile = new File(getDataFolder(), "events.yml");

        try {
            config.load(eventsFile);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final InvalidConfigurationException e) {
            e.printStackTrace();
        }

        final ConfigurationSection sec = config.getConfigurationSection("events");
        for (final String s: sec.getKeys(false)) {

            final Event event = Event.loadEvent(s, this);
            if (event != null) {
                events.add(event);
            } else {
                Quests.log.log(Level.SEVERE, "[Quests] Failed to load Event \"" + s + "\". Skipping.");
            }

        }

    }

    public static String parseString(String s, Quest quest) {

        String parsed = s;

        if (parsed.contains("<npc>")) {
            parsed = parsed.replaceAll("<npc>", quest.npcStart.getName());
        }

        parsed = parsed.replaceAll("<black>", ColorUtil.BLACK.toString());
        parsed = parsed.replaceAll("<darkblue>", ColorUtil.DARKBLUE.toString());
        parsed = parsed.replaceAll("<darkgreen>", ColorUtil.DARKGREEN.toString());
        parsed = parsed.replaceAll("<darkaqua>", ColorUtil.DARKAQUA.toString());
        parsed = parsed.replaceAll("<darkred>", ColorUtil.DARKRED.toString());
        parsed = parsed.replaceAll("<purple>", ColorUtil.PURPLE.toString());
        parsed = parsed.replaceAll("<gold>", ColorUtil.GOLD.toString());
        parsed = parsed.replaceAll("<grey>", ColorUtil.GRAY.toString());
        parsed = parsed.replaceAll("<gray>", ColorUtil.GRAY.toString());
        parsed = parsed.replaceAll("<darkgrey>", ColorUtil.DARKGRAY.toString());
        parsed = parsed.replaceAll("<darkgray>", ColorUtil.DARKGRAY.toString());
        parsed = parsed.replaceAll("<blue>", ColorUtil.BLUE.toString());
        parsed = parsed.replaceAll("<green>", ColorUtil.GREEN.toString());
        parsed = parsed.replaceAll("<aqua>", ColorUtil.AQUA.toString());
        parsed = parsed.replaceAll("<red>", ColorUtil.RED.toString());
        parsed = parsed.replaceAll("<pink>", ColorUtil.PINK.toString());
        parsed = parsed.replaceAll("<yellow>", ColorUtil.YELLOW.toString());
        parsed = parsed.replaceAll("<white>", ColorUtil.WHITE.toString());

        parsed = parsed.replaceAll("<random>", ColorUtil.MAGIC.toString());
        parsed = parsed.replaceAll("<italic>", ColorUtil.ITALIC.toString());
        parsed = parsed.replaceAll("<bold>", ColorUtil.BOLD.toString());
        parsed = parsed.replaceAll("<underline>", ColorUtil.UNDERLINE.toString());
        parsed = parsed.replaceAll("<strike>", ColorUtil.STRIKETHROUGH.toString());
        parsed = parsed.replaceAll("<reset>", ColorUtil.RESET.toString());
        parsed = ChatColor.translateAlternateColorCodes('&', parsed);

        return parsed;
    }

    public static String parseString(String s, NPC npc) {

        String parsed = s;

        if (parsed.contains("<npc>")) {
            parsed = parsed.replaceAll("<npc>", npc.getName());
        }

        parsed = parsed.replaceAll("<black>", ColorUtil.BLACK.toString());
        parsed = parsed.replaceAll("<darkblue>", ColorUtil.DARKBLUE.toString());
        parsed = parsed.replaceAll("<darkgreen>", ColorUtil.DARKGREEN.toString());
        parsed = parsed.replaceAll("<darkaqua>", ColorUtil.DARKAQUA.toString());
        parsed = parsed.replaceAll("<darkred>", ColorUtil.DARKRED.toString());
        parsed = parsed.replaceAll("<purple>", ColorUtil.PURPLE.toString());
        parsed = parsed.replaceAll("<gold>", ColorUtil.GOLD.toString());
        parsed = parsed.replaceAll("<grey>", ColorUtil.GRAY.toString());
        parsed = parsed.replaceAll("<gray>", ColorUtil.GRAY.toString());
        parsed = parsed.replaceAll("<darkgrey>", ColorUtil.DARKGRAY.toString());
        parsed = parsed.replaceAll("<darkgray>", ColorUtil.DARKGRAY.toString());
        parsed = parsed.replaceAll("<blue>", ColorUtil.BLUE.toString());
        parsed = parsed.replaceAll("<green>", ColorUtil.GREEN.toString());
        parsed = parsed.replaceAll("<aqua>", ColorUtil.AQUA.toString());
        parsed = parsed.replaceAll("<red>", ColorUtil.RED.toString());
        parsed = parsed.replaceAll("<pink>", ColorUtil.PINK.toString());
        parsed = parsed.replaceAll("<yellow>", ColorUtil.YELLOW.toString());
        parsed = parsed.replaceAll("<white>", ColorUtil.WHITE.toString());

        parsed = parsed.replaceAll("<random>", ColorUtil.MAGIC.toString());
        parsed = parsed.replaceAll("<italic>", ColorUtil.ITALIC.toString());
        parsed = parsed.replaceAll("<bold>", ColorUtil.BOLD.toString());
        parsed = parsed.replaceAll("<underline>", ColorUtil.UNDERLINE.toString());
        parsed = parsed.replaceAll("<strike>", ColorUtil.STRIKETHROUGH.toString());
        parsed = parsed.replaceAll("<reset>", ColorUtil.RESET.toString());
        parsed = ChatColor.translateAlternateColorCodes('&', parsed);

        return parsed;

    }

    public static String parseString(String s) {

        String parsed = s;

        parsed = parsed.replaceAll("<black>", ColorUtil.BLACK.toString());
        parsed = parsed.replaceAll("<darkblue>", ColorUtil.DARKBLUE.toString());
        parsed = parsed.replaceAll("<darkgreen>", ColorUtil.DARKGREEN.toString());
        parsed = parsed.replaceAll("<darkaqua>", ColorUtil.DARKAQUA.toString());
        parsed = parsed.replaceAll("<darkred>", ColorUtil.DARKRED.toString());
        parsed = parsed.replaceAll("<purple>", ColorUtil.PURPLE.toString());
        parsed = parsed.replaceAll("<gold>", ColorUtil.GOLD.toString());
        parsed = parsed.replaceAll("<grey>", ColorUtil.GRAY.toString());
        parsed = parsed.replaceAll("<gray>", ColorUtil.GRAY.toString());
        parsed = parsed.replaceAll("<darkgrey>", ColorUtil.DARKGRAY.toString());
        parsed = parsed.replaceAll("<darkgray>", ColorUtil.DARKGRAY.toString());
        parsed = parsed.replaceAll("<blue>", ColorUtil.BLUE.toString());
        parsed = parsed.replaceAll("<green>", ColorUtil.GREEN.toString());
        parsed = parsed.replaceAll("<aqua>", ColorUtil.AQUA.toString());
        parsed = parsed.replaceAll("<red>", ColorUtil.RED.toString());
        parsed = parsed.replaceAll("<pink>", ColorUtil.PINK.toString());
        parsed = parsed.replaceAll("<yellow>", ColorUtil.YELLOW.toString());
        parsed = parsed.replaceAll("<white>", ColorUtil.WHITE.toString());

        parsed = parsed.replaceAll("<random>", ColorUtil.MAGIC.toString());
        parsed = parsed.replaceAll("<italic>", ColorUtil.ITALIC.toString());
        parsed = parsed.replaceAll("<bold>", ColorUtil.BOLD.toString());
        parsed = parsed.replaceAll("<underline>", ColorUtil.UNDERLINE.toString());
        parsed = parsed.replaceAll("<strike>", ColorUtil.STRIKETHROUGH.toString());
        parsed = parsed.replaceAll("<reset>", ColorUtil.RESET.toString());
        parsed = ChatColor.translateAlternateColorCodes('&', parsed);

        return parsed;

    }

    private boolean setupEconomy() {
        try {

            final RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
                    .getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                Quests.economy = economyProvider.getProvider();
            }

            return (Quests.economy != null);

        } catch (final Exception e) {
            return false;
        }
    }

    private boolean setupPermissions() {
        final RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            Quests.permission = permissionProvider.getProvider();
        }
        return (Quests.permission != null);
    }

    private static Map<String, Integer> sort(Map<String, Integer> unsortedMap) {

        final List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortedMap.entrySet());

        Collections.sort(list, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                final int i = o1.getValue();
                final int i2 = o2.getValue();
                if (i < i2) {
                    return 1;
                } else if (i == i2) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        final Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (final Entry<String, Integer> entry: list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static void printSevere(String s) {

        // s = ChatColor.stripColor(s);
        Quests.log.severe(s);

    }

    public static void printWarning(String s) {

        // s = ChatColor.stripColor(s);
        Quests.log.warning(s);

    }

    public static void printInfo(String s) {

        // s = ChatColor.stripColor(s);
        Quests.log.info(s);

    }

    public boolean hasItem(Player player, ItemStack is) {

        final Inventory inv = player.getInventory();
        int playerAmount = 0;

        for (final ItemStack stack: inv.getContents()) {

            if (stack != null) {

                if (ItemUtil.compareItems(is, stack, false) == 0) {
                    playerAmount += stack.getAmount();
                }

            }

        }
        return playerAmount >= is.getAmount();

    }

    public static Location getLocation(String arg) {

        final String[] info = arg.split(" ");
        if (info.length != 4) {
            return null;
        }

        double x;
        double y;
        double z;

        try {
            x = Double.parseDouble(info[1]);
            y = Double.parseDouble(info[2]);
            z = Double.parseDouble(info[3]);
        } catch (final NumberFormatException e) {
            return null;
        }

        if (Bukkit.getServer().getWorld(info[0]) == null) {
            return null;
        }

        final Location finalLocation = new Location(Bukkit.getServer().getWorld(info[0]), x, y, z);

        return finalLocation;

    }

    public static String getLocationInfo(Location loc) {

        String info = "";

        info += loc.getWorld().getName();
        info += " " + loc.getX();
        info += " " + loc.getY();
        info += " " + loc.getZ();

        return info;

    }

    public static Effect getEffect(String eff) {

        if (eff.equalsIgnoreCase("BLAZE_SHOOT")) {
            return Effect.BLAZE_SHOOT;
        } else if (eff.equalsIgnoreCase("BOW_FIRE")) {
            return Effect.BOW_FIRE;
        } else if (eff.equalsIgnoreCase("CLICK1")) {
            return Effect.CLICK1;
        } else if (eff.equalsIgnoreCase("CLICK2")) {
            return Effect.CLICK2;
        } else if (eff.equalsIgnoreCase("DOOR_TOGGLE")) {
            return Effect.DOOR_TOGGLE;
        } else if (eff.equalsIgnoreCase("EXTINGUISH")) {
            return Effect.EXTINGUISH;
        } else if (eff.equalsIgnoreCase("GHAST_SHOOT")) {
            return Effect.GHAST_SHOOT;
        } else if (eff.equalsIgnoreCase("GHAST_SHRIEK")) {
            return Effect.GHAST_SHRIEK;
        } else if (eff.equalsIgnoreCase("ZOMBIE_CHEW_IRON_DOOR")) {
            return Effect.ZOMBIE_CHEW_IRON_DOOR;
        } else if (eff.equalsIgnoreCase("ZOMBIE_CHEW_WOODEN_DOOR")) {
            return Effect.ZOMBIE_CHEW_WOODEN_DOOR;
        } else if (eff.equalsIgnoreCase("ZOMBIE_DESTROY_DOOR")) {
            return Effect.ZOMBIE_DESTROY_DOOR;
        } else {
            return null;
        }
    }

    public static EntityType getMobType(String mob) {

        if (mob.equalsIgnoreCase("Bat")) {

            return EntityType.BAT;

        } else if (mob.equalsIgnoreCase("Blaze")) {

            return EntityType.BLAZE;

        } else if (mob.equalsIgnoreCase("CaveSpider")) {

            return EntityType.CAVE_SPIDER;

        } else if (mob.equalsIgnoreCase("Chicken")) {

            return EntityType.CHICKEN;

        } else if (mob.equalsIgnoreCase("Cow")) {

            return EntityType.COW;

        } else if (mob.equalsIgnoreCase("Creeper")) {

            return EntityType.CREEPER;

        } else if (mob.equalsIgnoreCase("Enderman")) {

            return EntityType.ENDERMAN;

        } else if (mob.equalsIgnoreCase("EnderDragon")) {

            return EntityType.ENDER_DRAGON;

        } else if (mob.equalsIgnoreCase("Ghast")) {

            return EntityType.GHAST;

        } else if (mob.equalsIgnoreCase("Giant")) {

            return EntityType.GIANT;

        } else if (mob.equalsIgnoreCase("Horse")) {

            return EntityType.HORSE;

        } else if (mob.equalsIgnoreCase("IronGolem")) {

            return EntityType.IRON_GOLEM;

        } else if (mob.equalsIgnoreCase("MagmaCube")) {

            return EntityType.MAGMA_CUBE;

        } else if (mob.equalsIgnoreCase("MushroomCow")) {

            return EntityType.MUSHROOM_COW;

        } else if (mob.equalsIgnoreCase("Ocelot")) {

            return EntityType.OCELOT;

        } else if (mob.equalsIgnoreCase("Pig")) {

            return EntityType.PIG;

        } else if (mob.equalsIgnoreCase("PigZombie")) {

            return EntityType.PIG_ZOMBIE;

        } else if (mob.equalsIgnoreCase("Sheep")) {

            return EntityType.SHEEP;

        } else if (mob.equalsIgnoreCase("Silverfish")) {

            return EntityType.SILVERFISH;

        } else if (mob.equalsIgnoreCase("Skeleton")) {

            return EntityType.SKELETON;

        } else if (mob.equalsIgnoreCase("Slime")) {

            return EntityType.SLIME;

        } else if (mob.equalsIgnoreCase("Snowman")) {

            return EntityType.SNOWMAN;

        } else if (mob.equalsIgnoreCase("Spider")) {

            return EntityType.SPIDER;

        } else if (mob.equalsIgnoreCase("Squid")) {

            return EntityType.SQUID;

        } else if (mob.equalsIgnoreCase("Villager")) {

            return EntityType.VILLAGER;

        } else if (mob.equalsIgnoreCase("Witch")) {

            return EntityType.WITCH;

        } else if (mob.equalsIgnoreCase("Wither")) {

            return EntityType.WITHER;

        } else if (mob.equalsIgnoreCase("Wolf")) {

            return EntityType.WOLF;

        } else if (mob.equalsIgnoreCase("Zombie")) {

            return EntityType.ZOMBIE;

        } else {

            return null;
        }

    }

    public static String getTime(long milliseconds) {

        String message = "";

        final long days = milliseconds / 86400000;
        final long hours = (milliseconds % 86400000) / 3600000;
        final long minutes = ((milliseconds % 86400000) % 3600000) / 60000;
        final long seconds = (((milliseconds % 86400000) % 3600000) % 60000) / 1000;
        final long milliSeconds2 = (((milliseconds % 86400000) % 3600000) % 60000) % 1000;

        if (days > 0) {

            if (days == 1) {
                message += " 1 Day,";
            } else {
                message += " " + days + " Days,";
            }

        }

        if (hours > 0) {

            if (hours == 1) {
                message += " 1 Hour,";
            } else {
                message += " " + hours + " Hours,";
            }

        }

        if (minutes > 0) {

            if (minutes == 1) {
                message += " 1 Minute,";
            } else {
                message += " " + minutes + " Minutes,";
            }

        }

        if (seconds > 0) {

            if (seconds == 1) {
                message += " 1 Second,";
            } else {
                message += " " + seconds + " Seconds,";
            }
        } else {
            if (milliSeconds2 > 0) {
                if (milliSeconds2 == 1) {
                    message += " 1 Millisecond,";
                } else {
                    message += " " + milliSeconds2 + " Milliseconds,";
                }
            }
        }

        message = message.substring(1, message.length() - 1);

        return message;

    }

    private static final String thou[] = {"", "M", "MM", "MMM"};
    private static final String hund[] = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
    private static final String ten[]  = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
    private static final String unit[] = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

    public static String getNumeral(int i) {

        final int th = i / 1000;
        final int h = (i / 100) % 10;
        final int t = (i / 10) % 10;
        final int u = i % 10;

        return Quests.thou[th] + Quests.hund[h] + Quests.ten[t] + Quests.unit[u];

    }

    public static PotionEffect getPotionEffect(String type, int duration, int amplifier) {

        PotionEffectType potionType;

        if (type.equalsIgnoreCase("BLINDNESS")) {
            potionType = PotionEffectType.BLINDNESS;
        } else if (type.equalsIgnoreCase("CONFUSION")) {
            potionType = PotionEffectType.CONFUSION;
        } else if (type.equalsIgnoreCase("DAMAGE_RESISTANCE")) {
            potionType = PotionEffectType.DAMAGE_RESISTANCE;
        } else if (type.equalsIgnoreCase("FAST_DIGGING")) {
            potionType = PotionEffectType.FAST_DIGGING;
        } else if (type.equalsIgnoreCase("FIRE_RESISTANCE")) {
            potionType = PotionEffectType.FIRE_RESISTANCE;
        } else if (type.equalsIgnoreCase("HARM")) {
            potionType = PotionEffectType.HARM;
        } else if (type.equalsIgnoreCase("HEAL")) {
            potionType = PotionEffectType.HEAL;
        } else if (type.equalsIgnoreCase("HUNGER")) {
            potionType = PotionEffectType.HUNGER;
        } else if (type.equalsIgnoreCase("INCREASE_DAMAGE")) {
            potionType = PotionEffectType.INCREASE_DAMAGE;
        } else if (type.equalsIgnoreCase("INVISIBILITY")) {
            potionType = PotionEffectType.INVISIBILITY;
        } else if (type.equalsIgnoreCase("JUMP")) {
            potionType = PotionEffectType.JUMP;
        } else if (type.equalsIgnoreCase("NIGHT_VISION")) {
            potionType = PotionEffectType.NIGHT_VISION;
        } else if (type.equalsIgnoreCase("POISON")) {
            potionType = PotionEffectType.POISON;
        } else if (type.equalsIgnoreCase("REGENERATION")) {
            potionType = PotionEffectType.REGENERATION;
        } else if (type.equalsIgnoreCase("SLOW")) {
            potionType = PotionEffectType.SLOW;
        } else if (type.equalsIgnoreCase("SLOW_DIGGING")) {
            potionType = PotionEffectType.SLOW_DIGGING;
        } else if (type.equalsIgnoreCase("SPEED")) {
            potionType = PotionEffectType.SPEED;
        } else if (type.equalsIgnoreCase("WATER_BREATHING")) {
            potionType = PotionEffectType.WATER_BREATHING;
        } else if (type.equalsIgnoreCase("WEAKNESS")) {
            potionType = PotionEffectType.WEAKNESS;
        } else if (type.equalsIgnoreCase("WITHER")) {
            potionType = PotionEffectType.WITHER;
        } else {
            return null;
        }

        return new PotionEffect(potionType, duration, amplifier);

    }

    public static SkillType getMcMMOSkill(String s) {

        if (s.equalsIgnoreCase("Acrobatics")) {
            return SkillType.ACROBATICS;
        } else if (s.equalsIgnoreCase("Archery")) {
            return SkillType.ARCHERY;
        } else if (s.equalsIgnoreCase("Axes")) {
            return SkillType.AXES;
        } else if (s.equalsIgnoreCase("Excavation")) {
            return SkillType.EXCAVATION;
        } else if (s.equalsIgnoreCase("Fishing")) {
            return SkillType.FISHING;
        } else if (s.equalsIgnoreCase("Herbalism")) {
            return SkillType.HERBALISM;
        } else if (s.equalsIgnoreCase("Mining")) {
            return SkillType.MINING;
        } else if (s.equalsIgnoreCase("Repair")) {
            return SkillType.REPAIR;
        } else if (s.equalsIgnoreCase("Smelting")) {
            return SkillType.SMELTING;
        } else if (s.equalsIgnoreCase("Swords")) {
            return SkillType.SWORDS;
        } else if (s.equalsIgnoreCase("Taming")) {
            return SkillType.TAMING;
        } else if (s.equalsIgnoreCase("Unarmed")) {
            return SkillType.UNARMED;
        } else if (s.equalsIgnoreCase("Woodcutting")) {
            return SkillType.WOODCUTTING;
        } else {
            return null;
        }

    }

    public static void addItem(Player p, ItemStack i) {

        final PlayerInventory inv = p.getInventory();
        final HashMap<Integer, ItemStack> leftover = inv.addItem(i);

        if (leftover != null) {

            if (leftover.isEmpty() == false) {

                for (final ItemStack i2: leftover.values()) {
                    p.getWorld().dropItem(p.getLocation(), i2);
                }

            }

        }

    }

    public static String getCurrency(boolean plural) {

        if (Quests.economy == null) {
            return "Money";
        }

        if (plural) {
            if (Quests.economy.currencyNamePlural().trim().isEmpty()) {
                return "Money";
            } else {
                return Quests.economy.currencyNamePlural();
            }
        } else {
            if (Quests.economy.currencyNameSingular().trim().isEmpty()) {
                return "Money";
            } else {
                return Quests.economy.currencyNameSingular();
            }
        }

    }

    public static boolean removeItem(Inventory inventory, ItemStack is) {

        final int type = is.getTypeId();
        final int amount = is.getAmount();
        final HashMap<Integer, ? extends ItemStack> allItems = inventory.all(type);
        final HashMap<Integer, Integer> removeFrom = new HashMap<Integer, Integer>();
        int foundAmount = 0;
        for (final Map.Entry<Integer, ? extends ItemStack> item: allItems.entrySet()) {

            if (ItemUtil.compareItems(is, item.getValue(), true) == 0) {

                if (item.getValue().getAmount() >= amount - foundAmount) {
                    removeFrom.put(item.getKey(), amount - foundAmount);
                    foundAmount = amount;
                } else {
                    foundAmount += item.getValue().getAmount();
                    removeFrom.put(item.getKey(), item.getValue().getAmount());
                }
                if (foundAmount >= amount) {
                    break;
                }

            }

        }
        if (foundAmount == amount) {

            for (final Map.Entry<Integer, Integer> toRemove: removeFrom.entrySet()) {

                final ItemStack item = inventory.getItem(toRemove.getKey());
                if (item.getAmount() - toRemove.getValue() <= 0) {
                    inventory.clear(toRemove.getKey());
                } else {
                    item.setAmount(item.getAmount() - toRemove.getValue());
                    inventory.setItem(toRemove.getKey(), item);
                }

            }
            return true;

        }
        return false;
    }

    public boolean checkQuester(String name) {

        for (final String s: questerBlacklist) {

            if (Quests.checkQuester(name, s)) {
                return true;
            }

        }

        return false;

    }

    private static boolean checkQuester(String name, String check) {

        if (check.endsWith("*") && check.startsWith("*") == false) {

            check = check.substring(0, check.length());
            return name.endsWith(check);

        } else if (check.endsWith("*") == false && check.startsWith("*")) {

            check = check.substring(1);
            return name.startsWith(check);

        } else if (check.endsWith("*") && check.startsWith("*")) {

            check = check.substring(1, check.length());
            return name.contains(check);

        } else {
            return name.equalsIgnoreCase(check);

        }

    }

    public static boolean checkList(List<?> list, Class<?> c) {

        if (list == null) {
            return false;
        }

        for (final Object o: list) {

            if (c.isAssignableFrom(o.getClass()) == false) {
                return false;
            }

        }

        return true;

    }

    public Quest getQuest(String s) {

        for (final Quest q: quests) {
            if (q.name.equalsIgnoreCase(s)) {
                return q;
            }
        }

        return null;
    }

    public Quest findQuest(String s) {

        for (final Quest q: quests) {
            if (q.name.equalsIgnoreCase(s)) {
                return q;
            }
        }

        for (final Quest q: quests) {
            if (q.name.toLowerCase().contains(s.toLowerCase())) {
                return q;
            }
        }

        return null;

    }

    public Event getEvent(String s) {

        for (final Event e: events) {
            if (e.name.equalsIgnoreCase(s)) {
                return e;
            }
        }

        return null;

    }

    public String getNPCName(int id) {

        return citizens.getNPCRegistry().getById(id).getName();

    }

    public static int countInv(Inventory inv, Material m, int subtract) {

        int count = 0;

        for (final ItemStack i: inv.getContents()) {

            if (i != null) {
                if (i.getType().equals(m)) {
                    count += i.getAmount();
                }
            }

        }

        return count - subtract;

    }

    public static Enchantment getEnchantment(String enchant) {

        if (enchant.equalsIgnoreCase("Power")) {

            return Enchantment.ARROW_DAMAGE;

        } else if (enchant.equalsIgnoreCase("Flame")) {

            return Enchantment.ARROW_FIRE;

        } else if (enchant.equalsIgnoreCase("Infinity")) {

            return Enchantment.ARROW_INFINITE;

        } else if (enchant.equalsIgnoreCase("Punch")) {

            return Enchantment.ARROW_KNOCKBACK;

        } else if (enchant.equalsIgnoreCase("Sharpness")) {

            return Enchantment.DAMAGE_ALL;

        } else if (enchant.equalsIgnoreCase("BaneOfArthropods")) {

            return Enchantment.DAMAGE_ARTHROPODS;

        } else if (enchant.equalsIgnoreCase("Smite")) {

            return Enchantment.DAMAGE_UNDEAD;

        } else if (enchant.equalsIgnoreCase("Efficiency")) {

            return Enchantment.DIG_SPEED;

        } else if (enchant.equalsIgnoreCase("Unbreaking")) {

            return Enchantment.DURABILITY;

        } else if (enchant.equalsIgnoreCase("FireAspect")) {

            return Enchantment.FIRE_ASPECT;

        } else if (enchant.equalsIgnoreCase("Knockback")) {

            return Enchantment.KNOCKBACK;

        } else if (enchant.equalsIgnoreCase("Fortune")) {

            return Enchantment.LOOT_BONUS_BLOCKS;

        } else if (enchant.equalsIgnoreCase("Looting")) {

            return Enchantment.LOOT_BONUS_MOBS;

        } else if (enchant.equalsIgnoreCase("Respiration")) {

            return Enchantment.OXYGEN;

        } else if (enchant.equalsIgnoreCase("Protection")) {

            return Enchantment.PROTECTION_ENVIRONMENTAL;

        } else if (enchant.equalsIgnoreCase("BlastProtection")) {

            return Enchantment.PROTECTION_EXPLOSIONS;

        } else if (enchant.equalsIgnoreCase("FeatherFalling")) {

            return Enchantment.PROTECTION_FALL;

        } else if (enchant.equalsIgnoreCase("FireProtection")) {

            return Enchantment.PROTECTION_FIRE;

        } else if (enchant.equalsIgnoreCase("ProjectileProtection")) {

            return Enchantment.PROTECTION_PROJECTILE;

        } else if (enchant.equalsIgnoreCase("SilkTouch")) {

            return Enchantment.SILK_TOUCH;

        } else if (enchant.equalsIgnoreCase("Thorns")) {

            return Enchantment.THORNS;

        } else if (enchant.equalsIgnoreCase("AquaAffinity")) {

            return Enchantment.WATER_WORKER;

        } else {

            return null;

        }

    }

    public static Enchantment getEnchantmentPretty(String enchant) {

        if (enchant.equalsIgnoreCase("Power")) {

            return Enchantment.ARROW_DAMAGE;

        } else if (enchant.equalsIgnoreCase("Flame")) {

            return Enchantment.ARROW_FIRE;

        } else if (enchant.equalsIgnoreCase("Infinity")) {

            return Enchantment.ARROW_INFINITE;

        } else if (enchant.equalsIgnoreCase("Punch")) {

            return Enchantment.ARROW_KNOCKBACK;

        } else if (enchant.equalsIgnoreCase("Sharpness")) {

            return Enchantment.DAMAGE_ALL;

        } else if (enchant.equalsIgnoreCase("Bane Of Arthropods")) {

            return Enchantment.DAMAGE_ARTHROPODS;

        } else if (enchant.equalsIgnoreCase("Smite")) {

            return Enchantment.DAMAGE_UNDEAD;

        } else if (enchant.equalsIgnoreCase("Efficiency")) {

            return Enchantment.DIG_SPEED;

        } else if (enchant.equalsIgnoreCase("Unbreaking")) {

            return Enchantment.DURABILITY;

        } else if (enchant.equalsIgnoreCase("Fire Aspect")) {

            return Enchantment.FIRE_ASPECT;

        } else if (enchant.equalsIgnoreCase("Knockback")) {

            return Enchantment.KNOCKBACK;

        } else if (enchant.equalsIgnoreCase("Fortune")) {

            return Enchantment.LOOT_BONUS_BLOCKS;

        } else if (enchant.equalsIgnoreCase("Looting")) {

            return Enchantment.LOOT_BONUS_MOBS;

        } else if (enchant.equalsIgnoreCase("Respiration")) {

            return Enchantment.OXYGEN;

        } else if (enchant.equalsIgnoreCase("Protection")) {

            return Enchantment.PROTECTION_ENVIRONMENTAL;

        } else if (enchant.equalsIgnoreCase("Blast Protection")) {

            return Enchantment.PROTECTION_EXPLOSIONS;

        } else if (enchant.equalsIgnoreCase("Feather Falling")) {

            return Enchantment.PROTECTION_FALL;

        } else if (enchant.equalsIgnoreCase("Fire Protection")) {

            return Enchantment.PROTECTION_FIRE;

        } else if (enchant.equalsIgnoreCase("Projectile Protection")) {

            return Enchantment.PROTECTION_PROJECTILE;

        } else if (enchant.equalsIgnoreCase("Silk Touch")) {

            return Enchantment.SILK_TOUCH;

        } else if (enchant.equalsIgnoreCase("Thorns")) {

            return Enchantment.THORNS;

        } else if (enchant.equalsIgnoreCase("Aqua Affinity")) {

            return Enchantment.WATER_WORKER;

        } else {

            return null;

        }

    }

    public static DyeColor getDyeColor(String s) {

        if (s.equalsIgnoreCase("Black")) {

            return DyeColor.BLACK;

        } else if (s.equalsIgnoreCase("Blue")) {

            return DyeColor.BLUE;

        } else if (s.equalsIgnoreCase("Brown")) {

            return DyeColor.BROWN;

        } else if (s.equalsIgnoreCase("Cyan")) {

            return DyeColor.CYAN;

        } else if (s.equalsIgnoreCase("Gray")) {

            return DyeColor.GRAY;

        } else if (s.equalsIgnoreCase("Green")) {

            return DyeColor.GREEN;

        } else if (s.equalsIgnoreCase("LightBlue")) {

            return DyeColor.LIGHT_BLUE;

        } else if (s.equalsIgnoreCase("Lime")) {

            return DyeColor.LIME;

        } else if (s.equalsIgnoreCase("Magenta")) {

            return DyeColor.MAGENTA;

        } else if (s.equalsIgnoreCase("Orange")) {

            return DyeColor.ORANGE;

        } else if (s.equalsIgnoreCase("Pink")) {

            return DyeColor.PINK;

        } else if (s.equalsIgnoreCase("Purple")) {

            return DyeColor.PURPLE;

        } else if (s.equalsIgnoreCase("Red")) {

            return DyeColor.RED;

        } else if (s.equalsIgnoreCase("Silver")) {

            return DyeColor.SILVER;

        } else if (s.equalsIgnoreCase("White")) {

            return DyeColor.WHITE;

        } else if (s.equalsIgnoreCase("Yellow")) {

            return DyeColor.YELLOW;

        } else {

            return null;

        }

    }

    public static String getDyeString(DyeColor dc) {

        if (dc.equals(DyeColor.BLACK)) {
            return "Black";
        } else if (dc.equals(DyeColor.BLUE)) {
            return "Blue";
        } else if (dc.equals(DyeColor.BROWN)) {
            return "Brown";
        } else if (dc.equals(DyeColor.CYAN)) {
            return "Cyan";
        } else if (dc.equals(DyeColor.GRAY)) {
            return "Gray";
        } else if (dc.equals(DyeColor.GREEN)) {
            return "Green";
        } else if (dc.equals(DyeColor.LIGHT_BLUE)) {
            return "LightBlue";
        } else if (dc.equals(DyeColor.LIME)) {
            return "Lime";
        } else if (dc.equals(DyeColor.MAGENTA)) {
            return "Magenta";
        } else if (dc.equals(DyeColor.ORANGE)) {
            return "Orange";
        } else if (dc.equals(DyeColor.PINK)) {
            return "Pink";
        } else if (dc.equals(DyeColor.PURPLE)) {
            return "Purple";
        } else if (dc.equals(DyeColor.RED)) {
            return "Red";
        } else if (dc.equals(DyeColor.SILVER)) {
            return "Silver";
        } else if (dc.equals(DyeColor.WHITE)) {
            return "White";
        } else if (dc.equals(DyeColor.YELLOW)) {
            return "Yellow";
        } else {
            return null;
        }

    }

    public void snoop() {

        final String ip = getServer().getIp().trim();
        if (ip.isEmpty() || ip.startsWith("192") || ip.startsWith("localhost") || ip.startsWith("127")
                || ip.startsWith("0.0")) {
            return;
        }

        snoop_delete();
        snoop_insert();

    }

    private void snoop_insert() {

        try {

            final Date date = new Date(System.currentTimeMillis());
            final Timestamp stamp = new Timestamp(date.getTime());
            final String arguments = "arg1="
                    + getServer().getIp()
                    + "&arg2="
                    + ((Integer) getServer().getPort()).toString()
                    + "&arg3="
                    + URLEncoder.encode(getServer().getServerName().replaceAll("'", "''").replaceAll("\"", "''"),
                            "UTF-8") + "&arg4="
                    + URLEncoder.encode(getServer().getMotd().replaceAll("'", "''").replaceAll("\"", "''"), "UTF-8")
                    + "&arg5=" + quests.size() + "&arg6=" + (citizens != null ? "true" : "false") + "&arg7="
                    + URLEncoder.encode(stamp.toString(), "UTF-8");
            final URL url = new URL("http://www.blackvein.net/php/quests.php?" + arguments);
            final URLConnection yc = url.openConnection();
            final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equalsIgnoreCase("false")) {
                    Quests.printWarning("[Quests] An error occurred inserting data into the snooper database!");
                }
            }
            in.close();

        } catch (final Exception e) {
            Quests.printWarning("[Quests] An error occurred inserting data into the snooper database!");
        }

    }

    private void snoop_delete() {

        try {

            final String arguments = "arg1=" + getServer().getIp() + "&arg2="
                    + ((Integer) getServer().getPort()).toString();
            final URL url = new URL("http://www.blackvein.net/php/quests_del.php?" + arguments);
            final URLConnection yc = url.openConnection();
            final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equalsIgnoreCase("false")) {
                    Quests.printWarning("[Quests] An error occurred removing old data from the snooper database!");
                }
            }
            in.close();

        } catch (final Exception e) {
            Quests.printWarning("[Quests] An error occurred removing old data from the snooper database!");
        }

    }

    public boolean hasQuest(NPC npc, Quester quester) {

        for (final Quest quest: quests) {

            if (quest.npcStart != null && quester.completedQuests.contains(quest.name) == false) {

                if (quest.npcStart.getId() == npc.getId()) {
                    return true;
                }

            }

        }

        return false;
    }

    public static int getMCMMOSkillLevel(SkillType st, String player) {

        final McMMOPlayer mPlayer = UserManager.getPlayer(player);
        if (mPlayer == null) {
            return -1;
        }

        return mPlayer.getProfile().getSkillLevel(st);

    }

    public Hero getHero(String player) {

        final Player p = getServer().getPlayer(player);
        if (p == null) {
            return null;
        }

        return Quests.heroes.getCharacterManager().getHero(p);

    }

    public boolean testPrimaryHeroesClass(String primaryClass, String player) {

        final Hero hero = getHero(player);
        return hero.getHeroClass().getName().equalsIgnoreCase(primaryClass);

    }

    public boolean testSecondaryHeroesClass(String secondaryClass, String player) {

        final Hero hero = getHero(player);
        return hero.getHeroClass().getName().equalsIgnoreCase(secondaryClass);

    }

    public void updateData() {

        final YamlConfiguration config = new YamlConfiguration();
        final File dataFile = new File(getDataFolder(), "data.yml");

        try {
            config.load(dataFile);
            config.set("npc-gui", questNPCGUIs);
            config.save(dataFile);
        } catch (final Exception e) {
            Quests.log.severe("[Quests] Unable to update data file.");
            if (debug) {
                Quests.log.severe("[Quests] Error log:");
                e.printStackTrace();
            } else {
                Quests.log.severe("[Quests] Enable debug to view the error log.");
            }
            return;
        }

    }

}
