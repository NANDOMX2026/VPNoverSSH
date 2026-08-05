package com.nandomx.v5.fragments;
import android.os.Bundle; 
import android.view.*; 
import android.widget.TextView;
import androidx.annotation.NonNull; 
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import ru.anton2319.vpnoverssh.data.singleton.StatusInfo; 
import ru.anton2319.vpnoverssh.data.utils.SSHConnectionProfileManager;
import ru.anton2319.vpnoverssh.services.SshService; 
import android.content.Intent;
import java.util.Timer; 
import java.util.TimerTask;
public class InicioFragment extends Fragment {
    TextView tv_bytes; MaterialButton btn; Timer timer;
    @Override public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle b){
        View v = inf.inflate(com.nandomx.v5.R.layout.fragment_inicio, c, false);
        tv_bytes = v.findViewById(com.nandomx.v5.R.id.tv_bytes);
        btn = v.findViewById(com.nandomx.v5.R.id.btn_conectar_real);
        btn.setOnClickListener(view -> {
            if(StatusInfo.getInstance().isConnected()){
                requireActivity().stopService(new Intent(requireActivity(), SshService.class));
            } else {
                SSHConnectionProfileManager mgr = new SSHConnectionProfileManager(requireContext());
                if(mgr.loadProfiles().isEmpty()){
                    tv_bytes.setText("Crea un perfil primero");
                    return;
                }
                Intent i = new Intent(requireActivity(), SshService.class);
                i.putExtra("uuid", mgr.loadProfiles().get(0).uuid.toString());
                requireActivity().startService(i);
            }
        });
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override public void run(){
                if(getActivity()==null) return;
                getActivity().runOnUiThread(() -> {
                    StatusInfo si = StatusInfo.getInstance();
                    if(si.isConnected()){
                        tv_bytes.setText("Conectado " + si.getUploadSpeed()/1024 + " KB/s");
                    } else {
                        tv_bytes.setText("Desconectado - NANDOMX V5");
                    }
                });
            }
        },0,1000);
        return v;
    }
    @Override public void onDestroyView(){ super.onDestroyView(); if(timer!=null) timer.cancel(); }
}
