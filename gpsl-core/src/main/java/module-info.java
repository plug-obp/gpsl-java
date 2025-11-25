module language.gpsl.core {
    requires org.antlr.antlr4.runtime;
    requires transitive obp.sli.runtime;
    requires reader.infra;
    requires language.gpsl.ltl3ba;
    exports gpsl.parser;
    exports gpsl.syntax;
    exports gpsl.semantics;
    exports gpsl.syntax.model;
}