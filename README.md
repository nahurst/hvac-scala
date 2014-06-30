# Scala TDD Example

TDDing Uncle Bob's HVAC kata while picking up scala.

### Requirements:
1. if it's cold (less than 65 degrees) => heat
1. if it's hot (greater than 75 degrees) => cool
1. after the heater stops => run the fan for five more minutes
1. after cooler stops => don't turn it back on for three minutes

### To run
```bash
sbt test
```

or run this while tweaking the code to auto compile and test:

```bash
sbt
~ test
```