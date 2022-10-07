# Secret-Diary
Secret Diary Android Application

---

### Custom Font

- `/res/font` 디렉터리 생성 후 폰트 업로드
- 리소스 파일명은 소문자여야 함.

### Theme

- `/res/values/themes.xml`
- 버튼의 경우 `androidx.appcompat.widget.AppCompatButton`를 사용하여 테마에 영향을 받지않게 함.

### ActionBar 없애기

1. /res/values/themes.xml 에 스타일 추가

```kotlin
<style name="Theme.SecretDiary.NoActionBar" parent="Theme.MaterialComponents.DayNight.NoActionBar"/>
```

1. AndroidManifest.xml에 activity theme 지정

```kotlin
android:theme="@style/Theme.SecretDiary.NoActionBar"
```

### by lazy + .apply 활용

```kotlin
private val numberPicker1: NumberPicker by lazy {
    findViewById<NumberPicker>(R.id.numberPicker1)
		    .apply {
            this.minValue = 0
            this.maxValue = 9
        }
}
```

### SharedPreference

- Docs
    
    ```java
    public android.content.SharedPreferences getSharedPreferences(
        String name,
    		int mode
    )
    ```
    
    ```java
    public abstract String getString(
    		String s, // file name
    		String s1 // default value (파일이 없을 경우)
    )
    ```
    
1. getSharedPreferences(*NAME, MODE*)
2. (GET) getString(*NAME, DEFAULT_VALUE*)
3. (EDIT) edit() ← ktx function
    
    저장 방법 2가지
    
    - **commit**: UI 스레드를 block 하고, 저장이 완료될 때까지 기다림. 즉, 작업이 끝날 때까지 UI(화면)가 멈춤.
        
        따라서 무거운 작업을 할 때는
        
        1. 스레드를 열어서 작업을 하거나,
        2. apply를 통해서 작업하는 것이 좋음.
    - **apply**: UI 스레드를 block 하지 않고, async하게 작업.

```kotlin
val pwPreferences = getSharedPreferences("password", Context.MODE_PRIVATE)
// Context.MODE_PRIVATE: 다른 앱들에서 사용할 수 없게 함.

pwPreferences.getString("password", "000")
...
pwPreferences.edit(true) {
		...
		putString
}
```

### AlertDialog

- builder 패턴

```kotlin
AlertDialog.Builder(this)
		.setTitle("실패")
		.setMessage("비밀번호가 잘못되었습니다.")
		.setPositiveButton("확인") { _, _ -> }
		.create()
		.show()
```

### Handler

- 스레드와 스레드 간의 통신을 엮어주는 안드로이드에서 제공하는 기능.
- runOnUiThread 에서 사용하는 기능이 handler

```kotlin
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
```
