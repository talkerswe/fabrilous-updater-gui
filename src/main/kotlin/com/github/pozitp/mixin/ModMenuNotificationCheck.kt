package com.github.pozitp.mixin

import com.github.pozitp.Util.Companion.shownUpdateNotification
import com.hughbone.fabrilousupdater.platform.ModPlatform
import net.minecraft.client.gui.screen.TitleScreen
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo


@Mixin(TitleScreen::class)
class ModMenuNotificationCheck {
    @Inject(at = [At("TAIL")], method = ["render"])
    fun init(info: CallbackInfo) {
        if (shownUpdateNotification) {
            return
        }
        shownUpdateNotification = true
        val runnable = Runnable {
            ModPlatform().start(null, "update")
        }
        val t = Thread(runnable)
        t.start()
    }
}