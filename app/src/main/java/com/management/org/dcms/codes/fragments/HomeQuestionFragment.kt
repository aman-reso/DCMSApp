package com.management.org.dcms.codes.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class HomeQuestionFragment : Fragment() {
    fun newInstance(): Fragment {
        val args = Bundle()
        val fragment = HomeQuestionFragment()
        fragment.arguments = args
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}