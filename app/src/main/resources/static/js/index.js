import { openModal } from './components/modals.js';

document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('[data-role]').forEach((button) => {
    button.addEventListener('click', () => {
      const role = button.getAttribute('data-role');
      const token = localStorage.getItem('token');

      if (role === 'patient') {
        selectRole('patient');
        window.location.href = '/pages/patientDashboard.html';
        return;
      }

      if (role === 'admin') {
        if (token) {
          window.location.href = `/adminDashboard/${token}`;
          return;
        }
        openModal('adminLogin');
        return;
      }

      if (role === 'doctor') {
        if (token) {
          window.location.href = `/doctorDashboard/${token}`;
          return;
        }
        openModal('doctorLogin');
      }
    });
  });
});
