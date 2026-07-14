package com.belahza.app;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

public final class EmergencySyncQueue {
    private static final String KEY = "offline_emergency_queue";

    private EmergencySyncQueue() {}

    public static synchronized void enqueueEmergency(Context context,String type,String note,String location) {
        enqueueEmergency(context,EmergencyMode.requestId(context).isEmpty()?java.util.UUID.randomUUID().toString():EmergencyMode.requestId(context),type,note,location);
    }

    public static synchronized void enqueueEmergency(Context context,String requestId,String type,String note,String location) {
        try {
            JSONArray queue = read(context);
            for (int i=0;i<queue.length();i++) {
                JSONObject existing = queue.optJSONObject(i);
                if (existing != null &&
                        "create".equals(existing.optString("kind")) &&
                        requestId.equals(existing.optString("clientRequestId"))) {
                    return;
                }
            }
            JSONObject item = new JSONObject();
            item.put("id",UUID.randomUUID().toString());
            item.put("kind","create");
            item.put("clientRequestId",requestId);
            item.put("type",type);
            item.put("note",note);
            item.put("location",location);
            item.put("createdAt",System.currentTimeMillis());
            queue.put(item);
            save(context,queue);
        } catch (Exception ignored) {}
    }

    public static synchronized void enqueueLocation(Context context,String emergencyId,double lat,double lng,float accuracy,int battery) {
        try {
            JSONArray queue = read(context);
            JSONObject item = new JSONObject();
            item.put("id",UUID.randomUUID().toString());
            item.put("kind","location");
            item.put("emergencyId",emergencyId == null ? "" : emergencyId);
            item.put("lat",lat);
            item.put("lng",lng);
            item.put("accuracy",accuracy);
            item.put("battery",battery);
            item.put("createdAt",System.currentTimeMillis());
            queue.put(item);
            trimLocationItems(queue);
            save(context,queue);
        } catch (Exception ignored) {}
    }

    public static synchronized void clear(Context context) {
        save(context,new JSONArray());
    }

    public static synchronized int size(Context context) {
        return read(context).length();
    }

    public static synchronized String preview(Context context) {
        JSONArray queue = read(context);
        if (queue.length() == 0) return "No queued emergency actions.";
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<queue.length();i++) {
            JSONObject item = queue.optJSONObject(i);
            if (item == null) continue;
            builder.append(i+1).append(". ").append(item.optString("kind"));
            if ("create".equals(item.optString("kind"))) builder.append(" · ").append(item.optString("type"));
            builder.append("\n");
        }
        return builder.toString().trim();
    }

    public static void retryAll(Context context,ConnectedBackendClient.Callback callback) {
        processNext(context,callback,0);
    }

    private static void processNext(Context context,ConnectedBackendClient.Callback callback,int completed) {
        JSONObject item;
        synchronized (EmergencySyncQueue.class) {
            JSONArray queue = read(context);
            if (queue.length() == 0) {
                if (callback != null) callback.done(true,"Synced "+completed+" queued actions");
                return;
            }
            item = queue.optJSONObject(0);
            if (item == null) {
                removeFirst(context);
                processNext(context,callback,completed);
                return;
            }
        }

        String kind = item.optString("kind");
        if ("create".equals(kind)) {
            String requestId = item.optString("clientRequestId");
            ConnectedBackendClient.createEmergencyAsync(
                    context,
                    item.optString("type","Emergency"),
                    item.optString("note",""),
                    item.optString("location",""),
                    requestId,
                    (ok,body) -> {
                        if (!ok) {
                            if (callback != null) callback.done(false,"Queue paused: "+body);
                            return;
                        }
                        try {
                            JSONObject response = new JSONObject(body);
                            String emergencyId = response.optString("emergencyId","");
                            int notified = response.optInt("notified",0);
                            EmergencyMode.attachServerEmergency(context,requestId,emergencyId,notified);
                            replacePendingLocationIds(context,emergencyId);
                        } catch (Exception ignored) {}
                        removeFirst(context);
                        processNext(context,callback,completed+1);
                    }
            );
            return;
        }

        if ("location".equals(kind)) {
            String emergencyId = item.optString("emergencyId");
            if (emergencyId.isEmpty()) emergencyId = EmergencyMode.emergencyId(context);
            if (emergencyId.isEmpty()) {
                if (callback != null) callback.done(false,"Waiting for the server emergency ID");
                return;
            }
            final String targetId = emergencyId;
            ConnectedBackendClient.updateLocationAsync(
                    context,
                    targetId,
                    item.optDouble("lat"),
                    item.optDouble("lng"),
                    (float)item.optDouble("accuracy"),
                    item.optInt("battery"),
                    (ok,body) -> {
                        if (!ok) {
                            if (callback != null) callback.done(false,"Queue paused: "+body);
                            return;
                        }
                        removeFirst(context);
                        processNext(context,callback,completed+1);
                    }
            );
            return;
        }

        removeFirst(context);
        processNext(context,callback,completed);
    }

    private static synchronized void removeFirst(Context context) {
        JSONArray queue = read(context);
        JSONArray next = new JSONArray();
        for (int i=1;i<queue.length();i++) next.put(queue.opt(i));
        save(context,next);
    }

    private static synchronized void replacePendingLocationIds(Context context,String emergencyId) {
        if (emergencyId == null || emergencyId.isEmpty()) return;
        JSONArray queue = read(context);
        for (int i=0;i<queue.length();i++) {
            JSONObject item = queue.optJSONObject(i);
            if (item != null && "location".equals(item.optString("kind")) && item.optString("emergencyId").isEmpty()) {
                try { item.put("emergencyId",emergencyId); } catch(Exception ignored) {}
            }
        }
        save(context,queue);
    }

    private static void trimLocationItems(JSONArray queue) {
        int locationCount = 0;
        for (int i=queue.length()-1;i>=0;i--) {
            JSONObject item = queue.optJSONObject(i);
            if (item != null && "location".equals(item.optString("kind"))) {
                locationCount++;
                if (locationCount > 20) {
                    JSONArray trimmed = new JSONArray();
                    for (int j=0;j<queue.length();j++) if (j != i) trimmed.put(queue.opt(j));
                    while (queue.length() > 0) queue.remove(queue.length()-1);
                    for (int j=0;j<trimmed.length();j++) queue.put(trimmed.opt(j));
                }
            }
        }
    }

    private static synchronized JSONArray read(Context context) {
        try {
            String raw = SecureLocalStore.get(context,KEY);
            return raw == null || raw.trim().isEmpty() ? new JSONArray() : new JSONArray(raw);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private static synchronized void save(Context context,JSONArray queue) {
        SecureLocalStore.put(context,KEY,queue.toString());
    }
}
