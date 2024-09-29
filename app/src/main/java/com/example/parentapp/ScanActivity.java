package com.example.parentapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.Manifest;


import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ScanActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;

    public DrawerLayout drawerLayout;
    public ImageView btnMenu;
    private FirebaseFirestore db;
    private EditText studentNumberInput;
    private Button markButton;
    private TextView Scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan);

        Scan = findViewById(R.id.scan_qr_code);

        db = FirebaseFirestore.getInstance();
        drawerLayout = findViewById(R.id.main);
        btnMenu = findViewById(R.id.Menu);
        markButton = findViewById(R.id.mark);
        studentNumberInput = findViewById(R.id.student_number_input);

        Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermissionAndStartScanner();
            }
        });
    }
    private void checkCameraPermissionAndStartScanner() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, start QR code scanner
            startQRCodeScanner();
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }
    }



    private void startQRCodeScanner() {
        Intent intent = new Intent(ScanActivity.this, CaptureActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start QR code scanner
                startQRCodeScanner();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            String scannedData = data.getStringExtra("SCAN_RESULT"); // Retrieve scanned QR data
            markAttendance(scannedData);
        }
    }

    private void markAttendance(String scannedData) {
        String studentId = studentNumberInput.getText().toString().trim();

        // Validate student ID
        if (TextUtils.isEmpty(studentId)) {
            Toast.makeText(this, "Please enter your student number.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map for the attendance data
        Map<String, Object> attendanceData = new HashMap<>();
        attendanceData.put("studentId", studentId);
        attendanceData.put("timestamp", System.currentTimeMillis());
        attendanceData.put("status", "present");


        db.collection("students")
                .add(attendanceData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Attendance marked successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error marking attendance: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}