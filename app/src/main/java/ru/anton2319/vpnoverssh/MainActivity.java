
package ru.anton2319.vpnoverssh;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle b){
        super.onCreate(b);
        try {
            setContentView(R.layout.activity_main);
            // Si el layout falla, muestra texto
        } catch (Exception e) {
            TextView tv = new TextView(this);
            tv.setText("NANDOMX V5 - MainActivity OK\n\nError en layout: " + e.getMessage());
            tv.setPadding(50,300,50,50);
            setContentView(tv);
        }
    }
}
