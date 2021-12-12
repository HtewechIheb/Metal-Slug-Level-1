package com.mygames.metalslug.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygames.metalslug.MetalSlug;

public class GameWonScreen implements Screen {
    private Viewport viewport;
    private Stage stage;
    private MetalSlug game;
    private AssetManager assetManager;
    private int score;

    public GameWonScreen(MetalSlug game, AssetManager assetManager, int score){
        this.game = game;
        this.assetManager = assetManager;
        this.score = score;

        viewport = new FitViewport(MetalSlug.V_WIDTH, MetalSlug.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Label gameWonLabel = new Label("YOU WIN!", font);
        Label scoreLabel = new Label(String.format("Your Score: %04d", score), font);
        Label playAgainLabel = new Label("Click Here To Play Again", font);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        table.add(gameWonLabel).expandX();
        table.row();
        table.add(scoreLabel).expandX().padTop(10);
        table.row();
        table.add(playAgainLabel).expandX().padTop(10);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(Gdx.input.justTouched()){
            game.setScreen(new MissionOneScreen(game, assetManager));
            dispose();
        }
        ScreenUtils.clear(0, 0, 0, 1);
        stage.draw();
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
        stage.dispose();
    }
}
