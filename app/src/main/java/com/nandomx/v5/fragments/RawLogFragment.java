package com.nandomx.v5.fragments;
import android.os.Bundle; import android.view.*;
import androidx.annotation.NonNull; import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.nandomx.v5.R;
public class RawLogFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle b){
        return i.inflate(R.layout.fragment_raw, c, false);
    }
}
