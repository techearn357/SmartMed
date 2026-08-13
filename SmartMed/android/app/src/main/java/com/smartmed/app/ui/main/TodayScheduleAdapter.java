package com.smartmed.app.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.smartmed.app.R;
import com.smartmed.app.data.api.ApiClient;
import com.smartmed.app.data.model.ApiResponse;
import com.smartmed.app.data.model.MedicationHistory;
import com.smartmed.app.data.model.Schedule;
import com.smartmed.app.utils.DateUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Adapter for displaying all today's upcoming medication schedules on the Home Dashboard.
 */
public class TodayScheduleAdapter extends RecyclerView.Adapter<TodayScheduleAdapter.ViewHolder> {

    private List<Schedule> schedules;
    private final OnScheduleTakenListener listener;

    public interface OnScheduleTakenListener {
        void onScheduleTaken();
    }

    public TodayScheduleAdapter(List<Schedule> schedules, OnScheduleTakenListener listener) {
        this.schedules = schedules;
        this.listener = listener;
    }

    public void updateList(List<Schedule> newList) {
        this.schedules = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Schedule schedule = schedules.get(position);

        holder.tvName.setText(schedule.getMedicineName());
        holder.tvDosage.setText(schedule.getMedicineDosage() != null ? schedule.getMedicineDosage() : "");

        String formattedTime = DateUtils.formatTimeString(schedule.getScheduledTime());
        if (schedule.isTomorrow()) {
            holder.tvFrequency.setText("⏰ Tomorrow at " + formattedTime);
            holder.tvStatus.setText("Tomorrow");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_snoozed);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.text_secondary));
            holder.tvStatus.setPadding(24, 12, 24, 12);
            holder.tvStatus.setOnClickListener(null);
            holder.card.setOnClickListener(null);
        } else {
            holder.tvFrequency.setText("⏰ " + formattedTime);
            holder.tvStatus.setText("Take Now");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_rounded_primary);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(android.R.color.white));
            holder.tvStatus.setPadding(32, 16, 32, 16);
            holder.tvStatus.setOnClickListener(v -> markTaken(holder, schedule));
            holder.card.setOnClickListener(v -> markTaken(holder, schedule));
        }

        int remaining = schedule.getRemainingTablets();

        if (remaining > 0) {
            holder.tvStock.setVisibility(View.VISIBLE);
            holder.tvStock.setText("📦 " + remaining + " tablets left");
            holder.tvStock.setTextColor(holder.itemView.getContext().getColor(R.color.text_secondary));
        } else {
            holder.tvStock.setVisibility(View.VISIBLE);
            holder.tvStock.setText("⚠️ Low / No stock left");
            holder.tvStock.setTextColor(holder.itemView.getContext().getColor(R.color.error));
        }

        holder.tvStatus.setText("Take Now");
        holder.tvStatus.setBackgroundResource(R.drawable.bg_rounded_primary);
        holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(android.R.color.white));
        holder.tvStatus.setPadding(32, 16, 32, 16);

        holder.tvStatus.setOnClickListener(v -> markTaken(holder, schedule));
        holder.card.setOnClickListener(v -> markTaken(holder, schedule));
    }

    private void markTaken(@NonNull ViewHolder holder, Schedule schedule) {
        holder.tvStatus.setEnabled(false);
        MedicationHistory history = new MedicationHistory();
        history.setMedicineId(schedule.getMedicineId() != null ? schedule.getMedicineId() : "med_general");
        history.setMedicineName(schedule.getMedicineName());
        history.setMedicineDosage(schedule.getMedicineDosage());
        history.setScheduledTime(schedule.getScheduledTime());
        history.setStatus("TAKEN");
        history.setDate(DateUtils.formatDateForApi(new java.util.Date()));
        history.setTakenTime(DateUtils.getCurrentTimeString());

        ApiClient.getApiService().createHistory(history).enqueue(new Callback<ApiResponse<MedicationHistory>>() {
            @Override
            public void onResponse(Call<ApiResponse<MedicationHistory>> call, Response<ApiResponse<MedicationHistory>> response) {
                holder.tvStatus.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    int remaining = schedule.getRemainingTablets() > 0 ? schedule.getRemainingTablets() - 1 : 0;
                    Toast.makeText(holder.itemView.getContext(),
                            "✅ " + schedule.getMedicineName() + " marked as Taken! (" + remaining + " tablets remaining)",
                            Toast.LENGTH_LONG).show();

                    if (listener != null) {
                        listener.onScheduleTaken();
                    }
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Failed to record history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MedicationHistory>> call, Throwable t) {
                holder.tvStatus.setEnabled(true);
                Toast.makeText(holder.itemView.getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return schedules != null ? schedules.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView tvName, tvDosage, tvFrequency, tvStock, tvStatus;

        ViewHolder(View v) {
            super(v);
            card = v.findViewById(R.id.cardMedicine);
            tvName = v.findViewById(R.id.tvMedicineName);
            tvDosage = v.findViewById(R.id.tvDosage);
            tvFrequency = v.findViewById(R.id.tvFrequency);
            tvStock = v.findViewById(R.id.tvStock);
            tvStatus = v.findViewById(R.id.tvStatus);
        }
    }
}
