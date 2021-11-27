package com.github.pozitp.mixin

import net.minecraft.client.gui.screen.TitleScreen
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.*

@Mixin(TitleScreen::class)
class ModMenuNotificationCheck {
    @Inject(at = [At("HEAD")], method = ["init()V"])
    fun init(info: CallbackInfo) {
        println(123)
    }
}