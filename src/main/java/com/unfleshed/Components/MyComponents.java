package com.unfleshed.Components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.util.Identifier;

public class MyComponents {
    public static final ComponentKey<EyesComponent> EYES =
            ComponentRegistry.getOrCreate(new Identifier("unfleshed", "eyes"), EyesComponent.class);
}
