package ru.anton2319.vpnoverssh;
import android.os.Bundle; import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2; import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
public class MainActivity extends AppCompatActivity {
    ViewPager2 vp; TabLayout tabs;
    @Override protected void onCreate(Bundle b){ super.onCreate(b); setContentView(R.layout.activity_main);
        vp=findViewById(R.id.viewPager); tabs=findViewById(R.id.tab_dots);
        vp.setAdapter(new ViewPagerAdapter(this));
        new TabLayoutMediator(tabs, vp, (tab,pos)->{}).attach();
    }
}
