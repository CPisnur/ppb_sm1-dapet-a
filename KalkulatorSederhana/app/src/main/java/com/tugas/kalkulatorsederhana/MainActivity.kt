package com.tugas.kalkulatorsederhana

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etNum1: EditText
    private lateinit var etNum2: EditText
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etNum1 = findViewById(R.id.etNum1)
        etNum2 = findViewById(R.id.etNum2)
        tvResult = findViewById(R.id.tvResult)

        findViewById<Button>(R.id.btnAdd).setOnClickListener { calculate("+") }
        findViewById<Button>(R.id.btnSub).setOnClickListener { calculate("-") }
        findViewById<Button>(R.id.btnMul).setOnClickListener { calculate("*") }
        findViewById<Button>(R.id.btnDiv).setOnClickListener { calculate("/") }
        findViewById<Button>(R.id.btnClear).setOnClickListener {
            etNum1.text.clear()
            etNum2.text.clear()
            tvResult.text = "Result"
        }
    }

    private fun calculate(operator: String) {
        val num1 = etNum1.text.toString().toDoubleOrNull()
        val num2 = etNum2.text.toString().toDoubleOrNull()

        if (num1 == null || num2 == null) {
            tvResult.text = "Isi kedua angka dengan benar!"
            return
        }

        val result = when (operator) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "*" -> num1 * num2
            "/" -> {
                if (num2 == 0.0) {
                    tvResult.text = "Error: Tidak bisa dibagi nol!"
                    return
                }
                num1 / num2
            }
            else -> return
        }

        // Tampilkan 15 sebagai "15", bukan "15.0"
        tvResult.text = if (result % 1 == 0.0) result.toLong().toString() else result.toString()
    }
}