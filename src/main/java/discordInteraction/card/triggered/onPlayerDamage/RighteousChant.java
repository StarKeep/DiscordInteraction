package discordInteraction.card.triggered.onPlayerDamage;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import discordInteraction.Main;
import discordInteraction.card.targeted.Smite;
import discordInteraction.card.triggered.TriggerTimingType;
import discordInteraction.command.ResultWithInt;
import discordInteraction.viewer.Viewer;

public class RighteousChant extends AbstractCardTriggeredOnPlayerDamage {
    @Override
    public String getName() {
        return "Righteous Chant";
    }

    @Override
    public int getCost() {
        return 6;
    }

    @Override
    public String getDescriptionForViewerDisplay() {
        return "For the rest of this fight, any damage the player would take is instead directed to you. Whenever you take damage, cast a free randomly targeted Smite.";
    }

    @Override
    public String getDescriptionForGameDisplay() {
        return "For the rest of this fight, any damage you would take is directed to this viewer. Whenever this occurs, this viewer casts a free randomly targeted Smite.";
    }

    @Override
    public String[] getViewerClasses() {
        return new String[]{
                "Paladin"
        };
    }

    @Override
    public TriggerTimingType getTriggerTimingType() {
        return TriggerTimingType.infinite;
    }

    @Override
    public ResultWithInt handleOnPlayerDamageTrigger(int incomingDamage, DamageInfo damageInfo, AbstractPlayer player, Viewer viewer) {
        AbstractCreature source = Main.battle.getViewerMonster(viewer);

        source.damage(damageInfo);
        new Smite().activate(viewer, player);

        return new ResultWithInt(true, "You took " + incomingDamage + " damage that would have hit the player, and responded with a Smite.", 0);
    }
}
