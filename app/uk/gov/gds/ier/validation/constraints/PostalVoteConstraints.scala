package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation._
import play.api.data.validation.{Valid, Invalid, Constraint}
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.PostalVote
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import scala.Some
import uk.gov.gds.ier.model.PostalVoteDeliveryMethod

trait PostalVoteConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val validDeliveryMethod = Constraint[PostalVoteDeliveryMethod](keys.deliveryMethod.key) {
    postaVoteDeliveryMethod =>
      if (postaVoteDeliveryMethod.deliveryMethod.isDefined)
        if (postaVoteDeliveryMethod.deliveryMethod == Some("email"))
          postaVoteDeliveryMethod.emailAddress.map(emailAddress =>
            if (EmailValidator.isValid(emailAddress)) Valid
            else Invalid("Please enter a valid email address", keys.postalVote.deliveryMethod.emailAddress)
          ).getOrElse(
            Invalid("Please enter your email address", keys.postalVote.deliveryMethod.emailAddress)
          )
        else Valid
      else Invalid("Please answer this question", keys.postalVote.deliveryMethod.methodName)
  }

  lazy val validPostVoteOption = Constraint[InprogressOrdinary](
      keys.postalVote.deliveryMethod.key
  ) { application =>
    application.postalVote match {
      case Some(PostalVote(Some(PostalVoteOption.Yes), None)) => Invalid(
        "Please answer this question",
        keys.postalVote.deliveryMethod.methodName
      )
      case _ => Valid
    }
  }

  lazy val validEmailAddressIfProvided = Constraint[InprogressOrdinary](
      keys.postalVote.deliveryMethod.emailAddress.key
  ) { application =>
    val deliveryMethod = application.postalVote.flatMap(_.deliveryMethod)
    val emailAddress = deliveryMethod.flatMap(_.emailAddress)
    emailAddress match {
      case Some(email) if !EmailValidator.isValid(emailAddress) => Invalid(
        "Please enter a valid email address",
        keys.postalVote.deliveryMethod.emailAddress
      )
      case _ => Valid
    }
  }

  lazy val questionIsAnswered = Constraint[InprogressOrdinary](
    keys.postalVote.optIn.key
  ) { application =>
    application.postalVote.flatMap(_.postalVoteOption) match {
      case Some(_) => Valid
      case None => Invalid(
        "Please answer this question",
        keys.postalVote.optIn
      )
    }
  }

  lazy val emailProvidedIfEmailAnswered = Constraint[InprogressOrdinary](
    keys.postalVote.deliveryMethod.emailAddress.key
  ) { application =>
    val deliveryMethod = application.postalVote.flatMap(_.deliveryMethod)
    val methodName = deliveryMethod.flatMap(_.deliveryMethod)
    val emailAddress = deliveryMethod.flatMap(_.emailAddress)
      
    (methodName, emailAddress) match {
      case (Some("email"), None) => Invalid(
        "Please enter your email address",
        keys.postalVote.deliveryMethod.emailAddress
      )
      case _ => Valid
    }
  }
}
