package uk.gov.gds.ier.validation.constraints.overseas

import uk.gov.gds.ier.validation._
import play.api.data.validation.{Valid, Invalid, Constraint}
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.PostalVote
import scala.Some
import uk.gov.gds.ier.model.PostalVoteDeliveryMethod
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait PostalOrProxyVoteConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val validDeliveryMethod = Constraint[PostalVoteDeliveryMethod](keys.deliveryMethod.key) {
    postaVoteDeliveryMethod =>
      if (postaVoteDeliveryMethod.deliveryMethod.isDefined)
        if (postaVoteDeliveryMethod.deliveryMethod == Some("email"))
          postaVoteDeliveryMethod.emailAddress.map(emailAddress =>
            if (EmailValidator.isValid(emailAddress)) Valid
            else Invalid("Please enter a valid email address", keys.postalOrProxyVote.deliveryMethod.emailAddress)
          ).getOrElse(
            Invalid("Please enter your email address", keys.postalOrProxyVote.deliveryMethod.emailAddress)
          )
        else Valid
      else Invalid("Please answer this question", keys.postalOrProxyVote.deliveryMethod.methodName)
  }

  lazy val validVoteOption = Constraint[PostalOrProxyVote](keys.postalOrProxyVote.deliveryMethod.key) {
    postalVote => {
      if (postalVote.postalVoteOption == Some(true) &&
        !postalVote.deliveryMethod.isDefined)
        Invalid("Please answer this question", keys.postalOrProxyVote.deliveryMethod.methodName)
      else 
        Valid
    }
  }
  
  lazy val questionIsRequired = Constraint[InprogressOverseas](keys.postalOrProxyVote.key) {
    application => application.postalOrProxyVote match {
      case Some(p) => Valid
      case None => Invalid("Please answer this question", keys.postalOrProxyVote.optIn)
    }
  }

}
