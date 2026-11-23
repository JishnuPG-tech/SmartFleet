import React, { useState, useEffect } from 'react';
import { Modal } from '../../shared/ui/Modal';
import { Shipment, CITY_NAMES } from '../../shared/api/types';
import { fleetStore } from '../../shared/api/client';
import { useToast } from '../../shared/ui/Toast';

interface ShipmentFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  shipmentToEdit?: Shipment | null;
}

export const ShipmentFormModal: React.FC<ShipmentFormModalProps> = ({
  isOpen,
  onClose,
  shipmentToEdit,
}) => {
  const { showToast } = useToast();

  const [from, setFrom] = useState(CITY_NAMES[0]);
  const [to, setTo] = useState(CITY_NAMES[3]);
  const [pAddr, setPAddr] = useState('');
  const [dAddr, setDAddr] = useState('');
  const [goods, setGoods] = useState('');
  const [qty, setQty] = useState(1);
  const [wt, setWt] = useState(5.0);
  const [sched, setSched] = useState(() => {
    const d = new Date(Date.now() + 3600000);
    return new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
  });
  const [consignee, setConsignee] = useState('');
  const [cphone, setCphone] = useState('');
  const [driverId, setDriverId] = useState('');
  const [vehicleId, setVehicleId] = useState('');
  const [note, setNote] = useState('');
  const [warnings, setWarnings] = useState<string[]>([]);

  useEffect(() => {
    if (shipmentToEdit) {
      setFrom(shipmentToEdit.from);
      setTo(shipmentToEdit.to);
      setPAddr(shipmentToEdit.pAddr || '');
      setDAddr(shipmentToEdit.dAddr || '');
      setGoods(shipmentToEdit.goods);
      setQty(shipmentToEdit.qty);
      setWt(shipmentToEdit.wt);
      const d = new Date(shipmentToEdit.sched);
      setSched(new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString().slice(0, 16));
      setConsignee(shipmentToEdit.consignee);
      setCphone(shipmentToEdit.cphone || '');
      setDriverId(shipmentToEdit.driver || '');
      setVehicleId(shipmentToEdit.vehicle || '');
      setNote(shipmentToEdit.note || '');
    } else {
      setFrom(CITY_NAMES[0]);
      setTo(CITY_NAMES[3]);
      setPAddr('');
      setDAddr('');
      setGoods('Rice bags');
      setQty(50);
      setWt(5.0);
      const d = new Date(Date.now() + 3600000);
      setSched(new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString().slice(0, 16));
      setConsignee('');
      setCphone('');
      setDriverId('');
      setVehicleId('');
      setNote('');
    }
  }, [shipmentToEdit, isOpen]);

  // Validation & capacity checks
  useEffect(() => {
    const msgs: string[] = [];
    const selectedDriver = fleetStore.drivers.find((d) => d.id === driverId);
    const selectedVehicle = fleetStore.vehicles.find((v) => v.id === vehicleId);

    if (from === to) {
      msgs.push('Pickup and drop are the same city.');
    }

    if (selectedDriver) {
      if (selectedDriver.status === 'On Trip' && selectedDriver.id !== shipmentToEdit?.driver) {
        msgs.push(`${selectedDriver.name} is currently on another trip.`);
      }
      if (['On Leave', 'Offline'].includes(selectedDriver.status)) {
        msgs.push(`${selectedDriver.name} is marked as ${selectedDriver.status.toLowerCase()}.`);
      }
    }

    if (selectedVehicle) {
      if (selectedVehicle.status === 'Maintenance') {
        msgs.push(`Vehicle ${selectedVehicle.reg} is currently in maintenance.`);
      }
      if (selectedVehicle.status === 'On Trip' && selectedVehicle.id !== shipmentToEdit?.vehicle) {
        msgs.push(`Vehicle ${selectedVehicle.reg} is currently on another trip.`);
      }
      if (wt > selectedVehicle.cap) {
        msgs.push(`Consignment weight (${wt} t) exceeds vehicle payload capacity (${selectedVehicle.cap} t).`);
      }
    }

    setWarnings(msgs);
  }, [from, to, driverId, vehicleId, wt, shipmentToEdit]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (driverId && !vehicleId) {
      showToast('Please allocate a vehicle for the assigned driver.');
      return;
    }

    const payload = {
      from,
      to,
      pAddr,
      dAddr,
      goods,
      qty: Number(qty),
      wt: Number(wt),
      sched: new Date(sched).toISOString(),
      consignee,
      cphone,
      driver: driverId || null,
      vehicle: vehicleId || null,
      note,
      status: (driverId ? 'Assigned' : 'Created') as Shipment['status'],
      delayed: false,
    };

    if (shipmentToEdit) {
      fleetStore.updateShipment(shipmentToEdit.id, payload, shipmentToEdit.version);
      showToast(`${shipmentToEdit.id} updated successfully.`);
    } else {
      const created = fleetStore.createShipment(payload);
      showToast(`${created.id} created and dispatched.`);
    }

    onClose();
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={shipmentToEdit ? `Edit Consignment ${shipmentToEdit.id}` : 'Create New Shipment'}
      maxWidth="620px"
      testId="modal:shipment_form"
    >
      <form onSubmit={handleSubmit}>
        <label className="fld">
          <span>Pickup City</span>
          <select value={from} onChange={(e) => setFrom(e.target.value)}>
            {CITY_NAMES.map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
        </label>

        <label className="fld">
          <span>Drop City</span>
          <select value={to} onChange={(e) => setTo(e.target.value)}>
            {CITY_NAMES.map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
        </label>

        <label className="fld">
          <span>Pickup Address</span>
          <input
            value={pAddr}
            onChange={(e) => setPAddr(e.target.value)}
            placeholder="Warehouse / Depot gate"
          />
        </label>

        <label className="fld">
          <span>Drop Address</span>
          <input
            value={dAddr}
            onChange={(e) => setDAddr(e.target.value)}
            placeholder="Consignee yard / Delivery point"
          />
        </label>

        <label className="fld">
          <span>Goods Manifest</span>
          <input
            required
            value={goods}
            onChange={(e) => setGoods(e.target.value)}
            placeholder="e.g. Rice bags, Cement"
          />
        </label>

        <label className="fld">
          <span>Quantity (Units)</span>
          <input
            type="number"
            min="1"
            value={qty}
            onChange={(e) => setQty(Number(e.target.value))}
          />
        </label>

        <label className="fld">
          <span>Gross Weight (Tonnes)</span>
          <input
            type="number"
            step="0.1"
            min="0.1"
            required
            value={wt}
            onChange={(e) => setWt(Number(e.target.value))}
          />
        </label>

        <label className="fld">
          <span>Scheduled Pickup</span>
          <input
            type="datetime-local"
            required
            value={sched}
            onChange={(e) => setSched(e.target.value)}
          />
        </label>

        <label className="fld">
          <span>Consignee Business</span>
          <input
            required
            value={consignee}
            onChange={(e) => setConsignee(e.target.value)}
            placeholder="Company or contact name"
          />
        </label>

        <label className="fld">
          <span>Consignee Phone</span>
          <input
            value={cphone}
            onChange={(e) => setCphone(e.target.value)}
            placeholder="+91 98470 XXXXX"
          />
        </label>

        <label className="fld">
          <span>Assigned Driver</span>
          <select value={driverId} onChange={(e) => setDriverId(e.target.value)}>
            <option value="">Unassigned</option>
            {fleetStore.drivers
              .filter((d) => d.status !== 'Inactive')
              .map((d) => (
                <option key={d.id} value={d.id}>
                  {d.name} · {d.status}
                </option>
              ))}
          </select>
        </label>

        <label className="fld">
          <span>Assigned Vehicle</span>
          <select value={vehicleId} onChange={(e) => setVehicleId(e.target.value)}>
            <option value="">None</option>
            {fleetStore.vehicles.map((v) => (
              <option key={v.id} value={v.id}>
                {v.reg} · {v.type} ({v.cap} t) · {v.status}
              </option>
            ))}
          </select>
        </label>

        <label className="fld full">
          <span>Operational Instructions / Notes</span>
          <textarea
            value={note}
            onChange={(e) => setNote(e.target.value)}
            placeholder="Handling guidelines, gate pass requirement, or route notes"
          />
        </label>

        {warnings.length > 0 && (
          <div className="warnbox" style={{ display: 'block' }}>
            {warnings.map((w, i) => (
              <div key={i}>⚠️ {w}</div>
            ))}
          </div>
        )}

        <div className="mf">
          <button type="button" className="btn" onClick={onClose}>
            Cancel
          </button>
          <button type="submit" className="btn primary" data-testid="modal:btn_submit_shipment">
            {shipmentToEdit ? 'Save Changes' : 'Create & Dispatch'}
          </button>
        </div>
      </form>
    </Modal>
  );
};
