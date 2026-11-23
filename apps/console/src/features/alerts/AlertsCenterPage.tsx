import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { fleetStore } from '../../shared/api/client';
import { FleetAlert } from '../../shared/api/types';
import { SosIcon, CloseIcon, ClockIcon, WarnIcon, DocIcon, CheckIcon } from '../../shared/icons';
import { Modal } from '../../shared/ui/Modal';
import { StatusChip } from '../../shared/ui/StatusChip';
import { useToast } from '../../shared/ui/Toast';

export const AlertsCenterPage: React.FC = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [activeFilter, setActiveFilter] = useState<string>('All');
  const [resolvingAlert, setResolvingAlert] = useState<FleetAlert | null>(null);
  const [resolutionNote, setResolutionNote] = useState('');
  const [, setTick] = useState(0);

  useEffect(() => {
    return fleetStore.subscribe(() => setTick((t) => t + 1));
  }, []);

  const alerts = fleetStore.alerts;

  const filters = ['All', 'Critical', 'Rejected', 'Delayed', 'Expiring', 'Pending'];

  const filteredAlerts = alerts.filter((a) => {
    if (activeFilter === 'All') return true;
    return a.type === activeFilter;
  });

  const getAlertIcon = (ic: string) => {
    switch (ic) {
      case 'sos':
        return <SosIcon size={18} />;
      case 'x':
        return <CloseIcon size={18} />;
      case 'clock':
        return <ClockIcon size={18} />;
      case 'doc':
        return <DocIcon size={18} />;
      default:
        return <WarnIcon size={18} />;
    }
  };

  const handleResolveSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!resolvingAlert) return;

    resolvingAlert.done = true;
    fleetStore.log(
      'Resolved alert',
      resolvingAlert.title,
      `Operator note: ${resolutionNote || 'Marked handled'}`
    );
    showToast(`Alert resolved: ${resolvingAlert.title}`);
    setResolvingAlert(null);
    setResolutionNote('');
  };

  const criticalCount = alerts.filter((a) => !a.done && a.type === 'Critical').length;
  const delayedCount = alerts.filter((a) => !a.done && a.type === 'Delayed').length;

  return (
    <div className="view-anim">
      {/* Alert KPIs */}
      <div className="grid" style={{ marginBottom: 16 }}>
        <div className="card kpi s3">
          <div className="l">
            <span>Critical Emergency (SOS)</span>
            <span className="ic" style={{ background: 'color-mix(in srgb, var(--crit) 15%, transparent)', color: 'var(--crit)' }}>
              <SosIcon size={16} />
            </span>
          </div>
          <div className="v mono" style={{ color: criticalCount > 0 ? 'var(--crit)' : 'inherit' }}>
            {criticalCount}
          </div>
          <div className="f">High priority panic signals</div>
        </div>

        <div className="card kpi s3">
          <div className="l">
            <span>Delayed Milestones</span>
            <span className="ic" style={{ background: 'color-mix(in srgb, var(--serious) 15%, transparent)', color: 'var(--serious)' }}>
              <ClockIcon size={16} />
            </span>
          </div>
          <div className="v mono">{delayedCount}</div>
          <div className="f">Requires ETA realignment</div>
        </div>

        <div className="card kpi s3">
          <div className="l">
            <span>Pending Driver Response</span>
            <span className="ic">
              <WarnIcon size={16} />
            </span>
          </div>
          <div className="v mono">
            {alerts.filter((a) => !a.done && a.type === 'Pending').length}
          </div>
          <div className="f">&gt; 10m without acceptance</div>
        </div>

        <div className="card kpi s3">
          <div className="l">
            <span>Document Renewals</span>
            <span className="ic">
              <DocIcon size={16} />
            </span>
          </div>
          <div className="v mono">
            {alerts.filter((a) => !a.done && a.type === 'Expiring').length}
          </div>
          <div className="f">Licences &amp; permits ≤ 30d</div>
        </div>
      </div>

      {/* Filter Toolbar */}
      <div className="toolbar">
        <div className="seg" data-testid="alerts:filter_group">
          {filters.map((f) => (
            <button
              key={f}
              className={f === activeFilter ? 'on' : ''}
              onClick={() => setActiveFilter(f)}
              data-testid={`alerts:filter_${f.toLowerCase()}`}
            >
              {f}
            </button>
          ))}
        </div>
      </div>

      {/* Alerts Table */}
      <div className="card">
        <div className="scroll">
          <table className="tbl" data-testid="alerts:table">
            <thead>
              <tr>
                <th>Severity</th>
                <th>Alert Title</th>
                <th>Context / Details</th>
                <th>Associated Entity</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {filteredAlerts.length === 0 ? (
                <tr>
                  <td colSpan={6} style={{ textAlign: 'center', padding: 40, color: 'var(--muted)' }}>
                    No alerts match the current filter.
                  </td>
                </tr>
              ) : (
                filteredAlerts.map((a) => (
                  <tr key={a.id} data-testid={`alerts:row_${a.id}`}>
                    <td>
                      <span className={`chip s-${a.type}`}>{a.type}</span>
                    </td>
                    <td style={{ fontWeight: 700 }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                        <span className={`ai s-${a.type}`} style={{ width: 24, height: 24, borderRadius: 6 }}>
                          {getAlertIcon(a.ic)}
                        </span>
                        {a.title}
                      </div>
                    </td>
                    <td>{a.sub}</td>
                    <td className="mono">{a.sid || 'General'}</td>
                    <td>
                      <StatusChip
                        status={a.done ? 'OK' : a.type}
                        label={a.done ? 'Resolved' : 'Active'}
                      />
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: 6 }}>
                        {!a.done && (
                          <button
                            className="btn sm"
                            onClick={() => setResolvingAlert(a)}
                            data-testid={`alerts:btn_resolve_${a.id}`}
                          >
                            <CheckIcon size={12} /> Resolve
                          </button>
                        )}
                        {a.sid && (
                          <button
                            className="btn sm primary"
                            onClick={() => navigate(`/shipments/${a.sid}`)}
                            data-testid={`alerts:btn_view_${a.id}`}
                          >
                            Inspect
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Resolve Alert Modal */}
      <Modal
        isOpen={!!resolvingAlert}
        onClose={() => setResolvingAlert(null)}
        title={`Resolve Alert: ${resolvingAlert?.title}`}
        maxWidth="500px"
        testId="modal:resolve_alert"
      >
        <form onSubmit={handleResolveSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 14, padding: 20 }}>
          <p style={{ color: 'var(--muted)', fontSize: 13 }}>
            Describe the corrective intervention executed to clear this operational anomaly.
          </p>

          <label className="fld full">
            <span>Operator Resolution Note (Mandatory)</span>
            <textarea
              required
              placeholder="e.g. Spoke with driver Suresh; route deviation due to landslide detour, expected delivery adjusted."
              value={resolutionNote}
              onChange={(e) => setResolutionNote(e.target.value)}
            />
          </label>

          <div className="mf">
            <button type="button" className="btn" onClick={() => setResolvingAlert(null)}>
              Cancel
            </button>
            <button type="submit" className="btn primary" data-testid="modal:btn_submit_resolve">
              Mark Alert Resolved
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
