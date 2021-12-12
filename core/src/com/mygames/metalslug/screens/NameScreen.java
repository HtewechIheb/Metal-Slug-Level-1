package com.mygames.metalslug.screens;

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

public class NameScreen implements Screen {
    private MetalSlug game;
    private Stage stage;
    private Viewport viewport;
    private AssetManager assetManager;

    private Label enterNameLabel;
    private Label letter1;
    private Label letter2;
    private Label letter3;
    private Label letter4;
    private Label letter5;
    private Label letter6;
    private Label letter7;
    private Label letter8;

    public NameScreen(MetalSlug game, AssetManager assetManager){
        this.game = game;
        viewport = new FitViewport(MetalSlug.V_WIDTH, MetalSlug.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        enterNameLabel = new Label("Enter Your Name", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        letter1 = new Label("_", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        letter2 = new Label("_", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        letter3 = new Label("_", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        letter4 = new Label("_", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        letter5 = new Label("_", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        letter6 = new Label("_", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        letter7 = new Label("_", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        letter8 = new Label("_", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(enterNameLabel).padTop(10).center();
        table.row();
        table.add(letter1);
        table.add(letter2);
        table.add(letter3);
        table.add(letter4);
        table.add(letter5);
        table.add(letter6);
        table.add(letter7);
        table.add(letter8);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    public void update(float delta){

    }

    @Override
    public void render(float delta) {
        update(delta);
        ScreenUtils.clear(0, 0, 0, 1);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
