package services.job.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateJobRequest {

    @NotBlank
    private String jobTitle;

    @NotBlank
    private String company;


    @NotNull
    private long numberOfOpenings;


    @NotBlank
    private String country;


    @NotBlank
    private String language;


    @NotBlank
    private String jobType;


    @NotBlank
    private String schedule;


    @NotNull
    private double minPay;


    @NotNull
    private double maxPay;

    @NotBlank
    private String supplementalPay;

    @NotBlank
    private String jobCategory;

    @NotBlank
    private String jobMode;

    @NotBlank
    private String benefits;

    @NotNull
    private List<String> skills;

    @NotBlank
    private String jobDescription;

    @NotNull
    private boolean isRemote;

    @NotBlank
    private String location;

}
