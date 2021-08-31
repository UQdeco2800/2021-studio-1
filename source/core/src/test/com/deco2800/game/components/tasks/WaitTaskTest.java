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

    //this float duration needs to be increased in accordance with
    //changes to WaitTask modifications it is currently set to 300
    // three times lower than what it was previously
    WaitTask task = new WaitTask(17f);
    task.start();
    assertEquals(Status.ACTIVE, task.getStatus());

    when(time.getTime()).thenReturn(5000L);
    task.update();
    assertEquals(Status.ACTIVE, task.getStatus());

    when(time.getTime()).thenReturn(6100L);
    task.update();
    assertEquals(Status.FINISHED, task.getStatus());
  }
}