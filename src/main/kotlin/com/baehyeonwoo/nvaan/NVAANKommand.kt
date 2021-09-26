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

import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.HandlerList
import org.bukkit.plugin.Plugin

/***
 * @author BaeHyeonWoo
 */

object NVAANKommand {
    private fun getInstance(): Plugin {
        return NVAANPluginMain.instance
    }

    private val config = getInstance().config

    private val server = getInstance().server

    fun nvaanKommand() {
        getInstance().kommand {
            register("nvaan") {
//                requires { isOp }
//                For console control reasons.
                executes {
                    sender.sendMessage(text("Usage: /nvaan <status/on/off>"))
                }
                then("status") {
                    executes {
                        val enabled = config.getBoolean("enabled")
                        sender.sendMessage(text("Current NVAAN Status: $enabled"))
                    }
                }
                then("on") {
                    executes {
                        val enabled = config.getBoolean("enabled")
                        if (enabled) {
                            sender.sendMessage(text("NVAAN is Already Enabled.", NamedTextColor.RED))
                        }
                        else {
                            config.set("enabled", true)
                            getInstance().saveConfig()
                            server.pluginManager.registerEvents(NVAANEvent(), getInstance())
                            server.scheduler.scheduleSyncRepeatingTask(getInstance(), NVAANConfigReloadTask(), 0, 0)
                            sender.sendMessage(text("NVAAN is Now Enabled!", NamedTextColor.GREEN))
                        }
                    }
                }
                then("off") {
                    executes {
                        val enabled = config.getBoolean("enabled")
                        if (!enabled) {
                            sender.sendMessage(text("NVAAN is Already Disabled.", NamedTextColor.RED))
                        }
                        else {
                            config.set("enabled", false)
                            getInstance().saveConfig()
                            HandlerList.unregisterAll(getInstance())
                            server.scheduler.cancelTasks(getInstance())
                            sender.sendMessage(text("NVAAN is Now Disabled!", NamedTextColor.GREEN))
                        }
                    }
                }
                then("config") {
                    then("whitelistOnly") {
                        then("on") {
                            executes {
                                val whitelistOnly = config.getBoolean("whitelist-only")
                                if (whitelistOnly) {
                                    sender.sendMessage(text("Whitelist Only Option is Already Enabled.", NamedTextColor.RED))
                                }
                                else {
                                    config.set("whitelist-only", true)
                                    sender.sendMessage(text("Whitelist Only Options is Now Enabled!", NamedTextColor.GREEN))
                                }
                            }
                        }
                        then("off") {
                            executes {
                                val whitelistOnly = config.getBoolean("whitelist-only")
                                if (!whitelistOnly) {
                                    sender.sendMessage(text("Whitelist Only Option is Already Disabled.", NamedTextColor.RED))
                                }
                                else {
                                    config.set("whitelist-only", false)
                                    sender.sendMessage(text("Whitelist Only Options is Now Disabled!", NamedTextColor.GREEN))
                                }
                            }
                        }
                    }
                    then("adminOnly") {
                        then("on") {
                            executes {
                                val adminOnly = config.getBoolean("admin-only")
                                if (adminOnly) {
                                    sender.sendMessage(text("Admin Only Option is Already Enabled.", NamedTextColor.RED))
                                }
                                else {
                                    config.set("admin-only", true)
                                    sender.sendMessage(text("Admin Only Options is Now Enabled!", NamedTextColor.GREEN))
                                }
                            }
                        }
                        then("off") {
                            executes {
                                val adminOnly = config.getBoolean("admin-only")
                                if (!adminOnly) {
                                    sender.sendMessage(text("Admin Only Option is Already Disabled.", NamedTextColor.RED))
                                }
                                else {
                                    config.set("admin-only", true)
                                    sender.sendMessage(text("Admin Only Options is Now Disabled!", NamedTextColor.GREEN))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}