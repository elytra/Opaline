package com.elytradev.opaline.util;

import java.util.List;

import com.elytradev.opaline.Opaline;
import com.google.common.base.Optional;
import com.ibm.icu.text.PluralRules;

/**
 * Helpful i18n utilities created by Unascribed
 */
public class C28n {

    public static String format(String key, Object... format) {
        return Opaline.proxy.i18nFormat(key, format);
    }

    public static boolean keyExists(String key) {
        return Opaline.proxy.i18nContains(key);
    }

    public static Optional<String> formatOptional(String key, Object... format) {
        return keyExists(key) ? Optional.of(Opaline.proxy.i18nFormat(key, format)) : Optional.absent();
    }

    public static String formatPlural(String key, long amount) {
        PluralRules pr = Opaline.proxy.getPluralRules();
        String s = pr.select(amount);
        if (keyExists(key+"."+s)) {
            return format(key+"."+s, amount);
        }
        return format(key+".other", amount);
    }

    public static void formatList(List<String> out, String key, Object... format) {
        int i = 0;
        while (keyExists(key + "." + i)) {
            out.add(C28n.format(key + "." + i));
            i++;
        }
    }

    // Hook point for JustEnoughCharacters for pinyin support
    // Which is to say: this method is designed as an ASM hook point. If you're
    // developing an IME mod, feel free to hack this method to bits.
    public static boolean contains(String haystack, String needle) {
        return haystack == null ? false : haystack.contains(needle);
    }

}