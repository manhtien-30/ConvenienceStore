package com.example.checkattendance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.checkattendance.Data.Connecting_MSSQL;
import com.example.checkattendance.Models.Account;
import com.example.checkattendance.SharedPreferences.MyPreferences;
import com.example.checkattendance.databinding.ActivityLoginBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Login extends AppCompatActivity {
    private ActivityLoginBinding binding;
    Account account;
    private static Connection connection;
    MyPreferences myPreferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        myPreferences = new MyPreferences(getApplicationContext());
        if (myPreferences.getKeyCheck() && !myPreferences.getPosition().equals("BOSS")) {
            Intent intent_boss = new Intent(getApplicationContext(), ManageStaff.class);
            intent_boss.putExtra("username", myPreferences.getUsername());
            intent_boss.putExtra("password", myPreferences.getPassword());
            startActivity(intent_boss);
            finish();
        }
        connection = new Connecting_MSSQL(connection).Connecting();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        login();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void FirebaseAuthentication(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e("FirebaseAuth", "Success");

                    } else {
                        // Sign-in failed, display a message to the user
                        String errorMessage = task.getException().getMessage();
                        Log.e("FirebaseAuth", errorMessage);
                    }
                });
    }

    protected void login() {
        binding.buttonLogin.setOnClickListener(v -> {
            if (checkLogin()) {
                String username = binding.loginUsername.getText().toString().trim();
                String password = binding.loginPassword.getText().toString().trim();
                if (connection != null) {
                    try {
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery("SELECT * FROM ACCOUNT;");
                        int check_key = 1;
                        while (resultSet.next()) {
                            account = new Account(resultSet.getString(1).trim(), resultSet.getString(2).trim(), resultSet.getString(3).trim());
                            check_key = account.AuthenticateLogin(username, password);
                            if (check_key == 2) {
                                Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                                break;
                            } else if (check_key == 3) {
                                Statement statement1 = connection.createStatement();
                                statement1.execute("UPDATE ACCOUNT\n" +
                                        "SET Status ='active'\n" +
                                        "WHERE Account_name =" + "'" + username + "'");
                                Statement statement2 = connection.createStatement();
                                ResultSet resultSet1 = statement2.executeQuery("select Email from [dbo].[STAFF] WHERE Staff_ID = " + "'" + username + "'");
                                String email = "";
                                while (resultSet1.next()) {
                                    email = resultSet1.getString(1).trim();
                                }
                                FirebaseAuthentication(email, password);
                                myPreferences.saveKeyCheck(true);
                                myPreferences.saveUsername(username);
                                myPreferences.savePassword(password);
                                myPreferences.savePosition(account.getPosition());
                                Intent intent = new Intent(getApplicationContext(), ManageStaff.class);
                                intent.putExtra("username", username);
                                intent.putExtra("password", password);
                                startActivity(intent);
                                finish();
                                break;
                            } else if (check_key == 4) {
                                Toast.makeText(getApplicationContext(), "Your Role is unsuitable with the App", Toast.LENGTH_SHORT).show();
                            }
                        }
                        resultSet.close();
                        statement.close();
                        if (check_key == 1) {
                            Toast.makeText(getApplicationContext(), "Wrong username", Toast.LENGTH_SHORT).show();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Connect makes error.Please hotline with the administrator!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected boolean checkLogin() {
        if (binding.loginUsername.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "You must enter your username!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.loginPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "You must enter your password!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}