package discordInteraction.card.targeted;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import discordInteraction.Main;
import discordInteraction.battle.TargetType;
import discordInteraction.command.Result;
import discordInteraction.util.Combat;
import discordInteraction.viewer.Viewer;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

import java.util.ArrayList;

public class HolyFire extends AbstractCardTargeted {
    @Override
    public String getName() {
        return "Holy Fire";
    }

    @Override
    public int getCost() {
        return 5;
    }

    @Override
    public String getDescriptionForViewerDisplay() {
        return "Spread holy fire onto up to 6 targets, healing allied and damaging hostile targets for 15, decreased by 2 for each additional target after the first.";
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
        return 6;
    }

    @Override
    public TargetType[] getTargetTypes() {
        return new TargetType[]{
                TargetType.player,
                TargetType.viewer,
                TargetType.enemy
        };
    }

    @Override
    protected Result apply(Viewer viewer, AbstractPlayer player, ArrayList<AbstractCreature> targets) {
        AbstractCreature source = Main.battle.getViewerMonster(viewer);

        int value = 15 - ((targets.size() - 1) * 2);

        for(AbstractCreature target : targets){
            if (target instanceof AbstractFriendlyMonster ||
            target instanceof AbstractPlayer)
                target.heal(value);
            else
                Combat.applyDamage(source, target, value, DamageInfo.DamageType.NORMAL);
        }

        return new Result(true, "You bathed your targets in a holy fire, healing and damaging for " + value + " respectively.");
    }
}
