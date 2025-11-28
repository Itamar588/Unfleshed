package com.unfleshed.damage;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ModDamageTypes {

    public static final RegistryKey<DamageType> DISMEMBERMENT_DAMAGE =
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("unfleshed", "dismemberment"));

    public static DamageSource dismemberment(World world) {
        RegistryEntry<DamageType> typeEntry = world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(DISMEMBERMENT_DAMAGE);
        return new DamageSource(typeEntry);
    }
}

