package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.model.{HasAddressOption, PossibleAddress, Addresses}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.transaction.forces.{InprogressForces, WithForcesControllers}
import uk.gov.gds.ier.service.WithAddressService

trait AddressSelectMustache extends StepTemplate[InprogressForces] {
    self:WithAddressService
    with WithForcesControllers
    with WithSerialiser =>

  private def pageTitle(hasUkAddress: Option[String]): String = {
    hasUkAddress.map(HasAddressOption.parse) match {
      case Some(HasAddressOption.YesAndLivingThere) => "What is your UK address?"
      case Some(HasAddressOption.YesAndNotLivingThere) => "What is your UK address?"
      case _ => "What was your last UK address?"
    }
  }

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

  val mustache = MustacheTemplate("forces/addressSelect") { (form, postUrl) =>
    implicit val progressForm = form

    val title = pageTitle(form(keys.address.hasAddress).value)

    val selectedUprn = form(keys.address.address.uprn).value
    val postcode = form(keys.address.address.postcode).value

    val storedAddresses = for(
      jsonList <- form(keys.possibleAddresses.jsonList).value;
      postcode <- postcode
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

    val options = possibleAddresses.map { possibleAddress =>
      possibleAddress.jsonList.addresses
    }.getOrElse(List.empty).map { address =>
      SelectOption(
        value = address.uprn.getOrElse(""),
        text = address.addressLine.getOrElse(""),
        selected = if (address.uprn == selectedUprn) {
          "selected=\"selected\""
        } else ""
      )
    }

    val hasAddresses = possibleAddresses.exists { poss =>
      !poss.jsonList.addresses.isEmpty
    }

    val hasAuthority = hasAddresses || addressService.validAuthority(postcode)

    val addressSelect = SelectField(
      key = keys.address.address.uprn,
      optionList = options,
      default = SelectOption(
        value = "",
        text = s"${options.size} addresses found"
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
        postUrl = postUrl.url,
        title = title,
        errorMessages = progressForm.globalErrors.map(_.message)
      ),
      lookupUrl = forces.AddressStep.routing.get.url,
      manualUrl = forces.AddressManualStep.routing.get.url,
      postcode = TextField(keys.address.address.postcode),
      address = addressSelectWithError,
      possibleJsonList = HiddenField(
        key = keys.possibleAddresses.jsonList,
        value = possibleAddresses.map { poss =>
          serialiser.toJson(poss.jsonList)
        }.getOrElse("")
      ),
      possiblePostcode = HiddenField(
        key = keys.possibleAddresses.postcode,
        value = form(keys.address.address.postcode).value.getOrElse("")
      ),
      hasAddresses = hasAddresses,
      hasAuthority = hasAuthority
    )
  }
}

