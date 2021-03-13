package discordInteraction.card;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import discordInteraction.command.Result;
import net.dv8tion.jda.api.entities.User;

public abstract class CardTargetless extends Card {
    @Override
    public ViewerCardType getViewerCardType() {
        return ViewerCardType.targetless;
    }

    public abstract Result activate(User user, AbstractPlayer player);
}
