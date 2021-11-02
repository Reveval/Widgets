package ru.startandroid.develop.p1181customwidget

import android.appwidget.AppWidgetManager.*
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import ru.startandroid.develop.p1181customwidget.widgets.MyWidget

class ConfigActivity : AppCompatActivity() {
    var widgetID = INVALID_APPWIDGET_ID
    lateinit var resultValue: Intent

    /*
        В onCreate мы из Intent (параметр EXTRA_APPWIDGET_ID) извлекаем ID экземпляра виджета,
            который будет конфигурироваться этим экраном. Если видим, что получен некорректный ID,
            выходим. Если все ок, то формируем Intent с ID для метода setResult и говорим, что
            результат отрицательный. Теперь, если пользователь передумает создавать виджет и нажмет
            в конфигурационном экране Назад, то система будет знать, что виджет создавать не надо.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate config")

        //извлекаем ID конфигурируемого виджета
        val extras = intent.extras
        if (extras != null) widgetID = extras.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)

        //проверяем его на корректность
        if (widgetID == INVALID_APPWIDGET_ID) finish()

        //формируем intent ответа
        resultValue = Intent()
            .putExtra(EXTRA_APPWIDGET_ID, widgetID)

        //отрицательный ответ
        setResult(RESULT_CANCELED, resultValue)

        setContentView(R.layout.config)
    }

    /*
        В onClick мы читаем выбранный цвет и введенный в поле текст и пишем эти значения в
            Preferences. В имени записываемого параметра мы используем ID, чтобы можно было
            отличать параметры разных экземпляров друг от друга. Далее мы говорим системе, что
            результат работы положительный, и виджет можно создавать. Закрываем Activity.
     */
    fun onClick(view: View) {
        val color = when(findViewById<RadioGroup>(R.id.rgColor).checkedRadioButtonId) {
            R.id.radioRed -> Color.parseColor("#66ff0000")
            R.id.radioGreen -> Color.parseColor("#6600ff00")
            R.id.radioBlue -> Color.parseColor("#660000ff")
            else -> Color.RED
        }

        val editText = findViewById<EditText>(R.id.etText)

        //Записываем значения с экрана в Preferences
        val sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE)
        val editor = sp.edit()
        editor.apply {
            putString(WIDGET_TEXT + widgetID, editText.text.toString())
            putInt(WIDGET_COLOR + widgetID, color)
        }.apply()

        getInstance(this).let {
            MyWidget.updateWidget(this, it, sp, widgetID)
        }

        //положительный ответ
        setResult(RESULT_OK, resultValue)
        Log.d(LOG_TAG, "finish config $widgetID")
        finish()
    }

    companion object {
        const val LOG_TAG = "myLogs"
        const val WIDGET_PREF = "widget_pref"
        const val WIDGET_TEXT = "widget_text_"
        const val WIDGET_COLOR = "widget_color_"
    }
}