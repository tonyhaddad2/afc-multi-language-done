package com.belahza.app;

import android.content.Context;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class SmartSearch {
    public static final class Result implements Comparable<Result> {
        public final EmergencyData.Emergency emergency;
        public final double score;
        Result(EmergencyData.Emergency emergency, double score) { this.emergency = emergency; this.score = score; }
        @Override public int compareTo(Result other) { return Double.compare(other.score, score); }
    }

    private SmartSearch() {}

    public static List<EmergencyData.Emergency> filter(Context context, List<EmergencyData.Emergency> all, String query) {
        String q = normalize(query);
        if (q.isEmpty()) return all;
        ArrayList<Result> results = new ArrayList<>();
        for (EmergencyData.Emergency e : all) {
            double s = score(context, e, q);
            if (s >= 0.42) results.add(new Result(e, s));
        }
        Collections.sort(results);
        ArrayList<EmergencyData.Emergency> out = new ArrayList<>();
        for (Result r : results) out.add(r.emergency);
        return out;
    }

    public static double score(Context context, EmergencyData.Emergency e, String query) {
        String q = normalize(query);
        if (q.isEmpty()) return 1;
        List<String> queryTerms = tokens(q);
        String doc = document(context, e);
        List<String> docTerms = tokens(doc);

        double phraseBonus = doc.contains(q) ? 0.35 : 0.0;
        double total = 0;
        int used = 0;
        for (String qt : queryTerms) {
            if (qt.length() == 0) continue;
            used++;
            double best = 0;
            for (String dt : docTerms) {
                if (dt.length() == 0) continue;
                if (dt.equals(qt)) best = Math.max(best, 1.0);
                else if (dt.contains(qt) || qt.contains(dt)) best = Math.max(best, 0.82);
                else {
                    double sim = similarity(qt, dt);
                    if (sim >= 0.72) best = Math.max(best, sim);
                    else if (qt.length() <= 4 && sim >= 0.66) best = Math.max(best, sim * 0.92);
                }
            }
            total += best;
        }
        if (used == 0) return 0;
        double avg = total / used;
        return Math.min(1.0, avg + phraseBonus);
    }

    private static String document(Context c, EmergencyData.Emergency e) {
        LinkedHashSet<String> words = new LinkedHashSet<>();
        add(words, e.key);
        add(words, e.title);
        add(words, e.arabic);
        add(words, e.category);
        add(words, e.oneLine);
        add(words, LanguageManager.emergencyTitle(c, e));
        add(words, LanguageManager.emergencyLine(c, e));
        add(words, LanguageManager.emergencyCategory(c, e));
        add(words, join(e.steps));
        add(words, join(e.donts));
        add(words, join(e.redFlags));
        add(words, synonyms(e.key));
        return normalize(join(words));
    }

    private static String synonyms(String key) {
        Map<String, String> s = new HashMap<>();
        s.put("cardiac",
                "cpr rcp cardio cardiac heart attack arrest no pulse unconscious collapse compression compressions massage coeur cœur arret arrêt cardiaque crise cardiaque infarctus قلب توقف القلب انعاش إنعاش انعاش قلبي تنفس اصطناعي ضغط صدر صدر");
        s.put("choking",
                "choke choking heimlich suffocation airway blocked obstruction food stuck cannot breathe gag cough étouffement etouffement s'étouffe avaler gorge airway اختناق شرق غصة علق أكل طعام حلق نفس ما عم يتنفس");
        s.put("bleeding",
                "bleed bleeding blood hemorrhage haemorrhage wound cut injury pressure bandage tourniquet saignement hemorragie hémorragie sang plaie coupure نزيف دم جرح ضغط ضماد");
        s.put("burns",
                "burn burns burnt scald fire hot water steam chemical burn brulure brûlure brule brulé brulee brûlé حرق حروق محروق نار سخن ماء ساخن كيميائي");
        s.put("stroke",
                "stroke fast face arm speech avc clot brain attack paralysis slurred droop weakness accident vasculaire جلطة دماغية فالج وجه كلام ذراع ضعف شلل");
        s.put("seizure",
                "seizure fit convulsion epilepsy shaking crise convulsive epilepsie épilepsie convulsion تشنج نوبة صرع رجفة");
        s.put("fire",
                "fire smoke flames burning evacuation trapped feu fumee fumée incendie evacuation évacuation حريق نار دخان محاصر اخلاء إخلاء");
        s.put("car",
                "car crash accident road traffic collision vehicle moto motorcycle voiture accident route circulation حادث سير سيارة تصادم طريق");
        s.put("electric",
                "electric shock electrocution current wire power electricity electrique électrique electrocution électrocution صعقة كهرباء كهربائي سلك تيار");
        s.put("breathing",
                "breathing breath asthma shortness wheeze suffocate respiratory inhaler dyspnea dyspnée respiration respirer asthme essoufflement نفس تنفس ضيق نفس ربو بخاخ");
        s.put("unconscious",
                "unconscious faint fainting passed out syncope coma not responding inconscient evanoui évanoui perte connaissance غائب عن الوعي اغماء إغماء فاقد الوعي");
        s.put("panic",
                "panic anxiety distress hyperventilation stress peur crise panique anxiete anxiété هلع خوف توتر قلق نفس سريع");
        s.put("allergy",
                "allergy allergic anaphylaxis epipen epinephrine swelling rash hives allergie allergique anaphylaxie adrénaline adrenaline حساسية تحسس طفح تورم انتفاخ ادرينالين");
        s.put("poison",
                "poison overdose intoxication drug pills chemical alcohol toxic poisoning surdose intoxication poison تسمم جرعة زائدة دواء حبوب مادة سامة");
        s.put("explosion",
                "explosion collapse blast earthquake building rubble trapped disaster explose effondrement explosion seisme séisme انهيار انفجار زلزال عالق ركام");
        s.put("drowning",
                "drown drowning water sea pool lake noyade noyer eau mer piscine غرق غريق بحر مسبح ماء");
        return s.containsKey(key) ? s.get(key) : "";
    }

    private static void add(LinkedHashSet<String> set, String value) {
        if (value != null && !value.trim().isEmpty()) set.add(value);
    }

    private static String join(List<String> values) {
        if (values == null) return "";
        StringBuilder sb = new StringBuilder();
        for (String v : values) {
            if (v != null) sb.append(v).append(' ');
        }
        return sb.toString();
    }

    private static String join(LinkedHashSet<String> values) {
        StringBuilder sb = new StringBuilder();
        for (String v : values) sb.append(v).append(' ');
        return sb.toString();
    }

    private static List<String> tokens(String input) {
        String n = normalize(input);
        String[] parts = n.split("\\s+");
        ArrayList<String> out = new ArrayList<>();
        for (String p : parts) if (p.length() > 0) out.add(p);
        return out;
    }

    public static String normalize(String input) {
        if (input == null) return "";
        String s = input.toLowerCase(Locale.ROOT);
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        s = s.replace('أ','ا').replace('إ','ا').replace('آ','ا').replace('ٱ','ا');
        s = s.replace('ى','ي').replace('ئ','ي').replace('ؤ','و').replace('ة','ه');
        s = s.replaceAll("[\\u064B-\\u065F\\u0670]", "");
        s = s.replaceAll("[^a-z0-9\\u0600-\\u06FF]+", " ");
        s = s.replaceAll("\\s+", " ").trim();
        return s;
    }

    private static double similarity(String a, String b) {
        if (a.equals(b)) return 1.0;
        int max = Math.max(a.length(), b.length());
        if (max == 0) return 1.0;
        int d = levenshtein(a, b);
        return 1.0 - ((double)d / (double)max);
    }

    private static int levenshtein(String a, String b) {
        int[] prev = new int[b.length() + 1];
        int[] cur = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) prev[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            cur[0] = i;
            char ca = a.charAt(i - 1);
            for (int j = 1; j <= b.length(); j++) {
                int cost = ca == b.charAt(j - 1) ? 0 : 1;
                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }
            int[] temp = prev; prev = cur; cur = temp;
        }
        return prev[b.length()];
    }
}
