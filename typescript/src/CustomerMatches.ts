import {Customer} from "@/Customer";

export class CustomerMatches {
  readonly duplicates: (Customer | null)[] = []
  matchTerm: string | null = null;
  customer: Customer | null = null;

  hasDuplicates() {
    return this.duplicates.length > 0;
  }

  addDuplicate(duplicate: Customer | null) {
    this.duplicates.push(duplicate);
  }
}
