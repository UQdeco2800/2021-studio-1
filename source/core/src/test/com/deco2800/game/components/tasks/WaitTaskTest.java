package com.deco2800.game.components.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.deco2800.game.ai.tasks.Task.Status;
import com.deco2800.game.extensions.GameExtension;
import com.deco2800.game.services.GameTime;
import com.deco2800.game.services.ServiceLocator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class WaitTaskTest {
  @Test
  void shouldWaitUntilTime() {
    GameTime time = mock(GameTime.class);
    when(time.getTime()).thenReturn(1000L);
    ServiceLocator.registerTimeSource(time);

    // Create a 5s wait task
    WaitTask task = new WaitTask(5f);
    task.start();
    assertEquals(Status.ACTIVE, task.getStatus());

    // The task should still be active when 4000ms have elapsed
    when(time.getTime()).thenReturn(4000L);
    task.update();
    assertEquals(Status.ACTIVE, task.getStatus());

    // Once 6100ms have elapsed, the task should be finished
    when(time.getTime()).thenReturn(6100L);
    task.update();
    assertEquals(Status.FINISHED, task.getStatus());
  }
}