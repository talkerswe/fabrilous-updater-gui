# Fabrilous Updater GUI

[![CircleCI](https://circleci.com/gh/RELOADEDev/fabrilous-updater-gui/tree/main.svg?style=shield)](https://circleci.com/gh/RELOADEDev/fabrilous-updater-gui/tree/main)
![License](https://img.shields.io/github/license/RELOADEDev/fabrilous-updater-gui.svg)
![Release](https://img.shields.io/github/release/RELOADEDev/fabrilous-updater-gui.svg)
![Downloads](https://img.shields.io/github/downloads/RELOADEDev/fabrilous-updater-gui/total.svg)

Minecraft client-side mod used to check for updates to your Fabric mods.

Note: Only works with mods uploaded to CurseForge or Modrinth.


## Commands
* "/fabdate update" - Shows a list of mods needing updates with a clickable download link.
* "/fabdate ignore"  -  Add, list, or remove mods from an ignore list to prevent update checks.
* "/fabdate autoupdate" - Automatically removes old mods and downloads new mods.


## How it works:
Using the magic of file hashes, Fabrilous Updater GUI searches through Curseforge and Modrinth to find the latest compatible version.

### Enhanced version of [Fabrilous Updater](https://github.com/HughBone/fabrilous-updater) (fork)
