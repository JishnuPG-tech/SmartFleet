/**
 * Google Maps Universal Redirection & Tracking Utility
 *
 * Generates direct Google Maps directions and tracking URLs that work 100% free:
 * - On Mobile (Android / iOS): Opens the preinstalled native Google Maps app directly.
 * - On Desktop: Opens Google Maps in a high-performance browser tab with live traffic & directions.
 * - Zero API keys required. Zero map canvas overhead.
 */

export interface GoogleMapsRouteParams {
  origin?: string;
  destination: string;
  currentCoords?: [number, number]; // [lng, lat]
  driverName?: string;
}

export function getGoogleMapsTrackingUrl(params: GoogleMapsRouteParams): string {
  const { origin, destination, currentCoords } = params;

  // 1. If real-time driver GPS coordinates are available, route from current driver location
  if (currentCoords && currentCoords.length === 2 && currentCoords[0] && currentCoords[1]) {
    const [lng, lat] = currentCoords;
    const dest = encodeURIComponent(destination || 'Kerala');
    return `https://www.google.com/maps/dir/?api=1&origin=${lat},${lng}&destination=${dest}&travelmode=driving`;
  }

  // 2. Route from pickup origin to drop destination
  if (origin && destination) {
    const orig = encodeURIComponent(origin);
    const dest = encodeURIComponent(destination);
    return `https://www.google.com/maps/dir/?api=1&origin=${orig}&destination=${dest}&travelmode=driving`;
  }

  // 3. Fallback: Search the destination or location directly
  const query = encodeURIComponent(destination || origin || 'Kerala');
  return `https://www.google.com/maps/search/?api=1&query=${query}`;
}

export function openGoogleMapsTracking(params: GoogleMapsRouteParams) {
  const url = getGoogleMapsTrackingUrl(params);
  if (typeof window !== 'undefined') {
    window.open(url, '_blank', 'noopener,noreferrer');
  }
}
