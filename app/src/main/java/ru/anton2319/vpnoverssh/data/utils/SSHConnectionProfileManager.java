package ru.anton2319.vpnoverssh.data.utils;
import android.content.Context; import android.util.Log;
import com.google.gson.Gson; import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap; import com.google.gson.reflect.TypeToken;
import java.io.*; import java.util.*;
import ru.anton2319.vpnoverssh.data.SSHConnectionProfile;
public class SSHConnectionProfileManager {
    private static final String TAG = "SSHProfileManager"; private static final String FILE_NAME = "ssh_profiles.json";
    private Context context; public SSHConnectionProfileManager(Context context){ this.context = context; }
    public void saveProfiles(List<SSHConnectionProfile> profiles){
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); String json = gson.toJson(profiles);
        try{ FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE); fos.write(json.getBytes()); fos.close(); }catch(IOException e){ Log.e(TAG, e.getMessage()); }
    }
    public void saveProfile(SSHConnectionProfile profile){
        List<LinkedTreeMap> existing = loadProfilesAsLinkedTreeMap(); boolean exists = false;
        for(int i=0;i<existing.size();i++){ if(existing.get(i).get("uuid").equals(profile.uuid.toString())){ existing.set(i, new Gson().fromJson(new Gson().toJson(profile), LinkedTreeMap.class)); exists=true; break; } }
        if(!exists){ existing.add(new Gson().fromJson(new Gson().toJson(profile), LinkedTreeMap.class)); }
        saveProfiles(new Gson().fromJson(new Gson().toJson(existing), TypeToken.getParameterized(List.class, SSHConnectionProfile.class).getType()));
    }
    public SSHConnectionProfile loadProfileByUUID(UUID uuid){
        List<SSHConnectionProfile> profiles = new Gson().fromJson(new Gson().toJson(loadProfiles()), TypeToken.getParameterized(List.class, SSHConnectionProfile.class).getType());
        for(SSHConnectionProfile p:profiles){ if(p.uuid.equals(uuid)) return p; } return null;
    }
    public List<SSHConnectionProfile> loadProfiles(){
        List<SSHConnectionProfile> profiles = new ArrayList<>();
        try{ FileInputStream fis = context.openFileInput(FILE_NAME); BufferedReader br = new BufferedReader(new InputStreamReader(fis)); StringBuilder sb = new StringBuilder(); String line; while((line=br.readLine())!=null) sb.append(line); profiles = new Gson().fromJson(sb.toString(), TypeToken.getParameterized(List.class, SSHConnectionProfile.class).getType()); }catch(IOException e){ Log.e(TAG, e.getMessage()); } return profiles;
    }
    public List<LinkedTreeMap> loadProfilesAsLinkedTreeMap(){
        List<SSHConnectionProfile> profiles = new ArrayList<>();
        try{ FileInputStream fis = context.openFileInput(FILE_NAME); BufferedReader br = new BufferedReader(new InputStreamReader(fis)); StringBuilder sb = new StringBuilder(); String line; while((line=br.readLine())!=null) sb.append(line); profiles = new Gson().fromJson(sb.toString(), TypeToken.getParameterized(List.class, SSHConnectionProfile.class).getType()); }catch(IOException e){ Log.e(TAG, e.getMessage()); }
        return new Gson().fromJson(new Gson().toJson(profiles), TypeToken.getParameterized(List.class, LinkedTreeMap.class).getType());
    }
    // NUEVO NANDOMX V5 - EXPORTAR .NMX ENCRIPTADO
    public boolean exportarNMX(SSHConnectionProfile perfil, File destino){
        try{ String json = new Gson().toJson(perfil); String enc = AESUtil.encrypt(json); FileWriter fw = new FileWriter(destino); fw.write(enc); fw.close(); return true; }catch(Exception e){ return false; }
    }
    public SSHConnectionProfile importarNMX(File origen){
        try{ BufferedReader br = new BufferedReader(new FileReader(origen)); StringBuilder sb = new StringBuilder(); String line; while((line=br.readLine())!=null) sb.append(line); br.close(); String json = AESUtil.decrypt(sb.toString()); return new Gson().fromJson(json, SSHConnectionProfile.class); }catch(Exception e){ return null; }
    }
}
