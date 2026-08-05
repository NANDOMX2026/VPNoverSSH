package ru.anton2319.vpnoverssh.services;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.DynamicPortForwarder;
import java.io.IOException;
import java.util.Optional;
import ru.anton2319.vpnoverssh.MainActivity;
import com.nandomx.v5.R;
import ru.anton2319.vpnoverssh.data.singleton.PortForward;

public class SshService extends Service {
    private static final String TAG="SshService";
    private static final String CH="NANDOMX_SSH_CHANNEL";
    Thread sshThread;
    Connection conn;
    DynamicPortForwarder forwarder;
    SharedPreferences sp;
    private PowerManager.WakeLock wl;
    private static final int MAX_RECONNECT=10;
    private static final int DELAY=3000;
    private void createChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel ch=new NotificationChannel(CH,"NANDOMX SSH",NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(ch);
        }
    }
    private Notification notif(String t){
        Intent mainIntent=new Intent(this,MainActivity.class);
        PendingIntent mainPI=PendingIntent.getActivity(this,0,mainIntent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Intent stopIntent=new Intent(this,SocksProxyService.class);
        stopIntent.setAction(SocksProxyService.ACTION_STOP);
        PendingIntent stopPI=PendingIntent.getService(this,1,stopIntent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        return new NotificationCompat.Builder(this,CH)
          .setContentTitle("NANDOMX SSH")
          .setContentText(t)
          .setSmallIcon(R.drawable.ic_notification)
          .setContentIntent(mainPI)
          .addAction(R.drawable.ic_notification,"Apagar VPN",stopPI)
          .setOngoing(true).build();
    }
    @Override public int onStartCommand(Intent i,int f,int s){
        createChannel();
        startForeground(2,notif("Tunel SSH activo"));
        PowerManager pm=(PowerManager)getSystemService(POWER_SERVICE);
        if(pm!=null){
            wl=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"NANDOMX:SshWakeLock");
            wl.acquire(86400000L);
        }
        sp=PreferenceManager.getDefaultSharedPreferences(this);
        Thread old=PortForward.getInstance().getSshThread();
        if(old!=null) old.interrupt();
        sshThread=newSshThread(i);
        PortForward.getInstance().setSshThread(sshThread);
        sshThread.start();
        return START_STICKY;
    }
    @Override public void onDestroy(){
        if(wl!=null&&wl.isHeld()) wl.release();
        if(conn!=null) conn.close();
        super.onDestroy();
    }
    @Override public IBinder onBind(Intent i){return null;}
    public void initiateSSH(Intent intent) throws IOException{
        String user=intent.getStringExtra("user");
        String host=intent.getStringExtra("hostname");
        String pass=intent.getStringExtra("password");
        int port=Integer.parseInt(Optional.ofNullable(intent.getStringExtra("port")).orElse("22"));
        String pk=intent.getStringExtra("privateKey");
        int attempts=MAX_RECONNECT;
        boolean ok=false;
        while(attempts>0 &&!Thread.currentThread().isInterrupted()){
            try{
                conn=new Connection(host,port);
                conn.connect();
                PortForward.getInstance().setConn(conn);
                boolean auth=false;
                if(pass==null&&pk==null) auth=conn.authenticateWithNone(user);
                if(pk!=null&&pass!=null) auth=conn.authenticateWithPublicKey(user,pk.toCharArray(),pass);
                else if(pk!=null) auth=conn.authenticateWithPublicKey(user,pk.toCharArray(),null);
                else if(pass!=null) auth=conn.authenticateWithPassword(user,pass);
                if(!auth) throw new IOException("Auth fail");
                int fwd=Integer.parseInt(Optional.ofNullable(sp.getString("forwarder_port","1080")).orElse("1080"));
                forwarder=conn.createDynamicPortForwarder(fwd);
                ok=true;
                NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                if(nm!=null) nm.notify(2,notif("Conectado - Auto-reconnect activo"));
                while(!Thread.currentThread().isInterrupted() && conn.isAuthenticationComplete()){
                    Thread.sleep(5000);
                    if(conn.getConnectionInfo()==null) throw new IOException("Lost");
                }
                break;
            }catch(Exception e){
                attempts--;
                if(attempts<=0) break;
                try{Thread.sleep(DELAY);}catch(InterruptedException ie){break;}
                NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                if(nm!=null) nm.notify(2,notif("Reconectando "+(MAX_RECONNECT-attempts+1)+"/"+MAX_RECONNECT));
            }
        }
        if(!ok) stopSelf();
    }
    private Thread newSshThread(Intent i){
        return new Thread(()->{try{initiateSSH(i);}catch(Exception e){}});
    }
}
