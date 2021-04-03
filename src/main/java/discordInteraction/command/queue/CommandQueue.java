package discordInteraction.command.queue;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import discordInteraction.Main;
import discordInteraction.Utilities;
import discordInteraction.command.*;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

import static discordInteraction.Utilities.sendMessageToUser;

public class CommandQueue {
    public Queue<QueuedCommandTargeted> targeted;
    public Queue<QueuedCommandTargetless> targetless;
    public Queue<QueuedCommandTriggerOnPlayerDamage> continousTriggerOnPlayerDamage;
    public Queue<QueuedCommandTriggerOnPlayerDamage> oneTimeTriggerOnPlayerDamage;

    public CommandQueue(){
        targeted = new Queue<QueuedCommandTargeted>();
        targetless = new Queue<QueuedCommandTargetless>();
        continousTriggerOnPlayerDamage = new Queue<QueuedCommandTriggerOnPlayerDamage>();
        oneTimeTriggerOnPlayerDamage = new Queue<QueuedCommandTriggerOnPlayerDamage>();
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
        for (QueuedCommandBase command : continousTriggerOnPlayerDamage.getCommands())
            if (command.getViewer() == user)
                return true;
        for (QueuedCommandBase command : oneTimeTriggerOnPlayerDamage.getCommands())
            if (command.getViewer() == user)
                return true;
        return false;
    }

    public void handlePerTurnLogic(){
        while (targeted.hasAnotherCommand()){
            QueuedCommandTargeted command = targeted.getNextCommand();
            Result result = command.getCard().activate(command.getViewer(), AbstractDungeon.player, command.getTargets());
            if (result.wasSuccessful()){
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
            if (result.wasSuccessful()){
                sendMessageToUser( command.getViewer(), "You successfully casted " + command.getCard().getName() + ". " + result.getWhatHappened());
            } else{
                sendMessageToUser(command.getViewer(), "You failed to cast " + command.getCard().getName() + ". " + result.getWhatHappened());
                Main.viewers.get(command.getViewer()).insertCard(command.getCard());
            }
            Main.battle.getViewerMonster(command.getViewer()).clearMoves();
        }
    }

    public void handlePostBattleLogic() {
        targeted.refund();
        targetless.refund();
        oneTimeTriggerOnPlayerDamage.refund();

        // We don't need to do anything with these triggers except clear them.
        continousTriggerOnPlayerDamage.clear();
    }

    public int handleOnPlayerDamagedLogic(int incomingDamage, DamageInfo damageInfo) {
        if (damageInfo.type == DamageInfo.DamageType.HP_LOSS || !continousTriggerOnPlayerDamage.hasAnotherCommand())
            return incomingDamage;

        // Handle one time triggers first.
        while (oneTimeTriggerOnPlayerDamage.hasAnotherCommand() && incomingDamage > 0){
            QueuedCommandTriggerOnPlayerDamage command = oneTimeTriggerOnPlayerDamage.getNextCommand();
            incomingDamage = handleTriggerOnPlayerDamageCommand(incomingDamage, damageInfo, command, true);
        }

        while (continousTriggerOnPlayerDamage.hasAnotherCommand() && incomingDamage > 0){
            QueuedCommandTriggerOnPlayerDamage command = continousTriggerOnPlayerDamage.getNextCommand();
            incomingDamage = handleTriggerOnPlayerDamageCommand(incomingDamage, damageInfo, command, false);
        }

        return incomingDamage;
    }
    private int handleTriggerOnPlayerDamageCommand(int incomingDamage, DamageInfo damageInfo, QueuedCommandTriggerOnPlayerDamage command, boolean refundOnFail){
        ResultWithInt result = command.getCard().handleOnPlayerDamageTrigger(incomingDamage, damageInfo, AbstractDungeon.player, command.getViewer());
        if (result.wasSuccessful()){
            sendMessageToUser( command.getViewer(), result.getWhatHappened());
        } else if (refundOnFail){
            sendMessageToUser(command.getViewer(), command.getCard().getName() + " failed to trigger, and has been refunded. " + result.getWhatHappened());
            Main.viewers.get(command.getViewer()).insertCard(command.getCard());
        }
        return result.getReturnInt();
    }
}
