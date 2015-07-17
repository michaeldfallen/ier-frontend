package uk.gov.gds.ier.transaction.ordinary.confirmation

import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.validation.{Key, ErrorTransformForm}
import uk.gov.gds.ier.model.{PostalVoteDeliveryMethod, PostalVoteOption, OtherAddress, MovedHouseOption}
import uk.gov.gds.ier.form.AddressHelpers
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.transaction.shared.{BlockContent, BlockError, EitherErrorOrContent}
import uk.gov.gds.ier.service.WithAddressService
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.form.OrdinaryFormImplicits
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.WithOrdinaryControllers

trait ConfirmationMustache
    extends StepTemplate[InprogressOrdinary] {
    self: WithRemoteAssets
      with WithOrdinaryControllers
      with WithAddressService
      with OrdinaryFormImplicits =>

  case class ConfirmationQuestion(
      content: EitherErrorOrContent,
      title: String,
      editLink: String,
      changeName: String
  )

  case class ConfirmationModel(
      question: Question,
      completeApplicantDetails: List[ConfirmationQuestion]
  ) extends MustacheData

  val mustache = MultilingualTemplate("ordinary/confirmation") { implicit lang => (form, postUrl) =>
    val confirmation = new ConfirmationBlocks(form)

    val completeApplicantData = List(
      confirmation.name,
      confirmation.previousName,
      confirmation.dateOfBirth,
      confirmation.nationality,
      confirmation.nino,
      confirmation.address,
      confirmation.secondAddress,
      confirmation.previousAddress,
      confirmation.openRegister,
      confirmation.postalVote,
      confirmation.contact
    ).flatten

    ConfirmationModel(
      question = Question(
        title = Messages("ordinary_confirmation_title_header"),
        postUrl = postUrl.url,
        contentClasses = "confirmation"
      ),
      completeApplicantDetails = completeApplicantData
    )

  }

  class ConfirmationBlocks(form: ErrorTransformForm[InprogressOrdinary])(implicit lang: Lang)
    extends AddressHelpers with Logging {

    val completeThisStepMessage = Messages("ordinary_confirmation_error_completeThis")

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
        title = Messages("ordinary_confirmation_name_title"),
        editLink = ordinary.NameStep.routing.editGet.url,
        changeName = Messages("ordinary_confirmation_name_changeName"),
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
        case _ => Messages("ordinary_confirmation_previousName_nameNotChanged")
      }
      Some(ConfirmationQuestion(
        title = Messages("ordinary_confirmation_previousName_title"),
        editLink = ordinary.NameStep.routing.editGet.url,
        changeName = Messages("ordinary_confirmation_previousName_changeName"),
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
            case Some(reasonDescription) =>Messages("ordinary_confirmation_dob_noDOBReason", reasonDescription)
            case _ => ""
          }
          val ageRange = form(keys.dob.noDob.range).value match {
            case Some("under18") => Messages("ordinary_confirmation_dob_noDOBUnder18")
            case Some("18to70") => Messages("ordinary_confirmation_dob_noDOB18to70")
            case Some("over70") => Messages("ordinary_confirmation_dob_noDOBOver70")
            case Some("dontKnow") => Messages("ordinary_confirmation_dob_noDOBDontKnow")
            case _ => ""
          }
          List(excuseReason, ageRange)
        }

      Some(ConfirmationQuestion(
        title = Messages("ordinary_confirmation_dob_title"),
        editLink = ordinary.DateOfBirthStep.routing.editGet.url,
        changeName = Messages("ordinary_confirmation_dob_changeName"),
        content = ifComplete(keys.dob) {
          dobContent
        }
      ))
    }

    def nationality = {
      Some(ConfirmationQuestion(
        title = Messages("ordinary_confirmation_nationality_title"),
        editLink = ordinary.NationalityStep.routing.editGet.url,
        changeName = Messages("ordinary_confirmation_nationality_changeName"),
        content = ifComplete(keys.nationality) {
          if (nationalityIsFilled) {
            List(confirmationNationalityString)
          } else {
            List(Messages("ordinary_confirmation_nationality_noNationalityReason"),
              form(keys.nationality.noNationalityReason).value.getOrElse(""))
          }
        }
      ))
    }

    def nino = {
      Some(ConfirmationQuestion(
        title = Messages("ordinary_confirmation_nino_title"),
        editLink = ordinary.NinoStep.routing.editGet.url,
        changeName = Messages("ordinary_confirmation_nino_changeName"),
        content = ifComplete(keys.nino) {
          if(form(keys.nino.nino).value.isDefined){
            List(form(keys.nino.nino).value.getOrElse("").toUpperCase)
          } else {
            List(Messages("ordinary_confirmation_nino_noNinoReason"),
              form(keys.nino.noNinoReason).value.getOrElse(""))
          }
        }
      ))
    }

    def address = {
      Some(ConfirmationQuestion(
        title = Messages("ordinary_confirmation_address_title"),
        editLink = if (isManualAddressDefined(form, keys.address.manualAddress)) {
          ordinary.AddressManualStep.routing.editGet.url
        } else {
          ordinary.AddressSelectStep.routing.editGet.url
        },
        changeName = Messages("ordinary_confirmation_address_changeName"),
        content = ifComplete(keys.address) {
          val addressLine = form(keys.address.addressLine).value.orElse{
            manualAddressToOneLine(form, keys.address.manualAddress)
          }.getOrElse("")
          val postcode = form(keys.address.postcode).value.getOrElse("").toUpperCase
          List(addressLine, postcode)
        }
      ))
    }

    def secondAddress = {
      Some(ConfirmationQuestion(
        title = Messages("ordinary_confirmation_secondAddress_title"),
        editLink = ordinary.OtherAddressStep.routing.editGet.url,
        changeName = Messages("ordinary_confirmation_secondAddress_changeName"),
        content =
          ifComplete(keys.otherAddress) {
            form(keys.otherAddress.hasOtherAddress).value match {
              case Some(secAddrType)
                if OtherAddress.parse(secAddrType) != OtherAddress.NoOtherAddress =>
                  List(Messages("ordinary_confirmation_secondAddress_haveAddress"))
              case _ => List(Messages("ordinary_confirmation_secondAddress_dontHaveAddress"))
            }
          }
      ))
    }
    def previousAddress = {
      val movedHouse = form(keys.previousAddress.movedRecently).value.map {
        MovedHouseOption.parse(_)
      }

      val title = movedHouse match {
        case Some(MovedHouseOption.MovedFromAbroadRegistered) => Messages("ordinary_confirmation_previousAddress_title_lastAddress")
        case _ => Messages("ordinary_confirmation_previousAddress_title_previous")
      }

      Some(ConfirmationQuestion(
        title = title,
        editLink = ordinary.PreviousAddressFirstStep.routing.editGet.url,
        changeName = Messages("ordinary_confirmation_previousAddress_changeName"),
        content = ifComplete(keys.previousAddress, keys.previousAddress.movedRecently) {
          movedHouse match {
            case Some(MovedHouseOption.MovedFromAbroadNotRegistered) =>
              List(Messages("ordinary_confirmation_previousAddress_movedFromAbroadNotRegistered"))
            case Some(moveOption) if moveOption.hasPreviousAddress => {
              val postcode = form(keys.previousAddress.previousAddress.postcode).value.map(_.toUpperCase)
              if (addressService.isNothernIreland(postcode.getOrElse(""))) {
                List(postcode, Some(Messages("ordinary_confirmation_previousAddress_movedFromNI"))).flatten
              } else {
                val address = form(keys.previousAddress.previousAddress.addressLine).value.orElse(
                  manualAddressToOneLine(form, keys.previousAddress.previousAddress.manualAddress))
                List(address, postcode).flatten
              }
            }
            case _ => List(Messages("ordinary_confirmation_previousAddress_notMoved"))
            }
          }
        ))
    }

    def openRegister = {
      Some(ConfirmationQuestion(
        title = Messages("ordinary_confirmation_openRegister_title"),
        editLink = ordinary.OpenRegisterStep.routing.editGet.url,
        changeName = Messages("ordinary_confirmation_openRegister_changeName"),
        content = ifComplete(keys.openRegister) {
          if (form(keys.openRegister.optIn).value == Some("true")){
            List(Messages("ordinary_confirmation_openRegister_optIn"))
          } else {
            List(Messages("ordinary_confirmation_openRegister_optOut"))
          }
        }
      ))
    }

    def postalVote = {
      val postalVoteOption = PostalVoteOption.parse(form(keys.postalVote.optIn).value.getOrElse(""))
      val deliveryMethod = PostalVoteDeliveryMethod(
        deliveryMethod = form(keys.postalVote.deliveryMethod.methodName).value,
        emailAddress = form(keys.postalVote.deliveryMethod.emailAddress).value
      )
      val postalVoteContent = if (deliveryMethod.isEmail) {
        List(
          Messages("ordinary_confirmation_postalVote_emailDelivery"),
          deliveryMethod.emailAddress getOrElse ""
        )
      } else {
        List(Messages("ordinary_confirmation_postalVote_mailDelivery"))
      }
      val noPostalVoteContent = List(
        Messages("ordinary_confirmation_postalVote_dontWant")
      )
      val alreadyHaveContent = List(
        Messages("ordinary_confirmation_postalVote_alreadyHave")
      )

      Some(ConfirmationQuestion(
        title = Messages("ordinary_confirmation_postalVote_title"),
        editLink = ordinary.PostalVoteStep.routing.editGet.url,
        changeName = Messages("ordinary_confirmation_postalVote_changeName"),
        content = ifComplete(keys.postalVote) {
          postalVoteOption match {
            case PostalVoteOption.Yes => postalVoteContent
            case PostalVoteOption.NoAndAlreadyHave => alreadyHaveContent
            case PostalVoteOption.NoAndVoteInPerson => noPostalVoteContent
            case _ => List(completeThisStepMessage)
          }
        }
      ))
    }

    def contact = {
      Some(ConfirmationQuestion(
        title = Messages("ordinary_confirmation_contact_title"),
        editLink = ordinary.ContactStep.routing.editGet.url,
        changeName = Messages("ordinary_confirmation_contact_changeName"),
        content = ifComplete(keys.contact) {
          val post = if (form(keys.contact.post.contactMe).value == Some("true")) {
            Some(Messages("ordinary_confirmation_contact_byPost"))
          } else None

          val phone = if (form(keys.contact.phone.contactMe).value == Some("true")) {
            form(keys.contact.phone.detail).value.map( phone =>
              Messages("ordinary_confirmation_contact_byPhone",phone))
          } else None

          val email = if( form(keys.contact.email.contactMe).value == Some("true")) {
            form(keys.contact.email.detail).value.map {email =>
              Messages("ordinary_confirmation_contact_byEmail", email)}
          } else None

          List(post, phone, email).flatten
        }
      ))
    }

    private def getNationalities: List[String] = {
      val british = form(keys.nationality.british).value
      val irish =form(keys.nationality.irish).value
      british.toList.filter(_ == "true").map(brit => Messages("ordinary_confirmation_nationality_british")) ++
      irish.toList.filter(_ == "true").map(isIrish => Messages("ordinary_confirmation_nationality_irish"))
    }

    private[confirmation] def confirmationNationalityString = {
      def concatCommaEndInAnd(
          list:List[String],
          prepend:String = "",
          append:String = "") = {
        val filteredList = list.filter(_.nonEmpty)

        val andStr=" "+Messages("ordinary_confirmation_nationality_and")+" "
        val str = List(
          filteredList.dropRight(1).mkString(", "),
          filteredList.takeRight(1).mkString("")
        ).filter(_.nonEmpty).mkString(andStr)

        if (str.isEmpty) "" else s"$prepend$str$append"
      }

      val localNationalities = getNationalities
      val foreignNationalities = concatCommaEndInAnd(
        prepend = Messages("ordinary_confirmation_nationality_citizenOf")+" ",
        list = form.obtainOtherCountriesList
      )

      val nationalityString = concatCommaEndInAnd(
        prepend = Messages("ordinary_confirmation_nationality_iAm")+" ",
        list = localNationalities :+ foreignNationalities
      )
      nationalityString
    }

    private def nationalityIsFilled: Boolean = {
      val isBritish = form(keys.nationality.british).value.getOrElse("false").toBoolean
      val isIrish = form(keys.nationality.irish).value.getOrElse("false").toBoolean
      val otherCountries = form.obtainOtherCountriesList
      (isBritish || isIrish || !otherCountries.isEmpty)
    }


  }
}
