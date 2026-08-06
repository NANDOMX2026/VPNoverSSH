package com.nandomx.v5.fragments;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class InicioFragment extends Fragment {
    @Override 
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle b){
        TextView tv = new TextView(getContext());
        tv.setText("NANDOMX V5 - INICIO OK\n\nYa entraste, bro. El fix funciono.\nAhora solo falta reconectar el boton.");
        tv.setTextSize(18);
        tv.setPadding(50,200,50,50);
        return tv;
    }
}
