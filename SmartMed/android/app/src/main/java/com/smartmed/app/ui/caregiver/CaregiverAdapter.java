package com.smartmed.app.ui.caregiver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.smartmed.app.R;
import com.smartmed.app.data.model.Caregiver;
import java.util.List;

/** Adapter for displaying caregiver items. */
public class CaregiverAdapter extends RecyclerView.Adapter<CaregiverAdapter.ViewHolder> {
    private List<Caregiver> caregivers;
    private final OnCaregiverClickListener listener;

    public interface OnCaregiverClickListener { void onDeleteClick(Caregiver caregiver); }

    public CaregiverAdapter(List<Caregiver> caregivers, OnCaregiverClickListener listener) {
        this.caregivers = caregivers;
        this.listener = listener;
    }

    public void updateList(List<Caregiver> newList) {
        this.caregivers = newList;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_caregiver, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Caregiver c = caregivers.get(position);
        h.tvName.setText(c.getName());
        h.tvRelationship.setText(c.getRelationship());
        h.tvContact.setText(c.getEmail() != null && !c.getEmail().isEmpty() ? c.getEmail() : c.getPhone());
        h.btnDelete.setOnClickListener(v -> { if (listener != null) listener.onDeleteClick(c); });
    }

    @Override public int getItemCount() { return caregivers != null ? caregivers.size() : 0; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRelationship, tvContact;
        ImageButton btnDelete;
        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvRelationship = v.findViewById(R.id.tvRelationship);
            tvContact = v.findViewById(R.id.tvContact);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}
