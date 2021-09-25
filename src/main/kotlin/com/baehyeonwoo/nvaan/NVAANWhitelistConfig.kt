package com.baehyeonwoo.nvaan

import com.google.common.collect.ImmutableSortedSet
import java.io.File

object NVAANWhitelistConfig {
    lateinit var allows: Set<String>

    fun load(whitelistConfig: File) {
        if (!whitelistConfig.exists()) {
            whitelistConfig.createNewFile()
        }

        val lines = whitelistConfig.readLines()
        allows = ImmutableSortedSet.copyOf(String.CASE_INSENSITIVE_ORDER, lines)
    }
}