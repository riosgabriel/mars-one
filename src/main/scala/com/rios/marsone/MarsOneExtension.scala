package com.rios.marsone

import akka.actor.{ ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider }
import com.typesafe.config.Config

class MarsOneExtension(config: Config) extends Extension {
  val marsOneSettings = new MarsOneSettings(config)
}

object MarsOneExtension extends ExtensionId[MarsOneExtension] with ExtensionIdProvider {

  override def createExtension(system: ExtendedActorSystem): MarsOneExtension =
    new MarsOneExtension(system.settings.config)

  override def lookup(): ExtensionId[_ <: Extension] = MarsOneExtension
}

case class MarsOneSettings(http: HttpSettings) {

  def this(config: Config) {
    this(
      http = new HttpSettings(config.getConfig("http"))
    )
  }
}

case class HttpSettings(interface: String, port: Int) {

  def this(config: Config) {
    this(
      interface = config.getString("interface"),
      port = config.getInt("port")
    )
  }
}
