package com.management.org.dcms.codes.dcmsclient

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.management.org.dcms.codes.dcmsclient.additem.AddItemActivity
import com.management.org.dcms.codes.dcmsclient.login.DcmsClientLoginActivity
import com.management.org.dcms.codes.dcmsclient.login.LoginViewModel
import com.management.org.dcms.codes.dcmsclient.util.UiState
import com.management.org.dcms.codes.dcmsclient.viewitem.ViewItemActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.management.org.dcms.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class DcmClientMainActivity : AppCompatActivity() {
    val viewModel by viewModels<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {

                } else {

                    Snackbar.make(findViewById(R.id.add_new_household),"This app needs your location to work properly",Snackbar.LENGTH_LONG).show()
                }
            }
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
        lifecycleScope.launch {
            viewModel.logoutStatus.collect {
                when(it){
                    is UiState.Success<*> ->{
                        Manager(application).isLoggedIn = false
                        Manager(application).token = ""
                        startActivity(Intent(this@DcmClientMainActivity, DcmsClientLoginActivity::class.java))
                        finish()
                        Timber.e("test")
                    }
                    is UiState.Failed ->{

                    }
                    is UiState.Loading->{

                    }

                }

            }
        }
        setContentView(R.layout.dcms_client_main_activity)
        findViewById<Button>(R.id.add_new_household).setOnClickListener {
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(this, AddItemActivity::class.java))
            }else{
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
        findViewById<Button>(R.id.view_added_details).setOnClickListener {
            startActivity(Intent(this, ViewItemActivity::class.java))
        }
        setSupportActionBar(findViewById<MaterialToolbar>(R.id.toolbar))


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.home_menu, menu)
        menu.findItem(R.id.logout)?.setOnMenuItemClickListener {
           viewModel.userLogout(Manager(application).token.toString())
            true
        }
        return true

    }


}