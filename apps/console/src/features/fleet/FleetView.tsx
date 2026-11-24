import React, { useState, useEffect } from 'react';
import { fleetStore } from '../../shared/api/client';
import { Driver, Vehicle, PendingApproval } from '../../shared/api/types';
import { Avatar } from '../../shared/ui/Avatar';
import { StatusChip } from '../../shared/ui/StatusChip';
import { DriverVehicleModal } from './DriverVehicleModal';
import { ApprovalReviewModal } from './ApprovalReviewModal';
import { PlusIcon } from '../../shared/icons';

type SubTab = 'drivers' | 'vehicles' | 'approvals';

export const FleetView: React.FC = () => {
  const [currentTab, setCurrentTab] = useState<SubTab>('drivers');
  const [filter, setFilter] = useState('All');
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [editingDriver, setEditingDriver] = useState<Driver | null>(null);
  const [editingVehicle, setEditingVehicle] = useState<Vehicle | null>(null);
  const [reviewingApplicant, setReviewingApplicant] = useState<PendingApproval | null>(null);
  const [, setTick] = useState(0);

  useEffect(() => {
    return fleetStore.subscribe(() => setTick((t) => t + 1));
  }, []);

  const drivers = fleetStore.drivers;
  const vehicles = fleetStore.vehicles;
  const approvals = fleetStore.approvals;

  const today = new Date('2026-11-22T00:00:00');

  const getExpiryState = (isoDate: string): [string, string] => {
    const d = new Date(isoDate);
    const diffDays = Math.round((d.getTime() - today.getTime()) / (24 * 3600000));
    if (diffDays < 0) return ['Expired', `Expired ${Math.abs(diffDays)}d ago`];
    if (diffDays <= 30) return ['Expiring', `Expires in ${diffDays}d`];
    return ['OK', d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })];
  };

  const driverFilterOptions = ['All', 'Available', 'On Trip', 'On Leave', 'Offline', 'Inactive'];
  const vehicleFilterOptions = ['All', 'Available', 'On Trip', 'Maintenance'];
  const activeFilterOptions = currentTab === 'drivers' ? driverFilterOptions : vehicleFilterOptions;

  const filteredDrivers = drivers.filter((d) => {
    if (filter === 'All') return true;
    return d.status === filter;
  });

  const filteredVehicles = vehicles.filter((v) => {
    if (filter === 'All') return true;
    return v.status === filter;
  });

  // KPIs
  const driversExpiringSoon = drivers.filter(
    (d) => getExpiryState(d.licExp)[0] !== 'OK'
  ).length;

  return (
    <div className="view-anim">
      {/* Tab Navigation Toolbar */}
      <div className="toolbar">
        <div className="seg" id="flTab" data-testid="fleet:tab_group">
          <button
            className={currentTab === 'drivers' ? 'on' : ''}
            onClick={() => {
              setCurrentTab('drivers');
              setFilter('All');
            }}
            data-testid="fleet:tab_drivers"
          >
            Drivers
          </button>
          <button
            className={currentTab === 'vehicles' ? 'on' : ''}
            onClick={() => {
              setCurrentTab('vehicles');
              setFilter('All');
            }}
            data-testid="fleet:tab_vehicles"
          >
            Vehicles
          </button>
          <button
            className={currentTab === 'approvals' ? 'on' : ''}
            onClick={() => {
              setCurrentTab('approvals');
              setFilter('All');
            }}
            data-testid="fleet:tab_approvals"
          >
            Approvals ({approvals.length})
          </button>
        </div>

        {currentTab !== 'approvals' && (
          <div className="seg" data-testid="fleet:filter_group">
            {activeFilterOptions.map((opt) => (
              <button
                key={opt}
                className={opt === filter ? 'on' : ''}
                onClick={() => setFilter(opt)}
                data-testid={`fleet:filter_${opt.toLowerCase().replace(/\s+/g, '_')}`}
              >
                {opt}
              </button>
            ))}
          </div>
        )}

        <span style={{ flex: 1 }} />

        {currentTab !== 'approvals' && (
          <button
            className="btn primary"
            onClick={() => setIsAddModalOpen(true)}
            data-testid="fleet:btn_add_asset"
          >
            <PlusIcon /> Add {currentTab === 'drivers' ? 'driver' : 'vehicle'}
          </button>
        )}
      </div>

      {/* KPI Cards */}
      <div className="kpi-grid">
        {currentTab === 'drivers' && (
          <>
            <div className="card kpi">
              <div className="l">Total drivers</div>
              <div className="v mono">{drivers.length}</div>
            </div>
            <div className="card kpi">
              <div className="l">Available</div>
              <div className="v mono">{drivers.filter((d) => d.status === 'Available').length}</div>
            </div>
            <div className="card kpi">
              <div className="l">On trip</div>
              <div className="v mono">{drivers.filter((d) => d.status === 'On Trip').length}</div>
            </div>
            <div className="card kpi">
              <div className="l">Licences expiring ≤30 d</div>
              <div className="v mono">{driversExpiringSoon}</div>
            </div>
          </>
        )}

        {currentTab === 'vehicles' && (
          <>
            <div className="card kpi">
              <div className="l">Total vehicles</div>
              <div className="v mono">{vehicles.length}</div>
            </div>
            <div className="card kpi">
              <div className="l">Available</div>
              <div className="v mono">{vehicles.filter((v) => v.status === 'Available').length}</div>
            </div>
            <div className="card kpi">
              <div className="l">On trip</div>
              <div className="v mono">{vehicles.filter((v) => v.status === 'On Trip').length}</div>
            </div>
            <div className="card kpi">
              <div className="l">In maintenance</div>
              <div className="v mono">{vehicles.filter((v) => v.status === 'Maintenance').length}</div>
            </div>
          </>
        )}

        {currentTab === 'approvals' && (
          <div className="card kpi">
            <div className="l">Awaiting operator review</div>
            <div className="v mono">{approvals.length}</div>
          </div>
        )}
      </div>

      {/* Table Content */}
      <div className="card">
        <div className="scroll">
          {currentTab === 'drivers' && (
            <table className="tbl" data-testid="fleet:drivers_table">
              <thead>
                <tr>
                  <th>Driver</th>
                  <th>Phone</th>
                  <th>Vehicle</th>
                  <th>Licence expiry</th>
                  <th>On-time</th>
                  <th>Trips (30 d)</th>
                  <th>App</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {filteredDrivers.map((d) => {
                  const assignedV = fleetStore.vehicles.find((v) => v.id === d.vehicle);
                  const exp = getExpiryState(d.licExp);

                  return (
                    <tr
                      key={d.id}
                      className="click"
                      onClick={() => setEditingDriver(d)}
                      data-testid={`driver:row_${d.id}`}
                    >
                      <td>
                        <div className="person">
                          <Avatar name={d.name} size={28} />
                          <span>{d.name}</span>
                        </div>
                      </td>
                      <td className="mono">{d.phone}</td>
                      <td className="mono">{assignedV ? assignedV.reg : '—'}</td>
                      <td>
                        {exp[0] === 'OK' ? (
                          exp[1]
                        ) : (
                          <StatusChip status={exp[0]} label={exp[1]} />
                        )}
                      </td>
                      <td>
                        <div style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
                          <div className="bar">
                            <i style={{ width: `${d.onTime}%` }} />
                          </div>
                          <span>{d.onTime}%</span>
                        </div>
                      </td>
                      <td className="mono">{d.trips}</td>
                      <td className="mono">v{d.version}</td>
                      <td>
                        <StatusChip status={d.status} />
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          )}

          {currentTab === 'vehicles' && (
            <table className="tbl" data-testid="fleet:vehicles_table">
              <thead>
                <tr>
                  <th>Registration</th>
                  <th>Type</th>
                  <th>Capacity</th>
                  <th>Default driver</th>
                  <th>Documents</th>
                  <th>Odometer</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {filteredVehicles.map((v) => {
                  const defDriver = fleetStore.drivers.find((d) => d.id === v.driver);
                  const exp = getExpiryState(v.docExp);

                  return (
                    <tr
                      key={v.id}
                      className="click"
                      onClick={() => setEditingVehicle(v)}
                      data-testid={`vehicle:row_${v.id}`}
                    >
                      <td className="mono">{v.reg}</td>
                      <td>{v.type}</td>
                      <td>{v.cap} t</td>
                      <td>
                        {defDriver ? (
                          <div className="person">
                            <Avatar name={defDriver.name} size={24} />
                            <span>{defDriver.name}</span>
                          </div>
                        ) : (
                          '—'
                        )}
                      </td>
                      <td>
                        {exp[0] === 'OK' ? (
                          `All valid · next ${exp[1]}`
                        ) : (
                          <StatusChip status={exp[0]} label={`Renewal ${exp[1].toLowerCase()}`} />
                        )}
                      </td>
                      <td className="mono">{v.km.toLocaleString('en-IN')} km</td>
                      <td>
                        <StatusChip status={v.status} />
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          )}

          {currentTab === 'approvals' && (
            <table className="tbl" data-testid="fleet:approvals_table">
              <thead>
                <tr>
                  <th>Applicant</th>
                  <th>Mobile</th>
                  <th>Licence</th>
                  <th>Vehicle Category</th>
                  <th>Submitted</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {approvals.length === 0 ? (
                  <tr>
                    <td colSpan={6} style={{ textAlign: 'center', padding: 40, color: 'var(--muted)' }}>
                      All clear. No pending driver registrations.
                    </td>
                  </tr>
                ) : (
                  approvals.map((p) => (
                    <tr key={p.id}>
                      <td style={{ fontWeight: 700 }}>{p.name}</td>
                      <td className="mono">{p.phone}</td>
                      <td className="mono">{p.lic}</td>
                      <td>{p.veh}</td>
                      <td>{p.at}</td>
                      <td>
                        <button
                          className="btn sm primary"
                          onClick={() => setReviewingApplicant(p)}
                          data-testid={`approvals:btn_review_${p.id}`}
                        >
                          Review
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {/* Add / Edit Driver or Vehicle Modal */}
      <DriverVehicleModal
        isOpen={isAddModalOpen || !!editingDriver || !!editingVehicle}
        onClose={() => {
          setIsAddModalOpen(false);
          setEditingDriver(null);
          setEditingVehicle(null);
        }}
        entityType={currentTab === 'vehicles' || editingVehicle ? 'vehicle' : 'driver'}
        driverToEdit={editingDriver}
        vehicleToEdit={editingVehicle}
      />

      {/* Driver Self-Registration Review Modal */}
      <ApprovalReviewModal
        applicant={reviewingApplicant}
        onClose={() => setReviewingApplicant(null)}
      />
    </div>
  );
};
