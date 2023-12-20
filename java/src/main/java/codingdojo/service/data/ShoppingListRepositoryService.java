package codingdojo.service.data;

import codingdojo.model.ShoppingList;
import codingdojo.repository.ShoppingListRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ShoppingListRepositoryService {

    private final ShoppingListRepository shoppingListRepository;

    public void updateShoppingList(ShoppingList consumerShoppingList) {
        shoppingListRepository.updateShoppingList(consumerShoppingList);
    }
}
