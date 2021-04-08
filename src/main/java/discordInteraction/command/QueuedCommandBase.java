package discordInteraction.command;

import discordInteraction.Main;
import discordInteraction.card.AbstractCard;
import discordInteraction.viewer.Viewer;
import net.dv8tion.jda.api.entities.User;

public class QueuedCommandBase<T extends AbstractCard> {
    protected Viewer viewer;
    protected T card;

    public Viewer getViewer() {
        return viewer;
    }

    public T getCard() {
        return card;
    }

    public void handleRemovalLogic(boolean refundToViewer){
        Main.battle.getViewerMonster(viewer).clearMoves();
        if (refundToViewer)
            viewer.insertCard(card);
    }

    public QueuedCommandBase(Viewer viewer, T card) {
        this.viewer = viewer;
        this.card = card;
    }

    public boolean hasLivingViewerMonster(){
        return Main.battle.hasLivingViewerMonster(viewer);
    }
}
