package com.netlight.fnnl.postalservice
import org.slf4j.LoggerFactory

sealed case class ParserResult
case class Sucessful(action: String, json: String) extends ParserResult
case class Unsucessful() extends ParserResult

object MessageParser {
  val MessagePattern = """^([a-zA-Z].*):\s+(\{.*\})\r?\n?$""".r
  val log = LoggerFactory.getLogger("com.netlight.fnnl.postalservice.MessageParser");
  def parse(data: String): ParserResult = {
    log.debug("About to parse message: '{}'", data);
    data match {
      case MessagePattern(action, json) => Sucessful(action, json)
      case _ => {
        log.warn("'{}' does not match reg exp '{}'", data, MessagePattern.toString())
        Unsucessful()
      }
    }
  }
}
