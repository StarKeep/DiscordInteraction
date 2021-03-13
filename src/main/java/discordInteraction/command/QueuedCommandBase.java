package discordInteraction.command;

import discordInteraction.card.Card;
import discordInteraction.card.CardTargetless;
import net.dv8tion.jda.api.entities.User;

public class QueuedCommandBase<T extends Card> {
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
