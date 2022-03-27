package com.management.org.dcms.codes.utility
import com.management.org.dcms.codes.DcmsApplication
import android.annotation.SuppressLint
import android.os.Build
import android.provider.Settings
import android.provider.Settings.Secure.getString
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*


object AndroidDeviceUtils {

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        var deviceId: String = UNDEFINED
        try {
            deviceId = getString(DcmsApplication.getDcmsAppContext()?.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (ex: Exception) {
        }
        return deviceId;
    }

    fun getAndroidVersion(): String {
        var androidVersion: String = UNDEFINED
        try {
            androidVersion = Build.VERSION.SDK_INT.toString()
        } catch (ex: Exception) {

        }
        return androidVersion;
    }

    fun getLocalIpAddress(): String? {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddress = intf.inetAddresses
                while (enumIpAddress.hasMoreElements()) {
                    val inetAddress = enumIpAddress.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        if (inetAddress.address == null) {
                            return UNDEFINED
                        }
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return UNDEFINED
    }
}

const val UNDEFINED = "com.management.org.dcms.codes.utility.UNDEFINED"