module language.gpsl.modelchecker {
    requires transitive obp.sli.runtime;
    requires language.gpsl.core;
    requires reader.infra;
    requires obp.algos;
    exports gpsl.modelchecker;
}