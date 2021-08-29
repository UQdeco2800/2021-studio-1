package com.deco2800.game.ui;

import com.badlogic.gdx.Gdx;
import com.deco2800.game.components.maingame.MainGameActions;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deco2800.game.entities.Entity;
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
 * @Link -- {'https://github.com/UQdeco2800/2021-studio-1/wiki/UI-Popup'}
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

    private MainGameActions action;

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
    // texture for button image - Setting
    private Texture popUpSettingImage;
    // texture for button image - Resume
    private Texture popUpResumeImage;
    // texture for button image - Quit;
    private Texture popUpQuitImage;
    // Image button - Setting
    private ImageButton settingButton;
    // Image button - Resume
    private ImageButton resumeButton;
    // Image button - Quit
    private ImageButton quitButton;
    //Setting screen
    private Table setting;
    //Title of setting screen
    private Label settingTitle;
    //Back button
    private Button backButton;
    // the skin for the popup
    private Skin popUpSkin;
    /*
     * UIPop constructor
     */
    public UIPop(String screenName, MainGameActions action) {
        this.action = action;

        if (!backGroundImages.containsKey(screenName)){
            throw new NoSuchElementException("Wrong Screen Name");
            };

        this.screenName = screenName;
        this.popUpBackGround = new Texture(backGroundImages.get(screenName));

        this.popUpSettingImage = new Texture("images/setting.png");
        this.popUpResumeImage = new Texture("images/resume.png");
        this.popUpQuitImage = new Texture("images/quit.png");

        this.settingButton = new ImageButton(new TextureRegionDrawable(popUpSettingImage));
        this.resumeButton = new ImageButton(new TextureRegionDrawable(popUpResumeImage));
        this.quitButton = new ImageButton(new TextureRegionDrawable(popUpQuitImage));

        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                action.onPause();
            }
        });

        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                action.onExit();
            }
        });

        settingButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                root.reset();
                root.add(getSettingScreen()).expandX().expandY();
            }
        });
        setting = new Table();
        root = new Table();
        popUp = new Table();

        popUp.setBackground(new TextureRegionDrawable(popUpBackGround));
        setting.setBackground(new TextureRegionDrawable(popUpBackGround));

        this.popUpSkin = skin;

        closeButton = new TextButton("Close", popUpSkin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (screenName.equals("Pause Menu")) {
                    ServiceLocator.getTimeSource().setTimeScale(1);
                }
                entity.dispose();
            }
        });

        backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                root.reset();
                root.add(popUp).expandX().expandY();
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

        title = new Label(screeName, popUpSkin);
        title.setFontScale(2.5f);

        popUp.add(title).top();
        popUp.row().padTop(2f);

        if (screeName == "Pause Menu") {
            popUp.add(title).top();
            popUp.row().padTop(5f);
            popUp.add(resumeButton).center();
            popUp.row().padTop(2.5f);
            popUp.add(settingButton).center();
            popUp.row().padTop(2.5f);
            popUp.add(quitButton).center();
        }

        stage.addActor(root);
        return true; //no errors
    }

    private Table getSettingScreen() {
        setting.reset();
        settingTitle = new Label("Setting", skin);
        settingTitle.setFontScale(2.5f);
        setting.add(settingTitle).top();
        setting.row().padTop(5f);

        for (int i = 0; i < 3; i++) {
            Label infoTitle = new Label(getInformation(screenName, i), popUpSkin);
            Label info = new Label(String.valueOf(getInfoValues(screenName, i)), popUpSkin);
            infoTitle.setFontScale(1.5f);
            info.setFontScale(1.5f);
            setting.add(infoTitle).left();
            setting.add(info).right();
            setting.row().padTop(20f);
        }

        setting.row().padTop(20f);
        setting.add(new Label("Volume :", skin)).left();
        setting.add(ScreenSpecialElement(screenName)).right();

        setting.row().padTop(20f);
        setting.add(closeButton).bottom().right();
        setting.add(backButton).bottom().left();

        setting.setZIndex(0);
        return setting;

    }

    /* Creates and returns the unique widget element associated with the
     * unique pop up
     */
    private Widget ScreenSpecialElement(String screenName) {

        Widget element = null;

        if (screenName.equals("Pause Menu")) {
            element = new Slider(0f, 1f, 0.1f, false, skin);
            element = new Slider(0.2f, 2f, 0.1f, false, popUpSkin);
        } else if (screenName.equals("Score Screen")) {
            element = new SelectBox<>(popUpSkin);
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
            return index == 0 ? "Sound" :
                   index == 1 ? "Difficulty" :
                   "Resolution";
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
