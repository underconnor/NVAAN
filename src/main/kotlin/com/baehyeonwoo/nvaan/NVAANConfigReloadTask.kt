package com.baehyeonwoo.nvaan

import org.bukkit.plugin.Plugin
import java.io.File

class NVAANConfigReloadTask : Runnable {

    private fun getInstance(): Plugin {
        return NVAANPluginMain.instance
    }

    private val logger = getInstance().logger

    private val adminConfigFile = File(getInstance().dataFolder, "config.yml")

    private val whitelistConfig = File("plugins/WhitelistConfig", "Whitelist.txt")

    private var adminLastModified = adminConfigFile.lastModified()

    private var whitelistLastModified = whitelistConfig.lastModified()

    override fun run() {
        if (adminLastModified != adminConfigFile.lastModified()) {
            NVAANAdminConfig.load(adminConfigFile)
            adminLastModified = adminConfigFile.lastModified()

            logger.info("Admin Config Reloaded.")
        }

        if (whitelistLastModified != whitelistConfig.lastModified()) {
            NVAANWhitelistConfig.load(whitelistConfig)
            whitelistLastModified = whitelistConfig.lastModified()

            logger.info("Whitelist Config Reloaded.")
        }
    }
}