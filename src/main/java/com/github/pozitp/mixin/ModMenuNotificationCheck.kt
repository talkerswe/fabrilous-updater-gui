package com.github.pozitp.mixin

import com.hughbone.fabrilousupdater.platform.ModPlatform
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.toast.SystemToast
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.*


@Mixin(TitleScreen::class)
class ModMenuNotificationCheck {
    private var shownUpdateNotification:Boolean = false

    @Inject(at = [At("TAIL")], method = ["render"])
    fun init(info: CallbackInfo) {
        if (shownUpdateNotification) {
            return
        }
        shownUpdateNotification = true
//        val player: PlayerEntity? = null
//        ModPlatform().start(player, "update");
        // TODO: Check updates
        Objects.requireNonNull(MinecraftClient.getInstance()).toastManager.add(
            SystemToast(
                SystemToast.Type.TUTORIAL_HINT,
                Text.of("Updates Available!"),
                Text.of("${1} mods can be updated")
            )
        )
    }
}