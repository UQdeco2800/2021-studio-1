package com.deco2800.game.services;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

public class SoundService {

    private static final Logger logger = LoggerFactory.getLogger(SoundService.class);

    private static Hashtable<String, String> soundTable;
    private static Hashtable<String, String> musicTable;
    private static String[] soundArray;
    private static String[] musicArray;

    public static boolean isLoaded;

    private static ResourceService resources;

    private static Music currentTrack;

    public SoundService() {

    }

    /**
     * Loads all the assets from the ResourceService, so they can be called
     * throughout the class.
     *
     * This must be called before any sounds can be played.
     */
    public void loadAssets() {

        // assigns resouces and initalises the tables used to retrieve sounds
        resources = ServiceLocator.getResourceService();
        soundTable = new Hashtable<>();
        musicTable = new Hashtable<>();
        isLoaded = false;

        String filepath = "configs/sound.ini";

        Hashtable<String, String> assigningTo = null;
        BufferedReader config;

        // loads in sounds and their keys from the sound.ini file
        try {
            config = new BufferedReader(new FileReader(filepath));

            String l;
            while ((l = config.readLine()) != null) {

                if (l.length() == 0) continue;
                if (l.charAt(0) == '#' || l.charAt(0) == '\n') continue;

                if (l.equals("[sounds]")) {
                    assigningTo = soundTable;
                    continue;
                } else if (l.equals("[music]")) {
                    assigningTo = musicTable;
                    continue;
                }

                String[] split = l.split(":");

                System.out.printf("key: %s, value: %s\n", split[0], split[1]);

                if (assigningTo == null) {
                    logger.error("Error reading file");
                    throw new IOException();
                }

                assigningTo.put(split[0], split[1]);

            }

            // Loads the table of sounds into the resource manager.
            ArrayList<String> soundList = new ArrayList<>(soundTable.values());
            soundArray = new String[soundList.size()];
            soundArray = soundList.toArray(soundArray);

            int i=0;
            for (String s : soundArray) {
                System.out.printf("%d %s\n", i++, s);
            }

            resources.loadSounds(soundArray);

            // Loads the table of music into the resource manager.
            ArrayList<String> musicList = new ArrayList<>(musicTable.values());
            musicArray = new String[musicList.size()];
            musicArray = musicList.toArray(musicArray);

            i=0;
            for (String s : musicArray) {
                System.out.printf("%d %s\n", i++, s);
            }

            resources.loadMusic(musicArray);
            // Code is heavily repeated due to sounds and music being managed
            // differently in the ResourceService

            isLoaded = true;

        } catch(FileNotFoundException e) {
            logger.error("File {} could not be loaded", filepath);
            isLoaded = false;
        } catch (IOException e) {
            logger.error("IOException while reading {}", filepath);
            isLoaded = false;
        }



    }

    /**
     * Unloads all assets in the Sound Service.
     * Because of the way the game manages Services, a Sound Service must be
     * initialised for each screen of the game.
     */
    public void unloadAssets() {
        // unloads the sound effects
        resources.unloadAssets(soundArray);

        // unloads the music
        resources.unloadAssets(musicArray);
    }

    /**
     * Accepts a string that is used as index to the Hashtable of sounds
     * Certain sounds have different subroutines associated with them.
     *
     * The stomping sound effect linked to the BPM of whichever song is playing
     *
     *
     * @param sound
     */
    public void playSound(String sound) {
        if (isLoaded) {
            if (soundTable.containsKey(sound)) {
                Sound toPlay = resources.getAsset(soundTable.get(sound), Sound.class);
                toPlay.play();
            } else {
                logger.debug("{} is not a key", sound);
            }
        } else {
            logger.error("playSound called without loading sounds");
        }
    }

    /**
     *
     * @param music
     */
    public void playMusic(String music) {
        if (isLoaded) {
            if (musicTable.containsKey(music)) {
                currentTrack = resources.getAsset(musicTable.get(music), Music.class);
                currentTrack.play();
            } else {
                logger.debug("{} is not a key", music);
            }
        } else {
            logger.error("playMusic called without loading sounds");
        }
    }

    public void setGiantDistance(float distance) {

    }
}

interface loadNew {
    abstract void load(String key, String value);
}