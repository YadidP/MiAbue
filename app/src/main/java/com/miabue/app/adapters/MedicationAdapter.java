package com.miabue.app.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.miabue.app.R;
import com.miabue.app.models.Treatment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private Context context;
    private List<Treatment> treatments;
    private OnMedicationTakenListener listener;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public interface OnMedicationTakenListener {
        void onMedicationTaken(Treatment treatment);
    }

    public MedicationAdapter(Context context, OnMedicationTakenListener listener) {
        this.context = context;
        this.treatments = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medication, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Treatment treatment = treatments.get(position);

        holder.tvMedicationName.setText(treatment.getName());
        holder.tvMedicationDose.setText("Dosis: " + treatment.getDose());

        // Calcular próxima dosis
        String nextDoseTime = calculateNextDose(treatment);
        holder.tvNextDose.setText("Próxima dosis: " + nextDoseTime);

        holder.btnTakeMedication.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMedicationTaken(treatment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return treatments.size();
    }

    public void updateTreatments(List<Treatment> newTreatments) {
        this.treatments.clear();
        this.treatments.addAll(newTreatments);
        notifyDataSetChanged();
    }

    private String calculateNextDose(Treatment treatment) {
        try {
            Date startTime = timeFormat.parse(treatment.getStartTime());
            Calendar calendar = Calendar.getInstance();
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startTime);

            // Establecer la hora de inicio para hoy
            calendar.set(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, startCalendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Date now = new Date();
            Date lastTaken = treatment.getLastTaken();

            // Si nunca se ha tomado, la próxima dosis es a la hora programada
            if (lastTaken == null) {
                if (calendar.getTime().before(now)) {
                    // Si la hora ya pasó hoy, programar para mañana
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                return timeFormat.format(calendar.getTime());
            }

            // Calcular próxima dosis basada en la última toma
            Calendar lastTakenCalendar = Calendar.getInstance();
            lastTakenCalendar.setTime(lastTaken);
            lastTakenCalendar.add(Calendar.HOUR_OF_DAY, treatment.getFrequencyHours());

            return timeFormat.format(lastTakenCalendar.getTime());

        } catch (ParseException e) {
            return "Error al calcular";
        }
    }

    static class MedicationViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicationName, tvMedicationDose, tvNextDose;
        MaterialButton btnTakeMedication;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicationName = itemView.findViewById(R.id.tv_medication_name);
            tvMedicationDose = itemView.findViewById(R.id.tv_medication_dose);
            tvNextDose = itemView.findViewById(R.id.tv_next_dose);
            btnTakeMedication = itemView.findViewById(R.id.btn_take_medication);
        }
    }
}