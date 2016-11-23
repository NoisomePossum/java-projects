package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.damnablevegetable.jackthegiant.GameMain;

import clouds.Cloud;
import clouds.CloudsController;
import helpers.GameInfo;

/**
 * Created by possum on 11/21/16.
 */

public class Gameplay implements Screen {

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

    private CloudsController cloudsController;


    public Gameplay(GameMain game) {
        this.game = game;

//        Set width and height of the camera and set its position to the center
//        of the screen
        mainCamera = new OrthographicCamera(GameInfo.WIDTH, GameInfo.HEIGHT);
        mainCamera.position.set(GameInfo.WIDTH / 2, GameInfo.HEIGHT / 2, 0);

//        Set the viewport's width and height and pass the camera to it
        gameViewport = new StretchViewport(GameInfo.WIDTH, GameInfo.HEIGHT, mainCamera);

        box2DCamera = new OrthographicCamera();
        box2DCamera.setToOrtho(false, GameInfo.WIDTH / GameInfo.PPM,
                GameInfo.HEIGHT / GameInfo.PPM);
        box2DCamera.position.set(GameInfo.WIDTH / 2, GameInfo.HEIGHT / 2, 0);

        debugRenderer = new Box2DDebugRenderer();

        world = new World(new Vector2(0, -9.8f), true);

        cloudsController = new CloudsController(world);

        createBackgrounds();
    }

//    Call the methods which allows the screen to scroll down continuously
    void update(float dt) {
        moveCamera();
        checkBackgroundOutOfBounds();
        cloudsController.setCameraY(mainCamera.position.y);
        cloudsController.createAndArrangeNewClouds();
    }

//    Temporary function to cause the screen to continuously scroll down every frame
    void moveCamera() {
        mainCamera.position.y -= 1.5;
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

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        update(delta);

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();

        drawBackgrounds();

        cloudsController.drawClouds(game.getBatch());

        game.getBatch().end();

//        Draw the hit boxes for clouds (debug mode)
//        debugRenderer.render(world, box2DCamera.combined);

        game.getBatch().setProjectionMatrix(mainCamera.combined);
        mainCamera.update();
    }

    @Override
    public void resize(int width, int height) {

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

    }
} // gameplay
