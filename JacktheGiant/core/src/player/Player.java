package player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import helpers.GameInfo;

/**
 * Created by possum on 11/23/16.
 */

public class Player extends Sprite {

    private World world;
    private Body body;

//    define variables for sprite animation for player
    private TextureAtlas playerAtlas;
    private Animation animation;
    private float elapsedTime;

    private boolean isWalking, dead;

    public Player(World world, float x, float y) {
        super(new Texture("./Player/Player_1.png"));
        this.world = world;
        setPosition(x, y);
        createBody();
        playerAtlas = new TextureAtlas("Player_Animation/Player_Animation.atlas");
        dead = false;
    }

    void createBody() {

//        define body for player
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((getX() + getWidth()) / GameInfo.PPM,
                (getY() + getHeight()) / GameInfo.PPM);

        body = world.createBody(bodyDef);
        body.setFixedRotation(true);

//        define shape and size of hit box
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((getWidth() / 2f - 20) / GameInfo.PPM,
                (getHeight() / 2f - 5) / GameInfo.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 4; // this is the mass of the body
        fixtureDef.friction = 2; // this makes the player not slide on surfaces
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GameInfo.PLAYER;
//        Defines which items player can collide with
        fixtureDef.filter.maskBits = GameInfo.DEFAULT | GameInfo.COLLECTABLE;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData("Player");

        shape.dispose();

    }

    public void movePlayer(float x) {

        if(x < 0 && !this.isFlipX()) {
            this.flip(true, false);
        } else if(x > 0 && this.isFlipX()) {
            this.flip(true, false);
        }

        isWalking = true;
        body.setLinearVelocity(x, body.getLinearVelocity().y);
    }

    public void drawPlayerIdle (SpriteBatch batch) {
        if(!isWalking) {
            batch.draw(this, getX() + getWidth() / 2f,
                    getY() - getHeight() / 2f);
        }
    }

    public void drawPlayerAnimation(SpriteBatch batch) {
        if(isWalking) {
            elapsedTime += Gdx.graphics.getDeltaTime();

            Array<TextureAtlas.AtlasRegion> frames = playerAtlas.getRegions();

            for(TextureRegion frame : frames) {
                if(body.getLinearVelocity().x < 0 && !frame.isFlipX()) {
                    frame.flip(true, false);
                } else if(body.getLinearVelocity().x > 0 && frame.isFlipX()) {
                    frame.flip(true, false);
                }
            }

            animation = new Animation(1f/10f, playerAtlas.getRegions());

            batch.draw(animation.getKeyFrame(elapsedTime, true),
                    getX() + getWidth() / 2f,
                    getY() - getHeight() / 2f);

        }
    }

    public void updatePlayer() {

        if(body.getLinearVelocity().x > 0) {
//            moving right
            setPosition(body.getPosition().x * GameInfo.PPM - getWidth(),
                    body.getPosition().y * GameInfo.PPM);
        } else if(body.getLinearVelocity().x < 0) {
//            moving left
            setPosition((body.getPosition().x) * GameInfo.PPM - getWidth(),
                    body.getPosition().y * GameInfo.PPM);
        }

    }

    public void setWalking(boolean isWalking) {
        this.isWalking = isWalking;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isDead() {
        return dead;
    }

} // player
