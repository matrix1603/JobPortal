package entity.job;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "job_records")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @Column(name = "company_name", nullable = false)
    private String company;

    @Column(name = "no_of_openings", nullable = false)
    private long numberOfOpenings;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "job_type", nullable = false)
    private String jobType;

    @Column(name = "job_category", nullable = false)
    private String jobCategory;

    @Column(name = "job_mode", nullable = false)  // REMOTE OR HYBRID
    private String jobMode;

    @Column(name = "schedule")
    private String schedule;

    @Column(name = "min_pay", nullable = false)
    private double minPay;

    @Column(name = "max_pay", nullable = false)
    private double maxPay;

    @Column(name = "supplemental_pay", nullable = false)
    private String supplementalPay;

    @Column(name = "benefits", nullable = false)
    private String benefits;

    @Column(name = "job_description", nullable = false)
    private String jobDescription;

    @Column(name = "is_remote", nullable = false)
    private boolean isRemote;

    @Column(name = "job_location", nullable = false)
    private String location;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "job_skills",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skills> skills;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "updated_date")
    private Date updatedDate;
}
