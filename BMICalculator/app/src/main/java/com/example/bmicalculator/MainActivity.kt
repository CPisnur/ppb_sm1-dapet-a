package com.example.bmicalculator

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bmicalculator.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private data class Kategori(val nama: String, val rentang: String, val warna: Int)

    private val daftarKategori = listOf(
        Kategori("Kurus", "< 18.5", Color.parseColor("#1E88E5")),
        Kategori("Normal", "18.5 – 24.9", Color.parseColor("#22A64B")),
        Kategori("Gemuk", "25.0 – 29.9", Color.parseColor("#FB8C00")),
        Kategori("Obesitas", "≥ 30.0", Color.parseColor("#E53935"))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnHitung.setOnClickListener { hitungBmi() }
        binding.btnReset.setOnClickListener { resetForm() }
        binding.btnKategori.setOnClickListener { tampilkanDialogKategori() }
    }


    private fun hitungBmi() {
        val berat = binding.etBerat.text.toString().replace(',', '.').toDoubleOrNull()
        val tinggiCm = binding.etTinggi.text.toString().replace(',', '.').toDoubleOrNull()


        var valid = true
        if (berat == null || berat <= 0) {
            binding.etBerat.error = getString(R.string.error_berat)
            valid = false
        }
        if (tinggiCm == null || tinggiCm <= 0) {
            binding.etTinggi.error = getString(R.string.error_tinggi)
            valid = false
        }
        if (!valid) return

        val tinggiM = tinggiCm!! / 100.0
        val bmi = berat!! / (tinggiM * tinggiM)

        binding.tvHasil.text = String.format(Locale.US, "%.1f", bmi)

        val kategori = getKategori(bmi)
        binding.tvKategori.text = kategori.nama
        (binding.tvKategori.background.mutate() as GradientDrawable).setColor(kategori.warna)
        binding.tvKategori.visibility = View.VISIBLE
    }


    private fun getKategori(bmi: Double): Kategori = when {
        bmi < 18.5 -> daftarKategori[0]
        bmi < 25.0 -> daftarKategori[1]
        bmi < 30.0 -> daftarKategori[2]
        else -> daftarKategori[3]
    }


    private fun resetForm() {
        binding.etBerat.text?.clear()
        binding.etTinggi.text?.clear()
        binding.etBerat.error = null
        binding.etTinggi.error = null
        binding.tvHasil.text = "0.0"
        binding.tvKategori.visibility = View.INVISIBLE
        binding.etBerat.requestFocus()
    }
    
    private fun tampilkanDialogKategori() {
        val dp = resources.displayMetrics.density
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((24 * dp).toInt(), (16 * dp).toInt(), (24 * dp).toInt(), 0)
        }

        daftarKategori.forEach { k ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, (8 * dp).toInt(), 0, (8 * dp).toInt())
            }
            val rentang = TextView(this).apply {
                text = k.rentang
                textSize = 15f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val badge = TextView(this).apply {
                text = k.nama
                setTextColor(Color.WHITE)
                textSize = 13f
                setPadding((14 * dp).toInt(), (4 * dp).toInt(), (14 * dp).toInt(), (4 * dp).toInt())
                background = GradientDrawable().apply {
                    cornerRadius = 100 * dp
                    setColor(k.warna)
                }
            }
            row.addView(rentang)
            row.addView(badge)
            container.addView(row)
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.kategori_bmi)
            .setView(container)
            .setPositiveButton(R.string.tutup, null)
            .show()
    }
}
