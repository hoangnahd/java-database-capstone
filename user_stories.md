# Clinical Management System (CMS) - User Stories

This document outlines the core user stories for the Clinical Management System, categorized by user roles: **Admin**, **Patient**, and **Doctor**. Each story follows the standard Agile framework: *As a [role], I want to [action], so that [benefit].*

---

## 1. Admin User Stories
The Admin manages system access, registers staff, and maintains overall operational integrity.

* **User Management (Doctor Registration)**
    * **As an** Admin  
    * **I want to** register new doctor profiles with their specialties and contact details  
    * **So that** they can be safely added to the hospital system and made available to patients.
* **System Access Control**
    * **As an** Admin  
    * **I want to** validate and authorize security tokens for incoming API data updates  
    * **So that** sensitive healthcare infrastructure remains protected against unauthorized access.
* **System-Wide Reporting**
    * **As an** Admin  
    * **I want to** generate analytical reports showing which doctors see the most patients per month or year  
    * **So that** I can optimize hospital staffing and resource allocation.

---

## 2. Patient User Stories
The Patient uses the system to seamlessly discover providers and manage bookings.

* **Doctor Discovery**
    * **As a** Patient  
    * **I want to** filter doctors based on their name, medical specialty, and time availability  
    * **So that** I can find the right healthcare provider matching my specific needs and schedule.
* **Appointment Booking**
    * **As a** Patient  
    * **I want to** schedule an appointment with a doctor for a chosen date and time block  
    * **So that** I can secure a consultation without visiting the clinic physically.
* **Personal Schedule Management**
    * **As a** Patient  
    * **I want to** view a comprehensive dashboard of all my booked, completed, or pending appointments  
    * **So that** I can easily keep track of my healthcare journey and upcoming visits.

---

## 3. Doctor User Stories
The Doctor manages daily schedules, sets availability windows, and tracks patient queues.

* **Availability Management**
    * **As a** Doctor  
    * **I want to** define and update my available time slots for any specific calendar date  
    * **So that** patients can only book appointments when I am actively on duty.
* **Daily Agenda Tracking**
    * **As a** Doctor  
    * **I want to** run a daily appointment report filtered by specific dates  
    * **So that** I can prepare for my day's scheduled physicals, consultations, or reviews.
* **Patient Context Review**
    * **As a** Doctor  
    * **I want to** check the number of unique patient encounters I have fulfilled over a month  
    * **So that** I can audit my clinical workload and performance metrics.
