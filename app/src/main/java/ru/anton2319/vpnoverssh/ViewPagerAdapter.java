package ru.anton2319.vpnoverssh;
import androidx.annotation.NonNull; 
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity; 
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.nandomx.v5.fragments.InicioFragment;
import com.nandomx.v5.fragments.RegistrosFragment;
import com.nandomx.v5.fragments.RawLogFragment;
public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fa){super(fa);}
    @NonNull @Override public Fragment createFragment(int p){ 
        if(p==0) return new InicioFragment(); 
        if(p==1) return new RegistrosFragment(); 
        return new RawLogFragment(); 
    }
    @Override public int getItemCount(){return 3;}
}
