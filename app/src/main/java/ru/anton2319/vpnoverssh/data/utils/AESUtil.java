package ru.anton2319.vpnoverssh.data.utils;
import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
public class AESUtil {
    private static final String KEY = "NANDOMX-V5-KEY-20"; // 16 chars
    public static String encrypt(String data){
        try{
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] enc = cipher.doFinal(data.getBytes());
            return Base64.encodeToString(enc, Base64.DEFAULT);
        }catch(Exception e){ return null; }
    }
    public static String decrypt(String encData){
        try{
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.decode(encData, Base64.DEFAULT);
            byte[] dec = cipher.doFinal(decoded);
            return new String(dec);
        }catch(Exception e){ return null; }
    }
}
