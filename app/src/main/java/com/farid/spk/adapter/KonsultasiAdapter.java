package com.farid.spk.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.farid.spk.R;

import java.util.ArrayList;

import com.farid.spk.model.ModelKonsultasi;

public class KonsultasiAdapter extends RecyclerView.Adapter<KonsultasiAdapter.KonsultasiHolder> {

    int varGlobal = 0;
    private Context ctx;
    private ArrayList<ModelKonsultasi> modelKonsultasiArrayList;

    public KonsultasiAdapter(Context context, ArrayList<ModelKonsultasi> items) {
        this.ctx = context;
        this.modelKonsultasiArrayList = new ArrayList<>();
        this.modelKonsultasiArrayList.addAll(items);
    }

    @Override
    public KonsultasiAdapter.KonsultasiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_gejala, parent, false);
        return new KonsultasiAdapter.KonsultasiHolder(view);
    }

    @Override
    public void onBindViewHolder(KonsultasiHolder holder, final int position) {
        final ModelKonsultasi data = modelKonsultasiArrayList.get(position);

        holder.cbGejala.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton checkboxView, boolean isChecked) {
                ModelKonsultasi modelKonsultasi = (ModelKonsultasi) checkboxView.getTag();

                Log.d("Checkbox", "Gejala: " + modelKonsultasi.getStrGejala() + ", Checked: " + isChecked);

                if (isChecked) {
                    varGlobal++;
                } else if (!isChecked) {
                    varGlobal--;
                }

                if(varGlobal >= 70) {
                    Toast.makeText(ctx,"Maaf Maksimal 70 Pilihan Saja", Toast.LENGTH_LONG).show();
                    checkboxView.setChecked(false);
                    varGlobal--;
                } else {
                    modelKonsultasi.setSelected(isChecked);
                }
            }
        });

        holder.cbGejala.setText(data.getStrGejala());
        holder.cbGejala.setChecked(data.isSelected());
        holder.cbGejala.setTag(data);
    }

    @Override
    public int getItemCount() {
        return modelKonsultasiArrayList.size();
    }

    static class KonsultasiHolder extends RecyclerView.ViewHolder {
        CheckBox cbGejala;

        public KonsultasiHolder(View itemView) {
            super(itemView);
            cbGejala = itemView.findViewById(R.id.cbGejala);
        }
    }
}