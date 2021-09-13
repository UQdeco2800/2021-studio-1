package com.deco2800.game.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.deco2800.game.components.Component;
import com.deco2800.game.extensions.GameExtension;
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

    private Entity mockGameEntity = new Entity();

    @Test
    void UIPopIsEntityTest() {

        Entity entity = mock(Entity.class);
        UIPop pop = new UIPop("Pause Menu", mockGameEntity);
        entity.addComponent(pop);

        assertTrue(entity.getId() >= 0);
        verify(entity).getId();
    }

    @Test
    void UIPopAttachesToEntityTest() {

        Entity ent = mock(Entity.class);
        UIPop p = new UIPop("Help Screen", mockGameEntity);
        ent.addComponent(p);
        verify(ent).addComponent(p);
    }

    @Test
    void UIPopIsUnique() {

        Entity ent1 = mock(Entity.class);
        ent1.addComponent(new UIPop("Pause Menu", mockGameEntity));
        Entity ent2 = mock(Entity.class);
        ent2.addComponent(new UIPop("Pause Menu", mockGameEntity));

        assertTrue(ent1 != ent2);
    }

    @Test
    void DefaultPopHasCorrectName() {

        UIPop popUp = mock(UIPop.class);
        when(popUp.GetName()).thenReturn("Help Screen");
        assertEquals(popUp.GetName(), "Help Screen");

        UIPop pop = new UIPop("Help Screen", mockGameEntity);
        assertEquals(pop.GetName(), "Help Screen");

        assertEquals(popUp.GetName(), pop.GetName());
    }

    @Test
    void ScoreScreenPopHasCorrectName() {

        UIPop popUp = mock(UIPop.class);
        when(popUp.GetName()).thenReturn("Score Screen");
        assertEquals(popUp.GetName(), "Score Screen");

        UIPop pop = new UIPop("Score Screen", mockGameEntity);
        assertEquals(pop.GetName(), "Score Screen");

        assertEquals(popUp.GetName(), pop.GetName());
    }

    @Test
    void PauseMenuPopHasCorrectName() {

        UIPop popUp = mock(UIPop.class);
        when(popUp.GetName()).thenReturn("Pause Menu");
        assertEquals(popUp.GetName(), "Pause Menu");

        UIPop pop = new UIPop("Pause Menu", mockGameEntity);
        assertEquals(pop.GetName(), "Pause Menu");

        assertEquals(popUp.GetName(), pop.GetName());
    }

    @Test
    void UIPopNamesTest() {

        UIPop pop = mock(UIPop.class);

        Set<String> namesSet = new HashSet<>();
        namesSet.add("Help Screen");
        namesSet.add("Score Screen");
        namesSet.add("Pause Menu");

        assertEquals(UIPop.GetPossibleUIScreens(), namesSet);
        assertEquals(pop.GetPossibleUIScreens(), namesSet);
    }

    @Test
    void UIPopNotInScreensTest() {

        UIPop pop;

        try {
            pop = new UIPop("Not in screens list", mockGameEntity);
        } catch (NoSuchElementException e) {
            assertTrue(true); //caught
            return;
        }

        fail();
    }

    @Test
    void UnmodifiableInstanceTest() {

        UIPop pop = new UIPop("Pause Menu", mockGameEntity);

        String screenName = pop.GetName();

        assertEquals("Pause Menu", screenName);

        screenName = pop.GetName() + "Trying to change";

        assertEquals("Pause Menu", pop.GetName());
    }

    @Test
    void shouldCreatePop() {

        Entity entity = new Entity();
        Component component1 = mock(UIPop.class);
        entity.addComponent(component1);
        entity.create();
        verify(component1).create();

        UIPop component2 = mock(UIPop.class);
        component2.create();

        verify(component2).create();
    }

    @Test
    void popShouldDispose() {

        EntityService entityService = new EntityService();
        Entity entity = mock(Entity.class);

        UIPop pop = mock(UIPop.class);
        entity.addComponent(pop);

        entityService.register(entity);
        entityService.dispose();

        verify(entity).dispose();
    }
 }
