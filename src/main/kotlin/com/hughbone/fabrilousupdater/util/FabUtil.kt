package com.hughbone.fabrilousupdater.util

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hughbone.fabrilousupdater.platform.CurrentMod
import com.hughbone.fabrilousupdater.platform.ReleaseFile
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.MinecraftVersion
import org.apache.commons.lang3.ArrayUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.Instant

object FabUtil {
    @JvmField
    var modPresentOnServer = false
    @JvmField
    var updaterIgnorePath: Path = FabricLoader.getInstance().configDir.resolve("fabrilous-updater-ignore.txt")
    @JvmField
    var modsDir: Path = FabricLoader.getInstance().gameDir.resolve("mods")
    @Throws(Exception::class)
    fun sendPost(murmurHash: String): String? {
        val body = "[$murmurHash]"
        val urlConn: HttpURLConnection
        val mUrl = URL("https://addons-ecs.forgesvc.net/api/v2/fingerprint")
        urlConn = mUrl.openConnection() as HttpURLConnection
        urlConn.doOutput = true
        urlConn.addRequestProperty("Accept", "application/json")
        urlConn.addRequestProperty("Content-Type", "application/json")
        urlConn.addRequestProperty("Content-Type", "application/json")
        urlConn.outputStream.write(body.toByteArray(StandardCharsets.UTF_8))
        val br = BufferedReader(InputStreamReader(urlConn.inputStream))
        var line: String?
        val content: StringBuilder = StringBuilder()
        while (br.readLine().also { line = it } != null) {
            content.append(line)
        }
        urlConn.disconnect()
        return if (content.toString().contains("\"exactMatches\":[]")) {
            null
        } else content.toString()
    }

    fun getNewUpdate(json: JsonArray, currentMod: CurrentMod, platform: String?): ReleaseFile? {
        // Find newest release for MC version
        var newestFile: ReleaseFile? = null
        var newestDate = FileTime.from(Instant.parse(currentMod.fileDate))
        for (jsonElement in json) {
            val modRelease = ReleaseFile(jsonElement.asJsonObject, platform!!)
            if (modRelease.isFabric) {
                if (modRelease.isCompatible(minecraftVersion)) {
                    // Compare release dates to get most recent mod version
                    val fileDate = FileTime.from(Instant.parse(modRelease.fileDate))
                    if (newestDate < fileDate) {
                        newestDate = fileDate
                        newestFile = modRelease
                    }
                }
            }
        }
        return newestFile
    }

    private fun getJsonString(sURL: String): String? {
        return try {
            val obj = URL(sURL)
            val con = obj.openConnection() as HttpURLConnection
            val `in` = BufferedReader(InputStreamReader(con.inputStream))
            var inputLine: String?
            val response = StringBuilder()
            while (`in`.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            `in`.close()
            response.toString()
        } catch (e: Exception) {
            null
        }
    }

    fun getJsonArray(sURL: String): JsonArray {
        val jsonStr = getJsonString(sURL)
        val jp = JsonParser()
        assert(jsonStr != null)
        return jp.parse(jsonStr).asJsonArray
    }

    fun getJsonObject(sURL: String): JsonObject {
        val jsonStr = getJsonString(sURL)
        val jp = JsonParser()
        assert(jsonStr != null)
        return jp.parse(jsonStr).asJsonObject
    }

    // remove last decimal in MC version (ex. 1.16.5 --> 1.16)
    private val minecraftVersion: String
        get() {
            val minecraftVersion = MinecraftVersion.create()
            // remove last decimal in MC version (ex. 1.16.5 --> 1.16)
            var versionStr = minecraftVersion.id
            var versionStrSplit = versionStr.split("\\.").toTypedArray()
            try {
                versionStrSplit = ArrayUtils.remove(versionStrSplit, 2)
            } catch (ignored: IndexOutOfBoundsException) {
            }
            versionStr = versionStrSplit[0] + "." + versionStrSplit[1]
            return versionStr
        }

    @JvmStatic
    fun createConfigFiles() {
        try {
            if (!Files.exists(updaterIgnorePath)) Files.createFile(updaterIgnorePath)
        } catch (ignored: IOException) {
        }
    }
}