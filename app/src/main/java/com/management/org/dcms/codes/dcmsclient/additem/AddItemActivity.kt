package com.management.org.dcms.codes.dcmsclient.additem

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.management.org.dcms.codes.dcmsclient.data.models.VillageResponse
import com.management.org.dcms.codes.dcmsclient.data.models.WardResponse
import com.management.org.dcms.codes.dcmsclient.util.UiState
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.management.org.dcms.R
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.databinding.DcmsClientActivityAddItemBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException


@AndroidEntryPoint
class AddItemActivity : AppCompatActivity() {
    val viewModel by viewModels<AddItemViewModel>()
    private lateinit var binding: DcmsClientActivityAddItemBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var fileUri: Uri? = null

    @SuppressLint("MissingPermission")
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                fileUri = data?.data!!
                binding.image.setImageURI(fileUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DcmsClientActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AuthConfigManager.getAuthToken().let { viewModel.getAllVillages(it) }
        val villageList = mutableListOf<String>()
        var villageName = ""
        var villageId = 0
        var mobileType = ""
        val mobileTypeList = listOf("Feature Phone", "Smart Phone")
        var documentType = ""
        val documentTypeList = listOf("AADHAAR", "VOTERID")
        binding.containerAppBar.icNavBackIcon.showHideView(true)
        binding.containerAppBar.appBarTitleTV.text = getString(R.string.add_household)
        binding.containerAppBar.icNavBackIcon.setOnClickListener {
            finish()
        }
        binding.imageSelector.setOnClickListener {
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
                        it.data.villages.forEach {
                            villageList.add(it.villageName)
                        }
                        Timber.e("$villageList")
                        val stateAdapter = ArrayAdapter(
                            this@AddItemActivity,
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
                                    villageName = s.toString()
                                    villageId = it.data.villages[villageList.indexOf(s.toString())].id
                                } catch (e: Exception) {
                                }
                            }
                        })

                    }

                    is UiState.Failed -> {
                        Snackbar.make(binding.root, it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                        stopLoad()
                    }
                    is UiState.Loading -> {
                        startLoad()
                    }

                }

            }
        }


        val mobileAdapter = ArrayAdapter(
            this@AddItemActivity,
            R.layout.dcms_client_simple_spinner_item,
            mobileTypeList
        )
        (binding.mobileType as? AutoCompleteTextView)?.setAdapter(
            mobileAdapter
        )
        binding.mobileType.addTextChangedListener(object : TextWatcher {
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
            this@AddItemActivity,
            R.layout.dcms_client_simple_spinner_item,
            documentTypeList
        )
        (binding.documentType as? AutoCompleteTextView)?.setAdapter(
            documentAdapter
        )
        binding.documentType.addTextChangedListener(object : TextWatcher {
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
                        Snackbar.make(binding.root, "Household Created", Snackbar.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                    is UiState.Failed -> {
                        Snackbar.make(binding.root, it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                        stopLoad()
                    }
                    is UiState.Loading -> {
                        startLoad()
                    }
                }
            }
        }
        findViewById<Button>(R.id.submit).setOnClickListener {
            if (!binding.address.text.isNullOrEmpty() && !binding.mobile.text.isNullOrEmpty() && !binding.name.text.isNullOrEmpty() && villageName != ""
                && !binding.landmark.text.isNullOrEmpty()  && !binding.ward.text.isNullOrEmpty() && villageId != 0
                && fileUri != null && mobileType != "" && documentType != "" && !binding.documentNumber.text.isNullOrEmpty()
            ) {
                var emailId = ""
                var whatsAppNum=""
                if (binding.email.text != null) {
                    emailId = binding.email.text.toString()
                }
                if (!binding.whatsapp.text.isNullOrEmpty()){
                    whatsAppNum=binding.whatsapp.text.toString()
                }

                if (binding.ward.text != null) {
                    try {
                        var wardNo = binding.ward.text?.toString()?.trim()?.toInt()
                        if (wardNo != null) {
                            if (wardNo in 1..30) {

                            }else{
                                Utility.showToastMessage("Please enter correct ward number")
                                return@setOnClickListener
                            }
                        }else{
                            Utility.showToastMessage("Please enter correct ward number")
                            return@setOnClickListener
                        }
                    } catch (e: Exception) {
                        Utility.showToastMessage("Please enter correct ward number")
                        return@setOnClickListener
                    }
                } else {
                    Utility.showToastMessage("Ward number is missing")
                    return@setOnClickListener
                }

                val a = CancellationTokenSource().token
                fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    a
                ).addOnSuccessListener {
                    val file = File("${fileUri?.path}")
                    val encodedImage: String = Base64.encodeToString(method(file), Base64.DEFAULT)
                    AuthConfigManager.getAuthToken()?.let { it1 ->
                        try {
                            viewModel.registerHousehold(
                                villageId,
                                villageName,
                                binding.name.text.toString(),
                                binding.mobile.text.toString(),
                                whatsAppNum,
                                emailId,
                                binding.address.text.toString(),
                                binding.father.text.toString(),
                                binding.mother.text.toString(),
                                binding.ward.text.toString(),
                                binding.landmark.text.toString(),
                                it1,
                                "123",
                                "123",
                                encodedImage,
                                ".jpg",
                                mobileType,
                                documentType,
                                binding.documentNumber.text.toString()
                            )
                        } catch (e: java.lang.Exception) {
                            System.out.println("exception-->" + e.localizedMessage)
                        }

                    }

                }.addOnFailureListener {
                    Snackbar.make(binding.root, "${it.message}", Snackbar.LENGTH_SHORT).show()
//                    Snackbar.make(binding.root, "Not able to Get your location", Snackbar.LENGTH_LONG).show()
                }
            } else {
                Snackbar.make(binding.root, "Enter the correct values", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    fun startLoad() {
        binding.progress.visibility = View.VISIBLE
        binding.submit.isEnabled = false
        binding.submit.text = "Loading.."
    }

    fun stopLoad() {
        binding.progress.visibility = View.GONE
        binding.submit.isEnabled = true
        binding.submit.text = "Submit"
    }

    @Throws(IOException::class)
    fun method(file: File): ByteArray? {
        return try {
            val fl = FileInputStream(file)
            val arr = ByteArray(file.length().toInt())
            fl.read(arr)
            fl.close()
            arr
        } catch (e: Exception) {
            Timber.e("$e")
            null
        }


    }
}