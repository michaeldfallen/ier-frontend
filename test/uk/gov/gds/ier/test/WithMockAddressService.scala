package uk.gov.gds.ier.test

import uk.gov.gds.ier.service.{WithAddressService, AddressService}

trait WithMockAddressService extends WithAddressService {

  private val mockito = new MockitoHelpers {}

  val addressService = {

    val mockService = mockito.mock[AddressService]
    mockito.when (mockService.isNothernIreland("BT7 1AA")).thenReturn(true)
    mockito.when (mockService.isNothernIreland("bt7 1aa")).thenReturn(true)
    mockito.when (mockService.isNothernIreland("BT71AA")).thenReturn(true)
    mockito.when (mockService.isNothernIreland("bt71aa")).thenReturn(true)
    mockService
  }

}
