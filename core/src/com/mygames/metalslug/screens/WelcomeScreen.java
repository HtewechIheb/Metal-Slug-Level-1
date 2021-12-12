package com.mygames.metalslug.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygames.metalslug.MetalSlug;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class WelcomeScreen implements Screen {
    private MetalSlug game;
    private Stage stage;
    private Viewport viewport;
    private AssetManager assetManager;
    private Table table;
    private Table nameTable;

    private Label welcomeLabel;
    private Label enterNameLabel;
    private Label[] letters;
    private boolean inputFull = false;
    private boolean inputEmpty = false;

    private byte letterIndex;
    private HashMap<Integer, String> acceptedInputKeys;

    public WelcomeScreen(MetalSlug game, AssetManager assetManager){
        this.game = game;
        this.assetManager = assetManager;
        viewport = new FitViewport(MetalSlug.V_WIDTH, MetalSlug.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);

        defineAcceptedKeys();

        table = new Table();
        table.top();
        table.setFillParent(true);

        nameTable = new Table();
        table.align(Align.center | Align.top);

        welcomeLabel = new Label("Welcome", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        enterNameLabel = new Label("Please Enter Your Name", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        letters = new Label[8];
        for(byte i = 0; i < 8; i++){
            letters[i] = new Label("_", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        }
        letterIndex = 0;

        table.padTop(50);
        table.add(welcomeLabel).padBottom(10);
        table.row();
        table.add(enterNameLabel).padBottom(10);
        table.row();

        for(byte i = 0; i < 8; i++){
            nameTable.add(letters[i]).padRight(5).padLeft(5);
        }
        table.add(nameTable);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    private void defineAcceptedKeys(){
        acceptedInputKeys = new HashMap<>();
        acceptedInputKeys.put(Input.Keys.A, "A");
        acceptedInputKeys.put(Input.Keys.B, "B");
        acceptedInputKeys.put(Input.Keys.C, "C");
        acceptedInputKeys.put(Input.Keys.D, "D");
        acceptedInputKeys.put(Input.Keys.E, "E");
        acceptedInputKeys.put(Input.Keys.F, "F");
        acceptedInputKeys.put(Input.Keys.G, "G");
        acceptedInputKeys.put(Input.Keys.H, "H");
        acceptedInputKeys.put(Input.Keys.I, "I");
        acceptedInputKeys.put(Input.Keys.J, "J");
        acceptedInputKeys.put(Input.Keys.K, "K");
        acceptedInputKeys.put(Input.Keys.L, "L");
        acceptedInputKeys.put(Input.Keys.M, "M");
        acceptedInputKeys.put(Input.Keys.N, "N");
        acceptedInputKeys.put(Input.Keys.O, "O");
        acceptedInputKeys.put(Input.Keys.P, "P");
        acceptedInputKeys.put(Input.Keys.Q, "Q");
        acceptedInputKeys.put(Input.Keys.R, "R");
        acceptedInputKeys.put(Input.Keys.S, "S");
        acceptedInputKeys.put(Input.Keys.T, "T");
        acceptedInputKeys.put(Input.Keys.U, "U");
        acceptedInputKeys.put(Input.Keys.V, "V");
        acceptedInputKeys.put(Input.Keys.W, "W");
        acceptedInputKeys.put(Input.Keys.X, "X");
        acceptedInputKeys.put(Input.Keys.Y, "Y");
        acceptedInputKeys.put(Input.Keys.NUMPAD_0, "0");
        acceptedInputKeys.put(Input.Keys.NUMPAD_1, "1");
        acceptedInputKeys.put(Input.Keys.NUMPAD_2, "2");
        acceptedInputKeys.put(Input.Keys.NUMPAD_3, "3");
        acceptedInputKeys.put(Input.Keys.NUMPAD_4, "4");
        acceptedInputKeys.put(Input.Keys.NUMPAD_5, "5");
        acceptedInputKeys.put(Input.Keys.NUMPAD_6, "6");
        acceptedInputKeys.put(Input.Keys.NUMPAD_7, "7");
        acceptedInputKeys.put(Input.Keys.NUMPAD_8, "8");
        acceptedInputKeys.put(Input.Keys.NUMPAD_9, "9");
        acceptedInputKeys.put(Input.Keys.NUM_0, "0");
        acceptedInputKeys.put(Input.Keys.NUM_1, "1");
        acceptedInputKeys.put(Input.Keys.NUM_2, "2");
        acceptedInputKeys.put(Input.Keys.NUM_3, "3");
        acceptedInputKeys.put(Input.Keys.NUM_4, "4");
        acceptedInputKeys.put(Input.Keys.NUM_5, "5");
        acceptedInputKeys.put(Input.Keys.NUM_6, "6");
        acceptedInputKeys.put(Input.Keys.NUM_7, "7");
        acceptedInputKeys.put(Input.Keys.NUM_8, "8");
        acceptedInputKeys.put(Input.Keys.NUM_9, "9");
    }

    @Override
    public void show() {

    }

    private void handleInput(float delta){
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && inputFull){
            StringBuilder nameBuilder = new StringBuilder();

            for(byte i = 0; i < 8; i++){
                nameBuilder.append(letters[i].getText());
            }
            game.setScreen(new MissionOneScreen(game, assetManager, nameBuilder.toString()));
        }
        if(!inputEmpty && Gdx.input.isKeyJustPressed(Input.Keys.DEL)){
            letterIndex--;
            assetManager.get("audio/sounds/button_press.wav", Sound.class).play();
            letters[letterIndex].setText("_");
            inputFull = false;

            if(letterIndex == 0){
                inputEmpty = true;
            }
        }

        if(!inputFull){
            for(Map.Entry<Integer, String> entry : acceptedInputKeys.entrySet()){
                if(Gdx.input.isKeyJustPressed(entry.getKey())){
                    assetManager.get("audio/sounds/button_press.wav", Sound.class).play();
                    letters[letterIndex].setText(entry.getValue());
                    letterIndex++;
                    if(letterIndex == letters.length){
                        inputFull = true;
                    }
                    else {
                        inputEmpty = false;
                    }
                }
            }
        }
    }

    public void update(float delta){
        handleInput(delta);
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
