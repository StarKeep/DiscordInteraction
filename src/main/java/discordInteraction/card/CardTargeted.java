package discordInteraction.card;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import discordInteraction.Main;
import discordInteraction.command.Result;
import net.dv8tion.jda.api.entities.User;

import java.util.concurrent.ThreadLocalRandom;

public abstract class CardTargeted extends CardTargetless {
    @Override
    public ViewerCardType getViewerCardType() {
        return ViewerCardType.targeted;
    }

    // Cards will be declined if they do not have these number of targets defined.
    // The player does not count for this purpose, purely for monster targets.
    public abstract int getTargetCountMin();
    public abstract int getTargetCountMax();

    // Allow the card to be queued with no target, in which case it will pick random enemies as its target
    // if there are enough enemies to meet the minimum target count left.
    @Override
    public Result activate(User user, AbstractPlayer player){
        if (!hasValidTargets(Main.battle.getBattleRoom().monsters))
            return new Result(false, "Failed to find valid monsters.");

        MonsterGroup targets = null;
        for(AbstractMonster monster : Main.battle.getBattleRoom().monsters.monsters)
            if (targets != null && targets.monsters.size() >= getTargetCountMax())
                break;
            else if (!monster.isDeadOrEscaped())
                if (targets == null)
                    targets = new MonsterGroup(monster);
                else
                    targets.add(monster);

        if (!hasValidTargets(targets))
            return new Result(false, "Invalid number of targets, one or more may have died before your card activated.");

        return activate(user, player, targets);
    }

    public Result activate(User user, AbstractPlayer player, MonsterGroup targets){
        if (!hasValidTargets(targets))
            return new Result(false, "Invalid number of targets, one or more may have died before your card activated.");

        return apply(user, player, targets);
    }

    // The actual application of the effect. This is left pretty open to allow maximum variety.
    protected abstract Result apply(User user, AbstractPlayer player, MonsterGroup targets);

    protected boolean hasValidTargets(MonsterGroup targets){
        if (targets == null
         || targets.monsters.size() == 0
         || targets.areMonstersBasicallyDead()
         || targets.monsters.size() < getTargetCountMin())
            return false;
        return true;
    }
}
