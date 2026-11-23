import React, { createContext, useContext, useState, useEffect } from 'react';

type Language = 'en' | 'ml';

interface LanguageContextType {
  language: Language;
  setLanguage: (lang: Language) => void;
  toggleLanguage: () => void;
  isMalayalam: boolean;
  t: (en: string, ml: string) => string;
}

const LanguageContext = createContext<LanguageContextType>({
  language: 'en',
  setLanguage: () => {},
  toggleLanguage: () => {},
  isMalayalam: false,
  t: (en) => en,
});

export const useLanguage = () => useContext(LanguageContext);

export const LanguageProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [language, setLanguageState] = useState<Language>(() => {
    return (localStorage.getItem('tc_lang') as Language) || 'en';
  });

  useEffect(() => {
    localStorage.setItem('tc_lang', language);
    document.documentElement.lang = language;
  }, [language]);

  const setLanguage = (lang: Language) => {
    setLanguageState(lang);
  };

  const toggleLanguage = () => {
    setLanguageState((prev) => (prev === 'en' ? 'ml' : 'en'));
  };

  // Helper: ONLY returns Malayalam if the user has chosen Malayalam. Default is strictly English.
  const t = (en: string, ml: string): string => {
    return language === 'ml' ? ml : en;
  };

  return (
    <LanguageContext.Provider
      value={{
        language,
        setLanguage,
        toggleLanguage,
        isMalayalam: language === 'ml',
        t,
      }}
    >
      {children}
    </LanguageContext.Provider>
  );
};
