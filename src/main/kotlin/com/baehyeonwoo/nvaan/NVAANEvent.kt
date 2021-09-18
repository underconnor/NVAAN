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
import org.bukkit.BanList.Type
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerLoginEvent.Result
import org.bukkit.plugin.Plugin

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
        
        if (enabled && result == Result.KICK_WHITELIST) {
            if (administrator.contains(p.uniqueId.toString())) return
            else {
                if (p.uniqueId.kickCount <= 0) {
                    server.getBanList(Type.NAME).addBan(p.name, "${ChatColor.BOLD}화이트리스트가 아니고 연속적인 경고를 주었으나 무시하고 접속하였기에 밴 처리 되셨습니다.", null, "Console")
                    server.banIP("${p.address}")
                    getInstance().logger.info("Player ${p.name} (/${p.address}) is not whitelisted, and has been banned for connecting the server for many times! Hooray! We got one of the worms!")
                    e.disallow(Result.KICK_BANNED,text("${ChatColor.BOLD}화이트리스트가 아니고 연속적인 경고를 주었으나 무시하고 접속하였기에 밴 처리 되셨습니다."))
                }
                else {
                    e.disallow(Result.KICK_WHITELIST,text("화이트리스트가 아니십니다. 서버에 접속하지 말아주세요.\n" +
                            "${ChatColor.BOLD}지금으로부터 ${p.uniqueId.kickCount}번 이상 접속하시게 되면 자동으로 밴 처리 되며, 앞으로의 컨텐츠에서 밴이 될 수 있음을 알려드립니다.\n" +
                            "${ChatColor.RESET}컨텐츠가 시작된 이후라면 화이트리스트 추가 이전이니 서버 접속 시도를 멈추시고 관리자 알림이 올때까지 잠시만 기다려 주세요.\n" +
                            "경고는 매 서버 시작마다 초기화 됩니다."))
                    --p.uniqueId.kickCount
                }
            }
        }
        else return
    }
}