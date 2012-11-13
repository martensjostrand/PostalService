package com.netlight.fnnl.postalservice
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class MessageParserTest extends FlatSpec with ShouldMatchers {
  "The MessageParser" should "accept correct messages" in {
      val messageType = "action"
      val messageArgs = """{"command": "walk", "args": ["arg1", "arg2"]}"""
      val message = messageType + ": " + messageArgs
      val actual = parse(message)
      val expected = Sucessful(messageType, messageArgs)
      actual should equal(expected)
  }
  it should "deny emty messages" in {
    val message = ""
    val actual = parse(message)
    val expected = Unsucessful()
    actual should equal(expected)
  }

  def parse(test: String): ParserResult = {
    MessageParser.parse(test)
  }
}