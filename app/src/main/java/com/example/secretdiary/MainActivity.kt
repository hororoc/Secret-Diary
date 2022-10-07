package com.example.secretdiary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {
    private val numberPicker1: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker1)
            .apply {
                this.minValue = 0
                this.maxValue = 9
            }
    }
    private val numberPicker2: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker2)
            .apply {
                this.minValue = 0
                this.maxValue = 9
            }
    }
    private val numberPicker3: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker3)
            .apply {
                this.minValue = 0
                this.maxValue = 9
            }
    }
    private val openButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.openButton)
    }
    private val changePasswordButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.changePasswordButton)
    }

    private var changePasswordMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker1
        numberPicker2
        numberPicker3

        openButton.setOnClickListener {
            if (changePasswordMode) {
                Toast.makeText(this, "비밀번호 변경 중입니다!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            when (isPasswordCorrect()) {
                true -> {
                    startActivity(Intent(this, DiaryActivity::class.java))
                }
                false -> showErrorAlertDialog()
            }
        }

        changePasswordButton.setOnClickListener {
            if (changePasswordMode) {
                val pwPreferences = getSharedPreferences("password", Context.MODE_PRIVATE)
                pwPreferences.edit(true) {
                    val pwFromUser: String =
                        "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"
                    putString("password", pwFromUser)
                    // commit()
                }
                changePasswordMode = false
                changePasswordButton.setBackgroundColor(Color.BLACK)
            } else {
                when (isPasswordCorrect()) {
                    true -> {
                        changePasswordMode = true
                        Toast.makeText(this, "변경할 패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                        changePasswordButton.setBackgroundColor(Color.RED)
                    }
                    false -> {
                        showErrorAlertDialog()
                    }
                }
            }
        }
    }

    private fun isPasswordCorrect(): Boolean {
        val pwPreferences = getSharedPreferences("password", Context.MODE_PRIVATE)
        val pwFromUser: String =
            "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

        return pwPreferences.getString("password", "000").equals(pwFromUser)
    }

    private fun showErrorAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("실패")
            .setMessage("비밀번호가 잘못되었습니다.")
            .setPositiveButton("확인") { _, _ -> }
            .create()
            .show()
    }
}