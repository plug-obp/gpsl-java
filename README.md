# GPSL Language

![Build Status](https://github.com/plug-obp/gpsl-java/actions/workflows/build-and-publish.yml/badge.svg)
![Tests](https://img.shields.io/badge/tests-283%20passing-success)
![Java](https://img.shields.io/badge/java-23-blue)


**Generic Property Specification Language** (GPSL) is the language used by OBP2 for specifying the properties that should be verified during the analysis. Currently, it supports Linear Temporal Logic, Buchi Automata, and NFA specifications.

GPSL is **only** a *property-specification* language. Methodologically it is orthogonal to the formalisms used for *capturing the operational environment* (xGDL scenarios) and for *taming the state-space explosion* problem during model-checking (state-space decomposition, pruning through state-constraints - TLA, etc.).

The main characteristic of GPSL is its independence from the formalism used for model specification. To achieve this independence relation, the GPSL language delegates the evaluation of the atomic properties to the *verification model* semantics. Thus, from the perspective of the specification language, the atomic propositions are simply a mapping of names to booleans. In other words, the GPSL semantics binds the *property* to the *verification model* through a semantics-driven evaluation function.

In GPSL each property is associated with a named variable. All these variables form the *property set*
Any *property* can be verified during an analysis run.

More details on GPSL are available at <http://www.obpcdl.org/gpsl/>

## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fplug-obp%2Fgpsl-java.svg?type=large&issueType=license)](https://app.fossa.com/projects/git%2Bgithub.com%2Fplug-obp%2Fgpsl-java?ref=badge_large&issueType=license)