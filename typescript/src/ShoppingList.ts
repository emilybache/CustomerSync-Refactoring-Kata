export class ShoppingList {
  readonly products: string[];

  constructor(...products: string[]) {
    this.products = products;
  }
}
