package net.youshallnotpatrol.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.youshallnotpatrol.YouShallNotPatrol;
import net.youshallnotpatrol.config.ServerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = PatrolSpawner.class, priority = 100)
public class PatrolSpawnerMixin {

    //We need two different variables as if the pillagers fail to spawn for whatever reason, last targeted player would be incorrect, and the same player may get targeted twice in a row.
    @Unique
    ServerPlayer youshallnotpatrol$lastTargetedPlayer;

    @Unique
    ServerPlayer youshallnotpatrol$selectedPlayer;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getCurrentDifficultyAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/DifficultyInstance;"))
    private void youshallnotpatrol$setSelectedPlayer(ServerLevel serverLevel, boolean bl, boolean bl2, CallbackInfoReturnable<Integer> cir, @Local Player player){
        YouShallNotPatrol.LOGGER.warn("Patrol Spawned. Set {} to the lastTargetedPlayer", youshallnotpatrol$selectedPlayer);
        youshallnotpatrol$selectedPlayer = (ServerPlayer) player;
    }

    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void youshallnotpatrol$setLastTargetedPlayer(ServerLevel serverLevel, boolean bl, boolean bl2, CallbackInfoReturnable<Integer> cir){

        if(cir.getReturnValue() > 1) { //If n is greater than 1 it means the patrol spawned successfully. N can be 1 in the case an attempt was made but the leader failed to spawn.
            YouShallNotPatrol.LOGGER.warn("Attempting Patrol Spawn. Set {} to the selectedPlayer", youshallnotpatrol$selectedPlayer);
            youshallnotpatrol$lastTargetedPlayer = youshallnotpatrol$selectedPlayer;
        }
    }

    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    private void youShallNotPatrol$checkForSpawnChance(ServerLevel serverLevel, boolean bl, boolean bl2, CallbackInfoReturnable<Integer> cir) {
        //Cancels the tick function if the spawn chance is 0.
        if (ServerConfig.pillagerSpawnChance.get() == 0) {
            cir.setReturnValue(0);
        }
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I", ordinal = 1))
    private int youshallnotpatrol$ShouldPatrolSpawn(int original, @Local RandomSource source, @Local(argsOnly = true) ServerLevel level){

        //TODO: Improve mod compat of this? Original value (could be modified by other mods) is not taken into account, which may be desirable
        if (original != 5){
            YouShallNotPatrol.LOGGER.warn("Another mod has modified the vanilla Pillager Patrol spawn rate. We will not affect the spawn rate. Expected 5. Got {}. Please report to YouShallNotPatrol with modlist.", original);
            return original;
        }

        //If the returned value is 0, a patrol will try to spawn
        int randomChance = source.nextInt(100);
        int spawnChance = ServerConfig.pillagerSpawnChance.get();
        if(!level.players().isEmpty()) {
            YouShallNotPatrol.LOGGER.warn("Attempting patrol spawn. Random roll was {}. Spawn chance is {}", randomChance, spawnChance);
        }
        return randomChance < spawnChance ? 0 : 1;
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;players()Ljava/util/List;"))
    private List<ServerPlayer> youshallnotpatrol$ModifyPlayerList(List<ServerPlayer> original, @Local(argsOnly = true) ServerLevel level){
        //If there is only one player, then we should return the original list
        if(level.players().size() == 1) {
            YouShallNotPatrol.LOGGER.warn("Only 1 player, spawning patrol with normal player selection.");
            return original;
        }

        //If we should spawn on a different player
        if(ServerConfig.pillagerSpawnOnDifferentPlayer.get()){
            //Modify the list and return it
            original.remove(youshallnotpatrol$lastTargetedPlayer);
            YouShallNotPatrol.LOGGER.warn("Spawning patrol. Will not spawn on {} as they were the last player.", youshallnotpatrol$lastTargetedPlayer);
            return original;
        }

        return original;
    }
}
