package discordInteraction;

import basemod.interfaces.*;
import com.evacipated.cardcrawl.modthespire.lib.ConfigUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import discordInteraction.command.queue.CommandQueue;
import kobting.friendlyminions.helpers.MinionConfigHelper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import basemod.BaseMod;
import basemod.interfaces.PreMonsterTurnSubscriber;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static discordInteraction.Utilities.*;


@SpireInitializer
public class Main implements PreMonsterTurnSubscriber, PostBattleSubscriber, OnStartBattleSubscriber,
        PostCampfireSubscriber, StartActSubscriber, StartGameSubscriber, OnPlayerDamagedSubscriber {
    public static final Logger logger = LogManager.getLogger(Main.class.getName());

    public Main() {
        BaseMod.subscribe(this);

        // Make sure the config files exist. If they don't, create some defaults.
        String dir = ConfigUtils.CONFIG_DIR + File.separator + "DiscordInteraction" + File.separator;
        File configDir = new File(dir);
        if (!configDir.exists())
            configDir.mkdirs();
        File config = new File(dir + "config.ini");
        if (!config.exists())
            try {
                config.createNewFile();
                FileWriter init = new FileWriter(config);
                init.write("ServerName:ChannelName");
                init.close();
            } catch (Exception e) { }
        File tokenConfig = new File(dir + "botToken.ini");
        if (!tokenConfig.exists())
            try {
                tokenConfig.createNewFile();
                FileWriter init = new FileWriter(tokenConfig);
                init.write("botTokenHere");
                init.close();
            } catch (Exception e) { }
    }

    // Various important aspects.
    public static JDA bot;
    public static MessageChannel channel;
    public static LocalDateTime lastMessageSent;
    public static Random random;

    // Holds a list of viewers along with their respective hands.
    public static HashMap<User, Hand> viewers;
    // Holds all cards split up into their respective card types.
    public static Deck deck;
    // Holds current battle information.
    public static Battle battle;
    // Holds current viewer command information.
    public static CommandQueue commandQueue;

    public static void initialize() {
        // Setup our random generator.
        random = new Random();

        // Create our bot.
        int attempt = 0;
        while (attempt < 100 && bot == null) {
            try {
                String dir = ConfigUtils.CONFIG_DIR + File.separator + "DiscordInteraction" + File.separator;
                File configDir = new File(dir);
                if (!configDir.exists())
                    configDir.mkdirs();
                File tokenConfig = new File(dir + "botToken.ini");
                String data = new String(Files.readAllBytes(tokenConfig.toPath())).trim();
                bot = JDABuilder.createDefault(data).build().awaitReady();
                bot.addEventListener(new MessageListener());
            } catch (Exception e) {
                bot = null;
            }
            attempt++;
        }

        if (bot == null)
            logger.debug("Failed to connect to discord bot after 100 attempts.");

        // Connect our bot to the requested server.
        attempt = 0;
        while (attempt < 100 && channel == null) {
            try {
                String dir = ConfigUtils.CONFIG_DIR + File.separator + "DiscordInteraction" + File.separator;
                File configDir = new File(dir);
                if (!configDir.exists())
                    configDir.mkdirs();
                Path config = Paths.get(dir + "config.ini");
                String[] data = new String(Files.readAllBytes(config)).split(":");
                List<Guild> guilds = bot.getGuildsByName(data[0], true);
                for (Guild guild : guilds) {
                    List<TextChannel> channels = guild.getTextChannelsByName(data[1], true);
                    if (channels.size() > 0) {
                        channel = channels.get(0);
                        break;
                    }
                }
                if (channel != null)
                    break;
                attempt++;
            } catch (Exception e) {
                attempt++;
            }
        }

        if (channel == null) {
            try {
                logger.debug("Failed to connect to channel. Please check your config file.");
                String dir = ConfigUtils.CONFIG_DIR + File.separator + "DiscordInteraction" + File.separator;
                File configDir = new File(dir);
                if (!configDir.exists())
                    configDir.mkdirs();
                Path config = Paths.get(dir + "config.ini");
                String[] data = new String(Files.readAllBytes(config)).split(":");
                logger.debug("Server Name:" + data[0]);
                logger.debug("Chanel Name:" + data[1]);

            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        }

        // Setup the mod.
        battle = new Battle();
        lastMessageSent = LocalDateTime.now();
        viewers = new HashMap<User, Hand>();
        commandQueue = new CommandQueue();
        new Main();
    }

    @Override
    public boolean receivePreMonsterTurn(AbstractMonster monster) {
        // Add any viewers that join mid fight.
        for(User viewer : viewers.keySet())
            if (!battle.hasViewerMonster(viewer) && battle.canUserSpawnIn(viewer))
                battle.addViewerMonster(viewer);

        // Send viewer commands. Start with targeted, so they hopefully don't miss their target
        commandQueue.handlePerTurnLogic();

        // Update our battle message to remove our commands now that they've been executed.
        Main.battle.setLastBattleUpdate(LocalDateTime.now());
        Main.channel.retrieveMessageById(Main.battle.getBattleMessageID()).queue((message -> {
            message.editMessage(Utilities.getEndOfBattleMessage() + Utilities.getListOfEnemies(false)).queue();
        }));

        return true;
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        // Let the rest of our program know we're in a fight.
        if (channel != null) {
            channel.sendMessage(Utilities.getStartOfInProgressBattleMessage() + Utilities.getListOfEnemies(true)).queue((message -> {
                battle.startBattle(abstractRoom, message.getId());
            }));
        }
    }

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        // Victory! Let all viewers draw 1 more card.
        for(User viewer : viewers.keySet()){
            viewers.get(viewer).draw(1, 1);
            listHandForViewer(viewer);
            sendMessageToUser(viewer, "A battle was won! You have drawn 1 random and 1 basic card, hand size permitting.");
        }

        // Refund any cards that weren't cast in time due to the player rudely winning the fight.
        commandQueue.handlePostBattleLogic();

        // Update our battle information.
        battle.endBattle();
    }

    @Override
    public boolean receivePostCampfire() {
        for(User viewer : viewers.keySet()){
            viewers.get(viewer).draw(3 + (AbstractDungeon.actNum / 2), 2);
            listHandForViewer(viewer);
            sendMessageToUser(viewer, "You have drawn new cards at the campfire, hand size permitting.");
        }

        return true;
    }

    @Override
    public void receiveStartAct() {
        for(User viewer : viewers.keySet()){
            viewers.get(viewer).drawNewHand(5 + (AbstractDungeon.actNum * 2), 2);
            listHandForViewer(viewer);
            viewers.get(viewer).drawBasics(2);
            sendMessageToUser(viewer, "You have drawn a new hand of cards due to a new game or new act.");
        }
    }

    @Override
    public void receiveStartGame() {
        // Register our cards.
        if (deck == null)
            deck = new Deck();

        if (channel != null)
            channel.sendMessage(AbstractDungeon.player.name + " has started a game in this channel! Type !join to join in.").queue();

        MinionConfigHelper.MinionAttackTargetChance = 0;
    }

    @Override
    public int receiveOnPlayerDamaged(int incomingDamage, DamageInfo damageInfo) {
        return commandQueue.handleOnPlayerDamagedLogic(incomingDamage, damageInfo);
    }
}