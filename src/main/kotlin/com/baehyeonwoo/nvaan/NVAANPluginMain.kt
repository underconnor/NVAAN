/*
 * Copyright (c) 2021 BaeHyeonWoo
 *
 *  Licensed under the General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/gpl-3.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baehyeonwoo.nvaan

import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/***
 * @author BaeHyeonWoo
 */

class NVAANPluginMain : JavaPlugin() {

    companion object {
        lateinit var instance: NVAANPluginMain
            private set
    }

    private val configFile = File(dataFolder, "config.yml")

    private val whitelistConfig = File("plugins/WhitelistConfig", "Whitelist.txt")

    override fun onEnable() {
        NVAANAdminConfig.load(configFile)
        NVAANWhitelistConfig.load(whitelistConfig)
        val enabled = config.getBoolean("enabled")
        instance = this
        if (enabled) {
            server.pluginManager.registerEvents(NVAANEvent(), this)
        }
        NVAANKommand.nvaanKommand()
        server.scheduler.scheduleSyncRepeatingTask(this, NVAANConfigReloadTask(), 0, 0)
    }
}