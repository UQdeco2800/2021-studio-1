package com.deco2800.game.components.powerups;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.UIComponent;

/**
 * Class that graphically displays the amount of each power up a player has available.
 * Component needs to be added to MainGameScreen and to the player in PlayerFatcory to make it visible.
 */
public class PowerUpGUIComponent extends UIComponent {
    // Paths to power up images.
    private static final String SHIELD_IMG = "images/PowerUpGUI/Shield.png";
    private static final String SPEAR_IMG = "images/PowerUpGUI/Spear.png";
    private static final String LIGHTNING_IMG = "images/PowerUpGUI/Lightning.png";
    private static final String EMPTY_IMG = "images/PowerUpGUI/Empty.png";

    //Icons used for gui, assets loaded in MainGameScreen
    private final Image shield1 = new Image(ServiceLocator.getResourceService().getAsset(SHIELD_IMG, Texture.class));
    private final Image shield2 = new Image(ServiceLocator.getResourceService().getAsset(SHIELD_IMG, Texture.class));
    private final Image shield3 = new Image(ServiceLocator.getResourceService().getAsset(SHIELD_IMG, Texture.class));
    private final Image spear1 = new Image(ServiceLocator.getResourceService().getAsset(SPEAR_IMG, Texture.class));
    private final Image spear2 = new Image(ServiceLocator.getResourceService().getAsset(SPEAR_IMG, Texture.class));
    private final Image spear3 = new Image(ServiceLocator.getResourceService().getAsset(SPEAR_IMG, Texture.class));
    private final Image lightning = new Image(ServiceLocator.getResourceService().getAsset(LIGHTNING_IMG, Texture.class));
    private final Image empty1 = new Image(ServiceLocator.getResourceService().getAsset(EMPTY_IMG, Texture.class));
    private final Image empty2 = new Image(ServiceLocator.getResourceService().getAsset(EMPTY_IMG, Texture.class));
    private final Image empty3 = new Image(ServiceLocator.getResourceService().getAsset(EMPTY_IMG, Texture.class));
    private final Image empty4 = new Image(ServiceLocator.getResourceService().getAsset(EMPTY_IMG, Texture.class));
    private final Image empty5 = new Image(ServiceLocator.getResourceService().getAsset(EMPTY_IMG, Texture.class));
    private final Image empty6 = new Image(ServiceLocator.getResourceService().getAsset(EMPTY_IMG, Texture.class));
    private final Image empty7 = new Image(ServiceLocator.getResourceService().getAsset(EMPTY_IMG, Texture.class));


    /**
     * Creates and adds actors to stage for PowerUpGUIComponent
     */
    @Override
    public void create() {
        super.create();
        addActors();
        entity.getEvents().addListener("updatePowerUps", this::updatePowerUps);
    }

    /**
     * Creates power up icons, positions them in tables and sets them initially to not be visible
     */
    private void addActors() {
        float iconSize = 40f;
        //Shield Display
        Table emptyShieldDisplay = new Table();
        emptyShieldDisplay.top().right().padTop(5).padRight(5);
        emptyShieldDisplay.setFillParent(true);

        emptyShieldDisplay.add(empty1).size(iconSize).pad(2);
        emptyShieldDisplay.add(empty2).size(iconSize).pad(2);
        emptyShieldDisplay.add(empty3).size(iconSize).pad(2);

        Table shieldDisplay = new Table();
        shieldDisplay.top().right().padTop(5).padRight(5);
        shieldDisplay.setFillParent(true);

        shieldDisplay.add(shield1).size(iconSize).pad(2);
        shieldDisplay.add(shield2).size(iconSize).pad(2);
        shieldDisplay.add(shield3).size(iconSize).pad(2);
        shield1.setVisible(false);
        shield2.setVisible(false);
        shield3.setVisible(false);

        //Spear Display
        Table emptySpearDisplay = new Table();
        emptySpearDisplay.top().right().padTop(47f).padRight(5);
        emptySpearDisplay.setFillParent(true);

        emptySpearDisplay.add(empty4).size(iconSize).pad(2);
        emptySpearDisplay.add(empty5).size(iconSize).pad(2);
        emptySpearDisplay.add(empty6).size(iconSize).pad(2);


        Table spearDisplay = new Table();
        spearDisplay.top().right().padTop(47f).padRight(5);
        spearDisplay.setFillParent(true);

        spearDisplay.add(spear1).size(iconSize).pad(2);
        spearDisplay.add(spear2).size(iconSize).pad(2);
        spearDisplay.add(spear3).size(iconSize).pad(2);
        spear1.setVisible(false);
        spear2.setVisible(false);
        spear3.setVisible(false);

        //Lightning Display
        Table emptyLightningDisplay = new Table();
        emptyLightningDisplay.top().right().padTop(89f).padRight(5);
        emptyLightningDisplay.setFillParent(true);

        emptyLightningDisplay.add(empty7).size(iconSize).pad(2);

        Table lightningDisplay = new Table();
        lightningDisplay.top().right().padTop(89f).padRight(5);
        lightningDisplay.setFillParent(true);

        lightningDisplay.add(lightning).size(iconSize).pad(2);
        lightning.setVisible(false);

        stage.addActor(emptyShieldDisplay);
        stage.addActor(shieldDisplay);
        stage.addActor(emptySpearDisplay);
        stage.addActor(spearDisplay);
        stage.addActor(emptyLightningDisplay);
        stage.addActor(lightningDisplay);
    }

    /**
     * Updates the visible icons for each power up based on the players current amount of each power up
     */
    private void updatePowerUps() {
        //Set lightning icon on/off
        if (entity.getComponent(LightningPowerUpComponent.class).getEnabled()) {
            lightning.setVisible(true);
        } else if (!entity.getComponent(LightningPowerUpComponent.class).getEnabled()) {
            lightning.setVisible(false);
        }

        //Set shield icons on/off
        switch (entity.getComponent(ShieldPowerUpComponent.class).getBlocks()) {
            case 3:
                shield1.setVisible(true);
                shield2.setVisible(true);
                shield3.setVisible(true);
                break;
            case 2:
                shield1.setVisible(false);
                shield2.setVisible(true);
                shield3.setVisible(true);
                break;
            case 1:
                shield1.setVisible(false);
                shield2.setVisible(false);
                shield3.setVisible(true);
                break;
            case 0:
                shield1.setVisible(false);
                shield2.setVisible(false);
                shield3.setVisible(false);
                break;
            default:
                break;
        }

        //Set spear icons on/off
        if (entity.getComponent(SpearPowerUpComponent.class).getEnabled()) {
            switch (entity.getComponent(SpearPowerUpComponent.class).getThrown()) {
                case 0:
                    spear1.setVisible(true);
                    spear2.setVisible(true);
                    spear3.setVisible(true);
                    break;
                case 1:
                    spear1.setVisible(false);
                    spear2.setVisible(true);
                    spear3.setVisible(true);
                    break;
                case 2:
                    spear1.setVisible(false);
                    spear2.setVisible(false);
                    spear3.setVisible(true);
                    break;
                case 3:
                    spear1.setVisible(false);
                    spear2.setVisible(false);
                    spear3.setVisible(false);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Draw the renderable. Should be called only by the renderer, not manually.
     *
     * @param batch Batch to render to.
     */
    @Override
    protected void draw(SpriteBatch batch) {
        //draw is handled by the stage
    }

}