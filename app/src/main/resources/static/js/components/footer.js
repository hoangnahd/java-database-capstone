function renderFooter() {
  const footerDiv = document.getElementById('footer');
  if (!footerDiv) return;

  footerDiv.innerHTML = `
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-logo">
          <img src="/assets/images/logo/logo.png" alt="Hospital CMS logo" />
          <p>© 2025 Hospital CMS. All rights reserved.</p>
        </div>
        <div class="footer-links">
          <div class="footer-column">
            <h4>Company</h4>
            <a href="#">About</a>
            <a href="#">Careers</a>
            <a href="#">Press</a>
          </div>
          <div class="footer-column">
            <h4>Support</h4>
            <a href="#">Help Center</a>
            <a href="#">Contact Us</a>
          </div>
          <div class="footer-column">
            <h4>Legal</h4>
            <a href="#">Privacy Policy</a>
            <a href="#">Terms</a>
          </div>
        </div>
      </div>
    </footer>
  `;
}

document.addEventListener('DOMContentLoaded', renderFooter);
