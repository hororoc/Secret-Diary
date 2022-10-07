package com.example.secretdiary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener

class DiaryActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper()) //  메인 스레드에 연결된 핸들러

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        val diaryEditText: EditText = findViewById(R.id.diaryEditText)
        val diaryPreferences = getSharedPreferences("diary", Context.MODE_PRIVATE)
        diaryEditText.setText(diaryPreferences.getString("diary", ""))

        // addTextChangedListener 를 사용해서 텍스트가 변경될 때마다 SharedPreferences 에 저장할 수 있으나,
        // 너무 자주 저장하게 되므로 스레드를 이용해서 입력 후 몇 초가 지나면 저장하도록 구현한다.

        val runnable = Runnable { // 스레드에서 일어나는 기능
            getSharedPreferences("diary", Context.MODE_PRIVATE).edit(false) {
                putString("diary", diaryEditText.text.toString())
            }
            Log.d("DiaryActivity", "TextSaved :: ${diaryEditText.text.toString()}")
        }
        diaryEditText.addTextChangedListener {
            Log.d("DiaryActivity", "TextChanged :: $it")
            handler.removeCallbacks(runnable) // 만약 0.5가 되기 이전에 .addTextChangedListener 가 호출되면 0.5초 딜레이를 초기화한다.
            handler.postDelayed(runnable, 500) // 텍스트 수정 후 0.5초가 지나면 SharedPreferences 에 저장된다.
            // TODO: 이 경우 0.5초가 지나기 전에 DiaryActivity 를 벗어나게 되면 저장하지 않게 된다.
        }
    }
}