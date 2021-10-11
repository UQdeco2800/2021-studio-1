package com.deco2800.game.ui.terminal.commands;

import com.deco2800.game.services.ServiceLocator;

import java.util.ArrayList;

public class MusicCommand implements Command {
    public boolean action(ArrayList<String> args) {

        if (!isValid(args)) return false;

        // format: music key/setting [value]

        // if args = 3, do routine for setting

        if (args.size() == 1) {
            ServiceLocator.getSoundService().playMusic(args.get(0));
            return true;
        }

        if (args.size() == 2) {

            float value;
            try {
                String arg = args.get(1).replace(',', '.');
                value = Float.parseFloat(arg);
            } catch (Exception e) {
                return false;
            }

            switch (args.get(0)) {
                case "vol":
                    ServiceLocator.getSoundService().setMusicVolume(value);
                    return true;
                case "pan":
                    ServiceLocator.getSoundService().setMusicPan(value);
                    return true;
            }

        }

        return true;

    }

    boolean isValid(ArrayList<String> args) {
        return (args.size() == 1 || args.size() == 2);
    }
}
