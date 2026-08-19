package com.moroccoit.attestation.dao;

import com.moroccoit.attestation.config.DatabaseConfig;
import com.moroccoit.attestation.model.AttestationTemplate;
import com.moroccoit.attestation.model.DocType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TemplateDao {

    public AttestationTemplate getByDocType(DocType docType) {
        String sql = "SELECT * FROM attestation_templates WHERE doc_type = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, docType.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTemplate(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<AttestationTemplate> getAllTemplates() {
        List<AttestationTemplate> list = new ArrayList<>();
        String sql = "SELECT * FROM attestation_templates ORDER BY id ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToTemplate(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateTemplate(AttestationTemplate template) {
        String sql = """
            UPDATE attestation_templates SET
                title = ?, legal_text_template = ?, header_text = ?, footer_text = ?
            WHERE doc_type = ?
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, template.getTitle());
            pstmt.setString(2, template.getLegalTextTemplate());
            pstmt.setString(3, template.getHeaderText());
            pstmt.setString(4, template.getFooterText());
            pstmt.setString(5, template.getDocType().name());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private AttestationTemplate mapResultSetToTemplate(ResultSet rs) throws SQLException {
        return new AttestationTemplate(
            rs.getInt("id"),
            DocType.valueOf(rs.getString("doc_type")),
            rs.getString("title"),
            rs.getString("legal_text_template"),
            rs.getString("header_text"),
            rs.getString("footer_text")
        );
    }
}
