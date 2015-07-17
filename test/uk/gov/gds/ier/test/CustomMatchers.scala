package uk.gov.gds.ier.test

import org.scalatest._
import matchers._
import scala.util.Try

trait CustomMatchers {

  class MapComparisonMatcher[A,B](expectedMap: Map[A,B]) extends Matcher[Map[A,B]] {
    def apply(leftMap: Map[A,B]) = {
      val keysNotInLeft = expectedMap.keySet.filterNot(key => leftMap.keySet.contains(key))
      val keysNotInExpected = leftMap.keys.toSet.filterNot(key => expectedMap.keySet.contains(key))
      val keysInBoth = (leftMap.keySet ++ expectedMap.keySet) -- (keysNotInLeft ++ keysNotInExpected)

      val messagesAfterNotInLeft = keysNotInLeft.foldLeft(List.empty[String]){ (messages, key) =>
        messages ++ List(s"\n$key was expected but not found in map")
      }
      val messagesAfterNotInExpected = keysNotInExpected.foldLeft(messagesAfterNotInLeft){ (messages, key) =>
        messages ++ List(s"\n$key was unexpected and found in map")
      }
      val errorMessages = keysInBoth.foldLeft(messagesAfterNotInExpected){ (messages, key) =>
        Try {
          val expected = expectedMap(key)
          val left = leftMap(key)
          if (expected == left) {
            messages
          } else {
            messages ++ List(s"\n($key -> $left) did not equal ($key -> $expected)")
          }
        }.getOrElse(messages)
      }

      MatchResult(
        errorMessages.isEmpty,
        s"${errorMessages.mkString(",")}",
        s"Map $leftMap matched $expectedMap"
      )
    }
  }

  def matchMap[A,B](expectedMap:Map[A,B]) = new MapComparisonMatcher(expectedMap)

}

// Make them easy to import with:
// import CustomMatchers._
object CustomMatchers extends CustomMatchers
