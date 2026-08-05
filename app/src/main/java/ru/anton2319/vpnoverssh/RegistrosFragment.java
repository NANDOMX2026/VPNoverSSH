package ru.anton2319.vpnoverssh;
import android.content.Intent; import android.os.Bundle; import android.view.*;
import android.widget.*; import androidx.annotation.NonNull; import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import ru.anton2319.vpnoverssh.data.SSHConnectionProfile; import ru.anton2319.vpnoverssh.data.singleton.StatusInfo;
import ru.anton2319.vpnoverssh.data.utils.SSHConnectionProfileManager;
import java.util.Timer; import java.util.TimerTask;
public class RegistrosFragment extends Fragment {
    ListView list; TextView tvTiempo; Timer timer; SSHConnectionProfileManager mgr;
    @Override public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle b){
        View v = inf.inflate(R.layout.fragment_registros, c, false);
        list = v.findViewById(R.id.profilesList);
        tvTiempo = v.findViewById(R.id.tv_tiempo);
        FloatingActionButton fab = v.findViewById(R.id.addBtn);
        fab.setOnClickListener(view -> startActivity(new Intent(requireContext(), NewConnectionActivity.class)));
        v.findViewById(R.id.btn_importar_nmx).setOnClickListener(view -> Toast.makeText(requireContext(),"Importar.NMX - NANDOMX V5",Toast.LENGTH_SHORT).show());
        v.findViewById(R.id.btn_exportar_nmx).setOnClickListener(view -> Toast.makeText(requireContext(),"Exportar.NMX - Encriptado AES",Toast.LENGTH_SHORT).show());
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override public void run(){
                if(getActivity()==null) return;
                getActivity().runOnUiThread(() -> {
                    long sec = StatusInfo.getInstance().getConnectedSeconds();
                    tvTiempo.setText(String.format("Tiempo: %02d:%02d:%02d", sec/3600, (sec%3600)/60, sec%60));
                });
            }
        },0,1000);
        return v;
    }
    @Override public void onResume(){ super.onResume(); refresh(); }
    @Override public void onDestroyView(){ super.onDestroyView(); if(timer!=null) timer.cancel(); }
    void refresh(){
        mgr = new SSHConnectionProfileManager(requireContext());
        java.util.List<SSHConnectionProfile> profiles = mgr.getProfiles();
        String[] names = new String[profiles.size()];
        for(int i=0;i<profiles.size();i++) names[i]=profiles.get(i).getServerIP()+" - "+profiles.get(i).getUsername()+"\nBanner: "+profiles.get(i).getBannerNotas();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, names);
        list.setAdapter(adapter);
        list.setOnItemClickListener((p,view,pos,id)->{
            Intent i = new Intent(requireContext(), NewConnectionActivity.class);
            i.putExtra("uuid", profiles.get(pos).getUuid().toString());
            startActivity(i);
        });
    }
}
