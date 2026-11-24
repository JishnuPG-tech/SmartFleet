import React from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import { Shell } from './Shell';
import { LoginView } from '../features/auth/LoginView';
import { DashboardView } from '../features/dashboard/DashboardView';
import { ShipmentsView } from '../features/shipments/ShipmentsView';
import { ShipmentDetailPage } from '../features/shipments/ShipmentDetailPage';
import { FleetView } from '../features/fleet/FleetView';
import { DriverDetailView } from '../features/fleet/DriverDetailView';
import { VehicleDetailView } from '../features/fleet/VehicleDetailView';
import { ReportsView } from '../features/reports/ReportsView';
import { AlertsCenterPage } from '../features/alerts/AlertsCenterPage';
import { ReleasesView } from '../features/releases/ReleasesView';
import { TeamView } from '../features/team/TeamView';
import { SettingsView } from '../features/settings/SettingsView';
import { AuditLogView } from '../features/audit/AuditLogView';
import { Forbidden403 } from '../shared/ui/Forbidden403';
import { fleetStore } from '../shared/api/client';

const AdminGuard: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const user = fleetStore.currentUser;
  if (!user || !user.isOwner) {
    return <Forbidden403 />;
  }
  return <>{children}</>;
};

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginView />,
  },
  {
    path: '/',
    element: <Shell />,
    children: [
      {
        index: true,
        element: <Navigate to="/dashboard" replace />,
      },
      {
        path: 'dashboard',
        element: <DashboardView />,
      },
      {
        path: 'map',
        element: <Navigate to="/shipments" replace />,
      },
      {
        path: 'tracking',
        element: <Navigate to="/shipments" replace />,
      },
      {
        path: 'shipments',
        element: <ShipmentsView />,
      },
      {
        path: 'shipments/:id',
        element: <ShipmentDetailPage />,
      },
      {
        path: 'fleet',
        element: <FleetView />,
      },
      {
        path: 'fleet/drivers/:id',
        element: <DriverDetailView />,
      },
      {
        path: 'fleet/vehicles/:id',
        element: <VehicleDetailView />,
      },
      {
        path: 'reports',
        element: <ReportsView />,
      },
      {
        path: 'alerts',
        element: <AlertsCenterPage />,
      },
      {
        path: 'releases',
        element: (
          <AdminGuard>
            <ReleasesView />
          </AdminGuard>
        ),
      },
      {
        path: 'team',
        element: (
          <AdminGuard>
            <TeamView />
          </AdminGuard>
        ),
      },
      {
        path: 'audit',
        element: (
          <AdminGuard>
            <AuditLogView />
          </AdminGuard>
        ),
      },
      {
        path: 'settings',
        element: (
          <AdminGuard>
            <SettingsView />
          </AdminGuard>
        ),
      },
      {
        path: '*',
        element: <Navigate to="/dashboard" replace />,
      },
    ],
  },
]);
