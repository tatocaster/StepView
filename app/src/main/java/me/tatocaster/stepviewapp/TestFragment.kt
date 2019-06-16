package me.tatocaster.stepviewapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_test.view.*

class TestFragment : Fragment() {

    @Override
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_test, container, false)
        view.numberText.text = " ${arguments!!.getInt(EXTRA_VALUE)}"
        return view
    }


    companion object {
        const val EXTRA_VALUE: String = "TestFragment._EXTRA_VALUE"

        fun newInstance(value: Int): TestFragment {

            val frag = TestFragment()
            val bundle = Bundle()
            bundle.putInt(EXTRA_VALUE, value + 1)
            frag.arguments = bundle
            return frag
        }
    }
}