package com.hughbone.fabrilousupdater.command.suggestion

import com.hughbone.fabrilousupdater.util.FabUtil
import com.hughbone.fabrilousupdater.util.FabUtil.createConfigFiles
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.io.IOException
import java.nio.file.Files
import java.util.concurrent.CompletableFuture

object ModList {
    fun getSuggestions(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        createConfigFiles()
        // Search through all mods
        try {
            outer@ for (modFile in Files.list(FabUtil.modsDir).toList()) {
                val modFileName = modFile.fileName.toString()
                if (modFileName.contains(".jar")) {
                    try {
                        var line: String
                        val file = Files.newBufferedReader(FabUtil.updaterIgnorePath)
                        while (file.readLine().also { line = it } != null) {
                            if (line == modFileName) {
                                continue@outer
                            }
                        }
                    } catch (ignored: IOException) {
                    }
                    builder.suggest(modFileName)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return builder.buildFuture()
    }
}