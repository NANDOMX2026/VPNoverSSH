package ru.anton2319.vpnoverssh;
import android.os.Bundle;
import android.widget.Toast;
import java.io.*;
import com.nandomx.v5.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b){
        Thread.setDefaultUncaughtExceptionHandler((t, ex) -> {
            try {
                File dir = getExternalFilesDir(null);
                if(dir==null) dir = getFilesDir();
                File f = new File(dir, "crash_nandomx.txt");
                PrintWriter pw = new PrintWriter(new FileWriter(f));
                pw.println("=== CRASH NANDOMX V5 ===");
                pw.println(ex.toString());
                for(StackTraceElement el : ex.getStackTrace()) pw.println(" at " + el);
                Throwable cause = ex.getCause();
                if(cause!=null){ pw.println("Caused by: "+cause); for(StackTraceElement el : cause.getStackTrace()) pw.println(" at " + el); }
                pw.close();
            } catch (Exception e) { e.printStackTrace(); }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(2);
        });

        super.onCreate(b);
        try{
            setContentView(R.layout.activity_main);
            ViewPager2 vp = findViewById(R.id.viewPager);
            TabLayout tabs = findViewById(R.id.tab_dots);
            vp.setAdapter(new ViewPagerAdapter(this));
            new TabLayoutMediator(tabs, vp, (tab,pos)->{}).attach();
        } catch (Throwable e){
            Toast.makeText(this, "CRASH: " + e.toString(), Toast.LENGTH_LONG).show();
            throw e;
        }
    }
}
