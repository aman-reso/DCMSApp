package com.management.org.dcms.codes.dcmsclient.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.text.format.Formatter.formatIpAddress
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.management.org.dcms.codes.dcmsclient.Manager
import com.management.org.dcms.codes.dcmsclient.DcmsSplashActivity
import com.management.org.dcms.codes.dcmsclient.signup.SignupActivity
import com.management.org.dcms.codes.dcmsclient.data.models.LoginResponse
import com.management.org.dcms.codes.dcmsclient.util.UiState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.management.org.dcms.databinding.DcmsClientActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DcmsClientLoginActivity : AppCompatActivity() {
    private lateinit var binding: DcmsClientActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("HardwareIds", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DcmsClientActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.signup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {

                } else {

                Snackbar.make(binding.root,"This app needs your location to work properly",Snackbar.LENGTH_LONG).show()
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
        lifecycleScope.launchWhenResumed {
            viewModel.loginStatus.collect {
                when(it){
                    is UiState.Success<*>->{
                        binding.login.text ="Login"
                        binding.login.isEnabled = false
                        binding.progress.visibility = View.GONE
                        it.data as LoginResponse
                        Timber.e("${it.data.token}")
                        Manager(application).token = it.data.token
                        if(!it.data.token.isNullOrEmpty()){
                            Timber.e("test")
                            Manager(application).isLoggedIn = true
                        }
                        startActivity(Intent(this@DcmsClientLoginActivity, DcmsSplashActivity::class.java))
                        finish()


                    }
                    is UiState.Failed->{
                        binding.login.text ="Login"
                        binding.login.isEnabled = false
                        binding.progress.visibility = View.GONE
                        binding.userName.setText("")
                        binding.password.setText("")
                        Snackbar.make(binding.root,"${it.message}",Snackbar.LENGTH_SHORT).show()
                    }
                    is UiState.Loading->{
                        binding.login.text ="Loading.."
                        binding.login.isEnabled = false
                        binding.progress.visibility = View.VISIBLE
                    }
                }

            }
        }


        binding.login.setOnClickListener {
            if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
                val deviceId =
                    Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                val ipAddress: String = formatIpAddress(wifiManager.connectionInfo.ipAddress)
                if (!binding.userName.text.isNullOrEmpty() && !binding.password.text.isNullOrEmpty()) {
                    val a = CancellationTokenSource().token
                    fusedLocationClient.getCurrentLocation(
                        LocationRequest.PRIORITY_HIGH_ACCURACY,
                        a
                    )
                        .addOnSuccessListener {
                            viewModel.userLogin(
                                binding.userName.text.toString(),
                                binding.password.text.toString(),
                                deviceId,
                                ipAddress,
                                it.latitude.toString(),
                                it.longitude.toString()
                            )
                        }
                        .addOnFailureListener {
                            Timber.e("$it")
                        }

//                startActivity(Intent(this, MainActivity::class.java))
                }else{
                    Snackbar.make(binding.root,"Enter the values",Snackbar.LENGTH_SHORT).show()
                }
            }
            else{
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                Snackbar.make(binding.root,"This app needs your location to work properly",Snackbar.LENGTH_LONG).show()
            }
        }
    }
}