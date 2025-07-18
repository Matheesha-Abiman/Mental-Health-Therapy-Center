package lk.ijse.gdse72.ormfinalcoursework.bo.custom;

import lk.ijse.gdse72.ormfinalcoursework.bo.SuperBO;
import lk.ijse.gdse72.ormfinalcoursework.dto.PaymentDTO;

import java.util.ArrayList;

public interface PaymentBO extends SuperBO {
    String getNextPaymentId() throws Exception;
    ArrayList<PaymentDTO> getAllPayments() throws Exception;
    boolean savePayment(PaymentDTO dto) throws Exception;
    boolean updatePayment(PaymentDTO dto) throws Exception;
    boolean deletePayment(String paymentId) throws Exception;
}