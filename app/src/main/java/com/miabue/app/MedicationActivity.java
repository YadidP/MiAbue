package com.miabue.app;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miabue.app.adapters.MedicationAdapter;
import com.miabue.app.database.DatabaseHelper;
import com.miabue.app.models.Treatment;

import java.util.Date;
import java.util.List;

public class MedicationActivity extends AppCompatActivity {

    private RecyclerView rvMedications;
    private LinearLayout layoutEmpty;
    private ImageView btnBack;
    private DatabaseHelper databaseHelper;
    private MedicationAdapter medicationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        initViews();
        initDatabase();
        setupClickListeners();
        loadMedications();
    }

    private void initViews() {
        rvMedications = findViewById(R.id.rv_medications);
        layoutEmpty = findViewById(R.id.layout_empty);
        btnBack = findViewById(R.id.btn_back);

        // Configurar RecyclerView
        rvMedications.setLayoutManager(new LinearLayoutManager(this));
        medicationAdapter = new MedicationAdapter(this, treatment -> {
            // Acción cuando se toma el medicamento
            takeMedication(treatment);
        });
        rvMedications.setAdapter(medicationAdapter);
    }

    private void initDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadMedications() {
        List<Treatment> treatments = databaseHelper.getAllActiveTreatments();

        if (treatments.isEmpty()) {
            rvMedications.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvMedications.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            medicationAdapter.updateTreatments(treatments);
        }
    }

    private void takeMedication(Treatment treatment) {
        // Actualizar última toma en la base de datos
        databaseHelper.updateLastTaken(treatment.getId(), new Date());

        // Mostrar mensaje de confirmación
        String message = "Medicamento tomado: " + treatment.getName();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // Recargar la lista para actualizar la información
        loadMedications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar medicamentos cuando se regrese a la actividad
        loadMedications();
    }
}