package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Robot implements IRobot{

    // Textures and sprites are now stored initially in the robot class as opposed to only in RoboRallyDemo
    private String textureStr;
    private Texture texture;
    private Sprite sprite;
    private float posX;
    private float posY;

    // Robot constructor
    public Robot(String textureStr, float posX, float posY){
        this.posX=posX;
        this.posY=posY;
        this.texture = new Texture(Gdx.files.internal(textureStr));
        this.sprite = new Sprite(texture);
        this.sprite.setPosition(posX,posY);
    }

    // A constructor that only takes in a sprite and sets its position to some default (0,0)
    public Robot(String textureStr){
        this.posX = 0;
        this.posY = 0;
        this.texture = new Texture(Gdx.files.internal(textureStr));
        this.sprite = new Sprite(texture);
        this.sprite.setPosition(posX,posY);
    }

    public float getPosX(){
        return posX;
    }

    public float getPosY(){
        return posY;
    }

    public String getTextureStr() { return textureStr; }

    public Texture getTexture() { return texture; }

    public Sprite getSprite(){
        return sprite;
    }
}
