package ru.startandroid.develop.p1251viewpager.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.startandroid.develop.p1251viewpager.R

const val ARGUMENT_PAGE_NUMBER = "arg_page_number"

class PageFragment : Fragment() {
    var pageNumber = 0
    var backColor = 0

    companion object {
        fun newInstance(page: Int) : PageFragment {
            val pageFragment = PageFragment()
            return pageFragment.apply {
                val arguments = Bundle()
                arguments.putInt(ARGUMENT_PAGE_NUMBER, page)
                setArguments(arguments)
            }
        }
    }

    /*
        В onCreate читаем номер страницы из аргументов. Далее формируем цвет из рандомных
            компонентов. Он будет использоваться для фона страниц, чтобы визуально отличать одну
            страницу от другой.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageNumber = arguments?.getInt(ARGUMENT_PAGE_NUMBER) ?: 0
        val rnd = java.util.Random()
        backColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256),
            rnd.nextInt(256))
    }

    /*
        В onCreateView создаем View, находим на нем TextView, пишем ему простой текст с номером
            страницы и ставим фоновый цвет.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment, null).also {
            it.findViewById<TextView>(R.id.tvPage).apply {
                text = "Page $pageNumber"
                setBackgroundColor(backColor)
            }
        }
    }
}