package discordInteraction;

import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.time.LocalDateTime;

public class Battle {
    private static final Object battleLock = new Object();
    private static Boolean inBattle;
    private static AbstractRoom battleRoom;
    private static String battleMessageID;
    public static Boolean isInBattle(){
        synchronized (battleLock){
            return inBattle;
        }
    }
    public static void setIsInBattle(boolean status){
        synchronized (battleLock){
            inBattle = status;
        }
    }
    public static AbstractRoom getBattleRoom(){
        synchronized (battleLock){
            return battleRoom;
        }
    }
    public static void setBattleRoom(AbstractRoom room) {
        synchronized (battleLock) {
            battleRoom = room;
        }
    }
    public static String getBattleMessageID(){
        synchronized (battleLock){
            return battleMessageID;
        }
    }
    public static void setBattleMessageID(String id){
        synchronized (battleLock){
            battleMessageID = id;
        }
    }

    private static LocalDateTime lastBattleUpdate;
    private static final Object battleTimeLock = new Object();
    public static LocalDateTime getLastBattleUpdate(){
        synchronized (battleTimeLock){
            return lastBattleUpdate;
        }
    }
    public static void setLastBattleUpdate(LocalDateTime time){
        synchronized (battleTimeLock){
            lastBattleUpdate = time;
        }
    }

    public Battle(){
        lastBattleUpdate = LocalDateTime.now();
        inBattle = false;
        battleRoom = null;
    }
}
