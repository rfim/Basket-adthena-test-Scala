import scala.math.BigDecimal.RoundingMode
import org.slf4j.LoggerFactory

// Represents an item with its price.
case class Item(name: String, price: BigDecimal)

// Catalog of available items.
object ItemCatalog {
  val items = Map(
    "Soup"   -> Item("Soup", BigDecimal("0.65")),
    "Bread"  -> Item("Bread", BigDecimal("0.80")),
    "Milk"   -> Item("Milk", BigDecimal("1.30")),
    "Apples" -> Item("Apples", BigDecimal("1.00"))
  )
}

// Interface for discount offers.
trait Offer {
  def applyOffer(basket: Map[String, Int]): Option[(String, BigDecimal)]
}

// 10% off on Apples.
object ApplesOffer extends Offer {
  override def applyOffer(basket: Map[String, Int]): Option[(String, BigDecimal)] =
    basket.get("Apples").filter(_ > 0).map { count =>
      val discount = ItemCatalog.items("Apples").price * 0.10 * count
      ("Apples 10% off", discount.setScale(2, RoundingMode.HALF_UP))
    }
}

// Buy 2 Soups, get Bread at half price.
object SoupBreadOffer extends Offer {
  override def applyOffer(basket: Map[String, Int]): Option[(String, BigDecimal)] =
    for {
      soups   <- basket.get("Soup") if soups >= 2
      breads  <- basket.get("Bread") if breads > 0
      discNum = math.min(soups / 2, breads) if discNum > 0
    } yield {
      val discount = ItemCatalog.items("Bread").price / 2 * discNum
      ("Bread half price", discount.setScale(2, RoundingMode.HALF_UP))
    }
}

// Simple receipt model.
case class Receipt(subtotal: BigDecimal, discounts: Seq[(String, BigDecimal)], total: BigDecimal)

// Shopping basket that tracks items and calculates totals.
class ShoppingBasket(private var offers: Seq[Offer] = Seq(ApplesOffer, SoupBreadOffer)) {
  private val logger = LoggerFactory.getLogger(getClass)
  private var basket = Map[String, Int]().withDefaultValue(0)

  // Case-insensitive item lookup.
  private def resolveItemName(input: String): Option[String] =
    ItemCatalog.items.keys.find(_.equalsIgnoreCase(input))

  // Add an item to the basket.
  def addItem(itemName: String): Unit = {
    resolveItemName(itemName) match {
      case Some(name) =>
        basket = basket.updated(name, basket(name) + 1)
        logger.debug(s"Added $name (count: ${basket(name)})")
      case None =>
        logger.warn(s"Item '$itemName' not available.")
    }
  }

  // Calculate subtotal using a view for efficiency.
  private def subtotal: BigDecimal =
    basket.view.foldLeft(BigDecimal(0)) { case (sum, (name, count)) =>
      sum + ItemCatalog.items(name).price * count
    }.setScale(2, RoundingMode.HALF_UP)

  // Compute discounts in parallel.
  private def discounts: Seq[(String, BigDecimal)] =
    offers.par.flatMap(_.applyOffer(basket)).toList

  // Compute total after discounts.
  private def total: BigDecimal =
    (subtotal - discounts.map(_._2).sum).setScale(2, RoundingMode.HALF_UP)

  // Build the receipt.
  def calculateReceipt: Receipt = Receipt(subtotal, discounts, total)

  // Print the receipt and log details.
  def printReceipt(): Unit = {
    val receipt = calculateReceipt

    logger.info(f"Subtotal: £${receipt.subtotal}%.2f")
    if (receipt.discounts.isEmpty)
      logger.info("(No offers)")
    else
      receipt.discounts.foreach { case (desc, disc) =>
        logger.info(f"$desc: £${disc}%.2f")
      }
    logger.info(f"Total: £${receipt.total}%.2f")

    println(f"Subtotal: £${receipt.subtotal}%.2f")
    if (receipt.discounts.isEmpty)
      println("(No offers)")
    else
      receipt.discounts.foreach { case (desc, disc) =>
        println(f"$desc: £${disc}%.2f")
      }
    println(f"Total: £${receipt.total}%.2f")
  }

  // Update the discount offers.
  def updateOffers(newOffers: Seq[Offer]): Unit = {
    offers = newOffers
    logger.info("Offers updated.")
  }
}
