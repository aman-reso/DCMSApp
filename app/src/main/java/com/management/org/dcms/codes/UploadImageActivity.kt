package com.management.org.dcms.codes

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.management.org.dcms.R
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.databinding.ActivityUploadImageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UploadImageActivity : AppCompatActivity() {
    private var parentDataBinding: ActivityUploadImageBinding? = null
    private var selectImageBtn: View? = null
    private var selectedImagePreview:ImageView?=null
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)
        parentDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_upload_image)
        setUpViews()
        setUpClickListener()
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
            selectedImagePreview=it.imageView
        }
    }

    private fun setUpClickListener() {
        selectImageBtn?.setOnClickListener {
            ImagePicker.with(this).compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080).cameraOnly().crop().createIntent {
                    startForProfileImageResult.launch(it)
                }
        }
    }

    @SuppressLint("MissingPermission")
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(), fun(result: ActivityResult) {
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                RESULT_OK -> {
                    fileUri = data?.data!!
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

}