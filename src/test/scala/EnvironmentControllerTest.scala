import org.scalatest.{Matchers, FlatSpec}

/*
environment controller
  tick

hvac
heat(bool)
cool(bool)
fan(bool)
getTemp

if it's cold (less than 65 degrees)
heat
if it's hot (greater than 75 degrees)
cool
after the heater stops
  run the fan for five more minutes
after cooler stops
don't turn it back on for three minutes

use matchers
hvacState match {
  case HVAC(heat, cool, fan, temp) if temp < 65 => heatUp
  case HVAC(heat, cool, fan, temp) if temp > 75 && !coolerRunRecently => coolDown
  case _ if heaterStoppedRecently => runFan
  case _ => doNothing

test with a spy of hvac instead of on hvac directly

do it immutable

magic numbers

refactor tests
}
*/

class HvacSpy(
      override val heat: Boolean,
      override val cool: Boolean,
      override val fan: Boolean,
      override val temperature: Double
) extends Hvac(heat, cool, fan, temperature)


class EnvironmentControllerTest extends FlatSpec with Matchers {
  "EnvironmentController" should "have tests" in {
    true should be === true
  }

  it should "heat when too cold" in {
    for (temperature <- 60 to 61) {
      val hvac = new HvacSpy(false, false, false, temperature)
      val envCntl = EnvironmentController(hvac)
      val hvacResult = envCntl.tick()
      hvacResult should have (
        'heat (true),
        'cool (false),
        'fan  (true),
        'temperature (temperature)
      )
    }
  }

  it should "cool when too hot" in {
    for (temperature <- 80 to 81) {
      val hvac = new HvacSpy(false, false, false, temperature)
      val envCntl = EnvironmentController(hvac)
      val hvacResult = envCntl.tick()
      hvacResult should have (
        'heat (false),
        'cool (true),
        'fan  (true),
        'temperature (temperature)
      )
    }
  }

  it should "not heat or cool when in ideal range and it wasn't heating or cooling" in {
    for (temperature <- List(65, 70, 75)) {
      val hvac = new HvacSpy(false, false, false, temperature)
      val envCntl = EnvironmentController(hvac)
      val hvacResult = envCntl.tick()
      hvacResult should have (
        'heat (false),
        'cool (false),
        'fan  (false),
        'temperature (temperature)
      )
    }
  }

  it should "not heat or cool when in ideal range and it was heating" in {
    for (temperature <- List(65, 70, 75)) {
      val hvac = new HvacSpy(true, false, false, temperature)
      val envCntl = EnvironmentController(hvac)
      val hvacResult = envCntl.tick()
      hvacResult should have (
        'heat (false),
        'cool (false),
        'fan  (false),
        'temperature (temperature)
      )
    }
  }

  it should "not heat or cool when in ideal range and it was cooling" in {
    for (temperature <- List(65, 70, 75)) {
      val hvac = new HvacSpy(false, true, false, temperature)
      val envCntl = EnvironmentController(hvac)
      val hvacResult = envCntl.tick()
      hvacResult should have (
        'heat (false),
        'cool (false),
        'fan  (false),
        'temperature (temperature)
      )
    }
  }

}
