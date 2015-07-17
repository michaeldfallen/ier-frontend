package uk.gov.gds.ier.transaction.forces

import uk.gov.gds.ier.step.InprogressApplication
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.Statement
import uk.gov.gds.ier.model.DateOfBirth
import uk.gov.gds.ier.model.PartialNationality
import uk.gov.gds.ier.model.PostalOrProxyVote
import uk.gov.gds.ier.model.PossibleContactAddresses
import uk.gov.gds.ier.model.Name
import uk.gov.gds.ier.model.PreviousName
import uk.gov.gds.ier.model.Service
import uk.gov.gds.ier.model.Rank
import uk.gov.gds.ier.model.Nino
import uk.gov.gds.ier.model.WaysToVote
import uk.gov.gds.ier.model.Contact

case class InprogressForces(
    statement: Option[Statement] = None,
    address: Option[LastAddress] = None,
    previousAddress: Option[PartialPreviousAddress] = None,
    nationality: Option[PartialNationality] = None,
    dob: Option[DateOfBirth] = None,
    name: Option[Name] = None,
    previousName: Option[PreviousName] = None,
    nino: Option[Nino] = None,
    service: Option[Service] = None,
    rank: Option[Rank] = None,
    contactAddress: Option[PossibleContactAddresses] = None,
    openRegisterOptin: Option[Boolean] = None,
    waysToVote: Option[WaysToVote] = None,
    postalOrProxyVote: Option[PostalOrProxyVote] = None,
    contact: Option[Contact] = None,
    possibleAddresses: Option[PossibleAddress] = None,
    sessionId: Option[String] = None)
  extends InprogressApplication[InprogressForces] {

  def merge(other:InprogressForces) = {
    other.copy(
      statement = this.statement.orElse(other.statement),
      address = this.address.orElse(other.address),
      previousAddress = this.previousAddress.orElse(other.previousAddress),
      nationality = this.nationality.orElse(other.nationality),
      dob = this.dob.orElse(other.dob),
      name = this.name.orElse(other.name),
      previousName = this.previousName.orElse(other.previousName),
      nino = this.nino.orElse(other.nino),
      service = this.service.orElse(other.service),
      rank = this.rank.orElse(other.rank),
      contactAddress = this.contactAddress.orElse(other.contactAddress),
      openRegisterOptin = this.openRegisterOptin.orElse(other.openRegisterOptin),
      waysToVote = this.waysToVote.orElse(other.waysToVote),
      postalOrProxyVote = this.postalOrProxyVote.orElse(other.postalOrProxyVote),
      contact = this.contact.orElse(other.contact),
      possibleAddresses = None,
      sessionId = this.sessionId.orElse(other.sessionId)
    )
  }
}
