package com.example.thermalprintertuto

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.emh.thermalprinter.EscPosPrinter
import com.emh.thermalprinter.connection.tcp.TcpConnection
import com.emh.thermalprinter.exceptions.EscPosConnectionException
import com.emh.thermalprinter.textparser.PrinterTextParserImg
import com.example.thermalprintertuto.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val tmp = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnPrint.setOnClickListener {
            val count = binding.etCount.text.toString().toInt()
            for (i in 1..count) {
                tmp.append("[L] Sprite-200ml [R]3.00\n")
            }
            test(tmp.toString())
        }
    }


    private fun test(orderString: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val printer = EscPosPrinter(TcpConnection("192.168.0.10", 9100), 203, 65f, 42)
                printer.printFormattedTextAndCut(
                    "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                        printer,
                        getApplicationContext().getResources()
                            .getDrawableForDensity(R.drawable.heart, DisplayMetrics.DENSITY_MEDIUM)
                    ) + "</img>\n" +
                            "[L]\n" +
                            "[C]<u><font size='big'>ORDER N°1125</font></u>\n[L]\n" +
                            "[L] _________________________________________\n" +
                            "[L] Description [C]Quantity [R]Amount\n[L]\n" +
                            "${tmp}\n" +
                            "[L] _________________________________________\n" +
                            "[L] TOTAL [R]13.00 BD\n" +
                            "[L] Total Collected [R]1.00 BD\n" +
                            "[L]\n" +
                            "[L] _________________________________________\n" +
                            "[L]\n" +
                            "[C]<font size='tall'>Customer Info</font>\n" +
                            "[L] EM Haseeb\n" +
                            "[L] 14 Streets\n" +
                            "[L] Cantt, LHR\n" +
                            "[L] Tel : +923040017916\n" +
                            "[L]\n" +
                            "[L] <barcode type='ean13' height='10'>831254784551</barcode>\n[L]\n" +
                            "[L] <qrcode>http://github.com/EmHaseeb/</qrcode>\n[L]\n[L]\n[L]\n"
                )
                printer.disconnectPrinter()
            } catch (e: EscPosConnectionException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun testing() {}
}