package com.deco2800.game.components;

import com.deco2800.game.entities.Entity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GroupDisposeComponentTest {
    @Mock
    Entity entity1;

    @Mock
    Entity entity2;

    @Mock
    Entity entity3;

    @Test
    void dispose() {
        GroupDisposeComponent gdc = new GroupDisposeComponent(new Entity[]{entity1, entity2, entity3});

        gdc.dispose();

        verify(entity1).flagDelete();
        verify(entity2).flagDelete();
        verify(entity3).flagDelete();
    }
}