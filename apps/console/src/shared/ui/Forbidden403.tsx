import React from 'react';
import { ShieldIcon } from '../icons';
import { Link } from 'react-router-dom';

export const Forbidden403: React.FC = () => {
  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '60px 20px',
        textAlign: 'center',
      }}
    >
      <div
        style={{
          width: 64,
          height: 64,
          borderRadius: 16,
          background: 'var(--accent-soft)',
          color: 'var(--accent)',
          display: 'grid',
          placeItems: 'center',
          marginBottom: 20,
        }}
      >
        <ShieldIcon size={32} />
      </div>
      <h2 style={{ fontSize: 24, fontWeight: 800, marginBottom: 8 }}>403 – Access Restricted</h2>
      <p style={{ color: 'var(--muted)', maxWidth: 440, marginBottom: 24, lineHeight: 1.5 }}>
        This administrative view requires Super Admin privileges. Your current role as Dispatcher cannot modify company settings, app releases, or team credentials.
      </p>
      <Link to="/dashboard" className="btn primary">
        Return to Dashboard
      </Link>
    </div>
  );
};
