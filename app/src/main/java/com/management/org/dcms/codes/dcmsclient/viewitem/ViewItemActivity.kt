package com.management.org.dcms.codes.dcmsclient.viewitem

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.management.org.dcms.codes.activity.PopScreen
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.dcmsclient.data.models.HouseHoldsResponse
import com.management.org.dcms.codes.dcmsclient.util.UiState
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.databinding.DcmsClientActivityViewItemBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ViewItemActivity : AppCompatActivity() {
    private lateinit var binding: DcmsClientActivityViewItemBinding
    private lateinit var adapter: ViewItemAdapter
    val viewModel by viewModels<ViewItemVIewModel>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DcmsClientActivityViewItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AuthConfigManager.getAuthToken()?.let { viewModel.getHouseholds(it) }
        adapter = ViewItemAdapter()
        binding.households.adapter = adapter
        binding.households.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.containerAppBar.appBarTitleTV.text="Added Household"
        binding.containerAppBar.icNavBackIcon.showHideView(true)

        binding.containerAppBar.icNavBackIcon.setOnClickListener {
            onBackPressed()
        }

        lifecycleScope.launch {
            viewModel.householdList.collect {
                when (it) {
                    is UiState.Success<*> -> {
                        stopLoad()
                        it.data as HouseHoldsResponse
                        adapter.submitList(it.data.households)
                        if (it.data.households.isEmpty()) {
                            binding.households.visibility = View.GONE
                            binding.empty.visibility = View.VISIBLE
                        }
                    }
                    is UiState.Loading -> {
                        startLoad()
                    }
                    is UiState.Failed -> {
                        stopLoad()
                        Snackbar.make(binding.root, "${it.message}", Snackbar.LENGTH_LONG).show()
                    }


                }
            }
        }

    }

    private fun startLoad() {
        binding.progress.visibility = View.VISIBLE
        binding.households.visibility = View.GONE
    }

    private fun stopLoad() {
        binding.progress.visibility = View.GONE
        binding.households.visibility = View.VISIBLE
    }


    @SuppressLint("ClickableViewAccessibility")
    fun onButtonShowPopupWindowClick(view: View?) {


        // inflate the layout of the popup window
        /*val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_window, null)

        // create the popup window
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        // dismiss the popup window when touched
        popupView.setOnTouchListener { v, event ->
            popupWindow.dismiss()
            true
        }*/

        val intent = Intent(this, PopScreen::class.java)
        startActivity(intent)
        finish()
    }
}