package com.unfleshed.events;

import com.unfleshed.Components.EyesComponent;
import com.unfleshed.Components.MyComponents;
import dev.onyxstudios.cca.api.v3.entity.PlayerCopyCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public class PlayerCloneEvents {
    public static void register() {
        PlayerCopyCallback.EVENT.register((oldPlayer, newPlayer, alive) -> {
            // only copy if player actually died
            if (!alive) {
                EyesComponent oldEyes = MyComponents.EYES.get(oldPlayer);
                EyesComponent newEyes = MyComponents.EYES.get(newPlayer);

                newEyes.setState(oldEyes.getState());
            }
        });
    }
}
