import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { SearchIcon, BoxIcon, UserIcon, TruckIcon, ShieldIcon, ReportsIcon } from '../../shared/icons';
import { fleetStore } from '../../shared/api/client';

interface CommandPaletteProps {
  isOpen: boolean;
  onClose: () => void;
  onOpenNewShipment: () => void;
}

export const CommandPalette: React.FC<CommandPaletteProps> = ({
  isOpen,
  onClose,
  onOpenNewShipment,
}) => {
  const [query, setQuery] = useState('');
  const [selectedIndex, setSelectedIndex] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') {
        e.preventDefault();
        if (isOpen) onClose();
        else setQuery('');
      } else if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const q = query.toLowerCase().trim();

  // Dynamic search items from fleet store
  const matchedShipments = fleetStore.shipments
    .filter((s) => !q || s.id.toLowerCase().includes(q) || s.consignee.toLowerCase().includes(q))
    .slice(0, 3)
    .map((s) => ({
      id: s.id,
      title: `${s.id} — ${s.goods} (${s.from} → ${s.to})`,
      meta: s.status,
      icon: <BoxIcon size={16} />,
      action: () => {
        navigate(`/shipments/${s.id}`);
        onClose();
      },
    }));

  const matchedDrivers = fleetStore.drivers
    .filter((d) => !q || d.name.toLowerCase().includes(q) || d.phone.includes(q))
    .slice(0, 3)
    .map((d) => ({
      id: d.id,
      title: `${d.name} (${d.phone})`,
      meta: d.status,
      icon: <UserIcon size={16} />,
      action: () => {
        navigate(`/fleet/drivers/${d.id}`);
        onClose();
      },
    }));

  const matchedVehicles = fleetStore.vehicles
    .filter((v) => !q || v.reg.toLowerCase().includes(q))
    .slice(0, 2)
    .map((v) => ({
      id: v.id,
      title: `${v.reg} — ${v.type} (${v.cap} t)`,
      meta: v.status,
      icon: <TruckIcon size={16} />,
      action: () => {
        navigate(`/fleet/vehicles/${v.id}`);
        onClose();
      },
    }));

  const actionItems = [
    {
      id: 'cmd-new-shipment',
      title: 'Action: Create New Shipment',
      meta: 'Launch wizard',
      icon: <BoxIcon size={16} />,
      action: () => {
        onClose();
        onOpenNewShipment();
      },
    },
    {
      id: 'cmd-map',
      title: 'Navigation: Go to Live Fleet Tracking',
      meta: 'Google Maps navigation',
      icon: <TruckIcon size={16} />,
      action: () => {
        navigate('/tracking');
        onClose();
      },
    },
    {
      id: 'cmd-alerts',
      title: 'Navigation: Open Alerts Center',
      meta: 'View exceptions',
      icon: <ShieldIcon size={16} />,
      action: () => {
        navigate('/alerts');
        onClose();
      },
    },
    {
      id: 'cmd-reports',
      title: 'Navigation: Analytics & Reports',
      meta: '90-day targets',
      icon: <ReportsIcon size={16} />,
      action: () => {
        navigate('/reports');
        onClose();
      },
    },
  ].filter((a) => !q || a.title.toLowerCase().includes(q));

  const allItems = [...actionItems, ...matchedShipments, ...matchedDrivers, ...matchedVehicles];

  const handleKeyDownInput = (e: React.KeyboardEvent) => {
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setSelectedIndex((prev) => (prev + 1) % Math.max(1, allItems.length));
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setSelectedIndex((prev) => (prev - 1 + allItems.length) % Math.max(1, allItems.length));
    } else if (e.key === 'Enter') {
      e.preventDefault();
      if (allItems[selectedIndex]) {
        allItems[selectedIndex].action();
      }
    }
  };

  return (
    <div className="cmd-palette-backdrop" onClick={onClose} aria-hidden="true">
      <div
        className="cmd-palette"
        onClick={(e) => e.stopPropagation()}
        role="dialog"
        aria-modal="true"
        aria-label="Command Palette"
      >
        <div className="cmd-input-wrap">
          <SearchIcon size={18} />
          <input
            autoFocus
            type="text"
            placeholder="Type a command, shipment ID, driver, or vehicle..."
            value={query}
            onChange={(e) => {
              setQuery(e.target.value);
              setSelectedIndex(0);
            }}
            onKeyDown={handleKeyDownInput}
            data-testid="cmd:palette_input"
          />
          <span className="kbd">ESC</span>
        </div>
        <div className="cmd-results">
          {allItems.length === 0 ? (
            <div style={{ padding: '24px', textAlign: 'center', color: 'var(--muted)' }}>
              No matching records or commands found.
            </div>
          ) : (
            allItems.map((item, idx) => (
              <div
                key={item.id}
                className={`cmd-item ${idx === selectedIndex ? 'active' : ''}`}
                onClick={item.action}
                onMouseEnter={() => setSelectedIndex(idx)}
              >
                {item.icon}
                <span>{item.title}</span>
                <span className="cmd-meta">{item.meta}</span>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};
