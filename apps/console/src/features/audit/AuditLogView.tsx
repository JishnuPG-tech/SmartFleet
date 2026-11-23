import React, { useState, useEffect } from 'react';
import { fleetStore } from '../../shared/api/client';
import { DownloadIcon } from '../../shared/icons';
import { useToast } from '../../shared/ui/Toast';

export const AuditLogView: React.FC = () => {
  const { showToast } = useToast();
  const [search, setSearch] = useState('');
  const [, setTick] = useState(0);

  useEffect(() => {
    return fleetStore.subscribe(() => setTick((t) => t + 1));
  }, []);

  const auditLogs = fleetStore.auditLogs;

  const filteredLogs = auditLogs.filter((a) => {
    const q = search.toLowerCase();
    return (
      !q ||
      a.who.toLowerCase().includes(q) ||
      a.a.toLowerCase().includes(q) ||
      a.e.toLowerCase().includes(q) ||
      a.d.toLowerCase().includes(q)
    );
  });

  const handleExport = () => {
    const csvLines = [
      'timestamp,user,action,entity,details',
      ...filteredLogs.map((l) =>
        [l.t, `"${l.who}"`, `"${l.a}"`, `"${l.e}"`, `"${l.d}"`].join(',')
      ),
    ].join('\n');

    try {
      navigator.clipboard.writeText(csvLines).then(
        () => showToast('Audit trail exported to clipboard (CSV)'),
        () => showToast('Clipboard write restricted')
      );
    } catch {
      showToast('Clipboard write restricted');
    }
  };

  return (
    <div className="view-anim">
      <div className="toolbar">
        <div>
          <h3 style={{ fontSize: 16, fontWeight: 800 }}>Immutable System Audit Log</h3>
          <p style={{ color: 'var(--muted)', fontSize: 12.5 }}>
            Cryptographically chained record of all operator dispatches, modifications, and approvals
          </p>
        </div>

        <span style={{ flex: 1 }} />

        <div className="search" style={{ marginLeft: 8, width: 220, height: 32 }}>
          <input
            type="text"
            placeholder="Search audit trail..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            style={{ fontSize: 12.5 }}
          />
        </div>

        <button className="btn" onClick={handleExport} data-testid="audit:btn_export">
          <DownloadIcon size={14} /> Export Audit Log
        </button>
      </div>

      <div className="card" style={{ marginTop: 16 }}>
        <div className="scroll">
          <table className="tbl" data-testid="audit:table">
            <thead>
              <tr>
                <th>Time (UTC/IST)</th>
                <th>User / Operator</th>
                <th>Action</th>
                <th>Entity Reference</th>
                <th>Field Mutation / Detail</th>
              </tr>
            </thead>
            <tbody>
              {filteredLogs.map((a) => (
                <tr key={a.id}>
                  <td className="mono" style={{ fontSize: 12.5 }}>
                    {new Date(a.t).toLocaleString('en-IN')}
                  </td>
                  <td style={{ fontWeight: 600 }}>{a.who}</td>
                  <td>{a.a}</td>
                  <td className="mono" style={{ fontWeight: 700 }}>
                    {a.e}
                  </td>
                  <td style={{ whiteSpace: 'normal', color: 'var(--text-2)' }}>{a.d}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="pg">
          <span>Every dispatch, reassignment, cancellation, and statutory check is preserved indefinitely</span>
        </div>
      </div>
    </div>
  );
};
