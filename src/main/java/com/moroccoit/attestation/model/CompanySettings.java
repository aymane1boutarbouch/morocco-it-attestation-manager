package com.moroccoit.attestation.model;

public class CompanySettings {
    private int id;
    private String companyName;
    private String ice;           // Identifiant Commun de l'Entreprise
    private String ifNum;         // Identifiant Fiscal
    private String rcNum;         // Registre du Commerce
    private String cnssCompany;   // N° Affiliation CNSS
    private String capital;       // Capital Social (e.g. 500.000,00 DH)
    private String address;       // Siège Social
    private String city;          // Ville (e.g. Casablanca)
    private String phone;         // Téléphone
    private String email;         // Email
    private String website;       // Site Web
    private String logoPath;      // Path to logo image
    private String stampPath;     // Path to stamp/cachet signature image
    private boolean watermarkEnabled;
    private String primaryColorHex;
    private String signatoryName;  // Nom du Signataire (e.g. Aymane BOUTARBOUCH)
    private String signatoryTitle; // Qualité du Signataire (e.g. Directeur Général / DRH)

    public CompanySettings() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getIce() { return ice; }
    public void setIce(String ice) { this.ice = ice; }

    public String getIfNum() { return ifNum; }
    public void setIfNum(String ifNum) { this.ifNum = ifNum; }

    public String getRcNum() { return rcNum; }
    public void setRcNum(String rcNum) { this.rcNum = rcNum; }

    public String getCnssCompany() { return cnssCompany; }
    public void setCnssCompany(String cnssCompany) { this.cnssCompany = cnssCompany; }

    public String getCapital() { return capital; }
    public void setCapital(String capital) { this.capital = capital; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }

    public String getStampPath() { return stampPath; }
    public void setStampPath(String stampPath) { this.stampPath = stampPath; }

    public boolean isWatermarkEnabled() { return watermarkEnabled; }
    public void setWatermarkEnabled(boolean watermarkEnabled) { this.watermarkEnabled = watermarkEnabled; }

    public String getPrimaryColorHex() { return primaryColorHex; }
    public void setPrimaryColorHex(String primaryColorHex) { this.primaryColorHex = primaryColorHex; }

    public String getSignatoryName() { return signatoryName; }
    public void setSignatoryName(String signatoryName) { this.signatoryName = signatoryName; }

    public String getSignatoryTitle() { return signatoryTitle; }
    public void setSignatoryTitle(String signatoryTitle) { this.signatoryTitle = signatoryTitle; }
}
