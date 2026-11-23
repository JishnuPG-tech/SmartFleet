import React, { useState } from 'react';
import { Modal } from '../../shared/ui/Modal';
import { Driver, Vehicle } from '../../shared/api/types';
import { fleetStore } from '../../shared/api/client';
import { useToast } from '../../shared/ui/Toast';

interface DriverVehicleModalProps {
  isOpen: boolean;
  onClose: () => void;
  entityType: 'driver' | 'vehicle';
  driverToEdit?: Driver | null;
  vehicleToEdit?: Vehicle | null;
}

export const DriverVehicleModal: React.FC<DriverVehicleModalProps> = ({
  isOpen,
  onClose,
  entityType,
  driverToEdit,
  vehicleToEdit,
}) => {
  const { showToast } = useToast();

  // Driver fields
  const [driverName, setDriverName] = useState(driverToEdit?.name || '');
  const [driverPhone, setDriverPhone] = useState(driverToEdit?.phone || '');
  const [licenceNo, setLicenceNo] = useState(driverToEdit?.lic || '');
  const [licenceExp, setLicenceExp] = useState(
    driverToEdit ? driverToEdit.licExp.slice(0, 10) : '2027-11-20'
  );
  const [driverStatus, setDriverStatus] = useState<Driver['status']>(
    driverToEdit?.status || 'Available'
  );
  const [defaultVehicle, setDefaultVehicle] = useState(driverToEdit?.vehicle || 'V1');

  // Vehicle fields
  const [regNo, setRegNo] = useState(vehicleToEdit?.reg || '');
  const [vType, setVType] = useState<Vehicle['type']>(vehicleToEdit?.type || 'Lorry');
  const [vCap, setVCap] = useState(vehicleToEdit?.cap || 10);
  const [vStatus, setVStatus] = useState<Vehicle['status']>(vehicleToEdit?.status || 'Available');
  const [defaultDriver, setDefaultDriver] = useState(vehicleToEdit?.driver || 'D1');
  const [docExp, setDocExp] = useState(
    vehicleToEdit ? vehicleToEdit.docExp.slice(0, 10) : '2027-11-20'
  );
  const [odometer, setOdometer] = useState(vehicleToEdit?.km || 50000);

  const isEditing = !!(driverToEdit || vehicleToEdit);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (entityType === 'driver') {
      if (driverToEdit) {
        Object.assign(driverToEdit, {
          name: driverName,
          phone: driverPhone,
          lic: licenceNo,
          licExp: new Date(licenceExp).toISOString(),
          status: driverStatus,
          vehicle: defaultVehicle,
        });
        fleetStore.log('Edited driver', driverName);
        showToast(`Driver ${driverName} profile saved.`);
      } else {
        const newD: Driver = {
          id: 'D' + (fleetStore.drivers.length + 1),
          name: driverName,
          phone: driverPhone,
          lic: licenceNo,
          licExp: new Date(licenceExp).toISOString(),
          status: driverStatus,
          onTime: 90,
          trips: 0,
          vehicle: defaultVehicle,
          version: '2.1.0',
        };
        fleetStore.drivers.push(newD);
        const pin = Math.floor(1000 + Math.random() * 9000);
        fleetStore.log('Added driver', driverName, `PIN generated: ${pin}`);
        showToast(`Driver added. One-time PIN: ${pin}`);
      }
    } else {
      if (vehicleToEdit) {
        Object.assign(vehicleToEdit, {
          reg: regNo.toUpperCase(),
          type: vType,
          cap: Number(vCap),
          status: vStatus,
          driver: defaultDriver,
          docExp: new Date(docExp).toISOString(),
          km: Number(odometer),
        });
        fleetStore.log('Edited vehicle', regNo);
        showToast(`Vehicle ${regNo} updated.`);
      } else {
        const newV: Vehicle = {
          id: 'V' + (fleetStore.vehicles.length + 1),
          reg: regNo.toUpperCase(),
          type: vType,
          cap: Number(vCap),
          status: vStatus,
          driver: defaultDriver,
          docExp: new Date(docExp).toISOString(),
          km: Number(odometer),
        };
        fleetStore.vehicles.push(newV);
        fleetStore.log('Added vehicle', regNo);
        showToast(`Vehicle ${regNo} registered.`);
      }
    }

    fleetStore.notify();
    onClose();
  };

  const handleResetPin = () => {
    if (!driverToEdit) return;
    const pin = fleetStore.resetDriverPin(driverToEdit.id);
    showToast(`New one-time login PIN for ${driverToEdit.name}: ${pin}`);
  };

  const handleRevokeDevice = () => {
    if (!driverToEdit) return;
    fleetStore.revokeDriverDevice(driverToEdit.id);
    showToast(`Device session revoked for ${driverToEdit.name}.`);
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={`${isEditing ? 'Edit' : 'Add'} ${entityType === 'driver' ? 'Driver' : 'Vehicle'}`}
      maxWidth="560px"
      testId="modal:driver_vehicle_form"
    >
      <form onSubmit={handleSubmit}>
        {entityType === 'driver' ? (
          <>
            <label className="fld">
              <span>Full Name</span>
              <input
                required
                value={driverName}
                onChange={(e) => setDriverName(e.target.value)}
                placeholder="Driver full name"
              />
            </label>

            <label className="fld">
              <span>Mobile Phone</span>
              <input
                required
                value={driverPhone}
                onChange={(e) => setDriverPhone(e.target.value)}
                placeholder="+91 98470 XXXXX"
              />
            </label>

            <label className="fld">
              <span>Licence Number</span>
              <input
                required
                value={licenceNo}
                onChange={(e) => setLicenceNo(e.target.value)}
                placeholder="KL-0720110012345"
              />
            </label>

            <label className="fld">
              <span>Licence Expiry</span>
              <input
                type="date"
                required
                value={licenceExp}
                onChange={(e) => setLicenceExp(e.target.value)}
              />
            </label>

            <label className="fld">
              <span>Operational Status</span>
              <select
                value={driverStatus}
                onChange={(e) => setDriverStatus(e.target.value as Driver['status'])}
              >
                <option value="Available">Available</option>
                <option value="On Trip">On Trip</option>
                <option value="On Leave">On Leave</option>
                <option value="Offline">Offline</option>
                <option value="Inactive">Inactive</option>
              </select>
            </label>

            <label className="fld">
              <span>Default Vehicle</span>
              <select
                value={defaultVehicle}
                onChange={(e) => setDefaultVehicle(e.target.value)}
              >
                {fleetStore.vehicles.map((v) => (
                  <option key={v.id} value={v.id}>
                    {v.reg} ({v.type})
                  </option>
                ))}
              </select>
            </label>
          </>
        ) : (
          <>
            <label className="fld">
              <span>Registration Number</span>
              <input
                required
                value={regNo}
                onChange={(e) => setRegNo(e.target.value)}
                placeholder="KL-07-AZ-1200"
              />
            </label>

            <label className="fld">
              <span>Vehicle Type</span>
              <select
                value={vType}
                onChange={(e) => setVType(e.target.value as Vehicle['type'])}
              >
                <option value="Lorry">Lorry</option>
                <option value="Mini truck">Mini truck</option>
                <option value="Container">Container</option>
                <option value="Pickup">Pickup</option>
              </select>
            </label>

            <label className="fld">
              <span>Payload Capacity (Tonnes)</span>
              <input
                type="number"
                step="0.5"
                min="0.5"
                required
                value={vCap}
                onChange={(e) => setVCap(Number(e.target.value))}
              />
            </label>

            <label className="fld">
              <span>Status</span>
              <select
                value={vStatus}
                onChange={(e) => setVStatus(e.target.value as Vehicle['status'])}
              >
                <option value="Available">Available</option>
                <option value="On Trip">On Trip</option>
                <option value="Maintenance">Maintenance</option>
              </select>
            </label>

            <label className="fld">
              <span>Default Driver</span>
              <select
                value={defaultDriver}
                onChange={(e) => setDefaultDriver(e.target.value)}
              >
                {fleetStore.drivers.map((d) => (
                  <option key={d.id} value={d.id}>
                    {d.name}
                  </option>
                ))}
              </select>
            </label>

            <label className="fld">
              <span>Statutory Document Expiry</span>
              <input
                type="date"
                required
                value={docExp}
                onChange={(e) => setDocExp(e.target.value)}
              />
            </label>

            <label className="fld full">
              <span>Cumulative Odometer (km)</span>
              <input
                type="number"
                min="0"
                value={odometer}
                onChange={(e) => setOdometer(Number(e.target.value))}
              />
            </label>
          </>
        )}

        <div className="mf">
          {isEditing && entityType === 'driver' && (
            <>
              <button
                type="button"
                className="btn"
                onClick={handleResetPin}
                data-testid="driver:btn_reset_pin"
              >
                Reset PIN
              </button>
              <button
                type="button"
                className="btn"
                onClick={handleRevokeDevice}
                data-testid="driver:btn_revoke_device"
              >
                Revoke device
              </button>
            </>
          )}

          <button type="button" className="btn" onClick={onClose}>
            Cancel
          </button>
          <button type="submit" className="btn primary" data-testid="fleet:btn_submit">
            {isEditing ? 'Save' : 'Add'}
          </button>
        </div>
      </form>
    </Modal>
  );
};
