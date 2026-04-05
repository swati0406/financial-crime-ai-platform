package co.eaiwo.sanctions.domain;

import jakarta.persistence.*;

@Entity
@Table(schema = "sanctions", name = "sanctions_list")
public class SanctionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String entityName;
    private String entityType;     // INDIVIDUAL, ORGANISATION
    private String listType;       // OFAC_SDN, UK_CONSOLIDATED, UN_SECURITY_COUNCIL, EU
    private String country;
    private String reason;
    private String status;         // ACTIVE, DELISTED

    public SanctionedEntity() {}

    public SanctionedEntity(String entityName, String entityType,
                            String listType, String country,
                            String reason, String status) {
        this.entityName = entityName;
        this.entityType = entityType;
        this.listType = listType;
        this.country = country;
        this.reason = reason;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getEntityName() { return entityName; }
    public String getEntityType() { return entityType; }
    public String getListType() { return listType; }
    public String getCountry() { return country; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
}