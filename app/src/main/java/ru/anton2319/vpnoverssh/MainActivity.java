package ru.anton2319.vpnoverssh;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        TextView tv = new TextView(this);
        tv.setText("NANDOMX V5 - SI ABRE - FIX OK");
        tv.setTextSize(28);
        tv.setPadding(50,300,50,50);
        setContentView(tv);
    }
}
