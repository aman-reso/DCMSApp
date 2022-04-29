package com.management.org.dcms.codes.extensions

import android.view.View

fun View.showHideView(canShow: Boolean) {
    visibility = if (canShow) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun View.enableDisableView(isEnable: Boolean) {
    isEnabled = isEnable
}