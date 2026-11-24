import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Drawer } from '../../shared/ui/Drawer';
import { Shipment, SHIPMENT_FLOW } from '../../shared/api/types';
import { fleetStore } from '../../shared/api/client';
import { StatusChip } from '../../shared/ui/StatusChip';
import { CamIcon, DocIcon, NavigationIcon, ExternalLinkIcon } from '../../shared/icons';
import { useToast } from '../../shared/ui/Toast';
import { openGoogleMapsTracking } from '../../shared/utils/gmaps';

interface ShipmentDrawerProps {
  shipmentId: string | null;
  onClose: () => void;
  onEdit: (shipment: Shipment) => void;
  onCancel: (shipment: Shipment) => void;
}

export const ShipmentDrawer: React.FC<ShipmentDrawerProps> = ({
  shipmentId,
  onClose,
  onEdit,
  onCancel,
}) => {
  const navigate = useNavigate();
  const { showToast } = useToast();

  if (!shipmentId) return null;

  const shipment = fleetStore.shipments.find((s) => s.id === shipmentId);
  if (!shipment) return null;

  const driver = shipment.driver ? fleetStore.drivers.find((d) => d.id === shipment.driver) : null;
  const vehicle = shipment.vehicle ? fleetStore.vehicles.find((v) => v.id === shipment.vehicle) : null;

  const currentIndex = SHIPMENT_FLOW.indexOf(shipment.status);
  const isTerminal = ['Cancelled', 'Failed', 'Rejected'].includes(shipment.status);

  const stepsToRender = isTerminal
    ? SHIPMENT_FLOW.slice(0, shipment.status === 'Rejected' ? 2 : 3).concat([shipment.status])
    : SHIPMENT_FLOW;

  const t0 = new Date(shipment.sched);

  const formatDateTime = (d: Date) =>
    d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short' }) +
    ', ' +
    d.toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', hour12: true });

  const statusLabel = shipment.delayed ? 'Delayed' : shipment.status;

  return (
    <Drawer
      isOpen={!!shipmentId}
      onClose={onClose}
      width="520px"
      testId="drawer:shipment_detail"
      header={
        <div>
          <div className="mono" style={{ fontWeight: 800, fontSize: 18 }}>
            {shipment.id}
          </div>
          <div style={{ marginTop: 8 }}>
            <StatusChip status={statusLabel} />
          </div>
        </div>
      }
    >
      <h4>Route &amp; Destination</h4>
      <div style={{ fontSize: 15, fontWeight: 700 }}>
        {shipment.from} → {shipment.to}
      </div>
      <div style={{ color: 'var(--muted)', fontSize: 12.5, marginTop: 4 }}>
        Consignee: {shipment.consignee} · Scheduled {formatDateTime(new Date(shipment.sched))}
      </div>

      <h4>Consignment Manifest</h4>
      <div className="kv" style={{ marginTop: 0 }}>
        <span>Description</span>
        <b>{shipment.goods}</b>
        <span>Quantity</span>
        <b>{shipment.qty} units</b>
        <span>Gross Weight</span>
        <b>{shipment.wt} tonnes</b>
        {shipment.pAddr && (
          <>
            <span>Pickup Point</span>
            <b>{shipment.pAddr}</b>
          </>
        )}
        {shipment.dAddr && (
          <>
            <span>Delivery Point</span>
            <b>{shipment.dAddr}</b>
          </>
        )}
      </div>

      <h4>Fleet Allocation</h4>
      <div className="kv" style={{ marginTop: 0 }}>
        <span>Assigned Driver</span>
        <b>{driver ? driver.name : 'Unassigned'}</b>
        <span>Contact Phone</span>
        <b>{driver ? driver.phone : '—'}</b>
        <span>Vehicle</span>
        <b>{vehicle ? `${vehicle.reg} (${vehicle.cap} t)` : '—'}</b>
        {shipment.note && (
          <>
            <span>Special Notes</span>
            <b>{shipment.note}</b>
          </>
        )}
      </div>

      <h4>Milestone Timeline</h4>
      <div className="tl">
        {stepsToRender.map((st, i) => {
          const isDone = isTerminal ? true : i <= currentIndex;
          const stepTime = new Date(t0.getTime() + i * 55 * 60000);
          return (
            <div key={st} className={`tl-step ${isDone ? '' : 'p'}`}>
              <b>{st}</b>
              <small>
                {isDone
                  ? `${formatDateTime(stepTime)}${
                      i >= 3 && !isTerminal ? ' · GPS verified' : ''
                    }${i === 0 ? ' · by Anil Kumar' : ''}`
                  : 'Pending milestone'}
              </small>
            </div>
          );
        })}
      </div>

      <h4>Proof of Delivery</h4>
      {shipment.status === 'Delivered' ? (
        <div className="pod">
          <div className="ph">
            <CamIcon size={28} />
          </div>
          <div>
            <b>Photo &amp; Digital Signature Recorded</b>
            <div style={{ color: 'var(--muted)', fontSize: 12.5, marginTop: 3 }}>
              Received by {shipment.consignee.split(' ')[0]} · OTP authentication passed
            </div>
          </div>
        </div>
      ) : (
        <div className="pod" style={{ color: 'var(--muted)', fontSize: 13 }}>
          POD verification artifact unlocks upon delivery confirmation.
        </div>
      )}

      {/* Action Buttons */}
      <div style={{ display: 'flex', gap: 10, marginTop: 24, flexWrap: 'wrap' }}>
        <button
          type="button"
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
          data-testid="drawer:btn_track_gmaps"
        >
          <NavigationIcon size={14} /> Track on Google Maps
          <ExternalLinkIcon size={12} style={{ opacity: 0.8 }} />
        </button>

        <button
          type="button"
          className="btn"
          onClick={() => {
            onClose();
            navigate(`/shipments/${shipment.id}`);
          }}
          data-testid="drawer:btn_view_full_page"
        >
          <DocIcon size={14} /> Full 6-Tab Dossier
        </button>

        {!['Delivered', 'Cancelled', 'Failed'].includes(shipment.status) && (
          <>
            <button
              type="button"
              className="btn"
              onClick={() => onEdit(shipment)}
              data-testid="drawer:btn_edit"
            >
              Edit details
            </button>
            <button
              type="button"
              className="btn danger"
              onClick={() => onCancel(shipment)}
              data-testid="drawer:btn_cancel"
            >
              Cancel shipment
            </button>
          </>
        )}

        {shipment.status === 'Delivered' && (
          <button
            type="button"
            className="btn"
            onClick={() => showToast('Proof of Delivery certificate downloaded (PDF)')}
          >
            Download POD
          </button>
        )}
      </div>
    </Drawer>
  );
};
