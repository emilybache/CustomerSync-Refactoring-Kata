package codingdojo.domain.entity;

import codingdojo.Address;
import codingdojo.dto.ExternalCustomer;
import codingdojo.ShoppingList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Customer {
    private String externalId;
    private String masterExternalId;
    private Address address;
    private String preferredStore;
    private List<ShoppingList> shoppingLists = new ArrayList<>();
    private String internalId;
    private String name;
    private CustomerType customerType;
    private String companyNumber;

    private int bonusPointBalance;

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public void setMasterExternalId(String masterExternalId) {
        this.masterExternalId = masterExternalId;
    }

    public String getMasterExternalId() {
        return masterExternalId;
    }

    public Address getAddress() {
        return address;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreferredStore() {
        return preferredStore;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public List<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }


    public String getName() {
        return name;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getExternalId() {
        return externalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(externalId, customer.externalId) &&
                Objects.equals(masterExternalId, customer.masterExternalId) &&
                Objects.equals(companyNumber, customer.companyNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalId, masterExternalId, companyNumber);
    }

    public void updateFromExternal(ExternalCustomer externalCustomer) {
        this.name = externalCustomer.getName();
        if (externalCustomer.isCompany()) {
            this.companyNumber = externalCustomer.getCompanyNumber();
            this.customerType = CustomerType.COMPANY;
        } else {
            this.customerType = CustomerType.PERSON;
        }
        address = externalCustomer.getPostalAddress();
        externalCustomer.getShoppingLists().forEach(consumerShoppingList -> this.shoppingLists.add(consumerShoppingList));
        bonusPointBalance = externalCustomer.getBonusPointsBalance();
    }

    private Customer(String externalId,
                     String masterExternalId,
                     Address address,
                     String preferredStore,
                     String internalId,
                     String name,
                     CustomerType customerType,
                     String companyNumber,
                     int bonusPointBalance) {
        this.externalId = externalId;
        this.masterExternalId = masterExternalId;
        this.address = address;
        this.preferredStore = preferredStore;
        this.shoppingLists = shoppingLists == null ? new ArrayList<>() : shoppingLists;
        this.internalId = internalId;
        this.name = name;
        this.customerType = customerType;
        this.companyNumber = companyNumber;
        this.bonusPointBalance = bonusPointBalance;

    }

    public static Customer from(ExternalCustomer externalCustomer) {
        Customer customer = new Customer(
                externalCustomer.getExternalId(),
                externalCustomer.getExternalId(),
                externalCustomer.getPostalAddress(),
                externalCustomer.getPreferredStore(),
                null,
                externalCustomer.getName(),
                externalCustomer.isCompany() ? CustomerType.COMPANY : CustomerType.PERSON,
                externalCustomer.isCompany() ? externalCustomer.getCompanyNumber() : null,
                externalCustomer.isCompany() ? 0 : externalCustomer.getBonusPointsBalance()
        );
        if(externalCustomer.getShoppingLists() != null) {
            externalCustomer.getShoppingLists().forEach(consumerShoppingList -> customer.shoppingLists.add(consumerShoppingList));
        }
        return customer;
    }

    public static Customer createCompany(String externalId, String internalId, String companyNumber) {
        return new Customer(externalId,
                null,
                null,
                null,
                internalId,
                null,
                CustomerType.COMPANY,
                companyNumber,
                0);
    }


    public static Customer createPerson(String externalId, String internalId) {
        return new Customer(externalId,
                null,
                null,
                null,
                internalId,
                null,
                CustomerType.PERSON,
                null,
                0);
    }

    public int getBonusPointBalance() {
        return bonusPointBalance;
    }
}