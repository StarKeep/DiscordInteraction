package discordInteraction;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import discordInteraction.card.Card;
import discordInteraction.command.QueuedCommandSingleTargeted;
import discordInteraction.command.QueuedCommandTargetless;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class Utilities {
    public static String lineBreak(int width) {
        return lineBreak(width, '-', '|');
    }

    // Mostly for output; put a dividing line.
    public static String lineBreak(int width, char filler, char edge) {
        StringBuilder sb = new StringBuilder(width);
        sb.append(edge);
        for (int x = 0; x < width - 2; x++)
            sb.append(filler);
        sb.append(edge);
        return sb.toString();
    }

    // Mostly for output. Center text in a requested width.
    public static String center(String s, int width) {
        s = s.trim();

        StringBuilder sb = new StringBuilder(width);

        for (int i = 0; i < (width - s.length()) / 2; i++) {
            sb.append(' ');
        }
        sb.append(s);
        while (sb.length() < width) {
            sb.append(' ');
        }

        return sb.toString();
    }

    // Mostly for output. Splits up a string into a List of strings, wich each string being smaller than the requested width.
    public static ArrayList<String> split(String s, int width) {
        ArrayList<String> result = new ArrayList<>();

        StringBuilder sb = new StringBuilder(width);
        for (String word : s.split(" ")) {
            if (sb.toString().trim().length() + word.trim().length() > width - 2) {
                result.add(sb.toString().trim());
                sb = new StringBuilder(width);
            }
            sb.append(word);
            sb.append(' ');
        }
        result.add(sb.toString().trim());

        return result;
    }

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

    // For output, spits out nicly formatted hands of cards.
    public static String[] getHandForViewer(User viewer) {
        if (!Main.viewers.containsKey(viewer))
            return new String[] {"You do not currently have a hand registered with the game. You can request a hand by typing !join in " + Main.channel.getName() + "."};

        Hand viewerHand = Main.viewers.get(viewer);

        if (viewerHand.getCards().size() == 0)
            return new String[] {"You do not currently have a hand. You'll draw cards when the streamer starts a new game, " +
                    "wins a battle, reaches a campfire, or starts a new act."};

        String[] result = new String[1 + ((viewerHand.getCards().size() - 1) / 3)];
        for(int x = 0; x < viewerHand.getCards().size(); x+=3) {
            int widthPerCard = 32;
            int width = 1 + ((widthPerCard - 1) * Math.min(3, viewerHand.getCards().size() - x));
            int internalWidthPerCard = widthPerCard - 4;

            StringBuilder sb = new StringBuilder(width);

            sb.append("```\n");

            sb.append(Utilities.lineBreak(width));
            sb.append("\n");

            sb.append("| ");

            for(int y = x; y < x+3 && y < viewerHand.getCards().size(); y++){
                Card card = viewerHand.getCards().get(y);
                sb.append(Utilities.center(card.getName(), 28));
                sb.append(" | ");
            }

            sb.append("\n");
            sb.append(Utilities.lineBreak(width));
            sb.append("\n");

            ArrayList<ArrayList<String>> descriptions = new ArrayList<>();
            for(int y = x; y < x+3 && y < viewerHand.getCards().size(); y++){
                Card card = viewerHand.getCards().get(y);
                descriptions.add(Utilities.split(card.getDescription(), internalWidthPerCard));
            }

            int max = 0;
            for (ArrayList<String> list : descriptions)
                max = Math.max(max, list.size());

            for (int y = 0; y < max; y++) {
                sb.append("| ");
                for (ArrayList<String> lines : descriptions) {
                    if (y < lines.size())
                        sb.append(Utilities.center(lines.get(y), internalWidthPerCard));
                    else
                        for (int z = 0; z < internalWidthPerCard; z++)
                            sb.append(' ');
                    sb.append(" | ");
                }
                sb.append("\n");
            }

            sb.append(Utilities.lineBreak(width));
            sb.append("\n");

            ArrayList<ArrayList<String>> flavors = new ArrayList<>();
            for(int y = x; y < x+3 && y < viewerHand.getCards().size(); y++){
                Card card = viewerHand.getCards().get(y);
                flavors.add(Utilities.split(card.getFlavorText(), internalWidthPerCard));
            }

            max = 0;
            for (ArrayList<String> list : flavors)
                max = Math.max(max, list.size());

            for (int y = 0; y < max; y++) {
                sb.append("| ");
                for (ArrayList<String> lines : flavors) {
                    if (y < lines.size())
                        sb.append(Utilities.center(lines.get(y), internalWidthPerCard));
                    else
                        for (int z = 0; z < internalWidthPerCard; z++)
                            sb.append(' ');
                    sb.append(" | ");
                }
                sb.append("\n");
            }

            sb.append(Utilities.lineBreak(width));

            sb.append("```");

            result[x/3] = sb.toString().trim();
        }
        return result;
    }

    // Format a viewer's hand and send it to them for ease of viewing.
    public static void sendHandToViewer(User viewer){
        String[] handFormatted = getHandForViewer(viewer);
        for(String line : handFormatted)
            sendMessageToUser(viewer, line);
    }

    // Send a simplified list of cards to a viewer. No formatting.
    public static String listHandForViewer(User viewer) {
        if (!Main.viewers.containsKey(viewer))
            return "You do not currently have a hand registered with the game. You can request a hand by typing !join in " + Main.channel.getName() + ".";

        StringBuilder sb = new StringBuilder();
        for (Card card : Main.viewers.get(viewer).getCards()) {
            sb.append(card.getName());
            sb.append(" : ");
            sb.append(card.getDescription());
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    // Get a comma separated list of enemy names in the current battle.
    public static String getListOfEnemies(boolean aliveOnly) {
        if (Main.battle.getBattleRoom() == null)
            return "No enemies.";

        StringBuilder sb = new StringBuilder();
        for(int x = 0; x < Main.battle.getBattleRoom().monsters.monsters.size(); x++){
            AbstractMonster monster = Main.battle.getBattleRoom().monsters.monsters.get(x);
            if (monster.isDeadOrEscaped() && aliveOnly)
                continue;

            sb.append("| ");
            sb.append(monster.name);
            sb.append(" [");
            sb.append(x + 1);
            sb.append("] |");

            sb.append("\n");
        }

        return sb.toString().replace('\n', ' ');
    }

    // Get a message showcasing all viewer cards to be resolved.
    public static String getUpcomingViewerCards() {
        if (!Main.hasAnotherTargetedCommand() && !Main.hasAnotherTargetlessCommand())
            return "No commands currently queued.";
        StringBuilder sb = new StringBuilder();
        for (QueuedCommandSingleTargeted command : Main.getQueuedTargetedCommands()) {
            sb.append(command.getViewer().getName());
            sb.append(" is going to cast ");
            sb.append(command.getCard().getName());
            sb.append( " on");
            String targets = "";
            for(AbstractMonster monster : command.getTargetsList())
                targets += " " + monster.name;
            sb.append(targets);
            sb.append(".\n");
        }
        for(QueuedCommandTargetless command : Main.getQueuedTargetlessCommands()){
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

    // Shortcut
    public static void applyPower(AbstractCreature target, AbstractPower power){
        ApplyPowerAction action = new ApplyPowerAction(target, target, power);
        AbstractDungeon.actionManager.addToBottom(action);
    }

    public static String getStartOfInProgressBattleMessage(){
        return "Enemies in the current room that is updated every minute. " +
                "You may request an updated list of enemies with !enemies in a private message." +
                "Their targeting IDs can be found in the brackets:\n";
    }
    public static String getEndOfBattleMessage(){
        return "This battle has ended; any cards in the queue have been refunded.\n";
    }
    public static String getStringFromArrayList(ArrayList<String> list, String divider){
        String result = list.get(0);
        for(int x = 1; x < list.size(); x++)
            result += divider + list.get(x);
        return  result;
    }
}
