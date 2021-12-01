package com.github.pozitp.mixin;

import com.github.pozitp.Util;
import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.gui.widget.ModMenuTexturedButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModsScreen.class)
public abstract class ModsCheckMixin extends Screen {
    private static final Identifier UPDATE_BUTTON_LOCATION = new Identifier("fabrilousupdatergui", "textures/gui/install_button.png");
    @Shadow
    private int paneWidth;
//    @Shadow
//    private int paneY;

    protected ModsCheckMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        int searchBoxWidth = this.paneWidth - 32 - 22;
        this.addDrawableChild(new ModMenuTexturedButtonWidget(this.paneWidth / 2 + searchBoxWidth / 2 + 14,
                22, 20, 20, 0, 0, UPDATE_BUTTON_LOCATION, 32, 64,
                button -> Util.showToast(new TranslatableText("atdevelopment.title"), new TranslatableText("atdevelopment.description")), LiteralText.EMPTY,
                (button, matrices, mouseX, mouseY) -> {
                    if (!button.isHovered()) {
                        return;
                    }
                    this.renderTooltip(matrices, new TranslatableText("gui.button.updates"), mouseX, mouseY);
                }));
    }
}