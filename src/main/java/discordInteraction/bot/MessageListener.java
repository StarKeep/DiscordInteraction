package discordInteraction.bot;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import discordInteraction.Main;
import discordInteraction.util.Formatting;
import discordInteraction.util.Output;
import discordInteraction.card.AbstractCard;
import discordInteraction.card.targeted.AbstractCardTargeted;
import discordInteraction.card.targetless.AbstractCardTargetless;
import discordInteraction.card.triggered.onPlayerDamage.AbstractCardTriggeredOnPlayerDamage;
import discordInteraction.command.*;
import discordInteraction.viewer.Viewer;
import kobting.friendlyminions.monsters.MinionMove;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;

// This big ugly fella is responsible for just about everything related to viewer input.
public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;

        // Attempt to load their existing viewer object, if it exists.
        Viewer viewer = Main.getViewerFromUserOrNull(event.getAuthor());

        // Split the message up into chunks for easy processing.
        String[] parts = event.getMessage().getContentDisplay().split(" ");

        // Handle any input from the main discord channel.
        // We want to restrict our output as much as possible in order to keep it as clean as possible.
        if (Main.bot.channel != null && event.getChannel().getId().equalsIgnoreCase(Main.bot.channel.getId())) {
            switch (ChatCommandType.valueOf(event.getMessage().getContentDisplay().substring(1).toLowerCase())) {
                case join: // Either initialize a hand for them, or tell them that they're already in.
                    handleJoinCommand(event.getAuthor(), parts);
                    break;
                case leave: // Get them out of the game, if they're in it.
                    if (viewer == null)
                        Output.sendMessageToUser(event.getAuthor(), "You have not joined " + AbstractDungeon.player.name + "'s game.");
                    else {
                        Main.viewers.remove(viewer);
                        if (Main.battle.isInBattle() && Main.battle.hasViewerMonster(viewer));
                            Main.battle.removeViewerMonster(viewer, false);
                        Output.sendMessageToUser(event.getAuthor(), "You have left " + AbstractDungeon.player.name + "'s game.");
                    }
                    break;
                default: // If they enter ANYTHING else, and its been 5 minutes since our last notice, let the chat know some basic information.
                    if (LocalDateTime.now().minusMinutes(5).isAfter(Main.bot.lastMessageSent)) {
                        Main.bot.lastMessageSent = LocalDateTime.now();
                        if (Main.deck == null)
                            event.getChannel().sendMessage("The game has not yet been started. Keep an eye out for a notification when the game can be joined. Please message me !help for any additional assistance.").queue();
                        else
                            event.getChannel().sendMessage("The only supported command in public chat is !join ClassName, in order to join a currently active game. Please message me !help for any additional assistance.").queue();
                    }
                    break;

            }
        }

        // Handle any input we receive via private messages.
        // Ignore any if they're from users that we don't already have registered.
        // This is primarily in case more than one game of this is ever occurring so that the bot won't mix inputs up between them.
        if (event.getChannelType() == ChannelType.PRIVATE && viewer != null) {
            try {
                switch (ChatCommandType.valueOf(parts[0].substring(1).toLowerCase())) {
                    case help:
                        handleHelpCommand(viewer);
                        break;
                    case other:
                    case extra:
                        handleOtherCommand(viewer);
                        break;
                    case debugmeafullhandofcards: // Shh.
                        viewer.drawNewHand(10, 2);
                        Output.sendMessageToViewer(viewer, Output.listHandForViewer(viewer));
                        break;
                    case hand: // Show them their cards.
                    case cards:
                        Output.sendMessageToViewer(viewer, Output.listHandForViewer(viewer));
                        break;
                    case cast:
                    case play:
                        handlePlayCommand(viewer, parts);
                        break;
                    case targets: // Give them a list of targets.
                        Output.sendMessageToViewer(viewer, Output.getTargetListForDisplay(true));
                        break;
                    case leave:
                        Output.sendMessageToViewer(viewer, "Sorry, !leave must be used in the channel of the game in question.");
                        break;
                    case status: // Display all information they may need for the turn.
                        Output.sendMessageToViewer(viewer, Output.getStatusForViewer(viewer));
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException ee) {
                Output.sendMessageToViewer(viewer, "Unknown command. Please type !help for assistance.");
            }
        }
    }

    private void handlePlayCommand(Viewer viewer, String[] parts) {
        // If they join middle battle; they have to wait to spawn in first.
        if (!Main.battle.hasViewerMonster(viewer)){
            if (Main.battle.canViewerSpawnIn(viewer))
                Output.sendMessageToViewer(viewer, "You have not yet spawned into the game, please wait until the next turn.");
            else
                Output.sendMessageToViewer(viewer, "You cannot play cards until the next battle begins.");
            return;
        }

        // If they send us only !cast or !play, we can't really do much.
        if (parts.length < 2) {
            Output.sendMessageToViewer(viewer, "Unsupported command. You must include at least a card name or number.");
            return;
        }

        ArrayList<String> failed = new ArrayList<String>(); // Error handling, we check this at a few points in order to stop processing and tell the user that something was wrong.

        if (!Main.battle.isInBattle())
            failed.add("A battle is not currently in progress");

        if (failed.size() > 0) {
            Output.sendMessageToViewer(viewer, "Command failed, reason(s): " + Formatting.getStringFromArrayList(failed, " "));
            return;
        }

        // Only one command allowed per turn.
        if (Main.commandQueue.viewerHasCommandQueued(viewer))
            failed.add("You have already queued up a card this turn");

        if (failed.size() > 0) {
            Output.sendMessageToViewer(viewer, "Command failed, reason(s): " + Formatting.getStringFromArrayList(failed, " "));
            return;
        }

        // Setup some basic variables to hold their arguments.
        AbstractCard card = null;
        ArrayList<AbstractCreature> targets = new ArrayList<AbstractCreature>();
        String rawCardName = "";

        // If they are using the "" surrounding method, we need different logic.
        Boolean isMultiPartCardName = parts[1].charAt(0) == '"';

        if (isMultiPartCardName) {
            // Record the index of the portion with the trailing ".
            // This is used below.
            int lastCardIndex = -1;
            for (int x = 1; x < parts.length && lastCardIndex < 0; x++) {
                if (parts[x].charAt(parts[x].length() - 1) == '"')
                    lastCardIndex = x;
            }

            // Stick the first piece on via substring to remove the initial ".
            rawCardName += parts[1].substring(1).trim();

            for (int x = 2; x < lastCardIndex; x++)
                rawCardName += parts[x].trim();

            // Remove the last ".
            rawCardName += parts[lastCardIndex].substring(0, parts[lastCardIndex].length() - 1).trim();

            // Confirm that they have a card with that name in their hand.
            card = viewer.getFirstCardByName(rawCardName);

            if (card == null)
                failed.add("Failed to find card of name " + rawCardName);

            // This is why we needed to get where the name part ended, to figure out where targeting begins.
            // If there is more content after the name, start targeting logic.
            if (lastCardIndex + 1 < parts.length && card instanceof AbstractCardTargeted) {
                // Get their target(s). Since we're forcing them to use comma separation, its quite simple.
                for (String raw : parts[lastCardIndex].split(",")) {
                    int id = -1;
                    try {
                        id = Integer.parseInt(raw);
                    } catch (Exception ee) {
                    }
                    Result targetingResult = Main.battle.isTargetValid(id, ((AbstractCardTargeted) card).getTargetTypes());
                    if (!targetingResult.wasSuccessful())
                        failed.add(targetingResult.getWhatHappened());
                    else
                        targets.add(Main.battle.getTargetByID(id));
                }
            }
        } else {
            // They're using the full name without spaces. Just get it nice and simple like.
            rawCardName = parts[1];

            // Confirm that they have a card with that name in their hand.
            card = viewer.getFirstCardByName(rawCardName);

            if (card == null)
                failed.add("Failed to find card of name " + rawCardName);

            // If there is more content after the name, start targeting logic.
            if (2 < parts.length && card instanceof AbstractCardTargeted) {
                // Get their target(s). Since we're forcing them to use comma separation, its quite simple.
                for (String raw : parts[2].split(",")) {
                    int id = -1;
                    try {
                        id = Integer.parseInt(raw);
                    } catch (Exception ee) {
                    }
                    Result targetingResult = Main.battle.isTargetValid(id, ((AbstractCardTargeted) card).getTargetTypes());
                    if (!targetingResult.wasSuccessful())
                        failed.add(targetingResult.getWhatHappened());
                    else
                        targets.add(Main.battle.getTargetByID(id));
                }
            }
        }

        if (failed.size() > 0) {
            Output.sendMessageToViewer(viewer, "Command failed, reason(s): " + Formatting.getStringFromArrayList(failed, " "));
            return;
        }

        // Make sure it isn't violating card targeting requirements.
        if (targets.size() > 0){
            if (!(card instanceof AbstractCardTargeted))
                failed.add("Attempted to add targets to a targetless card");
            else{
                AbstractCardTargeted cardT = (AbstractCardTargeted) card;
                if (targets.size() > cardT.getTargetCountMax() || targets.size() < cardT.getTargetCountMin())
                    failed.add("Invalid number of targets, " + card.getName() + " supports " +
                            cardT.getTargetCountMin() + " to " + cardT.getTargetCountMax() + " targets");
            }
        }

        if (failed.size() > 0) {
            Output.sendMessageToViewer(viewer, "Command failed, reason(s): " + Formatting.getStringFromArrayList(failed, " "));
            return;
        }

        // Queue the command based on its type.
        switch (card.getViewerCardType()){
            case targeted:
                if (targets.size() > 0)
                    Main.commandQueue.targeted.add(new QueuedCommandTargeted(viewer, (AbstractCardTargeted) card, targets));
                else
                    Main.commandQueue.targetless.add(new QueuedCommandTargetless(viewer, (AbstractCardTargetless) card));
                break;
            case targetless:
                Main.commandQueue.targetless.add(new QueuedCommandTargetless(viewer, (AbstractCardTargetless) card));
                break;
            case triggerOnPlayerDamage:
                Main.commandQueue.triggerOnPlayerDamage.add(new QueuedCommandTriggered(viewer, (AbstractCardTriggeredOnPlayerDamage) card));
                break;
            default:
                return;
        }

        Output.sendMessageToViewer(viewer, card.getName() + " has been queued up successfully.");

        // Remove the actual card from their hand.
        viewer.removeCard(card);

        // Update their monster display to showcase their card's primary flavor.
        MinionMove move = new MinionMove(card.getName(), Main.battle.getViewerMonster(viewer),
                AbstractCard.getTextureForCard(card.getClass()), card.getDescriptionForGameDisplay(), () -> {
        });

        Main.battle.getViewerMonster(viewer).addMove(move);
        Main.battle.getViewerMonster(viewer).rollMove();

        // Update our battle message to showcase the newly queued command.
        Main.bot.channel.retrieveMessageById(Main.battle.getBattleMessageID()).queue((message -> {
            message.editMessage(Output.getStartOfInProgressBattleMessage() +
                    "\n" + Output.getUpcomingViewerCards()).queue();
        }));
    }



    private void handleHelpCommand(Viewer viewer) {
        Output.sendMessageToViewer(viewer, "!status - Show your hand, targets, and your current health.\n" +
                "!(play/cast) card target - (Examples: !play \"A Cool Spell\" 1, " +
                "!cast ACoolSpell 1,3) - Play a card from your hand. Card should " +
                "be the name of the card with either no spaces or surrounded by \". " +
                "Target must be the numeric identifier of a monster, or multiple " +
                "comma separated numeric identifiers.\n" +
                "!flavors - Show all flavors that you currently allow.\n" +
                "!addflavor - Add a flavor to your allowed list.\n" +
                "!removeflavor - Remove a flavor from your allowed list.\n" +
                "!leave - Remove you from an active game. Must be used in the channel of an active game.\n" +
                "!(extra/other) - Get information on some lesser used commands."
        );
    }

    private void handleOtherCommand(Viewer viewer){
        Output.sendMessageToViewer(viewer, "!hand/cards - Show your hand.\n" +
                "!targets - Outputs an updated list of targets and their targeting ids, which are in square brackets.\n" +
                "!getallflavors - Show all card flavors currently in game.\n"
        );
    }

    private void handleJoinCommand(User user, String[] parts){
        if (Main.getViewerFromUserOrNull(user) == null) {
            if (Main.deck == null) {
                Output.sendMessageToUser(user, "Sorry, the game has yet to be started. Please wait for a notice to join.");
                return;
            }
            String requestedClass = null;
            if (parts.length < 2){
                // Pick random class.
                requestedClass = Main.deck.getViewerClasses().get(Main.random.nextInt(Main.deck.getViewerClasses().size()));
            } else {
                // Attempt to find requested class.
                requestedClass = parts[1];
                boolean found = false;
                for (String existingClass : Main.deck.getViewerClasses())
                    if (requestedClass.equalsIgnoreCase(existingClass)) {
                        requestedClass = existingClass;
                        found = true;
                        break;
                    }
            }
            Viewer viewer = new Viewer(user, requestedClass);
            Main.viewers.add(viewer);
            handleHelpCommand(viewer);
            if (Main.battle.isInBattle()){
                Output.sendMessageToViewer(viewer, Output.getStatusForViewer(viewer));
                Output.sendMessageToViewer(viewer, "A battle is currently occuring, you will appear in game at the start of the next turn.");
            }
        } else
            Output.sendMessageToUser(user,
                    "You have already joined a game, type !help in this private chat for additional information.");
    }
}
