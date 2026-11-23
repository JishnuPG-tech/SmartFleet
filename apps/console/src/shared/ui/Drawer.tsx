import React, { useEffect } from 'react';
import { CloseIcon } from '../icons';

interface DrawerProps {
  isOpen: boolean;
  onClose: () => void;
  header: React.ReactNode;
  children: React.ReactNode;
  width?: string;
  testId?: string;
}

export const Drawer: React.FC<DrawerProps> = ({
  isOpen,
  onClose,
  header,
  children,
  width,
  testId,
}) => {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <>
      <div className="scrim" onClick={onClose} aria-hidden="true" />
      <div
        className="drawer"
        style={width ? { width: `min(${width}, 100%)` } : undefined}
        role="dialog"
        aria-modal="true"
        data-testid={testId}
      >
        <header>
          {header}
          <button
            type="button"
            className="icon-btn"
            onClick={onClose}
            aria-label="Close drawer"
            data-testid="drawer:btn_close"
          >
            <CloseIcon />
          </button>
        </header>
        <div className="body">{children}</div>
      </div>
    </>
  );
};
