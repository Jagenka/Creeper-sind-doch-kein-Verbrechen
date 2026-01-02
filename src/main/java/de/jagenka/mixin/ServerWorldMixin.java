package de.jagenka.mixin;

import de.jagenka.CreeperSindDochKeinVerbrechen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.BlockParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.ExplosionImpl;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin
{

    @Shadow
    @Final
    List<ServerPlayerEntity> players;

    @Inject(method = "createExplosion", at = @At(value = "HEAD"), cancellable = true)
    private void preventCreeperGriefing(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, World.ExplosionSourceType explosionSourceType, ParticleEffect smallParticle, ParticleEffect largeParticle, Pool<BlockParticleEffect> blockParticles, RegistryEntry<SoundEvent> soundEvent, CallbackInfo ci)
    {
        // while creating for a mob, set DestructionType to Keep
        if (explosionSourceType == World.ExplosionSourceType.MOB)
        {
            Explosion.DestructionType destructionType = Explosion.DestructionType.KEEP;

            // copy pasta to do the usual stuffs (I dont like it this way...)
            Vec3d vec3d = new Vec3d(x, y, z);
            ExplosionImpl explosionImpl = new ExplosionImpl((ServerWorld) (Object) this, entity, damageSource, behavior, vec3d, power, createFire, destructionType);
            int i = explosionImpl.explode();
            ParticleEffect particleEffect = explosionImpl.isSmall() ? smallParticle : largeParticle;

            for (ServerPlayerEntity serverPlayerEntity : this.players)
            {
                if (serverPlayerEntity.squaredDistanceTo(vec3d) < 4096.0)
                {
                    Optional<Vec3d> optional = Optional.ofNullable((Vec3d) explosionImpl.getKnockbackByPlayer().get(serverPlayerEntity));
                    serverPlayerEntity.networkHandler.sendPacket(new ExplosionS2CPacket(vec3d, power, i, optional, particleEffect, soundEvent, blockParticles));
                }
            }

            if (entity instanceof CreeperEntity)
            {
                CreeperSindDochKeinVerbrechen.INSTANCE.createFireworkExplosionEffect((ServerWorld) (Object) this, x, y, z);
            }

            ci.cancel();
        }
        // should some other game rule be checked in this method, proceed with method
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