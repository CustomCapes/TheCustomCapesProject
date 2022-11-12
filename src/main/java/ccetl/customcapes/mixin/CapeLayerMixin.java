/*
 *  This file is part of the CustomCapes Project (https://github.com/CustomCapes)
 *  Copyright (C) 2022  ccetl
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ccetl.customcapes.mixin;

import ccetl.customcapes.util;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;

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

    public static final Logger LOGGER = LogManager.getLogger("customcapes");

    /**
     * @author ccetl
     * @reason render the cape
     */

    @Overwrite
    public void render(@NotNull PoseStack p_116615_, @NotNull MultiBufferSource p_116616_, int p_116617_, AbstractClientPlayer p_116618_, float p_116619_, float p_116620_, float p_116621_, float p_116622_, float p_116623_, float p_116624_) {
        String name = p_116618_.getName().getString();
        String runLocation = Paths.get(".").toAbsolutePath().normalize().toString().toLowerCase().replace(" ", "-");
        String path = runLocation.toLowerCase(Locale.ROOT) + "\\customcapes\\cache\\" + name.toLowerCase(Locale.ROOT) + ".png";
        String rawPath = runLocation.toLowerCase(Locale.ROOT) + "\\CustomCapes\\cache";
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
            URL url = null;
            try {
                url = new URL("https://customcapes.org/api/capes/" + name + ".png");
            } catch (MalformedURLException e) {
                LOGGER.warn("failed to set the url");
                LOGGER.warn(e.getMessage());
                e.printStackTrace();
            }

            //create the path
            if (new File(rawPath).mkdirs()) LOGGER.info("Created the cache folder");

            //safe the image from the api
            InputStream is = null;
            try {
                assert url != null;
                is = url.openStream();
            } catch (IOException e) {
                LOGGER.warn("Failed to open the connection to CustomCapes");
                LOGGER.warn(e.getMessage());
                e.printStackTrace();
            }
            OutputStream os = null;
            try {
                os = new FileOutputStream(path);
            } catch (FileNotFoundException e) {
                LOGGER.warn("failed to create the outputStream");
                LOGGER.warn(e.getMessage());
                e.printStackTrace();
            }
            byte[] b = new byte[2048];
            int length = 0;
            while (true) {
                try {
                    assert is != null;
                    if ((length = is.read(b)) == -1) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    assert os != null;
                    os.write(b, 0, length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                is.close();
            } catch (IOException e) {
                LOGGER.warn("failed to end the connection");
                LOGGER.warn(e.getMessage());
                e.printStackTrace();
            }
            try {
                assert os != null;
                os.close();
            } catch (IOException e) {
                LOGGER.warn("failed to end the download");
                LOGGER.warn(e.getMessage());
                e.printStackTrace();
            }
            util.INSTANCE.addToNamesOfPlayersWithSavedCape(name);
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
