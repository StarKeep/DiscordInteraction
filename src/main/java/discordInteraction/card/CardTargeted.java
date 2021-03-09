package discordInteraction.card;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import discordInteraction.command.Result;

public abstract class CardTargeted extends CardTargetless {
    // Cards will be declined if they do not have these number of targets defined.
    // The player does not count for this purpose, purely for monster targets.
    public abstract int getTargetCountMin();
    public abstract int getTargetCountMax();
    // Allow the card to be queued with no target, in which case it will pick a random enemy as its target
    // if there are enough enemies to meet the minimum target count left.
    public abstract Result activate(AbstractPlayer player, MonsterGroup target);
}
