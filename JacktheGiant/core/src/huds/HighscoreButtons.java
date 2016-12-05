package huds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.damnablevegetable.jackthegiant.GameMain;

import helpers.GameInfo;
import helpers.GameManager;
import scenes.HighScore;
import scenes.MainMenu;

/**
 * Created by possum on 11/24/16.
 */

public class HighscoreButtons {

    private GameMain game;
    private Stage stage;
    private Viewport gameViewport;

    private Label scoreLabel, coinLabel;

    private ImageButton backBtn;

    public HighscoreButtons(GameMain game) {
        this.game = game;

        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, new OrthographicCamera());

        stage = new Stage(gameViewport, game.getBatch());

        Gdx.input.setInputProcessor(stage);

        createAndPositionUIElements();

        stage.addActor(backBtn);
        stage.addActor(scoreLabel);
        stage.addActor(coinLabel);
    }

    void createAndPositionUIElements() {

        backBtn = new ImageButton((new SpriteDrawable(new Sprite(
                new Texture("Buttons/Options_Buttons/Back.png")))));

        FreeTypeFontGenerator generator =
                new FreeTypeFontGenerator(Gdx.files.internal("Fonts/blow.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;

//        Question: why two different fonts of the same type? Why not pass the same font to both?
//        answer: doesn't matter - merge these into one later
        BitmapFont scoreFont = generator.generateFont(parameter);
        BitmapFont coinFont = generator.generateFont(parameter);

        scoreLabel = new Label(String.valueOf(GameManager.getInstance().gameData.getHighscore()),
                new Label.LabelStyle(scoreFont, Color.WHITE));
        coinLabel = new Label(String.valueOf(GameManager.getInstance().gameData.getCoinHighscore()),
                new Label.LabelStyle(coinFont, Color.WHITE));

        backBtn.setPosition(10, 10, Align.bottomLeft);

        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenu(game));
            }
        });

        scoreLabel.setPosition(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f - 120);
        coinLabel.setPosition(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f - 219);

    }

    public Stage getStage() {
        return this.stage;
    }

} // highscore buttons
