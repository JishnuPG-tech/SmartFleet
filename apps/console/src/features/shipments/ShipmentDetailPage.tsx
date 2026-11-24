import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { fleetStore } from '../../shared/api/client';
import { StatusChip } from '../../shared/ui/StatusChip';
import { SHIPMENT_FLOW } from '../../shared/api/types';
import { useToast } from '../../shared/ui/Toast';
import { CamIcon, MapPinIcon, DocIcon, NavigationIcon, ExternalLinkIcon } from '../../shared/icons';
import { openGoogleMapsTracking } from '../../shared/utils/gmaps';

type DetailTab = 'overview' | 'timeline' | 'route' | 'docs' | 'pod' | 'audit';

export const ShipmentDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [activeTab, setActiveTab] = useState<DetailTab>('overview');

  const shipment = fleetStore.shipments.find((s) => s.id === id);

  if (!shipment) {
    return (
      <div style={{ padding: 40, textAlign: 'center' }}>
        <h3>Shipment not found</h3>
        <button className="btn" style={{ marginTop: 12 }} onClick={() => navigate('/shipments')}>
          Return to Shipments Hub
        </button>
      </div>
    );
  }

  const driver = shipment.driver ? fleetStore.drivers.find((d) => d.id === shipment.driver) : null;
  const vehicle = shipment.vehicle ? fleetStore.vehicles.find((v) => v.id === shipment.vehicle) : null;
  const auditEntries = fleetStore.auditLogs.filter((a) => a.e === shipment.id);

  const currentIndex = SHIPMENT_FLOW.indexOf(shipment.status);
  const t0 = new Date(shipment.sched);

  const formatDateTime = (d: Date) =>
    d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short' }) +
    ', ' +
    d.toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', hour12: true });

  return (
    <div className="view-anim" style={{ maxWidth: 1100 }}>
      {/* Header Dossier Summary */}
      <div className="card" style={{ padding: 24, marginBottom: 16 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: 14 }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <h2 className="mono" style={{ fontSize: 24, fontWeight: 800 }}>
                {shipment.id}
              </h2>
              <StatusChip status={shipment.delayed ? 'Delayed' : shipment.status} />
            </div>
            <div style={{ color: 'var(--muted)', fontSize: 13, marginTop: 4 }}>
              <b>{shipment.from} → {shipment.to}</b> · Consignee: {shipment.consignee} · Version ETag: v{shipment.version}
            </div>
          </div>

          <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
            <button
              className="btn primary"
              style={{ gap: 6 }}
              onClick={() => {
                openGoogleMapsTracking({
                  origin: shipment.pAddr || shipment.from,
                  destination: shipment.dAddr || shipment.to,
                  driverName: driver?.name,
                });
                showToast(`Opening Google Maps live navigation for ${shipment.id}...`);
              }}
              data-testid="shipment:btn_track_gmaps"
            >
              <NavigationIcon size={14} /> Track on Google Maps
              <ExternalLinkIcon size={12} style={{ opacity: 0.8 }} />
            </button>
            <button
              className="btn"
              onClick={() => navigate('/tracking')}
              data-testid="shipment:btn_tracking_hub"
            >
              <MapPinIcon size={14} /> Tracking Hub
            </button>
            <button
              className="btn"
              onClick={() => showToast('Consignment waybill dispatched to driver app')}
            >
              Push Notification to Driver
            </button>
          </div>
        </div>

        {/* 6 Tabs */}
        <div className="seg" style={{ marginTop: 18, width: '100%', overflowX: 'auto' }}>
          <button
            className={activeTab === 'overview' ? 'on' : ''}
            onClick={() => setActiveTab('overview')}
            style={{ flex: 1 }}
          >
            Overview
          </button>
          <button
            className={activeTab === 'timeline' ? 'on' : ''}
            onClick={() => setActiveTab('timeline')}
            style={{ flex: 1 }}
          >
            Milestone Timeline
          </button>
          <button
            className={activeTab === 'route' ? 'on' : ''}
            onClick={() => setActiveTab('route')}
            style={{ flex: 1 }}
          >
            Route Corridor
          </button>
          <button
            className={activeTab === 'docs' ? 'on' : ''}
            onClick={() => setActiveTab('docs')}
            style={{ flex: 1 }}
          >
            Commercial Docs
          </button>
          <button
            className={activeTab === 'pod' ? 'on' : ''}
            onClick={() => setActiveTab('pod')}
            style={{ flex: 1 }}
          >
            Proof of Delivery
          </button>
          <button
            className={activeTab === 'audit' ? 'on' : ''}
            onClick={() => setActiveTab('audit')}
            style={{ flex: 1 }}
          >
            Audit &amp; Diffs
          </button>
        </div>
      </div>

      {/* Tab Panels */}
      {activeTab === 'overview' && (
        <div className="grid">
          <div className="card s6" style={{ padding: 20 }}>
            <h3 style={{ fontSize: 15, fontWeight: 800, marginBottom: 12 }}>Cargo Manifest</h3>
            <div className="kv" style={{ marginTop: 0 }}>
              <span>Commodity Description</span>
              <b>{shipment.goods}</b>
              <span>Package Units</span>
              <b>{shipment.qty} Units</b>
              <span>Gross Payload</span>
              <b>{shipment.wt} Tonnes</b>
              <span>Scheduled Pickup</span>
              <b>{formatDateTime(new Date(shipment.sched))}</b>
              <span>Origin Warehouse</span>
              <b>{shipment.pAddr || `${shipment.from} Central Goods Depot`}</b>
              <span>Destination Consignee Point</span>
              <b>{shipment.dAddr || `${shipment.to} Logistics Yard`}</b>
            </div>
          </div>

          <div className="card s6" style={{ padding: 20 }}>
            <h3 style={{ fontSize: 15, fontWeight: 800, marginBottom: 12 }}>Fleet Resources</h3>
            <div className="kv" style={{ marginTop: 0 }}>
              <span>Allocated Driver</span>
              <b>{driver ? driver.name : 'Unassigned'}</b>
              <span>Driver Contact</span>
              <b>{driver ? driver.phone : '—'}</b>
              <span>Motor Vehicle</span>
              <b>{vehicle ? `${vehicle.reg} (${vehicle.type})` : '—'}</b>
              <span>Vehicle Payload Capacity</span>
              <b>{vehicle ? `${vehicle.cap} Tonnes` : '—'}</b>
              <span>Driver App Version</span>
              <b>{driver ? `v${driver.version}` : '—'}</b>
            </div>
          </div>
        </div>
      )}

      {activeTab === 'timeline' && (
        <div className="card" style={{ padding: 24 }}>
          <h3 style={{ fontSize: 15, fontWeight: 800, marginBottom: 16 }}>Chronological Milestone Audit</h3>
          <div className="tl">
            {SHIPMENT_FLOW.map((st, i) => {
              const isDone = i <= currentIndex;
              const stepTime = new Date(t0.getTime() + i * 55 * 60000);
              return (
                <div key={st} className={`tl-step ${isDone ? '' : 'p'}`}>
                  <b>{st}</b>
                  <small>
                    {isDone
                      ? `${formatDateTime(stepTime)} · Verified via GPS Geofence`
                      : 'Pending milestone'}
                  </small>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {activeTab === 'route' && (
        <div className="card" style={{ padding: 24 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
            <h3 style={{ fontSize: 16, fontWeight: 800, margin: 0 }}>Google Maps Highway Corridor &amp; Navigation</h3>
            <button
              className="btn primary sm"
              style={{ gap: 6 }}
              onClick={() => {
                openGoogleMapsTracking({
                  origin: shipment.pAddr || shipment.from,
                  destination: shipment.dAddr || shipment.to,
                  driverName: driver?.name,
                });
                showToast(`Opening Google Maps navigation...`);
              }}
            >
              <NavigationIcon size={13} /> Open in Google Maps
              <ExternalLinkIcon size={11} />
            </button>
          </div>

          <p style={{ color: 'var(--muted)', fontSize: 13, marginBottom: 18 }}>
            Direct navigation route via Kerala State Highway network from {shipment.from} to {shipment.to}.
          </p>

          <div
            style={{
              padding: 24,
              borderRadius: 12,
              background: 'var(--surface-2)',
              border: '1px solid var(--border)',
              display: 'flex',
              flexDirection: 'column',
              gap: 16,
            }}
          >
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: 16 }}>
              <div style={{ background: 'var(--surface)', padding: 14, borderRadius: 8, border: '1px solid var(--border-2)' }}>
                <span style={{ fontSize: 11, color: 'var(--muted)', textTransform: 'uppercase', fontWeight: 700 }}>Pickup Location</span>
                <div style={{ fontSize: 14, fontWeight: 700, marginTop: 4 }}>{shipment.from}</div>
                <div style={{ fontSize: 12, color: 'var(--text-2)', marginTop: 2 }}>{shipment.pAddr || `${shipment.from} Industrial Hub`}</div>
              </div>

              <div style={{ background: 'var(--surface)', padding: 14, borderRadius: 8, border: '1px solid var(--border-2)' }}>
                <span style={{ fontSize: 11, color: 'var(--muted)', textTransform: 'uppercase', fontWeight: 700 }}>Drop Destination</span>
                <div style={{ fontSize: 14, fontWeight: 700, marginTop: 4 }}>{shipment.to}</div>
                <div style={{ fontSize: 12, color: 'var(--text-2)', marginTop: 2 }}>{shipment.dAddr || `${shipment.to} Logistics Yard`}</div>
              </div>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: 10, paddingTop: 8 }}>
              <div style={{ fontSize: 12.5, color: 'var(--text-2)' }}>
                Driver: <b style={{ color: 'var(--text)' }}>{driver?.name || 'Assigned Driver'}</b> ({vehicle?.reg || 'Vehicle'})
              </div>

              <button
                type="button"
                className="btn primary"
                style={{ gap: 6, fontWeight: 600 }}
                onClick={() => {
                  openGoogleMapsTracking({
                    origin: shipment.pAddr || shipment.from,
                    destination: shipment.dAddr || shipment.to,
                    driverName: driver?.name,
                  });
                }}
              >
                <NavigationIcon size={14} /> Launch Full Google Maps Live Navigation
                <ExternalLinkIcon size={12} />
              </button>
            </div>
          </div>
        </div>
      )}

      {activeTab === 'docs' && (
        <div className="card" style={{ padding: 24 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
            <h3 style={{ fontSize: 15, fontWeight: 800 }}>Commercial Freight Documents</h3>
            <button className="btn sm primary" onClick={() => showToast('Presigned upload URL issued')}>
              Attach Document
            </button>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 14 }}>
            <div style={{ border: '1px solid var(--border)', padding: 16, borderRadius: 12, background: 'var(--surface-2)' }}>
              <DocIcon size={24} />
              <b style={{ display: 'block', marginTop: 10, fontSize: 13 }}>Tax Invoice (GST)</b>
              <small style={{ color: 'var(--muted)' }}>INV-2026-9481 · 240 KB</small>
            </div>
            <div style={{ border: '1px solid var(--border)', padding: 16, borderRadius: 12, background: 'var(--surface-2)' }}>
              <DocIcon size={24} />
              <b style={{ display: 'block', marginTop: 10, fontSize: 13 }}>E-Way Bill</b>
              <small style={{ color: 'var(--muted)' }}>EWB-5819028491 · Verified</small>
            </div>
            <div style={{ border: '1px solid var(--border)', padding: 16, borderRadius: 12, background: 'var(--surface-2)' }}>
              <DocIcon size={24} />
              <b style={{ display: 'block', marginTop: 10, fontSize: 13 }}>Lorry Receipt (LR)</b>
              <small style={{ color: 'var(--muted)' }}>Signed Driver Copy</small>
            </div>
          </div>
        </div>
      )}

      {activeTab === 'pod' && (
        <div className="card" style={{ padding: 24 }}>
          <h3 style={{ fontSize: 15, fontWeight: 800, marginBottom: 16 }}>Proof of Delivery (POD) Documentation</h3>
          {shipment.status === 'Delivered' ? (
            <div className="pod">
              <div className="ph">
                <CamIcon size={32} />
              </div>
              <div>
                <b>High-Resolution Delivery Photo &amp; Digital Signature</b>
                <div style={{ color: 'var(--muted)', fontSize: 13, marginTop: 4 }}>
                  Received by {shipment.consignee.split(' ')[0]} (Authorized Gate Officer) · 6-digit SMS OTP verified
                </div>
              </div>
            </div>
          ) : (
            <div style={{ color: 'var(--muted)', fontSize: 13 }}>
              Proof of delivery image and OTP verification will become available once the driver triggers delivery completion.
            </div>
          )}
        </div>
      )}

      {activeTab === 'audit' && (
        <div className="card" style={{ padding: 24 }}>
          <h3 style={{ fontSize: 15, fontWeight: 800, marginBottom: 14 }}>Field-Level Audit Trail</h3>
          <table className="tbl">
            <thead>
              <tr>
                <th>Timestamp</th>
                <th>Operator</th>
                <th>Action</th>
                <th>Entity</th>
                <th>Field Modifications</th>
              </tr>
            </thead>
            <tbody>
              {auditEntries.length === 0 ? (
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center', padding: 20, color: 'var(--muted)' }}>
                    No field mutations recorded for this shipment.
                  </td>
                </tr>
              ) : (
                auditEntries.map((a) => (
                  <tr key={a.id}>
                    <td className="mono">{new Date(a.t).toLocaleString('en-IN')}</td>
                    <td>{a.who}</td>
                    <td>{a.a}</td>
                    <td className="mono">{a.e}</td>
                    <td>{a.d}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};
