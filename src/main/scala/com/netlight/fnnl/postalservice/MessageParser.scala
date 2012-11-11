package com.netlight.fnnl.postalservice

sealed case class ParserResult
case class Sucessful(action: String, json:String) extends ParserResult
case class Unsucessful() extends ParserResult

object MessageParser {
  val MessagePattern = """^([a-zA-Z].*):\s+(\{.*\})""".r
  
  def parse(data: String): ParserResult = {
	  data match {
	    case MessagePattern(action, json) => Sucessful(action, json)
	    case _ => Unsucessful()
	  }
  }
}