package discordInteraction.card.targeted;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import discordInteraction.Main;
import discordInteraction.battle.TargetType;
import discordInteraction.command.Result;
import discordInteraction.util.Combat;
import discordInteraction.viewer.Viewer;

import java.util.ArrayList;

public class Smite extends AbstractCardTargeted {
    @Override
    public String getName() {
        return "Smite";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescriptionForViewerDisplay() {
        return "Deal 3 + X damage to an enemy, where X is equal to the amount of Block you have.";
    }

    @Override
    public String getDescriptionForGameDisplay() {
        return "Deal 3 + X damage to an enemy, where X is equal to the amount of Block this viewer has.";
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

        int damage = 3 + source.currentBlock;

        damage = Combat.applyDamage(source, target, damage, DamageInfo.DamageType.NORMAL);

        return new Result(true, "You dealt " + damage + " damage to " + target.name + ".");
    }
}
