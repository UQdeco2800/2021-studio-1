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
    //Icons used for gui, assets loaded in MainGameScreen
    private final Image shield1 = new Image(ServiceLocator.getResourceService().getAsset("images/powerup-shield.png", Texture.class));
    private final Image shield2 = new Image(ServiceLocator.getResourceService().getAsset("images/powerup-shield.png", Texture.class));
    private final Image shield3 = new Image(ServiceLocator.getResourceService().getAsset("images/powerup-shield.png", Texture.class));
    private final Image spear1 = new Image(ServiceLocator.getResourceService().getAsset("images/powerup-spear.png", Texture.class));
    private final Image spear2 = new Image(ServiceLocator.getResourceService().getAsset("images/powerup-spear.png", Texture.class));
    private final Image spear3 = new Image(ServiceLocator.getResourceService().getAsset("images/powerup-spear.png", Texture.class));
    private final Image lightning = new Image(ServiceLocator.getResourceService().getAsset("images/powerup-lightning.png", Texture.class));

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
        //Shield Display
        Table shieldDisplay = new Table();
        shieldDisplay.top().right().padTop(5);
        shieldDisplay.setFillParent(true);

        float shieldSize = 50f;
        shieldDisplay.add(shield1).size(shieldSize).pad(5);
        shieldDisplay.add(shield2).size(shieldSize).pad(5);
        shieldDisplay.add(shield3).size(shieldSize).pad(5);
        shield1.setVisible(false);
        shield2.setVisible(false);
        shield3.setVisible(false);

        //Spear Display
        Table spearDisplay = new Table();
        spearDisplay.top().right().padTop(55f);
        spearDisplay.setFillParent(true);

        float spearSize = 50f;
        spearDisplay.add(spear1).size(spearSize).pad(5);
        spearDisplay.add(spear2).size(spearSize).pad(5);
        spearDisplay.add(spear3).size(spearSize).pad(5);
        spear1.setVisible(false);
        spear2.setVisible(false);
        spear3.setVisible(false);

        //Lightning Display
        Table lightningDisplay = new Table();
        lightningDisplay.top().right().padTop(80f);
        lightningDisplay.setFillParent(true);

        float lightningSize = 64f;
        lightningDisplay.add(lightning).size(lightningSize).pad(5);
        lightning.setVisible(false);

        stage.addActor(shieldDisplay);
        stage.addActor(spearDisplay);
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