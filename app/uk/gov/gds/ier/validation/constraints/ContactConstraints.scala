package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{EmailValidator, Key, FormKeys, ErrorMessages}
import uk.gov.gds.ier.model.{Contact, ContactDetail}
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait ContactConstraints extends CommonConstraints {
  self:  FormKeys
  with ErrorMessages =>

  def detailFilled(key:Key, name:String) = {
    predicateHolds[ContactDetail](key, s"Please enter your $name") {
      t => t.detail.isDefined || !t.contactMe
    }
  }

  lazy val emailProvidedIfEmailSelected = Constraint[InprogressOrdinary](
    keys.contact.email.key
  ) { application =>
    application.contact.flatMap(_.email) match {
      case Some(ContactDetail(true, None)) => Invalid(
        "Please enter your email address",
        keys.contact.email.detail
      )
      case _ => Valid
    }
  }

  lazy val numberProvidedIfPhoneSelected = Constraint[InprogressOrdinary](
    keys.contact.phone.key
  ) { application =>
    application.contact.flatMap(_.phone) match {
      case Some(ContactDetail(true, None)) => Invalid(
        "Please enter your phone number",
        keys.contact.phone.detail
      )
      case _ => Valid
    }
  }

  lazy val emailIsValidIfProvided = Constraint[InprogressOrdinary](
    keys.contact.key
  ) { application =>
    application.contact.flatMap(_.email) match {
      case Some(ContactDetail(true, Some(emailAddress)))
        if !EmailValidator.isValid(emailAddress) => {
        Invalid("Please enter a valid email address", keys.contact.email.detail)
      }
      case _ => Valid
    }
  }

  lazy val emailIsValid = Constraint[Contact](keys.contact.key) {
    contact =>
      contact.email match {
        case Some(ContactDetail(true, Some(emailAddress))) => {
          if (EmailValidator.isValid(emailAddress)) Valid
          else Invalid("Please enter a valid email address", keys.contact.email.detail)
        }
        case _ => Valid
      }
  }

  lazy val atLeastOneOptionSelectedOverseas = Constraint[InprogressOverseas](keys.contact.key) {
    application =>
      atLeastOneContactOptionSelected (application.contact)
  }

  lazy val atLeastOneOptionSelectedOrdinary = Constraint[InprogressOrdinary](keys.contact.key) {
    application =>
      atLeastOneContactOptionSelected (application.contact)
  }

  def atLeastOneContactOptionSelected (contact: Option[Contact]) = {
    contact match {
      case Some(Contact(postOption,Some(ContactDetail(phoneOption,_)),Some(ContactDetail(emailOption,_)))) =>
        if (!postOption && !phoneOption && !emailOption)
          Invalid("Please answer this question", keys.contact)
        else Valid

      case Some(Contact(postOption,Some(ContactDetail(phoneOption,_)),None)) =>
        if (!postOption && !phoneOption)
          Invalid("Please answer this question", keys.contact)
        else Valid

      case Some(Contact(postOption,None,Some(ContactDetail(emailOption,_)))) =>
        if (!postOption && !emailOption)
          Invalid("Please answer this question", keys.contact)
        else Valid

      case None => Invalid("Please answer this question", keys.contact)
      case _ => Valid
    }
  }
}
