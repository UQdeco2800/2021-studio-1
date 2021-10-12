package com.deco2800.game.ui.terminal.commands;

import com.deco2800.game.services.ServiceLocator;

import java.util.ArrayList;

/**
 * Command to interface to music portion of the SoundService.
 * Can play music and change volume.
 */
public class MusicCommand implements Command {
    /**
     * Does the action based on input.
     * Format:
     * -music key|setting [setting value]
     * @param args command args
     * @return
     */
    public boolean action(ArrayList<String> args) {

        if (!isValid(args)) return false;

        // format: music key/setting [value]

        // if args = 3, do routine for setting

        if (args.size() == 1) {
            ServiceLocator.getSoundService().playMusic(args.get(0));
            return true;
        }

        // if there are more arguments
        if (args.size() == 2) {

            float value;
            // potentially risky conversion
            try {
                String arg = args.get(1).replace(',', '.');
                value = Float.parseFloat(arg);
            } catch (Exception e) {
                return false;
            }

            // request change in volume
            switch (args.get(0)) {
                case "-vol":
                    ServiceLocator.getSoundService().setMusicVolume(value);
                    return true;
            }

        }

        return true;

    }

    /**
     * Checks there is a valid amount of arguments.
     * @param args the amount of arguments given.
     * @return true if valid, false if not.
     */
    boolean isValid(ArrayList<String> args) {
        return (args.size() == 1 || args.size() == 2);
    }
}
