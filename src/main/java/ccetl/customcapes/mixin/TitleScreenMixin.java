/*
 *  This file is part of thr CustomCapes Project (https://github.com/CustomCapes)
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

import ccetl.customcapes.Customcapes;
import ccetl.customcapes.util;
import net.minecraft.client.gui.components.ImageButton;
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

    ResourceLocation API_ONLINE = new ResourceLocation(MODID, "/resources/assets/customcapes/icons/checkmark-height.png");
    ResourceLocation API_OFFLINE = new ResourceLocation(MODID, "/resources/assets/customcapes/icons/cloud-cross-height.png");
    ResourceLocation CONNECTION_ERROR = new ResourceLocation(MODID, "/resources/assets/customcapes/icons/wifi-off-height.png");
    ResourceLocation GRAY = new ResourceLocation(MODID, "/resources/assets/customcapes/icons/info-height.png");

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
