package discordInteraction.command;

import com.megacrit.cardcrawl.core.AbstractCreature;
import discordInteraction.card.targeted.AbstractCardTargeted;
import discordInteraction.viewer.Viewer;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class QueuedCommandTargeted extends QueuedCommandBase<AbstractCardTargeted> {
    private ArrayList<AbstractCreature> targets;

    public ArrayList<AbstractCreature> getTargetsList(){
        return targets;
    }

    public ArrayList<AbstractCreature> getTargets() {
        return targets;
    }

    public QueuedCommandTargeted(Viewer viewer, AbstractCardTargeted card, ArrayList<AbstractCreature> targets){
        super(viewer, card);

        this.targets = targets;
    }
}
