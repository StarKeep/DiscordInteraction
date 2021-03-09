package discordInteraction.card;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import discordInteraction.Main;
import discordInteraction.command.Result;

public class ArmorUp extends CardTargeted {
    @Override
    public String getName() {
        return "Armor Up";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Apply 3 Block to an enemy, and 9 Block to the streamer. If no target is specified, will pick a target at random.";
    }

    @Override
    public String getFlavorText() {
        return "You're helping! So what if you make them miss lethal.";
    }

    @Override
    public FlavorType[] getFlavorTypes() {
        return new FlavorType[]{
                FlavorType.chaos,
                FlavorType.support
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

        monster.addBlock(3);
        player.addBlock(9);

        return new Result(true, "You applied 3 armor to " + monster.name + " and 9 armor to the streamer.");
    }

    @Override
    public Result activate(AbstractPlayer player) {
        return activate(player, Main.battle.getBattleRoom().monsters);
    }
}
