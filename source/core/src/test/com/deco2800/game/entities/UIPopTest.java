package com.deco2800.game.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.components.Component;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.extensions.GameExtension;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.UIComponent;
import com.deco2800.game.ui.UIPop;
import com.deco2800.game.ui.terminal.Terminal;
import com.deco2800.game.ui.terminal.TouchTerminalInputComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(GameExtension.class)
public class UIPopTest {

    @Test
    void UIPopIsEntityTest() {

        //UIPop uiPop = new UIPop("Default Pop");
        //Entity ent = new Entity();
        //ent.addComponent(new UIPop("Default Pop"));

    }

    @Test
    void UIPopIsNotEmpty() {
        Entity ent = new Entity();
        Entity ent2 = new Entity();
        ent2.addComponent(new UIPop("Default Pop"));

        assertTrue(!ent.equals(ent2));
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
    void shouldUpdateMessageOnKeyTyped() {
    }

}
