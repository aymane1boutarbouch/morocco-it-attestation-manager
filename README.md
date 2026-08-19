# 🇲🇦 Morocco IT - Attestation Management System (JavaFX)

An enterprise-grade Java 17 desktop application designed for **Morocco IT** (accounting, audit, & IT consulting firm) to manage employees and interns and generate official Moroccan HR certificates (*Attestation de Stage*, *Attestation de Salaire*, *Attestation de Travail*) with pixel-precise PDFBox generation, embedded verification QR codes, Canva-inspired UI styling, custom template editor, live PDF preview, and SQLite archival.

![JavaFX](https://img.shields.io/badge/JavaFX-17-blue)
![Java](https://img.shields.io/badge/Java-17-orange)
![PDFBox](https://img.shields.io/badge/PDFBox-2.0.31-red)
![SQLite](https://img.shields.io/badge/SQLite-JDBC-green)
![Build](https://img.shields.io/badge/Maven-3.9+-brightgreen)

---

## ✨ Features

- **Canva-Inspired Desktop UI:** Sleek slate/navy (`#0F172A`) and gold (`#D97706`) color scheme, rounded cards, subtle shadows, responsive navigation, and smooth user experience.
- **Official Moroccan HR Certificates:**
  - 🎓 **Attestation de Stage** (Internship Certificate for PFE/PFE trainees with school name & thesis subject)
  - 💰 **Attestation de Salaire** (Salary Certificate for banking & credit with gross/net breakdown & French currency conversion)
  - 🏢 **Attestation de Travail** (Work Certificate in compliance with the Moroccan Labor Code / *Code du Travail*)
- **Authenticity & Verification:** Auto-generated reference numbers (`AT-2026-08-0042`) and scannable QR codes containing SHA-256 verification hashes.
- **Live PDF Preview:** Split-screen generator view rendering real-time vector PDF previews using Apache PDFBox `PDFRenderer`.
- **Template & Company Editor:** Custom corporate profile management (ICE, IF, RC, CNSS, Capital, Address, Signatory details), watermark toggles, and editable legal text templates per certificate type with dynamic placeholders (`{NOM}`, `{CIN}`, `{CNSS}`, `{POSTE}`, `{SALAIRE_BRUT}`, `{SALAIRE_NET}`, etc.).
- **Employee & Stagiaire Database:** Full CRUD grid with instant search, contract filter (CDI, CDD, Stage, Anapec), and modal form.
- **Document Archival & History:** Searchable audit trail of generated attestations with direct PDF viewing, re-export, and deletion.
- **Bilingual & Multi-Format Export:** French primary language with Arabic toggle support; exports primary PDF and secondary Word (`.docx`) documents.

---

## 🛠️ Technology Stack

- **Language & Runtime:** OpenJDK 17
- **GUI Framework:** JavaFX 17 (`javafx-controls`, `javafx-fxml`, `javafx-graphics`, `javafx-swing`)
- **PDF Engine:** Apache PDFBox 2.0.31 & FontBox 2.0.31
- **QR Code Engine:** ZXing 3.5.3 (`core` & `javase`)
- **Database:** SQLite JDBC 3.45.1.0 (local-first storage with automatic schema migration and seed data)
- **DOCX Export:** Apache POI 5.2.5
- **Build Tool:** Apache Maven 3.9+

---

## 🚀 Quick Start

### Prerequisites
- JDK 17 or higher
- Apache Maven 3.9+

### Building & Running

1. **Clone the repository:**
   ```bash
   git clone https://github.com/aymane1boutarbouch/morocco-it-attestation-manager.git
   cd morocco-it-attestation-manager
   ```

2. **Run the Application:**
   ```bash
   mvn clean compile javafx:run
   ```

3. **Login Credentials:**
   - **Admin User:** Username: `admin` | Password: `admin123`
   - **RH User:** Username: `rh` | Password: `rh123`

---

## 📁 Sample Output PDFs

Generated sample PDF certificates are available in the `output_samples/` directory:
- `output_samples/STAGE_Youssef_EL_AMRANI.pdf`
- `output_samples/SALAIRE_Salma_BENALI.pdf`
- `output_samples/TRAVAIL_Karim_TAZI.pdf`

---

## 📜 License

Distributed under the MIT License. Copyright © 2026 **Morocco IT S.A.R.L.** All rights reserved.
