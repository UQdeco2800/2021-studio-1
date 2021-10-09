package com.deco2800.game.ui.terminal.commands;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.components.CombatStatsComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;



/* Command to enable god mode */
public class GodCommand implements Command {
    
    private static final Logger logger = LoggerFactory.getLogger(GodCommand.class);

    /**
     * Toggles god mode on or off
     */
    
    
    public boolean action(ArrayList<String> args) {    

        if (!isValid(args)) {
            logger.debug("Invalid arguments received for 'god' command: {}", args);
            return false;
        }
        
        ServiceLocator.getAreaService().getManager().getPlayer().getComponent(CombatStatsComponent.class).toggleInvincible();
        return true;
        }
    boolean isValid(ArrayList<String> args) {
        return args.size() == 0;
    }
}
