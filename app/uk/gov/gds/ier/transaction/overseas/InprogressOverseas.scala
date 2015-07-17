package uk.gov.gds.ier.transaction.overseas

import uk.gov.gds.ier.step.InprogressApplication
import uk.gov.gds.ier.model._

case class InprogressOverseas(
    name: Option[Name] = None,
    previousName: Option[PreviousName] = None,
    dateLeftSpecial: Option[DateLeftSpecial] = None,
    dateLeftUk: Option[DateLeft] = None,
    overseasParentName: Option[OverseasParentName] = None,
    lastRegisteredToVote: Option[LastRegisteredToVote] = None,
    dob: Option[DOB] = None,
    nino: Option[Nino] = None,
    lastUkAddress: Option[PartialAddress] = None,
    parentsAddress: Option[PartialAddress] = None,
    address: Option[OverseasAddress] = None,
    openRegisterOptin: Option[Boolean] = None,
    waysToVote: Option[WaysToVote] = None,
    postalOrProxyVote: Option[PostalOrProxyVote] = None,
    contact: Option[Contact] = None,
    passport: Option[Passport] = None,
    possibleAddresses: Option[PossibleAddress] = None,
    sessionId: Option[String] = None)
  extends InprogressApplication[InprogressOverseas] {

  def merge(other:InprogressOverseas) = {
    other.copy(
      name = this.name.orElse(other.name),
      previousName = this.previousName.orElse(other.previousName),
      dateLeftSpecial = this.dateLeftSpecial.orElse(other.dateLeftSpecial),
      dateLeftUk = this.dateLeftUk.orElse(other.dateLeftUk),
      overseasParentName = this.overseasParentName.orElse(other.overseasParentName),
      lastRegisteredToVote = this.lastRegisteredToVote.orElse(other.lastRegisteredToVote),
      dob = this.dob.orElse(other.dob),
      nino = this.nino.orElse(other.nino),
      lastUkAddress = this.lastUkAddress.orElse(other.lastUkAddress),
      parentsAddress = this.parentsAddress.orElse(other.parentsAddress),
      address = this.address.orElse(other.address),
      openRegisterOptin = this.openRegisterOptin.orElse(other.openRegisterOptin),
      waysToVote = this.waysToVote.orElse(other.waysToVote),
      postalOrProxyVote = this.postalOrProxyVote.orElse(other.postalOrProxyVote),
      contact = this.contact.orElse(other.contact),
      passport = this.passport.orElse(other.passport),
      possibleAddresses = None,
      sessionId = this.sessionId.orElse(other.sessionId)
    )
  }
}
