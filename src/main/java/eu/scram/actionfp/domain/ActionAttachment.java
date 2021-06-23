package eu.scram.actionfp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A ActionAttachment.
 */
@Entity
@Table(name = "action_attachment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "actionattachment")
public class ActionAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "mime_type")
    private String mimeType;

    @Lob
    @Column(name = "attached_file")
    private byte[] attachedFile;

    @Column(name = "attached_file_content_type")
    private String attachedFileContentType;

    @Column(name = "url")
    private String url;

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

    public ActionAttachment id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public ActionAttachment name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public ActionAttachment mimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getAttachedFile() {
        return this.attachedFile;
    }

    public ActionAttachment attachedFile(byte[] attachedFile) {
        this.attachedFile = attachedFile;
        return this;
    }

    public void setAttachedFile(byte[] attachedFile) {
        this.attachedFile = attachedFile;
    }

    public String getAttachedFileContentType() {
        return this.attachedFileContentType;
    }

    public ActionAttachment attachedFileContentType(String attachedFileContentType) {
        this.attachedFileContentType = attachedFileContentType;
        return this;
    }

    public void setAttachedFileContentType(String attachedFileContentType) {
        this.attachedFileContentType = attachedFileContentType;
    }

    public String getUrl() {
        return this.url;
    }

    public ActionAttachment url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Action getAction() {
        return this.action;
    }

    public ActionAttachment action(Action action) {
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
        if (!(o instanceof ActionAttachment)) {
            return false;
        }
        return id != null && id.equals(((ActionAttachment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ActionAttachment{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", mimeType='" + getMimeType() + "'" +
            ", attachedFile='" + getAttachedFile() + "'" +
            ", attachedFileContentType='" + getAttachedFileContentType() + "'" +
            ", url='" + getUrl() + "'" +
            "}";
    }
}
