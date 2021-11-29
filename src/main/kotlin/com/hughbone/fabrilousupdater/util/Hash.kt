package com.hughbone.fabrilousupdater.util

import kotlin.Throws
import java.io.IOException
import java.security.MessageDigest
import java.lang.StringBuilder
import java.util.Locale
import java.security.NoSuchAlgorithmException
import java.lang.RuntimeException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.experimental.and

object Hash {
    @Throws(IOException::class)
    fun getMurmurHash(file: Path): String {
        val m = 0x5bd1e995
        val r = 24
        var k = 0x0L
        val seed = 1
        var shift = 0x0

        // get file size
        val flength = Files.size(file)

        // convert file to byte array
        val byteFile = Files.readAllBytes(file)
        var length: Long = 0
        var b: Char
        // get good bytes from file
        for (i in 0 until flength) {
            b = byteFile[i.toInt()].toInt().toChar()
            if (b.code == 0x9 || b.code == 0xa || b.code == 0xd || b.code == 0x20) {
                continue
            }
            length += 1
        }
        var h = (seed xor length.toInt()).toLong()
        for (i in 0 until flength) {
            b = byteFile[i.toInt()].toInt().toChar()
            if (b.code == 0x9 || b.code == 0xa || b.code == 0xd || b.code == 0x20) {
                continue
            }
            if (b.code > 255) {
                while (b.code > 255) {
                    b -= 255
                }
            }
            k = k or (b.code.toLong() shl shift)
            shift += 0x8
            if (shift == 0x20) {
                h = 0x00000000FFFFFFFFL and h
                k *= m
                k = 0x00000000FFFFFFFFL and k
                k = k xor (k shr r)
                k = 0x00000000FFFFFFFFL and k
                k *= m
                k = 0x00000000FFFFFFFFL and k
                h *= m
                h = 0x00000000FFFFFFFFL and h
                h = h xor k
                h = 0x00000000FFFFFFFFL and h
                k = 0x0
                shift = 0x0
            }
        }
        if (shift > 0) {
            h = h xor k
            h = 0x00000000FFFFFFFFL and h
            h *= m
            h = 0x00000000FFFFFFFFL and h
        }
        h = h xor (h shr 13)
        h = 0x00000000FFFFFFFFL and h
        h *= m
        h = 0x00000000FFFFFFFFL and h
        h = h xor (h shr 15)
        h = 0x00000000FFFFFFFFL and h
        return h.toString()
    }

    fun getSHA1(file: Path): String {
        // ex. --> String shString = getSHA1(Path.of("config/renammd.jar"));
        try {
            Files.newInputStream(file).use { `is` ->
                val md = MessageDigest.getInstance("SHA1")
                val dataBytes = ByteArray(1024)
                var nread: Int
                while (`is`.read(dataBytes).also { nread = it } != -1) {
                    md.update(dataBytes, 0, nread)
                }
                val mdbytes = md.digest()

                //convert the byte to hex format
                val sb = StringBuilder()
                for (mdbyte in mdbytes) {
                    sb.append(((mdbyte and 0xff.toByte()) + 0x100).toString(16).substring(1))
                }
                return sb.toString().lowercase(Locale.getDefault())
            }
        } catch (ex: NoSuchAlgorithmException) {
            throw RuntimeException(ex)
        } catch (ex: IOException) {
            throw RuntimeException(ex)
        }
    }
}