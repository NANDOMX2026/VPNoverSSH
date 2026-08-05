package ru.anton2319.vpnoverssh.data.singleton;
import android.content.Intent;
public class StatusInfo {
    private static volatile StatusInfo instance = null;
    private static final Object lock = new Object();
    private volatile boolean active = false;
    private Intent vpnIntent; private Intent sshIntent;
    private String statusText = "Desconectado - NANDOMX V5";
    private String rawLog = "RAW LOG VPN - NANDOMX V5\nEsperando conexion...\n";
    private long connectStartTime = 0;
    private long uploadSpeed = 0; private long downloadSpeed = 0;
    private StatusInfo(){ this.active = false; }
    public static StatusInfo getInstance(){
        if(instance==null){ synchronized(lock){ if(instance==null) instance=new StatusInfo(); } }
        return instance;
    }
    public synchronized boolean isActive(){ return active; }
    public synchronized void setActive(Boolean a){ active=a; if(a) connectStartTime=System.currentTimeMillis(); statusText=a?"Conectado - NANDOMX V5":"Desconectado - NANDOMX V5"; }
    public boolean isConnected(){ return isActive(); }
    public Intent getVpnIntent(){ return vpnIntent; } public void setVpnIntent(Intent i){ vpnIntent=i; }
    public Intent getSshIntent(){ return sshIntent; } public void setSshIntent(Intent i){ sshIntent=i; }
    public String getStatusText(){ return statusText; } public void setStatusText(String s){ statusText=s; rawLog+=s+"\n"; }
    public String getRawLog(){ return rawLog; } public void appendLog(String s){ rawLog+=s+"\n"; if(rawLog.length()>10000) rawLog=rawLog.substring(rawLog.length()-10000); }
    public long getConnectedSeconds(){ if(!active||connectStartTime==0) return 0; return (System.currentTimeMillis()-connectStartTime)/1000; }
    public long getUploadSpeed(){ return uploadSpeed; } public void setUploadSpeed(long s){ uploadSpeed=s; }
    public long getDownloadSpeed(){ return downloadSpeed; } public void setDownloadSpeed(long s){ downloadSpeed=s; }
}
