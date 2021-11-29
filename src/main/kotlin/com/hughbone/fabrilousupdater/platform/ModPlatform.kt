package com.hughbone.fabrilousupdater.platform

import com.github.pozitp.Util.Companion.showToast
import com.hughbone.fabrilousupdater.util.FabUtil
import com.hughbone.fabrilousupdater.util.Hash
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import java.io.IOException
import java.lang.reflect.Array
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

// TODO: IDK, but message sent twice, updates not finds => no startup notification
class ModPlatform {
    private fun showText(player: PlayerEntity?, text: Text) {
        if (player != null) {
            player.sendMessage(text, false)
        } else {
            if (i == 0) {
                return
            }
            showToast(TranslatableText("update.notification.fabrilousupdater.title"), text)
            i = 0
        }
    }

    fun start(player: PlayerEntity?, command: String) {
        if (isRunning) {
            showText(
                player,
                TranslatableText("update.message.error.already").setStyle(Style.EMPTY.withColor(Formatting.RED))
            )
        }
        isRunning = true

        // Search through all mods
        try {
            outer@ for (modFile in Files.list(FabUtil.modsDir).toList()) {
                // Skip mod if in ignore list
                val fileName = modFile.fileName.toString()
                try {
                    var line: String?
                    val file = Files.newBufferedReader(FabUtil.updaterIgnorePath)
                    while (file.readLine().also { line = it } != null) {
                        if (fileName == line) {
                            continue@outer
                        }
                    }
                } catch (ignored: IOException) {
                }
                if (player != null) showText(player, TranslatableText("update.message.checking", fileName))

                // Check for updates
                if (fileName.endsWith(".jar")) {
                    var newestFile: ReleaseFile? = null
                    try {
                        // Check if Modrinth mod
                        val sha1 = Hash.getSHA1(modFile)
                        var currentMod = CurrentMod(sha1, "modrinth")
                        if (currentMod.modName != null) {
                            // Get entire json list of release info
                            val json =
                                FabUtil.getJsonArray("https://api.modrinth.com/api/v1/mod/" + currentMod.projectID + "/version")
                            newestFile = FabUtil.getNewUpdate(json, currentMod, "modrinth")
                        } else {
                            val murmurHash = Hash.getMurmurHash(modFile)
                            val postResult = FabUtil.sendPost(murmurHash)
                            if (postResult != null) {
                                // Get project ID
                                currentMod = CurrentMod(postResult, "curseforge")
                                if (currentMod.modName != null) {
                                    // Get entire json list of release info
                                    val json =
                                        FabUtil.getJsonArray("https://addons-ecs.forgesvc.net/api/v2/addon/" + currentMod.projectID + "/files")
                                    newestFile = FabUtil.getNewUpdate(json, currentMod, "curseforge")
                                }
                            }
                        }
                        if (currentMod.modName == null) {
                            showText(
                                player, TranslatableText("update.message.error.notfound", fileName).setStyle(
                                    Style.EMPTY.withColor(
                                        Formatting.RED
                                    )
                                )
                            )
                        } else if (newestFile != null) {
                            if (command == "update") {
                                val updateMessage: Text? = Text.Serializer.fromJson(
                                    " [\"\",{\"text\":\"" +
                                            currentMod.modName + "  \",\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" +
                                            currentMod.websiteUrl + "\"}," + "\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"Website\",\"italic\":true}]}},{" + "\"text\":\"has an \"},{\"text\":\"update.\",\"color\":\"dark_green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" +
                                            newestFile.downloadUrl + "\"}," + "\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"Direct Download\",\"italic\":true}]}}]"
                                )
                                player?.sendMessage(updateMessage, false) ?: i++
                            } else if (command == "autoupdate") {
                                try {
                                    Files.delete(modFile)
                                    var newFileName:String? = newestFile.fileName
                                    val li = fileName.lastIndexOf(".jar")
                                    if (li < fileName.length - 4) newFileName += fileName.substring(li + 4)
                                    newestFile.downloadUrl?.let { downloadFromURL(it, FabUtil.modsDir.resolve(newFileName)) }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                                val updateMessage: Text? = Text.Serializer.fromJson(
                                    "[\"\",{\"text\":\"" +
                                            currentMod.modName + ": \",\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" +
                                            currentMod.websiteUrl + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Website\"]}},{\"text\":\"[" +
                                            Array.get(
                                                currentMod.fileDate?.split("T"),
                                                0
                                            ) + "] \",\"color\":\"white\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"" +
                                            currentMod.fileName + "\"]}},\"--> \",{\"text\":\"[" +
                                            Array.get(
                                                newestFile.fileDate?.split("T"),
                                                0
                                            ) + "]\",\"color\":\"dark_green\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"" +
                                            newestFile.fileName + "\"]}}]"
                                )
                                player?.sendMessage(updateMessage, false) ?: i++
                            }
                        }
                    } catch (ignored: Exception) {
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (player != null) showText(player, TranslatableText("update.message.finish")) else showText(
            null,
            TranslatableText("update.notification.fabrilousupdater.description", i)
        )
        isRunning = false
    }

    @Throws(IOException::class)
    private fun downloadFromURL(urlStr: String, target: Path) {
        URL(urlStr).openStream().use { `is` -> Files.copy(`is`, target) }
    }

    companion object {
        @JvmField
        var isRunning = false
        var i = 0
    }
}