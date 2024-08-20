package com.paulocosta.nfcreader

import android.nfc.tech.IsoDep
import com.github.devnied.emvnfccard.parser.IProvider
import java.io.IOException

class CustomNfcProvider(private val isoDep: IsoDep) : IProvider {

    override fun transceive(command: ByteArray): ByteArray {
        return try {
            isoDep.transceive(command)
        } catch (e: IOException) {
            e.printStackTrace()
            ByteArray(0)
        }
    }

    override fun getAt(): ByteArray {
        return isoDep.historicalBytes ?: byteArrayOf()
    }
}