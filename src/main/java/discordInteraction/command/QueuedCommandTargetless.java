package discordInteraction.command;

import discordInteraction.card.targetless.AbstractCardTargetless;
import discordInteraction.viewer.Viewer;
import net.dv8tion.jda.api.entities.User;

public class QueuedCommandTargetless extends QueuedCommandBase<AbstractCardTargetless> {
    public QueuedCommandTargetless(Viewer viewer, AbstractCardTargetless card) {
        super(viewer, card);
    }
}
