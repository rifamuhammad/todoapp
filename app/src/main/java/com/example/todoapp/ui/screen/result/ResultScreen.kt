package com.kaushalvasava.org.apps.qrscanner.ui.screen.result

import com.example.todoapp.MainActivitytodo2

import android.text.util.Linkify
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.todoapp.R
import com.example.todoapp.copyTextToClipboard
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.*


import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(navController: NavController, result: String?) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopAppBar(title = { Text(stringResource(id = R.string.result)) }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        })
        Spacer(modifier = Modifier.height(16.dp))
        AndroidView(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp), factory = {
            val textView = TextView(context)
            textView.text = result ?: ""
            textView.textSize = 20.sp.value
//            textView.setTextColor(Color.Blue.hashCode())
            Linkify.addLinks(textView, Linkify.ALL)
            textView
        }) {

        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {

                val startAdminDashboard = Intent(context, MainActivitytodo2::class.java)
                context.startActivity(startAdminDashboard)

        }) {
            Text(text = stringResource(id = R.string.copy))
        }
    }
}

