package com.unfleshed.Components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;

public class EyesComponent implements Component, AutoSyncedComponent {

    public enum EyeState { HUMAN, BLIND, ARCANE }

    private EyeState state = EyeState.HUMAN;
    private final Object provider; // usually the PlayerEntity

    public EyesComponent(Object provider) {
        this.provider = provider;
    }

    // Getter & setter
    public EyeState getState() { return state; }

    public void setState(EyeState newState) {
        this.state = newState;
        MyComponents.EYES.sync(provider); // auto-sync with client
    }

    // --- NBT Serialization ---
    @Override
    public void readFromNbt(NbtCompound tag) {
        state = EyeState.values()[tag.getInt("state")];
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("state", state.ordinal());
    }
    public String toString() {
        return state.name().toLowerCase(); // or just state.name() for uppercase
    }
}
