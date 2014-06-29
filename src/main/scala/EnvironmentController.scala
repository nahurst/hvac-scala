import java.util.Observable

case class Hvac(
  heat: Boolean,
  cool: Boolean,
  fan: Boolean,
  temperature: Double
)


case class EnvironmentController(hvac: Hvac) {

  def tick() : Hvac = hvac match {
    case Hvac(_, _, _, temperature) if temperature < 65 => heatUp
    case Hvac(_, _, _, temperature) if temperature > 75 => coolDown
    case _ => turnEverythingOff
  }

  def heatUp() : Hvac = {
    Hvac(true, false, true, hvac.temperature)
  }

  def coolDown() : Hvac = {
    Hvac(false, true, true, hvac.temperature)
  }

  def turnEverythingOff() : Hvac = {
    Hvac(false, false, false, hvac.temperature)
  }
}
