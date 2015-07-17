package uk.gov.gds.ier.transaction.ordinary.nationality

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.transaction.ordinary.confirmation.ConfirmationMustache
import uk.gov.gds.ier.form.OrdinaryFormImplicits

trait NationalityMustache extends StepTemplate[InprogressOrdinary]
  with OrdinaryFormImplicits {

  case class NationalityModel(
      question:Question,
      nationality: FieldSet,
      britishOption: Field,
      irishOption: Field,
      hasOtherCountryOption: Field,
      otherCountry: FieldSet,
      otherCountries0: Field,
      otherCountries1: Field,
      otherCountries2: Field,
      noNationalityReason: Field,
      noNationalityReasonShowFlag: String
  ) extends MustacheData

  val mustache = MultilingualTemplate("ordinary/nationality") { implicit lang =>
    (form, postEndpoint) =>

    implicit val progressForm = form

    val nationalityReason = form(keys.nationality.noNationalityReason).value

    val nationalityReasonClass = nationalityReason match {
      case Some("") | None => ""
      case _ => "-open"
    }

    val hasOtherCountryOption = CheckboxField(
      key = keys.nationality.hasOtherCountry,
      value = "true"
    )

    NationalityModel(
      question = Question(
        postUrl = postEndpoint.url,
        errorMessages = Messages.translatedGlobalErrors(form),
        number = "2 " + Messages("step_of") + " 11",
        title = Messages("ordinary_nationality_title")
      ),
      nationality = FieldSet(keys.nationality),
      britishOption = CheckboxField(
        key = keys.nationality.british,
        value = "true"
      ),
      irishOption = CheckboxField(
        key = keys.nationality.irish,
        value = "true"
      ),
      hasOtherCountryOption = hasOtherCountryOption.copy(
        attributes = hasOtherCountryOption.attributes.replaceAll("\"", "'")
      ),
      otherCountry = FieldSet(keys.nationality.otherCountries),
      otherCountries0 = TextField(keys.nationality.otherCountries.item(0)),
      otherCountries1 = TextField(keys.nationality.otherCountries.item(1)),
      otherCountries2 = TextField(keys.nationality.otherCountries.item(2)),
      noNationalityReason = TextField(keys.nationality.noNationalityReason),
      noNationalityReasonShowFlag = nationalityReasonClass
    )
  }
}
