package com.deco2800.game.components.powerups;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.UIComponent;

/**
 * Class that graphically displays the amount of each power up a player has available.
 * Component need to be added to MainGameScreen and to the player in PlayerFatcory to make it visible.
 */
public class PowerUpGUIComponent2 extends UIComponent {
        //Icons used for gui, assets loaded in MainGameScreen
        private final Image shield0 = new Image(ServiceLocator.getResourceService().getAsset("images/PowerUpGUI/shield0.png", Texture.class));
        private final Image shield1 = new Image(ServiceLocator.getResourceService().getAsset("images/PowerUpGUI/shield1.png", Texture.class));
        private final Image shield2 = new Image(ServiceLocator.getResourceService().getAsset("images/PowerUpGUI/shield2.png", Texture.class));
        private final Image shield3 = new Image(ServiceLocator.getResourceService().getAsset("images/PowerUpGUI/shield3.png", Texture.class));
        private final Image spear0 = new Image(ServiceLocator.getResourceService().getAsset("images/PowerUpGUI/spear0.png", Texture.class));
        private final Image spear1 = new Image(ServiceLocator.getResourceService().getAsset("images/PowerUpGUI/spear1.png", Texture.class));
        private final Image spear2 = new Image(ServiceLocator.getResourceService().getAsset("images/PowerUpGUI/spear2.png", Texture.class));
        private final Image spear3 = new Image(ServiceLocator.getResourceService().getAsset("images/PowerUpGUI/spear3.png", Texture.class));
        private final Image lightning0 = new Image(ServiceLocator.getResourceService().getAsset("images/PowerUpGUI/lightning0.png", Texture.class));
        private final Image lightning1 = new Image(ServiceLocator.getResourceService().getAsset("images/PowerUpGUI/lightning1.png", Texture.class));

        //place holder icons that will never be set to visible
        private final Image lightning2 = new Image(ServiceLocator.getResourceService().getAsset("images/PowerUpGUI/lightning1.png", Texture.class));
        private final Image lightning3 = new Image(ServiceLocator.getResourceService().getAsset("images/PowerUpGUI/lightning1.png", Texture.class));

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
            float iconSize = 50f;

            //Table holding 0 icons
            Table table0 = new Table();
            table0.top().right().padTop(10);
            table0.setFillParent(true);

            table0.add(shield0).size(iconSize).pad(5);
            table0.add(spear0).size(iconSize).pad(5);
            table0.add(lightning0).size(iconSize).pad(5);
            shield0.setVisible(true);
            spear0.setVisible(true);
            lightning0.setVisible(true);

            //Table holding 1 icons
            Table table1 = new Table();
            table1.top().right().padTop(10);
            table1.setFillParent(true);

            table1.add(shield1).size(iconSize).pad(5);
            table1.add(spear1).size(iconSize).pad(5);
            table1.add(lightning1).size(iconSize).pad(5);
            shield1.setVisible(false);
            spear1.setVisible(false);
            lightning1.setVisible(false);

            //Table holding 2 icons
            Table table2 = new Table();
            table2.top().right().padTop(10);
            table2.setFillParent(true);

            table2.add(shield2).size(iconSize).pad(5);
            table2.add(spear2).size(iconSize).pad(5);
            table2.add(lightning2).size(iconSize).pad(5);
            shield2.setVisible(false);
            spear2.setVisible(false);
            lightning2.setVisible(false);

            //Table holding 3 icons
            Table table3 = new Table();
            table3.top().right().padTop(10);
            table3.setFillParent(true);

            table3.add(shield3).size(iconSize).pad(5);
            table3.add(spear3).size(iconSize).pad(5);
            table3.add(lightning3).size(iconSize).pad(5);
            shield3.setVisible(false);
            spear3.setVisible(false);
            lightning3.setVisible(false);


            stage.addActor(table0);
            stage.addActor(table1);
            stage.addActor(table2);
            stage.addActor(table3);
        }

        /**
         * Updates the visible icons for each power up based on the players current amount of each power up
         */
        private void updatePowerUps() {
            //Set lightning icon on/off
            if (entity.getComponent(LightningPowerUpComponent.class).getEnabled()) {
                lightning0.setVisible(false);
                lightning1.setVisible(true);
            } else if (!entity.getComponent(LightningPowerUpComponent.class).getEnabled()) {
                lightning0.setVisible(true);
                lightning1.setVisible(false);
            }

            //Set shield icons on/off
            switch (entity.getComponent(ShieldPowerUpComponent.class).getBlocks()) {
                case 3:
                    shield0.setVisible(false);
                    shield1.setVisible(false);
                    shield2.setVisible(false);
                    shield3.setVisible(true);
                    break;
                case 2:
                    shield0.setVisible(false);
                    shield1.setVisible(false);
                    shield2.setVisible(true);
                    shield3.setVisible(false);
                    break;
                case 1:
                    shield0.setVisible(false);
                    shield1.setVisible(true);
                    shield2.setVisible(false);
                    shield3.setVisible(false);
                    break;
                case 0:
                    shield1.setVisible(false);
                    shield0.setVisible(true);
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
                        spear0.setVisible(false);
                        spear1.setVisible(false);
                        spear2.setVisible(false);
                        spear3.setVisible(true);
                        break;
                    case 1:
                        spear0.setVisible(false);
                        spear1.setVisible(false);
                        spear2.setVisible(true);
                        spear3.setVisible(false);
                        break;
                    case 2:
                        spear0.setVisible(false);
                        spear1.setVisible(true);
                        spear2.setVisible(false);
                        spear3.setVisible(false);
                        break;
                    case 3:
                        spear0.setVisible(true);
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
