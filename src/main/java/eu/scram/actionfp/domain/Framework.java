package eu.scram.actionfp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Framework.
 */
@Entity
@Table(name = "framework")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "framework")
public class Framework implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "year")
    private Integer year;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private Integer type;

    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(unique = true)
    private Unit unit;

    @ManyToOne
    @JsonIgnoreProperties(value = { "frameworks", "icsRecomm", "ownerUnit", "status" }, allowSetters = true)
    private Action action;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Framework id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getYear() {
        return this.year;
    }

    public Framework year(Integer year) {
        this.year = year;
        return this;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getName() {
        return this.name;
    }

    public Framework name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return this.type;
    }

    public Framework type(Integer type) {
        this.type = type;
        return this;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public Framework description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Unit getUnit() {
        return this.unit;
    }

    public Framework unit(Unit unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Action getAction() {
        return this.action;
    }

    public Framework action(Action action) {
        this.setAction(action);
        return this;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Framework)) {
            return false;
        }
        return id != null && id.equals(((Framework) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Framework{" +
            "id=" + getId() +
            ", year=" + getYear() +
            ", name='" + getName() + "'" +
            ", type=" + getType() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
