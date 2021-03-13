package discordInteraction.card;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import discordInteraction.command.Result;

public abstract class CardTargetless extends Card {
    @Override
    public ViewerCardType getViewerCardType() {
        return ViewerCardType.targetless;
    }

    public abstract Result activate(AbstractPlayer player);
}
