import { filterDoctors, getDoctors, saveDoctor } from './services/doctorServices.js';

const state = {
  doctors: [],
  filteredDoctors: []
};

const searchInput = document.getElementById('searchInput');
const timeFilter = document.getElementById('timeFilter');
const specialtyFilter = document.getElementById('specialtyFilter');
const doctorGrid = document.getElementById('doctorGrid');
const statusMessage = document.getElementById('statusMessage');
const totalDoctors = document.getElementById('totalDoctors');
const availableToday = document.getElementById('availableToday');
const doctorModal = document.getElementById('doctorModal');
const doctorForm = document.getElementById('doctorForm');
const addDoctorBtn = document.getElementById('addDoctorBtn');
const refreshBtn = document.getElementById('refreshBtn');
const closeModalBtn = document.getElementById('closeModalBtn');
const cancelBtn = document.getElementById('cancelBtn');

function init() {
  bindEvents();
  loadDoctors();
}

function bindEvents() {
  [searchInput, timeFilter, specialtyFilter].forEach((element) => {
    element.addEventListener('input', handleFilterChange);
    element.addEventListener('change', handleFilterChange);
  });

  addDoctorBtn.addEventListener('click', () => doctorModal.showModal());
  refreshBtn.addEventListener('click', loadDoctors);
  closeModalBtn.addEventListener('click', closeModal);
  cancelBtn.addEventListener('click', closeModal);
  doctorModal.addEventListener('close', resetForm);
  doctorForm.addEventListener('submit', handleDoctorSubmit);
}

async function loadDoctors() {
  setStatus('Loading doctors...');
  try {
    const doctors = await getDoctors();
    state.doctors = Array.isArray(doctors) ? doctors : [];
    state.filteredDoctors = [...state.doctors];
    populateSpecialties();
    renderDoctors();
    setStatus(`${state.doctors.length} doctors loaded`);
  } catch (error) {
    console.error(error);
    setStatus('Unable to load doctors from the backend.');
    renderDoctors([]);
  }
}

async function handleFilterChange() {
  try {
    const name = searchInput.value.trim();
    const time = timeFilter.value;
    const specialty = specialtyFilter.value;
    const filteredDoctors = await filterDoctors(name, time, specialty);
    state.filteredDoctors = Array.isArray(filteredDoctors) ? filteredDoctors : [];
    renderDoctors();
  } catch (error) {
    console.error(error);
    setStatus('Unable to apply filters.');
  }
}

function renderDoctors(doctors = state.filteredDoctors) {
  totalDoctors.textContent = state.doctors.length;
  const availableSlots = state.doctors.reduce((sum, doctor) => sum + (doctor.availableTimes?.length || 0), 0);
  availableToday.textContent = availableSlots;

  if (!doctors.length) {
    doctorGrid.innerHTML = '<div class="empty-state">No doctors match the current filters.</div>';
    return;
  }

  doctorGrid.innerHTML = doctors.map((doctor) => `
    <article class="doctor-card">
      <h4>${doctor.name || 'Unnamed doctor'}</h4>
      <p class="doctor-meta">${doctor.specialty || 'Specialty pending'}</p>
      <p class="doctor-meta">${doctor.email || 'No email'}</p>
      <p class="doctor-meta">${doctor.phone || 'No phone'}</p>
      <div class="tag-row">
        ${(doctor.availableTimes || []).slice(0, 6).map((slot) => `<span class="tag">${slot}</span>`).join('')}
      </div>
    </article>
  `).join('');
}

function populateSpecialties() {
  const specialties = [...new Set(state.doctors.map((doctor) => doctor.specialty).filter(Boolean))];
  specialtyFilter.innerHTML = '<option value="">All specialties</option>' + specialties.map((specialty) => `<option value="${specialty}">${specialty}</option>`).join('');
}

async function handleDoctorSubmit(event) {
  event.preventDefault();
  const formData = new FormData(doctorForm);
  const availableTimes = formData.get('availableTimes')
    ?.toString()
    .split(',')
    .map((slot) => slot.trim())
    .filter(Boolean) || [];

  const doctor = {
    name: formData.get('name')?.toString().trim(),
    email: formData.get('email')?.toString().trim(),
    phone: formData.get('phone')?.toString().trim(),
    password: formData.get('password')?.toString().trim(),
    specialty: formData.get('specialty')?.toString().trim(),
    availableTimes
  };

  try {
    const response = await saveDoctor(doctor, localStorage.getItem('token') || '');
    setStatus(response.message || 'Doctor saved');
    closeModal();
    await loadDoctors();
  } catch (error) {
    console.error(error);
    setStatus('Unable to save doctor.');
  }
}

function closeModal() {
  if (doctorModal.open) {
    doctorModal.close();
  }
}

function resetForm() {
  doctorForm.reset();
}

function setStatus(message) {
  statusMessage.textContent = message;
}

document.addEventListener('DOMContentLoaded', init);

