package com.hughbone.fabrilousupdater.platform

import com.google.gson.JsonArray
import com.google.gson.JsonObject

class ReleaseFile(json: JsonObject, platform: String) {
    private var platform: String? = null
    private var json: JsonObject? = null
    var fileName:String? = null
    @JvmField
    var fileDate:String? = null
    var downloadUrl:String? = null
    @JvmField
    var isFabric = false
    fun isCompatible(mcVer: String?): Boolean {
        var jsonVerArray: JsonArray? = null
        if (platform == "CF") {
            jsonVerArray = json!!.getAsJsonArray("gameVersion")
        } else if (platform == "MR") {
            jsonVerArray = json!!.getAsJsonArray("game_versions")
        }
        if (jsonVerArray != null) {
            for (j in jsonVerArray) {
                val ver = j.asString
                if (ver.contains(mcVer!!)) {
                    return true
                }
            }
        }
        return false
    }

    init {
        if (platform == "curseforge") {
            this.platform = "CF"
            val modulesArray = json.getAsJsonArray("modules")
            for (j in modulesArray) {
                if (j.asJsonObject["foldername"].asString == "fabric.mod.json") {
                    isFabric = true
                    break
                }
            }
            if (isFabric) {
                fileDate = json["fileDate"].asString
                fileName = json["fileName"].asString
                downloadUrl = json["downloadUrl"].asString
            }
        } else if (platform == "modrinth") {
            this.platform = "MR"
            val loadersArray = json.getAsJsonArray("loaders")
            for (j in loadersArray) {
                if (j.asJsonPrimitive.asString.contains("fabric")) {
                    isFabric = true
                    break
                }
            }
            if (isFabric) {
                fileDate = json["date_published"].asString
                val filesArray = json.getAsJsonArray("files")
                for (j in filesArray) {
                    val tempFile = j.asJsonObject["filename"].asString
                    if (!tempFile.contains("-sources") && !tempFile.contains("-dev")) {  // If multiple files uploaded, get rid of imposter ඞ ones
                        fileName = j.asJsonObject["filename"].asString
                        downloadUrl = j.asJsonObject["url"].asString
                        break
                    }
                }
            }
        }

        // Save json object
        if (isFabric) {
            this.json = json
        }
    }
}