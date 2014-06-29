import org.scalatest.{Matchers, FlatSpec}

/*
Requirements:
- if it's cold (less than 65 degrees) => heat
- if it's hot (greater than 75 degrees) => cool
- after the heater stops => run the fan for five more minutes
- after cooler stops => don't turn it back on for three minutes

use spy appropriately
  will be a problem because we create hvac so many times
  spy should allow writing temperature. others should not: use trait

refactor tests

how to fix the weird matcher "_ if"

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

  it should "not heat or cool when 65-75 deg and it was cooling" in {
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

  it should "not run cooler if run within 3 minutes" in {
    val hvac = new HvacSpy(false, false, false, 76)

    var envCntl = EnvironmentController(hvac)
    envCntl.tick() should have ( // 0 min
      'heat (false),
      'cool (true),
      'fan  (true),
      'temperature (76)
    )

    envCntl = envCntl.changeTemperature(74)
    envCntl.tick() should have ( // 1 min
      'heat (false),
      'cool (false),
      'fan  (false),
      'temperature (74)
    )

    envCntl = envCntl.changeTemperature(76)
    envCntl.tick() should have ( // 2 min
      'heat (false),
      'cool (false),
      'fan  (false),
      'temperature (76)
    )

    envCntl = envCntl.changeTemperature(76)
    envCntl.tick() should have ( // 3 min
      'heat (false),
      'cool (true),
      'fan  (true),
      'temperature (76)
    )
  }

  // don't think this applies
//  it should "continue running cooler if still too hot" in {
//    val hvac = new HvacSpy(false, false, false, 76)
//    var envCntl = EnvironmentController(hvac)
//    envCntl.tick() should have (
//      'heat (false),
//      'cool (true),
//      'fan  (true),
//      'temperature (76)
//    )
//
//    envCntl = envCntl.changeTemperature(76)
//    envCntl.tick() should have (
//      'heat (false),
//      'cool (true),
//      'fan  (true),
//      'temperature (76)
//    )
//  }

  it should "run fan for 5 minutes after heating" in {
    val hvac = new HvacSpy(false, false, false, 64)
    var envCntl = EnvironmentController(hvac)

    envCntl.tick() should have ( // 0 min
      'heat (true),
      'cool (false),
      'fan  (true),
      'temperature (64)
    )

    for (i <- 1 to 4) {
      envCntl = envCntl.changeTemperature(66)
      envCntl.tick() should have ( // 1-4 min
        'heat (false),
        'cool (false),
        'fan  (true),
        'temperature (66)
      )
    }

    envCntl = envCntl.changeTemperature(66)
    envCntl.tick() should have ( // 1-4 min
      'heat (false),
      'cool (false),
      'fan  (false),
      'temperature (66)
    )
  }

  it should "continue to run fan after heating even if cooling soon after" in {
    val hvac = new HvacSpy(false, false, false, 64)
    var envCntl = EnvironmentController(hvac)

    envCntl.tick() should have ( // 0 min
      'heat (true),
      'cool (false),
      'fan  (true),
      'temperature (64)
    )

    envCntl = envCntl.changeTemperature(66)
    envCntl.tick() should have ( // 1 min
      'heat (false),
      'cool (false),
      'fan  (true),
      'temperature (66)
    )

    envCntl = envCntl.changeTemperature(76)
    envCntl.tick() should have ( // 2 min
      'heat (false),
      'cool (true),
      'fan  (true),
      'temperature (76)
    )

    for (i <- 3 to 4) {
      envCntl = envCntl.changeTemperature(74)
      envCntl.tick() should have ( // 3-4 min
        'heat (false),
        'cool (false),
        'fan  (true),
        'temperature (74)
      )
    }

    envCntl = envCntl.changeTemperature(74)
    envCntl.tick() should have ( // 5 min
      'heat (false),
      'cool (false),
      'fan  (false),
      'temperature (74)
    )

  }


}
