import java.util.Observable

case class Hvac(
  heat: Boolean,
  cool: Boolean,
  fan: Boolean,
  temperature: Double
)


case class EnvironmentController(var hvac: Hvac) {


  def tick() : Hvac = {
    if (preventCoolingCounter > 0) {
      preventCoolingCounter -= 1
    }

    hvac match {
      case Hvac(_, _, _, temperature) if temperature < 65 => heatUp
      case Hvac(_, _, _, temperature) if temperature > 75 && !cooledRecently => coolDown
      case _ => turnEverythingOff
    }
  }

  def heatUp() : Hvac = {
    hvac = Hvac(true, false, true, hvac.temperature)
    hvac
  }

  var preventCoolingCounter = 0
  def cooledRecently() : Boolean = preventCoolingCounter > 0
  def coolDown() : Hvac = {
    hvac = Hvac(false, true, true, hvac.temperature)
    preventCoolingCounter = 3

    hvac
  }

  def turnEverythingOff() : Hvac = {
    hvac = Hvac(false, false, false, hvac.temperature)
    hvac
  }

  def changeTemperature(newTemperature: Double) : EnvironmentController = {
    hvac = hvac.copy(temperature = newTemperature)
    this
  }
}
