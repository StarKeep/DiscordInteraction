package discordInteraction.viewer;

import discordInteraction.Main;
import discordInteraction.card.AbstractCard;
import discordInteraction.card.Rarity;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.ArrayList;

public class Viewer {
    private User user;
    private String viewerClass;
    private int handCapacity;
    private ArrayList<AbstractCard> cards;

    public String getId(){
        return user.getId();
    }

    public String getName(){
        return user.getName();
    }

    public String getAvatarUrl(){
        return user.getAvatarUrl();
    }

    public RestAction<PrivateChannel> openPrivateChannel(){
        return user.openPrivateChannel();
    }

    public String getViewerClass(){
        return viewerClass;
    }

    public Viewer(User user, String viewerClass){
        this.user = user;
        this.viewerClass = "Page";
        this.handCapacity = 10;
        cards = new ArrayList<AbstractCard>();
    }

    public ArrayList<AbstractCard> getCards(){
        return cards;
    }

    public void insertCard(AbstractCard card){
        if (cards.size() < handCapacity)
            cards.add(card);
    }

    public void removeCard(AbstractCard card){
        cards.remove(card);
    }

    public AbstractCard getFirstCardByName(String cardName) {
        for (AbstractCard card : cards)
            if (card.getName().replaceAll("\\s+","").equalsIgnoreCase(cardName.replaceAll("\\s+","")))
                return card;

        return null;
    }

    public void discardHand(){
        cards.clear();
    }

    public void draw(int pointsToDraw, int basicsToDraw){
        ArrayList<AbstractCard> cardPool = new ArrayList<AbstractCard>();

        for(AbstractCard card : Main.deck.getCardsByViewerClass(viewerClass))
            if (card.getCost() > 1)
                for(int x = card.getCost(); x <= Rarity.getHighestCost(); x++)
                    cardPool.add(card);

        while(pointsToDraw > 0 && cards.size() < handCapacity){
            int toDraw = Main.random.nextInt(cardPool.size() + 1) - 1;
            if (toDraw < 0)
                toDraw = 0;
            AbstractCard drawnCard = cardPool.get(toDraw);
            pointsToDraw -= drawnCard.getCost();
            cards.add(drawnCard);
        }

        drawBasics(basicsToDraw);
    }

    public void drawNewHand(int pointsToDraw, int basicsToDraw){
        discardHand();
        draw(pointsToDraw, basicsToDraw);
    }

    public void drawBasics(int pointsToDraw){
        ArrayList<AbstractCard> cardPool = new ArrayList<AbstractCard>();

        for(AbstractCard card : Main.deck.getCardsByViewerClass(viewerClass))
            if (card.getCost() == 1)
                    cardPool.add(card);

        while(pointsToDraw > 0 && cards.size() < handCapacity){
            int toDraw = Main.random.nextInt(cardPool.size() + 1) - 1;
            if (toDraw < 0)
                toDraw = 0;
            AbstractCard drawnCard = cardPool.get(toDraw);
            pointsToDraw -= drawnCard.getCost();
            cards.add(drawnCard);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Viewer))
            return false;
        return this.getId().equals(((Viewer)obj).getId());
    }
}
