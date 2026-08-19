package com.moroccoit.attestation.model;

public enum DocType {
    STAGE("Attestation de Stage", "Stage de formation / Fin d'études"),
    SALAIRE("Attestation de Salaire", "Attestation de rémunération et salaire"),
    TRAVAIL("Attestation de Travail", "Attestation d'emploi et de travail");

    private final String displayName;
    private final String description;

    DocType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
