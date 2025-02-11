import scala.math.BigDecimal.RoundingMode

// Represents an item.
case class Item(name: String, price: BigDecimal)

// Catalog of available items.
object ItemCatalog {
  val items: Map[String, Item] = Map(
    "Soup"   -> Item("Soup", BigDecimal("0.65")),
    "Bread"  -> Item("Bread", BigDecimal("0.80")),
    "Milk"   -> Item("Milk", BigDecimal("1.30")),
    "Apples" -> Item("Apples", BigDecimal("1.00"))
  )
}

// Discount offer interface.
trait Offer {
  def applyOffer(basket: Map[String, Int]): Option[(String, BigDecimal)]
}

// 10% off on Apples.
object ApplesOffer extends Offer {
  override def applyOffer(basket: Map[String, Int]): Option[(String, BigDecimal)] =
    basket.get("Apples").map { count =>
      val discount = ItemCatalog.items("Apples").price * BigDecimal("0.10") * count
      ("Apples 10% off", discount.setScale(2, RoundingMode.HALF_UP))
    }
}

// Buy 2 Soups, get Bread at half price.
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

// Receipt data model.
case class Receipt(subtotal: BigDecimal, discountDetails: Seq[(String, BigDecimal)], total: BigDecimal)

// Shopping basket that manages items and calculates totals.
class ShoppingBasket {
  // Map of resolved item name -> quantity.
  private var basket: Map[String, Int] = Map().withDefaultValue(0)

  /**
    * Resolves an input string to a valid catalog item name using a case-insensitive exact match.
    * If no match is found, returns None.
    */
  private def resolveItemName(input: String): Option[String] =
    ItemCatalog.items.keys.find(_.equalsIgnoreCase(input))

  /** Adds an item (if resolved) to the basket. */
  def addItem(itemName: String): Unit = {
    resolveItemName(itemName) match {
      case Some(resolved) =>
        basket = basket.updated(resolved, basket(resolved) + 1)
      case None =>
        println(s"Item '$itemName' is not available.")
    }
  }

  // Calculates the subtotal.
  private def subtotal: BigDecimal =
    basket.foldLeft(BigDecimal(0)) { case (sum, (name, count)) =>
      sum + (ItemCatalog.items(name).price * count)
    }.setScale(2, RoundingMode.HALF_UP)

  // Computes all applicable discounts.
  private def discounts: Seq[(String, BigDecimal)] = {
    val offers: Seq[Offer] = Seq(ApplesOffer, SoupBreadOffer)
    offers.flatMap(_.applyOffer(basket))
  }

  // Computes the final total after discounts.
  private def total: BigDecimal = {
    val totalDiscount = discounts.map(_._2).sum
    (subtotal - totalDiscount).setScale(2, RoundingMode.HALF_UP)
  }

  /** Exposes the calculated receipt. */
  def calculateReceipt: Receipt =
    Receipt(subtotal, discounts, total)

  /** Prints the receipt to the console. */
  def printReceipt(): Unit = {
    val receipt = calculateReceipt
    println(f"Subtotal: £${receipt.subtotal}%.2f")
    if (receipt.discountDetails.isEmpty)
      println("(No offers available)")
    else
      receipt.discountDetails.foreach { case (desc, disc) =>
        println(f"$desc: £${disc}%.2f")
      }
    println(f"Total price: £${receipt.total}%.2f")
  }
}
