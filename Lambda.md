# Lambda

The purpose of this document is to describe a proposed language extension to Bantam Java, lambda expressions.

## Language Features Possible with this Extension

* Higher Order Functions
* Functions as first class citizens
* Currying (returning functions from functions)
* Closures (capturing variables)

## Implementation Details

* captured variables are passed by value
* parameter types explicit (formal statement)
* return type infered
* added as an expression to the grammar
* we choose to allow higher order lambdas

## Grammar

```
Lambda  :=  [ Closure* ] [[ ( Formal* ) ]] -*> identifier (( { Stmt* } ; || ReturnStmt ))
Closure :=  identifier,*
```

## Type Definition

```
ReturnType([_FormalType]*)
```
