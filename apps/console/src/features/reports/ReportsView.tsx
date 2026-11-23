import React, { useEffect, useRef, useState } from 'react';
import { Chart, registerables } from 'chart.js';
import { fleetStore } from '../../shared/api/client';
import { useToast } from '../../shared/ui/Toast';
import { DownloadIcon } from '../../shared/icons';

Chart.register(...registerables);

export const ReportsView: React.FC = () => {
  const { showToast } = useToast();
  const [timeRange, setTimeRange] = useState<8 | 6>(8);

  const onTimeChartRef = useRef<HTMLCanvasElement | null>(null);
  const ccChartRef = useRef<HTMLCanvasElement | null>(null);
  const drvChartRef = useRef<HTMLCanvasElement | null>(null);
  const accChartRef = useRef<HTMLCanvasElement | null>(null);
  const verChartRef = useRef<HTMLCanvasElement | null>(null);

  const chartInstances = useRef<Chart[]>([]);

  const getCss = (v: string) => getComputedStyle(document.documentElement).getPropertyValue(v).trim();

  useEffect(() => {
    // Destroy previous charts
    chartInstances.current.forEach((c) => c.destroy());
    chartInstances.current = [];

    const isWk = timeRange === 8;
    const labels = isWk
      ? ['W1', 'W2', 'W3', 'W4', 'W5', 'W6', 'W7', 'W8']
      : ['Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov'];

    const c1 = getCss('--c1') || '#2a78d6';
    const c2 = getCss('--c2') || '#eb6834';
    const c3 = getCss('--c3') || '#1baf7a';
    const c4 = getCss('--c4') || '#eda100';
    const c8 = getCss('--c8') || '#e34948';
    const surface = getCss('--surface') || '#ffffff';
    const text2 = getCss('--text-2') || '#4b5160';
    const border2 = getCss('--border-2') || '#d9dce3';
    const grid = getCss('--grid') || '#eef0f3';
    const muted = getCss('--muted') || '#8a90a0';

    const baseOptions = {
      responsive: true,
      maintainAspectRatio: false,
      animation: false as const,
      interaction: { mode: 'index' as const, intersect: false },
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: surface,
          titleColor: text2,
          bodyColor: text2,
          borderColor: border2,
          borderWidth: 1,
          padding: 8,
          cornerRadius: 8,
        },
      },
    };

    // 1. On-time delivery rate
    if (onTimeChartRef.current) {
      const onTimeData = isWk ? [86, 88, 87, 90, 91, 89, 93, 94] : [84, 86, 88, 90, 91, 93];
      const ch = new Chart(onTimeChartRef.current, {
        type: 'line',
        data: {
          labels,
          datasets: [
            {
              label: 'On-time %',
              data: onTimeData,
              borderColor: c1,
              backgroundColor: 'color-mix(in srgb, var(--c1) 12%, transparent)',
              fill: true,
              tension: 0.38,
              borderWidth: 2.5,
              pointRadius: 4,
              pointBackgroundColor: surface,
              pointBorderColor: c1,
            },
            {
              label: 'Target (92%)',
              data: labels.map(() => 92),
              borderColor: muted,
              borderDash: [6, 5],
              pointRadius: 0,
              borderWidth: 1.5,
            },
          ],
        },
        options: {
          ...baseOptions,
          plugins: {
            ...baseOptions.plugins,
            legend: {
              display: true,
              position: 'top',
              align: 'end',
              labels: { color: text2, boxWidth: 8, boxHeight: 8 },
            },
          },
          scales: {
            x: { grid: { display: false }, border: { display: false }, ticks: { color: muted } },
            y: {
              min: 70,
              max: 100,
              grid: { color: grid },
              border: { display: false },
              ticks: { color: muted, callback: (v) => v + '%' },
            },
          },
        },
      });
      chartInstances.current.push(ch);
    }

    // 2. Completed vs Cancelled
    if (ccChartRef.current) {
      const mo = ['Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov'];
      const cmp = [212, 236, 248, 265, 281, 246];
      const can = [14, 11, 16, 12, 9, 8];

      const ch = new Chart(ccChartRef.current, {
        type: 'bar',
        data: {
          labels: mo,
          datasets: [
            {
              label: 'Completed',
              data: cmp,
              backgroundColor: c3,
              borderColor: surface,
              borderWidth: 2,
              borderRadius: 4,
              maxBarThickness: 36,
            },
            {
              label: 'Cancelled',
              data: can,
              backgroundColor: c8,
              borderColor: surface,
              borderWidth: 2,
              borderRadius: 4,
              maxBarThickness: 36,
            },
          ],
        },
        options: {
          ...baseOptions,
          scales: {
            x: { stacked: true, grid: { display: false }, border: { display: false }, ticks: { color: muted } },
            y: { stacked: true, grid: { color: grid }, border: { display: false }, ticks: { color: muted } },
          },
        },
      });
      chartInstances.current.push(ch);
    }

    // 3. Trips per driver (top 8)
    if (drvChartRef.current) {
      const topDrivers = [...fleetStore.drivers].sort((a, b) => b.trips - a.trips).slice(0, 8);
      const ch = new Chart(drvChartRef.current, {
        type: 'bar',
        data: {
          labels: topDrivers.map((d) => d.name),
          datasets: [
            {
              label: 'Trips Completed',
              data: topDrivers.map((d) => d.trips),
              backgroundColor: c1,
              borderRadius: { topRight: 5, bottomRight: 5 },
              barThickness: 16,
            },
          ],
        },
        options: {
          ...baseOptions,
          indexAxis: 'y',
          scales: {
            x: { grid: { color: grid }, border: { display: false }, ticks: { color: muted }, beginAtZero: true },
            y: { grid: { display: false }, border: { display: false }, ticks: { color: text2, font: { size: 12 } } },
          },
        },
      });
      chartInstances.current.push(ch);
    }

    // 4. Assignment to acceptance duration
    if (accChartRef.current) {
      const acData = isWk ? [7.2, 6.8, 6.1, 5.9, 5.4, 5.1, 4.6, 4.2] : [8, 7.1, 6.4, 5.6, 5, 4.3];
      const ch = new Chart(accChartRef.current, {
        type: 'line',
        data: {
          labels,
          datasets: [
            {
              label: 'Median Minutes',
              data: acData,
              borderColor: c2,
              backgroundColor: 'color-mix(in srgb, var(--c2) 12%, transparent)',
              fill: true,
              tension: 0.38,
              borderWidth: 2.5,
              pointRadius: 3,
            },
            {
              label: 'Target (< 5m)',
              data: labels.map(() => 5),
              borderColor: muted,
              borderDash: [6, 5],
              pointRadius: 0,
              borderWidth: 1.5,
            },
          ],
        },
        options: {
          ...baseOptions,
          scales: {
            x: { grid: { display: false }, border: { display: false }, ticks: { color: muted } },
            y: {
              grid: { color: grid },
              border: { display: false },
              ticks: { color: muted, callback: (v) => v + ' m' },
            },
          },
        },
      });
      chartInstances.current.push(ch);
    }

    // 5. App version adoption
    if (verChartRef.current) {
      const versionCounts: Record<string, number> = {};
      fleetStore.drivers
        .filter((d) => d.status !== 'Inactive')
        .forEach((d) => {
          versionCounts[d.version] = (versionCounts[d.version] || 0) + 1;
        });

      const sortedVersions = Object.keys(versionCounts).sort().reverse();
      const versionColors = [c1, c4, c8];

      const ch = new Chart(verChartRef.current, {
        type: 'doughnut',
        data: {
          labels: sortedVersions.map((v) => 'v' + v),
          datasets: [
            {
              data: sortedVersions.map((k) => versionCounts[k]),
              backgroundColor: versionColors,
              borderColor: surface,
              borderWidth: 3,
              borderRadius: 4,
            },
          ],
        },
        options: {
          ...baseOptions,
          cutout: '72%',
        },
      });
      chartInstances.current.push(ch);
    }

    return () => {
      chartInstances.current.forEach((c) => c.destroy());
    };
  }, [timeRange]);

  const TARGETS = [
    { title: 'Shipments assigned via app', current: 94, goal: 90, unit: '%' },
    { title: 'Proof of delivery captured', current: 96, goal: 95, unit: '%' },
    { title: 'Trips with live tracking', current: 91, goal: 90, unit: '%' },
    { title: 'Weekly active drivers', current: 88, goal: 90, unit: '%' },
    { title: 'Crash-free app sessions', current: 99.7, goal: 99.5, unit: '%' },
  ];

  const handleCopySummaryCsv = () => {
    const csvLines = [
      'metric,actual,target,unit',
      'On-time delivery %,94,92,%',
      'Median assignment to acceptance,4.2,5.0,min',
      'Shipments assigned via app %,94,90,%',
      'Proof of delivery captured %,96,95,%',
      'Trips with live tracking %,91,90,%',
      'Crash-free sessions %,99.7,99.5,%',
    ].join('\n');

    try {
      navigator.clipboard.writeText(csvLines).then(
        () => showToast('Analytics summary exported to clipboard'),
        () => showToast('Clipboard write restricted')
      );
    } catch {
      showToast('Clipboard write restricted');
    }
  };

  return (
    <div className="view-anim">
      {/* Range Toolbar */}
      <div className="toolbar">
        <div className="seg" data-testid="reports:range_group">
          <button
            className={timeRange === 8 ? 'on' : ''}
            onClick={() => setTimeRange(8)}
            data-testid="reports:range_8w"
          >
            8 weeks
          </button>
          <button
            className={timeRange === 6 ? 'on' : ''}
            onClick={() => setTimeRange(6)}
            data-testid="reports:range_6m"
          >
            6 months
          </button>
        </div>

        <span style={{ flex: 1 }} />

        <button className="btn" onClick={handleCopySummaryCsv} data-testid="reports:btn_copy_csv">
          <DownloadIcon size={14} /> Copy summary CSV
        </button>
      </div>

      {/* Grid of Report Visualizations */}
      <div className="grid">
        {/* On-Time Delivery Rate (s7) */}
        <div className="card s7">
          <div className="card-h">
            <div>
              <h3>On-time delivery performance</h3>
              <p>Percentage of consignments completed within the SLA window</p>
            </div>
          </div>
          <div className="card-b">
            <div className="box t">
              <canvas ref={onTimeChartRef} />
            </div>
          </div>
        </div>

        {/* 90-Day Enterprise Targets (s5) */}
        <div className="card s5">
          <div className="card-h">
            <div>
              <h3>90-day SLA targets</h3>
              <p>Key success metrics from the product contract</p>
            </div>
          </div>
          <div className="card-b targets">
            {TARGETS.map((t) => (
              <div key={t.title} className="tg">
                <div className="row">
                  <b>{t.title}</b>
                  <span>
                    {t.current}
                    {t.unit} <span style={{ opacity: 0.7 }}>/ {t.goal}{t.unit}</span>
                  </span>
                </div>
                <div className="bar">
                  <i
                    style={{
                      width: `${Math.min(100, (t.current / t.goal) * 100)}%`,
                      background: t.current >= t.goal ? 'var(--ok)' : 'var(--warn)',
                    }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Completed vs Cancelled (s6) */}
        <div className="card s6">
          <div className="card-h">
            <div>
              <h3>Completed vs cancelled</h3>
              <p>Monthly shipment resolution volume</p>
            </div>
            <div className="legend">
              <span>
                <i style={{ background: 'var(--c3)' }} /> Completed
              </span>
              <span>
                <i style={{ background: 'var(--c8)' }} /> Cancelled
              </span>
            </div>
          </div>
          <div className="card-b">
            <div className="box">
              <canvas ref={ccChartRef} />
            </div>
          </div>
        </div>

        {/* Trips per driver (s6) */}
        <div className="card s6">
          <div className="card-h">
            <div>
              <h3>Trips per driver</h3>
              <p>Top eight operators by trip volume this month</p>
            </div>
          </div>
          <div className="card-b">
            <div className="box">
              <canvas ref={drvChartRef} />
            </div>
          </div>
        </div>

        {/* Assignment to Acceptance (s6) */}
        <div className="card s6">
          <div className="card-h">
            <div>
              <h3>Assignment → acceptance latency</h3>
              <p>Median duration (minutes), contractual SLA under 5m</p>
            </div>
          </div>
          <div className="card-b">
            <div className="box">
              <canvas ref={accChartRef} />
            </div>
          </div>
        </div>

        {/* App Version Adoption (s6) */}
        <div className="card s6">
          <div className="card-h">
            <div>
              <h3>Driver app adoption</h3>
              <p>Active driver cohort by installed APK release</p>
            </div>
          </div>
          <div className="card-b">
            <div className="donut">
              <div className="box" style={{ height: 230 }}>
                <canvas ref={verChartRef} />
              </div>
              <div className="mid">
                <b className="mono">75%</b>
                <small>on latest v2.1.0</small>
              </div>
            </div>
            <div className="legend" style={{ marginTop: 12 }}>
              <span>
                <i style={{ background: 'var(--c1)' }} /> v2.1.0 (latest) <b>9 drivers</b>
              </span>
              <span>
                <i style={{ background: 'var(--c4)' }} /> v2.0.3 <b>2 drivers</b>
              </span>
              <span>
                <i style={{ background: 'var(--c8)' }} /> v2.0.1 <b>1 driver</b>
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
