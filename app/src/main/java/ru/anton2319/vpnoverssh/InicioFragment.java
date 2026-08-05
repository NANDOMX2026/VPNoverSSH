package ru.anton2319.vpnoverssh;
import android.os.Bundle; import android.view.*; import android.widget.TextView;
import androidx.annotation.NonNull; import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import ru.anton2319.vpnoverssh.data.singleton.StatusInfo; import ru.anton2319.vpnoverssh.data.utils.SSHConnectionProfileManager;
import ru.anton2319.vpnoverssh.services.SshService; import android.content.Intent;
import java.util.Timer; import java.util.TimerTask;
public class InicioFragment extends Fragment {
    TextView tv_bytes; MaterialButton btn; Timer timer;
    @Override public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle b){
        View v = inf.inflate(R.layout.fragment_inicio, c, false);
        tv_bytes = v.findViewById(R.id.tv_bytes);
        btn = v.findViewById(R.id.btn_conectar_real);
        btn.setOnClickListener(view -> {
            if(StatusInfo.getInstance().isConnected()){
                requireActivity().stopService(new Intent(requireActivity(), SshService.class));
                btn.setIconResource(android.R.drawable.ic_media_pause);
            } else {
                SSHConnectionProfileManager mgr = new SSHConnectionProfileManager(requireContext());
                if(mgr.getProfiles().isEmpty()){
                    tv_bytes.setText("Crea un perfil primero -> swipe a Registros");
                    return;
                }
                // Usa el primer perfil
                Intent i = new Intent(requireActivity(), SshService.class);
                i.putExtra("uuid", mgr.getProfiles().get(0).getUuid().toString());
                requireActivity().startService(i);
            }
        });
        // Actualiza bytes cada segundo
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override public void run(){
                if(getActivity()==null) return;
                getActivity().runOnUiThread(() -> {
                    StatusInfo si = StatusInfo.getInstance();
                    if(si.isConnected()){
                        tv_bytes.setText(String.format("%.2f KB/s ↑ %.2f KB/s ↓", si.getUploadSpeed()/1024f, si.getDownloadSpeed()/1024f));
                        btn.setText("CONECTADO");
                    } else {
                        tv_bytes.setText(si.getStatusText().isEmpty()? "Desconectado - NANDOMX V5" : si.getStatusText());
                        btn.setText("CONECTAR");
                    }
                });
            }
        },0,1000);
        return v;
    }
    @Override public void onDestroyView(){ super.onDestroyView(); if(timer!=null) timer.cancel(); }
}
