package discordInteraction.util;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import discordInteraction.FlavorType;
import discordInteraction.Main;
import discordInteraction.card.AbstractCard;
import discordInteraction.command.QueuedCommandTargeted;
import discordInteraction.command.QueuedCommandTargetless;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Map;

public class Output {
    // Shortcut to send messages to a user.
    public static void sendMessageToUser(User user, String message) {
        user.openPrivateChannel().queue((channel) ->
        {
            sendMessageToUser(channel, message);
        });
    }

    // Shortcut to send message to a private channel.
    public static void sendMessageToUser(PrivateChannel channel, String message) {
        channel.sendMessage(message).queue();
    }

    // Send a simplified list of cards to a viewer. No formatting.
    public static String listHandForViewer(User viewer) {
        if (!Main.viewers.containsKey(viewer))
            return "You do not currently have a hand registered with the game. You can request a hand by typing !join in " + Main.bot.channel.getName() + ".";

        StringBuilder sb = new StringBuilder();
        for (AbstractCard card : Main.viewers.get(viewer).getCards()) {
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

    // List all FlavorTypes currently supported by the game, excluding basic.
    public static String getFlavorList() {
        String output = "The current flavors are:";
        for (FlavorType flavor : FlavorType.values())
            if (flavor == FlavorType.basic)
                continue;
            else
                output += " " + flavor.toString();
        return output;
    }

    public static String getStartOfInProgressBattleMessage() {
        return "Enemies in " + AbstractDungeon.player.name + "'s current room that is updated every turn.\n" +
                "If you have not yet joined in, you can type !join to join the game!\n" +
                "You may request an updated list of enemies with !target in a private message.\n" +
                "Their targeting IDs can be found in the brackets:\n";
    }

    public static String getEndOfBattleMessage() {
        return "This battle has ended; any cards in the queue have been refunded.\n";
    }
}
