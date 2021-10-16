package com.deco2800.game.components.tasks;

import com.deco2800.game.ai.tasks.DefaultTask;
import com.deco2800.game.ai.tasks.PriorityTask;
import com.deco2800.game.entities.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeathTask extends DefaultTask implements PriorityTask {
    private static final Logger logger = LoggerFactory.getLogger(DeathTask.class);

    private Entity dyingThing;
    private final float waitTime;
    private WaitTask waitTask;

    public DeathTask(Entity dyingThing){
        this.dyingThing = dyingThing;
        this.waitTime = 0;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    public void start(){
        super.start();

        waitTask = new WaitTask(waitTime);
        waitTask.create(owner);

        this.owner.getEntity().getEvents().trigger("death");
    }

    @Override
    public void update() {

    }
}
