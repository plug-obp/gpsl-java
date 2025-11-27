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

    public Hashable<SyntaxTreeElement> hashable() {
        return new Hashable<>() {
            @Override
            public boolean equal(SyntaxTreeElement x, SyntaxTreeElement y) {
                var lhs = intern.map().get(x);
                var rhs = intern.map().get(y);
                if (lhs != null && rhs != null) {
                    return lhs.tag() == rhs.tag();
                }
                //degrades to nameless equality if the terms are not hash consed
                return NamelessEquality.same(x, y);
            }

            @Override
            public int hash(SyntaxTreeElement x) {
                var hc = intern.map().get(x);
                if (hc != null) return hc.hashKey();
                //degrades to nameless hashcode if the term is not hash consed
                return NamelessHash.hashCode(x);
            }
        };
    }
 }
