package discordInteraction.card;

import com.badlogic.gdx.tools.flame.EventManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import discordInteraction.FlavorType;
import discordInteraction.command.Result;

import java.util.concurrent.ThreadLocalRandom;

public class Fatigue extends CardTargetless {
    @Override
    public String getName() {
        return "Fatigue";
    }

    @Override
    public int getCost() {
        return 4;
    }

    @Override
    public String getDescription() {
        return "Exhaust a random discarded card.";
    }

    @Override
    public String getFlavorText() {
        return "It was getting old anyway.";
    }

    @Override
    public FlavorType[] getFlavorTypes() {
        return new FlavorType[]{
                FlavorType.oppose,
                FlavorType.chaos
        };
    }

    @Override
    public Result activate(AbstractPlayer player) {
        if (player.discardPile.size() == 0)
            return new Result(false, "There are no cards in the discard pile.");

        AbstractCard card = player.discardPile.getRandomCard(true);
        AbstractDungeon.actionManager.addToBottom(new ExhaustSpecificCardAction(card, player.discardPile, true));

        return new Result(true, "You have exhausted a " + card.name + " card.");
    }
}
