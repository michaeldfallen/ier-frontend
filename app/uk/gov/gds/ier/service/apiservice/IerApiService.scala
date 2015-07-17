package uk.gov.gds.ier.service.apiservice

import com.google.inject.Inject
import uk.gov.gds.ier.client.{StatsdClient, IerApiClient}
import uk.gov.gds.ier.model.HasAddressOption._
import uk.gov.gds.ier.model.{
  LastRegisteredToVote,
  LastRegisteredType,
  MovedHouseOption,
  Fail,
  Success}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.digest.ShaHashProvider
import org.joda.time.DateTime
import uk.gov.gds.ier.service._
import uk.gov.gds.ier.step.InprogressApplication
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import play.api.libs.json.Json
import uk.gov.gds.ier.model.LocalAuthority

abstract class IerApiService {
  def submitOrdinaryApplication(
      ipAddress: Option[String],
      applicant: InprogressOrdinary,
      referenceNumber: Option[String],
      timeTaken: Option[String],
      language: String
  ): IerApiApplicationResponse

  def submitOverseasApplication(
      ip: Option[String],
      applicant: InprogressOverseas,
      refNum: Option[String],
      timeTaken: Option[String]
  ): IerApiApplicationResponse

  def submitForcesApplication (
      ip: Option[String],
      applicant: InprogressForces,
      refNum: Option[String],
      timeTaken: Option[String]
  ): IerApiApplicationResponse

  def submitCrownApplication (
      ip: Option[String],
      applicant: InprogressCrown,
      refNum: Option[String],
      timeTaken: Option[String]
  ): IerApiApplicationResponse

  def generateOrdinaryReferenceNumber(application: InprogressOrdinary): String
  def generateOverseasReferenceNumber(application: InprogressOverseas): String
  def generateForcesReferenceNumber(application: InprogressForces): String
  def generateCrownReferenceNumber(application: InprogressCrown): String
}

class ConcreteIerApiService @Inject() (
    apiClient: IerApiClient,
    serialiser: JsonSerialiser,
    config: Config,
    addressService: AddressService,
    shaHashProvider:ShaHashProvider,
    isoCountryService: IsoCountryService
  ) extends IerApiService with Logging with SubmissionHacks {

  def submitOrdinaryApplication(
      ipAddress: Option[String],
      applicant: InprogressOrdinary,
      referenceNumber: Option[String],
      timeTaken: Option[String],
      language: String
  ) = {

    val isoCodes = applicant.nationality map { nationality =>
      isoCountryService.transformToIsoCode(nationality)
    }

    val fullCurrentAddress = addressService.formFullAddress(applicant.address)
    val fullPreviousAddress = applicant.previousAddress flatMap { prevAddress =>
      addressService.formFullAddress(prevAddress.previousAddress)
    }

    val lastRegistered = applicant.previousAddress.flatMap(_.movedRecently) match {
      case Some(MovedHouseOption.MovedFromAbroadRegistered) => {
        Some(LastRegisteredToVote(LastRegisteredType.Overseas))
      }
      case _ => None
    }

    val completeApplication = OrdinaryApplication(
      name = applicant.name,
      lastRegisteredToVote = lastRegistered,
      previousName = applicant.previousName,
      dob = applicant.dob,
      nationality = isoCodes,
      nino = applicant.nino,
      address = fullCurrentAddress,
      previousAddress = fullPreviousAddress,
      otherAddress = applicant.otherAddress,
      openRegisterOptin = applicant.openRegisterOptin,
      postalVote = applicant.postalVote,
      contact = applicant.contact,
      referenceNumber = referenceNumber,
      ip = ipAddress,
      timeTaken = timeTaken.getOrElse("-1"),
      language = language,
      sessionId = applicant.sessionId.getOrElse("")
    )

    val apiApplicant = ApiApplication(completeApplication.toApiMap)

    sendApplication(apiApplicant)
  }

  def submitOverseasApplication(
      ip:Option[String],
      applicant: InprogressOverseas,
      refNum:Option[String],
      timeTaken: Option[String]
  ) = {

    val fullLastUkRegAddress = addressService.formFullAddress(applicant.lastUkAddress)

    val fullParentRegAddress = addressService.formFullAddress(applicant.parentsAddress)

    val completeApplication = OverseasApplication(
      name = applicant.name,
      previousName = applicant.previousName,
      dateLeftSpecial = applicant.dateLeftSpecial,
      dateLeftUk = applicant.dateLeftUk,
      overseasParentName = applicant.overseasParentName,
      lastRegisteredToVote = applicant.lastRegisteredToVote,
      dob = applicant.dob,
      nino = applicant.nino,
      lastUkAddress = fullLastUkRegAddress.orElse(fullParentRegAddress),
      address = applicant.address,
      openRegisterOptin = applicant.openRegisterOptin,
      postalOrProxyVote = applicant.postalOrProxyVote,
      passport = applicant.passport,
      contact = applicant.contact,
      referenceNumber = refNum,
      ip = ip,
      timeTaken = timeTaken.getOrElse("-1"),
      sessionId = applicant.sessionId.getOrElse("")
    )

    val apiApplicant = ApiApplication(completeApplication.toApiMap)

    sendApplication(apiApplicant)
  }

  def submitForcesApplication(
      ipAddress: Option[String],
      applicant: InprogressForces,
      referenceNumber: Option[String],
      timeTaken: Option[String]
  ) = {

    val isoCodes = applicant.nationality map { nationality =>
      isoCountryService.transformToIsoCode(nationality)
    }

    val fullCurrentAddress = applicant.address flatMap { lastUkAddress =>
      addressService.formFullAddress(lastUkAddress.address)
    }

    val residentType = applicant.address flatMap { lastUkAddress =>
      lastUkAddress.hasAddress.flatMap {
        case `YesAndLivingThere` => Some("resident")
        case `YesAndNotLivingThere` => Some("not-resident")
        case `No` => Some("no-connection")
        case _ => None
      }
    }

    val fullPreviousAddress = applicant.previousAddress flatMap { prevAddress =>
      addressService.formFullAddress(prevAddress.previousAddress)
    }

    val completeApplication = ForcesApplication(
      statement = applicant.statement,
      address = fullCurrentAddress,
      previousAddress = fullPreviousAddress,
      nationality = isoCodes,
      dob = applicant.dob,
      name = applicant.name,
      previousName = applicant.previousName,
      nino = applicant.nino,
      service = applicant.service,
      rank = applicant.rank,
      contactAddress = applicant.contactAddress,
      openRegisterOptin = applicant.openRegisterOptin,
      postalOrProxyVote = applicant.postalOrProxyVote,
      contact = applicant.contact,
      referenceNumber = referenceNumber,
      ip = ipAddress,
      timeTaken = timeTaken.getOrElse("-1"),
      sessionId = applicant.sessionId.getOrElse(""),
      ukAddr = residentType
    ).hackNoUkAddressToNonat(applicant.nationality, applicant.address)

    val apiApplicant = ApiApplication(completeApplication.toApiMap)

    sendApplication(apiApplicant)
  }

  def submitCrownApplication(
      ipAddress: Option[String],
      applicant: InprogressCrown,
      referenceNumber: Option[String],
      timeTaken: Option[String]
  ) = {

    val isoCodes = applicant.nationality map { nationality =>
      isoCountryService.transformToIsoCode(nationality)
    }

    val fullCurrentAddress = applicant.address flatMap { lastUkAddress =>
      addressService.formFullAddress(lastUkAddress.address)
    }

    val residentType = applicant.address flatMap { lastUkAddress =>
      lastUkAddress.hasAddress.flatMap {
        case `YesAndLivingThere` => Some("resident")
        case `YesAndNotLivingThere` => Some("not-resident")
        case `No` => Some("no-connection")
        case _ => None
      }
    }

    val fullPreviousAddress = applicant.previousAddress flatMap { prevAddress =>
      addressService.formFullAddress(prevAddress.previousAddress)
    }

    val completeApplication = CrownApplication(
      statement = applicant.statement,
      address = fullCurrentAddress,
      previousAddress = fullPreviousAddress,
      nationality = isoCodes,
      dob = applicant.dob,
      name = applicant.name,
      previousName = applicant.previousName,
      job = applicant.job,
      nino = applicant.nino,
      contactAddress = applicant.contactAddress,
      openRegisterOptin = applicant.openRegisterOptin,
      postalOrProxyVote = applicant.postalOrProxyVote,
      contact = applicant.contact,
      referenceNumber = referenceNumber,
      ip = ipAddress,
      timeTaken = timeTaken.getOrElse("-1"),
      sessionId = applicant.sessionId.getOrElse(""),
      ukAddr = residentType
    ).hackNoUkAddressToNonat(applicant.nationality, applicant.address)

    val apiApplicant = ApiApplication(completeApplication.toApiMap)

    sendApplication(apiApplicant)
  }

  def getLocalAuthorityByGssCode(gssCode: String): LocalAuthority = {
    apiClient.get(config.ierLocalAuthorityLookupUrl + gssCode,
                   ("Authorization", "BEARER " + config.ierApiToken)) match {
      case Success(body,timeTakenMs) => {
        serialiser.fromJson[LocalAuthority](body)
      }
      case Fail(error,timeTakenMs) => {
        logger.error("Local Authority lookup failed: " + error)
        throw new ApiException(error)
      }
    }
  }

  private def sendApplication(application: ApiApplication) = {
    val applicationType = application.application.get("applicationType").getOrElse("")
    apiClient.post(config.ierApiUrl,
                   serialiser.toJson(application),
                   ("Authorization", "BEARER " + config.ierApiToken)) match {
      case Success(body,timeTakenMs) => {
        serialiser.fromJson[IerApiApplicationResponse](body)
      }
      case Fail(error,timeTakenMs) => {
        logger.error("Submitting application to api failed: " + error)
        throw new ApiException(error)
      }
    }
  }

  def generateOrdinaryReferenceNumber(application: InprogressOrdinary): String = {
    generateReferenceNumber(application)
  }

  def generateOverseasReferenceNumber(application: InprogressOverseas): String = {
    generateReferenceNumber(application)
  }

  def generateForcesReferenceNumber(application: InprogressForces): String = {
    generateReferenceNumber(application)
  }

  def generateCrownReferenceNumber(application: InprogressCrown): String = {
    generateReferenceNumber(application)
  }

  private def generateReferenceNumber[T <: InprogressApplication[T]](application:T) = {
    val json = serialiser.toJson(application)
    val encryptedBytes = shaHashProvider.getHash(json, Some(DateTime.now.toString))
    val encryptedHex = encryptedBytes.map{ byte => "%02X" format byte }
    encryptedHex.take(3).mkString
  }
}


