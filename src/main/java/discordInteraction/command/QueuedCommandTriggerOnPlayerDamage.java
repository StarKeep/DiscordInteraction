package discordInteraction.command;

import discordInteraction.card.triggered.onPlayerDamage.AbstractCardTriggeredOnPlayerDamage;
import net.dv8tion.jda.api.entities.User;

public class QueuedCommandTriggerOnPlayerDamage extends QueuedCommandBase<AbstractCardTriggeredOnPlayerDamage> {
    public QueuedCommandTriggerOnPlayerDamage(User viewer, AbstractCardTriggeredOnPlayerDamage card) {
        super(viewer, card);
    }
}
