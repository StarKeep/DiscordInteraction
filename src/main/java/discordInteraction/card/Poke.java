package discordInteraction.card;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import discordInteraction.Main;
import discordInteraction.command.Result;

public class Poke extends CardTargeted {
    @Override
    public String getName() {
        return "Poke";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Deal 3 damage to an enemy. If no target is specified, will pick a target at random.";
    }

    @Override
    public String getFlavorText() {
        return "Nothing like a good hard bonk to annoy somebody.";
    }

    @Override
    public FlavorType[] getFlavorTypes() {
        return new FlavorType[]{
                FlavorType.basic
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
    public Result activate(AbstractPlayer player, MonsterGroup target) {
        if (target.monsters.size() < 1)
            return new Result(false, "No monsters found.");
        AbstractMonster monster = target.getRandomMonster(true);
        if (monster.isDeadOrEscaped())
            return new Result(false, monster.name + " died or escaped before your card could resolve.");
        monster.damage(new DamageInfo(player, 3, DamageInfo.DamageType.NORMAL));
        return new Result(true, "You dealt 3 damage to " + monster.name + ".");
    }

    @Override
    public Result activate(AbstractPlayer player) {
        return activate(player, Main.battle.getBattleRoom().monsters);
    }
}
