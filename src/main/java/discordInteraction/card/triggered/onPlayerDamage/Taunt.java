package discordInteraction.card.triggered.onPlayerDamage;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import discordInteraction.Main;
import discordInteraction.card.triggered.TriggerTimingType;
import discordInteraction.command.ResultWithInt;
import discordInteraction.util.Combat;
import discordInteraction.viewer.Viewer;

public class Taunt extends AbstractCardTriggeredOnPlayerDamage {
    @Override
    public String getName() {
        return "Taunt";
    }

    @Override
    public int getCost() {
        return 4;
    }

    @Override
    public String getDescriptionForViewerDisplay() {
        return "Any damage that the player would take this turn is redirected to you.";
    }

    @Override
    public String getDescriptionForGameDisplay() {
        return "Any damage you would take this turn is redirected to this viewer.";
    }

    @Override
    public String[] getViewerClasses() {
        return new String[]{
                "Paladin"
        };
    }

    @Override
    public TriggerTimingType getTriggerTimingType() {
        return TriggerTimingType.perTurnStart;
    }

    @Override
    public int getTimesToBeTriggered() {
        return 1;
    }

    @Override
    public ResultWithInt handleOnPlayerDamageTrigger(int incomingDamage, DamageInfo damageInfo, AbstractPlayer player, Viewer viewer) {
        AbstractCreature source = Main.battle.getViewerMonster(viewer);

        source.damage(damageInfo);

        return new ResultWithInt(true, "You took " + incomingDamage + " damage that would have hit the player.", 0);
    }
}
