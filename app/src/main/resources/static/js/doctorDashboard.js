import { getAllAppointments } from './services/appointmentRecordService.js';

const state = {
  appointments: [],
  selectedDate: new Date().toISOString().slice(0, 10),
  patientName: ''
};

function init() {
  const searchInput = document.getElementById('searchBar');
  const todayButton = document.getElementById('todayButton');
  const datePicker = document.getElementById('datePicker');
  const tableBody = document.getElementById('patientTableBody');
  const statusMessage = document.getElementById('statusMessage');

  if (!tableBody || !statusMessage) return;

  const token = localStorage.getItem('token') || getTokenFromPath();
  if (!token) {
    window.location.href = '/';
    return;
  }

  localStorage.setItem('token', token);
  localStorage.setItem('userRole', 'doctor');
  datePicker.value = state.selectedDate;

  searchInput?.addEventListener('input', () => {
    state.patientName = searchInput.value.trim();
    loadAppointments();
  });

  todayButton?.addEventListener('click', () => {
    state.selectedDate = new Date().toISOString().slice(0, 10);
    datePicker.value = state.selectedDate;
    loadAppointments();
  });

  datePicker?.addEventListener('change', () => {
    state.selectedDate = datePicker.value;
    loadAppointments();
  });

  loadAppointments();
}

async function loadAppointments() {
  const tableBody = document.getElementById('patientTableBody');
  const statusMessage = document.getElementById('statusMessage');
  const token = localStorage.getItem('token');

  if (!tableBody || !statusMessage || !token) return;

  statusMessage.textContent = 'Loading appointments...';
  try {
    const response = await getAllAppointments(state.selectedDate, state.patientName || 'null', token);
    const appointments = Array.isArray(response) ? response : response.appointments || [];
    state.appointments = appointments;

    if (!appointments.length) {
      tableBody.innerHTML = '<tr><td colspan="5" class="noPatientRecord">No appointments found for this day.</td></tr>';
      statusMessage.textContent = 'No appointments found.';
      return;
    }

    tableBody.innerHTML = appointments.map((appointment) => {
      const patient = appointment.patient || {};
      const appointmentTime = appointment.appointmentTime ? new Date(appointment.appointmentTime).toLocaleString() : 'Pending';
      const statusLabel = appointment.status === 1 ? 'Completed' : 'Booked';
      return `
        <tr>
          <td>${patient.name || 'Unknown patient'}</td>
          <td>${patient.phone || '—'}</td>
          <td>${patient.email || '—'}</td>
          <td>${appointmentTime} <span class="status-pill">${statusLabel}</span></td>
          <td>
            <button class="prescription-btn" data-appointment-id="${appointment.id}" data-patient-name="${(patient.name || '').replace(/"/g, '&quot;')}" type="button">+</button>
          </td>
        </tr>
      `;
    }).join('');

    document.querySelectorAll('.prescription-btn').forEach((button) => {
      button.addEventListener('click', () => {
        const appointmentId = button.getAttribute('data-appointment-id');
        const patientName = button.getAttribute('data-patient-name');
        window.location.href = `/pages/addPrescription.html?appointmentId=${appointmentId}&patientName=${encodeURIComponent(patientName)}&mode=add`;
      });
    });

    statusMessage.textContent = `${appointments.length} appointment(s) loaded.`;
  } catch (error) {
    console.error(error);
    tableBody.innerHTML = '<tr><td colspan="5" class="noPatientRecord">Unable to load appointments right now.</td></tr>';
    statusMessage.textContent = 'Unable to load appointments.';
  }
}

function getTokenFromPath() {
  return window.location.pathname.split('/').filter(Boolean).pop() || '';
}

document.addEventListener('DOMContentLoaded', init);

