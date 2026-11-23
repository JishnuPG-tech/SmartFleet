import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { fleetStore } from '../../shared/api/client';
import { StatusChip } from '../../shared/ui/StatusChip';
import { TruckIcon, DocIcon } from '../../shared/icons';

export const VehicleDetailView: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const vehicle = fleetStore.vehicles.find((v) => v.id === id);

  if (!vehicle) {
    return (
      <div style={{ padding: 40, textAlign: 'center' }}>
        <h3>Vehicle not found</h3>
        <button className="btn" style={{ marginTop: 12 }} onClick={() => navigate('/fleet')}>
          Back to Fleet Directory
        </button>
      </div>
    );
  }

  const assignedDriver = fleetStore.drivers.find((d) => d.id === vehicle.driver);
  const vehicleShipments = fleetStore.shipments.filter((s) => s.vehicle === vehicle.id);

  return (
    <div className="view-anim" style={{ maxWidth: 1080 }}>
      {/* Header Overview Card */}
      <div className="card" style={{ padding: 24, marginBottom: 20 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 18 }}>
          <div
            style={{
              width: 56,
              height: 56,
              borderRadius: 14,
              background: 'var(--accent-soft)',
              color: 'var(--accent)',
              display: 'grid',
              placeItems: 'center',
            }}
          >
            <TruckIcon size={28} />
          </div>

          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <h2 className="mono" style={{ fontSize: 24, fontWeight: 800 }}>
                {vehicle.reg}
              </h2>
              <StatusChip status={vehicle.status} />
            </div>
            <div style={{ color: 'var(--muted)', fontSize: 13, marginTop: 4 }}>
              Asset ID: {vehicle.id} · Type: {vehicle.type} · Max Payload: {vehicle.cap} Tonnes
            </div>
          </div>
        </div>
      </div>

      <div className="grid">
        {/* Statutory Document Health (s6) */}
        <div className="card s6" style={{ padding: 20 }}>
          <h3 style={{ fontSize: 15, fontWeight: 800, marginBottom: 12 }}>Compliance &amp; RTO Registrations</h3>
          <div className="kv" style={{ marginTop: 0 }}>
            <span>Registration Certificate (RC)</span>
            <b style={{ color: 'var(--ok)' }}>Valid (Parivahan VAHAN)</b>

            <span>Comprehensive Commercial Insurance</span>
            <b style={{ color: 'var(--ok)' }}>Valid (National Insurance)</b>

            <span>Fitness Certificate (FC)</span>
            <b>Expires in 45 days</b>

            <span>National Goods Permit (NP)</span>
            <b style={{ color: 'var(--ok)' }}>Valid (5-Year State Endorsement)</b>

            <span>Pollution Under Control (PUC)</span>
            <b>Valid until 14 Dec 2026</b>
          </div>
        </div>

        {/* Technical Specs & Utilization (s6) */}
        <div className="card s6" style={{ padding: 20 }}>
          <h3 style={{ fontSize: 15, fontWeight: 800, marginBottom: 12 }}>Operational Telemetry</h3>
          <div className="kv" style={{ marginTop: 0 }}>
            <span>Default Operator</span>
            <b>{assignedDriver ? assignedDriver.name : 'Unassigned'}</b>

            <span>Odometer Reading</span>
            <b className="mono">{vehicle.km.toLocaleString('en-IN')} km</b>

            <span>Maintenance Interval</span>
            <b>Every 10,000 km (Next at {(vehicle.km + 2400).toLocaleString('en-IN')} km)</b>

            <span>Fuel Type</span>
            <b>BS-VI Diesel / Heavy Duty</b>

            <span>Active Trips Completed</span>
            <b className="mono">{vehicleShipments.length} consignments</b>
          </div>
        </div>

        {/* Assigned Shipments (s12) */}
        <div className="card s12" style={{ padding: 20 }}>
          <h3 style={{ fontSize: 15, fontWeight: 800, marginBottom: 12 }}>Consignments Hauled</h3>
          <div className="scroll">
            <table className="tbl">
              <thead>
                <tr>
                  <th>Shipment</th>
                  <th>Route</th>
                  <th>Goods</th>
                  <th>Weight</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {vehicleShipments.map((s) => (
                  <tr key={s.id}>
                    <td className="mono" style={{ fontWeight: 700 }}>{s.id}</td>
                    <td>{s.from} → {s.to}</td>
                    <td>{s.goods}</td>
                    <td>{s.wt} t</td>
                    <td>
                      <StatusChip status={s.status} />
                    </td>
                    <td>
                      <button className="btn sm" onClick={() => navigate(`/shipments/${s.id}`)}>
                        <DocIcon size={12} /> Dossier
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};
