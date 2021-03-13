package discordInteraction.card;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.ThornsPower;
import discordInteraction.FlavorType;
import discordInteraction.Utilities;
import discordInteraction.command.Result;

public class Caltrops extends CardTargeted {
    @Override
    public String getName() {
        return "Caltrops";
    }

    @Override
    public int getCost() {
        return 4;
    }

    @Override
    public String getDescription() {
        return "Apply Thorns 4 to an enemy. If no target is specified, a random enemy is targeted instead.";
    }

    @Override
    public String getFlavorText() {
        return "Watch your step.";
    }

    @Override
    public FlavorType[] getFlavorTypes() {
        return new FlavorType[]{
                FlavorType.oppose
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
        Utilities.applyPower(targets.monsters.get(0), new ThornsPower(targets.monsters.get(0), 4));
        return new Result(true, "You applied 4 thorns to " + targets.monsters.get(0).name);
    }
}
