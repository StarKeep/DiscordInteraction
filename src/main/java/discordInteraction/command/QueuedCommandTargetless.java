package discordInteraction.command;

import discordInteraction.card.CardTargetless;
import net.dv8tion.jda.api.entities.User;

public class QueuedCommandTargetless extends QueuedCommandBase {
    protected CardTargetless card;

    public CardTargetless getCard(){
        return card;
    }

    public QueuedCommandTargetless(User viewer, CardTargetless card){
        super(viewer);
        this.card = card;
    }
}
