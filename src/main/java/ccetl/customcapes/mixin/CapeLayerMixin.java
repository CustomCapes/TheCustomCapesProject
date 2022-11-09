package ccetl.customcapes.mixin;

import ccetl.customcapes.createPlayerEntry;
import ccetl.customcapes.util;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

@Mixin(CapeLayer.class)
public class CapeLayerMixin extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public CapeLayerMixin(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> p_117346_) {
        super(p_117346_);
    }

    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * @author ccetl
     * @reason render the cape
     */

    @Overwrite
    public void render(@NotNull PoseStack p_116615_, @NotNull MultiBufferSource p_116616_, int p_116617_, AbstractClientPlayer p_116618_, float p_116619_, float p_116620_, float p_116621_, float p_116622_, float p_116623_, float p_116624_) {
        String name = p_116618_.getName().getString();
        String runLocation = Paths.get(".").toAbsolutePath().normalize().toString().toLowerCase().replace(" ", "-");
        String path = runLocation.toLowerCase(Locale.ROOT) + "\\customcapes\\cache\\" + name.toLowerCase(Locale.ROOT) + ".png";
        boolean hasCape = false;
        boolean hasNotACustomCape = false;
        boolean hasACustomCape = false;
        boolean hasASavedCape = false;
        List<String> namesOfPlayersWhoDoNotHaveACape = util.INSTANCE.getNamesOfPlayersWhoDoNotHaveACape();
        List<String> namesOfPlayersWhoDoHaveACape = util.INSTANCE.getNamesOfPlayersWhoDoHaveACape();
        List<String> namesOfPlayersWithSavedCape = util.INSTANCE.getNamesOfPlayersWithSavedCape();
        boolean DebugMode = util.INSTANCE.isDebugMode();

        if (DebugMode) {
            LOGGER.info("Started loading the Cape of " + name);
        }

        for (String ListName : namesOfPlayersWhoDoNotHaveACape) {
            if (ListName.equals(name)) {
                hasNotACustomCape = true;
                if (DebugMode) {
                    LOGGER.info(name + " hasn't a cape! (from List)");
                }
                break;
            }
        }

        for (String ListName : namesOfPlayersWhoDoHaveACape) {
            if (ListName.equals(name)) {
                hasACustomCape = true;
                if (DebugMode) {
                    LOGGER.info(name + " has a cape! (from List)");
                }
                break;
            }
        }

        if(hasACustomCape) {
            for (String ListName : namesOfPlayersWithSavedCape) {
                if (ListName.equals(name)) {
                    hasASavedCape = true;
                    if (DebugMode) {
                        LOGGER.info(name + " has a saved cape! (from List)");
                    }
                    break;
                }
            }
        }

        if(!hasACustomCape && !hasNotACustomCape) {
            URL hasCapeURL = null;
            if (DebugMode) {
                LOGGER.info("started checking if " + name + " has a cape");
            }
            try {
                hasCapeURL = new URL("https://customcapes.org/api/hascape/" + name);
            } catch (MalformedURLException e) {
                LOGGER.warn("Unable to set the url to api/ hasCape");
                LOGGER.warn(e.getMessage());
                e.printStackTrace();
            }
            try {
                assert hasCapeURL != null;
                URLConnection urlconnection = hasCapeURL.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
                String hasCapeString;
                while ((hasCapeString = br.readLine()) != null) {
                    if (DebugMode) {
                        LOGGER.warn("requested has cape for " + name);
                    }
                    if (hasCapeString.equals("true")) {
                        hasCape = true;
                        util.INSTANCE.addToNamesOfPlayersWhoDoHaveACape(name);
                    } else if (hasCapeString.equals("false")) {
                        hasCape = false;
                        util.INSTANCE.addToNamesOfPlayersWhoDoNotHaveACape(name);
                    } else {
                        LOGGER.warn("Something went wrong while reading " + hasCapeURL);
                        util.INSTANCE.addToNamesOfPlayersWhoDoNotHaveACape(name);
                    }
                }
                br.close();
            } catch (IOException e) {
                LOGGER.warn("Unable to check if " + name + " has a cape");
                LOGGER.warn(e.getMessage());
                e.printStackTrace();
            }
        } else if (hasACustomCape) {
            hasCape = true;
        }

        if (hasCape && !hasASavedCape) {
            util.INSTANCE.getCape(name);
        }

        if (p_116618_.isCapeLoaded() && !p_116618_.isInvisible() && p_116618_.isModelPartShown(PlayerModelPart.CAPE) && hasCape) {
            if (DebugMode) {
                LOGGER.info(name + " has a cape!");
            }

            ItemStack itemstack = p_116618_.getItemBySlot(EquipmentSlot.CHEST);
            if (!itemstack.is(Items.ELYTRA)) {
                p_116615_.pushPose();
                p_116615_.translate(0.0D, 0.0D, 0.125D);
                double d0 = Mth.lerp(p_116621_, p_116618_.xCloakO, p_116618_.xCloak) - Mth.lerp(p_116621_, p_116618_.xo, p_116618_.getX());
                double d1 = Mth.lerp(p_116621_, p_116618_.yCloakO, p_116618_.yCloak) - Mth.lerp(p_116621_, p_116618_.yo, p_116618_.getY());
                double d2 = Mth.lerp(p_116621_, p_116618_.zCloakO, p_116618_.zCloak) - Mth.lerp(p_116621_, p_116618_.zo, p_116618_.getZ());
                float f = p_116618_.yBodyRotO + (p_116618_.yBodyRot - p_116618_.yBodyRotO);
                double d3 = Mth.sin(f * ((float) Math.PI / 180F));
                double d4 = -Mth.cos(f * ((float) Math.PI / 180F));
                float f1 = (float) d1 * 10.0F;
                f1 = Mth.clamp(f1, -6.0F, 32.0F);
                float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
                f2 = Mth.clamp(f2, 0.0F, 150.0F);
                float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;
                f3 = Mth.clamp(f3, -20.0F, 20.0F);
                if (f2 < 0.0F) {
                    f2 = 0.0F;
                }

                float f4 = Mth.lerp(p_116621_, p_116618_.oBob, p_116618_.bob);
                f1 += Mth.sin(Mth.lerp(p_116621_, p_116618_.walkDistO, p_116618_.walkDist) * 6.0F) * 32.0F * f4;
                if (p_116618_.isCrouching()) {
                    f1 += 25.0F;
                }

                p_116615_.mulPose(Vector3f.XP.rotationDegrees(6.0F + f2 / 2.0F + f1));
                p_116615_.mulPose(Vector3f.ZP.rotationDegrees(f3 / 2.0F));
                p_116615_.mulPose(Vector3f.YP.rotationDegrees(180.0F - f3 / 2.0F));

                InputStream is;
                NativeImage ni = null;
                try {
                    is = new FileInputStream(path);
                    ni = NativeImage.read(is);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert ni != null;
                ResourceLocation cape = Minecraft.getInstance().getTextureManager().register(name + ".png", new DynamicTexture(ni));

                VertexConsumer vertexconsumer = p_116616_.getBuffer(RenderType.entitySolid(cape));
                this.getParentModel().renderCloak(p_116615_, vertexconsumer, p_116617_, OverlayTexture.NO_OVERLAY);
                p_116615_.popPose();
            }
        } else if (p_116618_.isCapeLoaded() && !p_116618_.isInvisible() && p_116618_.isModelPartShown(PlayerModelPart.CAPE) && p_116618_.getCloakTextureLocation() != null && !hasCape) {
            if (DebugMode) {
                LOGGER.info(name + " hasn't CustomCape ): but a vanilla cape");
            }
            ItemStack itemstack = p_116618_.getItemBySlot(EquipmentSlot.CHEST);
            if (!itemstack.is(Items.ELYTRA)) {
                p_116615_.pushPose();
                p_116615_.translate(0.0D, 0.0D, 0.125D);
                double d0 = Mth.lerp(p_116621_, p_116618_.xCloakO, p_116618_.xCloak) - Mth.lerp(p_116621_, p_116618_.xo, p_116618_.getX());
                double d1 = Mth.lerp(p_116621_, p_116618_.yCloakO, p_116618_.yCloak) - Mth.lerp(p_116621_, p_116618_.yo, p_116618_.getY());
                double d2 = Mth.lerp(p_116621_, p_116618_.zCloakO, p_116618_.zCloak) - Mth.lerp(p_116621_, p_116618_.zo, p_116618_.getZ());
                float f = p_116618_.yBodyRotO + (p_116618_.yBodyRot - p_116618_.yBodyRotO);
                double d3 = Mth.sin(f * ((float) Math.PI / 180F));
                double d4 = -Mth.cos(f * ((float) Math.PI / 180F));
                float f1 = (float) d1 * 10.0F;
                f1 = Mth.clamp(f1, -6.0F, 32.0F);
                float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
                f2 = Mth.clamp(f2, 0.0F, 150.0F);
                float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;
                f3 = Mth.clamp(f3, -20.0F, 20.0F);
                if (f2 < 0.0F) {
                    f2 = 0.0F;
                }

                float f4 = Mth.lerp(p_116621_, p_116618_.oBob, p_116618_.bob);
                f1 += Mth.sin(Mth.lerp(p_116621_, p_116618_.walkDistO, p_116618_.walkDist) * 6.0F) * 32.0F * f4;
                if (p_116618_.isCrouching()) {
                    f1 += 25.0F;
                }

                p_116615_.mulPose(Vector3f.XP.rotationDegrees(6.0F + f2 / 2.0F + f1));
                p_116615_.mulPose(Vector3f.ZP.rotationDegrees(f3 / 2.0F));
                p_116615_.mulPose(Vector3f.YP.rotationDegrees(180.0F - f3 / 2.0F));
                VertexConsumer vertexconsumer = p_116616_.getBuffer(RenderType.entitySolid(p_116618_.getCloakTextureLocation()));
                this.getParentModel().renderCloak(p_116615_, vertexconsumer, p_116617_, OverlayTexture.NO_OVERLAY);
                p_116615_.popPose();
            }
        } else if (!hasCape) {
            if (DebugMode) {
                LOGGER.info(name + " hasn't a cape");
            }
        } else if (p_116618_.isCapeLoaded() && p_116618_.isInvisible() && p_116618_.isModelPartShown(PlayerModelPart.CAPE)) {
            if (DebugMode) {
                LOGGER.info(name + " is invisible");
            }
        } else if (p_116618_.isCapeLoaded() && !p_116618_.isInvisible() && !p_116618_.isModelPartShown(PlayerModelPart.CAPE)) {
            if (DebugMode) {
                LOGGER.info(name + " has the cape toggled off");
            }
        }
    }
}
