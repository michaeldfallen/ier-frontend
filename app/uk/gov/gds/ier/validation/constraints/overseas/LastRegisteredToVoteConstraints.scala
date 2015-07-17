package uk.gov.gds.ier.validation.constraints.overseas

import uk.gov.gds.ier.validation._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait LastRegisteredToVoteConstraints {
  self: ErrorMessages
  with FormKeys =>

  lazy val lastRegisteredToVoteRequired = Constraint[InprogressOverseas](keys.lastRegisteredToVote.key) {
    application => application.lastRegisteredToVote match {
      case Some(LastRegisteredToVote(regType)) => Valid
      case None => Invalid(
        "Please answer this question",
        keys.lastRegisteredToVote.registeredType,
        keys.lastRegisteredToVote
      )
    }
  }

  lazy val registeredTypeIsValid = Constraint[String](keys.lastRegisteredToVote.key) { str =>
    if (LastRegisteredType.isValid(str)) {
      Valid
    } else {
      Invalid(
        s"$str is not a valid registration type",
        keys.lastRegisteredToVote.registeredType,
        keys.lastRegisteredToVote
      )
    }
  }
}
