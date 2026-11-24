import React, { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { fleetStore } from '../../shared/api/client';
import { ACTIVE_STATUSES, SHIPMENT_FLOW, Shipment } from '../../shared/api/types';
import { Avatar } from '../../shared/ui/Avatar';
import { StatusChip } from '../../shared/ui/StatusChip';
import { useToast } from '../../shared/ui/Toast';
import { useLanguage } from '../../shared/i18n/LanguageContext';
import { openGoogleMapsTracking } from '../../shared/utils/gmaps';
import {
  SearchIcon,
  TruckIcon,
  NavigationIcon,
  ExternalLinkIcon,
  BoxIcon,
  DocIcon,
} from '../../shared/icons';

export const LiveTrackingHubView: React.FC = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const { isMalayalam } = useLanguage();

  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<'ALL' | 'TRANSIT' | 'PICKUP' | 'DELAYED'>('ALL');
  const [, setTick] = useState(0);

  // Subscribe to live fleet store updates
  useEffect(() => {
    return fleetStore.subscribe(() => setTick((t) => t + 1));
  }, []);

  const shipments = fleetStore.shipments;
  const drivers = fleetStore.drivers;
  const vehicles = fleetStore.vehicles;

  // Only shipments that have an assigned driver and vehicle and have accepted or started delivery
  const activeTrips = useMemo(() => {
    return shipments.filter(
      (s) => ACTIVE_STATUSES.includes(s.status) && Boolean(s.driver) && Boolean(s.vehicle)
    );
  }, [shipments]);

  // Filtered trips based on user search and status
  const filteredTrips = useMemo(() => {
    return activeTrips.filter((s) => {
      if (statusFilter === 'TRANSIT' && s.status !== 'In Transit') return false;
      if (statusFilter === 'PICKUP' && !['Accepted', 'Reached Pickup', 'Loaded'].includes(s.status)) return false;
      if (statusFilter === 'DELAYED' && !s.delayed) return false;

      if (!searchQuery.trim()) return true;
      const q = searchQuery.toLowerCase().trim();
      const driver = drivers.find((d) => d.id === s.driver);
      const vehicle = vehicles.find((v) => v.id === s.vehicle);

      return (
        s.id.toLowerCase().includes(q) ||
        s.from.toLowerCase().includes(q) ||
        s.to.toLowerCase().includes(q) ||
        s.goods.toLowerCase().includes(q) ||
        (driver && driver.name.toLowerCase().includes(q)) ||
        (vehicle && vehicle.reg.toLowerCase().includes(q))
      );
    });
  }, [activeTrips, searchQuery, statusFilter, drivers, vehicles]);

  const renderStepper = (status: Shipment['status']) => {
    const idx = SHIPMENT_FLOW.indexOf(status) - 2;
    const stages = ['Accepted', 'Pickup', 'Loaded', 'Transit', 'Drop'];
    return (
      <div className="steps" style={{ marginTop: 6, marginBottom: 8 }}>
        {stages.map((_, i) => (
          <React.Fragment key={i}>
            <i className={i < idx ? 'd' : i === idx ? 'c' : ''} />
            {i < 4 && <u className={i < idx ? 'd' : ''} />}
          </React.Fragment>
        ))}
      </div>
    );
  };

  const handleTrackOnGoogleMaps = (s: Shipment) => {
    const orig = s.pAddr || s.from;
    const dest = s.dAddr || s.to;
    openGoogleMapsTracking({
      origin: orig,
      destination: dest,
    });
    showToast(
      isMalayalam
        ? `${s.id} ഗൂഗിൾ മാപ്പിൽ തുറക്കുന്നു...`
        : `Opening Google Maps live route for ${s.id}...`
    );
  };

  return (
    <div className="view-anim">
      {/* Top Summary KPI Bar */}
      <div className="grid kpis" style={{ marginBottom: 18 }}>
        <div className="kpi">
          <div className="top">
            <span className="lbl">{isMalayalam ? 'സജീവ ഓട്ടങ്ങൾ' : 'Active Live Trips'}</span>
            <span className="tag ok">GPS Live</span>
          </div>
          <div className="val">{activeTrips.length}</div>
          <div className="sub">{isMalayalam ? 'തത്സമയം ട്രാക്ക് ചെയ്യുന്നു' : 'Tracked via Google Maps'}</div>
        </div>

        <div className="kpi">
          <div className="top">
            <span className="lbl">{isMalayalam ? 'റോഡിലുള്ള വാഹനങ്ങൾ' : 'Vehicles on Road'}</span>
            <span className="tag neutral">Fleet</span>
          </div>
          <div className="val">{activeTrips.length}</div>
          <div className="sub">{isMalayalam ? 'ഡ്രൈവർമാർ ലഭ്യമാണ്' : 'Assigned with verified drivers'}</div>
        </div>

        <div className="kpi">
          <div className="top">
            <span className="lbl">{isMalayalam ? 'റൂട്ട് മാപ്പിംഗ്' : 'Google Maps Bridge'}</span>
            <span className="tag ok">Ready</span>
          </div>
          <div className="val">100%</div>
          <div className="sub">{isMalayalam ? 'തത്സമയ നാവിഗേഷൻ വഴി' : 'Direct zero-API-key redirection'}</div>
        </div>
      </div>

      {/* Toolbar & Filter Bar */}
      <div className="toolbar" style={{ marginBottom: 18 }}>
        <div className="search" style={{ width: 280, height: 36 }}>
          <SearchIcon size={16} />
          <input
            type="text"
            placeholder={isMalayalam ? 'ഷിപ്പ്‌മെന്റ്, വാഹനം, ഡ്രൈവർ...' : 'Search trip, vehicle, driver, city...'}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        <div className="seg">
          <button
            className={statusFilter === 'ALL' ? 'on' : ''}
            onClick={() => setStatusFilter('ALL')}
          >
            {isMalayalam ? 'എല്ലാം' : 'All'} ({activeTrips.length})
          </button>
          <button
            className={statusFilter === 'TRANSIT' ? 'on' : ''}
            onClick={() => setStatusFilter('TRANSIT')}
          >
            {isMalayalam ? 'ഓട്ടത്തിൽ' : 'In Transit'}
          </button>
          <button
            className={statusFilter === 'PICKUP' ? 'on' : ''}
            onClick={() => setStatusFilter('PICKUP')}
          >
            {isMalayalam ? 'പിക്കപ്പ് / ലോഡിംഗ്' : 'Pickup & Loading'}
          </button>
          <button
            className={statusFilter === 'DELAYED' ? 'on' : ''}
            onClick={() => setStatusFilter('DELAYED')}
          >
            {isMalayalam ? 'വൈകിയവ' : 'Delayed'}
          </button>
        </div>

        <button
          type="button"
          className="btn sm"
          style={{ marginLeft: 'auto' }}
          onClick={() => {
            fleetStore.syncAllFromBackendAndDatabase();
            showToast('Refreshed live trips from database');
          }}
        >
          {isMalayalam ? 'പുതുക്കുക' : 'Refresh Feed'}
        </button>
      </div>

      {/* Active Trips Card Grid */}
      {filteredTrips.length === 0 ? (
        <div
          className="card"
          style={{
            padding: '48px 24px',
            textAlign: 'center',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 12,
          }}
        >
          <div
            style={{
              width: 52,
              height: 52,
              borderRadius: 26,
              background: 'var(--surface-2)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'var(--muted)',
            }}
          >
            <TruckIcon size={26} />
          </div>
          <b style={{ fontSize: 16 }}>
            {isMalayalam ? 'സജീവ ഓട്ടങ്ങൾ ഒന്നും കണ്ടെത്തിയില്ല' : 'No Active Trips in Transit'}
          </b>
          <p style={{ color: 'var(--muted)', maxWidth: 440, margin: 0, fontSize: 13.5 }}>
            {isMalayalam
              ? 'ഡ്രൈവർ ഷിപ്പ്‌മെന്റ് സ്വീകരിച്ച് ഓട്ടം ആരംഭിക്കുമ്പോൾ ഇവിടെ കാണാം. നേരിട്ട് ഗൂഗിൾ മാപ്പ് വഴി തത്സമയം ട്രാക്ക് ചെയ്യാം.'
              : 'When a driver accepts an allocated consignment and starts delivery, it appears here immediately with instant 1-click Google Maps tracking.'}
          </p>
          <button
            type="button"
            className="btn primary"
            style={{ marginTop: 8 }}
            onClick={() => navigate('/shipments')}
          >
            <BoxIcon size={14} /> {isMalayalam ? 'ഷിപ്പ്‌മെന്റുകൾ കാണുക' : 'Dispatch Shipments'}
          </button>
        </div>
      ) : (
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(380px, 1fr))',
            gap: 16,
          }}
        >
          {filteredTrips.map((s) => {
            const driver = drivers.find((d) => d.id === s.driver);
            const vehicle = vehicles.find((v) => v.id === s.vehicle);

            return (
              <div
                key={s.id}
                className="card"
                style={{
                  display: 'flex',
                  flexDirection: 'column',
                  padding: 16,
                  gap: 12,
                  border: '1px solid var(--border)',
                }}
              >
                {/* Header: ID, Status, Scheduled */}
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <b className="mono" style={{ fontSize: 15, color: 'var(--text)' }}>
                      {s.id}
                    </b>
                    <StatusChip status={s.status} label={s.status} />
                  </div>
                  <small style={{ color: 'var(--muted)', fontSize: 11.5 }}>
                    {new Date(s.sched).toLocaleTimeString('en-IN', {
                      hour: '2-digit',
                      minute: '2-digit',
                      hour12: true,
                    })}
                  </small>
                </div>

                {/* Route Visualizer */}
                <div
                  style={{
                    background: 'var(--surface-2)',
                    borderRadius: 8,
                    padding: '10px 12px',
                    display: 'flex',
                    flexDirection: 'column',
                    gap: 6,
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'flex-start', gap: 8 }}>
                    <span
                      style={{
                        width: 8,
                        height: 8,
                        borderRadius: '50%',
                        background: 'var(--c1)',
                        marginTop: 5,
                        flexShrink: 0,
                      }}
                    />
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div style={{ fontSize: 11, color: 'var(--muted)', textTransform: 'uppercase' }}>
                        {isMalayalam ? 'പിക്കപ്പ്' : 'Pickup (From)'}
                      </div>
                      <b style={{ fontSize: 13, color: 'var(--text)' }}>{s.from}</b>
                      {s.pAddr && (
                        <div style={{ fontSize: 11.5, color: 'var(--text-2)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                          {s.pAddr}
                        </div>
                      )}
                    </div>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'flex-start', gap: 8 }}>
                    <span
                      style={{
                        width: 8,
                        height: 8,
                        borderRadius: '50%',
                        background: 'var(--c3)',
                        marginTop: 5,
                        flexShrink: 0,
                      }}
                    />
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div style={{ fontSize: 11, color: 'var(--muted)', textTransform: 'uppercase' }}>
                        {isMalayalam ? 'ഡെലിവറി' : 'Delivery (To)'}
                      </div>
                      <b style={{ fontSize: 13, color: 'var(--text)' }}>{s.to}</b>
                      {s.dAddr && (
                        <div style={{ fontSize: 11.5, color: 'var(--text-2)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                          {s.dAddr}
                        </div>
                      )}
                    </div>
                  </div>
                </div>

                {/* Cargo & Progress Stepper */}
                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12, color: 'var(--text-2)' }}>
                    <span>
                      Cargo: <b style={{ color: 'var(--text)' }}>{s.goods}</b> ({s.wt} t)
                    </span>
                    <span>
                      Consignee: <b style={{ color: 'var(--text)' }}>{s.consignee}</b>
                    </span>
                  </div>
                  {renderStepper(s.status)}
                </div>

                {/* Driver & Vehicle Profile */}
                <div
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    paddingTop: 8,
                    borderTop: '1px solid var(--border-2)',
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <Avatar name={driver?.name || 'Driver'} size={32} />
                    <div>
                      <b style={{ fontSize: 13, display: 'block' }}>{driver?.name || 'Assigned Driver'}</b>
                      <small style={{ color: 'var(--muted)', fontSize: 11.5 }}>
                        {vehicle?.reg || 'Vehicle'} · {vehicle?.type || 'Lorry'}
                      </small>
                    </div>
                  </div>

                  {driver?.phone && (
                    <button
                      type="button"
                      className="btn sm"
                      onClick={() => showToast(`Calling ${driver.name} at ${driver.phone}...`)}
                      style={{ fontSize: 11.5, height: 28, padding: '0 8px' }}
                    >
                      {isMalayalam ? 'വിളിക്കുക' : 'Call'}
                    </button>
                  )}
                </div>

                {/* Action Buttons: 1-Click Google Maps Tracking */}
                <div style={{ display: 'flex', gap: 8, marginTop: 4 }}>
                  <button
                    type="button"
                    className="btn primary"
                    style={{
                      flex: 1,
                      justifyContent: 'center',
                      gap: 6,
                      height: 36,
                      fontSize: 12.5,
                      fontWeight: 600,
                    }}
                    onClick={() => handleTrackOnGoogleMaps(s)}
                    data-testid={`trip:track_gmaps_${s.id}`}
                  >
                    <NavigationIcon size={14} />
                    {isMalayalam ? 'ഗൂഗിൾ മാപ്പിൽ കാണുക' : 'Track on Google Maps'}
                    <ExternalLinkIcon size={12} style={{ opacity: 0.8 }} />
                  </button>

                  <button
                    type="button"
                    className="btn"
                    style={{ height: 36, padding: '0 10px' }}
                    onClick={() => navigate(`/shipments/${s.id}`)}
                    title="View 6-Tab Dossier"
                  >
                    <DocIcon size={14} />
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
