package uk.gov.gds.ier.validation

import uk.gov.gds.ier.transaction.overseas.dateLeftUk.DateLeftUkForms
import uk.gov.gds.ier.transaction.overseas.dateOfBirth.DateOfBirthForms
import uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote.LastRegisteredToVoteForms
import uk.gov.gds.ier.transaction.overseas.nino.NinoForms
import uk.gov.gds.ier.transaction.overseas.contact.ContactForms
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.transaction.overseas.openRegister.OpenRegisterForms
import uk.gov.gds.ier.transaction.overseas.name.NameForms
import play.api.data.Forms._
import uk.gov.gds.ier.transaction.overseas.applicationFormVote.PostalOrProxyVoteForms


trait OverseasForms
  extends FormKeys
  with WithSerialiser
  with ErrorMessages
  with NameForms
  with DateLeftUkForms
  with DateOfBirthForms
  with LastRegisteredToVoteForms
  with NinoForms
  with OpenRegisterForms
  with PostalOrProxyVoteForms
  with ContactForms {

  val optInMapping = single(
    keys.optIn.key -> boolean
  )
}
