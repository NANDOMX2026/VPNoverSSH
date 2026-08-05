package com.nandomx.v5.fragments;
import com.nandomx.v5.R;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
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
        View v = inf.inflate(R.layout.fragment_inicio, c, false);
        try {
            tv_bytes = v.findViewById(R.id.tv_bytes);
            btn = v.findViewById(R.id.btn_conectar_real);
            if(tv_bytes==null || btn==null) return v;
            
            btn.setOnClickListener(view -> {
                try {
                    StatusInfo si = StatusInfo.getInstance();
                    if(si!=null && si.isConnected()){
                        requireActivity().stopService(new Intent(requireActivity(), SshService.class));
                    } else {
                        SSHConnectionProfileManager mgr = new SSHConnectionProfileManager(requireContext());
                        if(mgr.loadProfiles().isEmpty()){
                            Toast.makeText(getContext(),"Ve a Registros y crea un perfil primero",Toast.LENGTH_LONG).show();
                            if(tv_bytes!=null) tv_bytes.setText("Crea perfil en Registros");
                            return;
                        }
                        Intent i = new Intent(requireActivity(), SshService.class);
                        i.putExtra("uuid", mgr.loadProfiles().get(0).uuid.toString());
                        requireActivity().startService(i);
                    }
                } catch (Exception e){ if(tv_bytes!=null) tv_bytes.setText("Error: "+e.getMessage()); }
            });
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask(){
                @Override public void run(){
                    if(getActivity()==null) return;
                    getActivity().runOnUiThread(() -> {
                        try{
                            StatusInfo si = StatusInfo.getInstance();
                            if(si!=null && tv_bytes!=null) tv_bytes.setText(si.isConnected() ? "CONECTADO - NANDOMX" : "Desconectado - NANDOMX V5");
                        }catch(Exception ignored){}
                    });
                }
            },0,1000);
        } catch(Exception e){ e.printStackTrace(); }
        return v;
    }
    @Override public void onDestroyView(){ super.onDestroyView(); if(timer!=null) timer.cancel(); }
}
