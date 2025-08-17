// src/i18n.js

import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';

// Çeviri dosyaları
import translationEN from './locales/en/translation.json';
import translationTR from './locales/tr/translation.json';

const resources = {
  en: {
    translation: translationEN,
  },
  tr: {
    translation: translationTR,
  },
};

i18n
  .use(LanguageDetector) // Tarayıcı dilini algıla
  .use(initReactI18next) // i18n'i React ile kullan
  .init({
    resources,
    fallbackLng: 'en', // Eğer mevcut dilin çevirisi yoksa kullanılacak dil
    debug: true, // Geliştirme aşamasında hataları görmek için
    interpolation: {
      escapeValue: false, // React XSS saldırılarına karşı zaten koruma sağlar
    },
  });

export default i18n;