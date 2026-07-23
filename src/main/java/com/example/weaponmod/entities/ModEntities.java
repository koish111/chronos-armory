package com.example.weaponmod.entities;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.entities.custom.BlackFireBallEntity;
import com.example.weaponmod.entities.custom.NullBladeDash;
import com.example.weaponmod.entities.renderer.BlackFireBallGeoRenderer;
import com.example.weaponmod.entities.renderer.NullBladeDashRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, WeaponMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<BlackFireBallEntity>> BLACK_FIRE_BALL =
            ENTITY_TYPES.register("black_fire_ball",
                    () -> EntityType.Builder.<BlackFireBallEntity>of(BlackFireBallEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("black_fire_ball"));

    public static final DeferredHolder<EntityType<?>, EntityType<NullBladeDash>> NULL_BLADE_DASH =
            ENTITY_TYPES.register("null_blade_dash",
                    () -> EntityType.Builder.of(NullBladeDash::new, MobCategory.MISC)
                            .sized(1.0f, 1.0f)
                            .clientTrackingRange(8)
                            .updateInterval(1)
                            .build("null_blade_dash")
            );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        eventBus.addListener(ModEntities::registerRenderers);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BLACK_FIRE_BALL.get(), BlackFireBallGeoRenderer::new);
        event.registerEntityRenderer(NULL_BLADE_DASH.get(), NullBladeDashRenderer::new);
    }
}
