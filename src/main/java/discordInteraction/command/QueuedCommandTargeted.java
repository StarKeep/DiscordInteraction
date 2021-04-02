package discordInteraction.command;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import discordInteraction.card.CardTargeted;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class QueuedCommandTargeted extends QueuedCommandBase<CardTargeted> {
    private ArrayList<AbstractCreature> targets;

    public ArrayList<AbstractCreature> getTargetsList(){
        return targets;
    }

    public ArrayList<AbstractCreature> getTargets() {
        return targets;
    }

    public QueuedCommandTargeted(User player, CardTargeted card, ArrayList<AbstractCreature> targets){
        super(player, card);

        this.targets = targets;
    }
}
