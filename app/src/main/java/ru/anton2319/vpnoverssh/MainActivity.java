package ru.anton2319.vpnoverssh;
import android.os.Bundle;
import android.util.Log;
import java.io.*;
import com.nandomx.v5.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b){
        // ATRAPA TODO EL CRASH Y LO GUARDA EN /sdcard/crash_nandomx.txt
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            try {
                File f = new File("/sdcard/crash_nandomx.txt");
                PrintWriter pw = new PrintWriter(f);
                pw.println("=== CRASH NANDOMX V5 ===");
                e.printStackTrace(pw);
                pw.close();
                Log.e("NANDOMX_CRASH", "Crash guardado", e);
            } catch (Exception ex) {}
            // Ahora sí lo dejamos crashear para verlo
            android.os.Process.killProcess(android.os.Process.myPid());
        });

        super.onCreate(b);
        setContentView(R.layout.activity_main);
        ViewPager2 vp = findViewById(R.id.viewPager);
        TabLayout tabs = findViewById(R.id.tab_dots);
        vp.setAdapter(new ViewPagerAdapter(this));
        new TabLayoutMediator(tabs, vp, (tab,pos)->{}).attach();
    }
}
