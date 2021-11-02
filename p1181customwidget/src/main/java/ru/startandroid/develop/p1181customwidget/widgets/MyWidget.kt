package ru.startandroid.develop.p1181customwidget.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.RemoteViews
import ru.startandroid.develop.p1181customwidget.ConfigActivity
import ru.startandroid.develop.p1181customwidget.R
import java.util.*

const val LOG_TAG = "myLogs"

class MyWidget : AppWidgetProvider() {
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        Log.d(LOG_TAG, "onEnabled")
    }

    /*
        В onUpdate мы перебираем все ID экземпляров, которые необходимо обновить и для каждого
        из них вызываем наш метод обновления
     */
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.d(LOG_TAG, "on Update, ${Arrays.toString(appWidgetIds)}")

        context?.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE).let {
            for (id in appWidgetIds!!) {
                updateWidget(context, appWidgetManager, it!!, id)
            }
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        Log.d(LOG_TAG, "onDeleted, ${Arrays.toString(appWidgetIds)}")
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        Log.d(LOG_TAG, "onDisabled")
    }

    companion object {
        //Метод updateWidget обновляет конкретный экземпляр виджета, получая на вход его ID.
        fun updateWidget(
            context: Context?,
            appWidgetManager: AppWidgetManager?,
            sp: SharedPreferences,
            widgetID: Int
        ) {
            /*
                Здесь мы читаем настройки (и сразу выходим, если нет настройки WIDGET_TEXT),
                    которые записало нам конфигурационное Activity для этого экземпляра виджета.
             */
            val widgetText = sp.getString(ConfigActivity.WIDGET_TEXT + widgetID,
                null) ?: return
            val widgetColor = sp.getInt(ConfigActivity.WIDGET_COLOR + widgetID, 0)

            /*
                Нам надо применить эти параметры к view-компонентам нашего виджета. Но за
                    отображение виджета отвечает один процесс (какой-нибудь Home), а наш код из
                    MyWidget будет выполняться в другом, своем собственном процессе. Поэтому у нас
                    нет прямого доступа к view-компонентам виджета. И мы не можем вызывать метода
                    типа setText и setBackgroundColor напрямую. Поэтому используется класс
                    RemoteViews, он предназначен для межпроцессной работы с view.
                    Создаем RemoteViews. На вход он принимает имя пакета нашего приложения и ID
                    layout-файла виджета. Теперь RemoteViews  знает view-структуру нашего виджета.
             */
            val widgetView = RemoteViews(context?.packageName, R.layout.widget)
            widgetView.apply {
                setTextViewText(R.id.tv, widgetText)
                setInt(R.id.tv, "setBackgroundColor", widgetColor)
            }

            /*
                RemoteViews сформирован. Используем AppWidgetManager, чтобы применить к виджету
                    наши сформированные view-настройки. Для этого используется метод
                    updateAppWidget, который на вход берет ID экземпляра виджета и объект
                    RemoteViews. Система найдет указанный экземпляр виджета и настроит его так,
                    как мы только что накодили.
             */
            appWidgetManager?.updateAppWidget(widgetID, widgetView)
        }
    }
}