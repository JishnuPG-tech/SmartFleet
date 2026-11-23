import React, { useState } from 'react';
import { fleetStore } from '../../shared/api/client';
import { useToast } from '../../shared/ui/Toast';
import { ShieldIcon } from '../../shared/icons';

export const SettingsView: React.FC = () => {
  const { showToast } = useToast();
  const currentUser = fleetStore.currentUser;

  const [name, setName] = useState(currentUser?.name || 'Anil Kumar');
  const [phone, setPhone] = useState('+91 98470 11000');
  const [lang, setLang] = useState('en');
  const [is2faEnabled, setIs2faEnabled] = useState(false);
  const [sosSound, setSosSound] = useState(true);
  const [delaySound, setDelaySound] = useState(true);

  const handleSaveProfile = (e: React.FormEvent) => {
    e.preventDefault();
    if (currentUser) {
      currentUser.name = name;
      fleetStore.notify();
    }
    showToast('Profile configuration saved.');
  };

  const handleToggle2fa = () => {
    setIs2faEnabled(!is2faEnabled);
    showToast(!is2faEnabled ? '2FA TOTP authenticator enabled' : '2FA disabled');
  };

  const handleSignOutAll = () => {
    showToast('All other browser sessions revoked.');
  };

  return (
    <div className="view-anim" style={{ maxWidth: 880 }}>
      {/* Profile Section */}
      <div className="card" style={{ padding: 24, marginBottom: 20 }}>
        <h3 style={{ fontSize: 16, fontWeight: 800, marginBottom: 4 }}>Profile &amp; Regional Preferences</h3>
        <p style={{ color: 'var(--muted)', fontSize: 12.5, marginBottom: 18 }}>
          Personal identity and display language settings across console and mobile app
        </p>

        <form onSubmit={handleSaveProfile} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14 }}>
          <label className="fld">
            <span>Display Name</span>
            <input value={name} onChange={(e) => setName(e.target.value)} required />
          </label>

          <label className="fld">
            <span>Contact Phone</span>
            <input value={phone} onChange={(e) => setPhone(e.target.value)} required />
          </label>

          <label className="fld">
            <span>Primary Language</span>
            <select value={lang} onChange={(e) => setLang(e.target.value)}>
              <option value="en">English (India)</option>
              <option value="ml">മലയാളം (Malayalam)</option>
            </select>
          </label>

          <label className="fld">
            <span>Assigned Role</span>
            <input value={currentUser?.roleTitle || 'Super Admin'} disabled style={{ opacity: 0.7 }} />
          </label>

          <div className="mf">
            <button type="submit" className="btn primary">
              Save Profile Settings
            </button>
          </div>
        </form>
      </div>

      {/* Two-Factor Authentication (2FA) */}
      <div className="card" style={{ padding: 24, marginBottom: 20 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 16 }}>
          <div>
            <h3 style={{ fontSize: 16, fontWeight: 800, marginBottom: 4 }}>Two-Factor Authentication (TOTP)</h3>
            <p style={{ color: 'var(--muted)', fontSize: 12.5 }}>
              Protect administrative operations with Google Authenticator or hardware keys
            </p>
          </div>

          <button
            className={`btn ${is2faEnabled ? 'danger' : 'primary'}`}
            onClick={handleToggle2fa}
            data-testid="settings:btn_toggle_2fa"
          >
            <ShieldIcon size={14} /> {is2faEnabled ? 'Disable 2FA' : 'Enable 2FA'}
          </button>
        </div>

        {is2faEnabled && (
          <div style={{ background: 'var(--surface-2)', padding: 16, borderRadius: 12, border: '1px solid var(--border)' }}>
            <p style={{ fontSize: 13, fontWeight: 700, marginBottom: 8 }}>Recovery Backup Codes (Single Use)</p>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 8, fontFamily: 'monospace', fontSize: 12 }}>
              <span>8492-1049</span>
              <span>3910-4821</span>
              <span>9184-2041</span>
              <span>1048-5921</span>
              <span>4910-3841</span>
              <span>2049-1849</span>
              <span>7194-0194</span>
              <span>6194-3910</span>
            </div>
          </div>
        )}
      </div>

      {/* Alert Audio Preferences */}
      <div className="card" style={{ padding: 24, marginBottom: 20 }}>
        <h3 style={{ fontSize: 16, fontWeight: 800, marginBottom: 4 }}>Incident Audio &amp; Desktop Chimes</h3>
        <p style={{ color: 'var(--muted)', fontSize: 12.5, marginBottom: 16 }}>
          Audible alerts for high-consequence operational triggers
        </p>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          <label style={{ display: 'flex', alignItems: 'center', gap: 10, cursor: 'pointer' }}>
            <input type="checkbox" checked={sosSound} onChange={(e) => setSosSound(e.target.checked)} />
            <span>Play high-pitch siren on SOS Critical emergency panic triggers</span>
          </label>

          <label style={{ display: 'flex', alignItems: 'center', gap: 10, cursor: 'pointer' }}>
            <input type="checkbox" checked={delaySound} onChange={(e) => setDelaySound(e.target.checked)} />
            <span>Play warning chime when active shipment ETA exceeds milestone threshold by &gt; 30m</span>
          </label>
        </div>
      </div>

      {/* Active Sessions Watchdog */}
      <div className="card" style={{ padding: 24 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 16 }}>
          <div>
            <h3 style={{ fontSize: 16, fontWeight: 800, marginBottom: 4 }}>Active Console Sessions</h3>
            <p style={{ color: 'var(--muted)', fontSize: 12.5 }}>
              Browser endpoints currently authenticated with refresh credentials
            </p>
          </div>

          <button className="btn danger" onClick={handleSignOutAll} data-testid="settings:btn_signout_all">
            Sign Out Everywhere
          </button>
        </div>

        <div style={{ border: '1px solid var(--border)', borderRadius: 10, padding: 14 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <b>Current Browser (Chrome on Windows 11)</b>
              <small style={{ display: 'block', color: 'var(--muted)', marginTop: 2 }}>
                IP: 103.142.24.18 · Kochi, India · Active now
              </small>
            </div>
            <span className="chip s-OK">This Device</span>
          </div>
        </div>
      </div>
    </div>
  );
};
