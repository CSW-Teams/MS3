import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

import enTranslation from './multitenancyapp/locales/en.json';
import itTranslation from './multitenancyapp/locales/it.json';

const userLanguage = navigator.language;
const defaultLanguage = userLanguage.split('-')[0]; // Extract the language code


i18n.use(initReactI18next).init({
  resources: {
    en: {
      translation: enTranslation,
    },
    it: {
      translation: itTranslation,
    },
  },
  lng: defaultLanguage,
  fallbackLng: 'en',
  interpolation: {
    escapeValue: false,
  },
});

export default i18n;
