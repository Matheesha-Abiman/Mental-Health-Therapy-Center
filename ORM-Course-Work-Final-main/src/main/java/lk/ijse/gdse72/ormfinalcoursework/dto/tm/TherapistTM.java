package lk.ijse.gdse72.ormfinalcoursework.dto.tm;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class TherapistTM {
    private String therapistId;
    private String therapistName;
    private String specialization;
    private String availability;
    private int contact;
    private String program;
    private String mail;
}
