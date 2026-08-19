package com.moroccoit.attestation.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {

    private static final String DB_URL = "jdbc:sqlite:morocco_it.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // 1. Table Users
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    full_name TEXT NOT NULL,
                    role TEXT NOT NULL
                );
            """);

            // 2. Table Employees
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS employees (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    registration_num TEXT UNIQUE NOT NULL,
                    full_name TEXT NOT NULL,
                    cin TEXT UNIQUE NOT NULL,
                    cnss_num TEXT,
                    job_title TEXT NOT NULL,
                    department TEXT NOT NULL,
                    contract_type TEXT NOT NULL,
                    hire_date TEXT NOT NULL,
                    end_date TEXT,
                    monthly_salary_gross REAL DEFAULT 0,
                    monthly_salary_net REAL DEFAULT 0,
                    school_name TEXT,
                    internship_topic TEXT,
                    status TEXT DEFAULT 'Actif',
                    address TEXT
                );
            """);

            // 3. Table Company Settings
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS company_settings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    company_name TEXT NOT NULL,
                    ice TEXT NOT NULL,
                    if_num TEXT NOT NULL,
                    rc_num TEXT NOT NULL,
                    cnss_company TEXT NOT NULL,
                    capital TEXT NOT NULL,
                    address TEXT NOT NULL,
                    city TEXT NOT NULL,
                    phone TEXT NOT NULL,
                    email TEXT NOT NULL,
                    website TEXT NOT NULL,
                    logo_path TEXT,
                    stamp_path TEXT,
                    watermark_enabled INTEGER DEFAULT 1,
                    primary_color_hex TEXT DEFAULT '#0F172A',
                    signatory_name TEXT NOT NULL,
                    signatory_title TEXT NOT NULL
                );
            """);

            // 4. Table Attestation Templates
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS attestation_templates (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    doc_type TEXT UNIQUE NOT NULL,
                    title TEXT NOT NULL,
                    legal_text_template TEXT NOT NULL,
                    header_text TEXT,
                    footer_text TEXT
                );
            """);

            // 5. Table Attestation History
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS attestation_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    ref_number TEXT UNIQUE NOT NULL,
                    doc_type TEXT NOT NULL,
                    employee_id INTEGER NOT NULL,
                    employee_name TEXT NOT NULL,
                    employee_cin TEXT NOT NULL,
                    generated_by TEXT NOT NULL,
                    generation_date TEXT NOT NULL,
                    verification_hash TEXT NOT NULL,
                    pdf_file_path TEXT NOT NULL,
                    purpose TEXT
                );
            """);

            // Seed Initial Data if empty
            seedInitialData(conn, stmt);

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void seedInitialData(Connection conn, Statement stmt) throws SQLException {
        // Seed Users
        stmt.execute("""
            INSERT OR IGNORE INTO users (id, username, password, full_name, role) VALUES
            (1, 'admin', 'admin123', 'Administrateur Système', 'ADMIN'),
            (2, 'rh', 'rh123', 'Responsable RH Morocco IT', 'RH');
        """);

        // Seed Company Settings
        stmt.execute("""
            INSERT OR IGNORE INTO company_settings (
                id, company_name, ice, if_num, rc_num, cnss_company, capital,
                address, city, phone, email, website, watermark_enabled, primary_color_hex,
                signatory_name, signatory_title
            ) VALUES (
                1, 'MOROCCO IT S.A.R.L.', '002145893000045', '40291823', '512039', '8472910', '500 000,00 DH',
                'Boulevard Mohamed V, N° 142, 4ème étage', 'Casablanca', '+212 5 22 48 90 12',
                'contact@moroccoit.ma', 'www.moroccoit.ma', 1, '#0F172A',
                'Aymane BOUTARBOUCH', 'Directeur Général & Fondateur'
            );
        """);

        // Seed Sample Employees & Interns
        stmt.execute("""
            INSERT OR IGNORE INTO employees (
                id, registration_num, full_name, cin, cnss_num, job_title, department,
                contract_type, hire_date, end_date, monthly_salary_gross, monthly_salary_net,
                school_name, internship_topic, status, address
            ) VALUES
            (1, 'EMP-2023-01', 'Salma BENALI', 'AB654321', '198765432', 'Comptable Senior & Chef de Mission', 'Comptabilité', 'CDI', '2022-03-01', NULL, 14500.00, 11800.00, NULL, NULL, 'Actif', '25 Rue Hassan II, Casablanca'),
            (2, 'EMP-2023-02', 'Karim TAZI', 'BK987654', '201987654', 'Consultant Auditeur Financier', 'Audit', 'CDI', '2023-01-15', NULL, 12000.00, 9800.00, NULL, NULL, 'Actif', '10 Avenue des FAR, Casablanca'),
            (3, 'EMP-2024-03', 'Othmane CHRAIBI', 'BE112233', '210554433', 'Ingénieur Développeur Full-Stack', 'IT & Digital', 'CDD', '2024-02-01', '2025-01-31', 13500.00, 10900.00, NULL, NULL, 'Actif', '5 Boulevard Zerktouni, Casablanca'),
            (4, 'STG-2026-01', 'Youssef EL AMRANI', 'EE445566', NULL, 'Stagiaire Business Intelligence', 'IT & Digital', 'Stage', '2026-03-01', '2026-08-31', 0, 0, 'ENSIAS Rabat', 'Conception et réalisation d un Dashboard d Analyse Financière', 'Actif', '12 Rue Aguelmane, Agdal, Rabat'),
            (5, 'STG-2026-02', 'Khadija ALAMI', 'CD778899', NULL, 'Stagiaire Audit & Comptabilité', 'Comptabilité', 'Stage', '2026-04-01', '2026-09-30', 0, 0, 'ENCG Casablanca', 'Optimisation des procédures de contrôle interne comptable', 'Actif', '8 Allée des Roseaux, Mohammedia');
        """);

        // Seed Templates
        stmt.execute("""
            INSERT OR IGNORE INTO attestation_templates (id, doc_type, title, legal_text_template, header_text, footer_text) VALUES
            (1, 'STAGE', 'ATTESTATION DE STAGE',
             'Nous soussignés, la société MOROCCO IT S.A.R.L., attestons par la présente que M./Mme {NOM}, titulaire de la Carte d''Identité Nationale N° {CIN}, étudiant(e) à {ECOLE}, a effectué un stage au sein de notre établissement dans le département {DEPARTEMENT} du {DATE_DEBUT} au {DATE_FIN}.\n\nDurant cette période, l''intéressé(e) a travaillé sur le sujet suivant :\n« {SUJET_STAGE} ».\n\nNous certifions que M./Mme {NOM} a fait preuve d''un grand sérieux, de rigueur professionnelle et d''une excellente compétence dans l''exécution des missions qui lui ont été confiées.\n\nCette attestation lui est délivrée sur sa demande pour servir et valoir ce que de droit.',
             'MOROCCO IT S.A.R.L. - DIRECTION DES RESSOURCES HUMAINES',
             'MOROCCO IT S.A.R.L. | Capital: 500.000 DH | RC: 512039 | IF: 40291823 | ICE: 002145893000045 | CNSS: 8472910'),

            (2, 'SALAIRE', 'ATTESTATION DE SALAIRE',
             'Nous soussignés, la société MOROCCO IT S.A.R.L., attestons par la présente que M./Mme {NOM}, titulaire de la CIN N° {CIN} et immatriculé(e) à la CNSS sous le N° {CNSS}, est employé(e) au sein de notre société en qualité de {POSTE} depuis le {DATE_DEBUT} dans le cadre d''un contrat {TYPE_CONTRAT}.\n\nNous certifions que l''intéressé(e) perçoit une rémunération mensuelle fixée comme suit :\n  • Salaire Brut Mensuel : {SALAIRE_BRUT} DH ({SALAIRE_BRUT_LETTRES} Dirhams)\n  • Salaire Net Mensuel : {SALAIRE_NET} DH ({SALAIRE_NET_LETTRES} Dirhams)\n\nCette attestation est délivrée à l''intéressé(e) sur sa demande pour servir et valoir ce que de droit, notamment auprès des organismes bancaires et administratifs.',
             'MOROCCO IT S.A.R.L. - DIRECTION DES RESSOURCES HUMAINES',
             'MOROCCO IT S.A.R.L. | Capital: 500.000 DH | RC: 512039 | IF: 40291823 | ICE: 002145893000045 | CNSS: 8472910'),

            (3, 'TRAVAIL', 'ATTESTATION DE TRAVAIL',
             'Nous soussignés, la société MOROCCO IT S.A.R.L., attestons par la présente que M./Mme {NOM}, titulaire de la Carte d''Identité Nationale N° {CIN} et immatriculé(e) à la CNSS sous le N° {CNSS}, est actuellement employé(e) au sein de notre cabinet en qualité de {POSTE} au département {DEPARTEMENT}, depuis le {DATE_DEBUT} jusqu''à ce jour.\n\nM./Mme {NOM} exerce ses fonctions en toute régularité au regard du Code du Travail marocain et est libre de tout engagement envers notre société à compter de sa date de départ effective le cas échéant.\n\nEn foi de quoi, la présente attestation lui est délivrée pour servir et valoir ce que de droit.',
             'MOROCCO IT S.A.R.L. - DIRECTION DES RESSOURCES HUMAINES',
             'MOROCCO IT S.A.R.L. | Capital: 500.000 DH | RC: 512039 | IF: 40291823 | ICE: 002145893000045 | CNSS: 8472910');
        """);
    }
}
