import React, { useState, useEffect } from 'react';
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import {
  TruckIcon,
  DashboardIcon,
  MapPinIcon,
  BoxIcon,
  ReportsIcon,
  SearchIcon,
  BellIcon,
  ThemeIcon,
  PlusIcon,
  DocIcon,
  LayersIcon,
  UsersIcon,
  SettingsIcon,
  ShieldIcon,
  LogOutIcon,
  MenuIcon,
} from '../shared/icons';
import { EnterpriseLogo } from '../shared/ui/EnterpriseLogo';
import { Avatar } from '../shared/ui/Avatar';
import { ThemePopover } from '../shared/theme/ThemePopover';
import { AlertPopover } from '../features/alerts/AlertPopover';
import { CommandPalette } from '../features/command/CommandPalette';
import { ShipmentFormModal } from '../features/shipments/ShipmentFormModal';
import { fleetStore } from '../shared/api/client';
import { useToast } from '../shared/ui/Toast';
import { useLanguage } from '../shared/i18n/LanguageContext';

const LiveSyncBadge: React.FC<{ isMalayalam: boolean }> = ({ isMalayalam }) => {
  const [secondsAgo, setSecondsAgo] = useState(0);

  useEffect(() => {
    const timer = setInterval(() => {
      setSecondsAgo((s) => (s >= 25 ? 0 : s + 5));
    }, 5000);
    return () => clearInterval(timer);
  }, []);

  return (
    <div className="sync-badge">
      <span className="sync-live-label">{isMalayalam ? 'തത്സമയം' : 'LIVE'}</span>
      <span>
        {secondsAgo === 0
          ? isMalayalam
            ? 'ഇപ്പോൾ'
            : 'connected'
          : isMalayalam
          ? `${secondsAgo} സെക്കന്റ് മുൻപ്`
          : `${secondsAgo}s ago`}
      </span>
    </div>
  );
};

export const Shell: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { showToast } = useToast();
  const { language, toggleLanguage, isMalayalam } = useLanguage();

  const [isThemeOpen, setIsThemeOpen] = useState(false);
  const [isAlertOpen, setIsAlertOpen] = useState(false);
  const [isCommandOpen, setIsCommandOpen] = useState(false);
  const [isNewShipmentOpen, setIsNewShipmentOpen] = useState(false);
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState(false);
  const [globalSearch, setGlobalSearch] = useState('');

  // Sync listener for store mutations

  // Close popovers on navigation
  useEffect(() => {
    setIsThemeOpen(false);
    setIsAlertOpen(false);
  }, [location.pathname]);

  const currentUser = fleetStore.currentUser;
  const isOwner = currentUser?.isOwner ?? true;

  const ROUTE_META_EN: Record<string, [string, string]> = {
    '/dashboard': ['Dashboard', 'Operations Command Center · Kerala Fleet Network'],
    '/map': ['Live Fleet Tracking', 'Live GPS telemetry, trip checkpoints & 1-click Google Maps navigation'],
    '/tracking': ['Live Fleet Tracking', 'Live GPS telemetry, trip checkpoints & 1-click Google Maps navigation'],
    '/shipments': ['Shipments Dispatch', 'Create, allocate, and monitor consignment flows'],
    '/fleet': ['Drivers & Vehicles', 'Directory, compliance credentials, and registration reviews'],
    '/reports': ['Executive Analytics', 'Delivery SLAs and 90-day product targets'],
    '/alerts': ['Alerts & Exceptions', 'Critical incident triage, timeouts, and document reminders'],
    '/releases': ['App Releases', 'Driver APK distribution, rollout cohorts, and kill switch'],
    '/team': ['Team & Permissions', 'Admin and dispatcher account governance'],
    '/audit': ['Audit Trail', 'Immutable field-level change history & compliance log'],
    '/settings': ['Account & Security', 'Profile, 2FA configuration, and session watchdog'],
  };

  const ROUTE_META_ML: Record<string, [string, string]> = {
    '/dashboard': ['ഡാഷ്‌ബോർഡ്', 'ഓപ്പറേഷൻസ് കമാൻഡ് സെന്റർ · കേരള ഫ്ലീറ്റ് നെറ്റ്‌വർക്ക്'],
    '/map': ['തത്സമയ ട്രാക്കിംഗ്', 'തത്സമയ ജിപിഎസ് ടെലിമെട്രി, ഗൂഗിൾ മാപ്പ് നാവിഗേഷൻ'],
    '/tracking': ['തത്സമയ ട്രാക്കിംഗ്', 'തത്സമയ ജിപിഎസ് ടെലിമെട്രി, ഗൂഗിൾ മാപ്പ് നാവിഗേഷൻ'],
    '/shipments': ['ചരക്ക് നീക്കം', 'ഷിപ്പ്‌മെന്റുകൾ നിർമ്മിക്കുക, നിയോഗിക്കുക, നിരീക്ഷിക്കുക'],
    '/fleet': ['ഡ്രൈവർമാരും വാഹനങ്ങളും', 'രേഖകൾ, ലൈസൻസുകൾ, പരിശോധനകൾ'],
    '/reports': ['റിപ്പോർട്ടുകൾ', 'ഡെലിവറി പ്രകടനം, 90-ദിന ലക്ഷ്യങ്ങൾ'],
    '/alerts': ['മുന്നറിയിപ്പുകൾ', 'അടിയന്തര മുന്നറിയിപ്പുകൾ, സമയപരിധികൾ'],
    '/releases': ['ആപ്പ് റിലീസുകൾ', 'ഡ്രൈവർ ആപ്പ് അപ്‌ഡേറ്റുകൾ'],
    '/team': ['ഓപ്പറേറ്റർമാർ', 'അഡ്മിൻ അക്കൗണ്ടുകൾ, അനുമതികൾ'],
    '/audit': ['ഓഡിറ്റ് ലോഗ്', 'സമ്പൂർണ്ണ മാറ്റങ്ങളുടെ രേഖ'],
    '/settings': ['ക്രമീകരണങ്ങൾ', 'പ്രൊഫൈൽ, സുരക്ഷ'],
  };

  const activeMetaTable = isMalayalam ? ROUTE_META_ML : ROUTE_META_EN;
  const currentMeta = activeMetaTable[location.pathname] || [
    isMalayalam ? 'ഓപ്പറേഷൻസ് കൺസോൾ' : 'Operations Console',
    'TruckConnect Fleet Management Platform',
  ];

  const pendingCount = fleetStore.shipments.filter((s) =>
    ['Created', 'Assigned', 'Rejected'].includes(s.status)
  ).length;

  const openAlertCount = fleetStore.alerts.filter((a) => !a.done).length;

  const handleSignOut = () => {
    fleetStore.logout();
    showToast(isMalayalam ? 'കൺസോളിൽ നിന്ന് പുറത്തുകടന്നു' : 'Signed out of Operations Console.');
    navigate('/login');
  };

  return (
    <div style={{ display: 'flex', minHeight: '100vh', width: '100%' }}>
      {/* Sidebar Navigation */}
      <aside className={`sidebar ${isSidebarCollapsed ? 'collapsed' : ''}`}>
        <div className="brand" style={{ padding: '4px 6px 20px' }}>
          <EnterpriseLogo size={36} subtitle={isMalayalam ? 'ഓപ്പറേഷൻസ് കൺസോൾ' : 'Operations Console'} />
        </div>

        <div className="sec">{isMalayalam ? 'വർക്ക്‌സ്‌പേസ്' : 'Workspace'}</div>
        <nav className="nav-group">
          <NavLink
            to="/dashboard"
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            data-testid="nav:btn_dashboard"
          >
            <DashboardIcon />
            {isMalayalam ? 'ഡാഷ്‌ബോർഡ്' : 'Dashboard'}
          </NavLink>

          <NavLink
            to="/map"
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            data-testid="nav:btn_map"
          >
            <MapPinIcon />
            {isMalayalam ? 'തത്സമയ ട്രാക്കിംഗ്' : 'Live Tracking'}
          </NavLink>

          <NavLink
            to="/shipments"
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            data-testid="nav:btn_shipments"
          >
            <BoxIcon />
            {isMalayalam ? 'ചരക്കുകൾ' : 'Shipments'}
            {pendingCount > 0 && <span className="badge">{pendingCount}</span>}
          </NavLink>

          <NavLink
            to="/fleet"
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            data-testid="nav:btn_fleet"
          >
            <TruckIcon />
            {isMalayalam ? 'ഫ്ലീറ്റ്' : 'Drivers & vehicles'}
          </NavLink>

          <NavLink
            to="/reports"
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            data-testid="nav:btn_reports"
          >
            <ReportsIcon />
            {isMalayalam ? 'റിപ്പോർട്ടുകൾ' : 'Reports'}
          </NavLink>
        </nav>

        <div className="sec">{isMalayalam ? 'പ്രവർത്തനങ്ങൾ' : 'Operations'}</div>
        <nav className="nav-group">
          <NavLink
            to="/alerts"
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            data-testid="nav:btn_alerts"
          >
            <ShieldIcon />
            {isMalayalam ? 'അലേർട്ടുകൾ' : 'Alerts Center'}
            {openAlertCount > 0 && <span className="badge">{openAlertCount}</span>}
          </NavLink>
        </nav>

        {isOwner && (
          <>
            <div className="sec">{isMalayalam ? 'ഭരണം' : 'Administration'}</div>
            <nav className="nav-group">
              <NavLink
                to="/releases"
                className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
                data-testid="nav:btn_releases"
              >
                <LayersIcon />
                {isMalayalam ? 'ആപ്പ് റിലീസുകൾ' : 'App Releases'}
              </NavLink>

              <NavLink
                to="/team"
                className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
                data-testid="nav:btn_team"
              >
                <UsersIcon />
                {isMalayalam ? 'ടീം' : 'Team & Access'}
              </NavLink>

              <NavLink
                to="/audit"
                className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
                data-testid="nav:btn_audit"
              >
                <DocIcon />
                {isMalayalam ? 'ഓഡിറ്റ് ലോഗ്' : 'Audit Log'}
              </NavLink>

              <NavLink
                to="/settings"
                className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
                data-testid="nav:btn_settings"
              >
                <SettingsIcon />
                {isMalayalam ? 'ക്രമീകരണങ്ങൾ' : 'Settings'}
              </NavLink>
            </nav>
          </>
        )}

        <LiveSyncBadge isMalayalam={isMalayalam} />

        {currentUser && (
          <div className="side-foot" style={{ marginTop: 14 }}>
            <Avatar name={currentUser.name} size={34} />
            <div style={{ flex: 1, minWidth: 0 }}>
              <b>{currentUser.name}</b>
              <small>{currentUser.roleTitle}</small>
            </div>
            <button
              type="button"
              className="icon-btn"
              style={{ width: 30, height: 30, borderRadius: 8 }}
              onClick={handleSignOut}
              title="Sign Out"
              aria-label="Sign out"
              data-testid="shell:btn_signout"
            >
              <LogOutIcon size={14} />
            </button>
          </div>
        )}
      </aside>

      {/* Main Content Area */}
      <div className="wrap">
        {/* Top Header */}
        <header className="top">
          <button
            type="button"
            className="sidebar-toggle-btn"
            onClick={() => setIsSidebarCollapsed((prev) => !prev)}
            title={isSidebarCollapsed ? (isMalayalam ? 'സൈഡ്ബാർ കാണിക്കുക' : 'Show Sidebar') : (isMalayalam ? 'സൈഡ്ബാർ മറയ്ക്കുക' : 'Hide Sidebar')}
            aria-label={isSidebarCollapsed ? 'Show Sidebar' : 'Hide Sidebar'}
            data-testid="shell:btn_toggle_sidebar"
          >
            <MenuIcon size={18} />
          </button>

          <div>
            <h1 id="pt">{currentMeta[0]}</h1>
            <div className="sub" id="ps">
              {currentMeta[1]}
            </div>
          </div>

          <label
            className="search"
            onClick={() => setIsCommandOpen(true)}
            style={{ cursor: 'pointer' }}
          >
            <SearchIcon />
            <input
              type="text"
              placeholder={isMalayalam ? 'തിരയുക...' : 'Search shipment, driver, vehicle...'}
              value={globalSearch}
              onChange={(e) => setGlobalSearch(e.target.value)}
              onFocus={() => setIsCommandOpen(true)}
              data-testid="shell:input_search"
            />
            <span className="kbd">⌘K</span>
          </label>

          {/* Language Selector Button (EN / ML) */}
          <button
            type="button"
            className="btn sm"
            onClick={() => {
              toggleLanguage();
              showToast(language === 'en' ? 'ഭാഷ: മലയാളം' : 'Language: English');
            }}
            title={language === 'en' ? 'Switch to Malayalam' : 'Switch to English'}
            style={{
              fontWeight: 800,
              letterSpacing: '0.04em',
              background: language === 'ml' ? 'var(--accent-soft)' : 'var(--surface)',
              color: language === 'ml' ? 'var(--accent)' : 'inherit',
              borderColor: language === 'ml' ? 'var(--accent)' : 'var(--border)',
            }}
            data-testid="shell:btn_toggle_lang"
          >
            {language === 'en' ? 'മലയാളം' : 'English'}
          </button>

          {/* Alerts Bell */}
          <div className="rel">
            <button
              type="button"
              className="icon-btn"
              onClick={() => {
                setIsAlertOpen(!isAlertOpen);
                setIsThemeOpen(false);
              }}
              aria-label="Alerts"
              data-testid="top:btn_bell"
            >
              <BellIcon />
              {openAlertCount > 0 && <span className="pip" />}
            </button>
            <AlertPopover isOpen={isAlertOpen} onClose={() => setIsAlertOpen(false)} />
          </div>

          {/* Appearance Switcher */}
          <div className="rel">
            <button
              type="button"
              className="icon-btn"
              onClick={() => {
                setIsThemeOpen(!isThemeOpen);
                setIsAlertOpen(false);
              }}
              aria-label="Appearance options"
              data-testid="top:btn_theme_toggle"
            >
              <ThemeIcon />
            </button>
            <ThemePopover isOpen={isThemeOpen} onClose={() => setIsThemeOpen(false)} />
          </div>

          {/* New Shipment Modal Button */}
          <button
            type="button"
            className="btn primary"
            onClick={() => setIsNewShipmentOpen(true)}
            data-testid="top:btn_new_shipment"
          >
            <PlusIcon />
            {isMalayalam ? 'പുതിയ ചരക്ക്' : 'New shipment'}
          </button>
        </header>

        {/* Routed View */}
        <main className="main-content">
          <Outlet />
        </main>
      </div>

      {/* Global Modals */}
      <CommandPalette
        isOpen={isCommandOpen}
        onClose={() => setIsCommandOpen(false)}
        onOpenNewShipment={() => setIsNewShipmentOpen(true)}
      />

      <ShipmentFormModal
        isOpen={isNewShipmentOpen}
        onClose={() => setIsNewShipmentOpen(false)}
      />
    </div>
  );
};
