package com.hughbone.fabrilousupdater.platform

import com.google.gson.JsonParser
import com.hughbone.fabrilousupdater.util.FabUtil
import java.lang.Exception

class CurrentMod(hashOrResult: String, platform: String) {
    var projectID: String? = null
    @JvmField
    var fileDate: String? = null
    var modName: String? = null
    var websiteUrl: String? = null
    var fileName: String? = null

    init {
        try {
            if (platform == "curseforge") {
                var json = JsonParser.parseString(hashOrResult).asJsonObject
                projectID = json["exactMatches"].asJsonArray[0].asJsonObject["id"].asString
                fileDate = json["exactMatches"].asJsonArray[0].asJsonObject["file"].asJsonObject["fileDate"].asString
                fileName = json["exactMatches"].asJsonArray[0].asJsonObject["file"].asJsonObject["fileName"].asString
                json = FabUtil.getJsonObject("https://addons-ecs.forgesvc.net/api/v2/addon/$projectID")
                modName = json["name"].asString
                websiteUrl = json["websiteUrl"].asString + "/files"
            } else if (platform == "modrinth") {
                var json = FabUtil.getJsonObject("https://api.modrinth.com/api/v1/version_file/$hashOrResult?algorithm=sha1")
                projectID = json["mod_id"].asString
                fileDate = json["date_published"].asString
                val filesArray = json.getAsJsonArray("files")

                // Get filename
                for (j in filesArray) {
                    val tempFile:String = j.asJsonObject["filename"].asString
                    if (!tempFile.contains("-sources") && !tempFile.contains("-dev")) {  // If multiple files uploaded, get rid of imposter ඞ ones
                        fileName = j.asJsonObject["filename"].asString
                        break
                    }
                }
                json = FabUtil.getJsonObject("https://api.modrinth.com/api/v1/mod/$projectID")
                modName = json["title"].asString
                websiteUrl = "https://www.modrinth.com/mod/" + json["slug"].asString + "/versions"
            }
            assert(modName != null)
            modName = modName!!.replace("(fabric)", "")
            modName = modName!!.replace("(Fabric)", "")
            // Remove spaces at the end of the string
            while (Character.toString(modName!!.length-1) == " ") {
                modName = modName!!.substring(0, modName!!.length - 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}