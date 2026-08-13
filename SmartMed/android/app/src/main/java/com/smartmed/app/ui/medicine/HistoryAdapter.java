package com.smartmed.app.ui.medicine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.smartmed.app.R;
import com.smartmed.app.data.model.MedicationHistory;
import com.smartmed.app.utils.Constants;
import com.smartmed.app.utils.DateUtils;
import java.util.List;

/** RecyclerView adapter for medication history. */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<MedicationHistory> items;
    public HistoryAdapter(List<MedicationHistory> items) { this.items = items; }
    public void updateList(List<MedicationHistory> newList) { this.items = newList; notifyDataSetChanged(); }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        MedicationHistory item = items.get(position);
        h.tvName.setText(item.getMedicineName());
        h.tvDosage.setText(item.getMedicineDosage());
        h.tvScheduledTime.setText(DateUtils.formatTimeString(item.getScheduledTime()));

        String status = item.getStatus();
        h.tvStatus.setText(status);
        switch (status) {
            case Constants.STATUS_TAKEN:
                h.tvStatus.setBackgroundResource(R.drawable.bg_status_taken);
                h.tvTakenTime.setText("Taken at " + DateUtils.formatTimeString(item.getTakenTime()));
                h.tvTakenTime.setVisibility(View.VISIBLE);
                break;
            case Constants.STATUS_MISSED:
                h.tvStatus.setBackgroundResource(R.drawable.bg_status_missed);
                h.tvTakenTime.setText("Not taken");
                h.tvTakenTime.setVisibility(View.VISIBLE);
                break;
            case Constants.STATUS_SNOOZED:
                h.tvStatus.setBackgroundResource(R.drawable.bg_status_snoozed);
                h.tvTakenTime.setVisibility(View.GONE);
                break;
            case Constants.STATUS_LATE:
                h.tvStatus.setBackgroundResource(R.drawable.bg_status_snoozed);
                h.tvTakenTime.setText("Taken late at " + DateUtils.formatTimeString(item.getTakenTime()));
                h.tvTakenTime.setVisibility(View.VISIBLE);
                break;
            default:
                h.tvStatus.setBackgroundResource(R.drawable.bg_status_chip);
                h.tvTakenTime.setVisibility(View.GONE);
        }
    }

    @Override public int getItemCount() { return items != null ? items.size() : 0; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDosage, tvScheduledTime, tvTakenTime, tvStatus;
        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvMedicineName);
            tvDosage = v.findViewById(R.id.tvDosage);
            tvScheduledTime = v.findViewById(R.id.tvScheduledTime);
            tvTakenTime = v.findViewById(R.id.tvTakenTime);
            tvStatus = v.findViewById(R.id.tvStatus);
        }
    }
}
