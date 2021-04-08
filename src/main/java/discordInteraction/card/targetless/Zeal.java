package discordInteraction.card.targetless;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.RitualPower;
import discordInteraction.Main;
import discordInteraction.command.Result;
import discordInteraction.util.Combat;
import discordInteraction.viewer.Viewer;

public class Zeal extends AbstractCardTargetless {
    @Override
    public String getName() {
        return "Zeal";
    }

    @Override
    public int getCost() {
        return 4;
    }

    @Override
    public String getDescriptionForViewerDisplay() {
        return "Gain Ritual 2 and Metallicize 6.";
    }

    @Override
    public String[] getViewerClasses() {
        return new String[]{
                "Paladin"
        };
    }

    @Override
    public Result activate(Viewer viewer, AbstractPlayer player) {
        AbstractCreature target = Main.battle.getViewerMonster(viewer);

        Combat.applyPower(target, new RitualPower(target, 2, false));
        Combat.applyPower(target, new MetallicizePower(target, 6));

        return new Result(true, "You have entered a frenzy!");
    }
}
