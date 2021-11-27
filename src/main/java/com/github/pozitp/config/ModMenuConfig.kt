package com.github.pozitp.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.ConfigCategory
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

class ModMenuConfig : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent: Screen? ->
            val builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("Whoami"))
            val general = builder.getOrCreateCategory(Text.of("General"))
            builder.setFallbackCategory(general)
            builder.build()
        }
    }
}