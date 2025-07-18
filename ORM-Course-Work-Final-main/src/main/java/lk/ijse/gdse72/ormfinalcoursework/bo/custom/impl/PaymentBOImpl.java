package lk.ijse.gdse72.ormfinalcoursework.bo.custom.impl;

import lk.ijse.gdse72.ormfinalcoursework.bo.custom.PaymentBO;
import lk.ijse.gdse72.ormfinalcoursework.dao.DAOFactory;
import lk.ijse.gdse72.ormfinalcoursework.dao.custom.PatientDAO;
import lk.ijse.gdse72.ormfinalcoursework.dao.custom.PaymentDAO;
import lk.ijse.gdse72.ormfinalcoursework.dao.custom.TherapyProgramDAO;
import lk.ijse.gdse72.ormfinalcoursework.dto.PaymentDTO;
import lk.ijse.gdse72.ormfinalcoursework.entity.Patient;
import lk.ijse.gdse72.ormfinalcoursework.entity.Payment;
import lk.ijse.gdse72.ormfinalcoursework.entity.TherapyProgram;

import java.util.ArrayList;

public class PaymentBOImpl implements PaymentBO {

    private final PaymentDAO paymentDAO = DAOFactory.getInstance().getDAO(DAOFactory.DaoType.PAYMENT);
    private final PatientDAO patientDAO = DAOFactory.getInstance().getDAO(DAOFactory.DaoType.PATIENT);
    private final TherapyProgramDAO therapyProgramDAO = DAOFactory.getInstance().getDAO(DAOFactory.DaoType.THERAPY_PROGRAM);

    @Override
    public String getNextPaymentId() throws Exception {
        return paymentDAO.getNextPaymentId();
    }

    @Override
    public ArrayList<PaymentDTO> getAllPayments() throws Exception {
        ArrayList<PaymentDTO> paymentDTOS = new ArrayList<>();
        ArrayList<Payment> payments = (ArrayList<Payment>) paymentDAO.getAll();

        for (Payment payment : payments) {
            paymentDTOS.add(new PaymentDTO(
                    payment.getId(),
                    payment.getSessionId(),
                    payment.getPatientName(),
                    payment.getAmount(),
                    payment.getPaymentMethod(),
                    payment.getPaymentDate(),
                    payment.getStatus(),
                    payment.getPaidAmount(),
                    payment.getBalance(),
                    "",
                    ""
            ));
        }
        return paymentDTOS;
    }

    @Override
    public boolean savePayment(PaymentDTO dto) throws Exception {

        Patient patient = patientDAO.searchByName(dto.getPatientName());

        TherapyProgram therapyProgram = therapyProgramDAO.searchBySessionId(dto.getSessionId());

        if (patient == null) {
            throw new RuntimeException("Patient not found for name: " + dto.getPatientName());
        }
        if (therapyProgram == null) {
            throw new RuntimeException("Therapy Program not found for sessionId: " + dto.getSessionId());
        }

        Payment payment = new Payment(
                dto.getId(),
                dto.getSessionId(),
                dto.getPatientName(),
                dto.getAmount(),
                dto.getPaymentMethod(),
                dto.getPaymentDate(),
                dto.getStatus(),
                dto.getPaidAmount(),
                dto.getBalance(),
                patient,
                therapyProgram
        );
        return paymentDAO.save(payment);
    }

    @Override
    public boolean updatePayment(PaymentDTO dto) throws Exception {
        Patient patient = patientDAO.searchByName(dto.getPatientName());
        TherapyProgram therapyProgram = therapyProgramDAO.searchBySessionId(dto.getSessionId());

        if (patient == null) {
            throw new RuntimeException("Patient not found for name: " + dto.getPatientName());
        }
        if (therapyProgram == null) {
            throw new RuntimeException("Therapy Program not found for sessionId: " + dto.getSessionId());
        }

        Payment payment = new Payment(
                dto.getId(),
                dto.getSessionId(),
                dto.getPatientName(),
                dto.getAmount(),
                dto.getPaymentMethod(),
                dto.getPaymentDate(),
                dto.getStatus(),
                dto.getPaidAmount(),
                dto.getBalance(),
                patient,
                therapyProgram
        );
        return paymentDAO.update(payment);
    }

    @Override
    public boolean deletePayment(String paymentId) throws Exception {
        return paymentDAO.delete(paymentId);
    }
}
