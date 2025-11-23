package gpsl.syntax.hashcons;

import gpsl.syntax.model.Factory;
import gpsl.syntax.model.SyntaxTreeElement;
import obp3.hashcons.HashConsTable;
import obp3.hashcons.HashConsed;
import obp3.utils.Hashable;

public class HashConsingFactory extends Factory {
    HashConsTable<SyntaxTreeElement> intern;

    public HashConsingFactory() {
        this(new HashConsTable<>(
                Hashable.from(NamelessEquality::same, NamelessHash::hashCode),
                HashConsedElement::new));
    }

    public HashConsingFactory(HashConsTable<SyntaxTreeElement> initialTable) {
        this.intern = initialTable;
    }

    record HashConsedElement(SyntaxTreeElement node, int tag, int hashKey) implements HashConsed<SyntaxTreeElement> {
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof HashConsedElement other)) return false;
            return this.tag == other.tag;
        }

        @Override
        public int hashCode() {
            return hashKey;
        }
    }

    public SyntaxTreeElement hc(SyntaxTreeElement term) {
        return intern.hashCons(term).node();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends SyntaxTreeElement> T wrap(T term) {
        return (T) hc(term);
    }
 }
