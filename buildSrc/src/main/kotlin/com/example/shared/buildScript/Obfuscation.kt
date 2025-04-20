package com.example.shared.buildScript

import java.util.Base64
import kotlin.experimental.xor
import kotlin.math.min

object Obfuscation {
    fun encrypt(id: String, content: String): String {
        val hashId = id.hashCode()

        val hashBytes = ByteArray(4) { i -> (hashId shr (i * 8)).toByte() }
        val contentBytes = content.encodeToByteArray()
        val xorBytes = ByteArray(contentBytes.size) { i ->
            contentBytes[i] xor hashBytes[i % 4]
        }

        val merged = mutableListOf<Byte>()
        val minLength = min(4, xorBytes.size)
        for (i in 0 until minLength) {
            merged.add(xorBytes[i])
            merged.add(hashBytes[i])
        }

        merged.addAll(xorBytes.drop(4))
        merged.addAll(hashBytes.drop(minLength))
        return Base64.getEncoder().encodeToString(merged.toByteArray())
    }
}