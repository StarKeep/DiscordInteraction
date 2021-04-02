package discordInteraction.command;

import discordInteraction.card.triggered.onPlayerDamage.AbstractCardTriggerOnPlayerDamage;
import net.dv8tion.jda.api.entities.User;

public class QueuedCommandTriggerOnPlayerDamage extends QueuedCommandBase<AbstractCardTriggerOnPlayerDamage> {
    public QueuedCommandTriggerOnPlayerDamage(User viewer, AbstractCardTriggerOnPlayerDamage card) {
        super(viewer, card);
    }
}
