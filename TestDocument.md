# Test-Driven Development (TDD) Documentation for PriceBasket

## Introduction
This document provides a structured overview of the test-driven development (TDD) approach used to validate the functionality of the `ShoppingBasket` class in a Scala-based pricing and discounting system. The tests are implemented using **ScalaTest** and follow a clear, iterative methodology to ensure correctness and reliability.

## Test Structure
Each test follows the typical **Arrange-Act-Assert** structure:
- **Arrange:** Set up the test environment by creating a `ShoppingBasket` instance and adding items.
- **Act:** Call the `calculateReceipt` method to compute the total and discounts.
- **Assert:** Verify that the computed values match the expected values.

## Test Cases
### 1. Basic Calculation (No Discount)
**Scenario:** Verify that a basket with standard items (Milk, Bread) computes correctly when no discount applies.
- **Input:** Milk, Bread
- **Expected Output:**
  - Subtotal: £2.10
  - Discounts: None
  - Total: £2.10

### 2. Discount for Soup & Bread
**Scenario:** Bread receives a 50% discount when two Soups are purchased.
- **Input:** Soup, Soup, Bread
- **Expected Output:**
  - Subtotal: £2.10
  - Discounts: £0.40 off Bread
  - Total: £1.70

### 3. Case-Insensitive Item Names
**Scenario:** Item names should be case-insensitive.
- **Input:** soup, SOUP, mIlK, BREAD
- **Expected Output:**
  - Subtotal: £3.40
  - Discounts: £0.40 off Bread
  - Total: £3.00

### 4. Empty Basket
**Scenario:** Ensure an empty basket results in zero totals.
- **Input:** No items
- **Expected Output:**
  - Subtotal: £0.00
  - Discounts: None
  - Total: £0.00

### 5. Apple Discount (10% Off)
**Scenario:** Each Apple gets a 10% discount.
- **Input:** Apples x3
- **Expected Output:**
  - Subtotal: £3.00
  - Discounts: £0.30 off Apples
  - Total: £2.70

### 6. Combined Discounts
**Scenario:** Apply multiple discounts together.
- **Input:** Soup x2, Bread, Apples x2
- **Expected Output:**
  - Subtotal: £4.10
  - Discounts: £0.40 (Bread), £0.20 (Apples)
  - Total: £3.50

### 7. Large Quantities Stress Test
**Scenario:** Ensure calculations remain accurate for high item counts.
- **Input:** Soup x100, Bread x50, Apples x200
- **Expected Output:**
  - Subtotal: £305.00
  - Discounts: £20.00 (Bread), £20.00 (Apples)
  - Total: £265.00

### 8. Unknown Items
**Scenario:** Ignore items that are not in the catalog.
- **Input:** XYZ, Milk
- **Expected Output:**
  - Subtotal: £1.30
  - Discounts: None
  - Total: £1.30

### 9. Typo Rejection
**Scenario:** Misspelled items should not be recognized.
- **Input:** soop, brea
- **Expected Output:**
  - Subtotal: £0.00
  - Discounts: None
  - Total: £0.00

### 10. Maximum Input Capacity Test
**Scenario:** Test performance and accuracy with 1000 items.
- **Input:** Soup x400, Bread x300, Milk x200, Apples x100
- **Expected Output:**
  - Subtotal: £860.00
  - Discounts: £80.00 (Bread), £10.00 (Apples)
  - Total: £770.00

## Conclusion
This TDD approach ensures that each function of the `ShoppingBasket` class is rigorously tested under various scenarios, covering:
1. Basic price calculations
2. Discount applications
3. Edge cases (empty, unknown items, large datasets)
4. Performance testing

By continuously validating new changes against this test suite, the system maintains high reliability and correctness over time.


