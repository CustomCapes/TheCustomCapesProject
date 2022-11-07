package ccetl.customcapes.mixin;

import ccetl.customcapes.Customcapes;
import ccetl.customcapes.util;
import com.sun.jna.platform.unix.X11;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component p_96550_) {
        super(p_96550_);
    }
    String MODID = Customcapes.MODID;

    ResourceLocation API_ONLINE = new ResourceLocation(MODID, "/assets/customcapes/icons/checkmark-height.png");
    ResourceLocation API_OFFLINE = new ResourceLocation(MODID, "/assets/customcapes/icons/cloud-cross-height.png");
    ResourceLocation CONNECTION_ERROR = new ResourceLocation(MODID, "/assets/customcapes/icons/wifi-off-height.png");
    ResourceLocation GRAY = new ResourceLocation(MODID, "/assets/customcapes/icons/info-height.png");

    @Inject(at = @At("TAIL"), method = "init()V")
    private void init(CallbackInfo ci) {
        boolean apiOnline = util.INSTANCE.isApiOnline();
        boolean connection = util.INSTANCE.isConnection();

        int j = this.width - 4;
        if (apiOnline && connection) {
            addRenderableWidget(new ImageButton(j, this.height - 4, 20, 20, 0, 0, 20, API_ONLINE, 32, 64, (p_96784_) -> {
                util.INSTANCE.openWebpage("https://customcapes.org");
            }));
        } else if (!connection) {
            addRenderableWidget(new ImageButton(j, this.height - 4, 20, 20, 0, 0, 20, API_OFFLINE, 32, 64, (p_96784_) -> {
                util.INSTANCE.checkApiStatus();
            }));
        } else if (!apiOnline && connection) {
            addRenderableWidget(new ImageButton(j, this.height - 4, 20, 20, 0, 0, 20, CONNECTION_ERROR, 32, 64, (p_96784_) -> {
                util.INSTANCE.checkConnection();
            }));
        } else {
            addRenderableWidget(new ImageButton(j, this.height - 4, 20, 20, 0, 0, 20, GRAY, 32, 64, (p_96784_) -> {
                assert this.minecraft != null;
                this.minecraft.setScreen(new TitleScreen());
            }));
        }
    }

}
