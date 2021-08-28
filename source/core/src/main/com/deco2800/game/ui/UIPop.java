package com.deco2800.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.deco2800.game.services.ServiceLocator;

import java.util.*;

/*
 * A UIpop up class. Can be used to dynamically add pop ups to the game
 * and can be added directly to entities.
 *
 * See wiki for full description and example use
 * @Link -- {'to come'}
 */
public class UIPop extends UIComponent {

    /*
     * List of background images to be loaded with popUp
     */
    private static final Map<String, String> backGroundImages =
            new HashMap<>();
    static {
        //background images should ideally be 800 * 500 pixels
        backGroundImages.put("Default Pop", "images/blue_bck.png");
        backGroundImages.put("Score Screen", "images/blue_bck.png");
        backGroundImages.put("Pause Menu", "images/blue_bck.png");
    }

    // the screen name to instantiate
    private String screenName;
    // the title of the pop up screen
    private Label title;
    // the root table to be spawned
    private Table root;
    // the pop up table containing contents
    private Table popUp;
    // the close button for the pop up
    private TextButton closeButton;
    // the texture for the popup background
    private Texture popUpBackGround;

    /*
     * UIPop constructor
     */
    public UIPop(String screenName) {

        if (!backGroundImages.containsKey(screenName)){
            throw new NoSuchElementException("Wrong Screen Name");
            };

        this.screenName = screenName;
        this.popUpBackGround = new Texture(backGroundImages.get(screenName));

        root = new Table();
        popUp = new Table();

        popUp.setBackground(new TextureRegionDrawable(popUpBackGround));

        closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                entity.dispose();
            }
        });

        root.setZIndex(0);
        popUp.setZIndex(0);
    }


    @Override
    public void create() {
        super.create();
        addActors(screenName);
    }

    private void InstantiateElement(String screenName) {
        boolean t = screenName.equals("Pause Menu") ? addActors("111") :
        screenName.equals("Score Screen") ? addActors("110") :
        addActors("100");
    }

    // Adds actors based on screen name
    private boolean addActors(String screeName) {

        root.setFillParent(true);

        root.add(popUp).expandX().expandY();

        title = new Label(screeName, skin);
        title.setFontScale(2.5f);

        popUp.add(title).top();
        popUp.row().padTop(30f);

        for (int i = 0; i < 3; i++) {
            Label infoTitle = new Label(getInformation(screenName, i), skin);
            Label info = new Label(String.valueOf(getInfoValues(screenName, i)), skin);
            infoTitle.setFontScale(1.5f);
            info.setFontScale(1.5f);
            popUp.add(infoTitle).left();
            popUp.add(info).right();
            popUp.row().padTop(20f);
        }

        popUp.row().padTop(20f);
        popUp.add(new Label("Volume :", skin)).left();
        popUp.add(ScreenSpecialElement(screenName)).right();

        popUp.row().padTop(20f);
        popUp.add(closeButton).bottom().right();

        stage.addActor(root);

        return true; //no errors
    }

    /* Creates and returns the unique widget element associated with the
     * unique pop up
     */
    private Widget ScreenSpecialElement(String screenName) {

        Widget element = null;

        if (screenName.equals("Pause Menu")) {
            element = new Slider(0.2f, 2f, 0.1f, false, skin);
        } else if (screenName.equals("Score Screen")) {
            element = new SelectBox<>(skin);
        }
        return element;

    }

    /* Returns information values associated with pop ups
    * @param screenName -- The screen name
    * @param index -- the relative information index on the screen
    *                 i.e. the first, second or third text item
    */
    private int getInfoValues(String screenName, int index) {

        if (screenName.equals("Pause Menu")) {
            return index == 1 ? index * 10 :
                    index == 2 ? index * 20 :
                            index * 30;
        }

        if (screenName.equals("Score Screen")) {
            return index == 1 ? index * 15 :
                    index == 2 ? index * 20 :
                            index * 25;
        }

        if (screenName.equals("Default Pop")) {
            return index == 1 ? index :
                    index == 2 ? index * 2 :
                            index * 3;
        }

        return -1; //error
    }

    /* Returns information text associated with pop ups
     * @param screenName -- The screen name
     * @param index -- the relative information index on the screen
     *                 i.e. the first, second or third text item
     */
    private String getInformation(String screenName, int index) {

        if (screenName.equals("Pause Menu")) {
            return index == 0 ? "Pause info 1" :
                   index == 1 ? "Pause info 2" :
                   "Pause info 3";
        }

        if (screenName.equals("Score Screen")) {
            return index == 0 ? "Score info 1" :
                    index == 1 ? "Score info 2" :
                            "Score info 3";
        }

        if (screenName.equals("Default Pop")) {
            return index == 0 ? "Information 1" :
                    index == 1 ? "Information 2" :
                            "Information 3";
        }

        return "error";
    }

    @Override
    public void draw(SpriteBatch UIIndex) {

        int screenHeight = Gdx.graphics.getHeight();
        int screenWidth = Gdx.graphics.getHeight();
        float offsetX = 10f;
        float offsetY = 30f;
    }

    @Override
    public void dispose() {
        super.dispose();
        root.remove();
    }

    /*
     * Returns the list of possible UI screens
     * Can be used to read and create necessary screen.
     * @returns -- Set<String> of possible screen names
     */
    public static Set<String> GetPossibleUIScreens () {
        Set<String> s;
        s = backGroundImages.keySet();
        return s;
    }

    /*
     * Gets the name of the active pop Up
     * @returns -- String of the pop Up name
     */
    public String GetName() {
        String s = "";
        s = screenName;
        return s;
    }


}
