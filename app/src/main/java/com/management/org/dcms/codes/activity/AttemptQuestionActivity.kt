package com.management.org.dcms.codes.activity

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.management.org.dcms.R
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.utility.LanguageManager
import com.management.org.dcms.codes.utility.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

const val CAMP_ID: String = "camp_id"
const val THEME_ID: String = "theme_id"
const val SG_ID: String = "sg_id"
const val HH_ID: String = "hh_id"
const val URL_TO_BE_LOAD = "url_to_be_load"

//http://dcms.dmi.ac.in/Questionairs/index?campid=1&themeid=1&sgid=1&hhid=1&qtemplateid=1&lat=1&lng=1
class AttemptQuestionActivity : BaseActivity() {
    private var mWebView: WebView? = null
    private var url: String? = "http://dcms.dmi.ac.in/questionairs/index?campid=1&themeid=1&sgid=2"
    private var hHid: String? = "-1"
    private var progressBar: ProgressBar? = null
    private var progressbarContainer: View? = null
    private var qId: String? = "-1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attempt_question)
        mWebView = findViewById(R.id.webView)
        getDataFromIntent()
        setUpViews()
        hideShowPgContainer(true)
        lifecycleScope?.launch(Dispatchers.Main) {
            mWebView?.webChromeClient = webChromeClient
            mWebView?.webViewClient = webViewClient
            mWebView?.clearCache(true);
            mWebView?.clearHistory();
            val webSettings: WebSettings? = mWebView?.settings
            webSettings?.apply {
                domStorageEnabled = true
                javaScriptEnabled = true
            }
            delay(1000)
            url?.let { mWebView?.loadUrl(it) }
        }
        // mWebView?.
    }

    private fun getDataFromIntent() {
        try {
            if (intent != null && intent.extras != null) {
                url = intent.getStringExtra(URL_TO_BE_LOAD)
                if (intent.hasExtra(HH_ID)) {
                    hHid = intent.getStringExtra(HH_ID)
                }
                if (intent.hasExtra(Q_ID)) {
                    qId = intent.getStringExtra(Q_ID)
                }
            } else {
                Utility.showToastMessage(LanguageManager.getStringInfo(R.string.something_went_wrong))
                finish()
            }
        } catch (e: Exception) {
            Utility.showToastMessage(LanguageManager.getStringInfo(R.string.something_went_wrong))

            finish()
        }
    }

    private fun setUpViews() {
        progressBar = findViewById(R.id.progressBar)
        progressbarContainer = findViewById(R.id.containerProgressBar)
    }

    private fun hideShowPgContainer(boolean: Boolean) {
        progressbarContainer?.showHideView(boolean)
        progressBar?.showHideView(boolean)
        mWebView?.showHideView(!boolean)
    }

    private fun setProgressForProgressBar(int: Int) {
        progressBar?.progress = int

    }

    var webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView, progress: Int) {
            setProgressForProgressBar(progress)
            if (progress == 100) {
                hideShowPgContainer(false)
            } else {
                hideShowPgContainer(true)
            }
        }
    }

    private var webViewClient = object : WebViewClient() {
        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            Toast.makeText(this@AttemptQuestionActivity, description, Toast.LENGTH_SHORT).show()
        }

        @TargetApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView, req: WebResourceRequest, rerr: WebResourceError) {
            // Redirect to deprecated method, so you can use it in all SDK versions
            onReceivedError(view, rerr.errorCode, rerr.description.toString(), req.url.toString())
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            System.out.println("url-->${url}")
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            System.out.println("override-->${url}")
            if (url?.contains("success", ignoreCase = true) == true) {
                clearWebView()
                startImageVerificationActivity()
            } else {
                if (url != null) {
                    mWebView?.loadUrl(url);
                }
            }
            return true
        }

    }

    fun startImageVerificationActivity() {
        val intent = Intent(this, UploadImageActivity::class.java)
        intent.putExtra(HH_ID, hHid)
        intent.putExtra(Q_ID,qId)
        startActivity(intent)
        finish()
    }

    private fun clearWebView() {
        mWebView?.showHideView(false)
        if (Build.VERSION.SDK_INT < 18) {
            mWebView?.clearView();
        } else {
            mWebView?.loadUrl("about:blank");
        }
        mWebView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        clearWebView()
        try {
            System.gc()
        } catch (e: Exception) {

        }
    }

}