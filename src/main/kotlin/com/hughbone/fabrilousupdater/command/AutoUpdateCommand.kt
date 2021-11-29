package com.hughbone.fabrilousupdater.command

import com.hughbone.fabrilousupdater.command.ClientPlayerHack.getPlayer
import com.hughbone.fabrilousupdater.platform.ModPlatform
import com.hughbone.fabrilousupdater.util.FabUtil
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class AutoUpdateCommand {
    fun register(env: String) {
        if (env == "CLIENT") {
            registerClient()
        } else {
            registerServer()
        }
    }

    private fun registerClient() {
        ClientCommandManager.DISPATCHER.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource?>("fabdate")
                .then(
                    LiteralArgumentBuilder.literal<FabricClientCommandSource?>("autoupdate")
                        .executes { ctx: CommandContext<FabricClientCommandSource> ->
                            val player = getPlayer(ctx)
                            if (FabUtil.modPresentOnServer && player.hasPermissionLevel(4)) {
                                player.sendMessage(
                                    LiteralText("Note: Use '/fabdateserver update' for server mods.").setStyle(
                                        Style.EMPTY.withColor(
                                            Formatting.BLUE
                                        )
                                    ), false
                                )
                            }
                            val warningMessage: Text? =
                                Text.Serializer.fromJson("[\"\",{\"text\":\"[Warning] \",\"color\":\"red\"},\"This command automatically deletes old mods and downloads new versions. \",{\"text\":\"Click here to continue.\",\"color\":\"dark_green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/ඞmogusClient\"}}]")
                            player.sendMessage(warningMessage, false)
                            1
                        })
        )
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.literal<FabricClientCommandSource?>("ඞmogusClient")
            .executes { ctx: CommandContext<FabricClientCommandSource> ->
                StartThread(getPlayer(ctx)).start()
                1
            }
        )
    }

    private fun registerServer() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>, _: Boolean ->
            dispatcher.register(CommandManager.literal("fabdateserver")
                .requires { source: ServerCommandSource -> source.hasPermissionLevel(4) }
                .then(CommandManager.literal("autoupdate").executes { ctx: CommandContext<ServerCommandSource> ->
                    val warningMessage: Text? =
                        Text.Serializer.fromJson("[\"\",{\"text\":\"[Warning] \",\"color\":\"red\"},\"This command automatically deletes old mods and downloads new versions. \",{\"text\":\"Click here to continue.\",\"color\":\"dark_green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/ඞmogusServer\"}}]")
                    ctx.source.player.sendMessage(warningMessage, false)
                    1
                })
            )
        })
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>, _: Boolean ->
            dispatcher.register(
                CommandManager.literal("ඞmogusServer")
                    .requires { source: ServerCommandSource -> source.hasPermissionLevel(4) }
                    .executes { ctx: CommandContext<ServerCommandSource> ->
                        StartThread(ctx.source.player).start()
                        1
                    }
            )
        })
    }

    private class StartThread(var player: PlayerEntity) : Thread() {
        override fun run() {
            if (ModPlatform.isRunning) {
                player.sendMessage(
                    LiteralText("[Error] Already checking for updates!").setStyle(
                        Style.EMPTY.withColor(
                            Formatting.RED
                        )
                    ), false
                )
            } else {
                player.sendMessage(Text.of("[Fabrilous Updater] Automatically updating all mods..."), false)
                ModPlatform().start(player, "autoupdate")
                player.sendMessage(
                    LiteralText("[FabrilousUpdater] Finished! Restart Minecraft to apply updates."),
                    false
                )
            }
        }
    }
}