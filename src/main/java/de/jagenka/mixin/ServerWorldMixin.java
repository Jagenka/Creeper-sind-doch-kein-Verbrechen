package de.jagenka.mixin;

import de.jagenka.CreeperSindDochKeinVerbrechen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.particle.BlockParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin
{
    @Shadow
    public abstract GameRules getGameRules();

    @Redirect(method = "createExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private boolean preventCreeperGriefing(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule)
    {
        // while creating an explosion, force DO_MOB_GRIEFING to false
        if (rule == GameRules.DO_MOB_GRIEFING)
        {
            return false;
        }
        // should some other game rule be checked in this method, proceed with default return value
        else
        {
            return getGameRules().getBoolean(rule);
        }
    }

    @Inject(method = "createExplosion", at = @At("TAIL"))
    private void showAlternateCreeperExplosionEffect(Entity entity, DamageSource damageSource, ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, World.ExplosionSourceType explosionSourceType, ParticleEffect smallParticle, ParticleEffect largeParticle, Pool<BlockParticleEffect> blockParticles, RegistryEntry<SoundEvent> soundEvent, CallbackInfo ci)
    {
        if (entity instanceof CreeperEntity)
        {
            CreeperSindDochKeinVerbrechen.INSTANCE.createFireworkExplosionEffect((ServerWorld) (Object) this, x, y, z);
        }
    }


}