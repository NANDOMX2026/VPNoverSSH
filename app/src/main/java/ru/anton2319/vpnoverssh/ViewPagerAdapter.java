package ru.anton2319.vpnoverssh;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.nandomx.v5.fragments.InicioFragment;
import com.nandomx.v5.fragments.PerfilFragment;
import com.nandomx.v5.fragments.SoporteFragment;
public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fa){ super(fa); }
    @NonNull @Override public Fragment createFragment(int pos){
        if(pos==1) return new PerfilFragment();
        if(pos==2) return new SoporteFragment();
        return new InicioFragment();
    }
    @Override public int getItemCount(){ return 3; }
}
