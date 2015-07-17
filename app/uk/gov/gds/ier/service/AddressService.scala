package uk.gov.gds.ier.service

import uk.gov.gds.ier.model.{Address, PartialAddress}
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config

class AddressService @Inject()(locateService: LocateService, config: Config) {

  def formFullAddress(partial: Option[PartialAddress]): Option[Address] = {
    partial flatMap {
      // selected address
      case PartialAddress(_, Some(uprn), postcode, _, _) => {
        val listOfAddresses = locateService.lookupAddress(postcode)
        listOfAddresses.find(address => address.uprn == Some(uprn))
      }
      // manual address
      case PartialAddress(_, None, postcode, Some(manualAddress), gssCode) => {
        Some(Address(
          lineOne = manualAddress.lineOne,
          lineTwo = manualAddress.lineTwo,
          lineThree = manualAddress.lineThree,
          city = manualAddress.city,
          county = None,
          uprn = None,
          postcode = postcode,
          gssCode = ensureGssCode(gssCode, postcode)))
      }
      // special case address, like 'ignored' Northen Ireland
      case PartialAddress(_,_,postcode,_,_) => {
        Some(Address(
          lineOne = None,
          lineTwo = None,
          lineThree = None,
          city = None,
          county = None,
          uprn = None,
          postcode = postcode,
          gssCode = None
        ))
      }
    }
  }

  def lookupPartialAddress(postcode:String):List[PartialAddress] = {
    locateService.lookupAddress(postcode) map { address =>
      PartialAddress(
        addressLine = Some(formAddressLine(address)),
        uprn = address.uprn,
        postcode = address.postcode,
        None,
        gssCode = address.gssCode
      )
    }
  }

  def lookupGssCode(postcode: String): Option[String] = {
    locateService.lookupGssCode(postcode)
  }

  def fillAddressLine(partial:PartialAddress):PartialAddress = {
    val address = locateService.lookupAddress(partial)
    val line = address map formAddressLine

    partial.copy(addressLine = line, gssCode = address.flatMap(_.gssCode))
  }

  def isScotland(postcode: String): Boolean = {
    !config.availableForScotland && locateService.lookupGssCode(postcode).exists(_.startsWith("S"))
  }

  def isNothernIreland(postcode: String): Boolean = {
    postcode.trim.toUpperCase.startsWith("BT")
  }

  def validAuthority(postcode: Option[String]): Boolean = {
    postcode match {
      case Some(p) => locateService.lookupGssCode(p).isDefined
      case _ => false
    }
  }

  protected[service] def formAddressLine(address:Address):String = {
    List(
      address.lineOne,
      address.lineTwo,
      address.lineThree,
      address.city,
      address.county
    ).filterNot(line => line.map(_.replaceAllLiterally(" ","")) == Some(""))
      .flatten
      .mkString(", ")
  }

  /**
   * If Gss Code is not present use postcode and do locateService lookup
   * and pick first available gssCode from the results.
   */
  private def ensureGssCode(gssCode: Option[String], postcode: String): Option[String] = {
    gssCode orElse locateService.lookupGssCode(postcode)
  }
}
