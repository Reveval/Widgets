package ru.startandroid.develop.p1251viewpager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import ru.startandroid.develop.p1251viewpager.fragments.PageFragment

const val PAGE_COUNT = 10

class MainActivity : AppCompatActivity() {
    lateinit var pagerAdapter: FragmentStateAdapter
    lateinit var pager: ViewPager2

    //В onCreate создаем адаптер и устанавливаем его для ViewPager.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pagerAdapter = MyFragmentPagerAdapter(this)
        pager = findViewById(R.id.pager)
        pager.adapter = pagerAdapter
    }

    /*
        этот класс позволяет организовать прокрутки списка фрагментов.
            FragmentStateAdapter хранит только текущую страницу и по одной соседней (справа и
                слева), чтобы быстро можно было перелистнуть. Этот адаптер не очень быстрый, он
                будет подтормаживать при многократном перелистывании в обе стороны, т.к. постоянно
                пересоздает страницы. Но при этом он требует минимум памяти. Т.е. он подходит для
                большого количества страниц. Например, просмотр писем, смс, страниц книги.
     */
    class MyFragmentPagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
        //здесь мы должны возвращать кол-во страниц, используем константу
        override fun getItemCount(): Int {
            return PAGE_COUNT
        }

        //по номеру страницы нам надо вернуть фрагмент, используем наш метод newInstance
        override fun createFragment(position: Int): Fragment {
            return PageFragment.newInstance(position)
        }
    }
}