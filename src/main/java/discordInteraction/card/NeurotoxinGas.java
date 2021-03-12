package discordInteraction.card;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.PoisonPower;
import discordInteraction.FlavorType;
import discordInteraction.Utilities;
import discordInteraction.command.Result;

public class NeurotoxinGas extends CardTargeted {
    @Override
    public String getName() {
        return "Neurotoxin Gas";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Apply 3 Poison to an enemy.";
    }

    @Override
    public String getFlavorText() {
        return "Oh look, it's your old friend; deadly neurotoxin.";
    }

    @Override
    public FlavorType[] getFlavorTypes() {
        return new FlavorType[]{
                FlavorType.support
        };
    }

    @Override
    public int getTargetCountMin() {
        return 1;
    }

    @Override
    public int getTargetCountMax() {
        return 1;
    }

    @Override
    protected Result apply(AbstractPlayer player, MonsterGroup targets) {
        AbstractMonster target = targets.monsters.get(0);
        Utilities.applyPower(target, new PoisonPower(target, target, 3));

        return new Result(true, "You applied 3 poison to " + target.name + ", for science of course.");
    }
}
