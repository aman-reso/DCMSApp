package com.management.org.dcms.codes.compose.login_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.management.org.dcms.codes.compose.GenericComposeUiConnector

class LoginActivityMainUi : GenericComposeUiConnector() {

    @Composable
    @Preview
    override fun GetComposePreview() {

    }

    @Composable
    override fun GetComposableView() {

    }
}

@Preview(showBackground = true)
@Composable
fun GetLoginFormUi() {
    var mobileNumberInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        OutlinedTextField(
            value = mobileNumberInput,
            maxLines = 1,
            singleLine = true,
            onValueChange = { newInputString ->
                mobileNumberInput = newInputString
            },
            label = { Text("Phone Number")}
        )
        OutlinedTextField(
            value = passwordInput,
            onValueChange = { newPasswordString ->
                passwordInput = newPasswordString
            },
            maxLines = 1,
            singleLine = true,
            label = { Text("Password") }
        )

        Button(
            onClick = {},
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.Red),
            colors = ButtonDefaults.buttonColors(contentColor = Color.Red, backgroundColor = Color.White),
            modifier = Modifier.size(width = 200.dp,height = 60.dp)
        ) {
            Text(
                text = "Save", fontSize = 17.sp,
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 6.dp)
            )
        }
    }
}