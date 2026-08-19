package com.moroccoit.attestation.model;

import java.time.LocalDate;

public class Employee {
    private int id;
    private String registrationNum; // N° Matricule (e.g. EMP-2024-001)
    private String fullName;        // Nom & Prénom
    private String cin;             // N° CIN (e.g. AB123456)
    private String cnssNum;         // N° CNSS (e.g. 123456789)
    private String jobTitle;        // Poste / Intitulé de fonction
    private String department;      // Département (Audit, Comptabilité, IT, RH, Conseil)
    private String contractType;    // CDI, CDD, Stage, Anapec
    private LocalDate hireDate;     // Date de début / embauche
    private LocalDate endDate;      // Date de fin (pour Stage ou CDD)
    private double monthlySalaryGross; // Salaire brut (MAD)
    private double monthlySalaryNet;   // Salaire net (MAD)
    private String schoolName;      // École / Université (pour Stagiaires)
    private String internshipTopic; // Sujet de Stage
    private String status;          // Actif, Inactif, Suspendu
    private String address;         // Adresse personnelle

    public Employee() {}

    public Employee(int id, String registrationNum, String fullName, String cin, String cnssNum,
                    String jobTitle, String department, String contractType, LocalDate hireDate,
                    LocalDate endDate, double monthlySalaryGross, double monthlySalaryNet,
                    String schoolName, String internshipTopic, String status, String address) {
        this.id = id;
        this.registrationNum = registrationNum;
        this.fullName = fullName;
        this.cin = cin;
        this.cnssNum = cnssNum;
        this.jobTitle = jobTitle;
        this.department = department;
        this.contractType = contractType;
        this.hireDate = hireDate;
        this.endDate = endDate;
        this.monthlySalaryGross = monthlySalaryGross;
        this.monthlySalaryNet = monthlySalaryNet;
        this.schoolName = schoolName;
        this.internshipTopic = internshipTopic;
        this.status = status;
        this.address = address;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRegistrationNum() { return registrationNum; }
    public void setRegistrationNum(String registrationNum) { this.registrationNum = registrationNum; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public String getCnssNum() { return cnssNum; }
    public void setCnssNum(String cnssNum) { this.cnssNum = cnssNum; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public double getMonthlySalaryGross() { return monthlySalaryGross; }
    public void setMonthlySalaryGross(double monthlySalaryGross) { this.monthlySalaryGross = monthlySalaryGross; }

    public double getMonthlySalaryNet() { return monthlySalaryNet; }
    public void setMonthlySalaryNet(double monthlySalaryNet) { this.monthlySalaryNet = monthlySalaryNet; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public String getInternshipTopic() { return internshipTopic; }
    public void setInternshipTopic(String internshipTopic) { this.internshipTopic = internshipTopic; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public boolean isIntern() {
        return "Stage".equalsIgnoreCase(contractType) || "Stagiaire".equalsIgnoreCase(contractType);
    }

    @Override
    public String toString() {
        return fullName + " (" + cin + ") - " + jobTitle;
    }
}
