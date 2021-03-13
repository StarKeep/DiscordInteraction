package discordInteraction.command;

import discordInteraction.card.CardTargetless;
import net.dv8tion.jda.api.entities.User;

public class QueuedCommandTargetless extends QueuedCommandBase<CardTargetless> {
    public QueuedCommandTargetless(User viewer, CardTargetless card) {
        super(viewer, card);
    }
}
