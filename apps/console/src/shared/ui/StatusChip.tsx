import React from 'react';

interface StatusChipProps {
  status: string;
  label?: string;
  className?: string;
}

export const StatusChip: React.FC<StatusChipProps> = ({ status, label, className = '' }) => {
  const norm = status.replace(/\s+/g, '-');
  return (
    <span className={`chip s-${norm} ${className}`}>
      {label || status}
    </span>
  );
};
