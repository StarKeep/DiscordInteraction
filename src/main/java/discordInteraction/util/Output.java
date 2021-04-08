package discordInteraction.util;

import com.megacrit.cardcrawl.core.AbstractCreature;
import discordInteraction.Main;
import discordInteraction.card.AbstractCard;
import discordInteraction.command.QueuedCommandTargeted;
import discordInteraction.command.QueuedCommandTargetless;
import discordInteraction.viewer.Viewer;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Map;

public class Output {
    // Shortcut to send messages to a user.
    public static void sendMessageToUser(User user, String message){
        user.openPrivateChannel().queue((channel) ->
        {
            sendMessageToChannel(channel, message);
        });
    }
    public static void sendMessageToViewer(Viewer viewer, String message){
        viewer.openPrivateChannel().queue((channel) ->
        {
            sendMessageToChannel(channel, message);
        });
    }

    // Shortcut to send message to a private channel.
    public static void sendMessageToChannel(MessageChannel channel, String message) {
        channel.sendMessage(message).queue();
    }

    // Send a simplified list of cards to a viewer. No formatting.
    public static String listHandForViewer(Viewer viewer) {
        if (!Main.viewers.contains(viewer))
            return "You do not currently have a hand registered with the game. You can request a hand by typing !join in " + Main.bot.channel.getName() + ".";

        StringBuilder sb = new StringBuilder();
        for (AbstractCard card : viewer.getCards()) {
            sb.append(card.getName());
            sb.append(" : ");
            sb.append(card.getDescriptionForViewerDisplay());
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    // Get a comma separated formatted list of targets.
    public static String getTargetListForDisplay(boolean aliveOnly) {
        if (Main.battle.getBattleRoom() == null)
            return "Battle has not yet started.";

        if (Main.battle.getTargets(aliveOnly).size() == 0)
            return "Battle currently has no valid targets.";

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, AbstractCreature> target : Main.battle.getTargets(aliveOnly).entrySet()) {
            if (target.getValue().isDeadOrEscaped() && aliveOnly)
                continue;

            sb.append("| ");
            sb.append(target.getValue().name);
            sb.append(" [");
            sb.append(target.getKey());
            sb.append("] |");

            sb.append("\n");
        }

        return sb.toString().replace('\n', ' ');
    }

    // Get a message showcasing all viewer cards to be resolved.
    public static String getUpcomingViewerCards() {
        if (!Main.commandQueue.hasQueuedCommands())
            return "No commands currently queued.";
        StringBuilder sb = new StringBuilder();
        for (QueuedCommandTargeted command : Main.commandQueue.targeted.getCommands()) {
            sb.append(command.getViewer().getName());
            sb.append(" is going to cast ");
            sb.append(command.getCard().getName());
            sb.append(" on");
            String targets = "";
            for (AbstractCreature target : command.getTargetsList())
                targets += " " + target.name;
            sb.append(targets);
            sb.append(".\n");
        }
        for (QueuedCommandTargetless command : Main.commandQueue.targetless.getCommands()) {
            sb.append(command.getViewer().getName());
            sb.append(" is going to cast ");
            sb.append(command.getCard().getName());
            sb.append(".\n");
        }

        return sb.toString().replace('\n', ' ');
    }

    // List all classes in the game.
    public static String getViewerClassesList(boolean listOnly){
        StringBuilder sb = new StringBuilder();
        if (!listOnly)
            sb.append("Classes currently available: ");

        ArrayList<String> viewerClasses = Main.deck.getViewerClasses();
        switch (viewerClasses.size()){
            case 0:
                sb.append("None");
                break;
            case 1:
                sb.append(viewerClasses.get(0));
                break;
            default:
                sb.append(Formatting.getStringFromArrayList(viewerClasses, ", "));
                break;
        }

        return sb.toString();
    }

    public static String getStartOfInProgressBattleMessage() {
        return "A battle is in progress!\n" +
                "If you have not yet joined in, you can type !join to join the game!\n" +
                "You may request an updated list of enemies with !targets in a private message.";
    }

    public static String getEndOfBattleMessage() {
        return "This battle has ended; any cards in the queue have been refunded.\n";
    }

    // An 'all in one' display of sorts.
    public static String getStatusForViewer(Viewer viewer){
        StringBuilder sb = new StringBuilder();
        sb.append(Formatting.putInCodeBlock("Cards"));
        sb.append(listHandForViewer(viewer));
        if (Main.battle.isInBattle()) {
            sb.append(Formatting.putInCodeBlock("Targets [TargetingID]"));
            sb.append(getTargetListForDisplay(true));
            sb.append(Formatting.putInCodeBlock("Status"));
            if (Main.battle.hasLivingViewerMonster(viewer)) {
                AbstractCreature viewerMonster = Main.battle.getViewerMonster(viewer);
                sb.append("Health: ");
                sb.append(viewerMonster.currentHealth);
                sb.append('/');
                sb.append(viewerMonster.maxHealth);
                sb.append("\n");
                sb.append("Command Queued: ");
                sb.append(Main.commandQueue.viewerHasCommandQueued(viewer));
                sb.append("\n");
            } else if (Main.battle.canViewerSpawnIn(viewer))
                sb.append("You have not yet spawned in, and should at the start of the next turn.");
            else
                sb.append("You are currently dead, and are unable to play any cards until the next battle.");
        } else{
            sb.append(Formatting.putInCodeBlock("Status"));
            sb.append("There is currently no battle in progress.");
        }
        return sb.toString();
    }
}
