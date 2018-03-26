package com.tradition.akkasocket.shared


trait Code
object Code {
  case object Heartbeat extends Code
  case object News extends Code
  case object Kill extends Code
  case object RandomKill extends Code
}
