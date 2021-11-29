package com.hughbone.fabrilousupdater.command

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.entity.player.PlayerEntity

object ClientPlayerHack {
    @JvmStatic
    fun getPlayer(ctx: CommandContext<FabricClientCommandSource>): PlayerEntity {
        return ctx.source.player
    }
}