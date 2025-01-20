package de.jagenka.mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
}