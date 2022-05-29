package com.management.org.dcms.codes.dcmsclient.signup

import android.annotation.SuppressLint
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.text.format.Formatter
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.management.org.dcms.codes.dcmsclient.util.UiState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.management.org.dcms.R
import com.management.org.dcms.codes.activity.LocationValue
import com.management.org.dcms.codes.activity.LoginActivity
import com.management.org.dcms.codes.dcmsclient.data.models.*
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.databinding.DcmsClientActivitySignupBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {
    val viewModel by viewModels<SignupViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: DcmsClientActivitySignupBinding
    var stateId: Int = 0
    var districtId: Int = 0
    var blockId: Int = 0
    var gpId: Int = 0
    var villageId: Int = 0

    @SuppressLint("MissingPermission", "HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DcmsClientActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getStateList()
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val statesList = mutableListOf<String>()
        val districtList = mutableListOf<String>()
        val blockList = mutableListOf<String>()
        val gpList = mutableListOf<String>()
        val villageList = mutableListOf<String>()
        var districtJob: Job? = null
        var blockJob: Job? = null
        var gbJob: Job? = null
        var vJob: Job? = null
        lifecycleScope.launch {
            viewModel.stateList.collect {
                when (it) {
                    is UiState.Success<*> -> {
                        loadingStop()
                        it.data as StateListResponse
                        it.data.states.forEach {
                            statesList.add(it.stateName)
                        }
                        val stateAdapter = ArrayAdapter(
                            this@SignupActivity,
                            R.layout.dcms_client_simple_spinner_item,
                            statesList
                        )
                        (binding.statesSpinner as? AutoCompleteTextView)?.setAdapter(
                            stateAdapter
                        )
                        binding.statesSpinner.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                            }

                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {
                            }

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                                try {
                                    Timber.i("SELECTED = ${it.data.states[statesList.indexOf(s.toString())]}")

                                    stateId = it.data.states[statesList.indexOf(s.toString())].id
                                    districtJob?.cancel()
                                    districtJob = lifecycleScope.launch(Dispatchers.IO) {
                                        launch {
                                            viewModel.specificDistrict(stateId)
                                        }
                                    }
                                } catch (e: Exception) {
                                }
                            }
                        })

                    }
                    is UiState.Loading -> {
                        setLoading()
                    }
                    is UiState.Failed -> {
                        loadingStop()
                    }

                }

            }
        }
        lifecycleScope.launch {
            viewModel.districtList.collect {
                when (it) {
                    is UiState.Success<*> -> {
                        loadingStop()
                        it.data as DistrictResponse
                        it.data.districts.forEach {
                            districtList.add(it.districtName)
                        }
                        val districtAdapter = ArrayAdapter(
                            this@SignupActivity,
                            R.layout.dcms_client_simple_spinner_item,
                            districtList
                        )
                        (binding.DistrictSpinner as? AutoCompleteTextView)?.setAdapter(
                            districtAdapter
                        )
                        binding.DistrictSpinner.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                            }

                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {
                            }

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                                try {
                                    Timber.i("SELECTED = ${it.data.districts[districtList.indexOf(s.toString())]}")

                                    districtId =
                                        it.data.districts[districtList.indexOf(s.toString())].id
                                    blockJob?.cancel()
                                    blockJob = lifecycleScope.launch(Dispatchers.IO) {
                                        launch {
                                            viewModel.getBlockById(districtId)
                                        }
                                    }
                                } catch (e: Exception) {
                                    Timber.e("$e")
                                }
                            }
                        })

                    }
                    is UiState.Loading -> {
                        setLoading()
                    }
                    is UiState.Failed -> {
                        loadingStop()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.blockList.collect {
                when (it) {
                    is UiState.Success<*> -> {
                        loadingStop()
                        it.data as BlockResponse
                        it.data.blocks.forEach {
                            blockList.add(it.blockName)
                        }
                        val stateAdapter = ArrayAdapter(
                            this@SignupActivity,
                            R.layout.dcms_client_simple_spinner_item,
                            blockList
                        )
                        (binding.blocksSpinner as? AutoCompleteTextView)?.setAdapter(
                            stateAdapter
                        )
                        binding.blocksSpinner.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                            }

                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {
                            }

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                                try {
                                    Timber.i("SELECTED = ${it.data.blocks[blockList.indexOf(s.toString())]}")

                                    blockId = it.data.blocks[blockList.indexOf(s.toString())].id
                                    gbJob?.cancel()
                                    gbJob = lifecycleScope.launch(Dispatchers.IO) {
                                        launch {
                                            viewModel.getGpById(blockId)
                                        }
                                    }
                                } catch (e: Exception) {
                                }
                            }
                        })

                    }
                    is UiState.Loading -> {
                        setLoading()
                    }
                    is UiState.Failed -> {
                        loadingStop()
                    }

                }
            }
        }

        lifecycleScope.launch {
            viewModel.gpList.collect {
                when (it) {
                    is UiState.Success<*> -> {
                        loadingStop()
                        it.data as GpResponse
                        it.data.gp?.forEach {
                            gpList.add(it.GpName)
                        }
                        val stateAdapter = ArrayAdapter(
                            this@SignupActivity,
                            R.layout.dcms_client_simple_spinner_item,
                            gpList
                        )
                        (binding.gramSpinner as? AutoCompleteTextView)?.setAdapter(
                            stateAdapter
                        )
                        binding.gramSpinner.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                            }

                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {
                            }

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                                try {
                                    Timber.i("SELECTED = ${it.data.gp?.get(gpList.indexOf(s.toString()))}")

                                    gpId = it.data.gp?.get(gpList.indexOf(s.toString()))?.id ?: 0
                                    vJob?.cancel()
                                    vJob = lifecycleScope.launch(Dispatchers.IO) {
                                        launch {
                                            viewModel.getVillageBId(gpId)
                                        }
                                    }
                                } catch (e: Exception) {
                                }
                            }
                        })

                    }
                    is UiState.Loading -> {
                        setLoading()
                    }
                    is UiState.Failed -> {
                        loadingStop()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.villageList.collect {
                when (it) {
                    is UiState.Success<*> -> {
                        loadingStop()
                        it.data as VillageResponse
                        it.data.villages.forEach {
                            villageList.add(it.villageName)
                        }
                        val stateAdapter = ArrayAdapter(
                            this@SignupActivity,
                            R.layout.dcms_client_simple_spinner_item,
                            villageList
                        )
                        (binding.villageSpinner as? AutoCompleteTextView)?.setAdapter(
                            stateAdapter
                        )
                        binding.villageSpinner.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                            }

                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {
                            }

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                                try {
                                    Timber.i("SELECTED = ${it.data.villages[villageList.indexOf(s.toString())]}")

                                    villageId =
                                        it.data.villages[villageList.indexOf(s.toString())].id
                                } catch (e: Exception) {
                                }
                            }
                        })

                    }
                    is UiState.Loading -> {
                        setLoading()
                    }
                    is UiState.Failed -> {
                        loadingStop()
                    }

                }

            }
        }


        binding.register.setOnClickListener {
            if (stateId != 0 && districtId != 0 && blockId != 0 && gpId != 0 && villageId != 0 && !binding.name.text.isNullOrEmpty() &&
                !binding.email.text.isNullOrEmpty() && !binding.mobile.text.isNullOrEmpty() && !binding.password.text.isNullOrEmpty()
            ) {
                val latitude: String = LocationValue.latitude
                val longitude: String = LocationValue.longitude
                val a = CancellationTokenSource().token
                fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    a
                ).addOnSuccessListener {
                    val deviceId =
                        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                    val ipAddress: String =
                        Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
                    viewModel.userRegister(
                        stateId,
                        districtId,
                        blockId,
                        gpId,
                        villageId,
                        binding.name.text.toString(),
                        binding.password.text.toString(),
                        binding.email.text.toString(),
                        binding.mobile.text.toString(),
                        deviceId,
                        latitude,longitude
                    )

                }.addOnFailureListener {
                    Timber.e("$it")
                }
            } else {
                Snackbar.make(binding.root, "Enter the values", Snackbar.LENGTH_LONG).show()
            }

        }

        lifecycleScope.launch {
            viewModel.signupResponse.collect {
                when (it) {
                    is UiState.Success<*> -> {
                        Utility.showToastMessage("Account created")
                        //Snackbar.make(binding.root,"Account created",Snackbar.LENGTH_LONG).show()
                        startLoginActivity()
                        finish()
                    }
                    is UiState.Failed -> {
                        Utility.showToastMessage(it.message.toString())
                        //  Snackbar.make(binding.root,it.message.toString(),Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun startLoginActivity() {
        val homeLandingIntent = Intent(this, LoginActivity::class.java);
        startActivity(homeLandingIntent)
        finish()
    }
    private fun setLoading() {
        binding.progress.visibility = View.VISIBLE
        binding.register.text = "Loading..."
        binding.register.isEnabled = false
    }
    private fun loadingStop() {
        binding.progress.visibility = View.GONE
        binding.register.text = "Register"
        binding.register.isEnabled = true
    }


}