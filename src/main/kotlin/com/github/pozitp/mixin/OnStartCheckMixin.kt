package com.github.pozitp.mixin

import com.github.pozitp.Util.Companion.shownUpdateNotification
import com.hughbone.fabrilousupdater.platform.ModPlatform
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(TitleScreen::class)
open class OnStartCheckMixin protected constructor(title: Text?) : Screen(title) {
    @Inject(at = [At("TAIL")], method = ["onRender"])
    fun onRender(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float, ci: CallbackInfo) {
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