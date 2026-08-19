package com.moroccoit.attestation.dao;

import com.moroccoit.attestation.config.DatabaseConfig;
import com.moroccoit.attestation.model.CompanySettings;

import java.sql.*;

public class CompanySettingsDao {

    public CompanySettings getSettings() {
        String sql = "SELECT * FROM company_settings WHERE id = 1";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                CompanySettings settings = new CompanySettings();
                settings.setId(rs.getInt("id"));
                settings.setCompanyName(rs.getString("company_name"));
                settings.setIce(rs.getString("ice"));
                settings.setIfNum(rs.getString("if_num"));
                settings.setRcNum(rs.getString("rc_num"));
                settings.setCnssCompany(rs.getString("cnss_company"));
                settings.setCapital(rs.getString("capital"));
                settings.setAddress(rs.getString("address"));
                settings.setCity(rs.getString("city"));
                settings.setPhone(rs.getString("phone"));
                settings.setEmail(rs.getString("email"));
                settings.setWebsite(rs.getString("website"));
                settings.setLogoPath(rs.getString("logo_path"));
                settings.setStampPath(rs.getString("stamp_path"));
                settings.setWatermarkEnabled(rs.getInt("watermark_enabled") == 1);
                settings.setPrimaryColorHex(rs.getString("primary_color_hex"));
                settings.setSignatoryName(rs.getString("signatory_name"));
                settings.setSignatoryTitle(rs.getString("signatory_title"));
                return settings;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateSettings(CompanySettings settings) {
        String sql = """
            UPDATE company_settings SET
                company_name = ?, ice = ?, if_num = ?, rc_num = ?, cnss_company = ?,
                capital = ?, address = ?, city = ?, phone = ?, email = ?, website = ?,
                logo_path = ?, stamp_path = ?, watermark_enabled = ?, primary_color_hex = ?,
                signatory_name = ?, signatory_title = ?
            WHERE id = 1
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, settings.getCompanyName());
            pstmt.setString(2, settings.getIce());
            pstmt.setString(3, settings.getIfNum());
            pstmt.setString(4, settings.getRcNum());
            pstmt.setString(5, settings.getCnssCompany());
            pstmt.setString(6, settings.getCapital());
            pstmt.setString(7, settings.getAddress());
            pstmt.setString(8, settings.getCity());
            pstmt.setString(9, settings.getPhone());
            pstmt.setString(10, settings.getEmail());
            pstmt.setString(11, settings.getWebsite());
            pstmt.setString(12, settings.getLogoPath());
            pstmt.setString(13, settings.getStampPath());
            pstmt.setInt(14, settings.isWatermarkEnabled() ? 1 : 0);
            pstmt.setString(15, settings.getPrimaryColorHex());
            pstmt.setString(16, settings.getSignatoryName());
            pstmt.setString(17, settings.getSignatoryTitle());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
