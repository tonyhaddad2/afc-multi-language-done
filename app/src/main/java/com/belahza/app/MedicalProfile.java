package com.belahza.app;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class MedicalProfile {
    public static final String FULL_NAME = "fullName";
    public static final String BLOOD_TYPE = "bloodType";
    public static final String ALLERGIES = "allergies";
    public static final String CONDITIONS = "conditions";
    public static final String MEDICATIONS = "medications";
    public static final String NOTES = "notes";
    public static final String EMERGENCY_CONTACTS = "emergencyContacts";

    private MedicalProfile() {}

    public static String get(Context context,String key) {
        String value = SecureLocalStore.get(context,"medical_"+key);
        if (!value.isEmpty()) return value;
        if (FULL_NAME.equals(key)) return SecureLocalStore.get(context,"medical_name");
        if (BLOOD_TYPE.equals(key)) return SecureLocalStore.get(context,"medical_blood");
        if (MEDICATIONS.equals(key)) return SecureLocalStore.get(context,"medical_meds");
        return "";
    }

    public static void set(Context context,String key,String value) {
        SecureLocalStore.put(context,"medical_"+key,value == null ? "" : value.trim());
    }

    public static Set<String> visibleFields(Context context) {
        String raw = SecureLocalStore.get(context,"medical_visible_fields");
        if (raw.isEmpty()) raw = FULL_NAME+","+BLOOD_TYPE+","+ALLERGIES+","+CONDITIONS+","+MEDICATIONS+","+EMERGENCY_CONTACTS;
        Set<String> fields = new LinkedHashSet<>();
        for (String item : raw.split(",")) if (!item.trim().isEmpty()) fields.add(item.trim());
        return fields;
    }

    public static void setVisibleFields(Context context,Set<String> fields) {
        StringBuilder out = new StringBuilder();
        for (String field : fields) {
            if (out.length() > 0) out.append(',');
            out.append(field);
        }
        SecureLocalStore.put(context,"medical_visible_fields",out.toString());
    }

    public static JSONArray visibleFieldsJson(Context context) {
        JSONArray array = new JSONArray();
        for (String field : visibleFields(context)) array.put(field);
        return array;
    }

    public static JSONObject toJson(Context context) {
        JSONObject json = new JSONObject();
        try {
            json.put(FULL_NAME,get(context,FULL_NAME));
            json.put(BLOOD_TYPE,get(context,BLOOD_TYPE));
            json.put(ALLERGIES,get(context,ALLERGIES));
            json.put(CONDITIONS,get(context,CONDITIONS));
            json.put(MEDICATIONS,get(context,MEDICATIONS));
            json.put(NOTES,get(context,NOTES));
            JSONArray contacts = new JSONArray();
            for (int i=1;i<=3;i++) {
                String name = SecureLocalStore.get(context,"trusted_name_"+i);
                String phone = SecureLocalStore.get(context,"trusted_phone_"+i);
                if (!phone.isEmpty()) {
                    JSONObject contact = new JSONObject();
                    contact.put("name",name);
                    contact.put("phone",phone);
                    contacts.put(contact);
                }
            }
            json.put(EMERGENCY_CONTACTS,contacts);
        } catch(Exception ignored) {}
        return json;
    }

    public static String emergencyText(Context context) {
        Set<String> visible = visibleFields(context);
        StringBuilder out = new StringBuilder();
        add(out,visible,FULL_NAME,LanguageManager.t(context,"name"),get(context,FULL_NAME));
        add(out,visible,BLOOD_TYPE,LanguageManager.t(context,"blood_type"),get(context,BLOOD_TYPE));
        add(out,visible,ALLERGIES,LanguageManager.t(context,"allergies"),get(context,ALLERGIES));
        add(out,visible,CONDITIONS,LanguageManager.t(context,"conditions"),get(context,CONDITIONS));
        add(out,visible,MEDICATIONS,LanguageManager.t(context,"medications"),get(context,MEDICATIONS));
        add(out,visible,NOTES,LanguageManager.t(context,"notes"),get(context,NOTES));
        if (visible.contains(EMERGENCY_CONTACTS)) {
            String contacts = trustedContactsText(context);
            if (!contacts.startsWith("No ")) out.append(LanguageManager.t(context,"emergency_contacts")).append(":\n").append(contacts);
        }
        return out.length() == 0 ? "No emergency Medical ID saved." : out.toString().trim();
    }

    private static void add(StringBuilder out,Set<String> visible,String field,String label,String value) {
        if (visible.contains(field) && value != null && !value.trim().isEmpty()) {
            out.append(label).append(": ").append(value.trim()).append("\n");
        }
    }


    public static String contactPhone(Context context) {
        return firstTrustedPhone(context);
    }

    public static String firstTrustedPhone(Context context) {
        List<String> phones = trustedPhones(context);
        return phones.isEmpty() ? "" : phones.get(0);
    }

    public static List<String> trustedPhones(Context context) {
        ArrayList<String> phones = new ArrayList<>();
        for (int i=1;i<=3;i++) {
            String value = SecureLocalStore.get(context,"trusted_phone_"+i);
            if (!value.isEmpty()) phones.add(value);
        }
        String old = SecureLocalStore.get(context,"medical_contact_phone");
        if (!old.isEmpty() && !phones.contains(old)) phones.add(old);
        return phones;
    }

    public static String trustedPhonesJoined(Context context) {
        StringBuilder out = new StringBuilder();
        for (String phone : trustedPhones(context)) {
            if (out.length() > 0) out.append(';');
            out.append(phone);
        }
        return out.toString();
    }

    public static String trustedContactsText(Context context) {
        StringBuilder out = new StringBuilder();
        for (int i=1;i<=3;i++) {
            String name = SecureLocalStore.get(context,"trusted_name_"+i);
            String phone = SecureLocalStore.get(context,"trusted_phone_"+i);
            if (!phone.isEmpty()) {
                out.append(name.isEmpty() ? LanguageManager.t(context,"trusted_contacts") : name)
                        .append(": ").append(phone).append("\n");
            }
        }
        return out.length() == 0 ? "No trusted contacts saved." : out.toString().trim();
    }
}
