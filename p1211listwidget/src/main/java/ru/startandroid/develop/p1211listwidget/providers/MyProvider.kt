package ru.startandroid.develop.p1211listwidget.providers

import android.app.PendingIntent
import android.appwidget.AppWidgetManager.*
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import ru.startandroid.develop.p1211listwidget.Helper
import ru.startandroid.develop.p1211listwidget.R
import ru.startandroid.develop.p1211listwidget.services.MyService
import java.text.SimpleDateFormat
import java.util.*

class MyProvider : AppWidgetProvider() {
    private val sdf = SimpleDateFormat("HH:mm:ss", Locale.ROOT)

    /*
        onUpdate вызывается, когда поступает запрос на обновление виджетов. В нем мы перебираем ID,
            и для каждого вызываем метод updateWidget.
     */
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        if (appWidgetIds != null) {
            for (i in appWidgetIds) {
                updateWidget(context, appWidgetManager, i)
            }
        }
    }

    /*
        updateWidget – здесь вызываем три метода для формирования виджета и затем метод
            updateAppWidget, чтобы применить все изменения к виджету.
     */
    private fun updateWidget(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId : Int
    ) {
        val rv = RemoteViews(context?.packageName, R.layout.widget)

        setUpdateTV(rv, context, appWidgetId)
        setList(rv, context, appWidgetId)
        setListClick(rv, context, appWidgetId)

        appWidgetManager?.updateAppWidget(appWidgetId, rv)
        appWidgetManager?.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lvList)
    }

    /*
        setUpdateTV – в этом  методе работаем с TextView (который над списком). Ставим ему время
            в качестве текста и вешаем обновление виджета по нажатию.
     */
    private fun setUpdateTV(rv: RemoteViews, context: Context?, appWidgetId: Int) {
        rv.setTextViewText(R.id.tvUpdate, sdf.format(Date(System.currentTimeMillis())))
        val updIntent = Intent(context, MyProvider::class.java)
        updIntent.apply {
            action = ACTION_APPWIDGET_UPDATE
            putExtra(EXTRA_APPWIDGET_IDS, Helper.getSmallIntArray(appWidgetId))
        }
        val updPIntent = PendingIntent.getBroadcast(context, appWidgetId, updIntent, 0)
        rv.setOnClickPendingIntent(R.id.tvUpdate, updPIntent)
    }

    /*
        setList – с помощью метода setRemoteAdapter указываем списку, что для получения адаптера
            ему надо будет обратиться к нашему сервису MyService.
     */
    private fun setList(rv: RemoteViews, context: Context?, appWidgetId: Int) {
        rv.also {
            val adapter = Intent(context, MyService::class.java)
                .putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
            it.setRemoteAdapter(R.id.lvList, adapter)
        }
    }

    /*
        здесь обрабатываем нажатия на пункты ListView. Используем обычный алгоритм послания
            бродкаста. Мы с помощью метода setPendingIntentTemplate устанавливаем шаблонный
            PendingIntent, который затем будет использоваться всеми пунктами списка. В нем мы
            указываем, что необходимо будет вызвать наш класс провайдера (он же BroadcastReceiver)
            с action = ACTION_ON_CLICK.
     */
    private fun setListClick(rv: RemoteViews, context: Context?, appWidgetId: Int) {
        val listClickIntent = Intent(context, MyProvider::class.java)
            .setAction(ACTION_ON_CLICK)
        val listClickPIntent = PendingIntent.getBroadcast(context, 0,
            listClickIntent, 0)
        rv.setOnClickPendingIntent(R.id.lvList, listClickPIntent)
    }

    /*
        Теперь нам надо сделать обработку action ACTION_ON_CLICK. Вызываем метод родителя, чтобы
            не нарушать работу провайдера. Далее проверяем, что action тот, что нам нужен -
            ACTION_ON_CLICK, вытаскиваем позицию нажатого пункта в списке и выводим сообщение на
            экран.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action.equals(ACTION_ON_CLICK, true)) {
            val itemPos = intent?.getIntExtra(ITEM_POSITION, -1)
            if (itemPos != -1) {
                Toast.makeText(context, "Clicked on item $itemPos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val ACTION_ON_CLICK = "ru.startandroid.develop.p1211listwidget.itemonclick"
        const val ITEM_POSITION = "item_position"
    }
}