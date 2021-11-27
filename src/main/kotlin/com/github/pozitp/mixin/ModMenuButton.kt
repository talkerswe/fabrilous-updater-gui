package com.github.pozitp.mixin

import com.github.pozitp.Util.Companion.showToast
import com.terraformersmc.modmenu.gui.ModsScreen
import com.terraformersmc.modmenu.gui.widget.ModMenuTexturedButtonWidget
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ButtonWidget.TooltipSupplier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ModsScreen::class)
abstract class ModMenuButton protected constructor(title: Text?) : Screen(title) {
    @Shadow
    private val paneWidth = 0
    @Inject(method = ["init"], at = [At("TAIL")])
    fun onInit(ci: CallbackInfo?) {
        // TODO: Real working button
        val searchBoxWidth = paneWidth - 32 - 22
        addDrawableChild(
            ModMenuTexturedButtonWidget(
                paneWidth / 2 + searchBoxWidth / 2 + 14,
                22, 20, 20, 0, 0, UPDATE_BUTTON_LOCATION, 32, 64,
                { showToast(Text.of("Feature in development!"), Text.of("We are working on it."))}, LiteralText.EMPTY,
                TooltipSupplier { button: ButtonWidget, matrices: MatrixStack?, mouseX: Int, mouseY: Int ->
                    if (!button.isHovered) {
                        return@TooltipSupplier
                    }
                    this.renderTooltip(matrices, Text.of("Updates"), mouseX, mouseY)
                })
        )
    }

    private companion object {
        private val UPDATE_BUTTON_LOCATION = Identifier("fabrilousupdatergui", "textures/gui/install_button.png")
    }
}