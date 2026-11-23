import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Chart, registerables } from 'chart.js';
import { TruckIcon, UserIcon, BoxIcon, ClockIcon, SosIcon, CloseIcon, DocIcon, WarnIcon } from '../../shared/icons';
import { StatusChip } from '../../shared/ui/StatusChip';
import { fleetStore } from '../../shared/api/client';
import { ACTIVE_STATUSES, SHIPMENT_FLOW, Shipment } from '../../shared/api/types';
import { useToast } from '../../shared/ui/Toast';
import { useLanguage } from '../../shared/i18n/LanguageContext';

Chart.register(...registerables);

export const DashboardView: React.FC = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const { isMalayalam } = useLanguage();

  const dayChartRef = useRef<HTMLCanvasElement | null>(null);
  const mixChartRef = useRef<HTMLCanvasElement | null>(null);
  const dayChartInstance = useRef<Chart | null>(null);
  const mixChartInstance = useRef<Chart | null>(null);

  const [, setTick] = useState(0);

  useEffect(() => {
    return fleetStore.subscribe(() => setTick((t) => t + 1));
  }, []);

  const drivers = fleetStore.drivers;
  const shipments = fleetStore.shipments;
  const alerts = fleetStore.alerts;

  const onTrip = drivers.filter((d) => d.status === 'On Trip').length;
  const avail = drivers.filter((d) => d.status === 'Available').length;
  const totalActiveDrivers = drivers.filter((d) => d.status !== 'Inactive').length;
  const pend = shipments.filter((s) => ['Created', 'Assigned', 'Rejected'].includes(s.status)).length;
  const del = shipments.filter((s) => s.delayed && ACTIVE_STATUSES.includes(s.status)).length;

  const getCss = (v: string) => getComputedStyle(document.documentElement).getPropertyValue(v).trim();

  // Initialize and update Chart.js charts efficiently without destroying canvas on every render
  useEffect(() => {
    if (!dayChartRef.current || !mixChartRef.current) return;

    const c1 = getCss('--c1') || '#2a78d6';
    const c3 = getCss('--c3') || '#1baf7a';
    const c4 = getCss('--c4') || '#eda100';
    const c8 = getCss('--c8') || '#e34948';
    const surface = getCss('--surface') || '#ffffff';
    const text = getCss('--text') || '#111318';
    const text2 = getCss('--text-2') || '#4b5160';
    const border2 = getCss('--border-2') || '#d9dce3';
    const grid = getCss('--grid') || '#eef0f3';
    const muted = getCss('--muted') || '#8a90a0';

    // 14-day Created vs Delivered (initialized once)
    if (!dayChartInstance.current) {
      const days = Array.from({ length: 14 }, (_, i) => {
        const d = new Date('2026-11-22T00:00:00');
        d.setDate(d.getDate() + (i - 13));
        return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short' });
      });
      const cr = [9, 11, 8, 12, 14, 7, 5, 10, 13, 12, 9, 11, 14, 10];
      const dl = [8, 10, 9, 10, 12, 8, 4, 9, 11, 12, 8, 10, 12, 6];

      dayChartInstance.current = new Chart(dayChartRef.current, {
        type: 'bar',
        data: {
          labels: days,
          datasets: [
            {
              label: 'Created',
              data: cr,
              backgroundColor: c1,
              borderRadius: { topLeft: 4, topRight: 4 },
              borderSkipped: false,
              maxBarThickness: 14,
            },
            {
              label: 'Delivered',
              data: dl,
              backgroundColor: c3,
              borderRadius: { topLeft: 4, topRight: 4 },
              borderSkipped: false,
              maxBarThickness: 14,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          animation: false,
          interaction: { mode: 'index', intersect: false },
          plugins: {
            legend: { display: false },
            tooltip: {
              backgroundColor: surface,
              titleColor: text,
              bodyColor: text2,
              borderColor: border2,
              borderWidth: 1,
              padding: 10,
              cornerRadius: 8,
            },
          },
          scales: {
            x: {
              grid: { display: false },
              border: { display: false },
              ticks: { color: muted, font: { size: 11 } },
            },
            y: {
              grid: { color: grid },
              border: { display: false },
              ticks: { color: muted, font: { size: 11 }, padding: 6 },
              beginAtZero: true,
            },
          },
        },
      });
    }

    // Status Mix Donut (updated in-place for 60fps performance)
    const deliveredCount = shipments.filter((s) => s.status === 'Delivered').length;
    const inProgressCount = shipments.filter((s) => ACTIVE_STATUSES.includes(s.status)).length;
    const awaitingCount = shipments.filter((s) => ['Created', 'Assigned', 'Rejected'].includes(s.status)).length;
    const cancelledCount = shipments.filter((s) => ['Cancelled', 'Failed'].includes(s.status)).length;
    const mixData = [deliveredCount, inProgressCount, awaitingCount, cancelledCount];

    if (!mixChartInstance.current) {
      mixChartInstance.current = new Chart(mixChartRef.current, {
        type: 'doughnut',
        data: {
          labels: ['Delivered', 'In progress', 'Awaiting driver', 'Cancelled / failed'],
          datasets: [
            {
              data: mixData,
              backgroundColor: [c3, c1, c4, c8],
              borderColor: surface,
              borderWidth: 3,
              borderRadius: 4,
              hoverOffset: 6,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          cutout: '72%',
          animation: false,
          plugins: {
            legend: { display: false },
            tooltip: {
              backgroundColor: surface,
              titleColor: text,
              bodyColor: text2,
              borderColor: border2,
              borderWidth: 1,
              padding: 10,
              cornerRadius: 8,
            },
          },
        },
      });
    } else {
      mixChartInstance.current.data.datasets[0].data = mixData;
      mixChartInstance.current.update('none');
    }
  }, [shipments]);

  // Live trips (active shipments)
  const activeTrips = shipments.filter((s) => ACTIVE_STATUSES.includes(s.status)).slice(0, 6);

  const renderStepper = (status: Shipment['status']) => {
    const idx = SHIPMENT_FLOW.indexOf(status) - 2; // 'Accepted' is index 2 -> 0
    const stages = ['Accepted', 'Pickup', 'Loaded', 'Transit', 'Drop'];
    return (
      <div className="steps">
        {stages.map((_, i) => (
          <React.Fragment key={i}>
            <i className={i < idx ? 'd' : i === idx ? 'c' : ''} />
            {i < 4 && <u className={i < idx ? 'd' : ''} />}
          </React.Fragment>
        ))}
      </div>
    );
  };

  const getAlertIcon = (ic: string) => {
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

  const openAlerts = alerts.filter((a) => !a.done).slice(0, 5);

  return (
    <div className="view-anim">
      {/* 4 KPI Metrics */}
      <div className="grid">
        <div className="card kpi s3">
          <div className="l">
            <span>{isMalayalam ? 'ഓട്ടത്തിൽ' : 'On trip'}</span>
            <span className="ic">
              <TruckIcon size={16} />
            </span>
          </div>
          <div className="v mono">{onTrip}</div>
          <div className="f">
            <span className="up">+2</span> · {isMalayalam ? `${totalActiveDrivers} ഡ്രൈവർമാരിൽ` : `of ${totalActiveDrivers} drivers`}
          </div>
        </div>

        <div className="card kpi s3">
          <div className="l">
            <span>{isMalayalam ? 'ലഭ്യമായവർ' : 'Available'}</span>
            <span className="ic">
              <UserIcon size={16} />
            </span>
          </div>
          <div className="v mono">{avail}</div>
          <div className="f">
            <span className="up">{isMalayalam ? 'തയ്യാറാണ്' : 'Ready'}</span> · {isMalayalam ? 'നിയോഗിക്കാൻ തയ്യാർ' : 'ready to assign'}
          </div>
        </div>

        <div className="card kpi s3">
          <div className="l">
            <span>{isMalayalam ? 'നിയോഗം കാക്കുന്നു' : 'Pending assignment'}</span>
            <span className="ic">
              <BoxIcon size={16} />
            </span>
          </div>
          <div className="v mono">{pend}</div>
          <div className="f">
            <span className="down">{isMalayalam ? '1 സമയം കഴിഞ്ഞു' : '1 overdue'}</span> · {isMalayalam ? 'ഡ്രൈവറെ കാക്കുന്നു' : 'awaiting driver'}
          </div>
        </div>

        <div className="card kpi s3">
          <div className="l">
            <span>{isMalayalam ? 'വൈകിയവ' : 'Delayed'}</span>
            <span className="ic">
              <ClockIcon size={16} />
            </span>
          </div>
          <div className="v mono">{del}</div>
          <div className="f">
            <span className="down">{isMalayalam ? 'വൈകി' : 'Late'}</span> · {isMalayalam ? 'പ്രതീക്ഷിത സമയം കഴിഞ്ഞു' : 'past expected time'}
          </div>
        </div>
      </div>

      {/* Row 2: Charts */}
      <div className="grid" style={{ marginTop: 16 }}>
        {/* Shipments per day (s8) */}
        <div className="card s8">
          <div className="card-h">
            <div>
              <h3>{isMalayalam ? 'പ്രതിദിന ഷിപ്പ്‌മെന്റുകൾ' : 'Shipments per day'}</h3>
              <p>{isMalayalam ? 'രൂപീകരിച്ചതും കൈമാറിയതും (കഴിഞ്ഞ 14 ദിവസം)' : 'Created vs delivered, last 14 days'}</p>
            </div>
            <div className="legend">
              <span>
                <i style={{ background: 'var(--c1)' }} /> {isMalayalam ? 'രൂപീകരിച്ചവ' : 'Created'}
              </span>
              <span>
                <i style={{ background: 'var(--c3)' }} /> {isMalayalam ? 'കൈമാറിയവ' : 'Delivered'}
              </span>
            </div>
          </div>
          <div className="card-b">
            <div className="box">
              <canvas ref={dayChartRef} />
            </div>
          </div>
        </div>

        {/* Status Mix (s4) */}
        <div className="card s4">
          <div className="card-h">
            <div>
              <h3>{isMalayalam ? 'നിലവിലെ അവസ്ഥ' : 'Status mix'}</h3>
              <p>{isMalayalam ? 'ഈ മാസത്തെ എല്ലാ ലോഡുകളും' : 'All open and closed this month'}</p>
            </div>
          </div>
          <div className="card-b">
            <div className="donut">
              <div className="box" style={{ height: 210 }}>
                <canvas ref={mixChartRef} />
              </div>
              <div className="mid">
                <b className="mono">{shipments.length}</b>
                <small>{isMalayalam ? 'ലോഡുകൾ' : 'shipments'}</small>
              </div>
            </div>
            <div className="legend" style={{ marginTop: 14 }}>
              <span>
                <i style={{ background: 'var(--c3)' }} /> {isMalayalam ? 'കൈമാറിയവ' : 'Delivered'}{' '}
                <b>{shipments.filter((s) => s.status === 'Delivered').length}</b>
              </span>
              <span>
                <i style={{ background: 'var(--c1)' }} /> {isMalayalam ? 'ഓട്ടത്തിൽ' : 'In progress'}{' '}
                <b>{shipments.filter((s) => ACTIVE_STATUSES.includes(s.status)).length}</b>
              </span>
              <span>
                <i style={{ background: 'var(--c4)' }} /> {isMalayalam ? 'ഡ്രൈവറെ കാക്കുന്നു' : 'Awaiting driver'}{' '}
                <b>{shipments.filter((s) => ['Created', 'Assigned', 'Rejected'].includes(s.status)).length}</b>
              </span>
              <span>
                <i style={{ background: 'var(--c8)' }} /> {isMalayalam ? 'റദ്ദാക്കിയവ' : 'Cancelled'}{' '}
                <b>{shipments.filter((s) => ['Cancelled', 'Failed'].includes(s.status)).length}</b>
              </span>
            </div>
          </div>
        </div>

        {/* Live Trips (s8) */}
        <div className="card s8">
          <div className="card-h">
            <div>
              <h3>{isMalayalam ? 'തത്സമയ ട്രിപ്പുകൾ' : 'Live trips'}</h3>
              <p>{isMalayalam ? 'സജീവ ഷിപ്പ്‌മെന്റുകളുടെ നിലവിലെ പുരോഗതി' : 'Active shipments and their milestone progress'}</p>
            </div>
            <button className="btn sm" onClick={() => navigate('/map')}>
              {isMalayalam ? 'മാപ്പ് കാണുക' : 'Open map'}
            </button>
          </div>
          <div style={{ paddingTop: 12 }}>
            {activeTrips.map((s) => {
              const driver = fleetStore.drivers.find((d) => d.id === s.driver);
              const vehicle = fleetStore.vehicles.find((v) => v.id === s.vehicle);
              return (
                <div
                  key={s.id}
                  className="trip"
                  style={{ cursor: 'pointer' }}
                  onClick={() => navigate(`/shipments/${s.id}`)}
                >
                  <div>
                    <b className="mono">{s.id}</b>
                    <small>
                      {s.goods} · {s.wt} t
                    </small>
                  </div>
                  <div>
                    <b>
                      {s.from} → {s.to}
                    </b>
                    <small>
                      {driver?.name || 'Unassigned'} · {vehicle?.reg || 'No vehicle'}
                    </small>
                  </div>
                  <div>
                    {renderStepper(s.status)}
                    <small style={{ marginTop: 6 }}>{s.status}</small>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                    <StatusChip
                      status={s.delayed ? 'Delayed' : 'OK'}
                      label={s.delayed ? (isMalayalam ? 'വൈകി' : 'Delayed') : (isMalayalam ? 'കൃത്യസമയത്ത്' : 'On time')}
                    />
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Alerts Centre (s4) */}
        <div className="card s4">
          <div className="card-h">
            <div>
              <h3>{isMalayalam ? 'മുന്നറിയിപ്പുകൾ' : 'Alerts centre'}</h3>
              <p>{isMalayalam ? 'ശ്രദ്ധ ആവശ്യമായവ' : 'Needs your attention'}</p>
            </div>
            <button className="btn sm" onClick={() => navigate('/alerts')}>
              {isMalayalam ? 'എല്ലാം കാണുക' : 'View all'}
            </button>
          </div>
          <div style={{ paddingTop: 12 }}>
            {openAlerts.length === 0 ? (
              <div style={{ padding: '32px', textAlign: 'center', color: 'var(--muted)' }}>
                {isMalayalam ? 'എല്ലാം ശാന്തമാണ്. മുന്നറിയിപ്പുകൾ ഒന്നുമില്ല.' : 'All clear. No active alerts.'}
              </div>
            ) : (
              openAlerts.map((a) => (
                <div key={a.id} className="alert-row">
                  <span className={`ai s-${a.type}`}>{getAlertIcon(a.ic)}</span>
                  <div style={{ minWidth: 0, flex: 1 }}>
                    <b>{a.title}</b>
                    <small>{a.sub}</small>
                  </div>
                  <button
                    className="btn sm act"
                    onClick={() => {
                      if (a.sid) navigate(`/shipments/${a.sid}`);
                      else if (a.type === 'Critical') showToast('Emergency call dialed to driver');
                      else navigate('/alerts');
                    }}
                  >
                    {a.act}
                  </button>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
