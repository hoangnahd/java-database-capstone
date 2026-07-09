import { API_BASE_URL } from '../config/config.js';

const DOCTOR_API = `${API_BASE_URL}/doctor`;

export async function getDoctors() {
  const response = await fetch(DOCTOR_API, { method: 'GET' });
  if (!response.ok) {
    throw new Error('Unable to fetch doctors');
  }
  const result = await response.json();
  return result.doctors || [];
}

export async function saveDoctor(doctor, token = '') {
  const response = await fetch(`${DOCTOR_API}/${encodeURIComponent(token || 'guest')}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(doctor)
  });

  const result = await response.json().catch(() => ({}));
  return {
    success: response.ok,
    message: result.message || (response.ok ? 'Doctor saved successfully.' : 'Unable to save doctor.')
  };
}

export async function filterDoctors(name = '', time = '', specialty = '') {
  const params = new URLSearchParams({ name, time, specialty });
  const response = await fetch(`${DOCTOR_API}/filter?${params.toString()}`, { method: 'GET' });
  if (!response.ok) {
    throw new Error('Unable to filter doctors');
  }
  const result = await response.json();
  return result.doctors || [];
}

