package com.belahza.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public final class SecureLocalStore {
    private static final String TAG = "SecureLocalStore";
    private static final String PREFS = "be_lahza_secure_store";
    private static final String KEY_ALIAS = "be_lahza_local_aes_gcm_v1";
    private static final String DEVICE_ID_KEY = "device_id";

    private SecureLocalStore() {}

    public static void put(Context context, String key, String value) {
        if (key == null) return;
        String safeValue = value == null ? "" : value;
        try {
            prefs(context).edit().putString(key, encrypt(safeValue)).apply();
        } catch (Exception e) {
            Log.e(TAG, "Encrypted write failed", e);
        }
    }

    public static String get(Context context, String key) {
        if (key == null) return "";
        String packed = prefs(context).getString(key, "");
        if (packed == null || packed.trim().isEmpty()) return "";
        try {
            return decrypt(packed);
        } catch (Exception e) {
            Log.e(TAG, "Encrypted read failed", e);
            return "";
        }
    }

    public static void remove(Context context, String key) {
        if (key != null) prefs(context).edit().remove(key).apply();
    }

    public static String getOrCreateDeviceId(Context context) {
        String current = get(context, DEVICE_ID_KEY);
        if (!current.trim().isEmpty()) return current;
        String id = UUID.randomUUID().toString();
        put(context, DEVICE_ID_KEY, id);
        return id;
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static String encrypt(String plainText) throws Exception {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey(), new GCMParameterSpec(128, iv));
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(iv, Base64.NO_WRAP) + ":" + Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }

    private static String decrypt(String packed) throws Exception {
        String[] parts = packed.split(":", 2);
        if (parts.length != 2) return "";
        byte[] iv = Base64.decode(parts[0], Base64.NO_WRAP);
        byte[] encrypted = Base64.decode(parts[1], Base64.NO_WRAP);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), new GCMParameterSpec(128, iv));
        byte[] plain = cipher.doFinal(encrypted);
        return new String(plain, StandardCharsets.UTF_8);
    }

    private static SecretKey getOrCreateSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        KeyStore.Entry entry = keyStore.getEntry(KEY_ALIAS, null);
        if (entry instanceof KeyStore.SecretKeyEntry) {
            return ((KeyStore.SecretKeyEntry) entry).getSecretKey();
        }
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
         .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
         .setRandomizedEncryptionRequired(true);
        if (Build.VERSION.SDK_INT >= 28) builder.setUnlockedDeviceRequired(false);
        keyGenerator.init(builder.build());
        return keyGenerator.generateKey();
    }
}
