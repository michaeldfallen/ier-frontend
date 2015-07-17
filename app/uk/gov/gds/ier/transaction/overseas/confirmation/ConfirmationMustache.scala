package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.mustache.StepMustache

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.overseas.confirmation.blocks.{ConfirmationQuestion, ConfirmationBlocks}
import uk.gov.gds.ier.transaction.overseas.{InprogressOverseas, WithOverseasControllers}
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.step.StepTemplate

trait ConfirmationMustache extends StepTemplate[InprogressOverseas] {
  self: WithRemoteAssets
    with WithOverseasControllers =>

  case class ErrorModel(
      startUrl: String
  )

  case class ConfirmationModel(
      question: Question,
      applicantDetails: List[ConfirmationQuestion],
      parentDetails: List[ConfirmationQuestion],
      displayParentBlock: Boolean
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/confirmation") { (form, postUrl) =>

    val confirmation = new ConfirmationBlocks(form, overseas)
    val parentData = confirmation.parentBlocks()
    val applicantData = confirmation.applicantBlocks()

    ConfirmationModel(
      question = Question(
        postUrl = postUrl.url,
        title = "Confirm your details - Register to vote",
        contentClasses = "confirmation"
      ),
      parentDetails = parentData,
      applicantDetails = applicantData,
      displayParentBlock = !parentData.isEmpty
    )
  }
}
