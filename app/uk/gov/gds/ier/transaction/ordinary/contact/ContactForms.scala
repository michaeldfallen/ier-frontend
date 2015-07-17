package uk.gov.gds.ier.transaction.ordinary.contact

import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.model._
import play.api.data.Forms._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

import uk.gov.gds.ier.validation.{EmailValidator, FormKeys, ErrorMessages}
import uk.gov.gds.ier.model.{Contact, ContactDetail}
import play.api.data.validation.{Invalid, Valid, Constraint}

trait ContactForms {
  self:  FormKeys
    with ErrorMessages =>

  val contactForm = ErrorTransformForm(
    mapping(
      keys.contact.key -> optional(Contact.mapping),
      keys.postalVote.key -> optional(PostalVote.mapping)
    ) (
      (contact, postalVote) => InprogressOrdinary(
        postalVote = postalVote,
        contact = contact
      )
    ) (
      inprogress => Some(
        inprogress.contact,
        inprogress.postalVote
      )
    ).verifying(
      atLeastOneOptionSelectedOrdinary,
      numberProvidedIfPhoneSelected,
      emailProvidedIfEmailSelected,
      emailIsValidIfProvided
    )
  )


  lazy val emailProvidedIfEmailSelected = Constraint[InprogressOrdinary](
    keys.contact.email.key
  ) { application =>
    application.contact.flatMap(_.email) match {
      case Some(ContactDetail(true, None)) => Invalid(
        "ordinary_contact_error_enterYourEmail",
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
        "ordinary_contact_error_enterYourPhoneNo",
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
        Invalid("ordinary_contact_error_pleaseEnterValidEmail", keys.contact.email.detail)
      }
      case _ => Valid
    }
  }

  lazy val atLeastOneOptionSelectedOrdinary = Constraint[InprogressOrdinary](keys.contact.key) {
    application =>
      atLeastOneContactOptionSelected (application.contact)
  }

  def atLeastOneContactOptionSelected (contact: Option[Contact]) = {
    contact match {
      case Some(Contact(postOption,Some(ContactDetail(phoneOption,_)),Some(ContactDetail(emailOption,_))))
        if(!postOption && !phoneOption && !emailOption) =>
          Invalid("ordinary_contact_error_pleaseAnswer", keys.contact)

      case Some(Contact(postOption,Some(ContactDetail(phoneOption,_)),None))
        if (!postOption && !phoneOption) =>
          Invalid("ordinary_contact_error_pleaseAnswer", keys.contact)

      case Some(Contact(postOption,None,Some(ContactDetail(emailOption,_))))
        if (!postOption && !emailOption) =>
          Invalid("ordinary_contact_error_pleaseAnswer", keys.contact)

      case None => Invalid("ordinary_contact_error_pleaseAnswer", keys.contact)
      case _ => Valid
    }
  }



}

