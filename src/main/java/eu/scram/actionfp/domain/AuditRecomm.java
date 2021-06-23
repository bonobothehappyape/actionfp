package eu.scram.actionfp.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A AuditRecomm.
 */
@Entity
@Table(name = "audit_recomm")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "auditrecomm")
public class AuditRecomm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "recomm_number")
    private String recommNumber;

    @Column(name = "priority")
    private Long priority;

    @Column(name = "description")
    private String description;

    @ManyToOne
    private AuditReport report;

    @ManyToOne
    private Status status;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuditRecomm id(Long id) {
        this.id = id;
        return this;
    }

    public String getRecommNumber() {
        return this.recommNumber;
    }

    public AuditRecomm recommNumber(String recommNumber) {
        this.recommNumber = recommNumber;
        return this;
    }

    public void setRecommNumber(String recommNumber) {
        this.recommNumber = recommNumber;
    }

    public Long getPriority() {
        return this.priority;
    }

    public AuditRecomm priority(Long priority) {
        this.priority = priority;
        return this;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return this.description;
    }

    public AuditRecomm description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AuditReport getReport() {
        return this.report;
    }

    public AuditRecomm report(AuditReport auditReport) {
        this.setReport(auditReport);
        return this;
    }

    public void setReport(AuditReport auditReport) {
        this.report = auditReport;
    }

    public Status getStatus() {
        return this.status;
    }

    public AuditRecomm status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditRecomm)) {
            return false;
        }
        return id != null && id.equals(((AuditRecomm) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditRecomm{" +
            "id=" + getId() +
            ", recommNumber='" + getRecommNumber() + "'" +
            ", priority=" + getPriority() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
