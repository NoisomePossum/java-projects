package clouds;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

import helpers.GameInfo;

/**
 * Created by possum on 11/22/16.
 */

public class CloudsController {

    private World world;

    private Array<Cloud> clouds = new Array<Cloud>();

    private final float DISTANCE_BETWEEN_CLOUDS = 250;
    private float minX, maxX;
    private float lastCloudPositionY;
    private float cameraY;

    private Random random = new Random();

    public CloudsController(World world) {
        this.world = world;
        minX = GameInfo.WIDTH / 2 - 130;
        maxX = GameInfo.WIDTH / 2 + 130;
        createClouds();
        positionClouds(true);
    }

    void createClouds() {

        for(int i = 0; i < 2; i++) {
            clouds.add(new Cloud(world, "Dark_Cloud"));
        }

        int index = 1;

        for(int i = 0; i < 6; i++) {
            clouds.add(new Cloud(world, "Cloud_" + index));
            index++;

//            possible improvement; set index to random number 1-3 instead of incrementing
            if(index == 4) {
                index = 1;
            }
        }

        clouds.shuffle();
    }

    public void positionClouds(boolean firstTimeArranging) {

        while (clouds.get(0).getCloudName() == "Dark_Cloud") {
            clouds.shuffle();
        }

        float positionY = 0;

        if(firstTimeArranging) {
            positionY = GameInfo.HEIGHT / 2;
        } else {
            positionY = lastCloudPositionY;
        }

        int controlX = 0;

        for(Cloud c : clouds) {

            if(c.getX() == 0 && c.getY() == 0) {

                float tempX = 0;

                if(controlX == 0) {
                    tempX = randomBetweenNumbers(maxX - 30, maxX);
                    controlX = 1;
                } else if(controlX == 1) {
                    tempX = randomBetweenNumbers(minX + 30, minX);
                    controlX = 0;
                }

                c.setSpritePosition(tempX, positionY);

                positionY -= DISTANCE_BETWEEN_CLOUDS;
                lastCloudPositionY = positionY;
            }

        }

    }

    public void drawClouds(SpriteBatch batch) {
        for(Cloud c : clouds) {
            batch.draw(c, c.getX() - c.getWidth() / 2,
                    c.getY());
        }
    }

    public void createAndArrangeNewClouds() {
        for(int i = 0; i < clouds.size; i++) {
            if((clouds.get(i).getY() - GameInfo.HEIGHT / 2 - 5) > cameraY) {
                clouds.get(i).getTexture().dispose();
                clouds.removeIndex(i);
            }
        }

        if(clouds.size == 4) {
            createClouds();
            positionClouds(false);
        }
    }

    public void setCameraY(float cameraY) {
        this.cameraY = cameraY;
    }

    private float randomBetweenNumbers(float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }

} // clouds controller
