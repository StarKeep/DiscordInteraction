package discordInteraction.card.targeted;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import discordInteraction.Main;
import discordInteraction.battle.Battle;
import discordInteraction.battle.TargetType;
import discordInteraction.command.Result;
import discordInteraction.util.Combat;
import discordInteraction.viewer.Viewer;

import java.util.ArrayList;

public class ArmorUp extends AbstractCardTargeted {
    @Override
    public String getName() {
        return "Armor Up";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescriptionForViewerDisplay() {
        return "Apply 6 Block and 3 Metallicize to yourself and one ally. If you target yourself, apply it twice.";
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
                TargetType.player,
                TargetType.viewer
        };
    }

    @Override
    protected Result apply(Viewer viewer, AbstractPlayer player, ArrayList<AbstractCreature> targets) {
        AbstractCreature source = Main.battle.getViewerMonster(viewer);
        AbstractCreature target = targets.get(0);

        source.addBlock(6);
        Combat.applyPower(source, new MetallicizePower(source, 3));

        target.addBlock(6);
        Combat.applyPower(target, new MetallicizePower(target, 3));

        return new Result(true, "You have raised your defenses.");
    }
}
