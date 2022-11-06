package ccetl.customcapes.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mixin(CapeLayer.class)
public class CapeLayerMixin extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public CapeLayerMixin(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> p_117346_) {
        super(p_117346_);
    }

    private List<Object> namesOfPlayersWithSavedCape = new ArrayList<>();
    private List<Object> namesOfPlayersWhichDoNotHaveACape = new ArrayList<>();
    private List<Object> namesOfPlayersWhichDoHaveACape = new ArrayList<>();

    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * @author ccetl
     * @reason render the cape
     */

    @Overwrite
    public void render(PoseStack p_116615_, MultiBufferSource p_116616_, int p_116617_, AbstractClientPlayer p_116618_, float p_116619_, float p_116620_, float p_116621_, float p_116622_, float p_116623_, float p_116624_) {
        if (p_116618_.getName() != null && !p_116618_.getName().equals("")) {
            String name = p_116618_.getName().getString();
            String runLocation = Paths.get(".").toAbsolutePath().normalize().toString().toLowerCase().replace(" ", "-");
            String path = runLocation.toLowerCase(Locale.ROOT) + "\\customcapes\\cache\\" + name.toLowerCase(Locale.ROOT) + ".png";
            String rawPath = runLocation.toLowerCase(Locale.ROOT) + "\\CustomCapes\\cache";
            String locationForMinecraft = "\\customcapes\\cache\\" + name.toLowerCase(Locale.ROOT) + ".png";
            URL hasCapeURL = null;
            boolean hasCape = false;
            LOGGER.info("Started loading the Cape of " + name);

            if(!namesOfPlayersWhichDoNotHaveACape.contains(name) || !namesOfPlayersWhichDoHaveACape.contains(name)) {
                try {
                    hasCapeURL = new URL("https://customcapes.org/api/hascape/" + name);
                } catch (MalformedURLException e) {
                    LOGGER.warn("Unable to set the url to api/hasCape");
                    LOGGER.warn(e.getMessage());
                    e.printStackTrace();
                }
                try {
                    URLConnection urlconnection = hasCapeURL.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
                    String hasCapeString;
                    while ((hasCapeString = br.readLine()) != null) {
                        if (hasCapeString.equals("true")) {
                            hasCape = true;
                            namesOfPlayersWhichDoHaveACape.add(name);
                        } else if (hasCapeString.equals("false")) {
                            hasCape = false;
                            namesOfPlayersWhichDoNotHaveACape.add(name);
                        } else {
                            LOGGER.warn("Something went wrong while reading " + hasCapeURL);
                        }
                    }
                    br.close();
                } catch (IOException e) {
                    LOGGER.warn("Unable to check if " + name + " has a cape");
                    LOGGER.warn(e.getMessage());
                    e.printStackTrace();
                }
            } else if(namesOfPlayersWhichDoHaveACape.contains(name)) {
                hasCape = true;
            } else if(namesOfPlayersWhichDoNotHaveACape.contains(name)) {
                hasCape = false;
            } else {
                hasCape = false;
            }

            if(hasCape && !namesOfPlayersWithSavedCape.contains(name)) {
                URL url = null;
                try {
                    url = new URL("https://customcapes.org/api/capes/" + name + ".png");
                } catch (MalformedURLException e) {
                    LOGGER.warn("failed to set the url");
                    LOGGER.warn(e.getMessage());
                    e.printStackTrace();
                }
                String CustomCapes = "CustomCapes";

                //create the path
                new File(rawPath).mkdirs();

                //safe the image from the api
                InputStream is = null;
                try {
                    assert url != null;
                    is = url.openStream();
                } catch (IOException e) {
                    LOGGER.warn("failed to open the connection to CustomCapes");
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
                int check = 0;
                while (true) {
                    try {
                        assert is != null;
                        if ((length = is.read(b)) == -1) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    check += 1;
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
                namesOfPlayersWithSavedCape.add(name);
            }

            if (p_116618_.isCapeLoaded() && !p_116618_.isInvisible() && p_116618_.isModelPartShown(PlayerModelPart.CAPE) && hasCape) {
                LOGGER.info(name + " has a cape!");
                String capeLocation = locationForMinecraft.replace("\\", "/").toLowerCase(Locale.ROOT);
                ResourceLocation cape = new ResourceLocation(capeLocation);
                ItemStack itemstack = p_116618_.getItemBySlot(EquipmentSlot.CHEST);
                if (!itemstack.is(Items.ELYTRA)) {
                    p_116615_.pushPose();
                    p_116615_.translate(0.0D, 0.0D, 0.125D);
                    double d0 = Mth.lerp((double) p_116621_, p_116618_.xCloakO, p_116618_.xCloak) - Mth.lerp((double) p_116621_, p_116618_.xo, p_116618_.getX());
                    double d1 = Mth.lerp((double) p_116621_, p_116618_.yCloakO, p_116618_.yCloak) - Mth.lerp((double) p_116621_, p_116618_.yo, p_116618_.getY());
                    double d2 = Mth.lerp((double) p_116621_, p_116618_.zCloakO, p_116618_.zCloak) - Mth.lerp((double) p_116621_, p_116618_.zo, p_116618_.getZ());
                    float f = p_116618_.yBodyRotO + (p_116618_.yBodyRot - p_116618_.yBodyRotO);
                    double d3 = (double) Mth.sin(f * ((float) Math.PI / 180F));
                    double d4 = (double) (-Mth.cos(f * ((float) Math.PI / 180F)));
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


                    VertexConsumer vertexconsumer = p_116616_.getBuffer(RenderType.entitySolid(cape));
                    this.getParentModel().renderCloak(p_116615_, vertexconsumer, p_116617_, OverlayTexture.NO_OVERLAY);
                    p_116615_.popPose();
                }
            } else if (p_116618_.isCapeLoaded() && !p_116618_.isInvisible() && p_116618_.isModelPartShown(PlayerModelPart.CAPE) && p_116618_.getCloakTextureLocation() != null && !hasCape) {
                LOGGER.info(name + " hasn't CustomCape ): but a vanilla cape");
                ItemStack itemstack = p_116618_.getItemBySlot(EquipmentSlot.CHEST);
                if (!itemstack.is(Items.ELYTRA)) {
                    p_116615_.pushPose();
                    p_116615_.translate(0.0D, 0.0D, 0.125D);
                    double d0 = Mth.lerp((double) p_116621_, p_116618_.xCloakO, p_116618_.xCloak) - Mth.lerp((double) p_116621_, p_116618_.xo, p_116618_.getX());
                    double d1 = Mth.lerp((double) p_116621_, p_116618_.yCloakO, p_116618_.yCloak) - Mth.lerp((double) p_116621_, p_116618_.yo, p_116618_.getY());
                    double d2 = Mth.lerp((double) p_116621_, p_116618_.zCloakO, p_116618_.zCloak) - Mth.lerp((double) p_116621_, p_116618_.zo, p_116618_.getZ());
                    float f = p_116618_.yBodyRotO + (p_116618_.yBodyRot - p_116618_.yBodyRotO);
                    double d3 = (double) Mth.sin(f * ((float) Math.PI / 180F));
                    double d4 = (double) (-Mth.cos(f * ((float) Math.PI / 180F)));
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
                LOGGER.info(name + " hasn't cape");
            } else if (p_116618_.isCapeLoaded() && p_116618_.isInvisible() && p_116618_.isModelPartShown(PlayerModelPart.CAPE) && hasCape) {
                LOGGER.info(name + " is invisible");
            } else if (p_116618_.isCapeLoaded() && !p_116618_.isInvisible() && !p_116618_.isModelPartShown(PlayerModelPart.CAPE) && hasCape) {
                LOGGER.info(name + " has the cape toggled off");
            }
        }
    }
}
