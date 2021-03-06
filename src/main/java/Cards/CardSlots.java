package Cards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import Cards.ICardSlot;

public class CardSlots implements ICardSlot {
    private Sprite cardSlotSprite1;
    private float posX, posY;

    public CardSlots(float posX, float posY){
        this.posX=posX;
        this.posY=posY;
        Texture cardSlotTexture = new Texture(Gdx.files.internal("Models/AlleBevegelseKortUtenPrioritet/CardSlot.jpg"));
        cardSlotSprite1= new Sprite(cardSlotTexture);
        cardSlotSprite1.setPosition(posX, posY);
    }

    //Constructor necessary for testing.
    public CardSlots( float posX, float posY, int i){
        this.posX=posX;
        this.posY=posY;
    }

    public Sprite getCardSlotSprite(){ return cardSlotSprite1;}

    public Float getPosX(){
        return posX;
    }

    public Float getPosY(){
        return posY;
    }
}