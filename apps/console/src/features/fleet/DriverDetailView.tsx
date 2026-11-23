import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { fleetStore } from '../../shared/api/client';
import { Avatar } from '../../shared/ui/Avatar';
import { StatusChip } from '../../shared/ui/StatusChip';
import { useToast } from '../../shared/ui/Toast';
import { PhoneIcon, DocIcon } from '../../shared/icons';

export const DriverDetailView: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { showToast } = useToast();

  const driver = fleetStore.drivers.find((d) => d.id === id);

  if (!driver) {
    return (
      <div style={{ padding: 40, textAlign: 'center' }}>
        <h3>Driver not found</h3>
        <button className="btn" style={{ marginTop: 12 }} onClick={() => navigate('/fleet')}>
          Back to Fleet Directory
        </button>
      </div>
    );
  }

  const assignedVehicle = fleetStore.vehicles.find((v) => v.id === driver.vehicle);
  const driverShipments = fleetStore.shipments.filter((s) => s.driver === driver.id);

  const handleResetPin = () => {
    const pin = fleetStore.resetDriverPin(driver.id);
    showToast(`New one-time login PIN for ${driver.name}: ${pin}`);
  };

  const handleRevokeDevice = () => {
    fleetStore.revokeDriverDevice(driver.id);
    showToast(`Device session terminated for ${driver.name}.`);
  };

  return (
    <div className="view-anim" style={{ maxWidth: 1080 }}>
      {/* Header Profile Card */}
      <div className="card" style={{ padding: 24, marginBottom: 20 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 18 }}>
          <Avatar name={driver.name} size={64} />
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <h2 style={{ fontSize: 22, fontWeight: 800 }}>{driver.name}</h2>
              <StatusChip status={driver.status} />
            </div>
            <div style={{ color: 'var(--muted)', fontSize: 13, marginTop: 4 }}>
              Driver ID: {driver.id} · Assigned Vehicle: {assignedVehicle ? assignedVehicle.reg : 'Unassigned'}
            </div>
          </div>

          <div style={{ marginLeft: 'auto', display: 'flex', gap: 10 }}>
            <button
              className="btn"
              onClick={() => showToast(`Calling ${driver.name} (${driver.phone})...`)}
              data-testid="driver:btn_call"
            >
              <PhoneIcon size={14} /> Call Driver
            </button>
            <button className="btn" onClick={handleResetPin}>
              Reset PIN
            </button>
            <button className="btn danger" onClick={handleRevokeDevice}>
              Revoke Device
            </button>
          </div>
        </div>
      </div>

      <div className="grid">
        {/* Compliance & Licence (s6) */}
        <div className="card s6" style={{ padding: 20 }}>
          <h3 style={{ fontSize: 15, fontWeight: 800, marginBottom: 12 }}>Licence &amp; Statutory Compliance</h3>
          <div className="kv" style={{ marginTop: 0 }}>
            <span>Driving Licence Number</span>
            <b className="mono">{driver.lic}</b>

            <span>Licence Validity</span>
            <b>{new Date(driver.licExp).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })}</b>

            <span>Vehicle Endorsement</span>
            <b>HGMV / Heavy Goods Motor Vehicle</b>

            <span>Emergency Contact</span>
            <b>{driver.emergencyContact || 'Family (+91 98470 12345)'}</b>

            <span>Verification Status</span>
            <b style={{ color: 'var(--ok)' }}>Parivahan SARATHI Verified</b>
          </div>

          <div style={{ display: 'flex', gap: 10, marginTop: 16 }}>
            <div style={{ flex: 1, height: 80, border: '1px dashed var(--border-2)', borderRadius: 10, background: 'var(--surface-2)', display: 'grid', placeItems: 'center', fontSize: 12, color: 'var(--muted)' }}>
              📄 Driving Licence (Front)
            </div>
            <div style={{ flex: 1, height: 80, border: '1px dashed var(--border-2)', borderRadius: 10, background: 'var(--surface-2)', display: 'grid', placeItems: 'center', fontSize: 12, color: 'var(--muted)' }}>
              📄 Driving Licence (Back)
            </div>
          </div>
        </div>

        {/* Telemetry & Device Health (s6) */}
        <div className="card s6" style={{ padding: 20 }}>
          <h3 style={{ fontSize: 15, fontWeight: 800, marginBottom: 12 }}>Hardware &amp; Telemetry Health</h3>
          <div className="kv" style={{ marginTop: 0 }}>
            <span>Registered Device</span>
            <b>Samsung Galaxy A15 (Android 14)</b>

            <span>FCM Push Notification</span>
            <b style={{ color: 'var(--ok)' }}>Active / Delivered</b>

            <span>Installed Driver APK</span>
            <b className="mono">v{driver.version}</b>

            <span>Battery Level at Last Ping</span>
            <b>84% · Charging</b>

            <span>GPS Precision Accuracy</span>
            <b>4.2 meters (High precision)</b>
          </div>

          <div style={{ marginTop: 18 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13, marginBottom: 6 }}>
              <span>On-Time SLA Delivery Score</span>
              <b>{driver.onTime}%</b>
            </div>
            <div className="bar" style={{ height: 8 }}>
              <i style={{ width: `${driver.onTime}%`, background: 'var(--ok)' }} />
            </div>
          </div>
        </div>

        {/* Trip History Ledger (s12) */}
        <div className="card s12" style={{ padding: 20 }}>
          <h3 style={{ fontSize: 15, fontWeight: 800, marginBottom: 12 }}>Assigned Consignment Ledger</h3>
          <div className="scroll">
            <table className="tbl">
              <thead>
                <tr>
                  <th>Shipment</th>
                  <th>Route</th>
                  <th>Cargo</th>
                  <th>Scheduled</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {driverShipments.length === 0 ? (
                  <tr>
                    <td colSpan={6} style={{ textAlign: 'center', padding: 20, color: 'var(--muted)' }}>
                      No shipments currently assigned to this driver.
                    </td>
                  </tr>
                ) : (
                  driverShipments.map((s) => (
                    <tr key={s.id}>
                      <td className="mono" style={{ fontWeight: 700 }}>{s.id}</td>
                      <td>{s.from} → {s.to}</td>
                      <td>{s.goods} · {s.wt} t</td>
                      <td>{new Date(s.sched).toLocaleDateString('en-IN', { day: '2-digit', month: 'short' })}</td>
                      <td>
                        <StatusChip status={s.status} />
                      </td>
                      <td>
                        <button
                          className="btn sm"
                          onClick={() => navigate(`/shipments/${s.id}`)}
                        >
                          <DocIcon size={12} /> Dossier
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};
