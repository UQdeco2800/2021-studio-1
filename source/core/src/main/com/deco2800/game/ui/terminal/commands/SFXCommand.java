package com.deco2800.game.ui.terminal.commands;

import com.deco2800.game.services.ServiceLocator;

import java.security.Provider;
import java.util.ArrayList;

/**
 * Command to interface to the SoundFX portion of the
 * SoundService.
 */
public class SFXCommand implements Command {
    /**
     * Command to either play a sound effect or
     * change a setting.
     * Format:
     * -sfx key|setting [setting value]
     * @param args command args
     * @return true if action has been carried out, false otherwise
     */
    public boolean action(ArrayList<String> args) {

        if (!isValid(args)) return false;

        if (args.size() == 1) ServiceLocator.getSoundService().playSound(args.get(0));
        //if there are more arguments
        else if (args.size() == 2) {

            float value;
            try {
                String arg = args.get(1).replace(',', '.');
                value = Float.parseFloat(arg);
            } catch (Exception e) {
                return false;
            }

            if (args.get(0).equals("-vol")) {
                ServiceLocator.getSoundService().setSfxVolume(value);
                return true;
            }
        }

        return true;

    }

    boolean isValid(ArrayList<String> args) {
        return (args.size() == 1 || args.size() == 3);
    }
}
