package codingdojo;

public class AddressPrinter {
    public static String printAddress(Address address) {
        if (address == null) {
            return "'null'";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\'");
        sb.append(address.getStreet());
        sb.append(", ");
        sb.append(address.getPostalCode());
        sb.append(" ");
        sb.append(address.getCity());
        sb.append("\'");
        return sb.toString();
    }
}
