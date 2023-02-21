package com.compose.weather.view.login

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compose.weather.R
import com.compose.weather.common.empty
import com.compose.weather.common.mutableStateValue
import com.compose.weather.common.setMutableStateValue
import com.compose.weather.navigtion.Route
import com.compose.weather.view.BottomSheetLayout
import com.compose.weather.view.SpaceTop
import com.compose.weather.view.common.Loader
import com.compose.weather.viewmodel.LoginScreenViewModel

@Composable
fun ComponentLoginScreen(
    navigateToHome: (route: String) -> Unit,
    vm: LoginScreenViewModel = viewModel()
) {
    val loginState by vm.loginState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = loginState, block = {
        if (loginState == LoginState.Success) {
            Log.d("Login", "Login SUCCESS ##")
            navigateToHome.invoke(Route.Home.createRoute(vm.loginId.value))
        }
    })

    when (loginState) {
        LoginState.Default -> {
            BottomSheetLayout {
                Content(vm)
            }
        }
        is LoginState.Failure -> {
            val message = (loginState as LoginState.Failure).errorMessage
            BottomSheetLayout {
                Content(vm, message)
            }
        }
        LoginState.Loading -> {
            Loader()
        }
        LoginState.Success -> {
            Log.d("Login", "Login SUCCESS")
        }
    }

}

@Composable
private fun Content(vm: LoginScreenViewModel, error: String = String.empty()) {

    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpaceTop(20.dp)
        OutlinedTextField(
            label = { Text(stringResource(id = R.string.login_id)) },
            value = mutableStateValue(state = vm.loginId), onValueChange = {
                setMutableStateValue(state = vm.loginId, value = it)
            })
        SpaceTop(12.dp)
        OutlinedTextField(
            label = { Text(stringResource(id = R.string.password)) },
            value = mutableStateValue(state = vm.password), onValueChange = {
                setMutableStateValue(state = vm.password, value = it)
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        SpaceTop(12.dp)
        Button(onClick = {
            vm.login()
        }) {
            Text(text = stringResource(id = R.string.login))
        }
        if (error.isNotEmpty()) {
            Text(text = error)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ComponentLoginScreen({})
}