package lk.ijse.gdse72.ormfinalcoursework.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.gdse72.ormfinalcoursework.bo.BOFactory;
import lk.ijse.gdse72.ormfinalcoursework.bo.custom.PaymentBO;
import lk.ijse.gdse72.ormfinalcoursework.dao.custom.TherapySessionDAO;
import lk.ijse.gdse72.ormfinalcoursework.dao.custom.impl.TherapySessionDAOImpl;
import lk.ijse.gdse72.ormfinalcoursework.db.DBConnection;
import lk.ijse.gdse72.ormfinalcoursework.dto.PaymentDTO;
import lk.ijse.gdse72.ormfinalcoursework.dto.TherapySessionDTO;
import lk.ijse.gdse72.ormfinalcoursework.dto.tm.PaymentTM;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PaymentPageController {

    @FXML private JFXButton btnClear, btnGenerate, btnLoadSession, btnPrint, btnProcess, btnSearch;
    @FXML private JFXComboBox<String> cmbPaymentMethod, cmbStatus, cmbSessionId;
    @FXML private TableColumn<PaymentTM, BigDecimal> colAmount, colBalance, colPayedAmount;
    @FXML private TableColumn<PaymentTM, LocalDate> colDate;
    @FXML private TableColumn<PaymentTM, String> colMethod, colPatientName, colPaymentId, colSessionId, colStatus;
    @FXML private DatePicker datePayment;
    @FXML private AnchorPane paymentAnchor;
    @FXML private TableView<PaymentTM> tblPayments;
    @FXML private JFXTextField txtAmount, txtBalance, txtPayedAmmount, txtPatientName, txtPaymentId;

    private final PaymentBO paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOFactory.BOType.PAYMENT);

    public void initialize() {
        try {
            populateComboBoxes();
            refreshPage();
            loadTableData();
            visibleData();
            txtPaymentId.setText(paymentBO.getNextPaymentId());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "ID Generation Failed: " + e.getMessage()).show();
        }
    }

    private void refreshPage() throws Exception {
        txtPaymentId.setText(paymentBO.getNextPaymentId());
        cmbSessionId.setValue("");
        txtPatientName.clear();
        txtAmount.clear();
        cmbPaymentMethod.setValue(null);
        cmbStatus.setValue(null);
        txtBalance.clear();
        txtPayedAmmount.clear();
        datePayment.setValue(null);
    }

    private void loadTableData() throws Exception {
        ArrayList<PaymentDTO> paymentDTOS = paymentBO.getAllPayments();
        ObservableList<PaymentTM> paymentTMS = FXCollections.observableArrayList();

        for (PaymentDTO dto : paymentDTOS) {
            paymentTMS.add(new PaymentTM(
                    dto.getId(), dto.getSessionId(), dto.getPatientName(), dto.getAmount(),
                    dto.getPaymentMethod(), dto.getPaymentDate(), dto.getStatus(),
                    dto.getPaidAmount(), dto.getBalance()
            ));
        }
        tblPayments.setItems(paymentTMS);
    }

    private void populateComboBoxes() throws SQLException {
        TherapySessionDAO sessionDAO = new TherapySessionDAOImpl();
        cmbSessionId.setItems(FXCollections.observableArrayList(sessionDAO.getSessionId()));
        cmbPaymentMethod.setItems(FXCollections.observableArrayList("Cash", "Credit/Debit Card", "Bank Transfer", "Cheque"));
        cmbStatus.setItems(FXCollections.observableArrayList("Pending", "Completed", "Failed", "Refunded"));
    }

    private void visibleData() {
        colPaymentId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSessionId.setCellValueFactory(new PropertyValueFactory<>("sessionId"));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPayedAmount.setCellValueFactory(new PropertyValueFactory<>("paidAmount"));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
    }

    @FXML void clearOnAction(ActionEvent event) {
        try {
            refreshPage();
            loadTableData();
            visibleData();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    void generateInvoiceOnAction(ActionEvent event) {
        try {
            // Get database connection
            Connection connection = DBConnection.getInstance().getConnection();

            // Create parameter map
            Map<String, Object> parameters = new HashMap<>();

            // Load the pre-compiled jasper file
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(
                    getClass().getResourceAsStream("/reports/Report_Payment.jasper")
            );
            // Fill the report with data and the connection
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameters,
                    connection
            );
            // Display the report
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to generate report: " + e.getMessage()).show();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database connection error: " + e.getMessage()).show();
        }
    }


    @FXML void loadSessionOnAction(ActionEvent event) {
        try {
            TherapySessionDTO session = new TherapySessionDAOImpl().getSession(cmbSessionId.getValue());
            if (session != null) txtPatientName.setText(session.getPatientName());
            else {
                new Alert(Alert.AlertType.WARNING, "No Session found!").show();
                refreshPage();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load patient: " + e.getMessage()).show();
        }
    }

    @FXML void processPaymentOnAction(ActionEvent event) {
        try {
            PaymentDTO dto = new PaymentDTO(
                    txtPaymentId.getText(),
                    cmbSessionId.getValue(),
                    txtPatientName.getText(),
                    new BigDecimal(txtAmount.getText()),
                    cmbPaymentMethod.getValue(),
                    datePayment.getValue(),
                    cmbStatus.getValue(),
                    new BigDecimal(txtPayedAmmount.getText()),
                    new BigDecimal(txtBalance.getText()),
                    null, null
            );

            if (dto.getId().isEmpty() || dto.getSessionId().isEmpty() || dto.getPatientName().isEmpty()
                    || dto.getPaymentMethod() == null || dto.getStatus() == null || dto.getPaymentDate() == null)
                throw new IllegalArgumentException("Please fill all the fields with valid data!");

            boolean isSaved = paymentBO.savePayment(dto);
            new Alert(isSaved ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                    isSaved ? "Payment Process Saved Successfully!" : "Failed to Save Payment Process!").show();

            if (isSaved) {
                loadTableData();
                refreshPage();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }


    @FXML void canselPaymentOnAction(ActionEvent event) {
        PaymentTM selected = tblPayments.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to Cancel Payment: " + selected.getId() + "?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    boolean deleted = paymentBO.deletePayment(selected.getId());
                    new Alert(deleted ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                            deleted ? "Payment deleted successfully!" : "Failed to delete the Payment!").show();
                    if (deleted) {
                        loadTableData();
                        refreshPage();
                    }
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "Database error: " + e.getMessage()).show();
                }
            }
        } else new Alert(Alert.AlertType.WARNING, "Please select a Payment to delete.").show();
    }

    @FXML void tblPaymentClickOnAction(MouseEvent mouseEvent) {
        PaymentTM selected = tblPayments.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtPaymentId.setText(selected.getId());
            cmbSessionId.setValue(selected.getSessionId());
            txtPatientName.setText(selected.getPatientName());
            txtAmount.setText(selected.getAmount().toString());
            cmbPaymentMethod.setValue(selected.getPaymentMethod());
            cmbStatus.setValue(selected.getStatus());
            txtBalance.setText(selected.getBalance().toString());
            txtPayedAmmount.setText(selected.getPaidAmount().toString());
            datePayment.setValue(selected.getPaymentDate());
        }
    }

    @FXML void calOnAction(ActionEvent event) {
        try {
            BigDecimal amount = new BigDecimal(txtAmount.getText().trim());
            BigDecimal paidAmount = new BigDecimal(txtPayedAmmount.getText().trim());
            BigDecimal balance = paidAmount.subtract(amount);

            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                txtBalance.setText("0.00");
                new Alert(Alert.AlertType.WARNING,
                        "Not enough payment. You still owe: Rs. " +
                                balance.abs().setScale(2, RoundingMode.HALF_UP)
                ).show();
            } else {
                txtBalance.setText(balance.setScale(2, RoundingMode.HALF_UP).toString());
            }

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid number format. Enter numeric values only.").show();
        } catch (IllegalArgumentException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }
}