package ru.anton2319.vpnoverssh;
import android.os.Bundle; import android.view.*;
import androidx.annotation.*; import androidx.fragment.app.Fragment;
public class RegistrosFragment extends Fragment {
 @Nullable @Override public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b){
   return inf.inflate(R.layout.fragment_registros, c, false);
 }
}
