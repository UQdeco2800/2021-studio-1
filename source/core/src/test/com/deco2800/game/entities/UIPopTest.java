package com.deco2800.game.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import com.deco2800.game.extensions.GameExtension;
import com.deco2800.game.rendering.RenderService;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.UIComponent;
import com.deco2800.game.ui.UIPop;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;


@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
public class UIPopTest {

    RenderService service;
    EntityService entityService;

    @Test
    void UIPopIsEntityTest() {

        ServiceLocator.registerEntityService(entityService);
        ServiceLocator.registerRenderService(service);

        Entity ent = new Entity();
        ent.addComponent(new UIPop("Default Pop"));

        assertTrue(ent.getId() >= 0);
    }

    @Test
    void UIPopAttatchesToEntityTest() {

        ServiceLocator.registerEntityService(entityService);
        ServiceLocator.registerRenderService(service);

        Entity ent = new Entity();
        ent.addComponent(new UIPop("Default Pop"));

        assertNotNull(ent.getComponent(UIPop.class));
    }

    @Test
    void UIPopIsNotEmpty() {
        Entity ent = new Entity();
        Entity ent2 = new Entity();
        ent2.addComponent(new UIPop("Default Pop"));
        assertNull(ent.getComponent(UIPop.class));
        assertNotNull(ent2.getComponent(UIPop.class));
    }

    @Test
    void UIPopIsUnique() {
        Entity ent1 = new Entity();
        ent1.addComponent(new UIPop("Pause Menu"));
        Entity ent2 = new Entity();
        ent2.addComponent(new UIPop("Default Pop"));

        assertTrue(!ent1.equals(ent2));
    }

    @Test
    void UIPopHasCorrectName() {

        String defName = "Default Pop";
        String scoreName = "Score Screen";
        String pauseName = "Pause Menu";

        UIPop  pop1 = new UIPop("Default Pop");
        UIPop pop2 = new UIPop("Score Screen");
        UIPop pop3 = new UIPop("Pause Menu");

        assertEquals(defName, pop1.GetName());
        assertEquals(scoreName, pop2.GetName());
        assertEquals(pauseName, pop3.GetName());
    }

    @Test
    void UIPopNamesTest() {

        Set<String> namesSet = new HashSet<>();
        namesSet.add("Default Pop");
        namesSet.add("Score Screen");
        namesSet.add("Pause Menu");

        assertEquals(UIPop.GetPossibleUIScreens(), namesSet);
    }

    @Test
    void UIPopNotInScreensTest() {

        try {
            UIPop pop = new UIPop("Not in screens list");
        } catch (NoSuchElementException e) {
            assertTrue(true); //caught
            return;
        }

        fail();
    }

    @Test
    void UnmodifiableInstanceTest() {

        UIPop pop = new UIPop("Default Pop");

        String screenName = pop.GetName();

        assertEquals("Default Pop", screenName);

        screenName = pop.GetName() + "Trying to change";

        assertEquals("Default Pop", pop.GetName());
    }
}
