package com.miabue.app;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.miabue.app.database.DatabaseHelper;
import com.miabue.app.models.Treatment;
import com.miabue.app.utils.NotificationHelper;

import java.util.Calendar;
import java.util.Locale;

public class TreatmentActivity extends AppCompatActivity {

    private TextInputEditText etMedicationName, etDose, etFrequency, etStartTime;
    private MaterialButton btnSaveTreatment;
    private ImageView btnBack;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);

        initViews();
        initDatabase();
        setupClickListeners();
    }

    private void initViews() {
        etMedicationName = findViewById(R.id.et_medication_name);
        etDose = findViewById(R.id.et_dose);
        etFrequency = findViewById(R.id.et_frequency);
        etStartTime = findViewById(R.id.et_start_time);
        btnSaveTreatment = findViewById(R.id.btn_save_treatment);
        btnBack = findViewById(R.id.btn_back);
    }

    private void initDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        etStartTime.setOnClickListener(v -> showTimePickerDialog());

        btnSaveTreatment.setOnClickListener(v -> saveTreatment());
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    etStartTime.setText(time);
                },
                hour,
                minute,
                true // Formato 24 horas
        );

        timePickerDialog.show();
    }

    private void saveTreatment() {
        String medicationName = etMedicationName.getText().toString().trim();
        String dose = etDose.getText().toString().trim();
        String frequencyStr = etFrequency.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();

        // Validaciones
        if (TextUtils.isEmpty(medicationName)) {
            etMedicationName.setError(getString(R.string.enter_medication_name));
            etMedicationName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(dose)) {
            etDose.setError(getString(R.string.enter_dose));
            etDose.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(frequencyStr)) {
            etFrequency.setError("Ingrese la frecuencia en horas");
            etFrequency.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(startTime)) {
            etStartTime.setError("Seleccione la hora de inicio");
            etStartTime.requestFocus();
            return;
        }

        int frequency;
        try {
            frequency = Integer.parseInt(frequencyStr);
            if (frequency <= 0 || frequency > 24) {
                etFrequency.setError("La frecuencia debe ser entre 1 y 24 horas");
                etFrequency.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etFrequency.setError("Ingrese un número válido");
            etFrequency.requestFocus();
            return;
        }

        // Crear y guardar tratamiento
        Treatment treatment = new Treatment(medicationName, dose, frequency, startTime);
        long result = databaseHelper.insertTreatment(treatment);

        if (result != -1) {
            treatment.setId((int) result);

            // Programar notificaciones
            NotificationHelper.scheduleNotification(this, treatment);

            Toast.makeText(this, getString(R.string.treatment_saved_success), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, getString(R.string.error_saving_treatment), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        etMedicationName.setText("");
        etDose.setText("");
        etFrequency.setText("");
        etStartTime.setText("");
        etMedicationName.requestFocus();
    }
}
