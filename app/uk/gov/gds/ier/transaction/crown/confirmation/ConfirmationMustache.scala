package uk.gov.gds.ier.transaction.crown.confirmation

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.{HasAddressOption, WaysToVoteType, MovedHouseOption}
import uk.gov.gds.ier.validation.constants.{NationalityConstants, DateOfBirthConstants}
import uk.gov.gds.ier.validation.{ErrorTransformForm, Key}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.form.AddressHelpers
import uk.gov.gds.ier.transaction.crown.{InprogressCrown, WithCrownControllers}
import uk.gov.gds.ier.transaction.shared.{BlockContent, BlockError, EitherErrorOrContent}
import uk.gov.gds.ier.service.WithAddressService
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.step.StepTemplate

trait ConfirmationMustache
  extends StepTemplate[InprogressCrown]
  with WithAddressService {
    self: WithRemoteAssets
    with WithCrownControllers =>

  case class ConfirmationQuestion(
      content: EitherErrorOrContent,
      title: String,
      editLink: String,
      changeName: String
  )

  case class ConfirmationModel(
      question: Question,
      applicantDetails: List[ConfirmationQuestion],
      partnerDetails: List[ConfirmationQuestion],
      displayPartnerBlock: Boolean
  ) extends MustacheData

  val mustache = MustacheTemplate("crown/confirmation") {
    (form, post) =>

    val confirmation = new ConfirmationBlocks(form)

    val partnerData = List(
      confirmation.partnerJobTitle
    ).flatten

    val applicantData = List(
      confirmation.name,
      confirmation.previousName,
      confirmation.dateOfBirth,
      confirmation.nationality,
      confirmation.nino,
      confirmation.applicantJobTitle,
      confirmation.address,
      confirmation.previousAddress,
      confirmation.contactAddress,
      confirmation.openRegister,
      confirmation.waysToVote,
      confirmation.contact
    ).flatten

    ConfirmationModel(
      question = Question(
        title = "Confirm your details - Register to vote",
        postUrl = post.url,
        contentClasses = "confirmation"
      ),
      partnerDetails = partnerData,
      applicantDetails = applicantData,
      displayPartnerBlock = !partnerData.isEmpty
    )
  }

  class ConfirmationBlocks(form: ErrorTransformForm[InprogressCrown])
    extends AddressHelpers with Logging {


    val completeThisStepMessage = "Please complete this step"

    def ifComplete(key:Key)(confirmationHtml: => List[String]): EitherErrorOrContent = {
      if (form(key).hasErrors) {
        BlockError(completeThisStepMessage)
      } else {
        BlockContent(confirmationHtml)
      }
    }

    def ifComplete(keys:Key*)(confirmationHtml: => List[String]): EitherErrorOrContent = {
      if (keys.exists(form(_).hasErrors)) {
        BlockError(completeThisStepMessage)
      } else {
        BlockContent(confirmationHtml)
      }
    }

    def name = {
      Some(ConfirmationQuestion(
        title = "Name",
        editLink = crown.NameStep.routing.editGet.url,
        changeName = "full name",
        content = ifComplete(keys.name) {
          List(List(
            form(keys.name.firstName).value,
            form(keys.name.middleNames).value,
            form(keys.name.lastName).value).flatten.mkString(" "))
        }
      ))
    }

    def previousName = {
      val havePreviousName = form(keys.previousName.hasPreviousName).value
      val prevNameStr =  havePreviousName match {
        case Some("true") => {
          List(
            form(keys.previousName.previousName.firstName).value,
            form(keys.previousName.previousName.middleNames).value,
            form(keys.previousName.previousName.lastName).value
          ).flatten.mkString(" ")
        }
        case _ => "I have not changed my name in the last 12 months"
      }
      Some(ConfirmationQuestion(
        title = "Previous name",
        editLink = crown.NameStep.routing.editGet.url,
        changeName = "previous name",
        content = ifComplete(keys.previousName) {
          List(prevNameStr)
        }
      ))
    }

    def dateOfBirth = {

      val dobContent =
        if (form(keys.dob.dob.day).value.isDefined) {
          val day = form(keys.dob.dob.day).value.getOrElse("")
          val month = DateOfBirthConstants.monthsByNumber(form(keys.dob.dob.month).value.get)
          val year = form(keys.dob.dob.year).value.getOrElse("")
          List(day + " " + month + " "  + year)
        } else {
          val excuseReason = form(keys.dob.noDob.reason).value match {
            case Some(reasonDescription) =>
              s"You are unable to provide your date of birth because: $reasonDescription"
            case _ => ""
          }
          val ageRange = form(keys.dob.noDob.range).value match {
            case Some("under18") => "I am roughly under 18"
            case Some("18to70") => "I am over 18 years old"
            case Some("over70") => "I am over 70 years old"
            case Some("dontKnow") => "I don't know my age"
            case _ => ""
          }
          List(excuseReason, ageRange)
        }

      Some(ConfirmationQuestion(
        title = "Date of birth",
        editLink = crown.DateOfBirthStep.routing.editGet.url,
        changeName = "date of birth",
        content = ifComplete(keys.dob) {
          dobContent
        }
      ))
    }

    def nationality = {
      Some(ConfirmationQuestion(
        title = "Nationality",
        editLink = crown.NationalityStep.routing.editGet.url,
        changeName = "nationality",
        content = ifComplete(keys.nationality) {
          if (nationalityIsFilled) {
            List("I am " + confirmationNationalityString)
          } else {
            List("I cannot provide my nationality because:",
              form(keys.nationality.noNationalityReason).value.getOrElse(""))
          }
        }
      ))
    }

    def nino = {
      Some(ConfirmationQuestion(
        title = "National Insurance number",
        editLink = crown.NinoStep.routing.editGet.url,
        changeName = "national insurance number",
        content = ifComplete(keys.nino) {
          if(form(keys.nino.nino).value.isDefined){
            List(form(keys.nino.nino).value.getOrElse("").toUpperCase)
          } else {
            List("I cannot provide my national insurance number because:",
              form(keys.nino.noNinoReason).value.getOrElse(""))
          }
        }
      ))
    }

    def applicantJobTitle : Option[ConfirmationQuestion] = {
      if (!displayPartnerBlock) {
        Some(jobTitle)
      } else {
        None
      }
    }

    def partnerJobTitle : Option[ConfirmationQuestion] = {
      if (displayPartnerBlock) {
        Some(jobTitle)
      } else {
        None
      }
    }

    def jobTitle = {
      ConfirmationQuestion(
        title = "Role",
        editLink = crown.JobStep.routing.editGet.url,
        changeName = "role",
        content = ifComplete(keys.job) {
          List(
            form(keys.job.jobTitle).value,
            form(keys.job.payrollNumber).value,
            form(keys.job.govDepartment).value
          ).flatten
        }
      )
    }

    def address = {
      val hasAddressValue = form(keys.address.hasAddress).value.getOrElse("")

      val (addressTitle, addressChangeName) = HasAddressOption.parse(hasAddressValue) match {
        case HasAddressOption.YesAndLivingThere | HasAddressOption.YesAndNotLivingThere =>
          ("Registration address", "registration address")
        case _ => ("Registration address", "registration address")
      }

      Some(ConfirmationQuestion(
        title = addressTitle,
        editLink = crown.AddressFirstStep.routing.editGet.url,
        changeName = addressChangeName,
        content = ifComplete(keys.address) {
          val addressLine = form(keys.address.address.addressLine).value.orElse{
            manualAddressToOneLine(form, keys.address.address.manualAddress)
          }.getOrElse("")
          val postcode = form(keys.address.address.postcode).value.getOrElse("").toUpperCase
          List(addressLine, postcode)
        }
      ))
    }

    def previousAddress = {
      val hasCurrentAddress = form(keys.address.hasAddress).value.exists{
        HasAddressOption.parse(_) match {
          case HasAddressOption.YesAndLivingThere | HasAddressOption.YesAndNotLivingThere => true
          case _ => false
        }
      }

      if (hasCurrentAddress) {
        Some(ConfirmationQuestion(
          title = "Previous address",
          editLink = crown.PreviousAddressFirstStep.routing.editGet.url,
          changeName = "your previous address",
          content = ifComplete(keys.previousAddress) {
            val moved = form(keys.previousAddress.movedRecently).value
              .map(MovedHouseOption.parse(_).hasPreviousAddress)
              .getOrElse(false)

            if (moved) {
              val postcode = form(keys.previousAddress.previousAddress.postcode).value.getOrElse("").toUpperCase
              if (addressService.isNothernIreland(postcode)) {
                List(postcode, "I was previously registered in Northern Ireland")
              } else {
                val address = if (form(keys.previousAddress.previousAddress.addressLine).value.isDefined) {
                  form(keys.previousAddress.previousAddress.addressLine).value
                } else {
                  manualAddressToOneLine(form, keys.previousAddress.previousAddress.manualAddress)
                }
                List(address, Some(postcode)).flatten
              }
            } else {
              List("I have not moved in the last 12 months")
            }
          }
        ))
      }
      else None
    }


    def contactAddress = {
      Some(ConfirmationQuestion(
        title = "Correspondence address",
        editLink = crown.ContactAddressStep.routing.editGet.url,
        changeName = "correspondence address",
        content = {
          val addressTypeKey = form(keys.contactAddress.contactAddressType).value match {
            case Some("uk") => Some(keys.ukContactAddress)
            case Some("bfpo") => Some(keys.bfpoContactAddress)
            case Some("other") => Some(keys.otherContactAddress)
            case _ => None
          }

          if (!addressTypeKey.isDefined) {
            ifComplete(keys.contactAddress.contactAddressType) { List() }
          } else if (addressTypeKey.equals(Some(keys.ukContactAddress))) {
            ifComplete(keys.address) {
              val addressLine = form(keys.address.address.addressLine).value.orElse{
                manualAddressToOneLine(form, keys.address.address.manualAddress)
              }
              val postcode = form(keys.address.address.postcode).value.map(_.toUpperCase)
              List(addressLine, postcode).flatten
            }
          }
          else {
            ifComplete(keys.contactAddress) {
              val contactAddressKey = keys.contactAddress.prependNamespace(addressTypeKey.get)
              val addressLines = concatAddressToOneLine(form, contactAddressKey)
              val postcode = form(contactAddressKey.prependNamespace(keys.postcode)).value
              val country = form(contactAddressKey.prependNamespace(keys.country)).value

              List(addressLines, postcode, country).flatten
            }
          }
        }
      ))
    }

    def openRegister = {
      Some(ConfirmationQuestion(
        title = "Open register",
        editLink = crown.OpenRegisterStep.routing.editGet.url,
        changeName = "open register",
        content = ifComplete(keys.openRegister) {
          if (form(keys.openRegister.optIn).value == Some("true")){
            List("I want to include my name and address on the open register")
          } else {
            List("I don't want my name and address on the open register")
          }
        }
      ))
    }

    def waysToVote = {
      val way = form(keys.waysToVote.wayType).value.map(WaysToVoteType.parse(_))
      val prettyWayName = way match {
        case Some(WaysToVoteType.ByPost) => "a postal vote"
        case Some(WaysToVoteType.ByProxy) => "a proxy vote"
        case _ => "an"
      }
      val myEmail = form(keys.postalOrProxyVote.deliveryMethod.emailAddress).value.getOrElse("")
      val emailMe = form(keys.postalOrProxyVote.deliveryMethod.methodName).value == Some("email")
      val optIn = form(keys.postalOrProxyVote.optIn).value
      val ways = way match {
        case Some(WaysToVoteType.ByPost) => List("I want to vote by post")
        case Some(WaysToVoteType.ByProxy) => List("I want to vote by proxy (someone else voting for me)")
        case Some(WaysToVoteType.InPerson) => List("I want to vote in person, at a polling station")
        case _ => List()
      }
      val postalOrProxyVote = (optIn, emailMe) match {
        case (Some("true"), true) => List("Send an application form to:", myEmail)
        case (Some("true"), false) => List("Send me an application form in the post")
        case (Some("false"), _) => List(s"I do not need ${prettyWayName} application form")
        case (_, _) => List()
      }

      Some(ConfirmationQuestion(
        title = "Voting options",
        editLink = crown.WaysToVoteStep.routing.editGet.url,
        changeName = "voting",
        content = ifComplete(keys.waysToVote) {
          ways ++ postalOrProxyVote
        }
      ))
    }

    def contact = {
      Some(ConfirmationQuestion(
        title = "How we should contact you",
        editLink = crown.ContactStep.routing.editGet.url,
        changeName = "how we should contact you",
        content = ifComplete(keys.contact) {
          val post = if (form(keys.contact.post.contactMe).value == Some("true")) {
            Some("By post")
          } else None

          val phone = if (form(keys.contact.phone.contactMe).value == Some("true")) {
            Some(s"By phone: ${form(keys.contact.phone.detail).value.getOrElse("")}")
          } else None

          val email = if( form(keys.contact.email.contactMe).value == Some("true")) {
            Some(s"By email: ${form(keys.contact.email.detail).value.getOrElse("")}")
          } else None

          List(post, phone, email).flatten
        }
      ))
    }

    def getNationalities = {
      val british = form(keys.nationality.british).value
      val irish =form(keys.nationality.irish).value
      british.toList.filter(_ == "true").map(brit => "United Kingdom") ++
        irish.toList.filter(_ == "true").map(isIrish => "Ireland")
    }

    def confirmationNationalityString = {
      val allCountries = getNationalities ++ obtainOtherCountriesList
      val nationalityString = List(allCountries.dropRight(1).mkString(", "),
        allCountries.takeRight(1).mkString("")).filter(_.nonEmpty)
      s"a citizen of ${nationalityString.mkString(" and ")}"
    }

    def nationalityIsFilled:Boolean = {
      val british = form(keys.nationality.british).value.getOrElse("false").toBoolean
      val irish = form(keys.nationality.irish).value.getOrElse("false").toBoolean
      val otherCountries = obtainOtherCountriesList
      (british || irish || !otherCountries.isEmpty)
    }

    def obtainOtherCountriesList:List[String] = {
      (
        for (i <- 0 until NationalityConstants.numberMaxOfOtherCountries
             if (form(otherCountriesKey(i)).value.isDefined)
               && !form(otherCountriesKey(i)).value.get.isEmpty)
        yield form(otherCountriesKey(i)).value.get
        ).toList
    }

    def otherCountriesKey(i: Int) = keys.nationality.otherCountries.item(i)

    def displayPartnerBlock:Boolean = {

      val crownPartner = form(keys.statement.crownPartner).value == Some("true")
      val crownServant = form(keys.statement.crownServant).value == Some("true")
      val councilEmployee = form(keys.statement.councilEmployee).value == Some("true")
      val councilPartner = form(keys.statement.councilPartner).value == Some("true")

      (crownPartner || councilPartner) && !(crownServant || councilEmployee)
    }
  }
}
