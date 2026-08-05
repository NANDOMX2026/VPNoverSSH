package ru.anton2319.vpnoverssh;
import com.nandomx.v5.R;
import android.os.Bundle; import android.view.*; import android.widget.TextView;
import androidx.annotation.NonNull; import androidx.fragment.app.Fragment;
import ru.anton2319.vpnoverssh.data.singleton.StatusInfo;
import java.util.Timer; import java.util.TimerTask;
public class RawLogFragment extends Fragment {
    TextView log; Timer timer;
    @Override public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle b){
        View v = inf.inflate(R.layout.fragment_raw, c, false);
        log = v.findViewById(R.id.rawLogText);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override public void run(){
                if(getActivity()==null) return;
                getActivity().runOnUiThread(() -> log.setText(StatusInfo.getInstance().getRawLog()));
            }
        },0,1000);
        return v;
    }
    @Override public void onDestroyView(){ super.onDestroyView(); if(timer!=null) timer.cancel(); }
}
