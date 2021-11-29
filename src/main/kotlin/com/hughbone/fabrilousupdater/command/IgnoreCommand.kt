package com.hughbone.fabrilousupdater.command

import com.hughbone.fabrilousupdater.command.suggestion.IgnoreList
import com.hughbone.fabrilousupdater.command.suggestion.ModList
import com.hughbone.fabrilousupdater.util.FabUtil
import com.hughbone.fabrilousupdater.util.FabUtil.createConfigFiles
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.text.Style
import net.minecraft.util.Formatting
import java.io.IOException
import java.nio.file.Files

class IgnoreCommand {
    fun register(env: String) {
        if (env == "CLIENT") {
            registerClient()
        } else {
            registerServer()
        }
        removeDeletedMods() // Remove from ignore list if not found in mods directory
    }

    private fun registerClient() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.literal<FabricClientCommandSource>("fabdate")
            .then(LiteralArgumentBuilder.literal<FabricClientCommandSource>("ignore")
                .then(
                    LiteralArgumentBuilder.literal<FabricClientCommandSource>("add").then(
                        RequiredArgumentBuilder.argument<FabricClientCommandSource, String>(
                            "mod",
                            StringArgumentType.word()
                        ).suggests { _: CommandContext<FabricClientCommandSource>, builder: SuggestionsBuilder ->
                            ModList.getSuggestions(
                                builder
                            )
                        }
                            .executes { ctx: CommandContext<FabricClientCommandSource> ->
                                execute(
                                    1,
                                    StringArgumentType.getString(ctx, "mod"),
                                    ClientPlayerHack.getPlayer(ctx)
                                )
                            })
                )
                .then(
                    LiteralArgumentBuilder.literal<FabricClientCommandSource>("remove").then(
                        RequiredArgumentBuilder.argument<FabricClientCommandSource, String>(
                            "mod",
                            StringArgumentType.word()
                        ).suggests { _: CommandContext<FabricClientCommandSource>, builder: SuggestionsBuilder ->
                            IgnoreList.getSuggestions(
                                builder
                            )
                        }
                            .executes { ctx: CommandContext<FabricClientCommandSource> ->
                                execute(
                                    2,
                                    StringArgumentType.getString(ctx, "mod"),
                                    ClientPlayerHack.getPlayer(ctx)
                                )
                            })
                )
                .then(
                    LiteralArgumentBuilder.literal<FabricClientCommandSource>("list")
                        .executes { ctx: CommandContext<FabricClientCommandSource> ->
                            execute(
                                3,
                                null,
                                ClientPlayerHack.getPlayer(ctx)
                            )
                        })
            )
        )
    }

    private fun registerServer() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _: Boolean ->
            dispatcher.register(CommandManager.literal("fabdateserver")
                .requires { source: ServerCommandSource -> source.hasPermissionLevel(4) }
                .then(CommandManager.literal("ignore")
                    .then(
                        CommandManager.literal("add").then(
                            CommandManager.argument("mod", StringArgumentType.word())
                                .suggests { _: CommandContext<ServerCommandSource>, builder: SuggestionsBuilder ->
                                    ModList.getSuggestions(
                                        builder
                                    )
                                }
                                .executes { ctx: CommandContext<ServerCommandSource> ->
                                    execute(
                                        1,
                                        StringArgumentType.getString(ctx, "mod"),
                                        ctx.source.player
                                    )
                                })
                    )
                    .then(
                        CommandManager.literal("remove").then(
                            CommandManager.argument("mod", StringArgumentType.word())
                                .suggests { _: CommandContext<ServerCommandSource>, builder: SuggestionsBuilder ->
                                    IgnoreList.getSuggestions(
                                        builder
                                    )
                                }
                                .executes { ctx: CommandContext<ServerCommandSource> ->
                                    execute(
                                        2,
                                        StringArgumentType.getString(ctx, "mod"),
                                        ctx.source.player
                                    )
                                })
                    )
                    .then(
                        CommandManager.literal("list").executes { ctx: CommandContext<ServerCommandSource> ->
                            execute(
                                3,
                                null,
                                ctx.source.player
                            )
                        })
                )
            )
        })
    }

    fun removeDeletedMods() {
        // Remove from ignore list if mod is deleted
        createConfigFiles()
        try {
            val file = Files.newBufferedReader(FabUtil.updaterIgnorePath)
            val goodLines: MutableList<String> = ArrayList()
            var line: String?
            var modDeleted = false
            while ((file.readLine().also { line = it }) != null) {
                var modExists = false
                for (modFile in Files.list(FabUtil.modsDir).toList()) {
                    val fileName = modFile.fileName.toString()
                    if (fileName.contains(".jar")) {
                        if (fileName == line) {
                            modExists = true
                            break
                        }
                    }
                }
                if (modExists) {
                    line?.let { goodLines.add(it) }
                } else {
                    modDeleted = true
                }
            }
            file.close()
            if (modDeleted) {
                val writeFile = Files.newBufferedWriter(FabUtil.updaterIgnorePath)
                for (writeLine in goodLines) {
                    writeFile.write(
                        """
    $writeLine
    
    """.trimIndent()
                    )
                }
                writeFile.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun execute(
        option: Int,
        modInput: String?,
        player: PlayerEntity
    ): Int {  // Option: (1=add, 2=remove, 3=list)
        // get just the mod from input
        if (option == 3) {
            player.sendMessage(
                LiteralText("[Fabrilous Updater] Ignore List:").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false
            )
        }
        createConfigFiles() // Make sure ignore config file exists
        var line: String
        val newFile = StringBuilder()
        var isRemoved = false
        if (option == 1) {
            newFile.append(modInput).append("\n")
        }
        try {
            val file = Files.newBufferedReader(FabUtil.updaterIgnorePath)
            while (file.readLine().also { line = it } != null) {
                if (option == 1) {
                    if (line == modInput) {
                        player.sendMessage(
                            LiteralText("[Error] $modInput is already in ignore list.").setStyle(
                                Style.EMPTY.withColor(
                                    Formatting.RED
                                )
                            ), false
                        )
                        file.close()
                        return 0
                    } else {
                        newFile.append(line).append("\n")
                    }
                } else if (option == 2) {
                    if (line != modInput) {
                        newFile.append(line).append("\n")
                    } else {
                        isRemoved = true
                    }
                } else if (option == 3) {
                    player.sendMessage(LiteralText(line), false)
                }
            }
            file.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (option == 3) {
            player.sendMessage(LiteralText(""), false)
            return 1
        } else {
            try {
                val file = Files.newBufferedWriter(FabUtil.updaterIgnorePath)
                file.write(newFile.toString())
                file.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        // Success messages
        if (option == 1) {
            player.sendMessage(LiteralText("Added $modInput to ignore list."), false)
        } else if (option == 2) {
            if (isRemoved) {
                player.sendMessage(LiteralText("Removed $modInput from ignore list."), false)
            } else {
                player.sendMessage(
                    LiteralText("[Error] $modInput is not in ignore list.").setStyle(
                        Style.EMPTY.withColor(
                            Formatting.RED
                        )
                    ), false
                )
            }
        }
        return 1
    }
}