package ru.startandroid.develop.clickwidget.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import ru.startandroid.develop.clickwidget.Helper
import ru.startandroid.develop.clickwidget.R
import ru.startandroid.develop.clickwidget.config.ConfigActivity
import java.text.SimpleDateFormat
import java.util.*

const val ACTION_CHANGE = "ru.startandroid.develop.p1201clickwidget.change_count"

class MyWidget : AppWidgetProvider() {
    //В onUpdate мы обновляем все требующие обновления экземпляры
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        //обновляем все экземпляры
        if (appWidgetIds != null) {
            for (i in appWidgetIds) {
                updateWidget(context, appWidgetManager, i)
            }
        }
    }

    //в onDelete подчищаем Preferences после удаления экземпляров.
    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)

        context?.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE)?.edit()
            ?.apply {
                if (appWidgetIds != null) {
                    for (widgetID in appWidgetIds) {
                        remove(ConfigActivity.WIDGET_TIME_FORMAT + widgetID)
                        remove(ConfigActivity.WIDGET_COUNT + widgetID)
                    }
                }
            }?.apply()
    }

    /*
        В методе onReceive мы обязательно выполняем метод onReceive родительского класса, иначе
            просто перестанут работать обновления и прочие стандартные события виджета. Далее мы
            проверяем, что intent содержит наш action, читаем и проверяем ID из него, читаем из
            настроек значение счетчика, увеличиваем на единицу, пишем обратно в настройки и
            обновляем экземпляр виджета. Он прочтет новое значение счетчика из настроек и
            отобразит его.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        //проверяем, что этот intent от нажатия на 3-ю зону
        if (intent != null && context != null && intent.action.equals(ACTION_CHANGE, true)) {
            //извлекаем id
            var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
            val extras = intent.extras
            if (extras != null) {
                mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
            }
            if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                //читаем значение счестика, увеличиваем на единицу и записываем
                val sp = context.getSharedPreferences(ConfigActivity.WIDGET_PREF,
                    Context.MODE_PRIVATE)
                var counter = sp.getInt(ConfigActivity.WIDGET_COUNT + mAppWidgetId, 0)
                sp.edit().putInt(ConfigActivity.WIDGET_COUNT + mAppWidgetId, ++counter).apply()

                //обновляем виджет
                updateWidget(context, AppWidgetManager.getInstance(context), mAppWidgetId)
            }
        }
    }

    companion object {
        /*
            Метод updateWidget отвечает за обновления конкретного экземпляра виджета. Здесь мы
                настраиваем внешний вид и реакцию на нажатие.
         */
        fun updateWidget(
            ctx: Context?,
            appWidgetManager: AppWidgetManager?,
            widgetID: Int
        ) {
            val sp = ctx?.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE)

            /*
                Сначала мы читаем настройки формата времени (которые были сохранены в
                    конфигурационном экране), берем текущее время и конвертируем в строку
                    согласно формату.
             */
            val timeFormat = sp?.getString(ConfigActivity.WIDGET_TIME_FORMAT + widgetID,
                null) ?: return
            val currentTime = SimpleDateFormat(timeFormat, Locale.ROOT)
                .format(Date(System.currentTimeMillis()))

            //Также из настроек читаем значение счетчика.
            val count = sp.getInt(ConfigActivity.WIDGET_COUNT + widgetID, 0).toString()

            //Создаем RemoteViews и помещаем время и счетчик в соответствующие TextView.
            val widgetView = RemoteViews(ctx.packageName, R.layout.widget)
            widgetView.apply {
                setTextViewText(R.id.tvTime, currentTime)
                setTextViewText(R.id.tvCount, count)
            }

            /*
                Далее идет настройка обработки нажатия. Сначала мы готовим Intent, который содержит
                    в себе некие данные и знает куда он должен отправиться. Этот Intent мы
                    упаковываем в PendingIntent. Далее конкретному view-компоненту мы методом
                    setOnClickPendingIntent сопоставляем PendingIntent. И когда будет совершено
                    нажатие на этот view, система достанет Intent из PendingIntent и отправит его
                    по назначению.
                    В нашем виджете есть три зоны для нажатия. Для каждой из них мы формируем
                    отдельный Intent и PendingIntent. Первая зона – по нажатию должно открываться
                    конфигурационное Activity. Создаем Intent, который будет вызывать наше
                    Activity, помещаем данные об ID (чтобы экран знал, какой экземпляр он
                    настраивает), упаковываем в PendingIntent и сопоставляем view-компоненту первой
                    зоны.
             */
            val configIntent = Intent(ctx, ConfigActivity::class.java)
            configIntent.apply {
                action = AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
            }
            var pIntent = PendingIntent.getActivity(ctx, widgetID, configIntent, 0)
            widgetView.setOnClickPendingIntent(R.id.tvPressConfig, pIntent)

            /*
                Вторая зона – по нажатию должен обновляться виджет, на котором было совершено
                    нажатие. Создаем Intent, который будет вызывать наш класс виджета, добавляем
                    ему action = ACTION_APPWIDGET_UPDATE,  помещаем данные об ID (чтобы обновился
                    именно этот экземпляр), упаковываем в PendingIntent и сопоставляем
                    view-компоненту второй зоны.
             */
            val updateIntent = Intent(ctx, MyWidget::class.java)
            updateIntent.apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, Helper.getSmallIntArray(widgetID))
            }
            pIntent = PendingIntent.getBroadcast(ctx, widgetID, updateIntent, 0)
            widgetView.setOnClickPendingIntent(R.id.tvPressUpdate, pIntent)

            /*
                Третья зона – по нажатию должен увеличиваться на единицу счетчик нажатий. Создаем
                    Intent, который будет вызывать наш класс виджета, добавляем ему наш собственный
                    action = ACTION_CHANGE,  помещаем данные об ID (чтобы работать со счетчиком
                    именно этого экземпляра), упаковываем в PendingIntent и сопоставляем
                    view-компоненту третьей зоны.
             */
            val countIntent = Intent(ctx, MyWidget::class.java)
            configIntent.apply {
                action = ACTION_CHANGE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, Helper.getSmallIntArray(widgetID))
            }
            pIntent = PendingIntent.getBroadcast(ctx, widgetID, countIntent, 0)
            widgetView.setOnClickPendingIntent(R.id.tvPressCount, pIntent)
            appWidgetManager?.updateAppWidget(widgetID, widgetView)
        }
    }
}