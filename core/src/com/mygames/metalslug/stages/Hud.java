package com.mygames.metalslug.stages;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.screens.MissionOneScreen;

public class Hud implements Disposable {
    public Stage stage;
    private Viewport viewport;
    private MissionOneScreen screen;

    private Label scoreLabel;

    public Hud(SpriteBatch spriteBatch, MissionOneScreen screen){
        this.screen = screen;
        viewport = new FitViewport(MetalSlug.V_WIDTH, MetalSlug.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        Label scoreTitleLabel = new Label("SCORE", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%04d", screen.getScore()), new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(scoreTitleLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();

        stage.addActor(table);
    }

    public void update(float delta){
        scoreLabel.setText(String.format("%04d", screen.getScore()));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
