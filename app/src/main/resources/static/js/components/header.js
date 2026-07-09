function renderHeader() {
  const headerDiv = document.getElementById('header');
  if (!headerDiv) return;

  const role = localStorage.getItem('userRole');
  const token = localStorage.getItem('token');
  const isHome = window.location.pathname === '/' || window.location.pathname.endsWith('/index.html');

  let navContent = '';
  if ((role === 'admin' || role === 'doctor' || role === 'loggedPatient') && !token) {
    localStorage.removeItem('userRole');
  }

  if (role === 'admin') {
    navContent = `
      <a href="/" class="doctorHeader">Home</a>
      <a href="#" onclick="window.logout && window.logout()">Logout</a>`;
  } else if (role === 'doctor') {
    navContent = `
      <a href="/" class="doctorHeader">Home</a>
      <a href="#" onclick="window.logout && window.logout()">Logout</a>`;
  } else if (role === 'loggedPatient') {
    navContent = `
      <a href="/pages/loggedPatientDashboard.html">Home</a>
      <a href="/pages/patientAppointments.html">Appointments</a>
      <a href="#" onclick="window.logout && window.logout()">Logout</a>`;
  } else if (role === 'patient') {
    navContent = `
      <a href="/pages/patientDashboard.html">Browse doctors</a>
      <a href="#" onclick="window.location.href='/'">Back home</a>`;
  } else if (!isHome) {
    navContent = `
      <a href="/">Home</a>`;
  }

  headerDiv.innerHTML = `
    <header class="header">
      <a class="logo-link" href="/">
        <img class="logo-img" src="/assets/images/logo/logo.png" alt="Hospital CMS logo" />
        <span class="logo-title">Hospital CMS</span>
      </a>
      <nav>${navContent}</nav>
    </header>
  `;
}

function logout() {
  localStorage.removeItem('token');
  localStorage.removeItem('userRole');
  window.location.href = '/';
}

window.logout = logout;
window.renderHeader = renderHeader;
document.addEventListener('DOMContentLoaded', renderHeader);

