package discordInteraction.command;

import discordInteraction.card.AbstractCard;
import net.dv8tion.jda.api.entities.User;

public class QueuedCommandBase<T extends AbstractCard> {
    protected User viewer;
    protected T card;

    public User getViewer() {
        return viewer;
    }

    public T getCard() {
        return card;
    }

    public QueuedCommandBase(User viewer, T card) {
        this.viewer = viewer;
        this.card = card;
    }
}
