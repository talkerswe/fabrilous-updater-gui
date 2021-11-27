package com.github.pozitp.mixin

import net.minecraft.client.gui.screen.TitleScreen
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(TitleScreen::class)
class ModMenuNotificationCheck {
    private companion object {
        private var showed: Boolean = false;
    }

    @Inject(at = [At("RETURN")], method = ["render"])
    fun init(info: CallbackInfo) {
        if (!showed) {
            println("Hello, world!")
            // TODO: Check updates
        }
        showed = true;
    }
}