package uk.gov.gds.ier.form

import uk.gov.gds.ier.validation.FormKeys
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.validation.constants.NationalityConstants

trait OrdinaryFormImplicits {
  self: FormKeys =>

  implicit class OrdinaryImprovedForm(form: ErrorTransformForm[InprogressOrdinary]) {
    def obtainOtherCountriesList: List[String] = {
      val otherCountries = (
        for (i <- 0 until NationalityConstants.numberMaxOfOtherCountries
             if (form(otherCountriesKey(i)).value.isDefined)
               && !form(otherCountriesKey(i)).value.get.isEmpty)
        yield form(otherCountriesKey(i)).value.get
      )
      otherCountries.toList
    }

    private def otherCountriesKey(i:Int) = keys.nationality.otherCountries.item(i)
  }
}