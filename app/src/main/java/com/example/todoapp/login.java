package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;  // Make sure this import is present


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {
    EditText adminUsername, adminPassword;
    Button adminLogin;
    final String userName="admin";
    final String passWord="123";

    final String userNameworker="user";
    final String passWordworker="123";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar); // This should find the Toolbar now
        setSupportActionBar(toolbar); // Use the Toolbar as ActionBar

        // Set title for ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Login");
        }

        // Initialize EditText and Button
        adminUsername = findViewById(R.id.etAdminUserName);
        adminPassword = findViewById(R.id.etAdminPassWord);
        adminLogin = findViewById(R.id.bAdminLogin);

        // Set the click listener for the login button
        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.equals(adminUsername.getText().toString()) && passWord.equals(adminPassword.getText().toString())) {
                    Toast.makeText(getBaseContext(), "Login Successful...", Toast.LENGTH_SHORT).show();
                    Intent startAdminDashboard = new Intent(login.this, MainActivitytodo.class);
                    startActivity(startAdminDashboard);
                } else if (userNameworker.equals(adminUsername.getText().toString()) && passWordworker.equals(adminPassword.getText().toString())) {
                    Toast.makeText(getBaseContext(), "Login Successful...", Toast.LENGTH_SHORT).show();
                    Intent startAdminDashboard = new Intent(login.this, MainActivity.class);
                    startActivity(startAdminDashboard);
                }else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(login.this);
                    alertDialog.setTitle("!!!  Login Failed  !!!");
                    alertDialog.setMessage("Wrong Username or Password \n!!!  Try Again  !!!").show();
                }
            }
        });
    }
}