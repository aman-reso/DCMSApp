package com.management.org.dcms.codes.dcmsclient.viewitem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.management.org.dcms.codes.dcmsclient.Manager
import com.management.org.dcms.codes.dcmsclient.data.models.HouseHoldsResponse
import com.management.org.dcms.codes.dcmsclient.util.UiState
import com.google.android.material.snackbar.Snackbar
import com.management.org.dcms.databinding.DcmsClientActivityViewItemBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewItemActivity : AppCompatActivity() {
    private lateinit var binding: DcmsClientActivityViewItemBinding
    private lateinit var adapter: ViewItemAdapter
    val viewModel by viewModels<ViewItemVIewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DcmsClientActivityViewItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Manager(application).token?.let { viewModel.getHouseholds(it) }
        adapter = ViewItemAdapter()
        binding.households.adapter = adapter
        binding.households.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        lifecycleScope.launch {
            viewModel.householdList.collect {
                when (it) {
                    is UiState.Success<*> -> {
                        stopload()
                        it.data as HouseHoldsResponse
                        adapter.submitList(it.data.households)
                        if (it.data.households.isEmpty()) {
                            binding.households.visibility = View.GONE
                            binding.empty.visibility = View.VISIBLE
                        }
                    }
                    is UiState.Loading -> {
                        startload()
                    }
                    is UiState.Failed -> {
                        stopload()
                        Snackbar.make(binding.root, "${it.message}", Snackbar.LENGTH_LONG).show()
                    }


                }
            }
        }

    }
    fun startload() {
        binding.progress.visibility = View.VISIBLE
        binding.households.visibility = View.GONE
    }

    fun stopload() {
        binding.progress.visibility = View.GONE
        binding.households.visibility = View.VISIBLE
    }
}