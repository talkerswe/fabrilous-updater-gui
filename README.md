# Fabrilous Updater GUI
[![Build](https://github.com/RELOADEDev/fabrilous-updater-gui/actions/workflows/build.yml/badge.svg)](https://github.com/RELOADEDev/fabrilous-updater-gui/actions/workflows/build.yml)
[![License](https://img.shields.io/github/license/RELOADEDev/fabrilous-updater-gui.svg)](https://github.com/RELOADEDev/fabrilous-updater-gui/blob/main/LICENSE)
[![Release](https://img.shields.io/github/release/RELOADEDev/fabrilous-updater-gui.svg)](https://github.com/RELOADEDev/fabrilous-updater-gui/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/RELOADEDev/fabrilous-updater-gui/total.svg)](https://github.com/RELOADEDev/fabrilous-updater-gui/releases/latest)

Minecraft client-side (server side will be soon) mod used to check for updates to your Fabric mods.

Note: Only works with mods uploaded to CurseForge or Modrinth.

**Mod Menu, Fabric Language Kotlin and Cloth Config API should be installed!**

Nightly builds download link [here](https://nightly.link/RELOADEDev/fabrilous-updater-gui/workflows/build/main/Package.zip)

## Contributors
<a href="https://github.com/reloadedev/fabrilous-updater-gui/graphs/contributors">
  <img height="40em" src="https://contrib.rocks/image?repo=reloadedev/fabrilous-updater-gui" />
</a>

## Commands
* "`/fabdate update`" - Shows a list of mods needing updates with a clickable download link.
* "`/fabdate ignore`"  -  Add, list, or remove mods from an ignore list to prevent update checks.
* "`/fabdate autoupdate`" - Automatically removes old mods and downloads new mods. (deprecated)


## How it works:
Using the magic of file hashes, Fabrilous Updater searches through Curseforge and Modrinth to find the latest compatible version.

### Enhanced version of [Fabrilous Updater](https://github.com/HughBone/fabrilous-updater) (fork) 

_Added GUI, Startup Check and in future will be changed updates algorithm_
