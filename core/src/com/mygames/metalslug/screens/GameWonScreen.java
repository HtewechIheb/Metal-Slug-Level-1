package com.mygames.metalslug.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
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
import com.mygames.metalslug.misc.ScoreEntry;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GameWonScreen implements Screen {
    private Viewport viewport;
    private Stage stage;
    private MetalSlug game;
    private AssetManager assetManager;
    private int playerScore;
    private Map<String, ScoreEntry> scores;

    public GameWonScreen(MetalSlug game, AssetManager assetManager, int playerScore){
        this.game = game;
        this.assetManager = assetManager;
        this.playerScore = playerScore;

        viewport = new FitViewport(MetalSlug.V_WIDTH, MetalSlug.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);

        this.assetManager.get("audio/sounds/game_won.mp3", Sound.class).play();

        scores = new HashMap<>();
        try(Scanner scanner = new Scanner(new FileInputStream("scores.txt"), StandardCharsets.UTF_8)){
            String[] splitBuffer;

            while (scanner.hasNextLine()){
                splitBuffer = scanner.nextLine().split("\t");
                scores.put(splitBuffer[0], new ScoreEntry(splitBuffer[0], Integer.valueOf(splitBuffer[1]), LocalDateTime.parse(splitBuffer[2])));
            }
        }
        catch (FileNotFoundException exception){
            Gdx.app.log("ERROR", "Could not read scores from file!");
        }

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Label gameWonLabel = new Label("YOU WIN!", font);
        Label scoreLabel = new Label(String.format("Your Score: %04d", playerScore), font);
        Label highScoresLabel = new Label("High Scores:", font);
        Label playAgainLabel = new Label("Click Here To Play Again", font);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        table.padTop(10);
        table.add(gameWonLabel).expandX();
        table.row();
        table.add(scoreLabel).expandX().padTop(10);
        table.row();
        table.add(highScoresLabel).expandX().padTop(10);
        table.row();

        scores.entrySet()
                .stream()
                .map((Map.Entry<String, ScoreEntry> entry) -> entry.getValue())
                .sorted(Comparator.comparingInt((ScoreEntry scoreEntry) -> scoreEntry.getScore()).thenComparing((ScoreEntry scoreEntry) -> scoreEntry.getTime()).reversed())
                .limit(5)
                .forEach((ScoreEntry entry) -> {
            Table entryTable = new Table();
            Label entryNameLabel = new Label(entry.getPlayerName(), font);
            Label entryScoreLabel = new Label(entry.getScore().toString(), font);

            entryTable.add(entryNameLabel).expandX().padRight(10).padLeft(10);
            entryTable.add(entryScoreLabel).expandX().padRight(10).padLeft(10);

            table.add(entryTable).expandX();
            table.row();
        });
        table.add(playAgainLabel).expandX().padTop(10);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(Gdx.input.justTouched()){
            game.setScreen(new WelcomeScreen(game, assetManager));
            dispose();
        }
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
