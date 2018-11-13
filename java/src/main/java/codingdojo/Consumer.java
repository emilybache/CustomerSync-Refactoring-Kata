package codingdojo;

import java.util.List;

public class Consumer {
    private Address address;
    private String name;
    private String preferredStore;
    private List<Relation> relations;
    private String externalId;
    private String companyNumber;
    private String personalNumber;

    public String getExternalId() {
        return externalId;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public boolean isCompany() {
        return false;
    }

    public Address getPostalAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreferredStore() {
        return preferredStore;
    }

    public void setPreferredStore(String preferredStore) {
        this.preferredStore = preferredStore;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public void setRelations(List<Relation> relations) {
        this.relations = relations;
    }

}
