import React from 'react';
import { useNavigate } from 'react-router-dom';
import { fleetStore } from '../../shared/api/client';
import { SosIcon, CloseIcon, ClockIcon, WarnIcon, DocIcon } from '../../shared/icons';
import { useToast } from '../../shared/ui/Toast';

interface AlertPopoverProps {
  isOpen: boolean;
  onClose: () => void;
}

export const AlertPopover: React.FC<AlertPopoverProps> = ({ isOpen, onClose }) => {
  const navigate = useNavigate();
  const { showToast } = useToast();

  if (!isOpen) return null;

  const openAlerts = fleetStore.alerts.filter((a) => !a.done);

  const getIcon = (ic: string) => {
    switch (ic) {
      case 'sos':
        return <SosIcon size={16} />;
      case 'x':
        return <CloseIcon size={16} />;
      case 'clock':
        return <ClockIcon size={16} />;
      case 'doc':
        return <DocIcon size={16} />;
      default:
        return <WarnIcon size={16} />;
    }
  };

  const handleAction = (alert: (typeof openAlerts)[0]) => {
    onClose();
    if (alert.sid) {
      navigate(`/shipments/${alert.sid}`);
      return;
    }
    if (alert.type === 'Critical') {
      showToast('Initiating emergency patch to driver phone...');
    } else {
      navigate('/alerts');
    }
  };

  return (
    <div className="pop" style={{ display: 'block' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingRight: 14 }}>
        <h5>Operational Alerts ({openAlerts.length})</h5>
        <button
          className="btn sm"
          style={{ height: 24, fontSize: 11, padding: '0 8px' }}
          onClick={() => {
            onClose();
            navigate('/alerts');
          }}
        >
          View all
        </button>
      </div>

      <div style={{ maxHeight: 360, overflowY: 'auto' }}>
        {openAlerts.length === 0 ? (
          <div style={{ padding: '28px', textAlign: 'center', color: 'var(--muted)' }}>
            All clear. No active alerts.
          </div>
        ) : (
          openAlerts.map((a) => (
            <div key={a.id} className="alert-row">
              <span className={`ai s-${a.type}`}>{getIcon(a.ic)}</span>
              <div style={{ minWidth: 0, flex: 1 }}>
                <b>{a.title}</b>
                <small>{a.sub}</small>
              </div>
              <button
                className="btn sm act"
                onClick={() => handleAction(a)}
                data-testid={`alert:btn_${a.id}`}
              >
                {a.act}
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
