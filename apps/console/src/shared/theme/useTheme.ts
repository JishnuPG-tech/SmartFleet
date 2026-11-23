import { useState, useEffect, useCallback } from 'react';

export type ThemeAccent = 'indigo' | 'emerald' | 'graphite' | 'sunset';
export type ThemeMode = 'light' | 'dark' | 'auto';

export interface ThemeConfig {
  accent: ThemeAccent;
  mode: ThemeMode;
  resolvedMode: 'light' | 'dark';
}

const STORAGE_KEY_THEME = 'tc_theme';
const STORAGE_KEY_MODE = 'tc_mode';

export const THEME_SWATCHES: { id: ThemeAccent; label: string; color: string }[] = [
  { id: 'indigo', label: 'Indigo', color: '#4f46e5' },
  { id: 'emerald', label: 'Emerald', color: '#047857' },
  { id: 'graphite', label: 'Graphite', color: '#27272a' },
  { id: 'sunset', label: 'Sunset', color: '#d9480f' },
];

export function useTheme() {
  const [accent, setAccentState] = useState<ThemeAccent>(() => {
    return (localStorage.getItem(STORAGE_KEY_THEME) as ThemeAccent) || 'indigo';
  });

  const [mode, setModeState] = useState<ThemeMode>(() => {
    return (localStorage.getItem(STORAGE_KEY_MODE) as ThemeMode) || 'auto';
  });

  const [resolvedMode, setResolvedMode] = useState<'light' | 'dark'>('light');

  const applyTheme = useCallback((currentAccent: ThemeAccent, currentMode: ThemeMode) => {
    const isDark =
      currentMode === 'dark' ||
      (currentMode === 'auto' && window.matchMedia('(prefers-color-scheme: dark)').matches);

    const actualMode = isDark ? 'dark' : 'light';
    setResolvedMode(actualMode);

    document.documentElement.dataset.theme = currentAccent;
    document.documentElement.dataset.mode = actualMode;
  }, []);

  useEffect(() => {
    applyTheme(accent, mode);

    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    const handleChange = () => {
      if (mode === 'auto') {
        applyTheme(accent, 'auto');
      }
    };

    mediaQuery.addEventListener('change', handleChange);
    return () => mediaQuery.removeEventListener('change', handleChange);
  }, [accent, mode, applyTheme]);

  const setAccent = (newAccent: ThemeAccent) => {
    setAccentState(newAccent);
    localStorage.setItem(STORAGE_KEY_THEME, newAccent);
    applyTheme(newAccent, mode);
  };

  const setMode = (newMode: ThemeMode) => {
    setModeState(newMode);
    localStorage.setItem(STORAGE_KEY_MODE, newMode);
    applyTheme(accent, newMode);
  };

  return {
    accent,
    mode,
    resolvedMode,
    setAccent,
    setMode,
    swatches: THEME_SWATCHES,
  };
}
