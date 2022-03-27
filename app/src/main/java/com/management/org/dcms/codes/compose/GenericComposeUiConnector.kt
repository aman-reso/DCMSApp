package com.management.org.dcms.codes.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

abstract class GenericComposeUiConnector {

    @Preview
    @Composable
    abstract fun GetComposePreview()

    @Composable
    abstract fun GetComposableView()
}