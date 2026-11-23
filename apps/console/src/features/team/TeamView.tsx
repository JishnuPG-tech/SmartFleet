import React, { useState, useEffect } from 'react';
import { fleetStore } from '../../shared/api/client';
import { TeamMember, UserRole } from '../../shared/api/types';
import { StatusChip } from '../../shared/ui/StatusChip';
import { Avatar } from '../../shared/ui/Avatar';
import { Modal } from '../../shared/ui/Modal';
import { useToast } from '../../shared/ui/Toast';
import { PlusIcon } from '../../shared/icons';

export const TeamView: React.FC = () => {
  const { showToast } = useToast();
  const [, setTick] = useState(0);

  const [isInviteOpen, setIsInviteOpen] = useState(false);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [role, setRole] = useState<UserRole>('dispatcher');

  useEffect(() => {
    return fleetStore.subscribe(() => setTick((t) => t + 1));
  }, []);

  const team = fleetStore.team;

  const handleInviteSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fleetStore.addTeamMember({
      name,
      email,
      phone,
      role,
      isActive: true,
    });
    showToast(`Operator ${name} provisioned with role: ${role}`);
    setIsInviteOpen(false);
    setName('');
    setEmail('');
    setPhone('');
  };

  const handleToggleActive = (member: TeamMember) => {
    fleetStore.toggleTeamMember(member.id);
    showToast(member.isActive ? `${member.name} deactivated and sessions terminated` : `${member.name} activated`);
  };

  return (
    <div className="view-anim">
      <div className="toolbar">
        <div>
          <h3 style={{ fontSize: 16, fontWeight: 800 }}>Operator &amp; Staff Administration</h3>
          <p style={{ color: 'var(--muted)', fontSize: 12.5 }}>
            Manage privileged accounts, RBAC entitlements, and active console sessions
          </p>
        </div>

        <span style={{ flex: 1 }} />

        <button
          className="btn primary"
          onClick={() => setIsInviteOpen(true)}
          data-testid="team:btn_invite"
        >
          <PlusIcon /> Provision Operator
        </button>
      </div>

      <div className="card" style={{ marginTop: 16 }}>
        <div className="scroll">
          <table className="tbl" data-testid="team:table">
            <thead>
              <tr>
                <th>Operator</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Role</th>
                <th>Status</th>
                <th>Last Active</th>
                <th>Session Governance</th>
              </tr>
            </thead>
            <tbody>
              {team.map((m) => (
                <tr key={m.id} data-testid={`team:row_${m.id}`}>
                  <td>
                    <div className="person">
                      <Avatar name={m.name} size={30} />
                      <span style={{ fontWeight: 700 }}>{m.name}</span>
                    </div>
                  </td>
                  <td className="mono">{m.email}</td>
                  <td className="mono">{m.phone}</td>
                  <td>
                    <StatusChip
                      status={m.role === 'super_admin' ? 'Active' : 'Assigned'}
                      label={m.role === 'super_admin' ? 'Super Admin' : 'Dispatcher'}
                    />
                  </td>
                  <td>
                    <StatusChip
                      status={m.isActive ? 'OK' : 'Inactive'}
                      label={m.isActive ? 'Active' : 'Disabled'}
                    />
                  </td>
                  <td className="mono">{m.lastLogin}</td>
                  <td>
                    <button
                      className={`btn sm ${m.isActive ? 'danger' : ''}`}
                      onClick={() => handleToggleActive(m)}
                      data-testid={`team:btn_toggle_${m.id}`}
                    >
                      {m.isActive ? 'Revoke Access' : 'Restore Access'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Invite Modal */}
      <Modal
        isOpen={isInviteOpen}
        onClose={() => setIsInviteOpen(false)}
        title="Provision Console Operator"
        maxWidth="500px"
        testId="modal:invite_operator"
      >
        <form onSubmit={handleInviteSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 14, padding: 20 }}>
          <label className="fld">
            <span>Full Name</span>
            <input required value={name} onChange={(e) => setName(e.target.value)} placeholder="Operator name" />
          </label>

          <label className="fld">
            <span>Corporate Email</span>
            <input type="email" required value={email} onChange={(e) => setEmail(e.target.value)} placeholder="name@truckconnect.in" />
          </label>

          <label className="fld">
            <span>Phone Number</span>
            <input required value={phone} onChange={(e) => setPhone(e.target.value)} placeholder="+91 94470 XXXXX" />
          </label>

          <label className="fld">
            <span>Privilege Role</span>
            <select value={role} onChange={(e) => setRole(e.target.value as UserRole)}>
              <option value="dispatcher">Dispatcher (Shipments &amp; Fleet Operations)</option>
              <option value="super_admin">Super Admin (Full Company &amp; Releases Governance)</option>
            </select>
          </label>

          <div className="mf">
            <button type="button" className="btn" onClick={() => setIsInviteOpen(false)}>
              Cancel
            </button>
            <button type="submit" className="btn primary">
              Create &amp; Issue Credentials
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
