package discordInteraction.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Shiv;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import discordInteraction.FlavorType;
import discordInteraction.command.Result;

import java.util.ArrayList;

public class ShivStorm extends CardTargetless {
    @Override
    public String getName() {
        return "Shiv Storm";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Shuffle 5 Shivs in the player's draw pile. Shuffled all exhausted Shivs into the player's draw pile.";
    }

    @Override
    public String getFlavorText() {
        return "I think you dropped these.";
    }

    @Override
    public FlavorType[] getFlavorTypes() {
        return new FlavorType[]{
                FlavorType.support,
                FlavorType.chaos
        };
    }

    @Override
    public Result activate(AbstractPlayer player) {
        for (int x = 0; x < 5; x++)
            player.drawPile.addToRandomSpot(new Shiv());
        ArrayList<AbstractCard> cardsToMove = new ArrayList<AbstractCard>();
        for (AbstractCard card : player.exhaustPile.group)
            if (card.cardID == Shiv.ID)
                cardsToMove.add(card);
        for (AbstractCard card : cardsToMove)
            player.exhaustPile.moveToDeck(card, true);
        int moved = 5 + cardsToMove.size();
        return new Result(true, "You added " + moved + " Shivs to the player's deck.");
    }
}
