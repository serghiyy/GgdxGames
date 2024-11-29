package io.github.serghiyy.gdxgames.gdxproject;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter implements ApplicationListener {
    //fields of the game//
    Sprite bucketSprite;
    Texture backgroundTexture;
    Texture bucketdTexture;
    Texture dropTexture;
    Sound dropSound;
    Vector2 touchPos;
    SpriteBatch spriteBatch;
    FitViewport viewport;
    Array<Sprite> dropSprites;
    Rectangle bucketRectangle;
    Rectangle dropRectangle;


    float dropTimer;
    //fields of the game


    @Override
    public void create() {
        backgroundTexture = new Texture("background.png");
        bucketdTexture = new Texture("bucket.png");
        dropTexture = new Texture(Gdx.files.internal("drop.png"));
        spriteBatch = new SpriteBatch();
        viewport =new FitViewport(8,5);
        bucketSprite = new Sprite(bucketdTexture);
        bucketSprite.setSize(1, 1);
        touchPos = new Vector2();
        dropSprites = new Array<>();
        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();
    }
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        inputs();
        logic();
        draw();
    }
    private void inputs(){
        float speed=4f;
        float delta=Gdx.graphics.getDeltaTime();
        //An unfortunate side effect of having our logic inside the render method is
        // that our code behaves differently on different hardware. This is because of differences in framerate. More frames per second means
        // more movement per second. so we need to use delta
        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
            bucketSprite.translateX(speed*delta);
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
            bucketSprite.translateX(-speed*delta);
        }
        if(Gdx.input.isTouched())
        {
            touchPos.set(Gdx.input.getX(),Gdx.input.getY());
            viewport.unproject(touchPos);
            bucketSprite.setCenterX(touchPos.x);
        }

    }
    private void logic(){
        float worldWidth=viewport.getWorldWidth();
        float worldHeight=viewport.getWorldHeight();

        //store the bucketsize for brevity
         float bucketWidth=bucketSprite.getWidth();
         float bucketHeight=bucketSprite.getHeight();

        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(),0,worldWidth-bucketWidth));

        float delta=Gdx.graphics.getDeltaTime();

        bucketRectangle.set(bucketSprite.getX(),bucketSprite.getY(),bucketWidth,bucketHeight);

        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i); // Get the sprite from the list
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            dropSprite.translateY(-2f * delta);
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

            // if the top of the drop goes below the bottom of the view, remove it
            if (dropSprite.getY() < -dropHeight) dropSprites.removeIndex(i);
            else if(bucketRectangle.overlaps(dropRectangle)){
                dropSprites.removeIndex(i);
            }
        }



        dropTimer+=delta;

        if(dropTimer >= 1f){
            dropTimer=0;
            createDroplet();
        }
    }
    private void draw(){
        ScreenUtils.clear(Color.BLACK);
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        viewport.apply();
        spriteBatch.begin();
        // start to drawing

        float worldWidth=viewport.getWorldWidth();
        float worldHeight=viewport.getWorldHeight();

        spriteBatch.draw(backgroundTexture,0,0,worldWidth,worldHeight);
        bucketSprite.draw(spriteBatch);
        for(Sprite sprite:dropSprites){
            sprite.draw(spriteBatch);
        }
        // end of srawings

        spriteBatch.end();
    }
    private void createDroplet(){
        float dropWidth=1;
        float dropHeight=1;
        float worldWidth=viewport.getWorldWidth();
        float worldHeight=viewport.getWorldHeight();
        //create the drop sprite

        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth,dropHeight);
        dropSprite.setX(MathUtils.random(0f,worldWidth-dropWidth));
        dropSprite.setY(worldHeight);
        dropSprites.add(dropSprite);
    }
    @Override
    public void pause(){

    }
}
