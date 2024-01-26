package entity.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "skills_records")
@ToString(exclude = {"jobs"})
@JsonIgnoreProperties({"jobs"})
public class Skills {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "skill")
    private String skill;

    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    private List<Job> jobs;
}
