package discordInteraction.command;

import discordInteraction.card.CardTriggerOnPlayerDamage;
import net.dv8tion.jda.api.entities.User;

public class QueuedCommandTriggerOnPlayerDamage extends QueuedCommandBase<CardTriggerOnPlayerDamage> {
    public QueuedCommandTriggerOnPlayerDamage(User viewer, CardTriggerOnPlayerDamage card) {
        super(viewer, card);
    }
}
