package com.moroccoit.attestation.dao;

import com.moroccoit.attestation.config.DatabaseConfig;
import com.moroccoit.attestation.model.AttestationHistory;
import com.moroccoit.attestation.model.DocType;

import java.sql.*;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class HistoryDao {

    public List<AttestationHistory> getAllHistory() {
        List<AttestationHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM attestation_history ORDER BY id DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToHistory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addHistoryRecord(AttestationHistory history) {
        String sql = """
            INSERT INTO attestation_history (
                ref_number, doc_type, employee_id, employee_name, employee_cin,
                generated_by, generation_date, verification_hash, pdf_file_path, purpose
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, history.getRefNumber());
            pstmt.setString(2, history.getDocType().name());
            pstmt.setInt(3, history.getEmployeeId());
            pstmt.setString(4, history.getEmployeeName());
            pstmt.setString(5, history.getEmployeeCin());
            pstmt.setString(6, history.getGeneratedBy());
            pstmt.setString(7, history.getGenerationDate().toString());
            pstmt.setString(8, history.getVerificationHash());
            pstmt.setString(9, history.getPdfFilePath());
            pstmt.setString(10, history.getPurpose());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        history.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteHistoryRecord(int id) {
        String sql = "DELETE FROM attestation_history WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTodayGeneratedCount() {
        String sql = "SELECT COUNT(*) FROM attestation_history WHERE generation_date LIKE ?";
        String todayPrefix = LocalDateTime.now().toLocalDate().toString() + "%";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, todayPrefix);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String generateNextReferenceNumber() {
        int currentYear = LocalDateTime.now().getYear();
        int currentMonth = LocalDateTime.now().getMonthValue();
        String prefix = String.format("AT-%d-%02d-", currentYear, currentMonth);

        String sql = "SELECT COUNT(*) FROM attestation_history WHERE ref_number LIKE ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, prefix + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                int count = 0;
                if (rs.next()) {
                    count = rs.getInt(1);
                }
                return String.format("AT-%d-%02d-%04d", currentYear, currentMonth, count + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return String.format("AT-%d-%02d-0001", currentYear, currentMonth);
    }

    private AttestationHistory mapResultSetToHistory(ResultSet rs) throws SQLException {
        String dateStr = rs.getString("generation_date");
        LocalDateTime genDate = dateStr != null ? LocalDateTime.parse(dateStr) : LocalDateTime.now();

        return new AttestationHistory(
            rs.getInt("id"),
            rs.getString("ref_number"),
            DocType.valueOf(rs.getString("doc_type")),
            rs.getInt("employee_id"),
            rs.getString("employee_name"),
            rs.getString("employee_cin"),
            rs.getString("generated_by"),
            genDate,
            rs.getString("verification_hash"),
            rs.getString("pdf_file_path"),
            rs.getString("purpose")
        );
    }
}
