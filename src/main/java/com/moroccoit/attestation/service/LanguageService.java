package com.moroccoit.attestation.service;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageService {

    private static LanguageService instance;
    private Locale currentLocale;
    private ResourceBundle bundle;

    private LanguageService() {
        setLocale(new Locale("fr", "FR"));
    }

    public static synchronized LanguageService getInstance() {
        if (instance == null) {
            instance = new LanguageService();
        }
        return instance;
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        try {
            this.bundle = ResourceBundle.getBundle("i18n.messages", locale);
        } catch (Exception e) {
            System.err.println("Could not load bundle for " + locale + ", using fallback");
        }
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public String getString(String key) {
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getString(key);
        }
        return key;
    }

    public boolean isArabic() {
        return "ar".equalsIgnoreCase(currentLocale.getLanguage());
    }
}
