package com.example.checkattendance;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.checkattendance.Adapter.NotificationAdapter;
import com.example.checkattendance.Data.Connecting_MSSQL;
import com.example.checkattendance.Models.DetailMessage;
import com.example.checkattendance.SharedPreferences.MyPreferences;
import com.example.checkattendance.Singleton.StaffNote;
import com.example.checkattendance.databinding.ActivityStaffBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ManageStaff extends AppCompatActivity {
    private ActivityStaffBinding binding;
    private static Connection connection_staff;
    private String username = "";
    private String password = "";
    private Bundle bundle;
    MyPreferences myPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStaffBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        myPreferences = new MyPreferences(getApplicationContext());
        connection_staff = new Connecting_MSSQL(connection_staff).Connecting();
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        StaffNote.getInstance().setID(username);
        if (connection_staff != null) {
            bundle = new Bundle();
            try {
                Statement statement = connection_staff.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT Staff_ID , Name_of_staff ,Position , Gender , PhoneNumber , Email , ProvivedImage\n" +
                        "FROM STAFF\n" +
                        "WHERE Staff_ID =" + "'" + username + "'" + ";");
                while (resultSet.next()) {
                    bundle.putString("AccountName", resultSet.getString(1).trim());
                    bundle.putString("Password", password);
                    bundle.putString("Name", resultSet.getString(2).trim());
                    bundle.putString("Position", resultSet.getString(3).trim());
                    bundle.putString("Gender", resultSet.getString(4).trim());
                    bundle.putString("PhoneNumber", resultSet.getString(5).trim());
                    bundle.putString("Email", resultSet.getString(6).trim());
                    bundle.putString("Image", resultSet.getString(7).trim());
                }
                StaffNote.getInstance().setPosition(bundle.getString("Position"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
    }

    private ArrayList<String> parsedatetime(String noti) {
        String[] temp = noti.split("=");
        String time = temp[0];
        String date = temp[1].substring(0, 10);
        String message = temp[1].substring(10);
        ArrayList<String> results = new ArrayList<>();
        results.add(message);
        results.add(date);
        results.add(time);
        return results;
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.imageslidebar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawlayoutlayer.openDrawer(GravityCompat.START);

            }
        });
        binding.navigationview.setItemIconTintList(null);
        NavController navController = Navigation.findNavController(this, binding.navigationHostFragment.getId());
        NavigationUI.setupWithNavController(binding.navigationview, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                binding.textApp.setText(navDestination.getLabel());
            }
        });
        binding.navigationview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getTitle().toString().trim().equals("Profile")) {
                    navController.navigate(R.id.profile, bundle);
                } else if (item.getTitle().toString().equals("Attend Check")) {
                    navController.navigate(R.id.attendcheck);
                } else if (item.getTitle().toString().equals("Log Out")) {
                    Dialog dialog = new Dialog(ManageStaff.this);
                    dialog.setContentView(R.layout.activity_log_out);
                    Button btnYes = dialog.findViewById(R.id.btnCustomDialogYes);
                    Button btnNo = dialog.findViewById(R.id.btnCustomDialogNo);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Statement statement = connection_staff.createStatement();
                                statement.execute("UPDATE ACCOUNT\n" +
                                        "SET Status ='inactive'\n" +
                                        "WHERE Account_name =" + "'" + username + "'");
                                dialog.dismiss();
                                finish();
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                mAuth.signOut();
                                myPreferences.saveKeyCheck(false);
                                myPreferences.saveUsername("");
                                myPreferences.savePassword("");
                                myPreferences.savePosition("");
                                startActivity(new Intent(getApplicationContext(), Login.class));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else if (item.getTitle().toString().trim().equals("Notification")) {
                    Dialog dialog = new Dialog(ManageStaff.this);
                    dialog.setContentView(R.layout.activity_notification_dialog);
                    dialog.show();
                    ImageView btn_close = dialog.findViewById(R.id.imageclose);
                    //gọi database
                    DatabaseReference ref = null;//khai báo địa chỉ là null
                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://check-attendance-e80df-default-rtdb.asia-southeast1.firebasedatabase.app");
                    if (StaffNote.getInstance().getPosition().equals("Security")) {
                        ref = database.getReference("/Notification/Security");

                    } else if (StaffNote.getInstance().getPosition().equals("Waitress")) {
                        ref = database.getReference("/Notification/Waitress");

                    } else if (StaffNote.getInstance().getPosition().equals("Order")) {
                        ref = database.getReference("/Notification/Order");

                    } else if (StaffNote.getInstance().getPosition().equals("Bartender")) {
                        ref = database.getReference("/Notification/Bartender");
                    }
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() == null) {
                                TextView txt_error = dialog.findViewById(R.id.error);
                                txt_error.setVisibility(View.VISIBLE);
                                ListView list_notification = dialog.findViewById(R.id.list_notification);
                                list_notification.setVisibility(View.INVISIBLE);
                            } else {
                                ArrayList<DetailMessage> arrayList = new ArrayList<>();
                                ListView list_notification = dialog.findViewById(R.id.list_notification);
                                list_notification.setVisibility(View.VISIBLE);
                                TextView txt_error = dialog.findViewById(R.id.error);
                                txt_error.setVisibility(View.INVISIBLE);
                                Object b = snapshot.getValue(); // lấy giá trị message + datetime
                                Log.d("TAG", "Data: " + b);
                                String d = b.toString().replace("{", "").replace("}", "").replace(", ", "\n");
                                Log.d("TAG", "Data: " + d);
                                String[] e = d.split("\n");
                                arrayList.clear();
                                for (int i = 0; i < e.length; i++) {
                                    ArrayList<String> temp = parsedatetime(e[i]);
                                    arrayList.add(new DetailMessage(temp.get(0), temp.get(1), temp.get(2)));
                                }
                                NotificationAdapter notificationAdapter = new NotificationAdapter(arrayList);
                                notificationAdapter.notifyDataSetChanged();
                                list_notification.setAdapter(notificationAdapter);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("TAG", "Error: " + error);
                        }
                    });
                    btn_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                }
                return true;
            }
        });
    }
}