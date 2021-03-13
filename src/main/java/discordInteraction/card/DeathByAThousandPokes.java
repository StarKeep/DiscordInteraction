package discordInteraction.card;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import discordInteraction.FlavorType;
import discordInteraction.command.ResultWithInt;
import net.dv8tion.jda.api.entities.User;

public class DeathByAThousandPokes extends CardTriggerOnPlayerDamage {
    @Override
    public String getName() {
        return "Death By A Thousand Pokes";
    }

    @Override
    public int getCost() {
        return 6;
    }

    @Override
    public String getDescription() {
        return "For the rest of this fight, you cannot play additional cards, but any time the player takes damage, you will cast a free Poke and UnPoke.";
    }

    @Override
    public String getFlavorText() {
        return "This death will not be swift.";
    }

    @Override
    public FlavorType[] getFlavorTypes() {
        return new FlavorType[]{
                FlavorType.support
        };
    }

    @Override
    public ResultWithInt handleOnPlayerDamageTrigger(int incomingDamage, DamageInfo damageInfo, AbstractPlayer player, User user) {
        new Poke().activate(user, player);
        new UnPoke().activate(user, player);

        return new ResultWithInt(true, "You cast a free Poke and UnPoke.", incomingDamage);
    }
}
