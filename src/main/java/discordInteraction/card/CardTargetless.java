package discordInteraction.card;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import discordInteraction.command.Result;

public abstract class CardTargetless extends Card {
    public abstract Result activate(AbstractPlayer player);
}
