package ru.startandroid.develop.p1211listwidget.adapters

import android.appwidget.AppWidgetManager.*
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import ru.startandroid.develop.p1211listwidget.R
import ru.startandroid.develop.p1211listwidget.providers.MyProvider
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/*
    Создаем класс-адаптер. Используем основной конструктор с двумя параметрами – Context и Intent.
        Этот Intent будет передавать нам сервис при создании адаптера. В нем передаем адаптеру
        ID виджета.
 */
class MyFactory(ctx: Context, intent: Intent?) : RemoteViewsService.RemoteViewsFactory {
    lateinit var data: ArrayList<String>
    private val context = ctx
    private val sdf = SimpleDateFormat("HH:mm:ss", Locale.ROOT)
    private val widgetID =  intent?.getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)

    //создание адаптера
    override fun onCreate() {
        data = ArrayList()
    }

    /*
        onDataSetChanged – вызывается, когда поступил запрос на обновление данных в списке. Т.е.
            в этом методе мы подготавливаем данные для списка. Метод заточен под выполнение
            тяжелого долгого кода. В трех первых пунктах списка мы выводим текущее время, хэш-код
            адаптера и ID-виджета.
     */
    override fun onDataSetChanged() {
        data.apply {
            clear()
            add(sdf.format(Date(System.currentTimeMillis())))
            add(hashCode().toString())
            add(widgetID.toString())
            for (i in 3 until 15) {
                add("Item $i")
            }
        }
    }

    /*
        onDestroy – вызывается при удалении последнего списка, который использовал адаптер (один
            адаптер может использоваться несколькими списками).
     */
    override fun onDestroy() {}

    override fun getCount(): Int {
        return data.size
    }

    //getViewAt – создание пунктов списка. Здесь идет стандартное использование RemoteViews
    override fun getViewAt(position: Int): RemoteViews {
        val rView = RemoteViews(context.packageName, R.layout.item)
        return rView.apply {
            setTextViewText(R.id.tvItemText, data[position])
            /*
                Для каждого пункта списка мы создаем Intent, помещаем в него позицию пункта и
                    вызываем setOnClickFillInIntent. Этот метод получает на вход ID View и Intent.
                    Для View с полученным на вход ID он создает обработчик нажатия, который будет
                    дергать PendingIntent, который получается следующим образом. Берется шаблонный
                    PendingIntent, который был привязан к списку методом setPendingIntentTemplate
                    (в классе провайдера) и к нему добавляются данные полученного на вход Intent-а.
                    Т.е. получится PendingIntent, Intent которого будет содержать action =
                    ACTION_ON_CLICK (это мы сделали еще в провайдере) и данные по позиции пункта
                    списка. При нажатии на пункт списка, этот Intent попадет в onReceive нашего
                    MyProvider и будет обработан,
             */
            val clickIntent = Intent()
                .putExtra(MyProvider.ITEM_POSITION, position)
            setOnClickFillInIntent(R.id.tvItemText, clickIntent)
        }
    }

    /*
        getLoadingView – здесь вам предлагается возвращать View, которое система будет показывать
            вместо пунктов списка, пока они создаются. Если ничего здесь не создавать, то система
            использует некое дефолтное View.
     */
    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}