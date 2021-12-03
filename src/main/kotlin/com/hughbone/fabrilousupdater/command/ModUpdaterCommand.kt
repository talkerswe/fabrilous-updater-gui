package com.hughbone.fabrilousupdater.command

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import com.mojang.brigadier.context.CommandContext
import net.minecraft.entity.player.PlayerEntity
import com.hughbone.fabrilousupdater.util.FabUtil
import net.minecraft.text.TranslatableText
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.command.CommandManager
import com.hughbone.fabrilousupdater.platform.ModPlatform
import net.minecraft.text.Style
import net.minecraft.util.Formatting

class ModUpdaterCommand {
    fun register(env: String) {
        if (env == "CLIENT") {
            registerClient()
        } else {
            registerServer()
        }
    }

    private fun registerClient() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.literal<FabricClientCommandSource?>("fabdate")
            .then(
                LiteralArgumentBuilder.literal<FabricClientCommandSource?>("update")
                    .executes { ctx: CommandContext<FabricClientCommandSource> ->
                        val player = ClientPlayerHack.getPlayer(ctx)
                        if (FabUtil.modPresentOnServer && player.hasPermissionLevel(4)) {
                            player.sendMessage(
                                TranslatableText("update.message.note.use").setStyle(
                                    Style.EMPTY.withColor(
                                        Formatting.BLUE
                                    )
                                ), false
                            )
                        }
                        StartThread(player).start()
                        1
                    }
            ))
    }

    private fun registerServer() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>, _: Boolean ->
            dispatcher.register(CommandManager.literal("fabdateserver")
                .requires { source: ServerCommandSource -> source.hasPermissionLevel(4) }
                .then(CommandManager.literal("update").executes { ctx: CommandContext<ServerCommandSource> ->
                    StartThread(ctx.source.player).start()
                    1
                })
            )
        })
    }

    private class StartThread(var player: PlayerEntity) : Thread() {
        override fun run() {
            if (ModPlatform.isRunning) {
                player.sendMessage(
                    TranslatableText("update.message.error.already").setStyle(
                        Style.EMPTY.withColor(
                            Formatting.RED
                        )
                    ), false
                )
            } else {
                player.sendMessage(TranslatableText("update.message.search"), false)
                ModPlatform().start(player, "update")
            }
        }
    }
}