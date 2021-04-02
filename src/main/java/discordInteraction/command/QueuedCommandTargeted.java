package discordInteraction.command;

import com.megacrit.cardcrawl.core.AbstractCreature;
import discordInteraction.card.targeted.AbstractedTargetedCard;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class QueuedCommandTargeted extends QueuedCommandBase<AbstractedTargetedCard> {
    private ArrayList<AbstractCreature> targets;

    public ArrayList<AbstractCreature> getTargetsList(){
        return targets;
    }

    public ArrayList<AbstractCreature> getTargets() {
        return targets;
    }

    public QueuedCommandTargeted(User player, AbstractedTargetedCard card, ArrayList<AbstractCreature> targets){
        super(player, card);

        this.targets = targets;
    }
}
