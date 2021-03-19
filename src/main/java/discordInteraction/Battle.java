package discordInteraction;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.sun.org.apache.xerces.internal.impl.dv.xs.AbstractDateTimeDV;
import kobting.friendlyminions.helpers.BasePlayerMinionHelper;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;
import kobting.friendlyminions.patches.PlayerAddFieldsPatch;
import net.dv8tion.jda.api.entities.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;

import static discordInteraction.Utilities.listHandForViewer;
import static discordInteraction.Utilities.sendMessageToUser;

public class Battle {
    private final Object battleLock = new Object();
    private Boolean inBattle;
    private AbstractRoom battleRoom;
    private String battleMessageID;
    private HashMap<User, AbstractFriendlyMonster> viewers;
    private HashSet<User> viewersDeadUntilNextBattle;

    public Boolean isInBattle(){
        synchronized (battleLock){
            return inBattle;
        }
    }
    private void setIsInBattle(boolean status){
        synchronized (battleLock){
            inBattle = status;
        }
    }
    public AbstractRoom getBattleRoom(){
        synchronized (battleLock){
            return battleRoom;
        }
    }
    private void setBattleRoom(AbstractRoom room) {
        synchronized (battleLock) {
            battleRoom = room;
        }
    }
    public String getBattleMessageID(){
        synchronized (battleLock){
            return battleMessageID;
        }
    }
    private void setBattleMessageID(String id){
        synchronized (battleLock){
            battleMessageID = id;
        }
    }

    public void startBattle(AbstractRoom room, String id){
        synchronized (battleLock) {
            setIsInBattle(true);
            setBattleRoom(room);
            setBattleMessageID(id);

            // Spawn in viewers.
            for (User user : viewers.keySet()) {
                addViewerMonster(user);
                listHandForViewer(user);
                sendMessageToUser(user, "A new fight has begun!");
            }
        }
    }

    public void endBattle(){
        synchronized (battleLock) {
            // End the battle; edit the battle message to showcase the end result.
            setLastBattleUpdate(LocalDateTime.now());
            Main.channel.retrieveMessageById(getBattleMessageID()).queue((message -> {
                message.editMessage(Utilities.getEndOfBattleMessage()).queue();
                setBattleMessageID(null);
            }));

            // Remove all of our stored viewers.
            removeAllViewerMonsters();
            viewersDeadUntilNextBattle.clear();

            // Let the rest of the program know the fight ended.
            setIsInBattle(true);
            setBattleRoom(null);
        }
    }

    public boolean canUserSpawnIn(User user){
        return !viewersDeadUntilNextBattle.contains(user);
    }

    public void addViewerMonster(User user){
        if (!viewers.containsKey(user)){
            int x = -1200;
            int y = 500;

            int count = viewers.size();
            if ((Integer)PlayerAddFieldsPatch.f_maxMinions.get(AbstractDungeon.player) < count + 2)
                PlayerAddFieldsPatch.f_maxMinions.set(AbstractDungeon.player, count + 2);
            while (count >= 8){
                count -= 8;
                y -= 140;
            }
            x += (count * 120);

            AbstractFriendlyMonster viewer = new ViewerMinion(user, x, y);
            BasePlayerMinionHelper.addMinion(AbstractDungeon.player, viewer);
            viewers.put(user, viewer);
        }
    }
    public void removeViewerMonster(User user, boolean untilEndOfBattle){
        if (viewers.containsKey(user))
            viewers.remove(user);
        if (untilEndOfBattle)
            viewersDeadUntilNextBattle.add(user);
    }
    public void removeAllViewerMonsters(){
        viewers.clear();
    }
    public boolean hasViewerMonster(User user){
        return viewers.containsKey(user);
    }
    public HashMap<User, AbstractFriendlyMonster> getViewerMonsters(){
        return viewers;
    }
    public AbstractFriendlyMonster getViewerMonster(User user){
        if (viewers.containsKey(user))
            return viewers.get(user);
        else
            return null;
    }

    private LocalDateTime lastBattleUpdate;
    private final Object battleTimeLock = new Object();
    public LocalDateTime getLastBattleUpdate(){
        synchronized (battleTimeLock){
            return lastBattleUpdate;
        }
    }
    public void setLastBattleUpdate(LocalDateTime time){
        synchronized (battleTimeLock){
            lastBattleUpdate = time;
        }
    }

    public Battle(){
        lastBattleUpdate = LocalDateTime.now();
        inBattle = false;
        battleRoom = null;
        viewers = new HashMap<>();
        viewersDeadUntilNextBattle = new HashSet<User>();
    }
}
