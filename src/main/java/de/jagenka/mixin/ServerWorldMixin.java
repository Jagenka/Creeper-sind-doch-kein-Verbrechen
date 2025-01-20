package de.jagenka.mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin
{
    @Shadow
    protected abstract Explosion.DestructionType getDestructionType(GameRules.Key<GameRules.BooleanRule> decayRule);

    @Redirect(method = "createExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getDestructionType(Lnet/minecraft/world/GameRules$Key;)Lnet/minecraft/world/explosion/Explosion$DestructionType;"))
    private Explosion.DestructionType preventCreeperGriefing(ServerWorld instance, GameRules.Key<GameRules.BooleanRule> decayRule)
    {
        // if a mob is about to explode, override DestructionType with KEEP, so no blocks are destroyed.
        if (decayRule == GameRules.MOB_EXPLOSION_DROP_DECAY)
        {
            return Explosion.DestructionType.KEEP;
        }
        // if something else is about to explode, proceed with default code.
        else
        {
            return getDestructionType(decayRule);
        }
    }
}