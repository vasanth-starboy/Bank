package com.wipro.bank.Dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.wipro.bank.bean.TransferBean;
import com.wipro.bank.util.DbUtil;

public class BankDAO {

   
    public int generateSequenceNumber() {
        Connection con = DbUtil.getDbConnection();
        
        String sql = "SELECT transaction_seq.NEXTVAL FROM dual";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public boolean validateAccount(String accountNumber) {
        Connection con = DbUtil.getDbConnection();
        String sql = "SELECT 1 FROM account_tbl WHERE account_number = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    
    public float findBalance(String accountNumber) {
        Connection con = DbUtil.getDbConnection();
        String sql = "SELECT balance FROM account_tbl WHERE account_number = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getFloat("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    
    public boolean updateBalance(String accountNumber, float newBalance) {
        Connection con = DbUtil.getDbConnection();
        String sql = "UPDATE account_tbl SET balance = ? WHERE account_number = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setFloat(1, newBalance);
            ps.setString(2, accountNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

   
    public boolean transferMoney(TransferBean bean) {

        int txnId = generateSequenceNumber();
        bean.setTransactionID(txnId);

        Connection con = DbUtil.getDbConnection();

        String sql = "INSERT INTO transfer_tbl " +
                     "(transaction_id, account_number, beneficiary_account_number, " +
                     " transaction_date, transaction_amount) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bean.getTransactionID());
            ps.setString(2, bean.getFromAccountNumber());
            ps.setString(3, bean.getToAccountNumber());
            ps.setDate(4, new Date(bean.getDateOfTransaction().getTime()));
            ps.setFloat(5, bean.getAmount());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
