package discordInteraction.command;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import discordInteraction.card.CardTargeted;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class QueuedCommandTargeted extends QueuedCommandBase<CardTargeted> {
    private ArrayList<AbstractMonster> targets;

    public ArrayList<AbstractMonster> getTargetsList(){
        return targets;
    }

    public MonsterGroup getTargets() {
        MonsterGroup group = null;
        for (AbstractMonster monster : targets)
            if (group == null)
                group = new MonsterGroup(monster);
            else
                group.add(monster);
        return group;
    }

    public QueuedCommandTargeted(User player, CardTargeted card, ArrayList<AbstractMonster> targets){
        super(player, card);

        this.targets = targets;
    }
}
