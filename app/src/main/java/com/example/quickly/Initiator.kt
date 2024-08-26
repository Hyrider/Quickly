package com.example.quickly

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_WIFI_SETTINGS
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun Initiator(modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current

    val bottomSheetState = rememberModalBottomSheetState()
    var isOpened by remember {
        mutableStateOf(false)
    }


    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            Toast.makeText(context, "File Selected: $uri", Toast.LENGTH_LONG).show()
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {

        } else {
            Toast.makeText(context, "Permissions are required to proceed", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(250.dp))

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.share))

        LottieAnimation(
            composition,
            modifier = Modifier.size(150.dp),
            iterations = LottieConstants.IterateForever
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome to Quickly",
            fontSize = 28.sp,
            fontFamily = FontFamily.Serif,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {isOpened=true}) {
            Text(
                text = "Start sharing",
                fontSize = 20.sp,
                fontFamily = FontFamily.Serif,
                color = Color.White
            )
        }
        if(isOpened){
            ModalBottomSheet(onDismissRequest = { isOpened = false }, sheetState = bottomSheetState) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth() // Ensures the Box takes up the full width
                        .height(270.dp), // Sets the height of the Box
                    contentAlignment = Alignment.Center // Centers content within the Box
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val lightBlue = Color(0xFFADD8E6)
                        myButton(color = lightBlue, text = "Send", onClick = {
                            isOpened = false
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                askForPermission(context, requestPermissionLauncher, navController)
                            }
                        })
                    }
                }
            }

        }
        }
    }

fun openWiFiSettings(context: Context) {
    val openWiFiSettings= Intent(ACTION_WIFI_SETTINGS)
    context.startActivity(openWiFiSettings)
}

@Composable
fun myButton(color: Color, text: String,onClick:()->Unit) {
    Row {
        Box(
            modifier = Modifier
                .size(130.dp)
                .background(color = color, shape = CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center,

        ) {
            Text(text = text, color = Color.White)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun askForPermission(
    context: Context,
    requestPermissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    navController: NavHostController) {
    val permissionsNeeded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(

            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.NEARBY_WIFI_DEVICES,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,

            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE
        )
    } else {
        arrayOf(


        Manifest.permission.ACCESS_FINE_LOCATION,

        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,

        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.CHANGE_NETWORK_STATE
        )
    }

    val permissionsToRequest = permissionsNeeded.filter {
        ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
    }


    if (permissionsToRequest.isNotEmpty()) {
        requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())

    }else
    {
        Toast.makeText(context,"Permission already Granted!",Toast.LENGTH_SHORT).show()
        navController.navigate("home")

    }
}

@Composable
fun Nav(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination="initiator" ){
        composable("initiator"){ Initiator(navController = navController)}
        composable("home"){ Home(navController = navController)}
    }
}

