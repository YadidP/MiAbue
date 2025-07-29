package com.miabue.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.miabue.app.adapters.MedicationAdapter;
import com.miabue.app.database.DatabaseHelper;
import com.miabue.app.models.Treatment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final String EMERGENCY_PHONE = "911"; // Número de emergencia

    private TextView tvWelcome, tvDailyInfo;
    private MaterialButton btnRegisterTreatment, btnMyMedications, btnEmergency;
    private RecyclerView rvUpcomingMedications;
    private DatabaseHelper databaseHelper;
    private MedicationAdapter medicationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initDatabase();
        setupClickListeners();
        requestPermissions();
        updateDailyInfo();
        loadUpcomingMedications();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvDailyInfo = findViewById(R.id.tv_daily_info);
        btnRegisterTreatment = findViewById(R.id.btn_register_treatment);
        btnMyMedications = findViewById(R.id.btn_my_medications);
        btnEmergency = findViewById(R.id.btn_emergency);
        rvUpcomingMedications = findViewById(R.id.rv_upcoming_medications);

        // Configurar RecyclerView
        rvUpcomingMedications.setLayoutManager(new LinearLayoutManager(this));
        medicationAdapter = new MedicationAdapter(this, treatment -> {
            // Acción cuando se toma el medicamento
            takeMedication(treatment);
        });
        rvUpcomingMedications.setAdapter(medicationAdapter);
    }

    private void initDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void setupClickListeners() {
        btnRegisterTreatment.setOnClickListener(v -> {
            Intent intent = new Intent(this, TreatmentActivity.class);
            startActivity(intent);
        });

        btnMyMedications.setOnClickListener(v -> {
            Intent intent = new Intent(this, MedicationActivity.class);
            startActivity(intent);
        });

        btnEmergency.setOnClickListener(v -> {
            activateEmergency();
        });
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.POST_NOTIFICATIONS
        };

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
                break;
            }
        }
    }

    private void updateDailyInfo() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd 'de' MMMM", new Locale("es", "ES"));
        String currentDate = dateFormat.format(calendar.getTime());

        // Personalizar saludo según la hora
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12) {
            greeting = "¡Buenos días!";
        } else if (hour < 18) {
            greeting = "¡Buenas tardes!";
        } else {
            greeting = "¡Buenas noches!";
        }

        tvWelcome.setText(greeting + " ¿Cómo se encuentra hoy?");

        // Mostrar información del día
        List<Treatment> treatments = databaseHelper.getAllActiveTreatments();
        StringBuilder dailyInfo = new StringBuilder();
        dailyInfo.append("Hoy es ").append(currentDate).append("\n\n");

        if (treatments.isEmpty()) {
            dailyInfo.append("No tiene medicamentos registrados.\n");
            dailyInfo.append("Registre sus tratamientos para recibir recordatorios.");
        } else {
            dailyInfo.append("Tiene ").append(treatments.size()).append(" medicamento(s) registrado(s).\n");
            dailyInfo.append("Recuerde tomar sus medicamentos a tiempo.\n");
            dailyInfo.append("¡Que tenga un excelente día!");
        }

        tvDailyInfo.setText(dailyInfo.toString());
    }

    private void loadUpcomingMedications() {
        List<Treatment> treatments = databaseHelper.getAllActiveTreatments();
        medicationAdapter.updateTreatments(treatments);
    }

    private void takeMedication(Treatment treatment) {
        // Actualizar última toma
        databaseHelper.updateLastTaken(treatment.getId(), new Date());

        Toast.makeText(this, "Medicamento tomado: " + treatment.getName(), Toast.LENGTH_SHORT).show();

        // Recargar lista
        loadUpcomingMedications();
    }

    private void activateEmergency() {
        Toast.makeText(this, getString(R.string.emergency_activated), Toast.LENGTH_SHORT).show();

        // Verificar permisos de llamada
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {

            // Realizar llamada de emergencia
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + EMERGENCY_PHONE));
            startActivity(callIntent);

        } else {
            // Si no hay permisos, mostrar dialer
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + EMERGENCY_PHONE));
            startActivity(dialIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar información cuando se regrese a la actividad
        updateDailyInfo();
        loadUpcomingMedications();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (!allPermissionsGranted) {
                Toast.makeText(this, "Algunos permisos son necesarios para el funcionamiento completo de la app",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
