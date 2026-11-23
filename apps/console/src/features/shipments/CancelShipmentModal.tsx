import React, { useState } from 'react';
import { Modal } from '../../shared/ui/Modal';
import { Shipment } from '../../shared/api/types';
import { fleetStore } from '../../shared/api/client';
import { useToast } from '../../shared/ui/Toast';

interface CancelShipmentModalProps {
  shipment: Shipment | null;
  onClose: () => void;
}

export const CancelShipmentModal: React.FC<CancelShipmentModalProps> = ({ shipment, onClose }) => {
  const { showToast } = useToast();
  const [reason, setReason] = useState('Customer request');
  const [note, setNote] = useState('');

  if (!shipment) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const fullReason = note ? `${reason} · ${note}` : reason;
    fleetStore.cancelShipment(shipment.id, fullReason);
    showToast(`${shipment.id} cancelled. Driver notified.`);
    onClose();
  };

  return (
    <Modal
      isOpen={!!shipment}
      onClose={onClose}
      title={`Cancel Shipment ${shipment.id}`}
      maxWidth="480px"
      testId="modal:cancel_shipment"
    >
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
        <label className="fld full">
          <span>Cancellation Reason</span>
          <select value={reason} onChange={(e) => setReason(e.target.value)}>
            <option>Customer request</option>
            <option>No vehicle available</option>
            <option>Incorrect cargo specifications</option>
            <option>Route blocked / Force majeure</option>
            <option>Other</option>
          </select>
        </label>

        <label className="fld full">
          <span>Additional Internal Notes</span>
          <textarea
            value={note}
            onChange={(e) => setNote(e.target.value)}
            placeholder="Provide context for audit records"
          />
        </label>

        <div className="mf">
          <button type="button" className="btn" onClick={onClose}>
            Keep shipment
          </button>
          <button type="submit" className="btn danger" data-testid="modal:btn_confirm_cancel">
            Confirm Cancellation
          </button>
        </div>
      </form>
    </Modal>
  );
};
