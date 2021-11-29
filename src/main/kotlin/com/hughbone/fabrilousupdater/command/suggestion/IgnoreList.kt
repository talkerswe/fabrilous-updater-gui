package com.hughbone.fabrilousupdater.command.suggestion

import com.hughbone.fabrilousupdater.util.FabUtil
import com.hughbone.fabrilousupdater.util.FabUtil.createConfigFiles
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.io.IOException
import java.nio.file.Files
import java.util.concurrent.CompletableFuture

object IgnoreList {
    fun getSuggestions(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        createConfigFiles()
        try {
            var line: String?
            val file = Files.newBufferedReader(FabUtil.updaterIgnorePath)
            while (file.readLine().also { line = it } != null) {
                builder.suggest(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return builder.buildFuture()
    }
}