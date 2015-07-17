package uk.gov.gds.ier.stubs

import com.google.inject.Inject
import uk.gov.gds.ier.model._
import scala.util.Random
import scala.Some
import uk.gov.gds.ier.model.Nino
import uk.gov.gds.ier.service.apiservice.{ConcreteIerApiService, IerApiService}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class IerApiServiceWithStripNino @Inject() (ierService: ConcreteIerApiService) extends IerApiService {

  override def submitOrdinaryApplication(
      ipAddress: Option[String],
      applicant: InprogressOrdinary,
      referenceNumber: Option[String],
      timeTaken: Option[String],
      language: String
  ) = {
    applicant.nino match {
      case Some(Nino(None, Some(noNinoReason))) => ierService.submitOrdinaryApplication(
        ipAddress,
        applicant,
        referenceNumber,
        timeTaken,
        language
      )
      case Some(Nino(Some(nino), None)) => ierService.submitOrdinaryApplication(
        ipAddress,
        applicant.copy(nino = Some(Nino(Some(randomNino()), None))),
        referenceNumber,
        timeTaken,
        language
      )
      case unexpectedNino => throw new IllegalArgumentException("Unexpected NINO: " + unexpectedNino)
    }
  }

  override def submitForcesApplication(
      ipAddress: Option[String],
      applicant: InprogressForces,
      referenceNumber: Option[String],
      timeTaken: Option[String]
  ) = {
    applicant.nino match {
      case Some(Nino(None, Some(noNinoReason))) => ierService.submitForcesApplication(
        ipAddress,
        applicant,
        referenceNumber,
        timeTaken
      )
      case Some(Nino(Some(nino), None)) => ierService.submitForcesApplication(
        ipAddress,
        applicant.copy(nino = Some(Nino(Some(randomNino()), None))),
        referenceNumber,
        timeTaken
      )
      case unexpectedNino => throw new IllegalArgumentException("Unexpected NINO: " + unexpectedNino)
    }
  }

  override def submitCrownApplication(
      ipAddress: Option[String],
      applicant: InprogressCrown,
      referenceNumber: Option[String],
      timeTaken: Option[String]
  ) = {
    applicant.nino match {
      case Some(Nino(None, Some(noNinoReason))) => ierService.submitCrownApplication(
        ipAddress,
        applicant,
        referenceNumber,
        timeTaken
      )
      case Some(Nino(Some(nino), None)) => ierService.submitCrownApplication(
        ipAddress,
        applicant.copy(nino = Some(Nino(Some(randomNino()), None))),
        referenceNumber,
        timeTaken
      )
      case unexpectedNino => throw new IllegalArgumentException("Unexpected NINO: " + unexpectedNino)
    }
  }

  override def submitOverseasApplication(
      ipAddress: Option[String],
      applicant: InprogressOverseas,
      referenceNumber: Option[String],
      timeTaken: Option[String]
  ) = {
    applicant.nino match {
      case Some(Nino(None, Some(noNinoReason))) => ierService.submitOverseasApplication(
        ipAddress,
        applicant,
        referenceNumber,
        timeTaken
      )
      case Some(Nino(Some(nino), None)) => ierService.submitOverseasApplication(
        ipAddress,
        applicant.copy(nino = Some(Nino(Some(randomNino()), None))),
        referenceNumber,
        timeTaken
      )
      case unexpectedNino => throw new IllegalArgumentException("Unexpected NINO: " + unexpectedNino)
    }
  }


  override def generateOrdinaryReferenceNumber(application: InprogressOrdinary): String = {
    application.nino match {
      case Some(Nino(None, Some(noNinoReason))) => {
        ierService.generateOrdinaryReferenceNumber(application)
      }
      case Some(Nino(Some(nino), None)) => {
        ierService.generateOrdinaryReferenceNumber(
          application.copy(nino = Some(Nino(Some(randomNino()), None)))
        )
      }
      case unexpectedNino => throw new IllegalArgumentException("Unexpected NINO: " + unexpectedNino)
    }
  }

  override def generateOverseasReferenceNumber(application: InprogressOverseas): String = {
    application.nino match {
      case Some(Nino(None, Some(noNinoReason))) => {
        ierService.generateOverseasReferenceNumber(application)
      }
      case Some(Nino(Some(nino), None)) => {
        ierService.generateOverseasReferenceNumber(
          application.copy(nino = Some(Nino(Some(randomNino()), None)))
        )
      }
      case unexpectedNino => throw new IllegalArgumentException("Unexpected NINO: " + unexpectedNino)
    }
  }

  override def generateForcesReferenceNumber(application: InprogressForces): String = {
    application.nino match {
      case Some(Nino(None, Some(noNinoReason))) => {
        ierService.generateForcesReferenceNumber(application)
      }
      case Some(Nino(Some(nino), None)) => {
        ierService.generateForcesReferenceNumber(
          application.copy(nino = Some(Nino(Some(randomNino()), None)))
        )
      }
      case unexpectedNino => throw new IllegalArgumentException("Unexpected NINO: " + unexpectedNino)
    }
  }

  override def generateCrownReferenceNumber(application: InprogressCrown): String = {
    application.nino match {
      case Some(Nino(None, Some(noNinoReason))) => {
        ierService.generateCrownReferenceNumber(application)
      }
      case Some(Nino(Some(nino), None)) => {
        ierService.generateCrownReferenceNumber(
          application.copy(nino = Some(Nino(Some(randomNino()), None)))
        )
      }
      case unexpectedNino => throw new IllegalArgumentException("Unexpected NINO: " + unexpectedNino)
    }
  }

  private[stubs] def randomNino() = {
    def num = {
      Random.nextInt(9)
    }
    s"XX $num$num $num$num $num$num A"
  }
}
