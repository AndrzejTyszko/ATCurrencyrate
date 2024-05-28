package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    var currencyRates: JSONObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.editText)
        val spinner1 = findViewById<Spinner>(R.id.spinner1)
        val spinner2 = findViewById<Spinner>(R.id.spinner2)
        val button = findViewById<Button>(R.id.button)
        val textView = findViewById<TextView>(R.id.textView)

        button.setOnClickListener {
            val amount = editText.text.toString().toDouble()
            val fromCurrency = spinner1.selectedItem.toString()
            val toCurrency = spinner2.selectedItem.toString()
            val result = calculateCurrency(amount, fromCurrency, toCurrency)
            textView.text = "$amount $fromCurrency = $result $toCurrency"
        }

        GlobalScope.launch(Dispatchers.IO) {
            val apiResponse = URL("https://api.exchangerate-api.com/v4/latest/USD").readText()
            currencyRates = JSONObject(apiResponse).getJSONObject("rates")
            val currencies = currencyRates!!.names()!!
            val currencyList = List(currencies.length()) { i -> currencies.getString(i) }

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, currencyList)
                spinner1.adapter = adapter
                spinner2.adapter = adapter
            }
        }
    }
    private fun calculateCurrency(amount: Double, fromCurrency: String, toCurrency: String): String {
        val fromRate = currencyRates!!.getDouble(fromCurrency)
        val toRate = currencyRates!!.getDouble(toCurrency)
        val result = amount / fromRate * toRate

        val roundedResult = String.format("%.2f", result)

        return roundedResult
    }
}
