package eu.scram.actionfp.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A ICSRecomm.
 */
@Entity
@Table(name = "ics_recomm")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "icsrecomm")
public class ICSRecomm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "year")
    private Integer year;

    @Column(name = "ics_number")
    private String icsNumber;

    @Column(name = "ics_descr")
    private String icsDescr;

    @Column(name = "title")
    private String title;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ICSRecomm id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getYear() {
        return this.year;
    }

    public ICSRecomm year(Integer year) {
        this.year = year;
        return this;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getIcsNumber() {
        return this.icsNumber;
    }

    public ICSRecomm icsNumber(String icsNumber) {
        this.icsNumber = icsNumber;
        return this;
    }

    public void setIcsNumber(String icsNumber) {
        this.icsNumber = icsNumber;
    }

    public String getIcsDescr() {
        return this.icsDescr;
    }

    public ICSRecomm icsDescr(String icsDescr) {
        this.icsDescr = icsDescr;
        return this;
    }

    public void setIcsDescr(String icsDescr) {
        this.icsDescr = icsDescr;
    }

    public String getTitle() {
        return this.title;
    }

    public ICSRecomm title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ICSRecomm)) {
            return false;
        }
        return id != null && id.equals(((ICSRecomm) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ICSRecomm{" +
            "id=" + getId() +
            ", year=" + getYear() +
            ", icsNumber='" + getIcsNumber() + "'" +
            ", icsDescr='" + getIcsDescr() + "'" +
            ", title='" + getTitle() + "'" +
            "}";
    }
}
