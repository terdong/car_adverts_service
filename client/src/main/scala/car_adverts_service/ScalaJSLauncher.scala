package car_adverts_service

import car_adverts_service.routes._
import org.scalajs.dom._
import RouteManager._

/**
  * Created by DongHee Kim on 2019-05-09 오후 3:18.
  */

object ScalaJSLauncher{

  def main(args :Array[String]): Unit = {
/*    console.log(window.location.hash)
    console.log(window.location.pathname)*/
    registerRoute(Global)
    registerRoute(Home)
    registerRoute(Create)
    registerRoute(Fuels)

    runRoute(window.location.pathname);
  }
}
