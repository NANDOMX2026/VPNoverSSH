package ru.anton2319.vpnoverssh.services;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.IpPrefix;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import ru.anton2319.vpnoverssh.MainActivity;
import ru.anton2319.vpnoverssh.R;
import ru.anton2319.vpnoverssh.data.singleton.PortForward;
import ru.anton2319.vpnoverssh.data.singleton.SocksPersistent;
import ru.anton2319.vpnoverssh.data.singleton.StatusInfo;

public class SocksProxyService extends VpnService {
    public static final String ACTION_STOP = "STOP_VPN";
    private static final String TAG = "SocksProxyService";
    private static final String CHANNEL_ID = "NANDOMX_VPN_CHANNEL";
    private static final int NOTIF_ID = 1;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Thread vpnThread;
    SharedPreferences sharedPreferences;
    private PowerManager.WakeLock wakeLock;
    private ConnectivityManager.NetworkCallback networkCallback;

    public Future<String> getDnsIp(SharedPreferences sp){
        return executor.submit(()->sp.getString("dns_resolver_ip","1.1.1.1"));
    }
    private Future<Set<String>> getSelectedApps(SharedPreferences sp){
        return executor.submit(()->sp.getStringSet("included_apps",new HashSet<>()));
    }
    private Future<Set<String>> getSelectedAppsFuture;
    Future<String> getDnsIpFuture;

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel ch=new NotificationChannel(CHANNEL_ID,"NANDOMX VPN",NotificationManager.IMPORTANCE_LOW);
            ch.setDescription("NANDOMX VPN 24/7");
            NotificationManager nm=getSystemService(NotificationManager.class);
            if(nm!=null) nm.createNotificationChannel(ch);
        }
    }
    private Notification buildNotification(String txt){
        Intent mainIntent=new Intent(this,MainActivity.class);
        PendingIntent mainPI=PendingIntent.getActivity(this,0,mainIntent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Intent stopIntent=new Intent(this,SocksProxyService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPI=PendingIntent.getService(this,1,stopIntent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        return new NotificationCompat.Builder(this,CHANNEL_ID)
          .setContentTitle("NANDOMX VPN")
          .setContentText(txt)
          .setSmallIcon(R.drawable.ic_notification)
          .setContentIntent(mainPI)
          .addAction(R.drawable.ic_notification,"Apagar VPN",stopPI)
          .setOngoing(true)
          .setPriority(NotificationCompat.PRIORITY_LOW)
          .build();
    }
    @Override public int onStartCommand(Intent intent,int flags,int startId){
        if(intent!=null && ACTION_STOP.equals(intent.getAction())){
            Log.d(TAG,"Apagar presionado");
            StatusInfo.getInstance().setActive(false);
            Thread sshThread=PortForward.getInstance().getSshThread();
            if(sshThread!=null) sshThread.interrupt();
            Intent sshIntent=StatusInfo.getInstance().getSshIntent();
            if(sshIntent!=null) stopService(sshIntent);
            stopSelf();
            return START_NOT_STICKY;
        }
        createNotificationChannel();
        startForeground(NOTIF_ID,buildNotification("Conectado - Toca abrir | Apagar para detener"));
        PowerManager pm=(PowerManager)getSystemService(POWER_SERVICE);
        if(pm!=null){
            wakeLock=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"NANDOMX:VpnWakeLock");
            wakeLock.acquire(24*60*60*1000L);
        }
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        getSelectedAppsFuture=getSelectedApps(sharedPreferences);
        getDnsIpFuture=getDnsIp(sharedPreferences);
        vpnThread=newVpnThread();
        SocksPersistent.getInstance().setVpnThread(vpnThread);
        vpnThread.start();
        registerNetworkCallback();
        return START_STICKY;
    }
    private void registerNetworkCallback(){
        try{
            ConnectivityManager cm=(ConnectivityManager)getSystemService(ConnectivityManager.class);
            if(cm==null) return;
            NetworkRequest req=new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            networkCallback=new ConnectivityManager.NetworkCallback(){
                @Override public void onAvailable(Network n){}
                @Override public void onLost(Network n){
                    NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    if(nm!=null) nm.notify(NOTIF_ID,buildNotification("Reconectando... senal perdida"));
                }
            };
            cm.registerNetworkCallback(req,networkCallback);
        }catch(Exception e){}
    }
    @Override public void onRevoke(){
        StatusInfo.getInstance().setActive(false);
        Thread sshThread=PortForward.getInstance().getSshThread();
        if(sshThread!=null) sshThread.interrupt();
        super.onRevoke();
    }
    @Override public void onDestroy(){
        if(networkCallback!=null){
            try{
                ConnectivityManager cm=(ConnectivityManager)getSystemService(ConnectivityManager.class);
                if(cm!=null) cm.unregisterNetworkCallback(networkCallback);
            }catch(Exception ignored){}
        }
        if(wakeLock!=null&&wakeLock.isHeld()) wakeLock.release();
        AsyncTask.execute(()->{
            Intent sshIntent=StatusInfo.getInstance().getSshIntent();
            if(sshIntent!=null) stopService(sshIntent);
        });
        try{
            ParcelFileDescriptor pfd=SocksPersistent.getInstance().getVpnInterface();
            if(pfd!=null) pfd.close();
        }catch(Exception e){
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
    private void startVpn() throws IOException{
        try{
            ParcelFileDescriptor vpnInterface;
            Builder builder=new VpnService.Builder();
            builder.setMtu(1500).addAddress("26.26.26.1",24);
            if(Build.VERSION.SDK_INT>=33){
                builder.addRoute(new IpPrefix(InetAddress.getByName("0.0.0.0"),0));
                builder.excludeRoute(new IpPrefix(InetAddress.getByName(getDnsIpFuture.get()),32));
            }else{
                ArrayList<Long> excluded=new ArrayList<>();
                excluded.add(ipATON(getDnsIpFuture.get()));
                addRoutesExcluding(builder,excluded);
            }
            if(getSelectedAppsFuture.get().isEmpty()){
                builder.addDnsServer(getDnsIpFuture.get()).addDisallowedApplication("ru.anton2319.vpnoverssh");
            }else{
                for(String pkg:getSelectedAppsFuture.get()) builder.addAllowedApplication(pkg);
            }
            vpnInterface=builder.establish();
            SocksPersistent.getInstance().setVpnInterface(vpnInterface);
            int socksPort=Integer.parseInt(Optional.of(sharedPreferences.getString("forwarder_port","1080")).orElse("1080"));
            engine.Key key=new engine.Key();
            key.setMark(0);key.setMTU(1500);key.setDevice("fd://"+vpnInterface.getFd());
            key.setInterface("");key.setLogLevel("warning");
            key.setProxy("socks5://127.0.0.1:"+socksPort);
            key.setRestAPI("");key.setTCPSendBufferSize("");key.setTCPReceiveBufferSize("");
            key.setTCPModerateReceiveBuffer(false);
            engine.Engine.insert(key);engine.Engine.start();
            while(true){if(Thread.interrupted()) throw new InterruptedException();Thread.sleep(1000);}
        }catch(InterruptedException e){onDestroy();}catch(Exception e){stopSelf();}
    }
    private Thread newVpnThread(){return new Thread(()->{try{startVpn();}catch(IOException e){}finally{stopSelf();}});}
    public static void addRoutesExcluding(Builder b,ArrayList<Long> ex){
        Collections.sort(ex);
        long cur=ipATON("0.0.0.0");long end=ipATON("126.255.255.255");
        while(cur<=end){int mask=getMaximumMask(cur,ex.isEmpty()?end:ex.get(0));long res=cur+subnetSize(mask);if(ex.contains(cur))ex.remove(0);else b.addRoute(ipNTOA(cur),mask);cur=res;}
        cur=ipATON("128.0.0.0");end=ipATON("223.255.255.255");
        while(cur<=end){int mask=getMaximumMask(cur,ex.isEmpty()?end:ex.get(0));long res=cur+subnetSize(mask);if(ex.contains(cur))ex.remove(0);else b.addRoute(ipNTOA(cur),mask);cur=res;}
        cur=ipATON("240.0.0.0");end=ipATON("255.255.255.255");
        while(cur<=end){int mask=getMaximumMask(cur,ex.isEmpty()?end:ex.get(0));long res=cur+subnetSize(mask);if(ex.contains(cur))ex.remove(0);else b.addRoute(ipNTOA(cur),mask);cur=res;}
    }
    public static int getMaximumMask(long s,long max){int m=32;final byte[] oct=intToByteArrayBigEndian((int)s);while(m>0){long sz=subnetSize(m-1);if(s+sz>max)break;else m--;}while(m<32){byte[] copy=new byte[oct.length];System.arraycopy(oct,0,copy,0,oct.length);if(maskValid(m,copy))break;else m++;}return m;}
    public static boolean maskValid(int mask,byte[] oct){int off=mask/8;if(off<oct.length){for(oct[off]<<=mask%8;off<oct.length;++off)if(oct[off]!=0)return false;return true;}return true;}
    public static long subnetSize(long mask){return (long)Math.pow(2,32-mask);}
    public static long ipATON(String ip){String[] a=ip.split("\\.");long n=0;for(int i=0;i<a.length;i++){int p=3-i;n+=((Integer.parseInt(a[i])%256*Math.pow(256,p)));}return n;}
    public static String ipNTOA(long ip){StringBuilder d=new StringBuilder();for(int i=3;i>=0;i--){long o=(ip>>(i*8))&0xFF;d.append(o);if(i>0)d.append(".");}return d.toString();}
    public static byte[] intToByteArrayBigEndian(int v){byte[] b=new byte[4];b[0]=(byte)(v>>24);b[1]=(byte)(v>>16);b[2]=(byte)(v>>8);b[3]=(byte)v;return b;}
}
