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
import java.util.Hashtable;

public class SoundService {

    private static final Logger logger = LoggerFactory.getLogger(SoundService.class);

    private static Hashtable<String, String> soundTable;
    private static Hashtable<String, String> musicTable;
    private static String[] soundArray;
    private static String[] musicArray;

    public static boolean isLoaded;
    public static boolean isGiant;

    private static ResourceService resources;

    private static boolean stompOn = false;

    private static long nextStomp = 0; // which value (in milliseconds) the next stomp shoud startd
    private static double distanceMultiplier = 0;

    private static Sound giantSound;
    private static long giantWalkingId;

    private static Music currentTrack;
    private static float musicVolume = 1f;

    public SoundService() {

    }

    /**
     * Loads all the assets from the ResourceService, so they can be called
     * throughout the class.
     * <p>
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

            resources.loadSounds(soundArray);

            // Loads the table of music into the resource manager.
            ArrayList<String> musicList = new ArrayList<>(musicTable.values());
            musicArray = new String[musicList.size()];
            musicArray = musicList.toArray(musicArray);

            resources.loadMusic(musicArray);
            // Code is heavily repeated due to sounds and music being managed
            // differently in the ResourceService

            isLoaded = true;

            config.close();

        } catch (FileNotFoundException e) {
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
     * <p>
     * The stomping sound effect linked to the BPM of whichever song is playing
     *
     * @param sound
     */
    public void playSound(String sound) {
        if (isLoaded) {
            if (soundTable.containsKey(sound)) {
                Sound toPlay = resources.getAsset(soundTable.get(sound), Sound.class);
                long id = toPlay.play();
                toPlay.setPitch(id, (float) (Math.random() * 1.5 + 0.5));
            } else if (sound.equals("stomp")) {
                stompOn = !stompOn;
            } else {
                logger.debug("{} is not a key", sound);
            }
        } else {
            logger.error("playSound called without loading sounds");
        }
    }

    /**
     * @param music
     */
    public void playMusic(String music) {
        if (isLoaded) {
            if (musicTable.containsKey(music)) {
                if (currentTrack != null) currentTrack.stop();
                currentTrack = resources.getAsset(musicTable.get(music), Music.class);
                currentTrack.setVolume(musicVolume);
                currentTrack.play();
            } else {
                logger.debug("{} is not a key", music);
            }
        } else {
            logger.error("playMusic called without loading sounds");
        }
    }


    /**
     * Function that remodels the previous Giant Sound effect manipulation
     * in the CameraShakeComponent.
     * <p>
     * If the giant is >32f distance away, it's volume is determined by
     * vol = sin(2pi*distance/120f)
     *
     * @param distance a float value represnting the giant's distance
     *                 from the player
     */
    public void setGiantDistance(float distance) {

        if (nextStomp < ServiceLocator.getTimeSource().getTime()) return;

        if (distance <= 30f) {
            distanceMultiplier = 1;
        } else if (distance > 30f) {
            distanceMultiplier = Math.sin(2*Math.PI*distance/120);
        } else if (distance <= 60f) {
            distanceMultiplier = 0;
        }
    }

    private void playStomp() {

        if (!stompOn) return;

        int stompNum = (int) Math.floor(Math.random() * 4);
        String playSound = String.format("stomp%d", stompNum);

        giantSound = resources.getAsset(soundTable.get(playSound), Sound.class);
        giantWalkingId = giantSound.play();

        giantSound.setVolume(giantWalkingId, (float) distanceMultiplier);
        giantSound.setPan(giantWalkingId, -1f, (float)distanceMultiplier);
        giantSound.setPitch(giantWalkingId, (float)distanceMultiplier * 1.5f - 0.5f);

        nextStomp += 1000 + 2000 * (1 - distanceMultiplier);
    }

    public void setMusicVolume(float volume) {
        musicVolume = volume;
        currentTrack.setVolume(musicVolume);
    }

    public void setMusicPan(float pan) {
        currentTrack.setPan(pan, musicVolume);
    }

    public void update() {
        // run a routine to check if sound fx need to be played... ? use time source + list for next sound
        // instance stuff
        ServiceLocator.getTimeSource().getTime(); // in milliseconds

        long currentTime = ServiceLocator.getTimeSource().getTime(); // current time in seconds

        if (nextStomp < currentTime) {
            playStomp();
        }
    }
}