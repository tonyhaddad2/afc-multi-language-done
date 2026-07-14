package com.belahza.app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class ConnectedBackendClient {
    public interface Callback { void done(boolean ok,String body); }

    private ConnectedBackendClient() {}

    public static void createEmergencyAsync(Context context,String type,String note,String location,String clientRequestId,Callback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("clientRequestId",clientRequestId);
            body.put("deviceId",SecureLocalStore.getOrCreateDeviceId(context));
            body.put("emergencyType",type == null ? "Emergency" : type);
            body.put("note",note == null ? "" : note);
            body.put("location",location == null ? "" : location);
            body.put("medicalIdText",MedicalProfile.emergencyText(context));
            request(context,"POST","/v1/emergencies",body,true,callback);
        } catch (Exception e) {
            fail(callback,e);
        }
    }

    public static void createEmergencyAsync(Context context,String type,String note,String location,Callback callback) {
        createEmergencyAsync(context,type,note,location,java.util.UUID.randomUUID().toString(),callback);
    }

    public static void updateLocationAsync(Context context,String emergencyId,double lat,double lng,float accuracy,int battery,Callback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("lat",lat);
            body.put("lng",lng);
            body.put("accuracy",accuracy);
            body.put("battery",battery);
            body.put("source","fused");
            request(context,"POST","/v1/emergencies/"+emergencyId+"/location",body,true,callback);
        } catch (Exception e) {
            fail(callback,e);
        }
    }

    public static void resolveEmergencyAsync(Context context,String emergencyId,Callback callback) {
        request(context,"POST","/v1/emergencies/"+emergencyId+"/resolve",new JSONObject(),true,callback);
    }

    public static void cancelEmergencyAsync(Context context,String emergencyId,Callback callback) {
        request(context,"POST","/v1/emergencies/"+emergencyId+"/cancel",new JSONObject(),true,callback);
    }

    public static void getEmergencyAsync(Context context,String emergencyId,Callback callback) {
        request(context,"GET","/v1/emergencies/"+emergencyId,null,true,callback);
    }

    public static void getActiveEmergencyAsync(Context context,Callback callback) {
        request(context,"GET","/v1/emergencies/active",null,true,callback);
    }

    public static void emergencyHistoryAsync(Context context,Callback callback) {
        request(context,"GET","/v1/emergencies",null,true,callback);
    }

    public static void markEmergencyStatusAsync(Context context,String emergencyId,String status,String note,Callback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("status",status);
            body.put("note",note == null ? "" : note);
            request(context,"POST","/v1/emergencies/"+emergencyId+"/status",body,true,callback);
        } catch (Exception e) {
            fail(callback,e);
        }
    }

    public static void inviteTrustedContactAsync(Context context,String phone,Callback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("phone",phone == null ? "" : phone.trim());
            request(context,"POST","/v1/trusted-contacts/invite",body,true,callback);
        } catch (Exception e) {
            fail(callback,e);
        }
    }

    public static void listTrustedContactsAsync(Context context,Callback callback) {
        request(context,"GET","/v1/trusted-contacts",null,true,callback);
    }

    public static void listTrustedInvitesAsync(Context context,Callback callback) {
        request(context,"GET","/v1/trusted-contacts/invites",null,true,callback);
    }

    public static void acceptTrustedContactAsync(Context context,String inviteId,Callback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("inviteId",inviteId);
            request(context,"POST","/v1/trusted-contacts/accept",body,true,callback);
        } catch (Exception e) {
            fail(callback,e);
        }
    }

    public static void rejectTrustedContactAsync(Context context,String inviteId,Callback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("inviteId",inviteId);
            request(context,"POST","/v1/trusted-contacts/reject",body,true,callback);
        } catch (Exception e) {
            fail(callback,e);
        }
    }

    public static void removeTrustedContactAsync(Context context,String contactUserId,Callback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("contactUserId",contactUserId);
            request(context,"POST","/v1/trusted-contacts/remove",body,true,callback);
        } catch (Exception e) {
            fail(callback,e);
        }
    }

    public static void updateMedicalIdAsync(Context context,Callback callback) {
        syncMedicalIdAsync(context,callback);
    }

    public static void syncMedicalIdAsync(Context context,Callback callback) {
        try {
            JSONObject body = MedicalProfile.toJson(context);
            body.put("visibleFields",MedicalProfile.visibleFieldsJson(context));
            request(context,"PUT","/v1/medical-id",body,true,callback);
        } catch (Exception e) {
            fail(callback,e);
        }
    }

    public static void createUserProfileAsync(Context context,String name,String phone,Callback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("name",name == null ? "" : name.trim());
            body.put("phone",phone == null ? "" : phone.trim());
            body.put("language",LanguageManager.current(context));
            request(context,"PUT","/v1/users/profile",body,true,callback);
        } catch (Exception e) {
            fail(callback,e);
        }
    }

    public static void registerDeviceTokenAsync(Context context,String token,Callback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("token",token);
            body.put("platform","android");
            request(context,"POST","/v1/users/device-token",body,true,callback);
        } catch (Exception e) {
            fail(callback,e);
        }
    }

    public static void exchangeFirebaseIdTokenAsync(Context context,String idToken,Callback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("idToken",idToken);
            request(context,"POST","/v1/auth/firebase",body,false,callback);
        } catch (Exception e) {
            fail(callback,e);
        }
    }

    public static void requestDevTokenAsync(Context context,String userId,String phone,Callback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("userId",userId);
            body.put("phone",phone);
            request(context,"POST","/v1/auth/dev-token",body,false,callback);
        } catch (Exception e) {
            fail(callback,e);
        }
    }

    public static void healthAsync(Context context,Callback callback) {
        request(context,"GET","/health",null,false,callback);
    }

    public static void getAppConfigAsync(Context context,Callback callback) {
        request(context,"GET","/v1/config/public",null,false,callback);
    }

    public static void getAsync(Context context,String path,Callback callback) {
        request(context,"GET",path,null,true,callback);
    }

    public static void postAsync(Context context,String path,JSONObject body,Callback callback) {
        request(context,"POST",path,body,true,callback);
    }

    public static void putAsync(Context context,String path,JSONObject body,Callback callback) {
        request(context,"PUT",path,body,true,callback);
    }

    private static void request(Context context,String method,String path,JSONObject body,boolean authenticated,Callback callback) {
        new Thread(() -> {
            boolean ok = false;
            String response;
            HttpURLConnection connection = null;
            try {
                String base = AuthSession.apiBaseUrl(context);
                if (base.endsWith("/")) base = base.substring(0,base.length()-1);
                connection = (HttpURLConnection)new URL(base+path).openConnection();
                connection.setRequestMethod(method);
                connection.setConnectTimeout(9000);
                connection.setReadTimeout(14000);
                connection.setRequestProperty("Accept","application/json");
                connection.setRequestProperty("Content-Type","application/json; charset=utf-8");
                connection.setRequestProperty("X-BeLahza-Device",SecureLocalStore.getOrCreateDeviceId(context));
                if (authenticated) {
                    String token = AuthSession.token(context);
                    if (!token.isEmpty()) connection.setRequestProperty("Authorization","Bearer "+token);
                }
                if (body != null && !"GET".equals(method)) {
                    connection.setDoOutput(true);
                    byte[] data = body.toString().getBytes(StandardCharsets.UTF_8);
                    try (OutputStream output = connection.getOutputStream()) {
                        output.write(data);
                    }
                }
                int code = connection.getResponseCode();
                ok = code >= 200 && code < 300;
                response = read(ok ? connection.getInputStream() : connection.getErrorStream());
                if (response.isEmpty()) response = "HTTP "+code;
            } catch (Exception e) {
                response = e.getMessage() == null ? "network error" : e.getMessage();
            } finally {
                if (connection != null) connection.disconnect();
            }

            if (callback != null) {
                boolean finalOk = ok;
                String finalResponse = response;
                new Handler(Looper.getMainLooper()).post(() -> callback.done(finalOk,finalResponse));
            }
        }).start();
    }

    private static String read(InputStream input) throws Exception {
        if (input == null) return "";
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input,StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) builder.append(line);
        }
        return builder.toString();
    }

    private static void fail(Callback callback,Exception e) {
        if (callback != null) callback.done(false,e.getMessage() == null ? "error" : e.getMessage());
    }
}
