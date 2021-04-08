package discordInteraction.card;

public enum Rarity {
    basic (1),
    common (2),
    uncommon (3),
    rare (4),
    legendary (5),
    mythic (6);

    private final int cost;
    public static String getRarityForCost(int cost){
        for(Rarity rarity : Rarity.values())
            if (rarity.cost == cost)
                return rarity.toString();
        return "Unknown";
    }
    public static int getHighestCost(){
        int highest = 1;
        for(Rarity rarity : Rarity.values())
            highest = Math.max(highest, rarity.cost);
        return highest;
    }
    private Rarity(int i) {
        this.cost = i;
    }
}
