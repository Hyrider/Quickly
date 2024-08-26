package com.example.quickly

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.colorResource
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import java.io.File

@Composable
fun Home(navController: NavHostController) {
    AppList()
}

fun shareApkViaIntent(context: Context, packageInfo: PackageInfo) {
    try {
        val pm = context.packageManager
        val ai = pm.getApplicationInfo(packageInfo.packageName, 0)
        val apkFile = File(ai.sourceDir)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            apkFile
        )
   val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Share APK via"))
    } catch (e: Exception) {
        Toast.makeText(context, "Error sharing APK: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

fun getInstalledApps(packageManager: PackageManager): List<PackageInfo> {
    val allApps = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
    return allApps.filter { !isSystemPackage(it) }
}

fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
    return pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
}

@Composable
fun AppList() {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val apps = remember { getInstalledApps(packageManager) }
    Box (modifier = Modifier.background(Color.Black)){
        LazyColumn(modifier = Modifier.padding(5.dp)) {
            items(apps) { app ->
                AppItem(app, packageManager) {
                    shareApkViaIntent(context, app)

                }
            }
        }
    }
}
@Composable
fun AppItem(app: PackageInfo, packageManager: PackageManager, onAppClick: () -> Unit) {
    Spacer(modifier = Modifier.height(10.dp))
    val appName = remember(app) {
        try {
            app.applicationInfo.loadLabel(packageManager).toString()
        } catch (e: Exception) {
            "Unknown App"
        }
    }
    val itemBackground= colorResource(id = R.color.white)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .width(30.dp)
            .background(itemBackground, shape = RoundedCornerShape(12.dp))
    // Background with rounded corners
//            .shadow(8.dp, shape = RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onAppClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(app, packageManager)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = appName, fontSize = 16.sp)
        }

    }


}
@Composable
fun AppIcon(app: PackageInfo, packageManager: PackageManager) {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(app) {
        bitmap = try {
            val drawable = app.applicationInfo.loadIcon(packageManager)
            drawable.toBitmap().asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    bitmap?.let { imageBitmap ->
        Image(
            painter = BitmapPainter(imageBitmap),
            contentDescription = "App Icon",
            modifier = Modifier.size(48.dp)
        )
    } ?: Box(modifier = Modifier.size(48.dp))
}