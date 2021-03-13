package discordInteraction.card;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import discordInteraction.FlavorType;
import discordInteraction.Main;
import discordInteraction.command.Result;
import net.dv8tion.jda.api.entities.User;

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
    public Result apply(User user, AbstractPlayer player, MonsterGroup target) {
        AbstractMonster monster = target.monsters.get(0);
        monster.damage(new DamageInfo(Main.battle.getViewerMonster(user), 3, DamageInfo.DamageType.NORMAL));
        return new Result(true, "You dealt 3 damage to " + monster.name + ".");
    }
}
