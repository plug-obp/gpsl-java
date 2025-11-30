package gpsl.syntax.dimacs_cnf;

import gpsl.syntax.model.Expression;
import gpsl.syntax.model.Factory;
import gpsl.syntax.model.SyntaxTreeElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DIMACS CNF is the standard file format used for representing Boolean satisfiability (SAT) problems in Conjunctive Normal Form (CNF). Here's a complete description:
 *
 * Basic Structure
 * A DIMACS CNF file has two main parts:
 *
 * Header line starting with p cnf
 *
 * Clause lines representing the Boolean formula
 *
 * File Format Specification
 * Header Line
 * text
 * p cnf <variables> <clauses>
 * p - problem definition
 *
 * cnf - format type (conjunctive normal form)
 *
 * <variables> - number of Boolean variables
 *
 * <clauses> - number of clauses
 *
 * Clause Lines
 * Each clause is a disjunction (OR) of literals
 *
 * Ends with 0 (zero)
 *
 * Positive literal: variable number
 *
 * Negative literal: negative variable number
 *
 * Example 1: Simple SAT Problem
 * dimacs
 * c This is a comment
 * c 3 variables, 3 clauses
 * p cnf 3 3
 * 1 -2 0
 * -1 3 0
 * -3 2 0
 * Meaning:
 *
 * Variables: 1, 2, 3
 *
 * Formula: (x₁ ∨ ¬x₂) ∧ (¬x₁ ∨ x₃) ∧ (¬x₃ ∨ x₂)
 *
 * Example 2: Unsatisfiable Problem
 * dimacs
 * c This is UNSAT
 * p cnf 2 4
 * 1 2 0
 * 1 -2 0
 * -1 2 0
 * -1 -2 0
 * Meaning:
 * (x₁ ∨ x₂) ∧ (x₁ ∨ ¬x₂) ∧ (¬x₁ ∨ x₂) ∧ (¬x₁ ∨ ¬x₂) = UNSAT
 *
 * Detailed Syntax Rules
 * Comments
 * Any line starting with c is a comment
 *
 * Comments can appear anywhere in the file
 *
 * dimacs
 * c This file represents a SAT instance
 * c Created: 2024
 * c Variables: x1, x2, x3 correspond to positions 1, 2, 3
 * p cnf 3 2
 * c First clause: x1 OR NOT x2
 * 1 -2 0
 * c Second clause: NOT x1 OR x3
 * -1 3 0
 * Variable Numbering
 * Variables are positive integers starting from 1
 *
 * No gaps required (can use 1, 3, 5 if desired)
 *
 * Maximum variable number should match header
 *
 * Clause Rules
 * Empty clause (just 0) represents FALSE
 *
 * Unit clause (single literal + 0) represents that literal must be true
 *
 * Multiple literals per clause represent disjunction (OR)
 *
 *
 * variables map to gpsl atoms, so 1 -> atom(value: "1")
 */

public class Reader {
    private final Factory factory;

    public Reader(Factory factory) {
        this.factory = factory;
    }

    public Reader() {
        this(Factory.instance());
    }

    public SyntaxTreeElement read(java.io.Reader source) throws IOException {
        var buffered = new BufferedReader(source);
        var clauses = new ArrayList<List<Integer>>();
        boolean headerSeen = false;
        int declaredVariables = -1;
        int declaredClauses = -1;
        var currentClause = new ArrayList<Integer>();

        String line;
        while ((line = buffered.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("c")) {
                continue;
            }
            if (line.startsWith("p")) {
                String[] parts = line.split("\\s+");
                if (parts.length < 4 || !"cnf".equals(parts[1])) {
                    throw new IllegalArgumentException("Invalid DIMACS header: " + line);
                }
                declaredVariables = Integer.parseInt(parts[2]);
                declaredClauses = Integer.parseInt(parts[3]);
                headerSeen = true;
                continue;
            }
            if (!headerSeen) {
                throw new IllegalStateException("DIMACS header must appear before clauses");
            }
            for (String token : line.split("\\s+")) {
                if (token.isEmpty()) {
                    continue;
                }
                int value = Integer.parseInt(token);
                if (value == 0) {
                    clauses.add(List.copyOf(currentClause));
                    currentClause.clear();
                } else {
                    currentClause.add(value);
                }
            }
        }

        if (!currentClause.isEmpty()) {
            throw new IllegalArgumentException("Clause not terminated with 0");
        }
        if (declaredClauses >= 0 && clauses.size() != declaredClauses) {
            throw new IllegalArgumentException("Clause count mismatch: expected " + declaredClauses + " but read " + clauses.size());
        }
        validateVariableUsage(clauses, declaredVariables);
        return clausesToExpression(clauses);
    }

    private void validateVariableUsage(List<List<Integer>> clauses, int declaredVariables) {
        if (declaredVariables < 0) {
            return;
        }
        int maxVar = clauses.stream()
                .flatMap(List::stream)
                .mapToInt(Math::abs)
                .max()
                .orElse(0);
        if (maxVar > declaredVariables) {
            throw new IllegalArgumentException("Literal uses undeclared variable " + maxVar);
        }
    }

    private Expression clausesToExpression(List<List<Integer>> clauses) {
        if (clauses.isEmpty()) {
            return factory.t();
        }
        Expression formula = clauseToExpression(clauses.get(0));
        for (int i = 1; i < clauses.size(); i++) {
            formula = factory.conjunction(formula, clauseToExpression(clauses.get(i)));
        }
        return formula;
    }

    private Expression clauseToExpression(List<Integer> clause) {
        if (clause.isEmpty()) {
            return factory.f();
        }
        Expression expr = literalToExpression(clause.get(0));
        for (int i = 1; i < clause.size(); i++) {
            expr = factory.disjunction(expr, literalToExpression(clause.get(i)));
        }
        return expr;
    }

    private Expression literalToExpression(int literal) {
        String atomName = "x" + Math.abs(literal);
        Expression atom = factory.atom(atomName);
        return literal > 0 ? atom : factory.negation(atom);
    }
}
