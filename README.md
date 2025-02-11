# Basket Adthena Scala

## Overview

This repository contains a Scala-based application for processing a shopping basket, calculating discounts, and determining the total cost of items. The project follows best practices in functional programming and test-driven development (TDD).

## Features

- Parses shopping basket items from command-line input
- Applies predefined discounts and pricing rules
- Outputs a detailed receipt with itemized costs and discounts applied
- Supports running via SBT or Docker
- Implements special offers such as:
  - 10% discount on apples
  - Buy 2 tins of soup, get a loaf of bread for half price

## Prerequisites

- **Java**: JDK 8 or later
- **Scala**: Version 2.12 or 2.13
- **SBT (Scala Build Tool)**
- **Docker** (optional, for running in a container)

## Installation

Clone the repository:

```bash
 git clone https://github.com/rfim/Basket-adthena-test-Scala.git
 cd Basket-adthena-test-Scala
```

## Running the Application

### Using SBT

Run the application with:

```bash
sbt run -- Apples Milk Bread
```

### Using Docker

Build and run the application in a Docker container:

```bash
docker build -t basket-adthena .
docker run --rm basket-adthena Apples Milk Bread
```

Alternatively, use Docker Compose:

```bash
docker-compose up --build
```

To stop the container:

```bash
docker-compose down
```

## Pricing and Special Offers

The program calculates the total cost based on the following pricing rules:

- **Standard Prices:**
  - Soup – 65p per tin
  - Bread – 80p per loaf
  - Milk – £1.30 per bottle
  - Apples – £1.00 per bag

- **Current Special Offers:**
  - Apples have a 10% discount this week.
  - Buy 2 tins of soup and get a loaf of bread for half price.

## Example Usage

```bash
sbt run -- Apples Milk Bread
```

**Example Output:**

```bash
Subtotal: £3.10 
Apples 10% off: 10p 
Total price: £3.00
```

If no special offers apply:

```bash
Subtotal: £1.30
(No offers available) 
Total price: £1.30
```

## Running Tests

Execute unit tests using SBT:

```bash
sbt test
```

Run tests in a Docker container:

```bash
docker run --rm basket-adthena sbt test
```

## Contributing

Feel free to submit pull requests or report issues.

## License

This project is licensed under the MIT License.

