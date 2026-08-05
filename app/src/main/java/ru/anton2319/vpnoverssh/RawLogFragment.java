package ru.anton2319.vpnoverssh;
import android.os.Bundle; import android.view.LayoutInflater; import android.view.View; import android.view.ViewGroup; import android.widget.TextView; import androidx.annotation.NonNull; import androidx.annotation.Nullable; import androidx.fragment.app.Fragment;
public class RawLogFragment extends Fragment {
 @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
  TextView tv = new TextView(getContext()); tv.setText("LOG NANDOMX"); tv.setTextSize(16); tv.setTextColor(0xFFFFFFFF); tv.setPadding(30,150,30,30); return tv;
 }
}
