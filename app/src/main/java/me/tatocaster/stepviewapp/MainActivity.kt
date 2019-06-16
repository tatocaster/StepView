package me.tatocaster.stepviewapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = MyPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter

        stepView.setupWithViewPager(viewPager)
    }

    class MyPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(p0: Int): Fragment {
            return TestFragment.newInstance(p0)
        }

        override fun getCount(): Int {

            return 3
        }

    }
}
