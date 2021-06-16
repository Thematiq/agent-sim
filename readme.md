# Agent-sim

Project for the Scala course at the AGH UST

---

## Table of contents

[Concept](#concept)

[Simulation parameters](#parameters)

[Installation](#sbt)

---

# Concept

This project aims to show the example usage of the _Scala Akka Actors_ for the multi-agent simulation. Each person is represented using _Patient_ object defining behaviours of the Akka actor.
# Parameters

The simulation engine accepts multiple parameters defined in the _Config_ case class. These are:

- infection rate - the probability of being infected after the interaction with the sick
- mortality rate - the probability of dying after the infection
- duration - mean and the standard deviation of the Gaussian distribution defining infection time
- severity - how hard are the symptoms of the infection (currently this only decreases the mobility of infected)
- mobility - how likely people are to move to the other nodes
- _sneezebility_ - how likely ill people are to _sneeze_ at other

# SBT

Agent-sim uses SBT as a build tool. To run the program type in the terminal:

`sbt run`
