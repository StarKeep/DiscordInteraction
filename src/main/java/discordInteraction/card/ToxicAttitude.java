package discordInteraction.card;

import basemod.devcommands.power.Power;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import discordInteraction.Main;
import discordInteraction.command.Result;

import javax.swing.text.AbstractDocument;

public class ToxicAttitude extends CardTargetless {
    @Override
    public String getName() {
        return "Toxic Attitude";
    }

    @Override
    public int getCost() {
        return 4;
    }

    @Override
    public String getDescription() {
        return "Apply 5 poison to the player and all enemies.";
    }

    @Override
    public String getFlavorText() {
        return "A poisonous attitude is contagious.";
    }

    @Override
    public FlavorType[] getFlavorTypes() {
        return new FlavorType[]{
                FlavorType.chaos
        };
    }

    @Override
    public Result activate(AbstractPlayer player) {
        boolean existed = false;

        for (AbstractPower power : player.powers)
            if (power instanceof PoisonPower)
            {
                power.amount += 5;
                existed = true;
                break;
            }

        if (!existed)
            player.powers.add(new PoisonPower(player, player, 5));

        for (AbstractMonster monster : Main.battle.getBattleRoom().monsters.monsters) {
            existed = false;

            for (AbstractPower power : monster.powers)
                if (power instanceof PoisonPower)
                {
                    power.amount += 5;
                    existed = true;
                    break;
                }

            if (!existed)
                monster.powers.add(new PoisonPower(monster, monster, 5));
        }


        return new Result(true, "You applied 5 poison to all entities in the battle.");
    }
}
