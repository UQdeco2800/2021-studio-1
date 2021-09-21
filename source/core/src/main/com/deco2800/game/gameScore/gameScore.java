package com.deco2800.game.gameScore;


import com.deco2800.game.GdxGame;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.Component;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.services.GameTime;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.UIPop;

import java.util.function.ToDoubleBiFunction;


/**
 * The class handles the scoring of the player the score is increased linearly on the basis of time
 * elapsed since the beginning of the start.
 */
public class gameScore extends Component {


   // GameTime time = new GameTime();
    //GdxGame gdxGame = new GdxGame();

    @Override
    public Entity getEntity() {
        return super.getEntity();
    }

    private  GdxGame gdxGame = new GdxGame();
    private static long score = 0;
    private long current_time;
    private long paused_score;
    private long previous_score = 1;
    public Boolean pause = false;

    /**
     * Returns the current score to anywhere in the game
     *
     * @return current score
     **/
    public long getCurrentScore() {
        if (entity != null) {
          entity.getEvents().trigger("updateScore", score);
        }
        // if the game is not paused increment the score
        //TODO find the event attached to players death (Bach)
        if (ServiceLocator.getTimeSource().getDeltaTime() != 0  ){//&&
             // getEntity().getComponent(CombatStatsComponent.class).isDead() ){
            //score++;
            previous_score += (long) (previous_score * 0.001);
            score += (long) Math.floor(previous_score );
        }
        return score;
        // return score;
    }
   /*
    public void getCurrentTime() {
        current_time = (time.getTime() + 100);
    }
    */


    /**
     * Sets the current score on the basis of timeelapsed since the start of the game
     **/

    public void setScore() {
        if (gdxGame.paused) {
            //donothing
        } else {
           // getCurrentTime();
            //score = current_time / 100;
            if (entity != null) {
                entity.getEvents().trigger("updateScore", score);
            }
        }
    }

    /**
     * Score depletion on the basis of object being hit
     */
    public void enemyOneKilled() {

        score -= 1000;
    }

    /**
     * Score depletion on the basis of object being hit
     */

    public void obstacleTwoHit() {
        score -= 100;

    }

}