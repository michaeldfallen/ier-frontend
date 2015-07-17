package uk.gov.gds.ier.transaction.overseas.confirmation

import org.joda.time.{YearMonth, Years}
import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.form.OverseasFormImplicits
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, Key, ErrorMessages}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.transaction.overseas.lastUkAddress.LastUkAddressForms
import uk.gov.gds.ier.transaction.overseas.dateLeftSpecial.DateLeftSpecialForms
import uk.gov.gds.ier.transaction.overseas.dateLeftUk.DateLeftUkForms
import uk.gov.gds.ier.transaction.overseas.dateOfBirth.DateOfBirthForms
import uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote.LastRegisteredToVoteForms
import uk.gov.gds.ier.transaction.overseas.nino.NinoForms
import uk.gov.gds.ier.transaction.overseas.name.NameForms
import uk.gov.gds.ier.transaction.overseas.parentName.ParentNameForms
import uk.gov.gds.ier.transaction.overseas.openRegister.OpenRegisterForms
import uk.gov.gds.ier.transaction.overseas.contact.ContactForms
import uk.gov.gds.ier.transaction.overseas.passport.PassportForms
import uk.gov.gds.ier.transaction.overseas.address.AddressForms
import uk.gov.gds.ier.transaction.overseas.waysToVote.WaysToVoteForms
import uk.gov.gds.ier.transaction.overseas.applicationFormVote.PostalOrProxyVoteForms
import uk.gov.gds.ier.transaction.overseas.parentsAddress.ParentsAddressForms
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait ConfirmationForms
  extends FormKeys
  with ErrorMessages
  with WithSerialiser
  with DateLeftSpecialForms
  with DateLeftUkForms
  with ParentNameForms
  with DateOfBirthForms
  with LastRegisteredToVoteForms
  with NinoForms
  with AddressForms
  with LastUkAddressForms
  with ParentsAddressForms
  with OpenRegisterForms
  with NameForms
  with PassportForms
  with WaysToVoteForms
  with PostalOrProxyVoteForms
  with ContactForms
  with OverseasFormImplicits
  with CommonConstraints {

  val optInMapping = single(
    keys.optIn.key -> boolean
  )

  val confirmationForm = ErrorTransformForm(
    mapping(
      keys.name.key -> optional(Name.mapping),
      keys.previousName.key -> optional(PreviousName.mapping),
      keys.dateLeftSpecial.key -> optional(dateLeftSpecialTypeMapping),
      keys.dateLeftUk.key -> optional(dateLeftUkMapping),
      keys.overseasParentName.key -> optional(overseasParentNameMapping),
      keys.lastRegisteredToVote.key -> optional(LastRegisteredToVote.mapping),
      keys.dob.key -> optional(dobMapping),
      keys.nino.key -> optional(ninoMapping),
      keys.lastUkAddress.key -> optional(partialAddressMapping),
      keys.parentsAddress.key -> optional(parentsPartialAddressMapping),
      keys.overseasAddress.key -> optional(addressMapping),
      keys.openRegister.key -> optional(optInMapping),
      keys.waysToVote.key -> optional(waysToVoteMapping),
      keys.postalOrProxyVote.key -> optional(postalOrProxyVoteMapping),
      keys.contact.key -> optional(contactMapping),
      keys.passport.key -> optional(passportMapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping),
      keys.sessionId.key -> optional(text)
    )
    (InprogressOverseas.apply)
    (InprogressOverseas.unapply)
    verifying (validateOverseas)
  )

  lazy val validateOverseas = Constraint[InprogressOverseas]("validateOverseas") { application =>
    import uk.gov.gds.ier.model.ApplicationType._
    application.identifyApplication match {
      case YoungVoter => validateYoungVoter(application)
      case NewVoter => validateNewVoter(application)
      case RenewerVoter => validateRenewerVoter(application)
      case SpecialVoter => validateSpecialVoter(application)
      case DontKnow => validateBaseSetRequired(application)
    }
  }

  lazy val validateBaseSetRequired = Constraint[InprogressOverseas]("validateBaseSet") {
    application => Invalid("Base set criteria not met", keys.name)
  }

  lazy val validateYoungVoter = Constraint[InprogressOverseas]("validateYoungVoter") { app =>
    val errorKeys = List(
      if (app.dob.isDefined) None else Some(keys.dob),
      if (app.lastRegisteredToVote.isDefined) None else Some(keys.lastRegisteredToVote),
      if (app.dateLeftUk.isDefined) None else Some(keys.dateLeftUk),
      if (app.overseasParentName.flatMap(_.previousName).isDefined) None
        else Some(keys.overseasParentName.parentPreviousName),
      if (app.overseasParentName.flatMap(_.name).isDefined) None
        else Some(keys.overseasParentName.parentName),
      if (app.parentsAddress.isDefined) None else Some(Key("parentsAddress")),
      if (app.passport.isDefined) None else Some(keys.passport),
      if (app.previousName.isDefined) None else Some(keys.previousName),
      if (app.name.isDefined) None else Some(keys.name),
      if (app.nino.isDefined) None else Some(keys.nino),
      if (app.address.isDefined) None else Some(keys.overseasAddress),
      if (app.openRegisterOptin.isDefined) None else Some(keys.openRegister),
      if (app.waysToVote.isDefined) None else Some(keys.waysToVote),
      if (validatePostalOrProxyVote(app.waysToVote, app.postalOrProxyVote))
        None
      else Some(keys.postalOrProxyVote),
      if (app.contact.isDefined) None else Some(keys.contact)
    ).flatten
    if (errorKeys.size == 0) {
      Valid
    } else {
      Invalid ("Please complete this step", errorKeys:_*)
    }
  }

  lazy val validateSpecialVoter = Constraint[InprogressOverseas]("validateSpecialVoter") { app =>
    val errorKeys = List(
      if (app.dob.isDefined) None else Some(keys.dob),
      if (app.lastRegisteredToVote.isDefined) None else Some(keys.lastRegisteredToVote),
      if (app.dateLeftSpecial.isDefined) None else Some(keys.dateLeftSpecial),
      if (app.lastUkAddress.isDefined) None else Some(keys.lastUkAddress),
      if (app.passport.isDefined) None else Some(keys.passport),
      if (app.previousName.isDefined) None else Some(keys.previousName),
      if (app.name.isDefined) None else Some(keys.name),
      if (app.nino.isDefined) None else Some(keys.nino),
      if (app.address.isDefined) None else Some(keys.overseasAddress),
      if (app.openRegisterOptin.isDefined) None else Some(keys.openRegister),
      if (app.waysToVote.isDefined) None else Some(keys.waysToVote),
      if (validatePostalOrProxyVote(app.waysToVote, app.postalOrProxyVote))
        None
      else Some(keys.postalOrProxyVote),
      if (app.contact.isDefined) None else Some(keys.contact)
    ).flatten
    if (errorKeys.size == 0) {
      Valid
    } else {
      Invalid ("Please complete this step", errorKeys:_*)
    }
  }

  lazy val validateNewVoter = Constraint[InprogressOverseas]("validateNewVoter") { app =>
    val errorKeys = List(
      if (app.dob.isDefined) None else Some(keys.dob),
      if (app.dateLeftUk.isDefined) None else Some(keys.dateLeftUk),
      if (app.lastUkAddress.isDefined) None else Some(keys.lastUkAddress),
      if (app.passport.isDefined) None else Some(keys.passport),
      if (app.previousName.isDefined) None else Some(keys.previousName),
      if (app.name.isDefined) None else Some(keys.name),
      if (app.nino.isDefined) None else Some(keys.nino),
      if (app.address.isDefined) None else Some(keys.overseasAddress),
      if (app.openRegisterOptin.isDefined) None else Some(keys.openRegister),
      if (app.waysToVote.isDefined) None else Some(keys.waysToVote),
      if (validatePostalOrProxyVote(app.waysToVote, app.postalOrProxyVote))
        None
      else Some(keys.postalOrProxyVote),
      if (app.contact.isDefined) None else Some(keys.contact)
    ).flatten
    if (errorKeys.size == 0) {
      Valid
    } else {
      Invalid ("Please complete this step", errorKeys:_*)
    }
  }

  lazy val validateRenewerVoter = Constraint[InprogressOverseas]("validateRenewerVoter") { app =>
    val validationErrors = Seq (
      if (app.dob.isDefined) None else Some(keys.dob),
      if (app.dateLeftUk.isDefined) None else Some(keys.dateLeftUk),
      if (app.lastUkAddress.isDefined) None else Some(keys.lastUkAddress),
      if (app.previousName.isDefined) None else Some(keys.previousName),
      if (app.name.isDefined) None else Some(keys.name),
      if (app.nino.isDefined) None else Some(keys.nino),
      if (app.address.isDefined) None else Some(keys.overseasAddress),
      if (app.openRegisterOptin.isDefined) None else Some(keys.openRegister),
      if (app.waysToVote.isDefined) None else Some(keys.waysToVote),
      if (validatePostalOrProxyVote(app.waysToVote, app.postalOrProxyVote))
        None
      else Some(keys.postalOrProxyVote),
      if (app.contact.isDefined) None else Some(keys.contact)
    ).flatten

    if (validationErrors.size == 0)
      Valid
    else
      Invalid ("Please complete this step", validationErrors:_*)
  }


  private def validatePostalOrProxyVote (
      waysToVote: Option[WaysToVote],
      postalOrProxyVote: Option[PostalOrProxyVote]): Boolean = {
    waysToVote match {
      case None  => true
      case Some(WaysToVote(WaysToVoteType.InPerson)) if (!postalOrProxyVote.isDefined)  => true
      case Some(WaysToVote(WaysToVoteType.ByPost)) if (postalOrProxyVote.isDefined)  => true
      case Some(WaysToVote(WaysToVoteType.ByProxy)) if (postalOrProxyVote.isDefined) => true
      case _ => false
    }
  }
}
