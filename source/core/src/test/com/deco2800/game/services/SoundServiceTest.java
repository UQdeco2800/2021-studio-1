package com.deco2800.game.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SoundServiceTest {

    @Test
    void loadAssets() {

        // try to load all of the current config files,
        // then nullify the soundservice and try again

        ServiceLocator.registerResourceService(new ResourceService());

        SoundService sound = new SoundService("storyScreen");
        assertFalse(sound.isLoaded);
        sound.loadAssets();
        assertTrue(sound.isLoaded);

        sound = new SoundService("mainGame");
        assertFalse(sound.isLoaded);
        sound.loadAssets();
        assertTrue(sound.isLoaded);

        sound = new SoundService("mainMenu");
        assertFalse(sound.isLoaded);
        sound.loadAssets();
        assertTrue(sound.isLoaded);

    }

    @Test
    void unloadAssets() {
        ServiceLocator.registerResourceService(new ResourceService());
        SoundService sound = new SoundService("mainGame");
        assertFalse(sound.isLoaded);
        sound.loadAssets();
        sound.unloadAssets();
        assertFalse(sound.isLoaded);
    }
}