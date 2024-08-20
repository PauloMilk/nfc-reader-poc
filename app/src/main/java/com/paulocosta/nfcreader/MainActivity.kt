package com.paulocosta.nfcreader

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.devnied.emvnfccard.model.EmvCard
import com.github.devnied.emvnfccard.parser.EmvTemplate
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var nfcAdapter: NfcAdapter
    private var isReading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        findViewById<Button>(R.id.btn_read_card).setOnClickListener {
            isReading = true
            Log.d("NFC", "Leitura de cartão iniciada. Aproxime o cartão NFC.")
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (isReading) {
            isReading = false

            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val isoDep = IsoDep.get(tag)

            isoDep?.let {
                try {
                    it.connect()

                    val provider = CustomNfcProvider(isoDep)
                    val config = EmvTemplate.Config()
                        .setContactLess(true)
                        .setReadAllAids(true)
                        .setReadTransactions(true)
                        .setReadCplc(false)

                    val template = EmvTemplate.Builder()
                        .setProvider(provider)
                        .setConfig(config)
                        .build()

                    val card: EmvCard? = template.readEmvCard()

                    if (card != null) {
                        Log.d("NFC", "Número do Cartão: ${card.cardNumber}")
                        Log.d("NFC", "Data de Validade: ${card.expireDate}")
                        Log.d("NFC", "Primeiro Nome: ${card.holderFirstname}")
                        Log.d("NFC", "Type: ${card.type}")
                        Log.d("NFC", "Iban: ${card.iban}")

                    } else {
                        Log.d("NFC", "Não foi possível ler o cartão.")
                    }

                    it.close()
                } catch (e: IOException) {
                    Log.e("NFC", "Erro ao ler o cartão: ${e.message}")
                    e.printStackTrace()
                } catch (e: SecurityException) {
                    Log.e("NFC", "Tag foi invalidada: ${e.message}")
                }
            }
        }
    }
}

