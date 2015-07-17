package uk.gov.gds.ier.transaction.crown.declaration

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.service.{DeclarationPdfDownloadService, WithDeclarationPdfDownloadService}
import uk.gov.gds.ier.model.{HasAddressOption, LastAddress, PartialAddress}

class DeclarationPdfMustacheTest
  extends MustacheTestSuite
  with DeclarationPdfForms
  with DeclarationPdfMustache
  with WithDeclarationPdfDownloadService
  with MockitoHelpers {

  val declarationPdfDownloadService = mock[DeclarationPdfDownloadService]

  it should "construct model for declaration step with election authority details from mocked service" in {
    val emptyApplication = InprogressCrown()
    val model: DeclarationPdfModel = mustache.data(
      declarationPdfForm.fill(inprogressApplicationWithPostcode("WR26NJ")),
      Call("POST", "/register-to-vote/crown/declaration-pdf"),
      emptyApplication
    ).asInstanceOf[DeclarationPdfModel]

    model.question.title should be("Download your service declaration form")
    model.question.postUrl should be("/register-to-vote/crown/declaration-pdf")
  }

  private def inprogressApplicationWithPostcode(postcode: String) = {
    InprogressCrown().copy(
        address = Some(LastAddress(
          hasAddress = Some(HasAddressOption.YesAndLivingThere),
          address = Some(PartialAddress(
            addressLine = None,
            uprn = None,
            manualAddress = None,
            postcode = postcode)))))
  }
}
