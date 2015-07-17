package uk.gov.gds.ier.transaction.crown.declaration

import uk.gov.gds.ier.test.FormTestSuite

class DeclarationPdfFormTests
  extends FormTestSuite
  with DeclarationPdfForms {

  it should "should error out when in session is no postcode" in {
    val emptyUserInput = JsNull
    declarationPdfForm.bind(emptyUserInput).fold(
      failedForm => {
        failedForm.errorsAsTextAll should be("" +
          " -> error.required\n" +
          "address.address.postcode -> error.required") // no fancy message here, it will not be displayed anyway
      },
      success => fail("Should have errored out")
    )
  }

  it should "proceed with just address postcode on input" in {
    declarationPdfForm.bind(Map(
      "address.address.postcode" -> "WR26NJ"
    )).fold(
      hasErrors => fail("Should not have errored out"),
      success => success // do nothing
    )
  }
}

