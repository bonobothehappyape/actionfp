package eu.scram.actionfp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A ActionChangeMail.
 */
@Entity
@Table(name = "action_change_mail")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "actionchangemail")
public class ActionChangeMail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "action_type")
    private String actionType;

    @ManyToOne
    @JsonIgnoreProperties(value = { "frameworks", "icsRecomm", "ownerUnit", "status" }, allowSetters = true)
    private Action action;

    @ManyToOne
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActionChangeMail id(Long id) {
        this.id = id;
        return this;
    }

    public String getActionType() {
        return this.actionType;
    }

    public ActionChangeMail actionType(String actionType) {
        this.actionType = actionType;
        return this;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Action getAction() {
        return this.action;
    }

    public ActionChangeMail action(Action action) {
        this.setAction(action);
        return this;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public User getUser() {
        return this.user;
    }

    public ActionChangeMail user(User user) {
        this.setUser(user);
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActionChangeMail)) {
            return false;
        }
        return id != null && id.equals(((ActionChangeMail) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ActionChangeMail{" +
            "id=" + getId() +
            ", actionType='" + getActionType() + "'" +
            "}";
    }
}
