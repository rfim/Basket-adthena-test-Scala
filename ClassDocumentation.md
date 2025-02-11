# Scala Shopping Basket Documentation 🛒

## Overview
This Scala application models a simple shopping basket system. It allows users to add items to a basket, applies applicable discount offers, and generates a receipt with subtotal, discounts, and final total.

---

## Components
### 1. **Item Class** 📦
```scala
case class Item(name: String, price: BigDecimal)
```
- Represents an item with a **name** and a **price**.
- `BigDecimal` ensures precise monetary calculations.

---

### 2. **ItemCatalog Object** 🏪
```scala
object ItemCatalog {
  val items: Map[String, Item] = Map(
    "Soup"   -> Item("Soup", BigDecimal("0.65")),
    "Bread"  -> Item("Bread", BigDecimal("0.80")),
    "Milk"   -> Item("Milk", BigDecimal("1.30")),
    "Apples" -> Item("Apples", BigDecimal("1.00"))
  )
}
```
- Provides a **catalog** of available items.
- Maps item names to their corresponding `Item` objects.

---

### 3. **Offer Trait** 🎯 (Interface for Discount Rules)
```scala
trait Offer {
  def applyOffer(basket: Map[String, Int]): Option[(String, BigDecimal)]
}
```
- Defines a contract for discount calculations.
- Implementing objects must override `applyOffer` method.

---

### 4. **ApplesOffer Object** 🍏 (10% Discount on Apples)
```scala
object ApplesOffer extends Offer {
  override def applyOffer(basket: Map[String, Int]): Option[(String, BigDecimal)] =
    basket.get("Apples").map { count =>
      val discount = ItemCatalog.items("Apples").price * BigDecimal("0.10") * count
      ("Apples 10% off", discount.setScale(2, RoundingMode.HALF_UP))
    }
}
```
- **If Apples are in the basket**, applies a **10% discount** per unit.
- Uses `setScale(2, RoundingMode.HALF_UP)` for currency precision.

---

### 5. **SoupBreadOffer Object** 🍲🥖 (Buy 2 Soups, Get Bread at Half Price)
```scala
object SoupBreadOffer extends Offer {
  override def applyOffer(basket: Map[String, Int]): Option[(String, BigDecimal)] =
    for {
      soupCount  <- basket.get("Soup") if soupCount >= 2
      breadCount <- basket.get("Bread")
      discountBreadCount = math.min(soupCount / 2, breadCount)
      if discountBreadCount > 0
    } yield {
      val discount = ItemCatalog.items("Bread").price / 2 * discountBreadCount
      ("Bread half price", discount.setScale(2, RoundingMode.HALF_UP))
    }
}
```
- **For every 2 Soups purchased, 1 Bread is half-price**.
- The discount applies to **the lesser of (soupCount / 2) or breadCount**.

---

### 6. **Receipt Case Class** 🧾
```scala
case class Receipt(subtotal: BigDecimal, discountDetails: Seq[(String, BigDecimal)], total: BigDecimal)
```
- Represents the **final receipt** with:
  - `subtotal`: Total before discounts.
  - `discountDetails`: A list of applied discounts.
  - `total`: The final payable amount after discounts.

---

### 7. **ShoppingBasket Class** 🛍️
```scala
class ShoppingBasket {
  private var basket: Map[String, Int] = Map().withDefaultValue(0)
```
- Manages the shopping basket and discount calculations.
- Uses a **mutable map** to track item quantities.

#### **Key Methods**

#### (1) **resolveItemName** 🔍
```scala
private def resolveItemName(input: String): Option[String] =
  ItemCatalog.items.keys.find(_.equalsIgnoreCase(input))
```
- Resolves case-insensitive item names.

#### (2) **addItem** ➕
```scala
def addItem(itemName: String): Unit = {
  resolveItemName(itemName) match {
    case Some(resolved) =>
      basket = basket.updated(resolved, basket(resolved) + 1)
    case None =>
      println(s"Item '$itemName' is not available.")
  }
}
```
- Adds an item to the basket **if it exists in the catalog**.
- Displays an error message if the item is not found.

---

## Future Upgrades 🚀

### **Potential Enhancements**
🔹 Implement a **persistent storage** for baskets (e.g., database integration).  
🔹 Expand the catalog to support **dynamic product addition**.  
🔹 Introduce **more configurable discount structures** based on business requirements.  

---

## Summary 📋
| Component        | Description |
|-----------------|-------------|
| `Item`          | Represents a product. |
| `ItemCatalog`   | Stores available products. |
| `Offer`         | Defines a discount rule. |
| `ApplesOffer`   | 10% off Apples. 🍏 |
| `SoupBreadOffer` | Buy 2 Soups, get Bread at half price. 🍲🥖 |
| `ShoppingBasket` | Manages items, calculates total, and prints receipt. 🛍️ |

This Scala shopping basket system is structured for **extensibility** and **precision**, making it easy to **add new offers** and **expand features**.

