import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PriceBasketSpec extends AnyFlatSpec with Matchers {

  // 1. No discount – simple basket with Milk and Bread.
  "A ShoppingBasket" should "calculate correct totals when no discount is applicable" in {
    val basket = new ShoppingBasket()
    basket.addItem("Milk")
    basket.addItem("Bread")
    // Expected: Milk: £1.30, Bread: £0.80 → Subtotal: £2.10, no discounts, Total: £2.10.
    val receipt = basket.calculateReceipt
    receipt.subtotal shouldEqual BigDecimal("2.10")
    receipt.discounts shouldBe empty
    receipt.total shouldEqual BigDecimal("2.10")
  }

  // 2. Soup & Bread discount – for every 2 Soups, one Bread is half-price.
  it should "apply soup and bread discount correctly" in {
    val basket = new ShoppingBasket()
    basket.addItem("Soup")
    basket.addItem("Soup")
    basket.addItem("Bread")
    // Expected: 2 Soups: 2 * £0.65 = £1.30; Bread: £0.80; Subtotal: £2.10;
    // Discount: Bread half price = £0.40; Final Total: £1.70.
    val receipt = basket.calculateReceipt
    receipt.subtotal shouldEqual BigDecimal("2.10")
    receipt.discounts should contain (("Bread half price", BigDecimal("0.40")))
    receipt.total shouldEqual BigDecimal("1.70")
  }

  // 3. Case-insensitive resolution – items added in various letter cases resolve correctly.
  it should "resolve item names regardless of case" in {
    val basket = new ShoppingBasket()
    basket.addItem("soup")    // lowercase
    basket.addItem("SOUP")    // uppercase
    basket.addItem("mIlK")    // mixed case
    basket.addItem("BREAD")   // uppercase
    // Expected: Two Soups: 2 * £0.65 = £1.30; Milk: £1.30; Bread: £0.80;
    // Subtotal: £3.40, Discount on Bread applies once: £0.40; Final Total: £3.00.
    val receipt = basket.calculateReceipt
    receipt.subtotal shouldEqual BigDecimal("3.40")
    receipt.discounts should contain (("Bread half price", BigDecimal("0.40")))
    receipt.total shouldEqual BigDecimal("3.00")
  }

  // 4. Empty basket – no items yields zero totals.
  it should "handle an empty basket" in {
    val basket = new ShoppingBasket()
    val receipt = basket.calculateReceipt
    receipt.subtotal shouldEqual BigDecimal("0.00")
    receipt.discounts shouldBe empty
    receipt.total shouldEqual BigDecimal("0.00")
  }

  // 5. Apples discount only – each Apple gets 10% off.
  it should "apply apples discount correctly" in {
    val basket = new ShoppingBasket()
    basket.addItem("Apples")
    basket.addItem("Apples")
    basket.addItem("Apples")
    // Expected: 3 Apples at £1.00 each = £3.00; Discount: 3 * £0.10 = £0.30; Total: £2.70.
    val receipt = basket.calculateReceipt
    receipt.subtotal shouldEqual BigDecimal("3.00")
    receipt.discounts should contain (("Apples 10% off", BigDecimal("0.30")))
    receipt.total shouldEqual BigDecimal("2.70")
  }

  // 6. Combined discounts – mix of Soup, Bread, and Apples.
  it should "apply combined discounts correctly" in {
    val basket = new ShoppingBasket()
    basket.addItem("Soup")
    basket.addItem("Soup")
    basket.addItem("Bread")
    basket.addItem("Apples")
    basket.addItem("Apples")
    // Expected: Soups: 2 * £0.65 = £1.30; Bread: £0.80; Apples: 2 * £1.00 = £2.00;
    // Subtotal = £4.10; Discounts: Bread half price = £0.40, Apples discount = 2 * £0.10 = £0.20;
    // Final Total = £4.10 - £0.60 = £3.50.
    val receipt = basket.calculateReceipt
    receipt.subtotal shouldEqual BigDecimal("4.10")
    receipt.discounts should contain allOf (
      ("Bread half price", BigDecimal("0.40")),
      ("Apples 10% off", BigDecimal("0.20"))
    )
    receipt.total shouldEqual BigDecimal("3.50")
  }

  // 7. Large quantities – stress test with high item counts.
  it should "handle large quantities of items correctly" in {
    val basket = new ShoppingBasket()
    for (_ <- 1 to 100) basket.addItem("Soup")
    for (_ <- 1 to 50) basket.addItem("Bread")
    for (_ <- 1 to 200) basket.addItem("Apples")
    // Expected:
    // Soups: 100 * £0.65 = £65.00;
    // Bread: 50 * £0.80 = £40.00;
    // Apples: 200 * £1.00 = £200.00;
    // Subtotal = £305.00.
    // Discounts: Soup/Bread discount: floor(100/2) = 50 * £0.40 = £20.00;
    // Apples discount: 200 * £0.10 = £20.00;
    // Total discount = £40.00; Final Total = £305.00 - £40.00 = £265.00.
    val receipt = basket.calculateReceipt
    receipt.subtotal shouldEqual BigDecimal("305.00")
    receipt.discounts should contain allOf (
      ("Bread half price", BigDecimal("20.00")),
      ("Apples 10% off", BigDecimal("20.00"))
    )
    receipt.total shouldEqual BigDecimal("265.00")
  }

  // 8. Unknown item – items not in the catalog are ignored.
  it should "ignore unknown items" in {
    val basket = new ShoppingBasket()
    basket.addItem("XYZ")  // Unknown item.
    basket.addItem("Milk")
    // Expected: Only Milk: £1.30, no discounts.
    val receipt = basket.calculateReceipt
    receipt.subtotal shouldEqual BigDecimal("1.30")
    receipt.discounts shouldBe empty
    receipt.total shouldEqual BigDecimal("1.30")
  }

  // 9. Typo rejection – since no typo correction is performed, any typos are rejected.
  it should "reject items with any typos" in {
    val basket = new ShoppingBasket()
    basket.addItem("soop")   // Typo for "Soup" – rejected.
    basket.addItem("brea")   // Typo for "Bread" – rejected.
    // Expected: No valid items added, so the basket remains empty.
    val receipt = basket.calculateReceipt
    receipt.subtotal shouldEqual BigDecimal("0.00")
    receipt.discounts shouldBe empty
    receipt.total shouldEqual BigDecimal("0.00")
  }

  // 10. Maximal input – handle a basket with 1,000 items.
  it should "handle maximal input of 1000 items correctly" in {
    val basket = new ShoppingBasket()
    // Distribution: 400 Soups, 300 Bread, 200 Milk, 100 Apples.
    for (_ <- 1 to 400) basket.addItem("Soup")
    for (_ <- 1 to 300) basket.addItem("Bread")
    for (_ <- 1 to 200) basket.addItem("Milk")
    for (_ <- 1 to 100) basket.addItem("Apples")
    // Expected Calculations:
    // Soups: 400 * £0.65 = £260.00
    // Bread: 300 * £0.80 = £240.00
    // Milk: 200 * £1.30 = £260.00
    // Apples: 100 * £1.00 = £100.00
    // Subtotal = £860.00.
    // Discounts:
    // Soup & Bread discount: floor(400 / 2) = 200; 200 * £0.40 = £80.00.
    // Apples discount: 100 * £0.10 = £10.00.
    // Total discount = £80.00 + £10.00 = £90.00.
    // Final Total = £860.00 - £90.00 = £770.00.
    val receipt = basket.calculateReceipt
    receipt.subtotal shouldEqual BigDecimal("860.00")
    receipt.discounts should contain allOf (
      ("Bread half price", BigDecimal("80.00")),
      ("Apples 10% off", BigDecimal("10.00"))
    )
    receipt.total shouldEqual BigDecimal("770.00")
  }
}
