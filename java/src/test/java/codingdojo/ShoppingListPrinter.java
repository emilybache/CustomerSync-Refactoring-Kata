package codingdojo;

import java.util.List;

public class ShoppingListPrinter {
    public static String printShoppingLists(List<ShoppingList> shoppingLists, String indent) {
        if (shoppingLists.size() == 0) {
            return "[]";
        }
        if (shoppingLists.size() == 1) {
            return "[" + shoppingLists.get(0).getProducts().toString() + "]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n" + indent + "shoppingLists:");
        for (ShoppingList shoppingList : shoppingLists) {
            sb.append("\n" + indent + "- ");
            sb.append(shoppingList.getProducts().toString());
        }
        sb.append("\n" + indent + "]");
        return sb.toString();
    }
}
