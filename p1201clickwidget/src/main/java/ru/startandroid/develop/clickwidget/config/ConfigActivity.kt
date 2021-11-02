package ru.startandroid.develop.clickwidget.config

import android.appwidget.AppWidgetManager.*
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import ru.startandroid.develop.clickwidget.R
import ru.startandroid.develop.clickwidget.widgets.MyWidget

class ConfigActivity : AppCompatActivity() {
    private var widgetID = INVALID_APPWIDGET_ID

    lateinit var resultValue: Intent
    lateinit var sp: SharedPreferences
    lateinit var etFormat: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //извлекаем ID конфигурируемого виджета и проверяем его корректность
        val extras = intent.extras
        if (extras != null) {
            widgetID = extras.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)
        }

        if (widgetID == INVALID_APPWIDGET_ID) finish()

        //формируем intent ответа
        resultValue = Intent()
            .putExtra(EXTRA_APPWIDGET_ID, widgetID)

        //отрицательный ответ
        setResult(RESULT_CANCELED, resultValue)

        setContentView(R.layout.config)

        sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE)
        etFormat = findViewById(R.id.etFormat)
        etFormat.setText(sp.getString(WIDGET_TIME_FORMAT + widgetID, "HH:mm:ss"))

        val count = sp.getInt(WIDGET_COUNT + widgetID, -1)
        if (count == -1) sp.edit().putInt(WIDGET_COUNT + widgetID, 0).apply()
    }

    /*
        В onClick мы сохраняем в Preferences формат из EditText, обновляем виджет, формируем
            положительный ответ и выходим.
     */
    fun onClick(view: View) {
        sp.edit().putString(WIDGET_TIME_FORMAT + widgetID, etFormat.text.toString()).apply()
        MyWidget.updateWidget(this, getInstance(this), widgetID)
        setResult(RESULT_OK, resultValue)
        finish()
    }

    companion object {
        const val WIDGET_PREF = "widget_pref"
        const val WIDGET_TIME_FORMAT = "widget_time_format_"
        const val WIDGET_COUNT = "widget_count_"
    }
}