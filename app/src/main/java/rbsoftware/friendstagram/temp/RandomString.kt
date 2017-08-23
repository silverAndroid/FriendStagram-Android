package rbsoftware.friendstagram.temp

import java.util.*

/**
 * Created by silver_android on 07/10/16.
 */

class RandomString(length: Int) {
    private val random = Random()
    private val buf: CharArray = if (length < 1) {
        throw IllegalArgumentException("length < 1: $length")
    } else {
        CharArray(length)
    }

    fun nextString(): String {
        for (idx in buf.indices) {
            buf[idx] = symbols[random.nextInt(symbols.size)]
        }
        return String(buf)
    }

    companion object {
        private val symbols: CharArray

        init {
            val tmp = StringBuilder()
            run {
                var ch = '0'
                while (ch <= '9') {
                    tmp.append(ch)
                    ++ch
                }
            }
            var ch = 'a'
            while (ch <= 'z') {
                tmp.append(ch)
                ++ch
            }
            symbols = tmp.toString().toCharArray()
        }
    }
}
