package ru.anton2319.vpnoverssh;
import android.os.Bundle; import android.view.LayoutInflater; import android.view.View; import android.view.ViewGroup; import android.widget.TextView; import androidx.annotation.NonNull; import androidx.annotation.Nullable; import androidx.fragment.app.Fragment;
public class RegistrosFragment extends Fragment {
 @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
  TextView tv = new TextView(getContext()); tv.setText("REGISTROS NANDOMX"); tv.setTextSize(20); tv.setTextColor(0xFFFFFFFF); tv.setPadding(50,200,50,50); return tv;
 }
}
