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
import com.management.org.dcms.codes.activity.ModifyHouseHoldActivity
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.dcmsclient.data.models.HouseHold
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
        AuthConfigManager.getAuthToken().let { viewModel.getHouseholds(it) }
        adapter = ViewItemAdapter {
            handleModifyAction(it)
        }

        binding.households.adapter = adapter
        binding.households.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.containerAppBar.appBarTitleTV.text="House Hold List"
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


    private fun handleModifyAction(houseHold: HouseHold?) {
        if (houseHold==null){
            return
        }
        val hhId=houseHold.id
        val intent = Intent(this, ModifyHouseHoldActivity::class.java)
        intent.putExtra("hhId",hhId)
        startActivity(intent)
    }
}