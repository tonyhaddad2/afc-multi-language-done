package com.belahza.app;

import android.content.Context;

public final class AuthSession {
    private AuthSession() {}

    public static boolean hasBearerToken(Context context) {
        return !SecureLocalStore.get(context, "auth_bearer_token").trim().isEmpty();
    }

    public static String token(Context context) {
        return SecureLocalStore.get(context, "auth_bearer_token");
    }

    public static void save(Context context, String phone, String name, String token, String apiBaseUrl) {
        SecureLocalStore.put(context, "user_phone", phone == null ? "" : phone.trim());
        SecureLocalStore.put(context, "user_name", name == null ? "" : name.trim());
        SecureLocalStore.put(context, "auth_bearer_token", token == null ? "" : token.trim());
        if (apiBaseUrl != null && !apiBaseUrl.trim().isEmpty()) SecureLocalStore.put(context, "api_base_url", apiBaseUrl.trim());
    }

    public static String displayName(Context context) {
        String name = SecureLocalStore.get(context, "user_name").trim();
        if (!name.isEmpty()) return name;
        String phone = SecureLocalStore.get(context, "user_phone").trim();
        return phone.isEmpty() ? "Local user" : phone;
    }

    public static String apiBaseUrl(Context context) {
        String stored = SecureLocalStore.get(context, "api_base_url").trim();
        return stored.isEmpty() ? context.getString(R.string.production_api_base_url) : stored;
    }

    public static void clear(Context context) {
        SecureLocalStore.remove(context, "auth_bearer_token");
        SecureLocalStore.remove(context, "user_phone");
        SecureLocalStore.remove(context, "user_name");
    }
}
