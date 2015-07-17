package uk.gov.gds.ier.transaction.ordinary.previousAddress

import uk.gov.gds.ier.model.{PossibleAddress, Addresses, MovedHouseOption}
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait PreviousAddressSelectMustache extends StepTemplate[InprogressOrdinary] {

  val addressService: AddressService
  val serialiser: JsonSerialiser

  case class SelectModel (
      question: Question,
      lookupUrl: String,
      manualUrl: String,
      postcode: Field,
      address: Field,
      possibleJsonList: Field,
      possiblePostcode: Field,
      hasAddresses: Boolean,
      hasAuthority: Boolean
  ) extends MustacheData

  val mustache = MultilingualTemplate("ordinary/previousAddressSelect") { implicit lang =>
    (form, post) =>
    implicit val progressForm = form

    val movedRecently = form(keys.previousAddress.movedRecently).value.map {
      str => MovedHouseOption.parse(str)
    }

    val title = movedRecently match {
      case Some(MovedHouseOption.MovedFromAbroadRegistered) => Messages("ordinary_previousAddress_yesFromAbroadWasRegistered_title")
      case _ => Messages("ordinary_previousAddress_yesFromUk_title")
    }

    val selectedUprn = form(keys.previousAddress.previousAddress.uprn).value
    val postcode = form(keys.previousAddress.previousAddress.postcode).value

    val storedAddresses = for(
      jsonList <- form(keys.possibleAddresses.jsonList).value;
      postcode <- form(keys.possibleAddresses.postcode).value
    ) yield {
      PossibleAddress(
        jsonList = serialiser.fromJson[Addresses](jsonList),
        postcode = postcode
      )
    }

    //IER0091 : Temp removing the storedAddresses section of the code checks to remove populating against the hidden input field
    //val possibleAddresses = storedAddresses orElse postcode.map { pc =>
    val possibleAddresses = postcode.map { pc =>
      val addresses = addressService.lookupPartialAddress(pc)
      PossibleAddress(
        jsonList = Addresses(addresses),
        postcode = pc
      )
    }

    val options = for (
      address <- possibleAddresses.map(_.jsonList.addresses).toList.flatten
    ) yield SelectOption(
      value = address.uprn.getOrElse(""),
      text = address.addressLine.getOrElse(""),
      selected = if (address.uprn == selectedUprn) {
        "selected=\"selected\""
      } else ""
    )

    val hasAddresses = possibleAddresses.exists(!_.jsonList.addresses.isEmpty)
    //IER0055: Check authority table too to allow for manual entry
    val hasAuthority = hasAddresses || addressService.validAuthority(postcode)

    val addressSelect = SelectField(
      key = keys.previousAddress.previousAddress.uprn,
      optionList = options,
      default = SelectOption(
        value = "",
        text = s"${options.size} ${Messages("ordinary_previousAddress_select_addressesFound")}"
      )
    )
    val addressSelectWithError = addressSelect.copy(
      classes = if (!hasAddresses) {
        "invalid"
      } else {
        addressSelect.classes
      }
    )

    SelectModel(
      question = Question(
        postUrl = post.url,
        number = s"8 ${Messages("step_of")} 11",
        title = title,
        errorMessages = Messages.translatedGlobalErrors(form)
      ),
      lookupUrl = routes.PreviousAddressPostcodeStep.get.url,
      manualUrl = routes.PreviousAddressManualStep.get.url,
      postcode = TextField(keys.previousAddress.previousAddress.postcode),
      address = addressSelectWithError,  // this is model data for <select>
      possibleJsonList = HiddenField(
        key = keys.possibleAddresses.jsonList,
        value = possibleAddresses.map { poss =>
          serialiser.toJson(poss.jsonList)
        }.getOrElse("")
      ),
      possiblePostcode = HiddenField(
        key = keys.possibleAddresses.postcode,
        value = form(keys.previousAddress.previousAddress.postcode).value.getOrElse("")
      ),
      hasAddresses = hasAddresses,
      hasAuthority = hasAuthority
    )
  }
}
