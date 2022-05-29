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
import com.management.org.dcms.codes.dcmsclient.util.UiState
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.management.org.dcms.R
import com.management.org.dcms.codes.activity.LocationValue
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.utility.NetworkImpl
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.databinding.DcmsClientActivityAddItemBinding
import dagger.hilt.android.AndroidEntryPoint
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

    var villageName = ""
    var villageId = 0
    var mobileType = ""
    var documentType = ""

    val mobileTypeList = listOf("Feature Phone", "Smart Phone")
    val documentTypeList = listOf("AADHAAR", "VOTERID")

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
                        val stateAdapter = ArrayAdapter(this@AddItemActivity, R.layout.dcms_client_simple_spinner_item, villageList)
                        (binding.villageSpinner as? AutoCompleteTextView)?.setAdapter(
                            stateAdapter
                        )
                        binding.villageSpinner.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {}

                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    Timber.i("SELECTED = ${mobileTypeList[mobileTypeList.indexOf(s.toString())]}")
                    mobileType = s.toString()
                } catch (e: Exception) {
                }
            }
        })


        val documentAdapter = ArrayAdapter(this@AddItemActivity, R.layout.dcms_client_simple_spinner_item, documentTypeList)
        (binding.documentType as? AutoCompleteTextView)?.setAdapter(documentAdapter)
        binding.documentType.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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
                        Utility.showToastMessage("Household Created SuccessFully")
                        Snackbar.make(binding.root, "Household Created", Snackbar.LENGTH_SHORT).show()
                        finish()
                    }
                    is UiState.Failed -> {
                        Snackbar.make(binding.root, it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                        Utility.showToastMessage(it.message.toString())
                        stopLoad()
                    }
                    is UiState.Loading -> {
                        startLoad()
                    }
                }
            }
        }
        findViewById<Button>(R.id.submit).setOnClickListener {
            if (NetworkImpl.checkNetworkStatus()) {
                if (validateInput()) {
                    if (fileUri != null) {
                        var emailId = ""
                        var whatsAppNum = ""

                        if (!binding.email.text.isNullOrEmpty()) {
                            emailId = binding.email.text.toString()
                        }
                        if (!binding.whatsapp.text.isNullOrEmpty()) {
                            if (binding.whatsapp.text?.length == 10) {
                                whatsAppNum = binding.whatsapp.text.toString()
                            }else{
                                Utility.showToastMessage("Enter correct whatsApp number")
                            }
                        }

                        val latitude: String = LocationValue.latitude
                        val longitude: String = LocationValue.longitude
                        val a = CancellationTokenSource().token
                        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, a).addOnSuccessListener {
                            val file = File("${fileUri?.path}")
                            val encodedImage: String = Base64.encodeToString(method(file), Base64.DEFAULT)
                            AuthConfigManager.getAuthToken().let { authToken ->
                                try {
                                    viewModel.registerHousehold(
                                        villageId, villageName, binding.name.text.toString(),
                                        binding.mobile.text.toString(), whatsAppNum, emailId, binding.address.text.toString(),
                                        binding.father.text.toString(), binding.mother.text.toString(), binding.ward.text.toString(),
                                        binding.landmark.text.toString(), authToken, latitude, longitude, encodedImage, ".jpg",
                                        mobileType, documentType, binding.documentNumber.text.toString()
                                    )
                                } catch (e:Exception) {

                                }

                            }

                        }.addOnFailureListener {
                            Snackbar.make(binding.root, "${it.message}", Snackbar.LENGTH_SHORT).show()
                        }
                    }else{
                        Utility.showToastMessage("Please select Image")
                    }
                }
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

    private fun validateInput(): Boolean {
        if (validateVillageName()) {
            if (validateWardNo()) {
                if (validateNameField()) {
                    if (validateMobileNum()) {
                        if (validateMobileTypeNum()) {
                            if (validateAddress()) {
                                if (validateLandmark()) {
                                    if (validateDocType()) {
                                        if (validateDocNum()) {
                                            if (validateFatherName()) {
                                                if (validateMotherName()) {
                                                    return true
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    //                && mobileType != "" && documentType != ""
    private fun validateFatherName(): Boolean {
        val fatherName: String? = binding.father.text?.toString()
        if (fatherName != null && fatherName!="") {
            return true
        }
        Utility.showToastMessage("Please enter father's name")
        return false
    }

    private fun validateVillageName(): Boolean {
        if (villageName != "" && villageId != 0) {
            return true
        }
        Utility.showToastMessage("Please select village")
        return false
    }

    private fun validateLandmark(): Boolean {
        val landMark: String? = binding.landmark.text?.toString()
        if (landMark != null && landMark!="") {
            return true
        }
        Utility.showToastMessage("Please enter Landmark")
        return false
    }

    private fun validateMotherName(): Boolean {
        val motherName: String? = binding.mother.text?.toString()
        if (motherName != null && motherName!="") {
            return true
        }
        Utility.showToastMessage("Please enter mother's name")
        return false
    }

    private fun validateDocNum(): Boolean {
        val docNum: String? = binding.documentNumber.text?.toString()
        if (docNum != null && docNum!="") {
            return true
        }
        Utility.showToastMessage("Please enter correct Document number")
        return false
    }

    private fun validateDocType(): Boolean {
        val docType: String? = binding.documentType.text?.toString()
        if (docType != null && docType != "") {
            return true
        }
        Utility.showToastMessage("Please enter correct document type")
        return false
    }

    private fun validateAddress(): Boolean {
        val address: String? = binding.address.text?.toString()
        if (address != null && address!="") {
            return true
        }
        Utility.showToastMessage("Please enter correct Address")
        return false
    }


    private fun validateMobileTypeNum(): Boolean {
        val mobType: String? = binding.mobileType.text?.toString()
        if (mobType != null && mobileType != "") {
            return true
        }
        Utility.showToastMessage("Please enter correct mobile type")
        return false
    }

    private fun validateMobileNum(): Boolean {
        val mobNo: String? = binding.mobile.text?.toString()
        if (mobNo != null && mobNo.length == 10) {
            return true
        }
        Utility.showToastMessage("Please enter correct mobile number")
        return false
    }

    private fun validateWAMobileNum(): Boolean {
        val waNo: String? = binding.whatsapp.text?.toString()
        if (waNo != null && waNo.length == 10) {
            return true
        }
        Utility.showToastMessage("Please enter correct whatsapp number number")
        return false
    }

    private fun validateWardNo(): Boolean {
        try {
            val wardNo: String? = binding.ward.text?.toString()?.trim()
            if (wardNo != null) {
                var intWardNo = wardNo.toInt()
                return if (intWardNo in 1..30) {
                    true
                } else {
                    Utility.showToastMessage("Please enter correct ward number")
                    false
                }
            } else {
                Utility.showToastMessage("Ward number is missing")
            }
        } catch (e: Exception) {
            Utility.showToastMessage("Please enter correct ward number")
            return false
        }
        return false
    }

    private fun validateNameField(): Boolean {
        val nameVale = binding.name.text?.toString()
        if (nameVale != null && nameVale.length > 3) {
            return true
        } else {
            Utility.showToastMessage("Please enter Name")
        }
        return false
    }
}