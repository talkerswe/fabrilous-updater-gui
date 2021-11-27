# Fabrilous Updater GUI
Minecraft client-side mod used to check for updates to your Fabric mods.

Note: Only works with mods uploaded to CurseForge or Modrinth.


## Commands
* "/fabdate update" - Shows a list of mods needing updates with a clickable download link.
* "/fabdate ignore"  -  Add, list, or remove mods from an ignore list to prevent update checks.
* "/fabdate autoupdate" - Automatically removes old mods and downloads new mods.


## How it works:
Using the magic of file hashes, Fabrilous Updater GUI searches through Curseforge and Modrinth to find the latest compatible version.

### Enhanced version of [Fabrilous Updater](https://github.com/HughBone/fabrilous-updater) (fork)