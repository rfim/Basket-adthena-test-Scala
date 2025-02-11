object Main extends App {
  // Create a new shopping basket.
  val basket = new ShoppingBasket()

  // Add some items. You can modify these lines to test different inputs.
  basket.addItem("Soup")
  basket.addItem("Soup")
  basket.addItem("Bread")
  basket.addItem("Milk")
  basket.addItem("Apples")
  basket.addItem("apples") // Testing case-insensitivity.

  // Print the receipt.
  basket.printReceipt()
}
