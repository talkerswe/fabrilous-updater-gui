package com.github.pozitp.mixin

import org.spongepowered.asm.mixin.Mixin
import com.terraformersmc.modmenu.gui.ModsScreen
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.terraformersmc.modmenu.gui.widget.ModMenuTexturedButtonWidget
import com.github.pozitp.mixin.ModMenuButton
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ButtonWidget.PressAction
import net.minecraft.text.LiteralText
import net.minecraft.client.gui.widget.ButtonWidget.TooltipSupplier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier

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
                { button: ButtonWidget? -> println("What") }, LiteralText.EMPTY, label@
                TooltipSupplier { button: ButtonWidget, matrices: MatrixStack?, mouseX: Int, mouseY: Int ->
                    if (!button.isHovered) {
                        return@TooltipSupplier
                    }
                    this.renderTooltip(matrices, Text.of("Why"), mouseX, mouseY)
                })
        )
    }

    private companion object {
        private val UPDATE_BUTTON_LOCATION = Identifier("fabrilousupdatergui", "textures/gui/install_button.png")
    }
}