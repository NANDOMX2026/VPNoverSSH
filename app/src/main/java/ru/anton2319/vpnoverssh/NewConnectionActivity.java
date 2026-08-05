import com.nandomx.v5.R;
package ru.anton2319.vpnoverssh;
import android.app.Activity; import android.content.Intent; import android.net.Uri; import android.os.Bundle;
import android.view.View; import android.widget.*; import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton; import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.io.*; import java.util.UUID;
import ru.anton2319.vpnoverssh.data.SSHConnectionProfile; import ru.anton2319.vpnoverssh.data.utils.SSHConnectionProfileManager;
public class NewConnectionActivity extends AppCompatActivity {
    View passwordInputLayout; TextInputEditText serverAddressInput, serverPortInput, usernameInput, passwordInput, bannerInput;
    View addKeyButton, keyCard; FloatingActionButton saveButton;
    SSHConnectionProfile.AuthenticationType authenticationType = SSHConnectionProfile.AuthenticationType.PASSWORD;
    String privateKey; TextView keyInfoTextView; private static final int READ_REQUEST_CODE = 42;
    SSHConnectionProfile sshConnectionProfile = new SSHConnectionProfile();
    SSHConnectionProfileManager sshConnectionProfileManager = new SSHConnectionProfileManager(this); UUID profile_uuid;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getStringExtra("uuid") != null) profile_uuid = UUID.fromString(getIntent().getStringExtra("uuid"));
        setContentView(R.layout.activity_new_connection);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        serverAddressInput = findViewById(R.id.serverAddressInput); serverPortInput = findViewById(R.id.serverPortInput);
        usernameInput = findViewById(R.id.usernameInput); passwordInput = findViewById(R.id.passwordInput);
        bannerInput = findViewById(R.id.bannerInput); passwordInputLayout = findViewById(R.id.passwordInputLayout);
        addKeyButton = findViewById(R.id.addKeyButton); addKeyButton.setVisibility(View.GONE);
        keyCard = findViewById(R.id.keyCard); saveButton = findViewById(R.id.floatingActionButton);
        AutoCompleteTextView authenticationTypeDropdown = findViewById(R.id.authenticationTypeInput);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.authentication_type_array, android.R.layout.simple_dropdown_item_1line);
        authenticationTypeDropdown.setAdapter(adapter);
        if(profile_uuid != null) {
            sshConnectionProfile = sshConnectionProfileManager.loadProfileByUUID(profile_uuid);
            serverAddressInput.setText(sshConnectionProfile.getServerIP()); serverPortInput.setText(String.valueOf(sshConnectionProfile.getServerPort()));
            usernameInput.setText(sshConnectionProfile.getUsername()); passwordInput.setText(sshConnectionProfile.getPassword());
            bannerInput.setText(sshConnectionProfile.getBannerNotas());
            if(sshConnectionProfile.getAuthenticationType() == SSHConnectionProfile.AuthenticationType.PRIVATE_KEY) {
                authenticationType = SSHConnectionProfile.AuthenticationType.PRIVATE_KEY; passwordInputLayout.setVisibility(View.GONE);
                addKeyButton.setVisibility(View.VISIBLE); privateKey = sshConnectionProfile.getPrivateKey();
                authenticationTypeDropdown.setText("Private Key", false); updateKeyCardData();
            } else authenticationTypeDropdown.setText("Password", false);
        }
        saveButton.setOnClickListener(v -> {
            sshConnectionProfile.setAuthenticationType(authenticationType);
            sshConnectionProfile.setServerIP(((TextView) serverAddressInput).getText().toString());
            sshConnectionProfile.setServerPort(Integer.parseInt(((TextView) serverPortInput).getText().toString()));
            sshConnectionProfile.setUsername(((TextView) usernameInput).getText().toString());
            sshConnectionProfile.setBannerNotas(((TextView) bannerInput).getText().toString());
            if(authenticationType == SSHConnectionProfile.AuthenticationType.PASSWORD) sshConnectionProfile.setPassword(((TextView) passwordInput).getText().toString());
            else if(authenticationType == SSHConnectionProfile.AuthenticationType.PRIVATE_KEY) sshConnectionProfile.setPrivateKey(privateKey);
            sshConnectionProfileManager.saveProfile(sshConnectionProfile); finish();
        });
        addKeyButton.setOnClickListener(v -> pickFile());
        authenticationTypeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            if(position == 0){ authenticationType = SSHConnectionProfile.AuthenticationType.PASSWORD; passwordInputLayout.setVisibility(View.VISIBLE); addKeyButton.setVisibility(View.GONE); keyCard.setVisibility(View.GONE); }
            else { authenticationType = SSHConnectionProfile.AuthenticationType.PRIVATE_KEY; passwordInputLayout.setVisibility(View.GONE); addKeyButton.setVisibility(View.VISIBLE); }
        });
    }
    public void pickFile(){ Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); intent.addCategory(Intent.CATEGORY_OPENABLE); intent.setType("*/*"); startActivityForResult(intent, READ_REQUEST_CODE); }
    private String readTextFromUri(Uri uri){ StringBuilder sb = new StringBuilder(); try{ InputStream is = getContentResolver().openInputStream(uri); BufferedReader r = new BufferedReader(new InputStreamReader(is)); String line; while((line=r.readLine())!=null) sb.append(line).append("\n"); is.close(); }catch(Exception e){ e.printStackTrace(); } return sb.toString(); }
    @Override public void onActivityResult(int requestCode, int resultCode, Intent resultData){ super.onActivityResult(requestCode, resultCode, resultData); if(requestCode==READ_REQUEST_CODE && resultCode==Activity.RESULT_OK && resultData!=null){ privateKey = readTextFromUri(resultData.getData()); updateKeyCardData(); } }
    @Override public boolean onSupportNavigateUp(){ onBackPressed(); return super.onSupportNavigateUp(); }
    private void updateKeyCardData(){ String s = privateKey.replace("-----BEGIN OPENSSH PRIVATE KEY-----","").replace("-----END OPENSSH PRIVATE KEY-----","").replace("-----BEGIN PRIVATE KEY-----","").replace("-----END PRIVATE KEY-----",""); keyInfoTextView = findViewById(R.id.keyInfoTextView); if(s.length()>25) keyInfoTextView.setText(s.substring(0,25)+"•••••\n"); if(privateKey.length()>55) keyInfoTextView.setText(keyInfoTextView.getText()+"•••••"+s.substring(s.length()-26, s.length()-1)); keyCard.setVisibility(View.VISIBLE); findViewById(R.id.deleteKeyButton).setOnClickListener(v -> { keyInfoTextView.setText(""); privateKey=null; keyCard.setVisibility(View.GONE); }); }
}
