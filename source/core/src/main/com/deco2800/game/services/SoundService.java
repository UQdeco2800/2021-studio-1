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

    private Hashtable<String, String> soundTable;
    private Hashtable<String, String> musicTable;
    private String[] soundArray;
    private String[] musicArray;

    public boolean isLoaded;

    private ResourceService resources;

    private boolean stompOn = false;

    private long nextStomp = 0; // which value (in milliseconds) the next stomp shoud startd
    private double distanceMultiplier = 0;

    private String iniFilePath;

    private Sound giantSound;

    private Music currentTrack;

    private float musicVolume = 1f;

    /**
     * SoundService handles the playing of sounds and music in the game.
     * Please make a new initFile for each screen of the game, to save with memory issues.
     * @param initFile The Sound file to load [initFile].ini
     */
    public SoundService(String initFile) {
        iniFilePath = "sounds/service_files/" + initFile + ".ini";
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

        //String filepath = "configs/sound.ini";
        String filepath = iniFilePath;

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

                //System.out.printf("key: %s, value: %s\n", split[0], split[1]);

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

            for (String s : soundArray) {
                logger.debug(s);
            }

            resources.loadSounds(soundArray);

            // Loads the table of music into the resource manager.
            ArrayList<String> musicList = new ArrayList<>(musicTable.values());
            musicArray = new String[musicList.size()];
            musicArray = musicList.toArray(musicArray);

            for (String s : musicArray) {
                logger.debug(s);
            }

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

        if (currentTrack != null) currentTrack.stop();
        if (giantSound != null) giantSound.stop();

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
            } else if (sound.equals("onstomp")) {
                stompOn = true;
            } else if (sound.equals("offstomp")) {
                stompOn = false;
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

                if (currentTrack != null) {
                    currentTrack.stop();
                    currentTrack.dispose();
                }

                logger.debug("{}, resources contain track {}",
                        resources.containsAsset(musicTable.get(music), Music.class), music);

                currentTrack = resources.getAsset(musicTable.get(music), Music.class);
                currentTrack.play();

                //currentTrackId = currentTrack.play();
                //currentTrack.setVolume(currentTrackId, musicVolume);
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

        /* the distance multipler is given by the function
            { dist <= 25        mul = 1
            { 25 < dist < 48    mul = sin(2pi * dist / 97)
            { dist >= 48        mul = 0.05

            the equation was just worked out through testing what sounded nice
         */

        if (distance <= 25f) {
            distanceMultiplier = 1;
        } else if (25f < distance && distance < 48f) {
            distanceMultiplier = Math.sin(2*Math.PI*distance/97);
        } else if (distance >= 48f) {
            distanceMultiplier = 0.05f;
        }
    }

    private void playStomp() {

        if (!stompOn) return;

        int stompNum = (int) Math.floor(Math.random() * 4);
        String playSound = String.format("stomp%d", stompNum);

        giantSound = resources.getAsset(soundTable.get(playSound), Sound.class);
        long newId = giantSound.play();
        giantSound.setVolume(newId, (float) distanceMultiplier);
        giantSound.setPitch(newId, (float)distanceMultiplier * 0.5f + 1f);

        nextStomp += 1000 + 1000 * (1 - distanceMultiplier);
    }

    public void setMusicVolume(float volume) {
        musicVolume = volume;
        if (currentTrack != null) currentTrack.setVolume(musicVolume);
    }

    public void setMusicLoop(boolean value) {
        if (currentTrack != null) currentTrack.setLooping(value);
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