package com.moroccoit.attestation.dao;

import com.moroccoit.attestation.config.DatabaseConfig;
import com.moroccoit.attestation.model.Employee;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {

    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY full_name ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToEmployee(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Employee getById(int id) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addEmployee(Employee emp) {
        String sql = """
            INSERT INTO employees (
                registration_num, full_name, cin, cnss_num, job_title, department,
                contract_type, hire_date, end_date, monthly_salary_gross, monthly_salary_net,
                school_name, internship_topic, status, address
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setStatementParameters(pstmt, emp);
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        emp.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateEmployee(Employee emp) {
        String sql = """
            UPDATE employees SET
                registration_num = ?, full_name = ?, cin = ?, cnss_num = ?, job_title = ?,
                department = ?, contract_type = ?, hire_date = ?, end_date = ?,
                monthly_salary_gross = ?, monthly_salary_net = ?, school_name = ?,
                internship_topic = ?, status = ?, address = ?
            WHERE id = ?
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setStatementParameters(pstmt, emp);
            pstmt.setInt(16, emp.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getEmployeeCount() {
        String sql = "SELECT COUNT(*) FROM employees WHERE status = 'Actif'";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getInternCount() {
        String sql = "SELECT COUNT(*) FROM employees WHERE contract_type IN ('Stage', 'Stagiaire') AND status = 'Actif'";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void setStatementParameters(PreparedStatement pstmt, Employee emp) throws SQLException {
        pstmt.setString(1, emp.getRegistrationNum());
        pstmt.setString(2, emp.getFullName());
        pstmt.setString(3, emp.getCin());
        pstmt.setString(4, emp.getCnssNum());
        pstmt.setString(5, emp.getJobTitle());
        pstmt.setString(6, emp.getDepartment());
        pstmt.setString(7, emp.getContractType());
        pstmt.setString(8, emp.getHireDate() != null ? emp.getHireDate().toString() : null);
        pstmt.setString(9, emp.getEndDate() != null ? emp.getEndDate().toString() : null);
        pstmt.setDouble(10, emp.getMonthlySalaryGross());
        pstmt.setDouble(11, emp.getMonthlySalaryNet());
        pstmt.setString(12, emp.getSchoolName());
        pstmt.setString(13, emp.getInternshipTopic());
        pstmt.setString(14, emp.getStatus());
        pstmt.setString(15, emp.getAddress());
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        String hireStr = rs.getString("hire_date");
        String endStr = rs.getString("end_date");

        return new Employee(
            rs.getInt("id"),
            rs.getString("registration_num"),
            rs.getString("full_name"),
            rs.getString("cin"),
            rs.getString("cnss_num"),
            rs.getString("job_title"),
            rs.getString("department"),
            rs.getString("contract_type"),
            hireStr != null && !hireStr.isEmpty() ? LocalDate.parse(hireStr) : null,
            endStr != null && !endStr.isEmpty() ? LocalDate.parse(endStr) : null,
            rs.getDouble("monthly_salary_gross"),
            rs.getDouble("monthly_salary_net"),
            rs.getString("school_name"),
            rs.getString("internship_topic"),
            rs.getString("status"),
            rs.getString("address")
        );
    }
}
