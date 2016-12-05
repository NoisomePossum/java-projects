package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.damnablevegetable.jackthegiant.GameMain;

import clouds.Cloud;
import clouds.CloudsController;
import helpers.GameInfo;
import helpers.GameManager;
import huds.UIHud;
import player.Player;

/**
 * Created by possum on 11/21/16.
 */

public class Gameplay implements Screen, ContactListener {

    private GameMain game;

//    Declare objects for camera and viewport
    private OrthographicCamera mainCamera;
    private Viewport gameViewport;

//    Declare objects for camera for debug mode
    private OrthographicCamera box2DCamera;
    private Box2DDebugRenderer debugRenderer;

    private World world;

//    Declare an array to hold repeating background images
    private Sprite[] bgs;
//    Declare float to store Y position of final image in bg array
    private float lastYPosition;

    private boolean touchedForTheFirstTime;

    private UIHud hud;

    private CloudsController cloudsController;

    private Player player;
    private float lastPlayerY;

    private Sound coinSound, lifeSound;

    private float cameraSpeed = 10;
    private float maxSpeed = 10;
    private float acceleration = 10;

    public Gameplay(GameMain game) {
        this.game = game;

//        Set width and height of the camera and set its position to the center
//        of the screen
        mainCamera = new OrthographicCamera(GameInfo.WIDTH, GameInfo.HEIGHT);
        mainCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f, 0);

//        Set the viewport's width and height and pass the camera to it
        gameViewport = new StretchViewport(GameInfo.WIDTH, GameInfo.HEIGHT, mainCamera);

        box2DCamera = new OrthographicCamera();
        box2DCamera.setToOrtho(false, GameInfo.WIDTH / GameInfo.PPM,
                GameInfo.HEIGHT / GameInfo.PPM);
        box2DCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f, 0);

        debugRenderer = new Box2DDebugRenderer();

        hud = new UIHud(game);

        world = new World(new Vector2(0, -9.8f), true);
//        inform the world that contact listener is the gameplay class
        world.setContactListener(this);

        cloudsController = new CloudsController(world);

        player = cloudsController.positionThePlayer(player);

        createBackgrounds();

        setCameraSpeed();

        coinSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Coin_Sound.wav"));
        lifeSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Life_Sound.wav"));
    }

    void handleInput(float dt) {
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.movePlayer(-2);
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.movePlayer(2);
        } else {
            player.setWalking(false);
        }
    }

    void checkForFirstTouch() {
        if(!touchedForTheFirstTime) {
            if(Gdx.input.justTouched()) {
                touchedForTheFirstTime = true;
                GameManager.getInstance().isPaused = false;
                lastPlayerY = player.getY();
            }
        }
    }

//    Call the methods which allows the screen to scroll down continuously
    void update(float dt) {

        checkForFirstTouch();

        if(!GameManager.getInstance().isPaused) {
            handleInput(dt);
            moveCamera(dt);
            checkBackgroundOutOfBounds();
            cloudsController.setCameraY(mainCamera.position.y);
            cloudsController.createAndArrangeNewClouds();
            cloudsController.removeOffScreenCollectables();
            checkPlayersBounds();
            countScore();
        }
    }

//    Temporary function to cause the screen to continuously scroll down every frame
    void moveCamera(float delta) {
        mainCamera.position.y -= cameraSpeed * delta;

        cameraSpeed += acceleration * delta;

        if(cameraSpeed > maxSpeed) {
            cameraSpeed = maxSpeed;
        }
    }

    void setCameraSpeed() {

        if(GameManager.getInstance().gameData.isEasyDifficulty()) {
            cameraSpeed = 80;
            maxSpeed = 100;
        }

        if(GameManager.getInstance().gameData.isMediumDifficulty()) {
            cameraSpeed = 100;
            maxSpeed = 120;
        }

        if(GameManager.getInstance().gameData.isHardDifficulty()) {
            cameraSpeed = 120;
            maxSpeed = 140;
        }

    }

//    Create an array of backgrounds and then set them on top of each other
    void createBackgrounds() {
        bgs = new Sprite[3];

        for(int i = 0; i < bgs.length; i++) {
//            Set each index in the array equal to the background image
            bgs[i] = new Sprite(new Texture("Backgrounds/Game_BG.png"));
//            Multiply each background height by its index value
//            and set this as the value for its y position
            bgs[i].setPosition(0, -(i * bgs[i].getHeight()));
//            Set lastYPosition to the Y coordinate of the last background image in the loop
            lastYPosition = Math.abs(bgs[i].getY());
        }
    }

//    Draws the backgrounds as they were set by the createBackgrounds function
    void drawBackgrounds() {
        for(int i = 0; i < bgs.length; i++) {
            game.getBatch().draw(bgs[i], bgs[i].getX(), bgs[i].getY());
        }
    }

//    Check whether the previous background has passed off of the screen
//    then set that background's position to a new position below the last
//    background in the array
    void checkBackgroundOutOfBounds() {
        for(int i = 0; i < bgs.length; i++) {
//            Camera starts at 400 (height of bg / 2); when it reaches one full background length
//            (position -400 or current Y value - bg height / 2) (plus buffer of 5 pixels),
//            execute the following code. Do this for each bg in the array.
            if((bgs[i].getY() - bgs[i].getHeight() / 2 - 5) > mainCamera.position.y) {
                float newPosition = bgs[i].getHeight() + lastYPosition;
                bgs[i].setPosition(0, -newPosition);
                lastYPosition = Math.abs(newPosition);
            }
        }
    }

    void checkPlayersBounds() {

        if(player.getY() - GameInfo.HEIGHT / 2f - player.getHeight() / 2f
                > mainCamera.position.y) {
            if(!player.isDead()) {
                playerDied();
            }
        }

        if(player.getY() + GameInfo.HEIGHT / 2f + player.getHeight() / 2f
                < mainCamera.position.y) {
            if(!player.isDead()) {
                playerDied();
            }
        }

        if(player.getX() - 75 > GameInfo.WIDTH || player.getX() + 75 < 0) {
            if(!player.isDead()) {
                playerDied();
            }
        }

    }

    void countScore() {
        if(lastPlayerY > player.getY()) {
            hud.incrementScore(1);
            lastPlayerY = player.getY();
        }
    }

    void playerDied() {

        GameManager.getInstance().isPaused = true;

//        decrement life count
        hud.decrementLife();

        player.setDead(true);

        player.setPosition(1000, 1000);

        if(GameManager.getInstance().lifeScore < 0) {
//            no more lives left

//            check if we have new high score
            GameManager.getInstance().checkForNewHighScores();

//            show the end score to the user
            hud.createGameOverPanel();

//            load main menu

            RunnableAction run = new RunnableAction();
            run.setRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new MainMenu(game));
                }
            });

            SequenceAction sa = new SequenceAction();
            sa.addAction(Actions.delay(3f));
            sa.addAction(Actions.fadeOut(1f));
            sa.addAction(run);

            hud.getStage().addAction(sa);

        } else {

//            reload game
            RunnableAction run = new RunnableAction();
            run.setRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new Gameplay(game));
                }
            });

            SequenceAction sa = new SequenceAction();
            sa.addAction(Actions.delay(3f));
            sa.addAction(Actions.fadeOut(1f));
            sa.addAction(run);

            hud.getStage().addAction(sa);

        }

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();

        drawBackgrounds();

        cloudsController.drawClouds(game.getBatch());
        cloudsController.drawCollectables(game.getBatch());

        player.drawPlayerIdle(game.getBatch());
        player.drawPlayerAnimation(game.getBatch());

        game.getBatch().end();

//        Draw the hit boxes for clouds (debug mode)
        debugRenderer.render(world, box2DCamera.combined);

        game.getBatch().setProjectionMatrix(hud.getStage().getCamera().combined);
        hud.getStage().draw();
        hud.getStage().act();

        game.getBatch().setProjectionMatrix(mainCamera.combined);
        mainCamera.update();

        player.updatePlayer();

        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
        float box2DWidth = (float) width / GameInfo.PPM;
        float box2DHeight = (float) height / GameInfo.PPM;
        box2DCamera.viewportWidth = box2DWidth;
        box2DCamera.viewportHeight = box2DHeight;
        box2DCamera.position.set(box2DWidth / 2f, box2DHeight / 2f, 0); // center the box2DCamera
        box2DCamera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        world.dispose();
        for(int i = 0; i < bgs.length; i++) {
            bgs[i].getTexture().dispose();
        }
        player.getTexture().dispose();
        debugRenderer.dispose();
        coinSound.dispose();
        lifeSound.dispose();
    }

    @Override
    public void beginContact(Contact contact) {

        Fixture body1, body2;

        if(contact.getFixtureA().getUserData() == "Player"){
            body1 = contact.getFixtureA();
            body2 = contact.getFixtureB();
        } else {
            body1 = contact.getFixtureB();
            body2 = contact.getFixtureA();
        }

        if(body1.getUserData() == "Player" && body2.getUserData() == "Coin") {
//            collided with coin
            hud.incrementCoins();
            coinSound.play();
            body2.setUserData("Remove");
            cloudsController.removeCollectables();
        }

        if(body1.getUserData() == "Player" && body2.getUserData() == "Life") {
//            collided with coin
            hud.incrementLives();
            lifeSound.play();
            body2.setUserData("Remove");
            cloudsController.removeCollectables();
        }

        if(body1.getUserData() == "Player" && body2.getUserData() == "Dark_Cloud") {
//            collided with dark cloud
            if(!player.isDead()) {
                playerDied();
            }
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
} // gameplay
