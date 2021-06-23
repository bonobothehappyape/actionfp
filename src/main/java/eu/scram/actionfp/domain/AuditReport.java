package eu.scram.actionfp.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A AuditReport.
 */
@Entity
@Table(name = "audit_report")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "auditreport")
public class AuditReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "year")
    private Integer year;

    @Column(name = "report_title")
    private String reportTitle;

    @Column(name = "institution")
    private Long institution;

    @Column(name = "report_description")
    private String reportDescription;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuditReport id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getYear() {
        return this.year;
    }

    public AuditReport year(Integer year) {
        this.year = year;
        return this;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getReportTitle() {
        return this.reportTitle;
    }

    public AuditReport reportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
        return this;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public Long getInstitution() {
        return this.institution;
    }

    public AuditReport institution(Long institution) {
        this.institution = institution;
        return this;
    }

    public void setInstitution(Long institution) {
        this.institution = institution;
    }

    public String getReportDescription() {
        return this.reportDescription;
    }

    public AuditReport reportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
        return this;
    }

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditReport)) {
            return false;
        }
        return id != null && id.equals(((AuditReport) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditReport{" +
            "id=" + getId() +
            ", year=" + getYear() +
            ", reportTitle='" + getReportTitle() + "'" +
            ", institution=" + getInstitution() +
            ", reportDescription='" + getReportDescription() + "'" +
            "}";
    }
}
