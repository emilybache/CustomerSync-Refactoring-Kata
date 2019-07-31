package codingdojo;

public class ConsumerPrinter {

    public static String print(Consumer consumer, String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("Consumer {");
        sb.append("\n" + indent + "    externalId='" + consumer.getExternalId() + '\'');
        sb.append("\n" + indent + "    companyNumber='" + consumer.getCompanyNumber() + '\'' );
        sb.append("\n" + indent + "    name='" + consumer.getName() + '\'' );
        sb.append("\n" + indent + "    preferredStore='" + consumer.getPreferredStore() + '\'');
        sb.append("\n" + indent + "    address=" + AddressPrinter.printAddress(consumer.getPostalAddress()));
        sb.append("\n" + indent + "    shoppingLists=" + ShoppingListPrinter.printShoppingLists(consumer.getShoppingLists(), indent + "    ") );
        sb.append("}");

        return sb.toString();
    }
}
