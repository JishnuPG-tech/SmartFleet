import React, { useState } from 'react';
import { Modal } from '../../shared/ui/Modal';
import { PendingApproval } from '../../shared/api/types';
import { fleetStore } from '../../shared/api/client';
import { useToast } from '../../shared/ui/Toast';

interface ApprovalReviewModalProps {
  applicant: PendingApproval | null;
  onClose: () => void;
}

export const ApprovalReviewModal: React.FC<ApprovalReviewModalProps> = ({ applicant, onClose }) => {
  const { showToast } = useToast();
  const [callNote, setCallNote] = useState('');

  if (!applicant) return null;

  const handleApprove = () => {
    fleetStore.approveDriver(applicant.id, callNote);
    showToast(`Driver ${applicant.name} approved. Welcome SMS dispatched.`);
    onClose();
  };

  const handleReject = () => {
    fleetStore.rejectDriver(applicant.id, callNote || 'Documentation incomplete');
    showToast(`Applicant ${applicant.name} registration rejected.`);
    onClose();
  };

  return (
    <Modal
      isOpen={!!applicant}
      onClose={onClose}
      title={`Review Application: ${applicant.name}`}
      maxWidth="560px"
      testId="modal:review_driver"
    >
      <div style={{ padding: '20px 24px' }}>
        <div className="kv" style={{ margin: '0 0 16px' }}>
          <span>Applicant Phone</span>
          <b>{applicant.phone}</b>

          <span>Licence Details</span>
          <b>{applicant.lic} ({applicant.type})</b>

          <span>Licence Expiry</span>
          <b>{applicant.exp}</b>

          <span>Vehicle Category</span>
          <b>{applicant.veh}</b>

          <span>Emergency Contact</span>
          <b>{applicant.emg}</b>

          <span>Submitted On</span>
          <b>{applicant.at}</b>
        </div>

        {/* Licence Document Photos */}
        <div style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
          <div
            style={{
              flex: 1,
              height: 100,
              border: '1px dashed var(--border-2)',
              borderRadius: 10,
              background: 'var(--surface-2)',
              display: 'grid',
              placeItems: 'center',
              fontSize: 12,
              color: 'var(--muted)',
            }}
          >
            📄 Driving Licence (Front)
          </div>

          <div
            style={{
              flex: 1,
              height: 100,
              border: '1px dashed var(--border-2)',
              borderRadius: 10,
              background: 'var(--surface-2)',
              display: 'grid',
              placeItems: 'center',
              fontSize: 12,
              color: 'var(--muted)',
            }}
          >
            📄 Driving Licence (Back)
          </div>
        </div>

        <label className="fld full" style={{ marginBottom: 16 }}>
          <span>Phone Verification Call Notes (Mandatory)</span>
          <textarea
            required
            placeholder="Confirmed mobile identity, background check, and driving experience with applicant..."
            value={callNote}
            onChange={(e) => setCallNote(e.target.value)}
          />
        </label>

        <p style={{ fontSize: 12, color: 'var(--muted)', marginBottom: 20 }}>
          Verify the photos match the official Parivahan registry and call the applicant before approving.
        </p>

        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10 }}>
          <button
            type="button"
            className="btn danger"
            onClick={handleReject}
            data-testid="review:btn_reject"
          >
            Reject Application
          </button>
          <button
            type="button"
            className="btn primary"
            onClick={handleApprove}
            data-testid="review:btn_approve"
          >
            Approve &amp; Onboard
          </button>
        </div>
      </div>
    </Modal>
  );
};
