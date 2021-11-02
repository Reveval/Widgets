package ru.startandroid.develop.p1171simplewidget.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import java.util.*

const val LOG_TAG = "myLogs"

class MyWidget : AppWidgetProvider() {
    /*
        onEnabled вызывается системой при создании первого экземпляра виджета (мы ведь можем
            добавить в Home несколько экземпляров одного и того же виджета).
     */
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        Log.d(LOG_TAG, "onEnabled")
    }

    /*
        onUpdate вызывается при обновлении виджета. На вход, кроме контекста, метод получает объект
            AppWidgetManager и список ID экземпляров виджетов, которые обновляются. Именно этот
            метод обычно содержит код, который обновляет содержимое виджета. Для этого нам нужен
            будет AppWidgetManager, который мы получаем на вход.
     */
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.d(LOG_TAG, "on Update, ${Arrays.toString(appWidgetIds)}")
    }

    /*
        onDeleted вызывается при удалении каждого экземпляра виджета. На вход, кроме контекста,
            метод получает список ID экземпляров виджетов, которые удаляются.
     */
    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        Log.d(LOG_TAG, "onDeleted, ${Arrays.toString(appWidgetIds)}")
    }

    //onDisabled вызывается при удалении последнего экземпляра виджета.
    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        Log.d(LOG_TAG, "onDisabled")
    }
}