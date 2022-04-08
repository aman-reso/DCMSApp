package com.management.org.dcms.codes.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.management.org.dcms.R
import com.management.org.dcms.codes.activity.datacollection.AddItemViewModel
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.models.VillageResponse
import com.management.org.dcms.codes.models.WardResponse
import com.management.org.dcms.codes.network.path.UiState
import com.management.org.dcms.databinding.ActivityDataCollectionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException

@AndroidEntryPoint
class DataCollectionActivity : AppCompatActivity() {

    val viewModel by viewModels<AddItemViewModel>()
    private var binding: ActivityDataCollectionBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var fileUri: Uri? = null

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                fileUri = data?.data!!
                binding?.image?.setImageURI(fileUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_data_collection)

        setObserver()
        val villageList = mutableListOf<String>()
        var villageName = ""
        var villageId = 0
        var wardJob: Job = Job()
        var mobileType = ""
        val mobileTypeList = listOf("Feature", "Smart")
        var documentType = ""
        val documentTypeList = listOf("AADHAAR", "VOTERID")
        val wardList = mutableListOf<String>()
        binding?.toolbar?.setNavigationOnClickListener {
            finish()
        }

        binding?.imageSelector?.setOnClickListener {
            ImagePicker.with(this).compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080).cameraOnly().crop().createIntent {
                    startForProfileImageResult.launch(it)
                }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        lifecycleScope.launch {
            viewModel.allVillages.collect {
                when (it) {
                    is UiState.Success<*> -> {
                        stopLoad()
                        it.data as VillageResponse
                        it.data.Villages.forEach {
                            villageList.add(it.VillageName)
                        }
                        Timber.e("$villageList")
                        val stateAdapter = ArrayAdapter(
                            this@DataCollectionActivity,
                            R.layout.simple_spinner_item,
                            villageList
                        )
                        (binding?.villageSpinner as? AutoCompleteTextView)?.setAdapter(
                            stateAdapter
                        )
                        binding?.villageSpinner?.addTextChangedListener(object : TextWatcher {
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
                                    Timber.i("SELECTED = ${it.data.Villages[villageList.indexOf(s.toString())]}")
                                    villageName = s.toString()
                                    villageId =
                                        it.data.Villages[villageList.indexOf(s.toString())].Id
                                    wardJob.cancel()
                                    wardJob = lifecycleScope.launch(Dispatchers.IO) {
                                        viewModel.getWardById(villageId)
                                    }

                                } catch (e: Exception) {
                                }
                            }
                        })

                    }

                    is UiState.Failed -> {
                        Toast.makeText(this@DataCollectionActivity, it.message, Toast.LENGTH_SHORT).show()
//                        Snackbar.make(binding.root, it.message.toString(), Snackbar.LENGTH_SHORT)
//                            .show()
                        stopLoad()
                    }
                    is UiState.Loading -> {
                        startLoad()
                    }

                }

            }
        }

        lifecycleScope.launch {
            viewModel.wardById.collect {
                when (it) {
                    is UiState.Loading -> {
                        startLoad()
                    }
                    is UiState.Success<*> -> {
                        stopLoad()
                        it.data as WardResponse
                        it.data.wards.forEach {
                            wardList.add(it.WardNo)
                        }
                        val wardAdapter = ArrayAdapter(
                            this@DataCollectionActivity,
                            R.layout.simple_spinner_item,
                            wardList
                        )
                        binding?.ward?.setAdapter(
                            wardAdapter
                        )
                        binding?.ward?.addTextChangedListener(object : TextWatcher {
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
                                    Timber.i("SELECTED = ${it.data.wards[wardList.indexOf(s.toString())]}")
                                    mobileType = s.toString()
                                } catch (e: Exception) {
                                }
                            }
                        })
                    }
                    is UiState.Failed -> {
                        stopLoad()
                    }
                }
            }
        }

        val mobileAdapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            mobileTypeList
        )
        (binding?.mobileType as? AutoCompleteTextView)?.setAdapter(
            mobileAdapter
        )
        binding?.mobileType?.addTextChangedListener(object : TextWatcher {
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
                    Timber.i("SELECTED = ${mobileTypeList[mobileTypeList.indexOf(s.toString())]}")
                    mobileType = s.toString()
                } catch (e: Exception) {
                }
            }
        })


        val documentAdapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            documentTypeList
        )
        (binding?.documentType as? AutoCompleteTextView)?.setAdapter(
            documentAdapter
        )
        binding?.documentType?.addTextChangedListener(object : TextWatcher {
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
                    Timber.i("SELECTED = ${documentTypeList[documentTypeList.indexOf(s.toString())]}")
                    documentType = s.toString()
                } catch (e: Exception) {
                }
            }
        })

        lifecycleScope.launch {
            viewModel.registerResponse.collect {
                when (it) {
                    is UiState.Success<*> -> {
                        stopLoad()
                        Toast.makeText(this@DataCollectionActivity, "Household Created", Toast.LENGTH_SHORT).show()
//                        Snackbar.make(binding.root, "Household Created", Snackbar.LENGTH_SHORT)
//                            .show()
                        finish()
                    }
                    is UiState.Failed -> {
                        Toast.makeText(this@DataCollectionActivity, it.message.toString(), Toast.LENGTH_SHORT).show()
//                        Snackbar.make(binding.root, it.message.toString(), Snackbar.LENGTH_SHORT)
//                            .show()
                        stopLoad()
                    }
                    is UiState.Loading -> {
                        startLoad()
                    }

                }
            }
        }
        findViewById<Button>(R.id.submit).setOnClickListener {
            if (!binding?.address?.text.isNullOrEmpty() && !binding?.email?.text.isNullOrEmpty()
                && !binding?.mobile?.text.isNullOrEmpty() && !binding?.name?.text.isNullOrEmpty() && villageName != ""
                && !binding?.landmark?.text.isNullOrEmpty() && !binding?.whatsapp?.text.isNullOrEmpty() && !binding?.ward?.text.isNullOrEmpty() && villageId != 0
                && fileUri!=null && mobileType!= "" && documentType!="" && !binding?.documentNumber?.text.isNullOrEmpty()
            ) {
                val a = CancellationTokenSource().token
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
                    val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    ActivityCompat.requestPermissions(this,permission,0)
                }
                fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    a
                ).addOnSuccessListener {
                    val file = File("${fileUri?.path}")
                    val encodedImage: String = Base64.encodeToString(method(file), Base64.DEFAULT)
                    Log.d("asdf", "onCreate: $encodedImage")
                    val token = AuthConfigManager.getAuthToken()
                    viewModel.registerHousehold(
                        villageId,
                        villageName,
                        binding?.name?.text.toString(),
                        binding?.mobile?.text.toString(),
                        binding?.whatsapp?.text.toString(),
                        binding?.email?.text.toString(),
                        binding?.address?.text.toString(),
                        binding?.father?.text.toString(),
                        binding?.mother?.text.toString(),
                        binding?.ward?.text.toString(),
                        binding?.landmark?.text.toString(),
                        token!!,
                        it.latitude.toString(),
                        it.longitude.toString(),
                        encodedImage,
                        ".jpg",
                        mobileType,
                        documentType,
                        binding?.documentNumber?.text.toString()
                    )

                }
                    .addOnFailureListener {
                    Toast.makeText(this, "Error Occurred"+it.message, Toast.LENGTH_SHORT).show()
//                    Snackbar.make(binding.root, "Error Occurred", Snackbar.LENGTH_LONG).show()
                }
            }
            else {
                Toast.makeText(this, "Enter the correct values", Toast.LENGTH_SHORT).show()
//                Snackbar.make(binding.root, "Enter the correct values", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    fun startLoad() {
        binding?.progress?.visibility = View.VISIBLE
        binding?.submit?.isEnabled = false
        binding?.submit?.text = "Loading.."
    }

    fun stopLoad() {
        binding?.progress?.visibility = View.GONE
        binding?.submit?.isEnabled = true
        binding?.submit?.text = "Submit"
    }

    private fun setObserver() {
        val token = AuthConfigManager.getAuthToken()
        viewModel.getAllVillages(token!!)
    }

    @Throws(IOException::class)
    fun method(file: File): ByteArray? {
        return try {

            val fl = FileInputStream(file)
            val arr = ByteArray(file.length().toInt())
            fl.read(arr)
            fl.close()
            arr
        }catch (e:Exception){
            Timber.e("$e")
            null
        }
    }
}