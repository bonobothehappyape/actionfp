package eu.scram.actionfp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A AuditSubRecomm.
 */
@Entity
@Table(name = "audit_sub_recomm")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "auditsubrecomm")
public class AuditSubRecomm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "sub_recomm_num")
    private String subRecommNum;

    @Column(name = "description")
    private String description;

    @ManyToOne
    private Status status;

    @ManyToOne
    @JsonIgnoreProperties(value = { "report", "status" }, allowSetters = true)
    private AuditRecomm auditRecomm;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuditSubRecomm id(Long id) {
        this.id = id;
        return this;
    }

    public String getSubRecommNum() {
        return this.subRecommNum;
    }

    public AuditSubRecomm subRecommNum(String subRecommNum) {
        this.subRecommNum = subRecommNum;
        return this;
    }

    public void setSubRecommNum(String subRecommNum) {
        this.subRecommNum = subRecommNum;
    }

    public String getDescription() {
        return this.description;
    }

    public AuditSubRecomm description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return this.status;
    }

    public AuditSubRecomm status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public AuditRecomm getAuditRecomm() {
        return this.auditRecomm;
    }

    public AuditSubRecomm auditRecomm(AuditRecomm auditRecomm) {
        this.setAuditRecomm(auditRecomm);
        return this;
    }

    public void setAuditRecomm(AuditRecomm auditRecomm) {
        this.auditRecomm = auditRecomm;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditSubRecomm)) {
            return false;
        }
        return id != null && id.equals(((AuditSubRecomm) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditSubRecomm{" +
            "id=" + getId() +
            ", subRecommNum='" + getSubRecommNum() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
