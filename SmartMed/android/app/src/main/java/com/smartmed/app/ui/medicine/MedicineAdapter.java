package com.smartmed.app.ui.medicine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.smartmed.app.R;
import com.smartmed.app.data.model.Medicine;
import com.smartmed.app.utils.SharedPrefManager;
import java.util.List;

/** RecyclerView adapter for medicine list. */
public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {
    private List<Medicine> medicines;
    private final OnMedicineClickListener listener;

    public interface OnMedicineClickListener { void onMedicineClick(Medicine medicine); }

    public MedicineAdapter(List<Medicine> medicines, OnMedicineClickListener listener) {
        this.medicines = medicines;
        this.listener = listener;
    }

    public void updateList(List<Medicine> newList) {
        this.medicines = newList;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicine med = medicines.get(position);
        holder.tvName.setText(med.getName());
        holder.tvDosage.setText(med.getFormattedDosage());
        holder.tvFrequency.setText(med.getFrequency() != null ? med.getFrequency().replace("_", " ") : "");

        // Stock info
        if (med.getRemainingTablets() > 0) {
            holder.tvStock.setVisibility(View.VISIBLE);
            holder.tvStock.setText(med.getRemainingTablets() + " tablets left");
            int threshold = SharedPrefManager.getInstance().getLowStockThreshold();
            if (med.isLowStock(threshold)) {
                holder.tvStock.setTextColor(holder.itemView.getContext().getColor(R.color.error));
            } else {
                holder.tvStock.setTextColor(holder.itemView.getContext().getColor(R.color.text_secondary));
            }
        } else {
            holder.tvStock.setVisibility(View.GONE);
        }

        // Status indicator
        holder.tvStatus.setText(med.isActive() ? "Active" : "Inactive");
        holder.tvStatus.setBackgroundResource(med.isActive() ? R.drawable.bg_status_taken : R.drawable.bg_status_missed);

        holder.card.setOnClickListener(v -> { if (listener != null) listener.onMedicineClick(med); });
    }

    @Override public int getItemCount() { return medicines != null ? medicines.size() : 0; }

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
