package com.netlight.fnnl.postalservice

import scala.util.parsing.json.JSONObject
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator

sealed case class MessageData()
@JsonCreator
case class ActionData(@JsonProperty("command") command: String, 
                      @JsonProperty("args") args: List[String]) extends MessageData

@JsonCreator
case class RegisterData(@JsonProperty("actions") actions: List[String]) extends MessageData

/*sealed case class Message()*/
sealed case class Message(data: MessageData, typeName: String)

/* action: {"command": "walk", "args":[arg1 arg2 ...]} */
case class Action(override val data: ActionData) extends Message(data, "action")

/* register: {"host": "192.168.10", "port": 9010, "actions": ["walk", "look"]} */
case class Register(override val data: RegisterData) extends Message(data, "register")

/* Special case */
case class Unknown extends Message(null, "unknown")
