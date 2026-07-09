### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key -> doctor(id)
- patient_id: INT, Foreign Key -> patient(id)
- appointment_time: DATETIME, not null
- status: tinyint, not null, comment (0=Scheduled, 1=Completed, 2=Cancelled)
### Table: doctors
- id: INT, primary key, auto increment
- working_hours: int, not null, comment "Weekly hours"
- availability: boolean, default true
- name: varchar(100), not null
- gender: tinyint. not null, comment (0=female, 1=male)
### Table: patients
- id: INT, primary key, auto increment
- name: varchar(100), not null
- gender: tinyint, notn null, comment (0=female, 1=male)

## MongoDB Collection Design
### Collection: prescriptions
{
    "_id": "",
    "patientName":"",
    "appointmentId":"",
    "doctorName":"",
    "medication":"",
    "dosage":"",
    "doctorNotes":"",
    "refillCount":"",
    "pharmacy":{
        "name":",
        "loction":
    }
}