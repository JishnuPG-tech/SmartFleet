import React from 'react';
import { useTheme } from './useTheme';

interface ThemePopoverProps {
  isOpen: boolean;
  onClose: () => void;
}

export const ThemePopover: React.FC<ThemePopoverProps> = ({ isOpen }) => {
  const { accent, mode, setAccent, setMode, swatches } = useTheme();

  if (!isOpen) return null;

  return (
    <div className="pop" style={{ display: 'block', width: 260 }}>
      <h5>Appearance</h5>
      <div style={{ padding: '0 16px 12px', display: 'flex', gap: 10 }}>
        {swatches.map((s) => (
          <button
            key={s.id}
            className={`swatch ${accent === s.id ? 'on' : ''}`}
            style={{ background: s.color }}
            title={s.label}
            onClick={() => setAccent(s.id)}
            data-testid={`theme:swatch_${s.id}`}
          />
        ))}
      </div>
      <div style={{ padding: '0 16px 16px' }}>
        <div className="seg" style={{ width: '100%' }}>
          <button
            className={mode === 'light' ? 'on' : ''}
            style={{ flex: 1 }}
            onClick={() => setMode('light')}
            data-testid="theme:mode_light"
          >
            Light
          </button>
          <button
            className={mode === 'dark' ? 'on' : ''}
            style={{ flex: 1 }}
            onClick={() => setMode('dark')}
            data-testid="theme:mode_dark"
          >
            Dark
          </button>
          <button
            className={mode === 'auto' ? 'on' : ''}
            style={{ flex: 1 }}
            onClick={() => setMode('auto')}
            data-testid="theme:mode_auto"
          >
            Auto
          </button>
        </div>
      </div>
    </div>
  );
};
