package eu.scram.actionfp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Action.
 */
@Entity
@Table(name = "action")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "action")
public class Action implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "task_description")
    private String taskDescription;

    @Column(name = "requires_periodic_followup")
    private Boolean requiresPeriodicFollowup;

    @Column(name = "initial_deadline")
    private LocalDate initialDeadline;

    @Column(name = "updated_deadline")
    private LocalDate updatedDeadline;

    @Column(name = "done_date")
    private LocalDate doneDate;

    @Column(name = "verified_date")
    private LocalDate verifiedDate;

    @OneToMany(mappedBy = "action")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "unit", "action" }, allowSetters = true)
    private Set<Framework> frameworks = new HashSet<>();

    @ManyToOne
    private ICSRecomm icsRecomm;

    @ManyToOne
    private Unit ownerUnit;

    @ManyToOne
    private Status status;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Action id(Long id) {
        this.id = id;
        return this;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public Action taskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return this.taskDescription;
    }

    public Action taskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
        return this;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Boolean getRequiresPeriodicFollowup() {
        return this.requiresPeriodicFollowup;
    }

    public Action requiresPeriodicFollowup(Boolean requiresPeriodicFollowup) {
        this.requiresPeriodicFollowup = requiresPeriodicFollowup;
        return this;
    }

    public void setRequiresPeriodicFollowup(Boolean requiresPeriodicFollowup) {
        this.requiresPeriodicFollowup = requiresPeriodicFollowup;
    }

    public LocalDate getInitialDeadline() {
        return this.initialDeadline;
    }

    public Action initialDeadline(LocalDate initialDeadline) {
        this.initialDeadline = initialDeadline;
        return this;
    }

    public void setInitialDeadline(LocalDate initialDeadline) {
        this.initialDeadline = initialDeadline;
    }

    public LocalDate getUpdatedDeadline() {
        return this.updatedDeadline;
    }

    public Action updatedDeadline(LocalDate updatedDeadline) {
        this.updatedDeadline = updatedDeadline;
        return this;
    }

    public void setUpdatedDeadline(LocalDate updatedDeadline) {
        this.updatedDeadline = updatedDeadline;
    }

    public LocalDate getDoneDate() {
        return this.doneDate;
    }

    public Action doneDate(LocalDate doneDate) {
        this.doneDate = doneDate;
        return this;
    }

    public void setDoneDate(LocalDate doneDate) {
        this.doneDate = doneDate;
    }

    public LocalDate getVerifiedDate() {
        return this.verifiedDate;
    }

    public Action verifiedDate(LocalDate verifiedDate) {
        this.verifiedDate = verifiedDate;
        return this;
    }

    public void setVerifiedDate(LocalDate verifiedDate) {
        this.verifiedDate = verifiedDate;
    }

    public Set<Framework> getFrameworks() {
        return this.frameworks;
    }

    public Action frameworks(Set<Framework> frameworks) {
        this.setFrameworks(frameworks);
        return this;
    }

    public Action addFramework(Framework framework) {
        this.frameworks.add(framework);
        framework.setAction(this);
        return this;
    }

    public Action removeFramework(Framework framework) {
        this.frameworks.remove(framework);
        framework.setAction(null);
        return this;
    }

    public void setFrameworks(Set<Framework> frameworks) {
        if (this.frameworks != null) {
            this.frameworks.forEach(i -> i.setAction(null));
        }
        if (frameworks != null) {
            frameworks.forEach(i -> i.setAction(this));
        }
        this.frameworks = frameworks;
    }

    public ICSRecomm getIcsRecomm() {
        return this.icsRecomm;
    }

    public Action icsRecomm(ICSRecomm iCSRecomm) {
        this.setIcsRecomm(iCSRecomm);
        return this;
    }

    public void setIcsRecomm(ICSRecomm iCSRecomm) {
        this.icsRecomm = iCSRecomm;
    }

    public Unit getOwnerUnit() {
        return this.ownerUnit;
    }

    public Action ownerUnit(Unit unit) {
        this.setOwnerUnit(unit);
        return this;
    }

    public void setOwnerUnit(Unit unit) {
        this.ownerUnit = unit;
    }

    public Status getStatus() {
        return this.status;
    }

    public Action status(Status status) {
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
        if (!(o instanceof Action)) {
            return false;
        }
        return id != null && id.equals(((Action) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Action{" +
            "id=" + getId() +
            ", taskName='" + getTaskName() + "'" +
            ", taskDescription='" + getTaskDescription() + "'" +
            ", requiresPeriodicFollowup='" + getRequiresPeriodicFollowup() + "'" +
            ", initialDeadline='" + getInitialDeadline() + "'" +
            ", updatedDeadline='" + getUpdatedDeadline() + "'" +
            ", doneDate='" + getDoneDate() + "'" +
            ", verifiedDate='" + getVerifiedDate() + "'" +
            "}";
    }
}
