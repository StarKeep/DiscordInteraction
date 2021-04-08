package discordInteraction.card.targeted;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.WeakPower;
import discordInteraction.Main;
import discordInteraction.battle.TargetType;
import discordInteraction.command.Result;
import discordInteraction.util.Combat;
import discordInteraction.viewer.Viewer;

import java.util.ArrayList;

public class Bash extends AbstractCardTargeted {
    @Override
    public String getName() {
        return "Bash";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescriptionForViewerDisplay() {
        return "Deal 5 damage to an enemy and apply Weak 3.";
    }

    @Override
    public String[] getViewerClasses() {
        return new String[]{
                "Paladin"
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
    public TargetType[] getTargetTypes() {
        return new TargetType[]{
                TargetType.enemy
        };
    }

    @Override
    protected Result apply(Viewer viewer, AbstractPlayer player, ArrayList<AbstractCreature> targets) {
        AbstractCreature source = Main.battle.getViewerMonster(viewer);
        AbstractCreature target = targets.get(0);
        Combat.applyDamage(source, target, 5, DamageInfo.DamageType.NORMAL);
        Combat.applyPower(target, new WeakPower(target, 3, true));
        return new Result(true, "You dealt 3 damage to " + target.name + " and weakened it for 3 turns.");
    }
}
