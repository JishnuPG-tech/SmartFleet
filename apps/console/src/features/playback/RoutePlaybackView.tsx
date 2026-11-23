import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { fleetStore } from '../../shared/api/client';
import { PlayIcon, PauseIcon } from '../../shared/icons';

export const RoutePlaybackView: React.FC = () => {
  const [searchParams] = useSearchParams();
  const shipmentIdParam = searchParams.get('shipment') || 'TC-2026-000131';

  const [selectedId, setSelectedId] = useState(shipmentIdParam);
  const [isPlaying, setIsPlaying] = useState(false);
  const [progress, setProgress] = useState(35); // 0 to 100
  const [playbackSpeed, setPlaybackSpeed] = useState<1 | 2 | 5 | 10>(1);

  const shipment = fleetStore.shipments.find((s) => s.id === selectedId) || fleetStore.shipments[0];
  const driver = shipment?.driver ? fleetStore.drivers.find((d) => d.id === shipment.driver) : null;
  const vehicle = shipment?.vehicle ? fleetStore.vehicles.find((v) => v.id === shipment.vehicle) : null;

  // Playback timer loop
  useEffect(() => {
    if (!isPlaying) return;

    const interval = setInterval(() => {
      setProgress((p) => {
        if (p >= 100) {
          setIsPlaying(false);
          return 100;
        }
        return p + 0.5 * playbackSpeed;
      });
    }, 200);

    return () => clearInterval(interval);
  }, [isPlaying, playbackSpeed]);

  const simulatedSpeed = Math.round(42 + Math.sin(progress / 5) * 16);
  const batteryPct = Math.max(45, Math.round(92 - (progress * 0.4)));

  return (
    <div className="view-anim" style={{ maxWidth: 1100 }}>
      <div className="toolbar">
        <div>
          <h3 style={{ fontSize: 16, fontWeight: 800 }}>Historical GPS Telemetry Scrubber</h3>
          <p style={{ color: 'var(--muted)', fontSize: 12.5 }}>
            Post-trip investigative route reconstruction and anomaly inspection
          </p>
        </div>

        <span style={{ flex: 1 }} />

        <label className="fld" style={{ flexDirection: 'row', alignItems: 'center', gap: 8 }}>
          <span>Consignment:</span>
          <select
            value={selectedId}
            onChange={(e) => {
              setSelectedId(e.target.value);
              setProgress(10);
              setIsPlaying(false);
            }}
          >
            {fleetStore.shipments.map((s) => (
              <option key={s.id} value={s.id}>
                {s.id} ({s.from} → {s.to})
              </option>
            ))}
          </select>
        </label>
      </div>

      {/* Map Replay Canvas */}
      <div className="card" style={{ padding: 20, marginTop: 16 }}>
        <div
          style={{
            height: 380,
            borderRadius: 14,
            background: 'var(--map-sea)',
            border: '1px solid var(--border)',
            position: 'relative',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            overflow: 'hidden',
          }}
        >
          {/* Simulated Route Breadcrumbs */}
          <div style={{ textAlign: 'center', color: 'var(--text-2)' }}>
            <h4 style={{ fontSize: 16, marginBottom: 6 }}>
              {shipment.from} ────────► {shipment.to}
            </h4>
            <p style={{ color: 'var(--muted)', fontSize: 13 }}>
              Scrubbing position {progress.toFixed(1)}% along NH-66 Corridor
            </p>
            <div style={{ marginTop: 14, display: 'inline-flex', gap: 12, background: 'var(--surface)', padding: '8px 16px', borderRadius: 10, border: '1px solid var(--border)', boxShadow: 'var(--shadow)' }}>
              <span>Vehicle: <b>{vehicle?.reg}</b></span>
              <span>Driver: <b>{driver?.name}</b></span>
              <span>Speed: <b>{simulatedSpeed} km/h</b></span>
              <span>Battery: <b>{batteryPct}%</b></span>
            </div>
          </div>
        </div>

        {/* Playback Controls & Scrubber Slider */}
        <div style={{ marginTop: 20, background: 'var(--surface-2)', padding: 18, borderRadius: 12, border: '1px solid var(--border)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 14, marginBottom: 12 }}>
            <button
              className="btn primary"
              style={{ width: 42, height: 42, padding: 0, justifyContent: 'center', borderRadius: 12 }}
              onClick={() => setIsPlaying(!isPlaying)}
              data-testid="playback:btn_play_pause"
            >
              {isPlaying ? <PauseIcon size={18} /> : <PlayIcon size={18} />}
            </button>

            <div style={{ flex: 1 }}>
              <input
                type="range"
                min="0"
                max="100"
                step="0.1"
                value={progress}
                onChange={(e) => setProgress(Number(e.target.value))}
                style={{ width: '100%', accentColor: 'var(--accent)', cursor: 'pointer' }}
                data-testid="playback:slider_scrubber"
              />
            </div>

            <div className="seg">
              {[1, 2, 5, 10].map((spd) => (
                <button
                  key={spd}
                  className={playbackSpeed === spd ? 'on' : ''}
                  onClick={() => setPlaybackSpeed(spd as any)}
                >
                  {spd}x
                </button>
              ))}
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12, color: 'var(--muted)' }}>
            <span>00:00:00 (Trip Start)</span>
            <span className="mono">{progress.toFixed(0)}% Elapsed</span>
            <span>04:15:30 (Delivered)</span>
          </div>
        </div>
      </div>
    </div>
  );
};
