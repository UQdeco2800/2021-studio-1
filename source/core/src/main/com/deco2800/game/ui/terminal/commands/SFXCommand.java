package com.deco2800.game.ui.terminal.commands;

import com.deco2800.game.services.ServiceLocator;

import java.security.Provider;
import java.util.ArrayList;

public class SFXCommand implements Command {
    public boolean action(ArrayList<String> args) {

        if (!isValid(args)) return false;

        // format: sfx key

        // if args = 3, do routine for setting

        ServiceLocator.getSoundService().playSound(args.get(0));
        if (args.size() == 2) {

            // routine for chaning sound volume

        }

        return true;

    }

    boolean isValid(ArrayList<String> args) {
        return (args.size() == 1 || args.size() == 3);
    }
}
