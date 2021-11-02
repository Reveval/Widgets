package ru.startandroid.develop.p1211listwidget.services

import android.content.Intent
import android.widget.RemoteViewsService
import ru.startandroid.develop.p1211listwidget.adapters.MyFactory

/*
    Создаем сервис MyService. В нем мы просто реализуем метод onGetViewFactory, который создает
        адаптер, передает ему Context и Intent, и возвращает этот созданный адаптер системе.
 */

class MyService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return MyFactory(applicationContext, intent)
    }
}