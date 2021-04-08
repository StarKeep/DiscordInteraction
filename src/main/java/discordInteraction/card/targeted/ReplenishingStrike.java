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

public class ReplenishingStrike extends AbstractCardTargeted {
    @Override
    public String getName() {
        return "Replenishing Strike";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescriptionForViewerDisplay() {
        return "Deal 3 damage to an enemy and heal yourself for 6 health.";
    }

    @Override
    public String getDescriptionForGameDisplay() {
        return "Deal 3 damage to an enemy and heal this viewer for 6 health.";
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
        Combat.applyDamage(source, target, 3, DamageInfo.DamageType.NORMAL);
        source.heal(6);
        return new Result(true, "You dealt 3 damage to " + target.name + " and healed yourself.");
    }
}
