package com.vascomm.thermalprinter

import android.app.Activity
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.experimental.launch
import java.io.IOException
import java.io.OutputStream

object PrinterCommands {
    val HT: Byte = 0x9
    val LF: Byte = 0x0A
    val CR: Byte = 0x0D
    val ESC: Byte = 0x1B
    val DLE: Byte = 0x10
    val GS: Byte = 0x1D
    val FS: Byte = 0x1C
    val STX: Byte = 0x02
    val US: Byte = 0x1F
    val CAN: Byte = 0x18
    val CLR: Byte = 0x0C
    val EOT: Byte = 0x04

    val INIT = byteArrayOf(27, 64)
    var FEED_LINE = byteArrayOf(10)

    var SELECT_FONT_A = byteArrayOf(27, 33, 0)

    var SET_BAR_CODE_HEIGHT = byteArrayOf(29, 104, 100)
    var PRINT_BAR_CODE_1 = byteArrayOf(29, 107, 2)
    var SEND_NULL_BYTE = byteArrayOf(0x00)

    var SELECT_PRINT_SHEET = byteArrayOf(0x1B, 0x63, 0x30, 0x02)
    var FEED_PAPER_AND_CUT = byteArrayOf(0x1D, 0x56, 66, 0x00)

    var SELECT_CYRILLIC_CHARACTER_CODE_TABLE = byteArrayOf(0x1B, 0x74, 0x11)

    var SELECT_BIT_IMAGE_MODE = byteArrayOf(0x1B, 0x2A, 33, -128, 0)
    var SET_LINE_SPACING_24 = byteArrayOf(0x1B, 0x33, 24)
    var SET_LINE_SPACING_30 = byteArrayOf(0x1B, 0x33, 30)

    var TRANSMIT_DLE_PRINTER_STATUS = byteArrayOf(0x10, 0x04, 0x01)
    var TRANSMIT_DLE_OFFLINE_PRINTER_STATUS = byteArrayOf(0x10, 0x04, 0x02)
    var TRANSMIT_DLE_ERROR_STATUS = byteArrayOf(0x10, 0x04, 0x03)
    var TRANSMIT_DLE_ROLL_PAPER_SENSOR_STATUS = byteArrayOf(0x10, 0x04, 0x04)

    val ESC_FONT_COLOR_DEFAULT = byteArrayOf(0x1B, 'r'.toByte(), 0x00)
    val FS_FONT_ALIGN = byteArrayOf(0x1C, 0x21, 1, 0x1B, 0x21, 1)
    val ESC_ALIGN_LEFT = byteArrayOf(0x1b, 'a'.toByte(), 0x00)
    val ESC_ALIGN_RIGHT = byteArrayOf(0x1b, 'a'.toByte(), 0x02)
    val ESC_ALIGN_CENTER = byteArrayOf(0x1b, 'a'.toByte(), 0x01)
    val ESC_CANCEL_BOLD = byteArrayOf(0x1B, 0x45, 0)


    /** */
    val ESC_HORIZONTAL_CENTERS = byteArrayOf(0x1B, 0x44, 20, 28, 0)
    val ESC_CANCLE_HORIZONTAL_CENTERS = byteArrayOf(0x1B, 0x44, 0)
    /** */

    val ESC_ENTER = byteArrayOf(0x1B, 0x4A, 0x40)
    val PRINTE_TEST = byteArrayOf(0x1D, 0x28, 0x41)
}


class Printer(var outputStream: OutputStream?) {

    private object Command{
        val TAB_HORIZONTAL = byteArrayOf(0x9)
        val PAGE_MODE = byteArrayOf(0x1B, 0x0C)

        val RIGHT_SPACE = byteArrayOf(0x1B, 0x0C)
        val ALIGNMENT_LEFT = byteArrayOf(0x1B, 0x61)
        val ALIGNMENT_CENTER = byteArrayOf(0x1B, 0x61)
        val ALIGNMENT_RIGHT = byteArrayOf(0x1B, 0x61)

        val FONT_NORMAL = byteArrayOf(0x1B, 0x21, 0x0)
        val FONT_SMALL = byteArrayOf(0x1B, 0x21, 0x1)

        val FONT_NORMAL_BOLD = byteArrayOf(0x1B, 0x21, 0x8)
        val FONT_SMALL_BOLD = byteArrayOf(0x1B, 0x21, 0x9)

        val FONT_HEIGHT = byteArrayOf(0x1B, 0x21, 0x10)
        val FONT_WIDTH = byteArrayOf(0x1B, 0x21, 0x20)

        val FONT_TEST = byteArrayOf(0x1B, 0x4D, 0x0)
        val FONT_TEST_2 = byteArrayOf(0x1B, 0x4D, 0x1)
    }

    enum class TextSize{
        SMALL, MEDIUM
    }

    private val list = ArrayList<ByteArray>()

    fun text(data: String): Printer{
        return text(data, TextSize.MEDIUM)
    }

    fun text(data: String, textSize: TextSize): Printer{
        list.add(when(textSize){
            TextSize.SMALL -> byteArrayOf(0x1B, 0x21, 0x1)
            else -> byteArrayOf(0x1B, 0x21, 0x0)
        })
        list.add(data.toByteArray())
        return this
    }

    fun bold(): Printer{
        list.add(byteArrayOf(0x1B, 0x21, 0x8))
        return this
    }

    fun enter(): Printer{
        return enter(0)
    }

    fun enter(multiple: Int): Printer{
        for (i in 0..multiple) list.add(byteArrayOf(0xA))
        return this
    }

    fun print(){
        launch {
            enter(3)
            for (a in list) outputStream?.write(a)
        }
    }

    fun dummy(): Printer{
        list.add(byteArrayOf(0x1B, 0x4D, 0x0))
        list.add("Lorem  ".toByteArray())

        list.add(byteArrayOf(0x1B, 0x4D, 0x1))
        list.add("Lorem  ".toByteArray())

        list.add(byteArrayOf(0x1B, 0x4D, 0x2))
        list.add("Lorem  ".toByteArray())

        list.add(byteArrayOf(0x1B, 0x4D, 0x3))
        list.add("Lorem  ".toByteArray())

        list.add(byteArrayOf(0x1B, 0x4D, 0x4))
        list.add("Lorem  ".toByteArray())

        list.add(byteArrayOf(0x1B, 0x4D, 0x5))
        list.add("Lorem".toByteArray())
        enter()

        list.add(byteArrayOf(0x1B, 0x4D, 0x6))
        list.add("Lorem  ".toByteArray())

        list.add(byteArrayOf(0x1B, 0x4D, 0x7))
        list.add("Lorem  ".toByteArray())

        list.add(byteArrayOf(0x1B, 0x4D, 0x48))
        list.add("Lorem  ".toByteArray())

        list.add(byteArrayOf(0x1B, 0x4D, 0x49))
        list.add("Lorem  ".toByteArray())

        list.add(byteArrayOf(0x1B, 0x4D, 0x50))
        list.add("Lorem  ".toByteArray())

        list.add(byteArrayOf(0x1B, 0x4D, 0x51))
        list.add("Lorem".toByteArray())

        return this
    }
}

class Formatter(){
    companion object {
        private var mFormat = byteArrayOf(27, 33, 0)

        fun get(): ByteArray{
            return mFormat
        }

//        fun bold(): ByteArray{
//            mFormat[2] =
//        }
    }
}