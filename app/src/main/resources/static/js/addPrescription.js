import { savePrescription, getPrescription } from "./services/prescriptionServices.js";

document.addEventListener('DOMContentLoaded', async () => {
  const savePrescriptionBtn = document.getElementById("savePrescription");
  const patientNameInput = document.getElementById("patientName");
  const medicinesInput = document.getElementById("medicines");
  const dosageInput = document.getElementById("dosage");
  const notesInput = document.getElementById("notes");
  const heading = document.getElementById("heading");

  const urlParams = new URLSearchParams(window.location.search);
  const appointmentId = urlParams.get("appointmentId");
  const mode = urlParams.get("mode");
  const token = localStorage.getItem("token") || window.location.pathname.split('/').filter(Boolean).pop() || '';
  const patientName = urlParams.get("patientName");

  if (heading) {
    heading.innerHTML = mode === "view" ? `View <span>Prescription</span>` : `Add <span>Prescription</span>`;
  }

  if (patientNameInput && patientName) {
    patientNameInput.value = patientName;
  }

  if (appointmentId && token) {
    try {
      const response = await getPrescription(appointmentId, token);
      const existingPrescription = response.prescription && Array.isArray(response.prescription)
        ? response.prescription[0]
        : response.prescription;

      if (existingPrescription) {
        patientNameInput.value = existingPrescription.patientName || "";
        medicinesInput.value = existingPrescription.medication || "";
        dosageInput.value = existingPrescription.dosage || "";
        notesInput.value = existingPrescription.doctorNotes || "";
      }
    } catch (error) {
      console.warn("No existing prescription found or failed to load:", error);
    }
  }

  if (mode === 'view' && patientNameInput && medicinesInput && dosageInput && notesInput && savePrescriptionBtn) {
    patientNameInput.disabled = true;
    medicinesInput.disabled = true;
    dosageInput.disabled = true;
    notesInput.disabled = true;
    savePrescriptionBtn.style.display = "none";
  }

  if (savePrescriptionBtn) {
    savePrescriptionBtn.addEventListener('click', async (e) => {
      e.preventDefault();

      const prescription = {
        patientName: patientNameInput?.value || '',
        medication: medicinesInput?.value || '',
        dosage: dosageInput?.value || '',
        doctorNotes: notesInput?.value || '',
        appointmentId
      };

      const { success, message } = await savePrescription(prescription, token);

      if (success) {
        alert("✅ Prescription saved successfully.");
        window.location.href = '/doctorDashboard/' + token;
      } else {
        alert("❌ Failed to save prescription. " + message);
      }
    });
  }
});
