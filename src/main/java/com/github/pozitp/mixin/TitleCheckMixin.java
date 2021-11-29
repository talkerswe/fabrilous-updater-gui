package com.github.pozitp.mixin;

import com.github.pozitp.Util;
import com.hughbone.fabrilousupdater.platform.ModPlatform;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleCheckMixin extends Screen {

    protected TitleCheckMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "render")
    public void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (Util.shownUpdateNotification) {
            return;
        }
        Util.shownUpdateNotification = true;
        Runnable runnable = () -> new ModPlatform().start(null, "update");
        Thread t = new Thread(runnable);
        t.start();
    }
}

