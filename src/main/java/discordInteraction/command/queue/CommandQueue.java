package discordInteraction.command.queue;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import discordInteraction.Main;
import discordInteraction.Utilities;
import discordInteraction.card.CardTargetless;
import discordInteraction.card.CardTriggerOnPlayerDamage;
import discordInteraction.command.*;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

import static discordInteraction.Utilities.sendMessageToUser;

public class CommandQueue {
    public Queue<QueuedCommandTargeted> targeted;
    public Queue<QueuedCommandTargetless> targetless;
    public Queue<QueuedCommandTriggerOnPlayerDamage> triggerOnPlayerDamage;

    public CommandQueue(){
        targeted = new Queue<QueuedCommandTargeted>();
        targetless = new Queue<QueuedCommandTargetless>();
        triggerOnPlayerDamage = new Queue<QueuedCommandTriggerOnPlayerDamage>();
    }

    public boolean hasQueuedCommands(){
        return targeted.hasAnotherCommand() || targetless.hasAnotherCommand();
    }

    public boolean userHasCommandQueued(User user) {
        for (QueuedCommandBase command : targeted.getCommands())
            if (command.getViewer() == user)
                return true;
        for (QueuedCommandBase command : targetless.getCommands())
            if (command.getViewer() == user)
                return true;
        for (QueuedCommandBase command : triggerOnPlayerDamage.getCommands())
            if (command.getViewer() == user)
                return true;
        return false;
    }

    public void handlePerTurnLogic(){
        while (targeted.hasAnotherCommand()){
            QueuedCommandTargeted command = targeted.getNextCommand();
            Result result = command.getCard().activate(command.getViewer(), AbstractDungeon.player, command.getTargets());
            if (result.hadResolved()){
                sendMessageToUser(command.getViewer(), "You successfully casted " + command.getCard().getName() + ". " + result.getWhatHappened());
            } else{
                sendMessageToUser(command.getViewer(), "You failed to cast " + command.getCard().getName() + ". " + result.getWhatHappened());
                Main.viewers.get(command.getViewer()).insertCard(command.getCard());
            }
            Main.battle.getViewerMonster(command.getViewer()).clearMoves();
        }

        while (targetless.hasAnotherCommand()){
            QueuedCommandTargetless command = targetless.getNextCommand();
            Result result = command.getCard().activate(command.getViewer(), AbstractDungeon.player);
            if (result.hadResolved()){
                sendMessageToUser( command.getViewer(), "You successfully casted " + command.getCard().getName() + ". " + result.getWhatHappened());
            } else{
                sendMessageToUser(command.getViewer(), "You failed to cast " + command.getCard().getName() + ". " + result.getWhatHappened());
                Main.viewers.get(command.getViewer()).insertCard(command.getCard());
            }
            Main.battle.getViewerMonster(command.getViewer()).clearMoves();
        }
    }

    public void handlePostBattleLogic() {
        while (targeted.hasAnotherCommand()){
            QueuedCommandTargeted command = targeted.getNextCommand();
            sendMessageToUser(command.getViewer(), "Your " + command.getCard().getName() +
                    " failed to cast before the battle ended, and has been refunded.");
        }

        while (targetless.hasAnotherCommand()){
            QueuedCommandBase command = targetless.getNextCommand();
            sendMessageToUser(command.getViewer(), "Your " + command.getCard().getName() +
                    " failed to cast before the battle ended, and has been refunded.");
        }

        // We don't need to do anything with these triggers except clear them.
        while (triggerOnPlayerDamage.hasAnotherCommand())
            triggerOnPlayerDamage.getNextCommand();
    }

    public int handleOnPlayerDamagedLogic(int incomingDamage, DamageInfo damageInfo) {
        if (damageInfo.type == DamageInfo.DamageType.HP_LOSS || !triggerOnPlayerDamage.hasAnotherCommand())
            return incomingDamage;

        int damageToReturn = incomingDamage;

        ArrayList<QueuedCommandTriggerOnPlayerDamage> randomized = (ArrayList<QueuedCommandTriggerOnPlayerDamage>) triggerOnPlayerDamage.getCommands().clone();
        for(QueuedCommandTriggerOnPlayerDamage command : randomized ){
            if (damageToReturn <= 0 || !Main.battle.hasViewerMonster(command.getViewer()))
                break;
            ResultWithInt result = command.getCard().handleOnPlayerDamageTrigger(incomingDamage, damageInfo, AbstractDungeon.player, command.getViewer());
            damageToReturn = result.getReturnInt();
            Utilities.sendMessageToUser(command.getViewer(), result.getWhatHappened());
        }

        return damageToReturn;
    }
}
