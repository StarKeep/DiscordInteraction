package discordInteraction;

import basemod.interfaces.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ConfigUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.panels.AbstractPanel;
import discordInteraction.command.QueuedCommandTargetless;
import discordInteraction.command.QueuedCommandSingleTargeted;
import discordInteraction.command.Result;
import kobting.friendlyminions.helpers.BasePlayerMinionHelper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import basemod.BaseMod;
import basemod.interfaces.PreMonsterTurnSubscriber;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import sun.java2d.pipe.RenderingEngine;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

import static discordInteraction.Utilities.*;


@SpireInitializer
public class Main implements PreMonsterTurnSubscriber, PostBattleSubscriber, OnStartBattleSubscriber,
        PostCampfireSubscriber, StartActSubscriber, StartGameSubscriber {
    private static final String BOT_TOKEN = "botTokenHere";

    public static final Logger logger = LogManager.getLogger(Main.class.getName());

    public Main() {
        BaseMod.subscribe(this);

        // Make sure the config file exists. This is used to determine where this streamer's game should read and write messages to.
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

    // This should probably be moved out of main in the future.
    // Here is where we hold all queued commands by viewers until they are executed.
    // Designed to allow multithreaded input and output.
    private static Stack<QueuedCommandTargetless> queuedCommands;
    private static Stack<QueuedCommandSingleTargeted> queuedTargetedCommands;

    private static final Object targetlessLock = new Object();
    private static final Object targetedLock = new Object();

    public static ArrayList<QueuedCommandTargetless> getQueuedTargetlessCommands(){
        synchronized (targetlessLock){
            ArrayList<QueuedCommandTargetless> list = new ArrayList<QueuedCommandTargetless>();
            for(QueuedCommandTargetless command : queuedCommands)
                list.add(command);
            return list;
        }
    }
    public static ArrayList<QueuedCommandSingleTargeted> getQueuedTargetedCommands(){
        synchronized (targetedLock){
            ArrayList<QueuedCommandSingleTargeted> list = new ArrayList<QueuedCommandSingleTargeted>();
            for(QueuedCommandSingleTargeted command : queuedTargetedCommands)
                list.add(command);
            return list;
        }
    }

    public static void addToTargetlessQueue(QueuedCommandTargetless command){
        synchronized (targetlessLock){
            queuedCommands.add(command);
        }
    }
    public static void addToTargetedQueue(QueuedCommandSingleTargeted command){
        synchronized (targetedLock){
            queuedTargetedCommands.add(command);
        }
    }
    public static boolean hasAnotherTargetlessCommand(){
        synchronized (targetlessLock){
            return queuedCommands != null && !queuedCommands.isEmpty();
        }
    }
    public static boolean hasAnotherTargetedCommand(){
        synchronized (targetedLock){
            return queuedTargetedCommands != null && !queuedTargetedCommands.isEmpty();
        }
    }
    public static QueuedCommandTargetless getNextTargetlessCommand(){
        synchronized (targetlessLock){
            return queuedCommands.pop();
        }
    }
    public static QueuedCommandSingleTargeted getNextTargetedCommand(){
        synchronized (targetedLock){
            return queuedTargetedCommands.pop();
        }
    }

    public static void initialize() {
        // Setup our random generator.
        random = new Random();

        // Create our bot.
        int attempt = 0;
        while (attempt < 100 && bot == null) {
            try {
                bot = JDABuilder.createDefault(BOT_TOKEN).build().awaitReady();
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
        queuedCommands = new Stack<QueuedCommandTargetless>();
        queuedTargetedCommands = new Stack<QueuedCommandSingleTargeted>();
        new Main();
    }

    @Override
    public boolean receivePreMonsterTurn(AbstractMonster monster) {
        // Add any viewers that join mid fight.
        for(User viewer : viewers.keySet())
            if (!battle.hasViewerMonster(viewer))
                battle.addViewerMonster(viewer);

        // Send viewer commands. Start with targeted, so they hopefully don't miss their target
        while (hasAnotherTargetedCommand()){
            QueuedCommandSingleTargeted command = getNextTargetedCommand();
            Result result = command.getCard().activate(AbstractDungeon.player, command.getTargets());
            if (result.hadResolved()){
                viewers.get(command.getViewer()).removeCard(command.getCard());
                sendMessageToUser(command.getViewer(), "You successfully casted " + command.getCard().getName() + ". " + result.getWhatHappened());
            } else{
                sendMessageToUser(command.getViewer(), "You failed to cast " + command.getCard().getName() + ". " + result.getWhatHappened());
            }
            battle.getViewerMonster(command.getViewer()).clearMoves();
        }

        while (hasAnotherTargetlessCommand()){
            QueuedCommandTargetless command = getNextTargetlessCommand();
            Result result = command.getCard().activate(AbstractDungeon.player);
            if (result.hadResolved()){
                viewers.get(command.getViewer()).removeCard(command.getCard());
                sendMessageToUser( command.getViewer(), "You successfully casted " + command.getCard().getName() + ". " + result.getWhatHappened());
            } else{
                sendMessageToUser(command.getViewer(), "You failed to cast " + command.getCard().getName() + ". " + result.getWhatHappened());
            }
            battle.getViewerMonster(command.getViewer()).clearMoves();
        }

        // Update our battle message to remove our commands now that they've been executed.
        Main.battle.setLastBattleUpdate(LocalDateTime.now());
        Main.channel.retrieveMessageById(Main.battle.getBattleMessageID()).queue((message -> {
            message.editMessage(Utilities.getEndOfBattleMessage() + Utilities.getListOfEnemies(false) +
                    "\n" + Utilities.getUpcomingViewerCards()).queue();
        }));

        return true;
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        // Let the rest of our program know we're in a fight.
        battle.setIsInBattle(true);
        battle.setBattleRoom(abstractRoom);

        // Start the battle information message in the bot channel.
        if (channel != null) {
            channel.sendMessage(Utilities.getStartOfInProgressBattleMessage() + Utilities.getListOfEnemies(true)).queue((message -> {
                battle.setBattleMessageID(message.getId());
            }));
        }

        // Spawn in viewers.
        for (User user : viewers.keySet()) {
            battle.addViewerMonster(user);
            sendHandToViewer(user);
            sendMessageToUser(user, "A new fight has begun!");
        }
    }

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        // Victory! Let all viewers draw 1 more card.
        for(User viewer : viewers.keySet()){
            viewers.get(viewer).draw(1);
            sendHandToViewer(viewer);
            sendMessageToUser(viewer, "You have drawn a new card due to victory in battle.");
        }

        // Refund any cards that weren't cast in time due to the player rudely winning the fight.
        while (hasAnotherTargetedCommand()){
            QueuedCommandSingleTargeted command = getNextTargetedCommand();
            sendMessageToUser(command.getViewer(), "Your " + command.getCard().getName() +
                    " failed to cast before the battle ended, and has been refunded.");
        }

        while (hasAnotherTargetlessCommand()){
            QueuedCommandTargetless command = getNextTargetlessCommand();
            sendMessageToUser(command.getViewer(), "Your " + command.getCard().getName() +
                    " failed to cast before the battle ended, and has been refunded.");
        }

        // End the battle; edit the battle message to showcase the end result.
        battle.setLastBattleUpdate(LocalDateTime.now());
        channel.retrieveMessageById(battle.getBattleMessageID()).queue((message -> {
            message.editMessage(Utilities.getEndOfBattleMessage()).queue();
            battle.setBattleMessageID(null);
        }));

        // Remove all of our stored viewers.
        battle.removeAllViewerMonsters();

        // Let the rest of the program know the fight ended.
        battle.setIsInBattle(true);
        battle.setBattleRoom(null);
    }

    @Override
    public boolean receivePostCampfire() {
        for(User viewer : viewers.keySet()){
            viewers.get(viewer).draw(3 + (AbstractDungeon.actNum / 2));
            sendHandToViewer(viewer);
            sendMessageToUser(viewer, "You have drawn new cards at the campfire.");
        }

        return true;
    }

    @Override
    public void receiveStartAct() {
        for(User viewer : viewers.keySet()){
            viewers.get(viewer).drawNewHand(5 + (AbstractDungeon.actNum * 2));
            sendHandToViewer(viewer);
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
    }
}