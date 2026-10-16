DROP TABLE IF EXISTS treatments, lab_reports, medical_records, appointments, doctors, patients, admins, managers CASCADE;

CREATE TABLE patients (
    patient_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(30),
    phone VARCHAR(30),
    password VARCHAR(100) NOT NULL
);

CREATE TABLE doctors (
    doctor_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100),
    department VARCHAR(100),
    availability VARCHAR(100),
    password VARCHAR(100) NOT NULL
);

CREATE TABLE appointments (
    appointment_id INT PRIMARY KEY,
    patient_id INT REFERENCES patients(patient_id),
    doctor_id INT REFERENCES doctors(doctor_id),
    appointment_date VARCHAR(50),
    status VARCHAR(30)
);

CREATE TABLE medical_records (
    record_id INT PRIMARY KEY,
    patient_id INT REFERENCES patients(patient_id),
    doctor_id INT REFERENCES doctors(doctor_id),
    diagnosis VARCHAR(255),
    clinical_notes TEXT,
    record_date VARCHAR(50)
);

CREATE TABLE lab_reports (
    report_id INT PRIMARY KEY,
    patient_id INT REFERENCES patients(patient_id),
    test_name VARCHAR(100),
    result VARCHAR(255),
    report_date VARCHAR(50)
);

CREATE TABLE treatments (
    treatment_id INT PRIMARY KEY,
    patient_id INT REFERENCES patients(patient_id),
    doctor_id INT REFERENCES doctors(doctor_id),
    treatment VARCHAR(255),
    notes TEXT
);

CREATE TABLE admins (
    admin_id INT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE managers (
    manager_id INT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

-- ==========================================
-- PATIENTS DATASET (25 Records)
-- ==========================================
INSERT INTO patients VALUES
(101,'Ananya Sharma',24,'Female','9000000001','pass123'),
(102,'Rahul Verma',31,'Male','9000000002','rahul123'),
(103,'Priya Patel',28,'Female','9000000003','priya123'),
(104,'David Chen',45,'Male','9000000004','david123'),
(105,'Sarah Jenkins',52,'Female','9000000005','sarah123'),
(106,'Aisha Khan',34,'Female','9000000006','aisha123'),
(107,'Carlos Gomez',39,'Male','9000000007','carlos123'),
(108,'Emily Taylor',29,'Female','9000000008','emily123'),
(109,'Vikram Malhotra',61,'Male','9000000009','vikram123'),
(110,'Sneha Nair',22,'Female','9000000010','sneha123'),
(111,'Robert Wilson',58,'Male','9000000011','robert123'),
(112,'Sunita Rao',47,'Female','9000000012','sunita123'),
(113,'Amit Singh',36,'Male','9000000013','amit123'),
(114,'Fatima Al-Mansoor',41,'Female','9000000014','fatima123'),
(115,'Michael Brown',67,'Male','9000000015','michael123'),
(116,'Pooja Joshi',26,'Female','9000000016','pooja123'),
(117,'Arjun Kapoor',33,'Male','9000000017','arjun123'),
(118,'Maria Santos',50,'Female','9000000018','maria123'),
(119,'Kevin Lee',42,'Male','9000000019','kevin123'),
(120,'Deepa Krishnan',38,'Female','9000000020','deepa123'),
(121,'James Anderson',63,'Male','9000000021','james123'),
(122,'Neha Gupta',27,'Female','9000000022','neha123'),
(123,'Rajesh Khanna',55,'Male','9000000023','rajesh123'),
(124,'Laura Martinez',32,'Female','9000000024','laura123'),
(125,'Karan Mehta',49,'Male','9000000025','karan123');

-- ==========================================
-- DOCTORS DATASET (16 Records)
-- ==========================================
INSERT INTO doctors VALUES
(201,'Dr. Arun Sharma','Cardiology','Heart Department','09:00 AM - 01:00 PM','doctor123'),
(202,'Dr. Meera Iyer','Dermatology','Skin & Allergy Department','11:00 AM - 03:00 PM','doctor123'),
(203,'Dr. Ravi Kumar','General Medicine','Internal Medicine Department','08:30 AM - 12:30 PM','doctor123'),
(204,'Dr. Elena Rostova','Neurology','Neuroscience Department','10:00 AM - 02:00 PM','doctor123'),
(205,'Dr. Sanjay Gupta','Orthopedics','Bone & Joint Department','01:00 PM - 05:00 PM','doctor123'),
(206,'Dr. Sarah Mitchell','Pediatrics','Child Healthcare Department','09:00 AM - 01:00 PM','doctor123'),
(207,'Dr. Tariq Mansoor','Oncology','Cancer Care Department','11:00 AM - 04:00 PM','doctor123'),
(208,'Dr. Ananya Sen','Endocrinology','Diabetes & Hormone Department','02:00 PM - 06:00 PM','doctor123'),
(209,'Dr. Vikram Rathore','Pulmonology','Respiratory & Chest Department','10:00 AM - 02:00 PM','doctor123'),
(210,'Dr. Kavita Reddy','Gastroenterology','Digestive Health Department','09:30 AM - 01:30 PM','doctor123'),
(211,'Dr. James Wilson','Ophthalmology','Eye Care Department','08:00 AM - 12:00 PM','doctor123'),
(212,'Dr. Maya Patel','Psychiatry','Mental Health Department','02:00 PM - 06:00 PM','doctor123'),
(213,'Dr. David Miller','Nephrology','Kidney Care Department','11:00 AM - 03:00 PM','doctor123'),
(214,'Dr. Sunita Varma','Gynecology','Women Healthcare Department','09:00 AM - 02:00 PM','doctor123'),
(215,'Dr. Rajesh Nambiar','ENT','Ear Nose Throat Department','10:00 AM - 01:00 PM','doctor123'),
(216,'Dr. Anthony Chen','Urology','Urinary Health Department','01:30 PM - 05:30 PM','doctor123');

-- ==========================================
-- APPOINTMENTS DATASET (35 Records)
-- ==========================================
INSERT INTO appointments VALUES
(301,101,201,'2026-09-15 09:30 AM','Scheduled'),
(302,102,202,'2026-09-16 11:15 AM','Scheduled'),
(303,103,203,'2026-09-17 09:00 AM','Completed'),
(304,104,204,'2026-09-18 10:30 AM','Completed'),
(305,105,205,'2026-09-19 01:30 PM','Scheduled'),
(306,106,206,'2026-09-20 09:45 AM','Completed'),
(307,107,207,'2026-09-21 11:30 AM','Scheduled'),
(308,108,208,'2026-09-22 02:15 PM','Confirmed'),
(309,109,209,'2026-09-23 10:00 AM','Scheduled'),
(310,110,210,'2026-09-24 10:30 AM','Completed'),
(311,111,211,'2026-09-25 08:30 AM','Confirmed'),
(312,112,212,'2026-09-26 02:45 PM','Scheduled'),
(313,113,213,'2026-09-27 11:00 AM','Completed'),
(314,114,214,'2026-09-28 09:30 AM','Scheduled'),
(315,115,215,'2026-09-29 10:15 AM','Completed'),
(316,116,216,'2026-09-30 02:00 PM','Scheduled'),
(317,117,201,'2026-10-01 10:00 AM','Scheduled'),
(318,118,202,'2026-10-02 12:00 PM','Completed'),
(319,119,203,'2026-10-03 10:30 AM','Confirmed'),
(320,120,204,'2026-10-04 11:00 AM','Scheduled'),
(321,121,205,'2026-10-05 02:30 PM','Completed'),
(322,122,208,'2026-10-06 03:00 PM','Scheduled'),
(323,123,209,'2026-10-07 11:15 AM','Completed'),
(324,124,210,'2026-10-08 10:45 AM','Scheduled'),
(325,125,201,'2026-10-09 11:30 AM','Scheduled'),
(326,101,208,'2026-10-10 02:30 PM','Scheduled'),
(327,102,203,'2026-10-11 09:15 AM','Scheduled'),
(328,103,205,'2026-10-12 01:45 PM','Scheduled'),
(329,104,209,'2026-10-13 10:30 AM','Cancelled'),
(330,105,201,'2026-10-14 10:45 AM','Scheduled'),
(331,106,202,'2026-10-15 11:45 AM','Completed'),
(332,107,204,'2026-10-16 01:15 PM','Scheduled'),
(333,108,211,'2026-10-17 09:00 AM','Completed'),
(334,109,213,'2026-10-18 12:00 PM','Scheduled'),
(335,110,214,'2026-10-19 10:30 AM','Scheduled');

-- ==========================================
-- MEDICAL RECORDS DATASET (25 Records)
-- ==========================================
INSERT INTO medical_records VALUES
(401,101,201,'Hypertension','Patient has elevated systolic blood pressure (145/92 mmHg). Prescribed lifestyle changes and low sodium diet. Follow-up required.','2026-09-01'),
(402,102,202,'Atopic Dermatitis','Patient reports severe itching erythematous patches on forearm and neck. Allergic history present. Topical steroid advised.','2026-09-02'),
(403,103,203,'Acute Viral Bronchitis','Patient presents with persistent cough low-grade fever and mild fatigue. Chest examination reveals mild wheezing.','2026-09-03'),
(404,104,204,'Migraine with Aura','Recurrent unilateral throbbing headache accompanied by visual aura and photophobia. Sleep hygiene counseling provided.','2026-09-04'),
(405,105,205,'Lumbar Spondylosis','Chronic low back stiffness and bilateral radiation to posterior thighs. Physical therapy and ergonomic posture recommended.','2026-09-05'),
(406,106,206,'Pediatric Asthma','6-year-old patient with nocturnal wheezing and shortness of breath triggered by seasonal pollen. Metered dose inhaler prescribed.','2026-09-06'),
(407,107,207,'Hodgkin Lymphoma Follow-up','Post-chemotherapy surveillance. Lymph node ultrasound shows complete radiological remission without acute relapse signs.','2026-09-07'),
(408,108,208,'Type 2 Diabetes Mellitus','Fasting blood glucose elevated at 168 mg/dL. HbA1c is 8.2 percent. Metformin regimen initiated with dietary counseling.','2026-09-08'),
(409,109,209,'Chronic Obstructive Pulmonary Disease','Longstanding dyspnea on minimal exertion and chronic sputum production. Spirometry indicates FEV1/FVC ratio below 0.70.','2026-09-09'),
(410,110,210,'Gastroesophageal Reflux Disease','Frequent heartburn regurgitation and retrosternal discomfort after meals. Proton pump inhibitor therapy initiated.','2026-09-10'),
(411,111,211,'Senile Nuclear Cataract','Progressive painless visual acuity loss in right eye with glare intolerance. Phacoemulsification surgery scheduled.','2026-09-11'),
(412,112,212,'Generalized Anxiety Disorder','Persistent generalized worry restlessness muscle tension and insomnia lasting over six months. Cognitive behavioral therapy planned.','2026-09-12'),
(413,113,213,'Chronic Kidney Disease Stage 2','Mild albuminuria detected with estimated GFR of 74 mL/min. Blood pressure control and nephroprotective regimen started.','2026-09-13'),
(414,114,214,'Polycystic Ovarian Syndrome','Irregular menstrual cycles hirsutism and bilateral pelvic ultrasound ovaries with multiple peripheral subcentimeter follicles.','2026-09-14'),
(415,115,215,'Chronic Allergic Sinusitis','Bilateral nasal obstruction frontal headache and postnasal drip. Nasal corticosteroid spray and saline wash advised.','2026-09-15'),
(416,116,216,'Benign Prostatic Hyperplasia','Hesitancy nocturia and intermittent urinary stream in elderly patient. Alpha-blocker medication initiated.','2026-09-16'),
(417,117,201,'Coronary Artery Disease','Exertional angina pectoris relieved by sublingual nitroglycerin. Coronary angiography evaluation recommended.','2026-09-17'),
(418,118,202,'Psoriasis Vulgaris','Well-demarcated silvery-scale plaques on bilateral extensor surfaces of knees and elbows. Topical calcipotriol prescribed.','2026-09-18'),
(419,119,203,'Community Acquired Pneumonia','High fever productive rust-colored sputum and localized crepitations in right lower lung lobe. Antibiotic course initiated.','2026-09-19'),
(420,120,204,'Carpal Tunnel Syndrome','Numbness and paresthesia along median nerve distribution of dominant right hand. Wrist splinting recommended.','2026-09-20'),
(421,121,205,'Osteoarthritis of Knee','Bilateral knee crepitus joint stiffness in morning and difficulty descending stairs. Quad-strengthening exercises taught.','2026-09-21'),
(422,122,208,'Primary Hypothyroidism','Fatigue cold intolerance weight gain and elevated TSH level at 11.4 mIU/L. Levothyroxine replacement therapy started.','2026-09-22'),
(423,123,209,'Obstructive Sleep Apnea','Excessive daytime sleepiness heavy snoring and witnessed apneas during sleep. Polysomnography test ordered.','2026-09-23'),
(424,124,210,'Irritable Bowel Syndrome','Alternating constipation and loose stools associated with lower abdominal cramping relieved upon defecation.','2026-09-24'),
(425,125,201,'Atrial Fibrillation','Irregularly irregular cardiac rhythm palpitations and fatigue. Rate control medication and oral anticoagulation started.','2026-09-25');

-- ==========================================
-- LAB REPORTS DATASET (20 Records)
-- ==========================================
INSERT INTO lab_reports VALUES
(501,101,'Blood Pressure Monitoring','145/92 mmHg - Stage 1 Hypertension','2026-09-01'),
(502,102,'Serum IgE Level','380 IU/mL - Elevated allergic marker','2026-09-02'),
(503,103,'Complete Blood Count (CBC)','WBC 11500 /mcL with mild neutrophilia','2026-09-03'),
(504,104,'Brain MRI Non-Contrast','No acute intracranial hemorrhage or structural lesion','2026-09-04'),
(505,105,'Lumbar Spine X-Ray','L4-L5 disc space narrowing with mild osteophytes','2026-09-05'),
(506,106,'Peak Expiratory Flow Rate','PEFR 210 L/min - Moderate airway reversibility','2026-09-06'),
(507,107,'Neck and Thorax CT Scan','No pathological lymphadenopathy detected','2026-09-07'),
(508,108,'HbA1c Glycated Hemoglobin','8.2% - Poor glycemic control','2026-09-08'),
(509,109,'Pulmonary Function Test','FEV1/FVC 64% - Obstructive ventilatory defect','2026-09-09'),
(510,110,'Upper Gastrointestinal Endoscopy','Grade B reflux esophagitis at gastroesophageal junction','2026-09-10'),
(511,111,'Slit Lamp Examination','Right eye central nuclear opacity grade 3','2026-09-11'),
(512,112,'Thyroid Function Panel','TSH 2.1 mIU/L - Normal euthyroid state','2026-09-12'),
(513,113,'Serum Creatinine and eGFR','Creatinine 1.4 mg/dL and eGFR 74 mL/min/1.73m2','2026-09-13'),
(514,114,'Pelvic Ultrasonography','Bilateral polycystic ovarian morphology','2026-09-14'),
(515,115,'CT Paranasal Sinuses','Mucosal thickening in maxillary and ethmoid sinuses','2026-09-15'),
(516,116,'Serum Prostate-Specific Antigen (PSA)','PSA 3.1 ng/mL - Within normal range for age','2026-09-16'),
(517,117,'12-Lead Electrocardiogram (ECG)','ST segment depression 1.5mm in leads V4-V6','2026-09-17'),
(518,118,'Skin Biopsy Histopathology','Hyperkeratosis with parakeratosis consistent with psoriasis','2026-09-18'),
(519,119,'Chest Radiograph PA View','Consolidation patch in right lower lobe infiltrate','2026-09-19'),
(520,120,'Electromyography and Nerve Conduction','Median nerve sensory latency prolongation across carpal tunnel','2026-09-20');

-- ==========================================
-- TREATMENTS DATASET (20 Records)
-- ==========================================
INSERT INTO treatments VALUES
(601,101,201,'Telmisartan 40mg daily and dietary sodium reduction','Check blood pressure weekly and repeat serum electrolytes in 4 weeks.'),
(602,102,202,'Mometasone furoate 0.1% topical cream twice daily','Moisturize skin with ceramide emollient; avoid hot showers.'),
(603,103,203,'Azithromycin 500mg daily for 5 days with steam inhalation','Hydration and warm saline gargles; rest for 3 days.'),
(604,104,204,'Sumatriptan 50mg SOS at onset and Propranolol 40mg daily','Maintain migraine headache diary; minimize screen exposure.'),
(605,105,205,'Core stability lumbar physiotherapy and Aceclofenac SOS','Avoid heavy weight lifting; follow ergonomic desk chair setup.'),
(606,106,206,'Salbutamol 100mcg MDI with spacer and Budesonide 200mcg','Demonstrated inhaler technique to parents; review in 3 weeks.'),
(607,107,207,'Routine hematological surveillance and 3-month PET-CT follow-up','Report immediately if any constitutional fever or weight loss occurs.'),
(608,108,208,'Metformin 500mg twice daily with meals and diabetic diet','Perform daily self-monitoring of blood glucose; recheck HbA1c in 3 months.'),
(609,109,209,'Tiotropium 18mcg dry powder inhaler daily and pulmonary rehab','Influenza vaccination advised; avoid biomass and tobacco smoke.'),
(610,110,210,'Esomeprazole 40mg once daily before breakfast for 8 weeks','Elevate head of bed 15 degrees; avoid caffeine and late night meals.'),
(611,111,211,'Pre-operative ophthalmic evaluation and topical antibiotic drops','Booked for day-care phacoemulsification with intraocular lens implant.'),
(612,112,212,'Sertraline 25mg daily titrated to 50mg with relaxation therapy','Counseling sessions scheduled biweekly; monitor mood and sleep patterns.'),
(613,113,213,'Losartan 25mg daily and low protein low potassium diet','Strict target BP < 130/80 mmHg; avoid NSAID analgesics.'),
(614,114,214,'Combined oral contraceptive pill and Metformin 500mg once daily','Encouraged brisk walking 30 minutes daily for insulin resistance.'),
(615,115,215,'Fluticasone furoate nasal spray 2 sprays each nostril daily','Continue saline nasal irrigation morning and night.'),
(616,116,216,'Tamsulosin 0.4mg at bedtime','Advised avoiding fluid intake 2 hours before bed; review in 1 month.'),
(617,117,201,'Aspirin 75mg Atorvastatin 40mg and Metoprolol 25mg twice daily','Schedule elective coronary angiography next week.'),
(618,118,202,'Calcipotriol plus Betamethasone dipropionate ointment','Apply once daily to scalp and body plaques for 4 weeks.'),
(619,119,203,'Amoxicillin-Clavulanate 625mg thrice daily for 7 days','Oxygen saturation monitoring; return if breathlessness increases.'),
(620,120,204,'Neutral wrist splinting during sleep and Pregabalin 75mg at night','Follow occupational ergonomic adjustments for keyboard typing.');

-- ==========================================
-- ADMINS & MANAGERS
-- ==========================================
INSERT INTO admins VALUES 
(1,'admin1','admin123'),
(2,'admin2','adminSecure456');

INSERT INTO managers VALUES 
(1,'manager1','manager123'),
(2,'manager2','managerSecure456');
