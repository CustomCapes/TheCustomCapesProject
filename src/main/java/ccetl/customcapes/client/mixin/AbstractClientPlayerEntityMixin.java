package ccetl.customcapes.client.mixin;

import ccetl.customcapes.client.util;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;
import java.util.List;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {

    private static final Logger LOGGER = LogUtils.getLogger();

    GameProfile gameProfile;
    String playerName = getGameProfile().getName();

    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
        this.gameProfile = gameProfile;
    }

    @Inject(at = @At("HEAD"), method = "getCapeTexture", cancellable = true)
    private void getCapeTexture(CallbackInfoReturnable<Identifier> cir) throws IOException {
        boolean hasCape = false;
        boolean hasNotACustomCape = false;
        boolean hasACustomCape = false;
        boolean hasASavedCape = false;
        List<String> namesOfPlayersWhoDoNotHaveACape = util.INSTANCE.getNamesOfPlayersWhoDoNotHaveACape();
        List<String> namesOfPlayersWhoDoHaveACape = util.INSTANCE.getNamesOfPlayersWhoDoHaveACape();
        List<String> namesOfPlayersWithSavedCape = util.INSTANCE.getNamesOfPlayersWithSavedCape();
        boolean DebugMode = util.INSTANCE.isDebugMode();

        if (DebugMode) {
            LOGGER.info("Started loading the Cape of " + playerName);
        }

        for (String ListName : namesOfPlayersWhoDoNotHaveACape) {
            if (ListName.equals(playerName)) {
                hasNotACustomCape = true;
                if (DebugMode) {
                    LOGGER.info(playerName + " hasn't a cape! (from List)");
                }
                break;
            }
        }

        for (String ListName : namesOfPlayersWhoDoHaveACape) {
            if (ListName.equals(playerName)) {
                hasACustomCape = true;
                if (DebugMode) {
                    LOGGER.info(playerName + " has a cape! (from List)");
                }
                break;
            }
        }

        if(hasACustomCape) {
            for (String ListName : namesOfPlayersWithSavedCape) {
                if (ListName.equals(playerName)) {
                    hasASavedCape = true;
                    if (DebugMode) {
                        LOGGER.info(playerName + " has a saved cape! (from List)");
                    }
                    break;
                }
            }
        }

        if(!hasACustomCape && !hasNotACustomCape) {
            hasCape = util.INSTANCE.hasCape(playerName);
        } else if (hasACustomCape) {
            hasCape = true;
        }

        if (hasCape && !hasASavedCape) {
            if (DebugMode) {
                LOGGER.info("Started to download the cape of " + playerName);
            }
            util.INSTANCE.getCape(playerName);
        }

        cir.setReturnValue(util.INSTANCE.getResourceLocation(playerName));
            
    }

}
