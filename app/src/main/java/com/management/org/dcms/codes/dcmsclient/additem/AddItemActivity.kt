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
import com.management.org.dcms.codes.dcmsclient.Manager
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
import com.management.org.dcms.databinding.DcmsClientActivityAddItemBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*


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
        Manager(application).token?.let { viewModel.getAllVillages(it) }
        val villageList = mutableListOf<String>()
        var villageName = ""
        var villageId = 0
        var wardJob: Job = Job()
        var mobileType = ""
        val mobileTypeList = listOf("Feature", "Smart")
        var documentType = ""
        val documentTypeList = listOf("AADHAAR", "VOTERID")
        val wardList = mutableListOf<String>()
        binding.toolbar.setNavigationOnClickListener {
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
                                    villageId =
                                        it.data.villages[villageList.indexOf(s.toString())].id
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
                            wardList.add(it.wardNo)
                        }
                        val wardAdapter = ArrayAdapter(
                            this@AddItemActivity,
                            R.layout.dcms_client_simple_spinner_item,
                            wardList
                        )
                        (binding.ward as? AutoCompleteTextView)?.setAdapter(
                            wardAdapter
                        )
                        binding.ward.addTextChangedListener(object : TextWatcher {
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
            if (!binding.address.text.isNullOrEmpty() && !binding.email.text.isNullOrEmpty()
                && !binding.mobile.text.isNullOrEmpty() && !binding.name.text.isNullOrEmpty() && villageName != ""
                && !binding.landmark.text.isNullOrEmpty() && !binding.whatsapp.text.isNullOrEmpty() && !binding.ward.text.isNullOrEmpty() && villageId != 0
                && fileUri!=null && mobileType!= "" && documentType!="" && !binding.documentNumber.text.isNullOrEmpty()
            ) {
                val a = CancellationTokenSource().token
                fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    a
                ).addOnSuccessListener {
                    val file = File("${fileUri?.path}")
                    val encodedImage: String = Base64.encodeToString(method(file),Base64.DEFAULT)
                        Manager(application).token?.let { it1 ->
                            viewModel.registerHousehold(
                                villageId,
                                villageName,
                                binding.name.text.toString(),
                                binding.mobile.text.toString(),
                                binding.whatsapp.text.toString(),
                                binding.email.text.toString(),
                                binding.address.text.toString(),
                                binding.father.text.toString(),
                                binding.mother.text.toString(),
                                binding.ward.text.toString(),
                                binding.landmark.text.toString(),
                                it1,
                                it.latitude.toString(),
                                it.longitude.toString(),
                                encodedImage,
                                ".jpg",
                                mobileType,
                                documentType,
                                binding.documentNumber.text.toString()

                            )
                        }

                }.addOnFailureListener {
                    Snackbar.make(binding.root,"${it.message}",Snackbar.LENGTH_SHORT).show()
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
        }catch (e:Exception){
            Timber.e("$e")
            null
        }


    }
}