import React, { useState, useEffect } from 'react';
import { fleetStore } from '../../shared/api/client';
import { StatusChip } from '../../shared/ui/StatusChip';
import { Modal } from '../../shared/ui/Modal';
import { useToast } from '../../shared/ui/Toast';
import { PlusIcon } from '../../shared/icons';
import { useLanguage } from '../../shared/i18n/LanguageContext';

export const ReleasesView: React.FC = () => {
  const { showToast } = useToast();
  const { isMalayalam } = useLanguage();
  const [, setTick] = useState(0);

  const [isPublishModalOpen, setIsPublishModalOpen] = useState(false);
  const [versionName, setVersionName] = useState('2.2.0');
  const [versionCode, setVersionCode] = useState(22);
  const [minSupported, setMinSupported] = useState(20);
  const [apkUrl, setApkUrl] = useState(
    'https://easqjokyjulfuozdxiyp.supabase.co/storage/v1/object/public/releases/app-direct-release-v2.2.0.apk'
  );
  const [fileSizeMb, setFileSizeMb] = useState(14.8);
  const [sha256, setSha256] = useState(
    'a1b2c3d4e5f60718293a4b5c6d7e8f90123456789abcdef0123456789abcdef0'
  );
  const [notesEn, setNotesEn] = useState(
    'High-frequency GPS telemetry, offline POD caching, and automated trip sync.'
  );
  const [notesMl, setNotesMl] = useState('ജിപിഎസ് ഒപ്റ്റിമൈസേഷൻ, ഓഫ്‌ലൈൻ ട്രിപ്പ് സിങ്ക്.');

  useEffect(() => {
    return fleetStore.subscribe(() => setTick((t) => t + 1));
  }, []);

  const releases = fleetStore.releases;

  const handleRolloutChange = (releaseId: string, pct: number) => {
    fleetStore.updateRollout(releaseId, pct);
    showToast(`Rollout target updated to ${pct}%`);
  };

  const handleToggleHalt = (releaseId: string) => {
    fleetStore.toggleHaltRelease(releaseId);
    const rel = releases.find((r) => r.id === releaseId);
    showToast(rel?.isHalted ? 'Emergency halt activated' : 'Rollout resumed');
  };

  const handlePublishSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await fleetStore.publishNewApkRelease({
        flavor: 'direct',
        versionCode: Number(versionCode),
        versionName,
        minSupportedVersionCode: Number(minSupported),
        apkUrl,
        sha256,
        fileSizeMb: Number(fileSizeMb),
        releaseNotesEn: notesEn,
        releaseNotesMl: notesMl,
        rolloutPercentage: 10,
      });
      showToast(`Release v${versionName} published & broadcasted to driver fleet!`);
      setIsPublishModalOpen(false);
    } catch {
      showToast('Published locally; Supabase sync queued');
      setIsPublishModalOpen(false);
    }
  };

  return (
    <div className="view-anim">
      <div className="toolbar">
        <div>
          <h3 style={{ fontSize: 16, fontWeight: 800 }}>In-App APK Rollout Controller</h3>
          <p style={{ color: 'var(--muted)', fontSize: 12.5 }}>
            Manage OTA driver updates, staged rollout cohorts, and emergency version locks
          </p>
        </div>

        <span style={{ flex: 1 }} />

        <button
          className="btn primary"
          onClick={() => setIsPublishModalOpen(true)}
          data-testid="releases:btn_publish_new"
        >
          <PlusIcon /> Publish New APK
        </button>
      </div>

      {/* Release Cards */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: 16, marginTop: 16 }}>
        {releases.map((rel) => (
          <div key={rel.id} className="card" style={{ padding: 20 }} data-testid={`releases:card_${rel.id}`}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                <h4 style={{ fontSize: 16, fontWeight: 800, margin: 0, textTransform: 'none', letterSpacing: 'normal' }}>
                  v{rel.versionName}
                </h4>
                <span className="mono" style={{ color: 'var(--muted)', fontSize: 12.5 }}>
                  Build {rel.versionCode} · {rel.flavor === 'direct' ? 'Self-hosted APK' : 'Play Store'} · {rel.fileSizeMb} MB
                </span>
                <StatusChip
                  status={rel.isHalted ? 'Rejected' : 'OK'}
                  label={rel.isHalted ? 'Emergency Halted' : 'Active Rollout'}
                />
              </div>

              <div style={{ display: 'flex', gap: 10 }}>
                <button
                  className={`btn sm ${rel.isHalted ? 'primary' : 'danger'}`}
                  onClick={() => handleToggleHalt(rel.id)}
                  data-testid={`releases:btn_halt_${rel.id}`}
                >
                  {rel.isHalted ? 'Resume Rollout' : 'Halt Rollout'}
                </button>
              </div>
            </div>

            {/* Staged Rollout Progress */}
            <div style={{ margin: '14px 0' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12.5, marginBottom: 6 }}>
                <span>Staged Rollout Cohort</span>
                <b className="mono">{rel.rolloutPercentage}% target</b>
              </div>

              <div className="bar" style={{ height: 10, borderRadius: 5 }}>
                <i
                  style={{
                    width: `${rel.rolloutPercentage}%`,
                    background: rel.isHalted ? 'var(--crit)' : 'var(--accent)',
                  }}
                />
              </div>

              {/* Quick Preset Buttons */}
              <div style={{ display: 'flex', gap: 6, marginTop: 8 }}>
                {[10, 25, 50, 75, 100].map((pct) => (
                  <button
                    key={pct}
                    className={`btn sm ${rel.rolloutPercentage === pct ? 'primary' : ''}`}
                    style={{ height: 26, padding: '0 8px', fontSize: 11 }}
                    onClick={() => handleRolloutChange(rel.id, pct)}
                    disabled={rel.isHalted}
                  >
                    {pct}%
                  </button>
                ))}
              </div>
            </div>

            {/* Metadata & Release Notes (Strict English unless Malayalam is chosen) */}
            <div className="kv" style={{ marginTop: 12, borderTop: '1px solid var(--border)', paddingTop: 10 }}>
              <span>Min Supported Version</span>
              <b>v{rel.minSupportedVersionCode} (Forced in-app update required below this)</b>

              <span>SHA-256 Checksum</span>
              <b className="mono" style={{ fontSize: 11 }}>{rel.sha256}</b>

              <span>Release Notes</span>
              <b>{isMalayalam ? rel.releaseNotesMl : rel.releaseNotesEn}</b>
            </div>
          </div>
        ))}
      </div>

      {/* Publish Modal */}
      <Modal
        isOpen={isPublishModalOpen}
        onClose={() => setIsPublishModalOpen(false)}
        title="Publish Driver APK Release to Fleet"
        maxWidth="580px"
        testId="modal:publish_release"
      >
        <form onSubmit={handlePublishSubmit} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14, padding: 20 }}>
          <label className="fld">
            <span>Version Name</span>
            <input required value={versionName} onChange={(e) => setVersionName(e.target.value)} placeholder="e.g. 2.2.0" />
          </label>

          <label className="fld">
            <span>Version Code (Integer)</span>
            <input type="number" required value={versionCode} onChange={(e) => setVersionCode(Number(e.target.value))} />
          </label>

          <label className="fld">
            <span>Minimum Supported Build Code</span>
            <input type="number" required value={minSupported} onChange={(e) => setMinSupported(Number(e.target.value))} />
          </label>

          <label className="fld">
            <span>File Size (MB)</span>
            <input type="number" step="0.1" required value={fileSizeMb} onChange={(e) => setFileSizeMb(Number(e.target.value))} />
          </label>

          <label className="fld full">
            <span>APK Binary URL (GitHub Releases or Supabase CDN)</span>
            <input required value={apkUrl} onChange={(e) => setApkUrl(e.target.value)} />
          </label>

          <label className="fld full">
            <span>SHA-256 Checksum</span>
            <input required value={sha256} onChange={(e) => setSha256(e.target.value)} />
          </label>

          <label className="fld full">
            <span>English Release Notes</span>
            <textarea required value={notesEn} onChange={(e) => setNotesEn(e.target.value)} />
          </label>

          <label className="fld full">
            <span>Malayalam Release Notes (Optional)</span>
            <textarea value={notesMl} onChange={(e) => setNotesMl(e.target.value)} />
          </label>

          <div className="mf">
            <button type="button" className="btn" onClick={() => setIsPublishModalOpen(false)}>
              Cancel
            </button>
            <button type="submit" className="btn primary">
              Publish &amp; Broadcast to Drivers
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
