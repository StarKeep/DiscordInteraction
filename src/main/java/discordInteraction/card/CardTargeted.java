package discordInteraction.card;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import discordInteraction.Main;
import discordInteraction.battle.TargetType;
import discordInteraction.command.Result;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public abstract class CardTargeted extends CardTargetless {
    @Override
    public ViewerCardType getViewerCardType() {
        return ViewerCardType.targeted;
    }

    // Cards will be declined if they do not have these number of targets defined.
    public abstract int getTargetCountMin();
    public abstract int getTargetCountMax();

    // Accepted target types.
    public abstract TargetType[] getTargetTypes();

    // Allow the card to be queued with no target, in which case it will pick random enemies as its target
    // if there are enough enemies to meet the minimum target count left.
    @Override
    public Result activate(User user, AbstractPlayer player){
        if (!hasValidTargets(Main.battle.getTargetList(true, getTargetTypes())))
            return new Result(false, "Failed to find valid targets.");

        // Trim the target list until we reach our minimum target count, or our maximum with a % chance to stop each step.
        ArrayList<AbstractCreature> targets = Main.battle.getTargetList(true, getTargetTypes());
        while (targets.size() > getTargetCountMin())
        {
            targets.remove(Main.random.nextInt(targets.size()));
            if (targets.size() <= getTargetCountMax() || Main.random.nextBoolean())
                break;
        }

        if (!hasValidTargets(targets))
            return new Result(false, "Invalid number of targets, one or more may have died before your card activated.");

        return activate(user, player, targets);
    }

    public Result activate(User user, AbstractPlayer player, ArrayList<AbstractCreature> targets){
        if (!hasValidTargets(targets))
            return new Result(false, "Invalid number of targets, one or more may have died before your card activated.");

        return apply(user, player, targets);
    }

    // The actual application of the effect. This is left pretty open to allow maximum variety.
    protected abstract Result apply(User user, AbstractPlayer player, ArrayList<AbstractCreature> targets);

    protected boolean hasValidTargets(ArrayList<AbstractCreature> targets){
        if (targets == null
         || targets.size() < getTargetCountMin())
            return false;
        return true;
    }
}
