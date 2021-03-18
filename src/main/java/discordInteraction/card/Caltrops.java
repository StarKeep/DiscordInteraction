package discordInteraction.card;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.ThornsPower;
import discordInteraction.FlavorType;
import discordInteraction.Main;
import discordInteraction.Utilities;
import discordInteraction.command.Result;
import net.dv8tion.jda.api.entities.User;

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
        return "Apply Thorns 2 to an enemy.";
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
    protected Result apply(User user, AbstractPlayer player, MonsterGroup targets) {
        Utilities.applyPower(targets.monsters.get(0), new ThornsPower(targets.monsters.get(0), 2));
        return new Result(true, "You applied 2 thorns to " + targets.monsters.get(0).name);
    }
}
