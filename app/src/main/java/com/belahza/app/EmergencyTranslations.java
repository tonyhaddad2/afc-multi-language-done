package com.belahza.app;

import java.util.Arrays;
import java.util.List;

public final class EmergencyTranslations {
    private EmergencyTranslations() {}

    public static String title(String language, String key, String english, String arabic) {
        if (LanguageManager.AR.equals(language)) return arabic;
        if (!LanguageManager.FR.equals(language)) return english;
        switch (key) {
            case "cardiac": return "Arrêt cardiaque / RCP";
            case "choking": return "Étouffement / Heimlich";
            case "bleeding": return "Saignement sévère";
            case "burns": return "Brûlures";
            case "stroke": return "Signes d’AVC";
            case "seizure": return "Crise convulsive";
            case "fire": return "Feu / fumée";
            case "car": return "Accident de la route";
            case "electric": return "Électrocution";
            case "breathing": return "Difficulté respiratoire";
            case "unconscious": return "Personne inconsciente";
            case "panic": return "Panique / détresse";
            case "allergy": return "Allergie sévère";
            case "poison": return "Intoxication / surdose";
            case "explosion": return "Explosion / effondrement";
            case "drowning": return "Noyade";
            default: return english;
        }
    }

    public static String summary(String language, String key, String english) {
        if (LanguageManager.FR.equals(language)) {
            switch (key) {
                case "cardiac": return "Inconscient et ne respire pas normalement. Appelle et commence les compressions.";
                case "choking": return "Ne peut pas parler, respirer ou tousser efficacement.";
                case "bleeding": return "Sang abondant ou qui ne s’arrête pas. Applique une pression ferme.";
                case "burns": return "Refroidis la brûlure, couvre légèrement et appelle si elle est grave.";
                case "stroke": return "Visage affaissé, bras faible, parole anormale : le temps compte.";
                case "seizure": return "Protège la personne, chronomètre et ne la retiens pas.";
                case "fire": return "Reste bas sous la fumée, sors et appelle les secours.";
                case "car": return "Sécurise la scène, appelle et ne déplace pas sauf danger immédiat.";
                case "electric": return "Coupe le courant d’abord. Ne touche pas avant que ce soit sûr.";
                case "breathing": return "Installe la personne assise, aide avec son traitement et appelle si c’est grave.";
                case "unconscious": return "Vérifie la respiration. Position latérale si elle respire, RCP sinon.";
                case "panic": return "Reste calme, guide une respiration lente et appelle s’il existe un danger.";
                case "allergy": return "Gonflement, sifflement, éruption ou malaise peuvent indiquer une anaphylaxie.";
                case "poison": return "Identifie la substance, appelle et ne fais pas vomir sauf consigne.";
                case "explosion": return "Éloigne-toi du danger, partage la position et signale si tu es bloqué.";
                case "drowning": return "Sors la personne en sécurité, vérifie la respiration et commence la RCP si nécessaire.";
            }
        }
        if (LanguageManager.AR.equals(language)) {
            switch (key) {
                case "cardiac": return "فاقد الوعي ولا يتنفس طبيعياً. اتصل وابدأ الضغطات الصدرية.";
                case "choking": return "لا يستطيع الكلام أو التنفس أو السعال بقوة.";
                case "bleeding": return "نزيف غزير أو لا يتوقف. اضغط بقوة وثبات.";
                case "burns": return "برّد الحرق وغطّه بخفة واتصل إذا كان خطيراً.";
                case "stroke": return "ميلان الوجه أو ضعف الذراع أو اضطراب الكلام: الوقت مهم.";
                case "seizure": return "احمِ الشخص، احسب الوقت ولا تقيّده.";
                case "fire": return "ابقَ منخفضاً تحت الدخان، اخرج واتصل بالمساعدة.";
                case "car": return "أمّن المكان واتصل ولا تحرّك المصاب إلا عند خطر مباشر.";
                case "electric": return "اقطع الكهرباء أولاً. لا تلمس قبل أن يصبح المكان آمناً.";
                case "breathing": return "أجلس الشخص وساعده بدوائه واتصل إذا كانت الحالة شديدة.";
                case "unconscious": return "افحص التنفس. وضعية الإفاقة إذا يتنفس وCPR إذا لا.";
                case "panic": return "ابقَ هادئاً وساعده على التنفس ببطء واتصل عند وجود خطر.";
                case "allergy": return "التورم أو الصفير أو الطفح أو الإغماء قد يعني حساسية مفرطة.";
                case "poison": return "حدّد المادة واتصل ولا تجعله يتقيأ إلا بتعليمات.";
                case "explosion": return "ابتعد عن الخطر وشارك موقعك وأرسل إشارة إذا كنت عالقاً.";
                case "drowning": return "أخرجه بأمان وافحص التنفس وابدأ CPR إذا لزم.";
            }
        }
        return english;
    }

    public static List<String> steps(String language, EmergencyData.Emergency emergency) {
        if (LanguageManager.EN.equals(language)) return emergency.steps;
        if (LanguageManager.FR.equals(language)) return frSteps(emergency.key, emergency.steps);
        return arSteps(emergency.key, emergency.steps);
    }

    public static List<String> donts(String language, EmergencyData.Emergency emergency) {
        if (LanguageManager.EN.equals(language)) return emergency.donts;
        if (LanguageManager.FR.equals(language)) return frDonts(emergency.key, emergency.donts);
        return arDonts(emergency.key, emergency.donts);
    }

    private static List<String> frSteps(String key, List<String> fallback) {
        switch (key) {
            case "cardiac": return Arrays.asList(
                    "Vérifie que la zone est sûre et essaie de réveiller la personne.",
                    "Appelle le 140 ou le 112 et demande un défibrillateur.",
                    "Si elle ne respire pas normalement, place les mains au centre de la poitrine.",
                    "Comprime fort et vite, bras tendus et épaules au-dessus des mains.",
                    "Utilise le défibrillateur dès qu’il arrive et suis ses consignes.",
                    "Continue jusqu’à l’arrivée des secours ou au retour d’une respiration normale.");
            case "choking": return Arrays.asList(
                    "Demande si la personne s’étouffe et encourage une toux forte si possible.",
                    "Si elle ne peut ni parler ni respirer, appelle les secours.",
                    "Donne cinq claques fermes entre les omoplates.",
                    "Donne cinq compressions abdominales au-dessus du nombril et sous les côtes.",
                    "Alterne cinq claques et cinq compressions jusqu’à expulsion ou perte de connaissance.",
                    "Si elle devient inconsciente, allonge-la avec précaution et commence la RCP.");
            case "bleeding": return Arrays.asList(
                    "Appelle le 140 ou le 112, ou demande à quelqu’un de le faire.",
                    "Expose seulement ce qui est nécessaire pour trouver la source.",
                    "Appuie fermement et sans interruption avec un tissu propre.",
                    "Ajoute du tissu s’il est imbibé sans retirer la première couche.",
                    "Utilise un garrot uniquement si tu es formé et si l’hémorragie d’un membre menace la vie.",
                    "Garde la personne immobile et au chaud.");
            case "burns": return Arrays.asList(
                    "Éloigne la personne de la chaleur, flamme, produit chimique ou électricité.",
                    "Refroidis sous l’eau courante fraîche quand c’est possible.",
                    "Retire bijoux et objets serrés avant le gonflement.",
                    "Couvre avec un pansement propre non collant.",
                    "Appelle pour une brûlure profonde, étendue, au visage, mains, organes génitaux, inhalation de fumée ou chez un enfant.");
            case "stroke": return Arrays.asList(
                    "Utilise FAST : visage, bras, parole, temps.",
                    "Appelle immédiatement le 140 ou le 112 et note l’heure de début.",
                    "Laisse la personne au repos, tête légèrement relevée si confortable.",
                    "Ne donne ni nourriture, ni boisson, ni médicament sauf consigne.",
                    "Surveille la respiration et prépare-toi à commencer la RCP.");
            case "seizure": return Arrays.asList(
                    "Éloigne les objets dangereux et protège la tête.",
                    "Commence à chronométrer la crise.",
                    "Ne retiens pas la personne et ne mets rien dans sa bouche.",
                    "Après les secousses, vérifie la respiration et tourne-la sur le côté si possible.",
                    "Appelle si la crise dépasse cinq minutes, se répète, s’il y a blessure, grossesse, diabète ou première crise.");
            case "fire": return Arrays.asList(
                    "Alerte les personnes proches et sors immédiatement.",
                    "Reste bas sous la fumée et couvre bouche et nez si possible.",
                    "Ferme les portes derrière toi.",
                    "Appelle le 125, 175 ou 112 une fois en sécurité.",
                    "Si tu es bloqué, bouche les ouvertures, signale-toi et partage ta position.");
            case "car": return Arrays.asList(
                    "Mets-toi en sécurité et active les feux de détresse si possible.",
                    "Appelle le 112 ou le 140 et donne la position exacte et le nombre de blessés.",
                    "Ne déplace pas une possible blessure de la colonne sauf feu ou danger immédiat.",
                    "Contrôle le saignement avec une pression ferme.",
                    "Garde les blessés immobiles et au chaud.");
            case "electric": return Arrays.asList(
                    "Ne touche pas la personne tant qu’elle est reliée au courant.",
                    "Coupe le courant ou débranche uniquement si c’est sûr.",
                    "Appelle pour choc grave, brûlures ou perte de connaissance.",
                    "Une fois la zone sûre, vérifie la respiration et commence la RCP si nécessaire.",
                    "Refroidis les brûlures et couvre-les légèrement.");
            case "breathing": return Arrays.asList(
                    "Aide la personne à s’asseoir et desserre les vêtements serrés.",
                    "Demande si elle a un inhalateur ou traitement prescrit et aide-la à l’utiliser.",
                    "Garde l’environnement calme et aéré.",
                    "Appelle le 140 ou le 112 si c’est sévère, lèvres bleues, douleur thoracique ou aucune amélioration.",
                    "Prépare-toi à commencer la RCP si la respiration s’arrête.");
            case "unconscious": return Arrays.asList(
                    "Vérifie la sécurité et essaie de réveiller par la voix et une légère stimulation de l’épaule.",
                    "Appelle immédiatement le 140 ou le 112.",
                    "Ouvre les voies respiratoires et vérifie la respiration normale.",
                    "Si elle respire normalement, place-la sur le côté et surveille.",
                    "Si elle ne respire pas normalement, commence la RCP et demande un défibrillateur.");
            case "panic": return Arrays.asList(
                    "Va dans un endroit plus calme et sûr si possible.",
                    "Parle doucement et reste avec la personne.",
                    "Guide la respiration : inspire quatre secondes, expire six secondes.",
                    "Utilise l’ancrage : cinq choses vues, quatre senties, trois entendues.",
                    "Appelle en cas de douleur thoracique, malaise, risque d’automutilation ou violence.");
            case "allergy": return Arrays.asList(
                    "Appelle le 140 ou le 112 s’il y a difficulté respiratoire, gonflement, malaise ou aggravation rapide.",
                    "Aide à utiliser l’auto-injecteur d’adrénaline prescrit s’il est disponible.",
                    "Laisse la personne assise si elle respire mieux ou couchée si elle se sent faible.",
                    "Surveille la respiration et prépare la RCP si elle devient inconsciente.",
                    "Une seconde dose peut être nécessaire si elle est prescrite et que les symptômes persistent.");
            case "poison": return Arrays.asList(
                    "Appelle le 140 ou le 112 et indique la substance, la quantité et l’heure.",
                    "Garde le contenant, l’étiquette ou les comprimés pour les secours.",
                    "Si la personne est inconsciente mais respire, place-la sur le côté.",
                    "Si elle ne respire pas normalement, commence la RCP.",
                    "Pour un produit sur la peau, retire les vêtements contaminés et rince si c’est sûr.");
            case "explosion": return Arrays.asList(
                    "Éloigne-toi des bâtiments instables, vitres, fumée et autres dangers.",
                    "Appelle le 125, 112 ou 140 et partage la position exacte.",
                    "Contrôle une hémorragie grave avec une pression ferme.",
                    "Si tu es coincé, protège bouche et nez, frappe ou signale-toi et économise la batterie.",
                    "Ne déplace pas de lourds débris sauf si c’est sûr et indispensable.");
            case "drowning": return Arrays.asList(
                    "N’entre pas dans une eau dangereuse sans formation ; tends un objet ou lance une aide flottante.",
                    "Appelle le 140 ou le 112 dès que possible.",
                    "Une fois sortie de l’eau, vérifie la conscience et la respiration.",
                    "Si elle ne respire pas normalement, commence la RCP et utilise un défibrillateur si disponible.",
                    "Garde la personne au chaud et surveille-la même si elle semble aller mieux.");
            default: return fallback;
        }
    }

    private static List<String> arSteps(String key, List<String> fallback) {
        switch (key) {
            case "cardiac": return Arrays.asList(
                    "تأكد أن المكان آمن وحاول إيقاظ الشخص.",
                    "اتصل بـ140 أو 112 واطلب جهاز AED.",
                    "إذا لم يكن يتنفس طبيعياً، ضع يديك في منتصف الصدر.",
                    "اضغط بقوة وسرعة مع ذراعين مستقيمين وكتفين فوق اليدين.",
                    "استخدم جهاز AED فور وصوله واتبع التعليمات.",
                    "استمر حتى وصول المختصين أو عودة التنفس الطبيعي.");
            case "choking": return Arrays.asList(
                    "اسأل إن كان يختنق وشجعه على السعال القوي إذا استطاع.",
                    "إذا لم يستطع الكلام أو التنفس، اتصل بالمساعدة.",
                    "أعطِ خمس ضربات قوية بين لوحي الكتف.",
                    "أعطِ خمس ضغطات بطنية فوق السرة وتحت الأضلاع.",
                    "كرر خمس ضربات وخمس ضغطات حتى خروج الجسم أو فقدان الوعي.",
                    "إذا فقد الوعي، أنزله بحذر وابدأ CPR.");
            case "bleeding": return Arrays.asList(
                    "اتصل بـ140 أو 112 أو اطلب من شخص آخر الاتصال.",
                    "اكشف فقط ما يلزم لتحديد مصدر النزيف.",
                    "اضغط بثبات وقوة بقطعة قماش نظيفة.",
                    "أضف قماشاً إذا ابتل ولا تزل الطبقة الأولى.",
                    "استخدم رباطاً ضاغطاً فقط إذا كنت مدرباً وكان نزيف الطرف يهدد الحياة.",
                    "أبقِ الشخص ثابتاً ودافئاً.");
            case "burns": return Arrays.asList(
                    "أبعد الشخص عن الحرارة أو النار أو المادة الكيميائية أو الكهرباء.",
                    "برّد الحرق بماء جارٍ فاتر أو بارد عندما يمكن.",
                    "أزل الحلي والأشياء الضيقة قبل التورم.",
                    "غطِّ الحرق بضماد نظيف غير لاصق.",
                    "اتصل للحروق الكبيرة أو العميقة أو الوجه أو اليدين أو الأعضاء الحساسة أو استنشاق الدخان أو الأطفال.");
            case "stroke": return Arrays.asList(
                    "استخدم FAST: الوجه، الذراعان، الكلام، الوقت.",
                    "اتصل فوراً بـ140 أو 112 وسجل وقت بدء الأعراض.",
                    "دع الشخص يرتاح وارفع الرأس قليلاً إذا كان مرتاحاً.",
                    "لا تعطِ طعاماً أو شراباً أو دواءً إلا بتعليمات.",
                    "راقب التنفس واستعد لبدء CPR عند الحاجة.");
            case "seizure": return Arrays.asList(
                    "أبعد الأشياء الخطرة واحمِ الرأس.",
                    "ابدأ بحساب مدة النوبة.",
                    "لا تقيّد الشخص ولا تضع شيئاً في فمه.",
                    "بعد توقف التشنج افحص التنفس وضعه على جانبه إذا أمكن.",
                    "اتصل إذا استمرت أكثر من خمس دقائق أو تكررت أو توجد إصابة أو حمل أو سكري أو كانت أول نوبة.");
            case "fire": return Arrays.asList(
                    "نبّه القريبين واخرج فوراً.",
                    "ابقَ منخفضاً تحت الدخان وغطِّ الفم والأنف إن أمكن.",
                    "أغلق الأبواب خلفك.",
                    "اتصل بـ125 أو 175 أو 112 بعد الوصول إلى مكان آمن.",
                    "إذا كنت عالقاً، أغلق الفتحات وأرسل إشارة وشارك موقعك.");
            case "car": return Arrays.asList(
                    "انتقل إلى مكان آمن وشغّل أضواء الخطر إن أمكن.",
                    "اتصل بـ112 أو 140 وأعطِ الموقع الدقيق وعدد المصابين.",
                    "لا تحرّك إصابة محتملة في العمود الفقري إلا عند حريق أو خطر مباشر.",
                    "أوقف النزيف بالضغط القوي.",
                    "أبقِ المصابين ثابتين ودافئين.");
            case "electric": return Arrays.asList(
                    "لا تلمس الشخص ما دام متصلاً بالكهرباء.",
                    "اقطع التيار أو افصل القابس فقط إذا كان ذلك آمناً.",
                    "اتصل عند صعقة قوية أو حروق أو فقدان الوعي.",
                    "بعد أمان المكان افحص التنفس وابدأ CPR إذا لزم.",
                    "برّد الحروق وغطِّها بخفة.");
            case "breathing": return Arrays.asList(
                    "ساعد الشخص على الجلوس وأرخِ الملابس الضيقة.",
                    "اسأله عن بخاخ أو دواء موصوف وساعده على استخدامه.",
                    "حافظ على الهدوء والتهوية.",
                    "اتصل بـ140 أو 112 إذا كانت الحالة شديدة أو الشفاه زرقاء أو يوجد ألم صدر أو لا تحسن.",
                    "استعد لبدء CPR إذا توقف التنفس.");
            case "unconscious": return Arrays.asList(
                    "تأكد من الأمان وحاول إيقاظه بالصوت ولمسة خفيفة على الكتف.",
                    "اتصل فوراً بـ140 أو 112.",
                    "افتح مجرى التنفس وافحص التنفس الطبيعي.",
                    "إذا كان يتنفس طبيعياً، ضعه على جانبه وراقبه.",
                    "إذا لم يكن يتنفس طبيعياً، ابدأ CPR واطلب جهاز AED.");
            case "panic": return Arrays.asList(
                    "انتقل إلى مكان أهدأ وأكثر أماناً إن أمكن.",
                    "تحدث بهدوء وابقَ مع الشخص.",
                    "وجّه التنفس: شهيق أربع ثوانٍ وزفير ست ثوانٍ.",
                    "استخدم التثبيت: خمس أشياء يراها، أربع يلمسها، ثلاث يسمعها.",
                    "اتصل عند ألم الصدر أو الإغماء أو خطر إيذاء النفس أو العنف.");
            case "allergy": return Arrays.asList(
                    "اتصل بـ140 أو 112 عند صعوبة التنفس أو التورم أو الإغماء أو التدهور السريع.",
                    "ساعده على استخدام قلم الإبينفرين الموصوف إن توفر.",
                    "دعه يجلس إذا كان التنفس أسهل أو يستلقي إذا شعر بالإغماء.",
                    "راقب التنفس واستعد لـCPR إذا فقد الوعي.",
                    "قد تلزم جرعة ثانية إذا كانت موصوفة واستمرت الأعراض.");
            case "poison": return Arrays.asList(
                    "اتصل بـ140 أو 112 واذكر المادة والكمية والوقت.",
                    "احتفظ بالعبوة أو الملصق أو الحبوب للمسعفين.",
                    "إذا كان فاقد الوعي لكنه يتنفس، ضعه على جانبه.",
                    "إذا لم يتنفس طبيعياً، ابدأ CPR.",
                    "إذا كانت مادة على الجلد، أزل الملابس الملوثة واشطف بأمان.");
            case "explosion": return Arrays.asList(
                    "ابتعد عن المباني غير المستقرة والزجاج والدخان والأخطار الثانوية.",
                    "اتصل بـ125 أو 112 أو 140 وشارك الموقع الدقيق.",
                    "أوقف النزيف الخطير بالضغط.",
                    "إذا كنت عالقاً، غطِّ الفم والأنف واضرب أو أرسل إشارة ووفر البطارية.",
                    "لا تحرّك حطاماً ثقيلاً إلا إذا كان آمناً وضرورياً.");
            case "drowning": return Arrays.asList(
                    "لا تدخل ماءً خطراً دون تدريب؛ مدّ شيئاً أو ارمِ وسيلة طفو.",
                    "اتصل بـ140 أو 112 بأسرع وقت.",
                    "بعد إخراجه افحص الاستجابة والتنفس.",
                    "إذا لم يتنفس طبيعياً، ابدأ CPR واستخدم AED إذا توفر.",
                    "حافظ على دفئه وراقبه حتى لو بدا أفضل.");
            default: return fallback;
        }
    }

    private static List<String> frDonts(String key, List<String> fallback) {
        switch (key) {
            case "cardiac": return Arrays.asList("Ne retarde pas l’appel aux secours.","N’interromps pas longtemps les compressions.","Ne reste pas appuyé sur la poitrine entre les compressions.");
            case "choking": return Arrays.asList("Ne balaie pas la bouche avec les doigts à l’aveugle.","Ne donne pas d’eau à une personne qui ne peut pas respirer.","N’utilise pas les compressions abdominales d’adulte sur un nourrisson.");
            case "bleeding": return Arrays.asList("Ne retire pas un objet planté.","Ne soulève pas constamment le tissu pour vérifier.","Ne donne ni nourriture ni boisson.");
            case "burns": return Arrays.asList("N’applique pas de glace directement.","Ne perce pas les cloques.","N’applique ni beurre, huile, dentifrice ni poudre.");
            case "stroke": return Arrays.asList("N’attends pas que les symptômes passent.","Ne conduis pas si les secours peuvent venir.","Ne donne pas d’aspirine sans consigne.");
            case "seizure": return Arrays.asList("Ne retiens pas la personne.","Ne mets rien dans sa bouche.","Ne donne rien à manger ou boire avant un réveil complet.");
            case "fire": return Arrays.asList("N’utilise pas l’ascenseur.","Ne retourne pas chercher des objets.","N’ouvre pas une porte chaude.");
            case "car": return Arrays.asList("Ne reste pas sur la chaussée.","Ne retire pas un casque sauf obstruction des voies respiratoires et formation.","Ne donne ni nourriture ni boisson.");
            case "electric": return Arrays.asList("N’utilise pas de métal pour déplacer un câble.","Ne touche pas une ligne électrique tombée.","N’ignore pas une douleur thoracique après la décharge.");
            case "breathing": return Arrays.asList("Ne force pas la personne à s’allonger.","Ne donne pas le médicament de quelqu’un d’autre.","Ne retarde pas l’appel si l’état s’aggrave.");
            case "unconscious": return Arrays.asList("Ne donne ni nourriture, ni boisson, ni médicament.","Ne secoue pas violemment.","Ne laisse pas la personne seule.");
            case "panic": return Arrays.asList("Ne juge pas et ne discute pas agressivement.","N’entoure pas la personne de trop de monde.","N’ignore pas une menace d’automutilation.");
            case "allergy": return Arrays.asList("Ne fais pas marcher la personne.","Ne retarde pas l’adrénaline prescrite.","Ne donne pas de médicament oral si elle ne peut pas avaler.");
            case "poison": return Arrays.asList("Ne provoque pas de vomissement sauf consigne.","Ne donne ni nourriture, ni boisson, ni alcool.","N’attends pas les symptômes après une exposition dangereuse.");
            case "explosion": return Arrays.asList("Ne retourne pas dans un bâtiment endommagé.","N’allume pas de flamme s’il y a une odeur de gaz.","Ne déplace pas un blessé grave sauf danger immédiat.");
            case "drowning": return Arrays.asList("Ne deviens pas une deuxième victime.","Ne retarde pas la RCP pour vider l’eau.","N’ignore pas une toux ou difficulté respiratoire après le sauvetage.");
            default: return fallback;
        }
    }

    private static List<String> arDonts(String key, List<String> fallback) {
        switch (key) {
            case "cardiac": return Arrays.asList("لا تؤخر الاتصال بالطوارئ.","لا توقف الضغطات لفترات طويلة.","لا تبقَ ضاغطاً على الصدر بين الضغطات.");
            case "choking": return Arrays.asList("لا تدخل أصابعك عشوائياً في الفم.","لا تعطِ ماءً لمن لا يستطيع التنفس.","لا تستخدم ضغطات بطنية للبالغين على الرضيع.");
            case "bleeding": return Arrays.asList("لا تزل جسماً مغروساً.","لا ترفع القماش مراراً لفحص الجرح.","لا تعطِ طعاماً أو شراباً.");
            case "burns": return Arrays.asList("لا تضع الثلج مباشرة.","لا تفقع الفقاعات.","لا تضع زبدة أو زيتاً أو معجون أسنان أو مسحوقاً.");
            case "stroke": return Arrays.asList("لا تنتظر زوال الأعراض.","لا تقُد السيارة إذا أمكن وصول الإسعاف.","لا تعطِ أسبرين دون تعليمات.");
            case "seizure": return Arrays.asList("لا تمسك الشخص بقوة.","لا تضع شيئاً في فمه.","لا تعطِ طعاماً أو شراباً قبل أن يستيقظ تماماً.");
            case "fire": return Arrays.asList("لا تستخدم المصعد.","لا تعد لجلب الأغراض.","لا تفتح باباً ساخناً.");
            case "car": return Arrays.asList("لا تقف في مسار السيارات.","لا تزل الخوذة إلا إذا انسد مجرى التنفس وكنت مدرباً.","لا تعطِ طعاماً أو شراباً.");
            case "electric": return Arrays.asList("لا تستخدم معدناً لتحريك الأسلاك.","لا تلمس خط كهرباء ساقطاً.","لا تتجاهل ألم الصدر بعد الصعقة.");
            case "breathing": return Arrays.asList("لا تجبره على الاستلقاء.","لا تعطِ دواء شخص آخر.","لا تؤخر الاتصال إذا ساءت الحالة.");
            case "unconscious": return Arrays.asList("لا تعطِ طعاماً أو شراباً أو دواءً.","لا تهزه بعنف.","لا تتركه وحده.");
            case "panic": return Arrays.asList("لا تسخر ولا تجادل بعنف.","لا تحاصر الشخص بالناس.","لا تتجاهل تهديد إيذاء النفس.");
            case "allergy": return Arrays.asList("لا تجعل الشخص يمشي.","لا تؤخر الإبينفرين الموصوف.","لا تعطِ دواءً فموياً إذا لم يستطع البلع.");
            case "poison": return Arrays.asList("لا تحفز القيء إلا بتعليمات.","لا تعطِ طعاماً أو شراباً أو كحولاً.","لا تنتظر ظهور الأعراض بعد تعرض خطير.");
            case "explosion": return Arrays.asList("لا تعد إلى مبنى متضرر.","لا تشعل ناراً قرب رائحة الغاز.","لا تحرّك المصاب الخطير إلا عند خطر مباشر.");
            case "drowning": return Arrays.asList("لا تصبح ضحية ثانية.","لا تؤخر CPR لتصريف الماء.","لا تتجاهل السعال أو ضيق التنفس بعد الإنقاذ.");
            default: return fallback;
        }
    }
}
