import React, { useState, useEffect, useRef, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import maplibregl from 'maplibre-gl';
import 'maplibre-gl/dist/maplibre-gl.css';
import { fleetStore } from '../../shared/api/client';
import { KERALA_CITIES, ACTIVE_STATUSES } from '../../shared/api/types';
import { Avatar } from '../../shared/ui/Avatar';
import { StatusChip } from '../../shared/ui/StatusChip';
import { useToast } from '../../shared/ui/Toast';
import { useLanguage } from '../../shared/i18n/LanguageContext';
import { SearchIcon, TruckIcon, CloseIcon, ChevronLeftIcon, ChevronRightIcon } from '../../shared/icons';

// Pure OpenStreetMap Standard Style (100% Free, Zero API Key)
const OSM_STYLE: maplibregl.StyleSpecification = {
  version: 8,
  sources: {
    'osm-tiles': {
      type: 'raster',
      tiles: ['https://tile.openstreetmap.org/{z}/{x}/{y}.png'],
      tileSize: 256,
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    },
  },
  layers: [
    {
      id: 'osm-layer',
      type: 'raster',
      source: 'osm-tiles',
      minzoom: 0,
      maxzoom: 19,
    },
  ],
};

// Detailed Kerala Highway Waypoints (NH-66, NH-544, MC Road)
const DETAILED_WAYPOINTS: Record<string, [number, number]> = {
  Kasaragod: [74.9896, 12.5102],
  Kanhangad: [75.0932, 12.3167],
  Payyanur: [75.2038, 12.1009],
  Kannur: [75.3704, 11.8745],
  Thalassery: [75.4934, 11.7486],
  Mahe: [75.5342, 11.7012],
  Vadakara: [75.5901, 11.6048],
  Koyilandy: [75.6980, 11.4422],
  Kozhikode: [75.7804, 11.2588],
  Ramanattukara: [75.8672, 11.1785],
  Tirur: [75.9234, 10.9142],
  Ponnani: [75.9281, 10.7712],
  Chavakkad: [76.0245, 10.5841],
  Kodungallur: [76.2045, 10.2241],
  Paravur: [76.2312, 10.1458],
  Aluva: [76.3533, 10.1076],
  Kochi: [76.2673, 9.9312],
  Cherthala: [76.3314, 9.6848],
  Alappuzha: [76.3388, 9.4981],
  Ambalapuzha: [76.3571, 9.3804],
  Harippad: [76.4521, 9.2814],
  Kayamkulam: [76.4998, 9.1742],
  Karunagappally: [76.5367, 9.0564],
  Chavara: [76.5412, 8.9890],
  Kollam: [76.5955, 8.8932],
  Parippally: [76.7562, 8.8124],
  Attingal: [76.8142, 8.6961],
  Kazhakkoottam: [76.8712, 8.5684],
  Thiruvananthapuram: [76.9366, 8.5241],
  Angamaly: [76.3867, 10.1964],
  Chalakudy: [76.3345, 10.3072],
  Puthukkad: [76.2845, 10.4287],
  Thrissur: [76.2144, 10.5276],
  Mannuthy: [76.2654, 10.5398],
  Vadakkencherry: [76.4987, 10.5964],
  Alathur: [76.5412, 10.6452],
  Palakkad: [76.6548, 10.7867],
  Malappuram: [76.0711, 11.0732],
  Wayanad: [76.0830, 11.6103],
  Kottayam: [76.5222, 9.5916],
  Changanassery: [76.5412, 9.4487],
  Idukki: [76.9744, 9.8494],
  Pathanamthitta: [76.7870, 9.2648],
};

// Accurate Kerala Highway Spine Polyline
function getAccurateHighwayRoute(from: string, to: string): [number, number][] {
  const p1 = KERALA_CITIES[from] || DETAILED_WAYPOINTS[from] || [76.2673, 9.9312];
  const p2 = KERALA_CITIES[to] || DETAILED_WAYPOINTS[to] || [75.7804, 11.2588];

  const nh66Seq = [
    'Thiruvananthapuram',
    'Kazhakkoottam',
    'Attingal',
    'Parippally',
    'Kollam',
    'Chavara',
    'Karunagappally',
    'Kayamkulam',
    'Harippad',
    'Ambalapuzha',
    'Alappuzha',
    'Cherthala',
    'Kochi',
    'Paravur',
    'Kodungallur',
    'Chavakkad',
    'Ponnani',
    'Tirur',
    'Ramanattukara',
    'Kozhikode',
    'Koyilandy',
    'Vadakara',
    'Mahe',
    'Thalassery',
    'Kannur',
    'Payyanur',
    'Kanhangad',
    'Kasaragod',
  ];

  const idx1 = nh66Seq.indexOf(from);
  const idx2 = nh66Seq.indexOf(to);

  if (idx1 !== -1 && idx2 !== -1 && idx1 !== idx2) {
    const step = idx1 < idx2 ? 1 : -1;
    const pts: [number, number][] = [p1];
    for (let i = idx1 + step; idx1 < idx2 ? i <= idx2 : i >= idx2; i += step) {
      const name = nh66Seq[i];
      if (DETAILED_WAYPOINTS[name]) {
        pts.push(DETAILED_WAYPOINTS[name]);
      }
    }
    pts[pts.length - 1] = p2;
    return pts;
  }

  // Central Corridor: Kochi <-> Palakkad via NH-544
  if ((from === 'Kochi' && to === 'Palakkad') || (from === 'Palakkad' && to === 'Kochi')) {
    const seq = ['Kochi', 'Aluva', 'Angamaly', 'Chalakudy', 'Puthukkad', 'Thrissur', 'Mannuthy', 'Vadakkencherry', 'Alathur', 'Palakkad'];
    const s = from === 'Kochi' ? seq : [...seq].reverse();
    return s.map((n) => DETAILED_WAYPOINTS[n] || p1);
  }

  // Central Corridor: Thrissur <-> Kozhikode
  if ((from === 'Thrissur' && to === 'Kozhikode') || (from === 'Kozhikode' && to === 'Thrissur')) {
    const seq = ['Thrissur', 'Chavakkad', 'Ponnani', 'Tirur', 'Ramanattukara', 'Kozhikode'];
    const s = from === 'Thrissur' ? seq : [...seq].reverse();
    return s.map((n) => DETAILED_WAYPOINTS[n] || p1);
  }

  // Kochi <-> Thrissur
  if ((from === 'Kochi' && to === 'Thrissur') || (from === 'Thrissur' && to === 'Kochi')) {
    const seq = ['Kochi', 'Aluva', 'Angamaly', 'Chalakudy', 'Puthukkad', 'Thrissur'];
    const s = from === 'Kochi' ? seq : [...seq].reverse();
    return s.map((n) => DETAILED_WAYPOINTS[n] || p1);
  }

  // Malappuram connector to Kozhikode
  if (from === 'Kasaragod' && to === 'Malappuram') {
    const seq = ['Kasaragod', 'Kanhangad', 'Payyanur', 'Kannur', 'Thalassery', 'Vadakara', 'Kozhikode', 'Ramanattukara', 'Malappuram'];
    return seq.map((n) => DETAILED_WAYPOINTS[n] || p1);
  }

  const midLng = (p1[0] + p2[0]) / 2 + 0.015;
  const midLat = (p1[1] + p2[1]) / 2;
  return [p1, [midLng, midLat], p2];
}

// Distance between coordinates in km
function distanceKm(a: [number, number], b: [number, number]): number {
  const R = 6371;
  const dLat = ((b[1] - a[1]) * Math.PI) / 180;
  const dLng = ((b[0] - a[0]) * Math.PI) / 180;
  const lat1 = (a[1] * Math.PI) / 180;
  const lat2 = (b[1] * Math.PI) / 180;

  const h =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.sin(dLng / 2) * Math.sin(dLng / 2) * Math.cos(lat1) * Math.cos(lat2);
  return 2 * R * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));
}

// Spherical Bearing in Degrees (0 - 360°)
function calculateBearing(start: [number, number], end: [number, number]): number {
  const startLat = (start[1] * Math.PI) / 180;
  const startLng = (start[0] * Math.PI) / 180;
  const endLat = (end[1] * Math.PI) / 180;
  const endLng = (end[0] * Math.PI) / 180;

  const dLng = endLng - startLng;
  const y = Math.sin(dLng) * Math.cos(endLat);
  const x =
    Math.cos(startLat) * Math.sin(endLat) -
    Math.sin(startLat) * Math.cos(endLat) * Math.cos(dLng);

  const brng = (Math.atan2(y, x) * 180) / Math.PI;
  return (brng + 360) % 360;
}

function getCompassHeading(deg: number): string {
  const cardinals = ['N', 'NNE', 'NE', 'ENE', 'E', 'ESE', 'SE', 'SSE', 'S', 'SSW', 'SW', 'WSW', 'W', 'WNW', 'NW', 'NNW'];
  const idx = Math.round(deg / 22.5) % 16;
  return `${Math.round(deg)}° ${cardinals[idx]}`;
}

// Interpolate Position, Heading, and Segments along Polyline
function interpolateAlongPolyline(
  points: [number, number][],
  prog: number
): {
  position: [number, number];
  heading: number;
  traveled: [number, number][];
  remaining: [number, number][];
} {
  if (points.length < 2) {
    const pt = points[0] || [76.2673, 9.9312];
    return { position: pt, heading: 0, traveled: [pt], remaining: [pt] };
  }

  let totalLength = 0;
  const segLengths: number[] = [];
  for (let i = 0; i < points.length - 1; i++) {
    const d = distanceKm(points[i], points[i + 1]);
    segLengths.push(d);
    totalLength += d;
  }

  if (totalLength === 0) {
    return { position: points[0], heading: 0, traveled: [points[0]], remaining: points };
  }

  const targetDist = Math.max(0, Math.min(1, prog)) * totalLength;
  let accumulated = 0;
  const traveledPts: [number, number][] = [points[0]];

  for (let i = 0; i < segLengths.length; i++) {
    const segLen = segLengths[i];
    if (accumulated + segLen >= targetDist || i === segLengths.length - 1) {
      const segT = segLen > 0 ? (targetDist - accumulated) / segLen : 0;
      const pA = points[i];
      const pB = points[i + 1];
      const curLng = pA[0] + (pB[0] - pA[0]) * segT;
      const curLat = pA[1] + (pB[1] - pA[1]) * segT;
      const curPos: [number, number] = [curLng, curLat];
      const heading = calculateBearing(pA, pB);

      traveledPts.push(curPos);
      const remainingPts: [number, number][] = [curPos, ...points.slice(i + 1)];

      return {
        position: curPos,
        heading,
        traveled: traveledPts,
        remaining: remainingPts,
      };
    }
    accumulated += segLen;
    traveledPts.push(points[i + 1]);
  }

  const last = points[points.length - 1];
  return { position: last, heading: 0, traveled: points, remaining: [last] };
}

export const LiveMapView: React.FC = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const { isMalayalam } = useLanguage();

  const mapContainerRef = useRef<HTMLDivElement | null>(null);
  const mapRef = useRef<maplibregl.Map | null>(null);
  const markersRef = useRef<Record<string, maplibregl.Marker>>({});
  const endpointsRef = useRef<{ origin?: maplibregl.Marker; destination?: maplibregl.Marker }>({});

  const [isMapReady, setIsMapReady] = useState(false);
  const [selectedShipmentId, setSelectedShipmentId] = useState<string | null>(null);
  const [isRightPanelOpen, setIsRightPanelOpen] = useState(true);
  const [isMiniHudVisible, setIsMiniHudVisible] = useState(true);
  const [searchFilter, setSearchFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState<'ALL' | 'MOVING' | 'DELAYED' | 'STALE'>('ALL');
  const [speeds, setSpeeds] = useState<Record<string, number>>({});
  const [, setTick] = useState(0);

  // Store subscription
  useEffect(() => {
    return fleetStore.subscribe(() => setTick((t) => t + 1));
  }, []);

  const activeShipments = useMemo(
    () => fleetStore.shipments.filter((s) => ACTIVE_STATUSES.includes(s.status)),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [fleetStore.shipments]
  );

  // Default selection
  useEffect(() => {
    if (!selectedShipmentId && activeShipments.length > 0) {
      setSelectedShipmentId(activeShipments[0].id);
    }
  }, [activeShipments, selectedShipmentId]);

  // Live motion tick
  useEffect(() => {
    const timer = setInterval(() => {
      setSpeeds((prev) => {
        const next = { ...prev };
        activeShipments.forEach((s) => {
          if (!next[s.id]) next[s.id] = Math.round(46 + Math.random() * 22);
          else next[s.id] = Math.max(32, Math.min(80, next[s.id] + Math.round((Math.random() - 0.5) * 4)));
          s.prog = Math.min(0.97, s.prog + 0.002);
        });
        return next;
      });
    }, 2500);
    return () => clearInterval(timer);
  }, [activeShipments]);

  // Handle panel toggle map resize
  useEffect(() => {
    const timer = setTimeout(() => {
      mapRef.current?.resize();
    }, 80);
    return () => clearTimeout(timer);
  }, [isRightPanelOpen]);

  // Filtered shipments
  const filteredShipments = useMemo(() => {
    return activeShipments.filter((s, idx) => {
      const driver = fleetStore.drivers.find((d) => d.id === s.driver);
      const vehicle = fleetStore.vehicles.find((v) => v.id === s.vehicle);
      const isStale = idx === 3;

      if (statusFilter === 'MOVING' && (s.delayed || isStale)) return false;
      if (statusFilter === 'DELAYED' && !s.delayed) return false;
      if (statusFilter === 'STALE' && !isStale) return false;

      if (!searchFilter.trim()) return true;
      const query = searchFilter.toLowerCase();
      return (
        s.id.toLowerCase().includes(query) ||
        (vehicle?.reg && vehicle.reg.toLowerCase().includes(query)) ||
        (driver?.name && driver.name.toLowerCase().includes(query)) ||
        s.from.toLowerCase().includes(query) ||
        s.to.toLowerCase().includes(query)
      );
    });
  }, [activeShipments, searchFilter, statusFilter]);

  const selectedShipment = activeShipments.find((s) => s.id === selectedShipmentId) || activeShipments[0];
  const selectedDriver = selectedShipment
    ? fleetStore.drivers.find((d) => d.id === selectedShipment.driver)
    : null;
  const selectedVehicle = selectedShipment
    ? fleetStore.vehicles.find((v) => v.id === selectedShipment.vehicle)
    : null;

  const selectedTelemetry = useMemo(() => {
    if (!selectedShipment) return null;
    const corridor = getAccurateHighwayRoute(selectedShipment.from, selectedShipment.to);
    return interpolateAlongPolyline(corridor, selectedShipment.prog);
  }, [selectedShipment]);

  // Initialize MapLibre GL Map with pure OpenStreetMap Standard
  useEffect(() => {
    if (!mapContainerRef.current) return;

    const map = new maplibregl.Map({
      container: mapContainerRef.current,
      style: OSM_STYLE,
      center: [76.35, 10.3], // Central Kerala
      zoom: 7.8,
      minZoom: 6,
      maxZoom: 18,
    });

    map.addControl(new maplibregl.NavigationControl({ showCompass: true, visualizePitch: true }), 'top-right');
    map.addControl(new maplibregl.FullscreenControl(), 'top-right');

    const onMapLoaded = () => {
      setIsMapReady(true);

      // Fit bounds to show all active vehicles immediately
      if (activeShipments.length > 0) {
        const bounds = new maplibregl.LngLatBounds();
        activeShipments.forEach((s) => {
          const corridor = getAccurateHighwayRoute(s.from, s.to);
          const tele = interpolateAlongPolyline(corridor, s.prog);
          bounds.extend(tele.position);
        });
        map.fitBounds(bounds, { padding: 60, maxZoom: 10 });
      }
    };

    if (map.loaded()) {
      onMapLoaded();
    } else {
      map.once('load', onMapLoaded);
    }

    mapRef.current = map;

    const ro = new ResizeObserver(() => {
      map.resize();
    });
    ro.observe(mapContainerRef.current);

    return () => {
      ro.disconnect();
      // Clean up markers
      Object.values(markersRef.current).forEach((m) => m.remove());
      markersRef.current = {};
      if (endpointsRef.current.origin) {
        endpointsRef.current.origin.remove();
        endpointsRef.current.origin = undefined;
      }
      if (endpointsRef.current.destination) {
        endpointsRef.current.destination.remove();
        endpointsRef.current.destination = undefined;
      }
      map.remove();
      mapRef.current = null;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Update Vehicle Markers
  useEffect(() => {
    const map = mapRef.current;
    if (!map) return;

    const currentIds = new Set(activeShipments.map((s) => s.id));
    Object.keys(markersRef.current).forEach((id) => {
      if (!currentIds.has(id)) {
        markersRef.current[id].remove();
        delete markersRef.current[id];
      }
    });

    activeShipments.forEach((s, idx) => {
      const isSelected = selectedShipmentId === s.id;
      const isStale = idx === 3;
      const isDelayed = s.delayed;
      const vehicle = fleetStore.vehicles.find((v) => v.id === s.vehicle);
      const driver = fleetStore.drivers.find((d) => d.id === s.driver);

      const corridor = getAccurateHighwayRoute(s.from, s.to);
      const tele = interpolateAlongPolyline(corridor, s.prog);

      let marker = markersRef.current[s.id];

      const markerColor = isStale
        ? '#eda100' // Warning amber
        : isDelayed
        ? '#e34948' // Serious red
        : '#1baf7a'; // Active emerald

      if (!marker) {
        const el = document.createElement('div');
        el.className = `tc-vehicle-marker ${isSelected ? 'active' : ''}`;
        el.setAttribute('data-testid', `map:marker_${s.id}`);
        el.setAttribute('title', `${vehicle?.reg || s.id} (${driver?.name || 'Driver'}) - ${s.from} → ${s.to}`);

        el.innerHTML = `
          <div class="tc-marker-pin" style="border-color: ${markerColor};">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color: ${markerColor}; transform: rotate(${tele.heading}deg); transition: transform 0.25s ease;">
              <polygon points="12 2 19 21 12 17 5 21 12 2" fill="${markerColor}" stroke="none"></polygon>
            </svg>
          </div>
          <div class="tc-marker-tag">${vehicle?.reg || s.id}</div>
        `;

        el.addEventListener('click', (e) => {
          e.stopPropagation();
          setSelectedShipmentId(s.id);
          setIsMiniHudVisible(true);
          map.flyTo({
            center: tele.position,
            zoom: Math.max(map.getZoom(), 12.5),
            speed: 1.2,
          });
        });

        marker = new maplibregl.Marker({ element: el, anchor: 'center' })
          .setLngLat(tele.position)
          .addTo(map);

        markersRef.current[s.id] = marker;
      } else {
        marker.setLngLat(tele.position);
        const el = marker.getElement();
        el.classList.toggle('active', isSelected);
        const pin = el.querySelector<HTMLElement>('.tc-marker-pin');
        const svg = el.querySelector<SVGElement>('svg');
        const tag = el.querySelector<HTMLElement>('.tc-marker-tag');

        if (pin) pin.style.borderColor = markerColor;
        if (svg) {
          svg.style.transform = `rotate(${tele.heading}deg)`;
          svg.style.color = markerColor;
          const poly = svg.querySelector('polygon');
          if (poly) poly.setAttribute('fill', markerColor);
        }
        if (tag) tag.textContent = vehicle?.reg || s.id;
      }
    });
  }, [activeShipments, selectedShipmentId, speeds]);

  // Render Origin and Destination Endpoint Markers on Map
  useEffect(() => {
    const map = mapRef.current;
    if (!map || !selectedShipment) return;

    const fromCoord = KERALA_CITIES[selectedShipment.from] || DETAILED_WAYPOINTS[selectedShipment.from] || [76.2673, 9.9312];
    const toCoord = KERALA_CITIES[selectedShipment.to] || DETAILED_WAYPOINTS[selectedShipment.to] || [75.7804, 11.2588];

    // Origin Marker
    if (!endpointsRef.current.origin) {
      const originEl = document.createElement('div');
      originEl.className = 'tc-endpoint-marker';
      originEl.innerHTML = `
        <div class="tc-endpoint-pin origin">A</div>
        <div class="tc-endpoint-tag">FROM: ${selectedShipment.from}</div>
      `;
      endpointsRef.current.origin = new maplibregl.Marker({ element: originEl, anchor: 'bottom' })
        .setLngLat(fromCoord)
        .addTo(map);
    } else {
      endpointsRef.current.origin.setLngLat(fromCoord);
      const tag = endpointsRef.current.origin.getElement().querySelector('.tc-endpoint-tag');
      if (tag) tag.textContent = `FROM: ${selectedShipment.from}`;
    }

    // Destination Marker
    if (!endpointsRef.current.destination) {
      const destEl = document.createElement('div');
      destEl.className = 'tc-endpoint-marker';
      destEl.innerHTML = `
        <div class="tc-endpoint-pin destination">B</div>
        <div class="tc-endpoint-tag">TO: ${selectedShipment.to}</div>
      `;
      endpointsRef.current.destination = new maplibregl.Marker({ element: destEl, anchor: 'bottom' })
        .setLngLat(toCoord)
        .addTo(map);
    } else {
      endpointsRef.current.destination.setLngLat(toCoord);
      const tag = endpointsRef.current.destination.getElement().querySelector('.tc-endpoint-tag');
      if (tag) tag.textContent = `TO: ${selectedShipment.to}`;
    }
  }, [selectedShipment]);

  // Render GeoJSON Route Path (Traveled + Remaining) on Map
  useEffect(() => {
    const map = mapRef.current;
    if (!map || !isMapReady || !selectedShipment || !selectedTelemetry) return;

    const sourceId = 'selected-route-source';
    const traveledLayerId = 'route-traveled-layer';
    const remainingLayerId = 'route-remaining-layer';

    const geojson: GeoJSON.FeatureCollection = {
      type: 'FeatureCollection',
      features: [
        {
          type: 'Feature',
          properties: { type: 'traveled' },
          geometry: {
            type: 'LineString',
            coordinates: selectedTelemetry.traveled,
          },
        },
        {
          type: 'Feature',
          properties: { type: 'remaining' },
          geometry: {
            type: 'LineString',
            coordinates: selectedTelemetry.remaining,
          },
        },
      ],
    };

    const source = map.getSource(sourceId) as maplibregl.GeoJSONSource | undefined;
    if (source) {
      source.setData(geojson);
    } else {
      map.addSource(sourceId, {
        type: 'geojson',
        data: geojson,
      });

      // Traveled solid neon line
      map.addLayer({
        id: traveledLayerId,
        type: 'line',
        source: sourceId,
        filter: ['==', 'type', 'traveled'],
        layout: { 'line-join': 'round', 'line-cap': 'round' },
        paint: {
          'line-color': '#1677ff',
          'line-width': 5.5,
          'line-opacity': 0.9,
        },
      });

      // Remaining dashed line
      map.addLayer({
        id: remainingLayerId,
        type: 'line',
        source: sourceId,
        filter: ['==', 'type', 'remaining'],
        layout: { 'line-join': 'round', 'line-cap': 'round' },
        paint: {
          'line-color': '#8a90a0',
          'line-width': 3,
          'line-dasharray': [2, 2.5],
          'line-opacity': 0.65,
        },
      });
    }
  }, [selectedShipment, selectedTelemetry, isMapReady]);

  // Recenter to full Kerala state bounds
  const handleRecenterKerala = () => {
    if (!mapRef.current) return;
    mapRef.current.flyTo({
      center: [76.35, 10.3],
      zoom: 7.8,
      bearing: 0,
      pitch: 0,
      speed: 1.2,
    });
  };

  // Fly camera to vehicle
  const handleLocateVehicle = (shipmentId: string) => {
    setSelectedShipmentId(shipmentId);
    setIsMiniHudVisible(true);
    const target = activeShipments.find((s) => s.id === shipmentId);
    if (!target || !mapRef.current) return;

    const corridor = getAccurateHighwayRoute(target.from, target.to);
    const tele = interpolateAlongPolyline(corridor, target.prog);
    mapRef.current.flyTo({
      center: tele.position,
      zoom: 12.5,
      speed: 1.3,
    });
  };

  const calculateEta = (prog: number) => {
    const remainingHours = (1 - prog) * 4.2;
    const etaDate = new Date(Date.now() + remainingHours * 3600000);
    return etaDate.toLocaleTimeString('en-IN', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: true,
    });
  };

  return (
    <div className="view-anim">
      <div className={`mapwrap ${!isRightPanelOpen ? 'full' : ''}`}>
        {/* Map Canvas Container */}
        <div className="map-container" data-testid="map:canvas_container">
          <div ref={mapContainerRef} className="map-gl-canvas" />

          {/* Top Floating Map Controls Bar */}
          <div className="map-controls-bar">
            {/* Quick Vehicle Search & Detection */}
            <div className="map-search-box">
              <SearchIcon size={16} />
              <input
                type="text"
                placeholder={isMalayalam ? 'വാഹനം / ഡ്രൈവർ തിരയുക...' : 'Search vehicle, driver, city...'}
                value={searchFilter}
                onChange={(e) => setSearchFilter(e.target.value)}
                data-testid="map:input_search"
              />
            </div>

            {/* Status Quick Filter Pills */}
            <div style={{ display: 'flex', gap: 6 }}>
              {(['ALL', 'MOVING', 'DELAYED', 'STALE'] as const).map((filter) => (
                <button
                  key={filter}
                  type="button"
                  className={`btn sm ${statusFilter === filter ? 'primary' : ''}`}
                  onClick={() => setStatusFilter(filter)}
                  style={{ height: 38 }}
                >
                  {filter === 'ALL'
                    ? isMalayalam ? 'എല്ലാം' : 'All'
                    : filter === 'MOVING'
                    ? isMalayalam ? 'ഓട്ടത്തിൽ' : 'Moving'
                    : filter === 'DELAYED'
                    ? isMalayalam ? 'വൈകിയവ' : 'Delayed'
                    : isMalayalam ? 'സ്റ്റെയിൽ' : 'Stale'}
                </button>
              ))}
            </div>

            {/* Right Map Actions (Recenter & Panel Toggle) */}
            <div style={{ marginLeft: 'auto', display: 'flex', gap: 8 }}>
              <button
                type="button"
                className="btn sm"
                style={{ height: 38 }}
                onClick={handleRecenterKerala}
                data-testid="map:btn_recenter"
                title="Fit Kerala State Bounds"
              >
                {isMalayalam ? 'കേരളം കാണുക' : 'Kerala Bounds'}
              </button>

              {/* Toggle to Hide or Show Right Active Vehicles Drawer */}
              <button
                type="button"
                className={`btn sm ${!isRightPanelOpen ? 'primary' : ''}`}
                style={{ height: 38, gap: 6 }}
                onClick={() => setIsRightPanelOpen((prev) => !prev)}
                data-testid="map:btn_toggle_drawer"
                title={isRightPanelOpen ? 'Hide Vehicles Panel' : 'Show Active Vehicles Panel'}
              >
                {isRightPanelOpen ? (
                  <>
                    <ChevronRightIcon size={14} />
                    {isMalayalam ? 'പാനൽ മറയ്ക്കുക' : 'Hide Panel'}
                  </>
                ) : (
                  <>
                    <ChevronLeftIcon size={14} />
                    {isMalayalam ? `വാഹനങ്ങൾ (${activeShipments.length})` : `Active Vehicles (${activeShipments.length})`}
                  </>
                )}
              </button>
            </div>
          </div>

          <div className="attr">
            &copy; OpenStreetMap contributors &middot; Live GPS Telemetry
          </div>

          {/* Compact Non-Intrusive Bottom-Right HUD when Drawer is Hidden */}
          {!isRightPanelOpen && isMiniHudVisible && selectedShipment && selectedDriver && selectedVehicle && selectedTelemetry && (
            <div className="tc-floating-hud">
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 8 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <Avatar name={selectedDriver.name} size={28} />
                  <div>
                    <b style={{ fontSize: 13 }}>{selectedVehicle.reg}</b>
                    <small style={{ color: 'var(--muted)', display: 'block', fontSize: 11 }}>
                      {selectedShipment.from} &rarr; {selectedShipment.to}
                    </small>
                  </div>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                  <StatusChip
                    status={selectedShipment.delayed ? 'Delayed' : 'On-Trip'}
                    label={selectedShipment.delayed ? 'Delayed' : 'Moving'}
                  />
                  <button
                    type="button"
                    className="icon-btn"
                    style={{ width: 24, height: 24, border: 0, background: 'none' }}
                    onClick={() => setIsMiniHudVisible(false)}
                    title="Dismiss"
                  >
                    <CloseIcon size={12} />
                  </button>
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 11.5, color: 'var(--text-2)' }}>
                <span>Speed: <b style={{ color: 'var(--text)' }}>{speeds[selectedShipment.id] || 48} km/h</b></span>
                <span>Heading: <b style={{ color: 'var(--text)' }}>{getCompassHeading(selectedTelemetry.heading)}</b></span>
                <span>ETA: <b style={{ color: 'var(--text)' }}>{calculateEta(selectedShipment.prog)}</b></span>
              </div>

              <div style={{ display: 'flex', gap: 6, marginTop: 2 }}>
                <button
                  type="button"
                  className="btn sm"
                  style={{ flex: 1, height: 28, fontSize: 11.5, justifyContent: 'center' }}
                  onClick={() => setIsRightPanelOpen(true)}
                >
                  Expand Panel
                </button>
                <button
                  type="button"
                  className="btn sm primary"
                  style={{ flex: 1, height: 28, fontSize: 11.5, justifyContent: 'center' }}
                  onClick={() => showToast(`Dialing ${selectedDriver.name} (${selectedDriver.phone})...`)}
                >
                  Call Driver
                </button>
              </div>
            </div>
          )}
        </div>

        {/* Right Active Vehicles Drawer (Collapsible) */}
        {isRightPanelOpen && (
          <div className="card" style={{ display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
            <div className="card-h" style={{ paddingBottom: 12 }}>
              <div>
                <h3>{isMalayalam ? 'സജീവ വാഹനങ്ങൾ' : 'Active vehicles'}</h3>
                <p>
                  {filteredShipments.length} {isMalayalam ? 'ചരക്കുവാഹനങ്ങൾ ഓട്ടത്തിൽ' : 'moving loads · updates live'}
                </p>
              </div>
              <button
                type="button"
                className="icon-btn"
                style={{ width: 28, height: 28, borderRadius: 6 }}
                onClick={() => setIsRightPanelOpen(false)}
                title={isMalayalam ? 'പാനൽ ചുരുക്കുക' : 'Hide panel for full map'}
                aria-label="Hide panel"
              >
                <CloseIcon size={14} />
              </button>
            </div>

            {/* Vehicle List */}
            <div className="vlist">
              {filteredShipments.length === 0 ? (
                <div style={{ padding: 24, textAlign: 'center', color: 'var(--muted)' }}>
                  {isMalayalam ? 'വാഹനങ്ങൾ ഒന്നും കണ്ടെത്തിയില്ല.' : 'No vehicles match your filter.'}
                </div>
              ) : (
                filteredShipments.map((s, idx) => {
                  const driver = fleetStore.drivers.find((d) => d.id === s.driver);
                  const vehicle = fleetStore.vehicles.find((v) => v.id === s.vehicle);
                  const isSelected = selectedShipmentId === s.id;
                  const isStale = idx === 3;
                  const statusTag = isStale ? 'Stale' : s.delayed ? 'Delayed' : 'Moving';

                  return (
                    <div
                      key={s.id}
                      className={`vrow ${isSelected ? 'on' : ''}`}
                      onClick={() => handleLocateVehicle(s.id)}
                      data-testid={`map:vrow_${s.id}`}
                    >
                      <Avatar name={driver?.name || 'Driver'} size={32} />
                      <div style={{ flex: 1, minWidth: 0 }}>
                        <b>{vehicle?.reg || 'KL-XX'}</b>
                        <small>
                          {driver?.name} · {s.from} → {s.to}
                        </small>
                      </div>
                      <StatusChip
                        status={statusTag === 'Moving' ? 'On-Trip' : statusTag}
                        label={statusTag}
                      />
                    </div>
                  );
                })
              )}
            </div>

            {/* Selected Vehicle Detail HUD */}
            {selectedShipment && selectedDriver && selectedVehicle && selectedTelemetry ? (
              <div className="vdetail">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <b style={{ fontSize: 15 }}>{selectedVehicle.reg}</b>{' '}
                    <span style={{ color: 'var(--muted)', fontSize: 12 }}>
                      · {selectedVehicle.type} ({selectedVehicle.cap} t)
                    </span>
                  </div>
                  <button
                    type="button"
                    className="btn sm"
                    style={{ padding: '0 8px' }}
                    onClick={() => handleLocateVehicle(selectedShipment.id)}
                    title="Locate and track camera"
                  >
                    <TruckIcon size={14} /> Locate
                  </button>
                </div>

                <div className="kv" style={{ marginTop: 10 }}>
                  <span>{isMalayalam ? 'ഡ്രൈവർ' : 'Driver'}</span>
                  <b>{selectedDriver.name}</b>

                  <span>{isMalayalam ? 'റൂട്ട്' : 'Route (From → To)'}</span>
                  <b>{selectedShipment.from} &rarr; {selectedShipment.to}</b>

                  <span>{isMalayalam ? 'ഷിപ്പ്‌മെന്റ്' : 'Shipment'}</span>
                  <b className="mono">{selectedShipment.id}</b>

                  <span>{isMalayalam ? 'വേഗത' : 'Current speed'}</span>
                  <b>{speeds[selectedShipment.id] || 48} km/h</b>

                  <span>{isMalayalam ? 'ദിശ' : 'Heading'}</span>
                  <b>{getCompassHeading(selectedTelemetry.heading)}</b>

                  <span>{isMalayalam ? 'ജിപിഎസ് സ്ഥാനം' : 'GPS Coordinates'}</span>
                  <b className="mono" style={{ fontSize: 11.5 }}>
                    {selectedTelemetry.position[1].toFixed(4)}° N, {selectedTelemetry.position[0].toFixed(4)}° E
                  </b>

                  <span>{isMalayalam ? 'ടെലിമെട്രി പിംഗ്' : 'Telemetry ping'}</span>
                  <b>
                    {activeShipments.indexOf(selectedShipment) === 3 ? '6 min ago' : '12s ago'}
                  </b>

                  <span>{isMalayalam ? 'എത്തിച്ചേരുന്ന സമയം' : 'Destination ETA'}</span>
                  <b>{calculateEta(selectedShipment.prog)}</b>
                </div>

                <div style={{ display: 'flex', gap: 8, marginTop: 14 }}>
                  <button
                    type="button"
                    className="btn sm"
                    style={{ flex: 1, justifyContent: 'center' }}
                    onClick={() => navigate(`/shipments/${selectedShipment.id}`)}
                    data-testid="map:btn_view_timeline"
                  >
                    {isMalayalam ? 'ടൈംലൈൻ' : 'Timeline'}
                  </button>
                  <button
                    type="button"
                    className="btn sm primary"
                    style={{ flex: 1, justifyContent: 'center' }}
                    onClick={() =>
                      showToast(
                        isMalayalam
                          ? `${selectedDriver.name} (${selectedDriver.phone}) എന്ന നമ്പറിലേക്ക് വിളിക്കുന്നു...`
                          : `Dialing ${selectedDriver.name} (${selectedDriver.phone})...`
                      )
                    }
                    data-testid="map:btn_call_driver"
                  >
                    {isMalayalam ? 'വിളിക്കുക' : 'Call driver'}
                  </button>
                </div>
              </div>
            ) : (
              <div className="vdetail" style={{ color: 'var(--muted)', fontSize: 12.5 }}>
                {isMalayalam
                  ? 'വിവരങ്ങൾ കാണാൻ മാപ്പിലോ പട്ടികയിലോ ഒരു വാഹനം തിരഞ്ഞെടുക്കുക.'
                  : 'Select a vehicle on the map or list to inspect speed, driver, and ETA.'}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
