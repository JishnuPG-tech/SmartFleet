import React from 'react';

interface EnterpriseLogoProps {
  size?: number;
  showText?: boolean;
  subtitle?: string;
  className?: string;
}

export const EnterpriseLogo: React.FC<EnterpriseLogoProps> = ({
  size = 36,
  showText = true,
  subtitle,
  className = '',
}) => {
  return (
    <div
      className={`enterprise-logo-wrap ${className}`}
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        gap: size > 32 ? 12 : 9,
        userSelect: 'none',
      }}
    >
      {/* Precision Geometric Fleet Emblem */}
      <svg
        width={size}
        height={size}
        viewBox="0 0 48 48"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        style={{ flexShrink: 0 }}
      >
        <defs>
          <linearGradient id="tc-gradient-primary" x1="4" y1="4" x2="44" y2="44" gradientUnits="userSpaceOnUse">
            <stop stopColor="#10b981" />
            <stop offset="0.6" stopColor="#059669" />
            <stop offset="1" stopColor="#047857" />
          </linearGradient>
          <linearGradient id="tc-gradient-accent" x1="16" y1="12" x2="38" y2="34" gradientUnits="userSpaceOnUse">
            <stop stopColor="#34d399" />
            <stop offset="1" stopColor="#059669" />
          </linearGradient>
          <filter id="tc-shadow" x="0" y="2" width="48" height="46" filterUnits="userSpaceOnUse" colorInterpolationFilters="sRGB">
            <feDropShadow dx="0" dy="3" stdDeviation="2.5" floodColor="#047857" floodOpacity="0.35" />
          </filter>
        </defs>

        {/* Outer Shield Hex / Rounded Tech Container */}
        <rect
          x="3"
          y="3"
          width="42"
          height="42"
          rx="12"
          fill="url(#tc-gradient-primary)"
          filter="url(#tc-shadow)"
        />

        {/* Subtle Inner Border Inset */}
        <rect
          x="3.75"
          y="3.75"
          width="40.5"
          height="40.5"
          rx="11.25"
          stroke="rgba(255, 255, 255, 0.25)"
          strokeWidth="1.5"
        />

        {/* Geometric Interlocking "T" & "C" Route Nexus */}
        {/* Horizontal T-Bar & Forward Vector */}
        <path
          d="M13 16C13 14.8954 13.8954 14 15 14H33C34.1046 14 35 14.8954 35 16V17C35 18.1046 34.1046 19 33 19H27V33C27 34.1046 26.1046 35 25 35H23C21.8954 35 21 34.1046 21 33V19H15C13.8954 19 13 18.1046 13 17V16Z"
          fill="#ffffff"
          opacity="0.95"
        />

        {/* Interlocking "C" Logistics Loop with High-Speed Forward Arrow */}
        <path
          d="M32 23L36 27L32 31M35 27H29"
          stroke="#ffffff"
          strokeWidth="2.4"
          strokeLinecap="round"
          strokeLinejoin="round"
        />

        {/* Dynamic Forward Chevron Nexus */}
        <path
          d="M12 25L17 29L12 33"
          stroke="rgba(255, 255, 255, 0.45)"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
      </svg>

      {/* Typography */}
      {showText && (
        <div style={{ display: 'flex', flexDirection: 'column', minWidth: 0 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
            <span
              style={{
                fontSize: size > 32 ? 17 : 15,
                fontWeight: 900,
                letterSpacing: '-0.03em',
                lineHeight: 1.1,
                color: 'var(--text)',
                fontFamily: 'inherit',
              }}
            >
              TruckConnect
            </span>
            <span
              style={{
                fontSize: 9.5,
                fontWeight: 800,
                letterSpacing: '0.08em',
                textTransform: 'uppercase',
                padding: '1.5px 5px',
                borderRadius: 4,
                background: 'color-mix(in srgb, var(--accent) 15%, transparent)',
                color: 'var(--accent)',
                lineHeight: 1,
              }}
            >
              OPS
            </span>
          </div>

          <span
            style={{
              fontSize: size > 32 ? 11.5 : 10.5,
              fontWeight: 500,
              color: 'var(--muted)',
              marginTop: 2,
              lineHeight: 1,
              letterSpacing: '0.01em',
            }}
          >
            {subtitle || 'Fleet Command Platform'}
          </span>
        </div>
      )}
    </div>
  );
};
