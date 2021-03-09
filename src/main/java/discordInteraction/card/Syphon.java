package discordInteraction.card;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import discordInteraction.Main;
import discordInteraction.command.Result;

public class Syphon extends CardTargetless {
    @Override
    public String getName() {
        return "Syphon";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Deal 2 damage to all enemies, restoring health to the streamer equal to the amount of non-blocked damage dealt.";
    }

    @Override
    public String getFlavorText() {
        return "Its like a swarm of vampire bees, except the bees are on strike along with the graphics designer.";
    }

    @Override
    public FlavorType[] getFlavorTypes() {
        return new FlavorType[]{
                FlavorType.support
        };
    }

    @Override
    public Result activate(AbstractPlayer player) {
        int damageDealt = 0;
        for (AbstractMonster monster : Main.battle.getBattleRoom().monsters.monsters) {
            if (monster.isDeadOrEscaped())
                continue;
            if (monster.currentBlock < 2)
                if (monster.currentHealth == 1)
                    damageDealt += 1;
                else
                    damageDealt += 2;
            monster.damage(new DamageInfo(player, 2, DamageInfo.DamageType.NORMAL));
        }
        player.heal(damageDealt, true);
        return new Result(true, "You dealt 2 damage to all enemies, and healed " +
                damageDealt + " health on the streamer due to non-blocked damage dealt.");
    }
}
