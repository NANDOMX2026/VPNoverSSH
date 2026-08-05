package com.nandomx.v5.fragments;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
public class InicioFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b){
        TextView tv = new TextView(getContext());
        tv.setText("Inicio NANDOMX V5 - OK");
        tv.setTextColor(0xFFFFFFFF);
        tv.setTextSize(22);
        tv.setPadding(50,350,50,50);
        tv.setBackgroundColor(0xFF000000);
        return tv;
    }
}
