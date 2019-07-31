package codingdojo;

public class CustomerPrinter {

    public static String print(Customer customer, String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n" + indent + "Customer {");
        sb.append("\n" + indent + "    externalId='" + customer.getExternalId() + '\'');
        sb.append("\n" + indent + "    masterExternalId='" + customer.getMasterExternalId() + '\'');
        sb.append("\n" + indent + "    companyNumber='" + customer.getCompanyNumber() + '\'' );
        sb.append("\n" + indent + "    internalId='" + customer.getInternalId() + '\'' );
        sb.append("\n" + indent + "    name='" + customer.getName() + '\'' );
        sb.append("\n" + indent + "    customerType=" + customer.getCustomerType() );
        sb.append("\n" + indent + "    preferredStore='" + customer.getPreferredStore() + '\'');
        sb.append("\n" + indent + "    address=" + AddressPrinter.printAddress(customer.getAddress()));
        sb.append("\n" + indent + "    shoppingLists=" + ShoppingListPrinter.printShoppingLists(customer.getShoppingLists(), indent + "    ") );
        sb.append("\n" + indent + "}");
        return sb.toString();
    }

}
