package com.deco2800.game.entities.factories;

import com.badlogic.gdx.graphics.Texture;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.extensions.GameExtension;
import com.deco2800.game.services.ResourceService;
import com.deco2800.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
class ObstacleFactoryTest {
    @Mock ResourceService resourceService;

    @BeforeEach
    void beforeEach() {
        ServiceLocator.registerResourceService(resourceService);
    }

    @Test
    void createBackground() {
        Entity background = ObstacleFactory.createBackground(null, 15);

        verify(resourceService).getAsset("images/Backgrounds/black_back.png", Texture.class);
        assertEquals(15 * 1.5, background.getScale().x); // tests width is correctly scaled
    }

    @Test
    void createBackgroundHel() {
        Entity background = ObstacleFactory.createBackground("hel_5432", 15);

        verify(resourceService).getAsset("images/Backgrounds/Background Hel.png", Texture.class);
    }
}