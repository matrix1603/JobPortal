package producers.job.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobApplicationCreatedNotification {
    private int jobId;
    private String jobTitle;
    private String companyName;
    private String userEmail;
    private String message;
}
