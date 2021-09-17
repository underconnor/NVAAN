package com.baehyeonwoo.nvaan

import java.util.*

object NVAANObject {

    var UUID.kickCount: Int
        get() {
            return timestamps[this] ?: 4
        }
        set(value) {
            timestamps[this] = value
        }

    private val timestamps = HashMap<UUID, Int>()

}