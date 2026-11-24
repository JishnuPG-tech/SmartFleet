import React, { useState, useEffect } from 'react';
import { fleetStore } from '../../shared/api/client';
import { ACTIVE_STATUSES, Shipment } from '../../shared/api/types';
import { Avatar } from '../../shared/ui/Avatar';
import { StatusChip } from '../../shared/ui/StatusChip';
import { ShipmentDrawer } from './ShipmentDrawer';
import { ShipmentFormModal } from './ShipmentFormModal';
import { CancelShipmentModal } from './CancelShipmentModal';
import { useToast } from '../../shared/ui/Toast';
import { DownloadIcon, NavigationIcon } from '../../shared/icons';
import { openGoogleMapsTracking } from '../../shared/utils/gmaps';

const FILTERS = ['All', 'Active', 'Pending', 'Delivered', 'Cancelled'] as const;
type FilterType = (typeof FILTERS)[number];

export const ShipmentsView: React.FC = () => {
  const { showToast } = useToast();
  const [activeFilter, setActiveFilter] = useState<FilterType>('All');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedDrawerId, setSelectedDrawerId] = useState<string | null>(null);
  const [editingShipment, setEditingShipment] = useState<Shipment | null>(null);
  const [cancellingShipment, setCancellingShipment] = useState<Shipment | null>(null);
  const [, setTick] = useState(0);

  useEffect(() => {
    return fleetStore.subscribe(() => setTick((t) => t + 1));
  }, []);

  const shipments = fleetStore.shipments;

  const filteredShipments = shipments.filter((s) => {
    const matchesFilter =
      activeFilter === 'All' ||
      (activeFilter === 'Active' && ACTIVE_STATUSES.includes(s.status)) ||
      (activeFilter === 'Pending' && ['Created', 'Assigned', 'Rejected'].includes(s.status)) ||
      (activeFilter === 'Delivered' && s.status === 'Delivered') ||
      (activeFilter === 'Cancelled' && ['Cancelled', 'Failed'].includes(s.status));

    const driver = s.driver ? fleetStore.drivers.find((d) => d.id === s.driver) : null;
    const vehicle = s.vehicle ? fleetStore.vehicles.find((v) => v.id === s.vehicle) : null;

    const q = searchQuery.toLowerCase().trim();
    const matchesSearch =
      !q ||
      s.id.toLowerCase().includes(q) ||
      s.from.toLowerCase().includes(q) ||
      s.to.toLowerCase().includes(q) ||
      s.goods.toLowerCase().includes(q) ||
      (driver && driver.name.toLowerCase().includes(q)) ||
      (vehicle && vehicle.reg.toLowerCase().includes(q));

    return matchesFilter && matchesSearch;
  });

  const handleCopyCsv = () => {
    const headers = ['id', 'from', 'to', 'goods', 'weight_tonnes', 'driver', 'status'];
    const rows = filteredShipments.map((s) => {
      const driver = s.driver ? fleetStore.drivers.find((d) => d.id === s.driver)?.name : '';
      return [s.id, s.from, s.to, `"${s.goods}"`, s.wt, `"${driver || 'Unassigned'}"`, s.status].join(',');
    });
    const csvContent = [headers.join(','), ...rows].join('\n');

    try {
      navigator.clipboard.writeText(csvContent).then(
        () => showToast('Shipments CSV exported to clipboard'),
        () => showToast('Clipboard write restricted')
      );
    } catch {
      showToast('Clipboard write restricted');
    }
  };

  const formatDateTime = (iso: string) => {
    const d = new Date(iso);
    return (
      d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short' }) +
      ', ' +
      d.toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', hour12: true })
    );
  };

  return (
    <div className="view-anim">
      {/* Toolbar */}
      <div className="toolbar">
        <div className="seg" data-testid="shipments:filter_group">
          {FILTERS.map((f) => (
            <button
              key={f}
              className={f === activeFilter ? 'on' : ''}
              onClick={() => setActiveFilter(f)}
              data-testid={`shipments:filter_${f.toLowerCase()}`}
            >
              {f}
            </button>
          ))}
        </div>

        <div className="search" style={{ marginLeft: 8, width: 220, height: 32 }}>
          <input
            type="text"
            placeholder="Filter table rows..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{ fontSize: 12.5 }}
            data-testid="shipments:input_search_filter"
          />
        </div>

        <span style={{ flex: 1 }} />

        <button className="btn" onClick={handleCopyCsv} data-testid="shipments:btn_copy_csv">
          <DownloadIcon size={14} /> Copy CSV
        </button>
      </div>

      {/* Shipments Table */}
      <div className="card">
        <div className="scroll">
          <table className="tbl" data-testid="shipments:table">
            <thead>
              <tr>
                <th style={{ width: 140 }}>Shipment</th>
                <th style={{ width: 180 }}>Route</th>
                <th>Goods</th>
                <th>Driver</th>
                <th style={{ width: 130 }}>Vehicle</th>
                <th style={{ width: 140 }}>Scheduled</th>
                <th style={{ width: 110 }}>Status</th>
                <th style={{ width: 130, textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredShipments.length === 0 ? (
                <tr>
                  <td colSpan={8} style={{ textAlign: 'center', padding: '40px', color: 'var(--muted)' }}>
                    No shipments match the selected criteria.
                  </td>
                </tr>
              ) : (
                filteredShipments.map((s) => {
                  const driver = s.driver ? fleetStore.drivers.find((d) => d.id === s.driver) : null;
                  const vehicle = s.vehicle ? fleetStore.vehicles.find((v) => v.id === s.vehicle) : null;
                  const statusLabel = s.delayed ? 'Delayed' : s.status;

                  return (
                    <tr
                      key={s.id}
                      className="click"
                      onClick={() => setSelectedDrawerId(s.id)}
                      data-testid={`shipments:row_${s.id}`}
                    >
                      <td className="mono nowrap" style={{ fontWeight: 700 }}>
                        {s.id}
                      </td>
                      <td className="nowrap">
                        {s.from} → {s.to}
                      </td>
                      <td className="wrap-cell">
                        {s.goods} · {s.wt} t
                      </td>
                      <td className="nowrap">
                        {driver ? (
                          <div className="person">
                            <Avatar name={driver.name} size={28} />
                            <span>{driver.name}</span>
                          </div>
                        ) : (
                          <span style={{ color: 'var(--muted)' }}>Unassigned</span>
                        )}
                      </td>
                      <td className="mono nowrap">{vehicle ? vehicle.reg : '—'}</td>
                      <td className="nowrap">{formatDateTime(s.sched)}</td>
                      <td className="nowrap">
                        <StatusChip status={statusLabel} />
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <div style={{ display: 'flex', gap: 6, justifyContent: 'flex-end', alignItems: 'center' }}>
                          {ACTIVE_STATUSES.includes(s.status) && s.driver && (
                            <button
                              type="button"
                              className="btn sm"
                              style={{
                                padding: '0 8px',
                                height: 26,
                                fontSize: 11,
                                gap: 4,
                                borderColor: 'var(--c1)',
                                color: 'var(--c1)',
                                background: 'transparent',
                              }}
                              onClick={(e) => {
                                e.stopPropagation();
                                openGoogleMapsTracking({
                                  origin: s.pAddr || s.from,
                                  destination: s.dAddr || s.to,
                                  driverName: driver?.name,
                                });
                                showToast(`Opening Google Maps for ${s.id}...`);
                              }}
                              title="Open live Google Maps tracking"
                            >
                              <NavigationIcon size={11} /> Track
                            </button>
                          )}
                          <button
                            type="button"
                            className="btn sm"
                            style={{ padding: '0 8px', height: 26, fontSize: 11 }}
                            onClick={(e) => {
                              e.stopPropagation();
                              setSelectedDrawerId(s.id);
                            }}
                          >
                            Details
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Table Footer */}
        <div className="pg">
          <span>
            Showing {filteredShipments.length} of {shipments.length} consignments
          </span>
          <span>Click any row for full lifecycle timeline &amp; proof of delivery</span>
        </div>
      </div>

      {/* Slide-out Drawer */}
      <ShipmentDrawer
        shipmentId={selectedDrawerId}
        onClose={() => setSelectedDrawerId(null)}
        onEdit={(shipment) => {
          setSelectedDrawerId(null);
          setEditingShipment(shipment);
        }}
        onCancel={(shipment) => {
          setSelectedDrawerId(null);
          setCancellingShipment(shipment);
        }}
      />

      {/* Edit Modal */}
      <ShipmentFormModal
        isOpen={!!editingShipment}
        onClose={() => setEditingShipment(null)}
        shipmentToEdit={editingShipment}
      />

      {/* Cancel Modal */}
      <CancelShipmentModal
        shipment={cancellingShipment}
        onClose={() => setCancellingShipment(null)}
      />
    </div>
  );
};
