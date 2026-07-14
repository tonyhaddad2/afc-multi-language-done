package com.belahza.app;

import android.content.Context;
import android.view.View;

public final class LanguageManager {
    public static final String EN = "en";
    public static final String FR = "fr";
    public static final String AR = "ar";

    private LanguageManager() {}

    public static String current(Context context) {
        String value = SecureLocalStore.get(context, "language");
        if (FR.equals(value) || AR.equals(value)) return value;
        return EN;
    }

    public static void set(Context context, String language) {
        if (!FR.equals(language) && !AR.equals(language)) language = EN;
        SecureLocalStore.put(context, "language", language);
    }

    public static boolean isArabic(Context context) {
        return AR.equals(current(context));
    }

    public static int layoutDirection(Context context) {
        return isArabic(context) ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR;
    }

    public static String languageName(Context context) {
        if (AR.equals(current(context))) return "العربية";
        if (FR.equals(current(context))) return "Français";
        return "English";
    }

    public static String t(Context context, String key) {
        return t(current(context), key);
    }

    public static String tr(Context context, String key) {
        return t(context,key);
    }

    public static String tr(String language, String key) {
        return t(language,key);
    }

    public static String t(String language, String key) {
        if (AR.equals(language)) return ar(key);
        if (FR.equals(language)) return fr(key);
        return en(key);
    }

    private static String en(String key) {
        switch (key) {
            case "back": return "Back";
            case "app_tagline": return "Emergency help, made immediate";
            case "sos": return "SOS";
            case "start_sos": return "Start emergency alert";
            case "sos_desc": return "Call help, notify trusted contacts, and share your live location.";
            case "cancel": return "Cancel";
            case "continue": return "Continue";
            case "call_140": return "Call Red Cross 140";
            case "call_112": return "Call Police 112";
            case "emergency_numbers": return "Emergency numbers";
            case "trusted_contacts": return "Trusted contacts";
            case "medical_id": return "Medical ID";
            case "settings": return "Settings";
            case "language": return "Language";
            case "search_title": return "What is happening?";
            case "search_hint": return "Describe what you see — typos are okay";
            case "search_help": return "Search understands English, French, Arabic, common synonyms, and small spelling mistakes.";
            case "no_match": return "No close match. Try symptoms such as chest, cannot breathe, bleeding, burn, weak arm, shaking, smoke, or unconscious.";
            case "life_threat": return "Life threat";
            case "airway": return "Airway";
            case "trauma": return "Trauma";
            case "injury": return "Injury";
            case "neurologic": return "Neurologic";
            case "rescue": return "Rescue";
            case "medical": return "Medical";
            case "mental_health": return "Mental health";
            case "disaster": return "Disaster";
            case "emergency_first": return "Emergency first";
            case "emergency_first_desc": return "Call emergency services and share your location before following this guide when danger is immediate.";
            case "call": return "Call";
            case "open_map": return "Open map";
            case "animation": return "3D maneuver lesson";
            case "animation_desc": return "Use the visual lesson together with the written steps. Move through the lesson one step at a time.";
            case "full_lesson": return "Open step-by-step lesson";
            case "practice": return "Practice";
            case "ai_coach": return "CPR posture coach";
            case "ai_limits": return "The coach checks camera framing, arm posture, shoulder alignment, and approximate rhythm. It cannot measure compression depth, force, or patient response.";
            case "alert_contacts": return "Alert trusted contacts";
            case "what_to_do": return "What to do";
            case "do_not": return "Do not";
            case "guide_disclaimer": return "Follow emergency dispatcher instructions. This guide supports rapid action but does not replace certified first-aid training.";
            case "choose_language": return "Choose language";
            case "choose_language_desc": return "The public emergency screens and search will use this language.";
            case "selected": return "Selected";
            case "save": return "Save";
            case "profile": return "Profile";
            case "medical_privacy": return "Medical ID visibility";
            case "permissions": return "Emergency permissions";
            case "connected_status": return "Connected service";
            case "developer_tools": return "Developer tools";
            case "developer_only": return "Available only in debug builds";
            case "countdown_title": return "Sending emergency alert";
            case "countdown_desc": return "Cancel now if this was accidental.";
            case "sending": return "Connecting and notifying trusted contacts…";
            case "active_title": return "Emergency active";
            case "active_desc": return "Keep this screen open when possible. Location sharing continues through an ongoing notification.";
            case "delivery": return "Alert delivery";
            case "location": return "Location";
            case "responses": return "Trusted-contact responses";
            case "waiting_backend": return "Connecting to emergency service";
            case "offline_queued": return "Internet unavailable — alert safely queued. Use the share fallback now.";
            case "notified": return "Trusted contacts notified";
            case "location_fresh": return "Fresh location";
            case "location_stale": return "Location may be stale";
            case "open_medical_id": return "Open emergency Medical ID";
            case "share_fallback": return "Share emergency fallback";
            case "mark_safe": return "I am safe — resolve";
            case "false_alarm": return "False alarm — cancel";
            case "add_contact": return "Add trusted contact";
            case "phone_number": return "Phone number";
            case "send_invite": return "Send invitation";
            case "pending_invites": return "Invitations for you";
            case "your_contacts": return "Your trusted contacts";
            case "accept": return "Accept";
            case "reject": return "Reject";
            case "remove": return "Remove";
            case "refresh": return "Refresh";
            case "incoming_emergency": return "Incoming emergency";
            case "seen": return "I saw this";
            case "helping": return "I am helping";
            case "cannot_help": return "I cannot help";
            case "directions": return "Directions";
            case "medical_information": return "Emergency medical information";
            case "no_active_emergency": return "No active emergency";
            case "privacy_fields": return "Visible during an emergency";
            case "name": return "Name";
            case "blood_type": return "Blood type";
            case "allergies": return "Allergies";
            case "conditions": return "Conditions";
            case "medications": return "Medications";
            case "notes": return "Medical notes";
            case "emergency_contacts": return "Emergency contacts";
            case "camera_permission": return "Camera";
            case "location_permission": return "Location";
            case "notification_permission": return "Notifications";
            case "allowed": return "Allowed";
            case "missing": return "Missing";
            case "grant": return "Allow";
            case "numbers_desc": return "Lebanon quick dial. Follow the dispatcher’s instructions.";
            case "copy": return "Copy";
            case "number_tip": return "Save 140, 112, 125, and 175 in your phone favorites.";
            case "lesson_step": return "Step";
            case "previous": return "Previous";
            case "next": return "Next";
            case "close": return "Close";
            case "play_pause": return "Play / pause";
            case "coach_title": return "CPR posture coach";
            case "start_camera": return "Start camera";
            case "flip_camera": return "Flip camera";
            case "reset": return "Reset tracking";
            case "tracking_waiting": return "Finding the rescuer";
            case "tracking_active": return "Rescuer tracking active";
            case "guided_only": return "Visual-guided practice only";
            case "cannot_assess": return "This maneuver cannot be assessed reliably by the camera. Follow the 3D lesson and instructor guidance.";
            case "too_far": return "Move the camera closer.";
            case "too_close": return "Move the camera farther away.";
            case "low_light": return "Increase the lighting.";
            case "straighten_arms": return "Straighten your elbows.";
            case "shoulders_over_hands": return "Move your shoulders above your hands.";
            case "rhythm_slow": return "Rhythm is slow. Aim for 100–120/min.";
            case "rhythm_fast": return "Rhythm is fast. Aim for 100–120/min.";
            case "good_form": return "Good visible CPR posture. Keep steady rhythm and allow full recoil.";
            default: return key;
        }
    }

    private static String fr(String key) {
        switch (key) {
            case "back": return "Retour";
            case "app_tagline": return "Aide d’urgence, immédiatement";
            case "sos": return "SOS";
            case "start_sos": return "Démarrer l’alerte";
            case "sos_desc": return "Appelle les secours, avertis tes proches et partage ta position en direct.";
            case "cancel": return "Annuler";
            case "continue": return "Continuer";
            case "call_140": return "Appeler la Croix-Rouge 140";
            case "call_112": return "Appeler la police 112";
            case "emergency_numbers": return "Numéros d’urgence";
            case "trusted_contacts": return "Contacts de confiance";
            case "medical_id": return "Fiche médicale";
            case "settings": return "Réglages";
            case "language": return "Langue";
            case "search_title": return "Que se passe-t-il ?";
            case "search_hint": return "Décris ce que tu vois — les fautes sont acceptées";
            case "search_help": return "La recherche comprend l’anglais, le français, l’arabe, les synonymes et les petites fautes.";
            case "no_match": return "Aucun résultat proche. Essaie : poitrine, ne respire pas, saignement, brûlure, bras faible, tremblements, fumée ou inconscient.";
            case "life_threat": return "Danger vital";
            case "airway": return "Voies respiratoires";
            case "trauma": return "Traumatisme";
            case "injury": return "Blessure";
            case "neurologic": return "Neurologique";
            case "rescue": return "Secours";
            case "medical": return "Médical";
            case "mental_health": return "Santé mentale";
            case "disaster": return "Catastrophe";
            case "emergency_first": return "Priorité urgence";
            case "emergency_first_desc": return "Appelle les secours et partage ta position avant le guide si le danger est immédiat.";
            case "call": return "Appeler";
            case "open_map": return "Ouvrir la carte";
            case "animation": return "Leçon 3D";
            case "animation_desc": return "Utilise la leçon visuelle avec les étapes écrites, une étape à la fois.";
            case "full_lesson": return "Ouvrir la leçon détaillée";
            case "practice": return "Entraînement";
            case "ai_coach": return "Coach posture RCP";
            case "ai_limits": return "Le coach vérifie le cadrage, les bras, l’alignement des épaules et le rythme approximatif. Il ne mesure ni profondeur, ni force, ni réponse du patient.";
            case "alert_contacts": return "Alerter les contacts";
            case "what_to_do": return "Que faire";
            case "do_not": return "À ne pas faire";
            case "guide_disclaimer": return "Suis les consignes des secours. Ce guide aide à agir vite mais ne remplace pas une formation certifiée.";
            case "choose_language": return "Choisir la langue";
            case "choose_language_desc": return "Les écrans d’urgence et la recherche utiliseront cette langue.";
            case "selected": return "Sélectionné";
            case "save": return "Enregistrer";
            case "profile": return "Profil";
            case "medical_privacy": return "Visibilité médicale";
            case "permissions": return "Permissions d’urgence";
            case "connected_status": return "Service connecté";
            case "developer_tools": return "Outils développeur";
            case "developer_only": return "Disponible uniquement en version debug";
            case "countdown_title": return "Envoi de l’alerte";
            case "countdown_desc": return "Annule maintenant si c’était accidentel.";
            case "sending": return "Connexion et notification des contacts…";
            case "active_title": return "Urgence active";
            case "active_desc": return "Garde cet écran ouvert si possible. Le partage de position continue via une notification.";
            case "delivery": return "Envoi de l’alerte";
            case "location": return "Position";
            case "responses": return "Réponses des contacts";
            case "waiting_backend": return "Connexion au service d’urgence";
            case "offline_queued": return "Pas d’Internet — alerte mise en file. Utilise le partage de secours.";
            case "notified": return "Contacts avertis";
            case "location_fresh": return "Position récente";
            case "location_stale": return "Position peut-être ancienne";
            case "open_medical_id": return "Ouvrir la fiche médicale";
            case "share_fallback": return "Partager l’alerte de secours";
            case "mark_safe": return "Je suis en sécurité";
            case "false_alarm": return "Fausse alerte — annuler";
            case "add_contact": return "Ajouter un contact";
            case "phone_number": return "Numéro de téléphone";
            case "send_invite": return "Envoyer l’invitation";
            case "pending_invites": return "Invitations reçues";
            case "your_contacts": return "Tes contacts";
            case "accept": return "Accepter";
            case "reject": return "Refuser";
            case "remove": return "Supprimer";
            case "refresh": return "Actualiser";
            case "incoming_emergency": return "Urgence reçue";
            case "seen": return "J’ai vu l’alerte";
            case "helping": return "Je viens aider";
            case "cannot_help": return "Je ne peux pas aider";
            case "directions": return "Itinéraire";
            case "medical_information": return "Informations médicales";
            case "no_active_emergency": return "Aucune urgence active";
            case "privacy_fields": return "Visible pendant une urgence";
            case "name": return "Nom";
            case "blood_type": return "Groupe sanguin";
            case "allergies": return "Allergies";
            case "conditions": return "Maladies";
            case "medications": return "Médicaments";
            case "notes": return "Notes médicales";
            case "emergency_contacts": return "Contacts d’urgence";
            case "camera_permission": return "Caméra";
            case "location_permission": return "Position";
            case "notification_permission": return "Notifications";
            case "allowed": return "Autorisé";
            case "missing": return "Manquant";
            case "grant": return "Autoriser";
            case "numbers_desc": return "Appels rapides au Liban. Suis les consignes du régulateur.";
            case "copy": return "Copier";
            case "number_tip": return "Enregistre 140, 112, 125 et 175 dans les favoris.";
            case "lesson_step": return "Étape";
            case "previous": return "Précédente";
            case "next": return "Suivante";
            case "close": return "Fermer";
            case "play_pause": return "Lecture / pause";
            case "coach_title": return "Coach posture RCP";
            case "start_camera": return "Démarrer la caméra";
            case "flip_camera": return "Changer de caméra";
            case "reset": return "Réinitialiser";
            case "tracking_waiting": return "Recherche du secouriste";
            case "tracking_active": return "Suivi du secouriste actif";
            case "guided_only": return "Entraînement visuel uniquement";
            case "cannot_assess": return "Cette manœuvre ne peut pas être évaluée de façon fiable par la caméra. Suis la leçon 3D et l’instructeur.";
            case "too_far": return "Rapproche la caméra.";
            case "too_close": return "Éloigne la caméra.";
            case "low_light": return "Augmente la lumière.";
            case "straighten_arms": return "Tends les coudes.";
            case "shoulders_over_hands": return "Place les épaules au-dessus des mains.";
            case "rhythm_slow": return "Rythme trop lent. Vise 100–120/min.";
            case "rhythm_fast": return "Rythme trop rapide. Vise 100–120/min.";
            case "good_form": return "Bonne posture RCP visible. Garde un rythme régulier et laisse la poitrine remonter.";
            default: return en(key);
        }
    }

    private static String ar(String key) {
        switch (key) {
            case "back": return "رجوع";
            case "app_tagline": return "مساعدة طارئة فوراً";
            case "sos": return "SOS";
            case "start_sos": return "بدء تنبيه الطوارئ";
            case "sos_desc": return "اتصل بالمساعدة، نبّه الأشخاص الموثوقين، وشارك موقعك المباشر.";
            case "cancel": return "إلغاء";
            case "continue": return "متابعة";
            case "call_140": return "اتصل بالصليب الأحمر 140";
            case "call_112": return "اتصل بالشرطة 112";
            case "emergency_numbers": return "أرقام الطوارئ";
            case "trusted_contacts": return "أشخاص موثوقون";
            case "medical_id": return "الهوية الطبية";
            case "settings": return "الإعدادات";
            case "language": return "اللغة";
            case "search_title": return "ماذا يحدث؟";
            case "search_hint": return "صف ما تراه — الأخطاء الإملائية مقبولة";
            case "search_help": return "البحث يفهم العربية والإنجليزية والفرنسية والمرادفات والأخطاء البسيطة.";
            case "no_match": return "لا توجد نتيجة قريبة. جرّب: صدر، لا يتنفس، نزيف، حرق، ضعف ذراع، تشنج، دخان أو فاقد الوعي.";
            case "life_threat": return "خطر على الحياة";
            case "airway": return "مجرى التنفس";
            case "trauma": return "إصابة";
            case "injury": return "إصابة";
            case "neurologic": return "عصبي";
            case "rescue": return "إنقاذ";
            case "medical": return "طبي";
            case "mental_health": return "صحة نفسية";
            case "disaster": return "كارثة";
            case "emergency_first": return "الأولوية للطوارئ";
            case "emergency_first_desc": return "اتصل بالطوارئ وشارك موقعك قبل اتباع الدليل إذا كان الخطر فورياً.";
            case "call": return "اتصال";
            case "open_map": return "فتح الخريطة";
            case "animation": return "درس المناورة ثلاثي الأبعاد";
            case "animation_desc": return "استخدم الدرس المرئي مع الخطوات المكتوبة، خطوة بعد خطوة.";
            case "full_lesson": return "فتح الدرس خطوة بخطوة";
            case "practice": return "تدريب";
            case "ai_coach": return "مدرّب وضعية CPR";
            case "ai_limits": return "يفحص المدرّب إطار الكاميرا ووضعية الذراعين ومحاذاة الكتفين والإيقاع التقريبي. لا يقيس العمق أو القوة أو استجابة المصاب.";
            case "alert_contacts": return "تنبيه الموثوقين";
            case "what_to_do": return "ماذا تفعل";
            case "do_not": return "لا تفعل";
            case "guide_disclaimer": return "اتبع تعليمات الطوارئ. هذا الدليل يساعد على التصرف السريع ولا يستبدل التدريب المعتمد.";
            case "choose_language": return "اختر اللغة";
            case "choose_language_desc": return "ستستخدم شاشات الطوارئ والبحث هذه اللغة.";
            case "selected": return "المحددة";
            case "save": return "حفظ";
            case "profile": return "الملف";
            case "medical_privacy": return "ظهور الهوية الطبية";
            case "permissions": return "أذونات الطوارئ";
            case "connected_status": return "الخدمة المتصلة";
            case "developer_tools": return "أدوات المطور";
            case "developer_only": return "متاحة فقط في نسخة الاختبار";
            case "countdown_title": return "إرسال تنبيه الطوارئ";
            case "countdown_desc": return "ألغِ الآن إذا كان التنبيه بالخطأ.";
            case "sending": return "يتم الاتصال وتنبيه الأشخاص الموثوقين…";
            case "active_title": return "الطوارئ فعّالة";
            case "active_desc": return "اترك هذه الشاشة مفتوحة إذا أمكن. يستمر إرسال الموقع عبر إشعار دائم.";
            case "delivery": return "إرسال التنبيه";
            case "location": return "الموقع";
            case "responses": return "ردود الأشخاص الموثوقين";
            case "waiting_backend": return "يتم الاتصال بخدمة الطوارئ";
            case "offline_queued": return "لا يوجد إنترنت — تم حفظ التنبيه للإرسال. استخدم المشاركة الاحتياطية الآن.";
            case "notified": return "تم تنبيه الأشخاص";
            case "location_fresh": return "موقع حديث";
            case "location_stale": return "قد يكون الموقع قديماً";
            case "open_medical_id": return "فتح الهوية الطبية";
            case "share_fallback": return "مشاركة تنبيه احتياطي";
            case "mark_safe": return "أنا بخير — إنهاء";
            case "false_alarm": return "إنذار خاطئ — إلغاء";
            case "add_contact": return "إضافة شخص موثوق";
            case "phone_number": return "رقم الهاتف";
            case "send_invite": return "إرسال الدعوة";
            case "pending_invites": return "دعوات لك";
            case "your_contacts": return "الأشخاص الموثوقون";
            case "accept": return "قبول";
            case "reject": return "رفض";
            case "remove": return "إزالة";
            case "refresh": return "تحديث";
            case "incoming_emergency": return "تنبيه طارئ وارد";
            case "seen": return "رأيت التنبيه";
            case "helping": return "أنا قادم للمساعدة";
            case "cannot_help": return "لا أستطيع المساعدة";
            case "directions": return "الاتجاهات";
            case "medical_information": return "المعلومات الطبية الطارئة";
            case "no_active_emergency": return "لا توجد حالة طارئة فعّالة";
            case "privacy_fields": return "ظاهرة أثناء الطوارئ";
            case "name": return "الاسم";
            case "blood_type": return "فئة الدم";
            case "allergies": return "الحساسية";
            case "conditions": return "الحالات المرضية";
            case "medications": return "الأدوية";
            case "notes": return "ملاحظات طبية";
            case "emergency_contacts": return "جهات اتصال الطوارئ";
            case "camera_permission": return "الكاميرا";
            case "location_permission": return "الموقع";
            case "notification_permission": return "الإشعارات";
            case "allowed": return "مسموح";
            case "missing": return "غير مسموح";
            case "grant": return "سماح";
            case "numbers_desc": return "اتصال سريع في لبنان. اتبع تعليمات عامل الطوارئ.";
            case "copy": return "نسخ";
            case "number_tip": return "احفظ 140 و112 و125 و175 في المفضلة.";
            case "lesson_step": return "الخطوة";
            case "previous": return "السابقة";
            case "next": return "التالية";
            case "close": return "إغلاق";
            case "play_pause": return "تشغيل / إيقاف";
            case "coach_title": return "مدرّب وضعية CPR";
            case "start_camera": return "تشغيل الكاميرا";
            case "flip_camera": return "تبديل الكاميرا";
            case "reset": return "إعادة التتبع";
            case "tracking_waiting": return "البحث عن المُسعف";
            case "tracking_active": return "تتبع المُسعف فعّال";
            case "guided_only": return "تدريب مرئي فقط";
            case "cannot_assess": return "لا يمكن للكاميرا تقييم هذه المناورة بشكل موثوق. اتبع الدرس ثلاثي الأبعاد وتعليمات المدرب.";
            case "too_far": return "قرّب الكاميرا.";
            case "too_close": return "أبعد الكاميرا.";
            case "low_light": return "زد الإضاءة.";
            case "straighten_arms": return "مدّ المرفقين.";
            case "shoulders_over_hands": return "ضع الكتفين فوق اليدين.";
            case "rhythm_slow": return "الإيقاع بطيء. الهدف 100–120 بالدقيقة.";
            case "rhythm_fast": return "الإيقاع سريع. الهدف 100–120 بالدقيقة.";
            case "good_form": return "وضعية CPR الظاهرة جيدة. حافظ على الإيقاع واترك الصدر يرتد كاملاً.";
            default: return en(key);
        }
    }

    public static String emergencyTitle(Context context, EmergencyData.Emergency emergency) {
        return EmergencyTranslations.title(current(context), emergency.key, emergency.title, emergency.arabic);
    }

    public static String emergencyLine(Context context, EmergencyData.Emergency emergency) {
        return EmergencyTranslations.summary(current(context), emergency.key, emergency.oneLine);
    }

    public static String emergencyCategory(Context context, EmergencyData.Emergency emergency) {
        String key;
        switch (emergency.category) {
            case "Life threat": key = "life_threat"; break;
            case "Airway": key = "airway"; break;
            case "Trauma": key = "trauma"; break;
            case "Injury": key = "injury"; break;
            case "Neurologic": key = "neurologic"; break;
            case "Rescue": key = "rescue"; break;
            case "Medical": key = "medical"; break;
            case "Mental health": key = "mental_health"; break;
            case "Disaster": key = "disaster"; break;
            default: return emergency.category;
        }
        return t(context, key);
    }
}
