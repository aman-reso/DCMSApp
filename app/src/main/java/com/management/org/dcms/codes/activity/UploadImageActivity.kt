package com.management.org.dcms.codes.activity

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.JsonObject
import com.management.org.dcms.R
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.UploadImageViewModel
import com.management.org.dcms.databinding.ActivityUploadImageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

const val Q_ID: String = "qId"

@AndroidEntryPoint
class UploadImageActivity : BaseActivity() {
    private var parentDataBinding: ActivityUploadImageBinding? = null
    private var selectImageBtn: View? = null
    private var selectedImagePreview: ImageView? = null
    private var fileUri: Uri? = null
    private var filePath: String? = ""

    private var hHid: String? = "-1"
    private var qId: String? = "-1"

    private val uploadImageViewModel: UploadImageViewModel? by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)
        parentDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_upload_image)
        setUpViews()
        getDataFromIntent()
        setUpClickListener()
        setUpObserver()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpViews() {
        parentDataBinding?.let {
            it.containerAppBar.appBarTitleTV.text = "Verification"
            it.containerAppBar.icNavBackIcon.showHideView(true)
            it.containerAppBar.icNavBackIcon.setOnClickListener {
                finish()
            }
            selectImageBtn = it.selectImageBtn
            selectedImagePreview = it.imageView
        }
    }

    private fun setUpClickListener() {
        selectImageBtn?.setOnClickListener {
            ImagePicker.with(this).compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080).cameraOnly().crop().createIntent {
                    startForProfileImageResult.launch(it)
                }
        }
        parentDataBinding?.uploadImageForVerificationBtn?.setOnClickListener {
            uploadImageToServer()
        }
    }

    private fun getDataFromIntent() {
        if (intent?.data != null) {
            if (intent.hasExtra(HH_ID)) {
                hHid = intent.getStringExtra(HH_ID)
            }
            if (intent.hasExtra(Q_ID)) {
                qId = intent.getStringExtra(Q_ID)
            }
        }
    }

    private fun uploadImageToServer() {
        if (filePath != null && filePath != "") {
            lifecycleScope.launch(Dispatchers.Main) {
                var body: MultipartBody.Part? = null
                if (filePath != null) {
                    if (!filePath.equals("", ignoreCase = true)) {
                        val file = File(filePath!!)
                        val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                        body = MultipartBody.Part.createFormData("image", file.name, reqFile)
                        uploadImageViewModel?.uploadImageToServer(imagePart = body)
                    }
                }
            }
        } else {
            Utility.showToastMessage(getString(R.string.please_select_a_image))
        }
    }

    @SuppressLint("MissingPermission")
    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), fun(result: ActivityResult) {
        val resultCode = result.resultCode
        val data = result.data
        when (resultCode) {
            RESULT_OK -> {
                fileUri = data?.data
                filePath = data?.data?.path
                selectedImagePreview?.setImageURI(fileUri)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    })

    private fun setUpObserver() {
        uploadImageViewModel?.liveDataForUploadImageResponse?.observe(this) { networkResponse ->
            if (networkResponse != null) {
                parseNetworkResponse(networkResponse)
            }
        }
    }

    private fun parseNetworkResponse(networkResponse: GlobalNetResponse<*>) {
        when (networkResponse) {
            is GlobalNetResponse.NetworkFailure -> {
                Utility.showToastMessage(networkResponse.error)
            }
            is GlobalNetResponse.Success -> {
                val successResponse: JsonObject? = networkResponse.value as JsonObject
                if (successResponse != null && successResponse.has("Message")) {
                    Utility.showToastMessage(successResponse["Message"].asString)
                    if (successResponse.has("Status")) {
                        if (successResponse.get("Status").asInt == 1) {
                            finish()
                        }
                    }
                }
            }
        }
    }
}