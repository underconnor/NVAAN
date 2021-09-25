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

import com.baehyeonwoo.nvaan.NVAANObject.kickCount
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.BanList.Type
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerLoginEvent.Result
import org.bukkit.plugin.Plugin
import java.net.InetAddress

/***
 * @author BaeHyeonWoo
 */

class NVAANEvent : Listener {
    private fun getInstance(): Plugin {
        return NVAANPluginMain.instance
    }

    private val server = getInstance().server

    private val config = getInstance().config

    private val administrator = config.getString("administrator").toString()

    private fun adminAndWhitelistOnly(player: Player, realAddress: InetAddress) {
        if (player.uniqueId.kickCount <= 0 || config.getInt("${player.uniqueId}.kickCount") <= 0) {
            server.getBanList(Type.NAME).addBan(player.name, "${ChatColor.BOLD}관리자나 화이트리스트가 아니고 연속적인 경고를 주었으나 무시하고 접속하였기에 밴 처리 되셨습니다.", null, "Console")
            server.getBanList(Type.IP).addBan("$realAddress", "${ChatColor.BOLD}관리자나 화이트리스트가 아니고 연속적인 경고를 주었으나 무시하고 접속하였기에 밴 처리 되셨습니다.", null, "Console")
            player.kick(text("관리자나 화이트리스트가 아니고 연속적인 경고를 주었으나 무시하고 접속하였기에 밴 처리 되셨습니다.").decorate(TextDecoration.BOLD))
        } else {
            player.kick(text("관리자나 화이트리스트가 아니십니다. 서버에 접속하지 말아주세요.\n" +
                    "${ChatColor.BOLD}지금으로부터 ${player.uniqueId.kickCount}번 이상 접속하시게 되면 자동으로 밴 처리 되며, 앞으로의 이 서버에서 영구적으로 밴이 될 수 있음을 알려드립니다.\n" +
                    "${ChatColor.RESET}접속이 되어야 된다 생각되는 시점이면 대기 혹은 관리자 문의 부탁드립니다.\n"))
            --player.uniqueId.kickCount
            config.set("${player.uniqueId}.kickCount", player.uniqueId.kickCount)
        }
    }

    private fun adminOnly(player: Player, realAddress: InetAddress) {
        if (player.uniqueId.kickCount <= 0 || config.getInt("${player.uniqueId}.kickCount") <= 0) {
            server.getBanList(Type.NAME).addBan(player.name, "${ChatColor.BOLD}현재 관리자만 접속이 가능하다는 연속적인 경고를 주었으나 무시하고 접속하였기에 밴 처리 되셨습니다.", null, "Console")
            server.getBanList(Type.IP).addBan("$realAddress", "${ChatColor.BOLD}현재 관리자만 접속이 가능하다는 연속적인 경고를 주었으나 무시하고 접속하였기에 밴 처리 되셨습니다.", null, "Console")
            player.kick(text("현재 관리자만 접속이 가능하다는 연속적인 경고를 주었으나 무시하고 접속하였기에 밴 처리 되셨습니다.").decorate(TextDecoration.BOLD))
        }
        else {
            player.kick(text("관리자가 아니십니다. 서버에 접속하지 말아주세요.\n" +
                    "${ChatColor.BOLD}지금으로부터 ${player.uniqueId.kickCount}번 이상 접속하시게 되면 자동으로 밴 처리 되며, 앞으로 이 서버에서 영구적으로 밴이 될 수 있음을 알려드립니다.\n" +
                    "${ChatColor.RESET}접속이 되어야 된다 생각되는 시점이면 대기 혹은 관리자 문의 부탁드립니다.\n"))
            --player.uniqueId.kickCount
            config.set("${player.uniqueId}.kickCount", player.uniqueId.kickCount)
        }
    }

    @EventHandler
    fun onPlayerCommandPreProcess(e: PlayerCommandPreprocessEvent) {
        val p = e.player
        val msg = e.message

        if (msg.lowercase() == "nvaan") {
            if(!administrator.contains(p.uniqueId.toString())) e.isCancelled = true
            else return
        }
        else return
    }

    @EventHandler
    fun onPlayerLogin(e: PlayerLoginEvent) {
        val result = e.result
        val p = e.player
        val enabled = config.getBoolean("enabled")
        val whitelistOnly = config.getBoolean("whitelist-only")
        val adminOnly = config.getBoolean("admin-only")

        if (enabled) {
            if (adminOnly && whitelistOnly && result != Result.ALLOWED) {
                if (administrator.contains(p.uniqueId.toString()) || p.name in NVAANWhitelistConfig.allows) return
                else {
                    adminAndWhitelistOnly(p, e.realAddress)
                }
            }
            if (adminOnly && result != Result.ALLOWED) {
                if (administrator.contains(p.uniqueId.toString())) return
                else {
                    adminOnly(p, e.realAddress)
                }
            }
        }
    }
}