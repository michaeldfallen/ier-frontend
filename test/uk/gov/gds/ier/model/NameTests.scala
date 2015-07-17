package uk.gov.gds.ier.model

import uk.gov.gds.ier.test.UnitTestSuite
import uk.gov.gds.ier.validation.constants.NameConstants

class NameTests extends UnitTestSuite {

  it should "generate the expected payload with first,last and middle names" in {
    val name = Name(
      firstName = "John",
      middleNames = Some("James"),
      lastName = "Smith"
    )
    val apiNameMap = name.toApiMap("fn", "mn", "ln")
    val expected = Map(
      "fn" -> "John",
      "mn" -> "James",
      "ln" -> "Smith"
    )
    apiNameMap should matchMap(expected)
  }

  it should "generate the expected payload with empty middle names" in {
    val name = Name(
      firstName = "Antonio",
      middleNames = None,
      lastName = "Perez"
    )
    val apiNameMap = name.toApiMap("fn", "mn", "ln")
    val expected = Map(
      "fn" -> "Antonio",
      "ln" -> "Perez"
    )
    apiNameMap should matchMap(expected)
  }

  it should "generate the expected payload with max lenght first,last and middle names" in {
    val name = Name(
      firstName = "A" * NameConstants.firstNameMaxLength,
      middleNames = Some("B" * NameConstants.middleNamesMaxLength),
      lastName = "C" * NameConstants.lastNameMaxLength
    )
    val apiNameMap = name.toApiMap("fn", "mn", "ln")
    val expected = Map(
      "fn" -> "A" * NameConstants.firstNameMaxLength,
      "mn" -> "B" * NameConstants.middleNamesMaxLength,
      "ln" -> "C" * NameConstants.lastNameMaxLength
    )
    apiNameMap should matchMap(expected)
  }

  it should "generate the expected truncated payload with a long first,last and middle names" in {
    val name = Name(
      firstName = "A" * (NameConstants.firstNameMaxLength +1),
      middleNames = Some("B" * (NameConstants.middleNamesMaxLength + 1)),
      lastName = "C" * (NameConstants.lastNameMaxLength + 1)
    )
    val apiNameMap = name.toApiMap("fn", "mn", "ln")
    val expected = Map(
      "fn" -> "A" * NameConstants.firstNameMaxLength,
      "mn" -> "B" * NameConstants.middleNamesMaxLength,
      "ln" -> "C" * NameConstants.lastNameMaxLength
    )
    apiNameMap should matchMap(expected)
  }


}
