package uk.gov.gds.ier.service.apiservice

import uk.gov.gds.ier.test.UnitTestSuite

class CompleteApplicationTests extends UnitTestSuite {

  val appliation = new CompleteApplication {
    val timeTaken = "1234"
    val toApiMap:Map[String, String] = Map.empty
  }

  it should "keep the string as it is if it doesn't contain special characters" in {
    val apiMap = Map ("a" -> "Hello world", "b" -> "Hello gds")
    val result = appliation.removeSpecialCharacters(apiMap)
    result should be (apiMap)
  }

  it should "remove defined special characters" in {
    val apiMap = Map ("a" -> "Hel<l>o world|", "b" -> "H<e>llo gd|s", "c" -> "\ttab Tabs	are here	\t")
    val expect = Map ("a" -> "Hello world", "b" -> "Hello gds", "c" -> "tab Tabs are here")
    val result = appliation.removeSpecialCharacters(apiMap)
    result should be (expect)
  }

  it should "remove trailing spaces" in {
    val apiMap = Map ("a" -> "", "b" -> " Hello gds ", "c" -> "\t	tab before and after	\t")
    val expect = Map ("a" -> "", "b" -> "Hello gds", "c" -> "tab before and after")
    val result = appliation.removeSpecialCharacters(apiMap)
    result should be (expect)
  }

}
