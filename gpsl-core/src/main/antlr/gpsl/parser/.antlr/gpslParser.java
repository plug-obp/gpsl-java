// Generated from /Users/ciprian/Playfield/repositories/gpsl-java/gpsl-core/src/main/antlr/gpsl/parser/gpsl.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class gpslParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, CONJUNCTION=3, DISJUNCTION=4, EQUIVALENCE=5, EVENTUALLY=6, 
		FALSE=7, GLOBALLY=8, IMPLICATION=9, IN=10, LET=11, NEGATION=12, NEXT=13, 
		SUNTIL=14, WUNTIL=15, SRELEASE=16, WRELEASE=17, TRUE=18, XOR=19, NFA=20, 
		BUCHI=21, STATES=22, INITIAL=23, ACCEPT=24, ATOMINLINE=25, PIPEATOM=26, 
		QUOTEATOM=27, IDENTIFIER=28, NATURAL=29, EQ=30, SEQ=31, COMMA=32, SEMICOLON=33, 
		LPAREN=34, RPAREN=35, LINE_COMMENT=36, COMMENT=37, WS=38;
	public static final int
		RULE_block = 0, RULE_formulaDeclaration = 1, RULE_formulaDeclarationList = 2, 
		RULE_formula = 3, RULE_literal = 4, RULE_atom = 5, RULE_letDecl = 6, RULE_stateDecl = 7, 
		RULE_initialDecl = 8, RULE_acceptDecl = 9, RULE_transitionDecl = 10, RULE_automatonDecl = 11, 
		RULE_automaton = 12, RULE_reserved = 13;
	private static String[] makeRuleNames() {
		return new String[] {
			"block", "formulaDeclaration", "formulaDeclarationList", "formula", "literal", 
			"atom", "letDecl", "stateDecl", "initialDecl", "acceptDecl", "transitionDecl", 
			"automatonDecl", "automaton", "reserved"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'['", "']'", null, null, null, null, null, null, null, "'in'", 
			null, null, null, null, null, null, null, null, null, "'nfa'", "'buchi'", 
			"'states'", "'initial'", "'accept'", null, null, null, null, null, "'='", 
			"'*='", "','", "';'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "CONJUNCTION", "DISJUNCTION", "EQUIVALENCE", "EVENTUALLY", 
			"FALSE", "GLOBALLY", "IMPLICATION", "IN", "LET", "NEGATION", "NEXT", 
			"SUNTIL", "WUNTIL", "SRELEASE", "WRELEASE", "TRUE", "XOR", "NFA", "BUCHI", 
			"STATES", "INITIAL", "ACCEPT", "ATOMINLINE", "PIPEATOM", "QUOTEATOM", 
			"IDENTIFIER", "NATURAL", "EQ", "SEQ", "COMMA", "SEMICOLON", "LPAREN", 
			"RPAREN", "LINE_COMMENT", "COMMENT", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "gpsl.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public gpslParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BlockContext extends ParserRuleContext {
		public List<FormulaDeclarationContext> formulaDeclaration() {
			return getRuleContexts(FormulaDeclarationContext.class);
		}
		public FormulaDeclarationContext formulaDeclaration(int i) {
			return getRuleContext(FormulaDeclarationContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(29); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(28);
				formulaDeclaration();
				}
				}
				setState(31); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FormulaDeclarationContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(gpslParser.IDENTIFIER, 0); }
		public TerminalNode SEQ() { return getToken(gpslParser.SEQ, 0); }
		public TerminalNode EQ() { return getToken(gpslParser.EQ, 0); }
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public AutomatonContext automaton() {
			return getRuleContext(AutomatonContext.class,0);
		}
		public FormulaDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formulaDeclaration; }
	}

	public final FormulaDeclarationContext formulaDeclaration() throws RecognitionException {
		FormulaDeclarationContext _localctx = new FormulaDeclarationContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_formulaDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(33);
			match(IDENTIFIER);
			setState(34);
			_la = _input.LA(1);
			if ( !(_la==EQ || _la==SEQ) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(37);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(35);
				formula(0);
				}
				break;
			case 2:
				{
				setState(36);
				automaton();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FormulaDeclarationListContext extends ParserRuleContext {
		public List<FormulaDeclarationContext> formulaDeclaration() {
			return getRuleContexts(FormulaDeclarationContext.class);
		}
		public FormulaDeclarationContext formulaDeclaration(int i) {
			return getRuleContext(FormulaDeclarationContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(gpslParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(gpslParser.COMMA, i);
		}
		public FormulaDeclarationListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formulaDeclarationList; }
	}

	public final FormulaDeclarationListContext formulaDeclarationList() throws RecognitionException {
		FormulaDeclarationListContext _localctx = new FormulaDeclarationListContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_formulaDeclarationList);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(39);
			formulaDeclaration();
			setState(44);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(40);
					match(COMMA);
					setState(41);
					formulaDeclaration();
					}
					} 
				}
				setState(46);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			setState(48);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(47);
				match(COMMA);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FormulaContext extends ParserRuleContext {
		public FormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula; }
	 
		public FormulaContext() { }
		public void copyFrom(FormulaContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LiteralExpContext extends FormulaContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public LiteralExpContext(FormulaContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ReferenceExpContext extends FormulaContext {
		public TerminalNode IDENTIFIER() { return getToken(gpslParser.IDENTIFIER, 0); }
		public ReferenceExpContext(FormulaContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParenExpContext extends FormulaContext {
		public TerminalNode LPAREN() { return getToken(gpslParser.LPAREN, 0); }
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(gpslParser.RPAREN, 0); }
		public ParenExpContext(FormulaContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LetExpContext extends FormulaContext {
		public LetDeclContext letDecl() {
			return getRuleContext(LetDeclContext.class,0);
		}
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public LetExpContext(FormulaContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AtomExpContext extends FormulaContext {
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public AtomExpContext(FormulaContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class UnaryExpContext extends FormulaContext {
		public Token operator;
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public TerminalNode NEGATION() { return getToken(gpslParser.NEGATION, 0); }
		public TerminalNode NEXT() { return getToken(gpslParser.NEXT, 0); }
		public TerminalNode EVENTUALLY() { return getToken(gpslParser.EVENTUALLY, 0); }
		public TerminalNode GLOBALLY() { return getToken(gpslParser.GLOBALLY, 0); }
		public UnaryExpContext(FormulaContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BinaryExpContext extends FormulaContext {
		public Token operator;
		public List<FormulaContext> formula() {
			return getRuleContexts(FormulaContext.class);
		}
		public FormulaContext formula(int i) {
			return getRuleContext(FormulaContext.class,i);
		}
		public TerminalNode SUNTIL() { return getToken(gpslParser.SUNTIL, 0); }
		public TerminalNode WUNTIL() { return getToken(gpslParser.WUNTIL, 0); }
		public TerminalNode SRELEASE() { return getToken(gpslParser.SRELEASE, 0); }
		public TerminalNode WRELEASE() { return getToken(gpslParser.WRELEASE, 0); }
		public TerminalNode CONJUNCTION() { return getToken(gpslParser.CONJUNCTION, 0); }
		public TerminalNode DISJUNCTION() { return getToken(gpslParser.DISJUNCTION, 0); }
		public TerminalNode XOR() { return getToken(gpslParser.XOR, 0); }
		public TerminalNode IMPLICATION() { return getToken(gpslParser.IMPLICATION, 0); }
		public TerminalNode EQUIVALENCE() { return getToken(gpslParser.EQUIVALENCE, 0); }
		public BinaryExpContext(FormulaContext ctx) { copyFrom(ctx); }
	}

	public final FormulaContext formula() throws RecognitionException {
		return formula(0);
	}

	private FormulaContext formula(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		FormulaContext _localctx = new FormulaContext(_ctx, _parentState);
		FormulaContext _prevctx = _localctx;
		int _startState = 6;
		enterRecursionRule(_localctx, 6, RULE_formula, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FALSE:
			case TRUE:
				{
				_localctx = new LiteralExpContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(51);
				literal();
				}
				break;
			case IDENTIFIER:
				{
				_localctx = new ReferenceExpContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(52);
				match(IDENTIFIER);
				}
				break;
			case ATOMINLINE:
				{
				_localctx = new AtomExpContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(53);
				atom();
				}
				break;
			case LPAREN:
				{
				_localctx = new ParenExpContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(54);
				match(LPAREN);
				setState(55);
				formula(0);
				setState(56);
				match(RPAREN);
				}
				break;
			case NEGATION:
				{
				_localctx = new UnaryExpContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(58);
				((UnaryExpContext)_localctx).operator = match(NEGATION);
				setState(59);
				formula(9);
				}
				break;
			case NEXT:
				{
				_localctx = new UnaryExpContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(60);
				((UnaryExpContext)_localctx).operator = match(NEXT);
				setState(61);
				formula(8);
				}
				break;
			case EVENTUALLY:
			case GLOBALLY:
				{
				_localctx = new UnaryExpContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(62);
				((UnaryExpContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==EVENTUALLY || _la==GLOBALLY) ) {
					((UnaryExpContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(63);
				formula(7);
				}
				break;
			case LET:
				{
				_localctx = new LetExpContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(64);
				letDecl();
				setState(65);
				formula(1);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(86);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(84);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryExpContext(new FormulaContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(69);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(70);
						((BinaryExpContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 245760L) != 0)) ) {
							((BinaryExpContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(71);
						formula(6);
						}
						break;
					case 2:
						{
						_localctx = new BinaryExpContext(new FormulaContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(72);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(73);
						((BinaryExpContext)_localctx).operator = match(CONJUNCTION);
						setState(74);
						formula(6);
						}
						break;
					case 3:
						{
						_localctx = new BinaryExpContext(new FormulaContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(75);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(76);
						((BinaryExpContext)_localctx).operator = match(DISJUNCTION);
						setState(77);
						formula(5);
						}
						break;
					case 4:
						{
						_localctx = new BinaryExpContext(new FormulaContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(78);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(79);
						((BinaryExpContext)_localctx).operator = match(XOR);
						setState(80);
						formula(4);
						}
						break;
					case 5:
						{
						_localctx = new BinaryExpContext(new FormulaContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(81);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(82);
						((BinaryExpContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==EQUIVALENCE || _la==IMPLICATION) ) {
							((BinaryExpContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(83);
						formula(2);
						}
						break;
					}
					} 
				}
				setState(88);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(gpslParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(gpslParser.FALSE, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			_la = _input.LA(1);
			if ( !(_la==FALSE || _la==TRUE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AtomContext extends ParserRuleContext {
		public TerminalNode ATOMINLINE() { return getToken(gpslParser.ATOMINLINE, 0); }
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_atom);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			match(ATOMINLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LetDeclContext extends ParserRuleContext {
		public TerminalNode LET() { return getToken(gpslParser.LET, 0); }
		public FormulaDeclarationListContext formulaDeclarationList() {
			return getRuleContext(FormulaDeclarationListContext.class,0);
		}
		public TerminalNode IN() { return getToken(gpslParser.IN, 0); }
		public LetDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_letDecl; }
	}

	public final LetDeclContext letDecl() throws RecognitionException {
		LetDeclContext _localctx = new LetDeclContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_letDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			match(LET);
			setState(94);
			formulaDeclarationList();
			setState(95);
			match(IN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StateDeclContext extends ParserRuleContext {
		public TerminalNode STATES() { return getToken(gpslParser.STATES, 0); }
		public List<TerminalNode> IDENTIFIER() { return getTokens(gpslParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(gpslParser.IDENTIFIER, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(gpslParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(gpslParser.COMMA, i);
		}
		public StateDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stateDecl; }
	}

	public final StateDeclContext stateDecl() throws RecognitionException {
		StateDeclContext _localctx = new StateDeclContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_stateDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			match(STATES);
			setState(98);
			match(IDENTIFIER);
			setState(103);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(99);
				match(COMMA);
				setState(100);
				match(IDENTIFIER);
				}
				}
				setState(105);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InitialDeclContext extends ParserRuleContext {
		public TerminalNode INITIAL() { return getToken(gpslParser.INITIAL, 0); }
		public List<TerminalNode> IDENTIFIER() { return getTokens(gpslParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(gpslParser.IDENTIFIER, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(gpslParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(gpslParser.COMMA, i);
		}
		public InitialDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_initialDecl; }
	}

	public final InitialDeclContext initialDecl() throws RecognitionException {
		InitialDeclContext _localctx = new InitialDeclContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_initialDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			match(INITIAL);
			setState(107);
			match(IDENTIFIER);
			setState(112);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(108);
				match(COMMA);
				setState(109);
				match(IDENTIFIER);
				}
				}
				setState(114);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AcceptDeclContext extends ParserRuleContext {
		public TerminalNode ACCEPT() { return getToken(gpslParser.ACCEPT, 0); }
		public List<TerminalNode> IDENTIFIER() { return getTokens(gpslParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(gpslParser.IDENTIFIER, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(gpslParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(gpslParser.COMMA, i);
		}
		public AcceptDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_acceptDecl; }
	}

	public final AcceptDeclContext acceptDecl() throws RecognitionException {
		AcceptDeclContext _localctx = new AcceptDeclContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_acceptDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			match(ACCEPT);
			setState(116);
			match(IDENTIFIER);
			setState(121);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(117);
				match(COMMA);
				setState(118);
				match(IDENTIFIER);
				}
				}
				setState(123);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TransitionDeclContext extends ParserRuleContext {
		public Token priority;
		public List<TerminalNode> IDENTIFIER() { return getTokens(gpslParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(gpslParser.IDENTIFIER, i);
		}
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public TerminalNode FALSE() { return getToken(gpslParser.FALSE, 0); }
		public TerminalNode TRUE() { return getToken(gpslParser.TRUE, 0); }
		public TerminalNode NATURAL() { return getToken(gpslParser.NATURAL, 0); }
		public TransitionDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_transitionDecl; }
	}

	public final TransitionDeclContext transitionDecl() throws RecognitionException {
		TransitionDeclContext _localctx = new TransitionDeclContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_transitionDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(124);
			match(IDENTIFIER);
			setState(126);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 537133184L) != 0)) {
				{
				setState(125);
				((TransitionDeclContext)_localctx).priority = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 537133184L) != 0)) ) {
					((TransitionDeclContext)_localctx).priority = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(128);
			match(T__0);
			setState(129);
			formula(0);
			setState(130);
			match(T__1);
			setState(131);
			match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AutomatonDeclContext extends ParserRuleContext {
		public StateDeclContext stateDecl() {
			return getRuleContext(StateDeclContext.class,0);
		}
		public List<TerminalNode> SEMICOLON() { return getTokens(gpslParser.SEMICOLON); }
		public TerminalNode SEMICOLON(int i) {
			return getToken(gpslParser.SEMICOLON, i);
		}
		public InitialDeclContext initialDecl() {
			return getRuleContext(InitialDeclContext.class,0);
		}
		public AcceptDeclContext acceptDecl() {
			return getRuleContext(AcceptDeclContext.class,0);
		}
		public List<TransitionDeclContext> transitionDecl() {
			return getRuleContexts(TransitionDeclContext.class);
		}
		public TransitionDeclContext transitionDecl(int i) {
			return getRuleContext(TransitionDeclContext.class,i);
		}
		public TerminalNode NFA() { return getToken(gpslParser.NFA, 0); }
		public TerminalNode BUCHI() { return getToken(gpslParser.BUCHI, 0); }
		public AutomatonDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_automatonDecl; }
	}

	public final AutomatonDeclContext automatonDecl() throws RecognitionException {
		AutomatonDeclContext _localctx = new AutomatonDeclContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_automatonDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NFA || _la==BUCHI) {
				{
				setState(133);
				_la = _input.LA(1);
				if ( !(_la==NFA || _la==BUCHI) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(136);
			stateDecl();
			setState(137);
			match(SEMICOLON);
			setState(138);
			initialDecl();
			setState(139);
			match(SEMICOLON);
			setState(140);
			acceptDecl();
			setState(141);
			match(SEMICOLON);
			setState(142);
			transitionDecl();
			setState(147);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEMICOLON) {
				{
				{
				setState(143);
				match(SEMICOLON);
				setState(144);
				transitionDecl();
				}
				}
				setState(149);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AutomatonContext extends ParserRuleContext {
		public AutomatonDeclContext automatonDecl() {
			return getRuleContext(AutomatonDeclContext.class,0);
		}
		public LetDeclContext letDecl() {
			return getRuleContext(LetDeclContext.class,0);
		}
		public AutomatonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_automaton; }
	}

	public final AutomatonContext automaton() throws RecognitionException {
		AutomatonContext _localctx = new AutomatonContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_automaton);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LET) {
				{
				setState(150);
				letDecl();
				}
			}

			setState(153);
			automatonDecl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ReservedContext extends ParserRuleContext {
		public TerminalNode CONJUNCTION() { return getToken(gpslParser.CONJUNCTION, 0); }
		public TerminalNode DISJUNCTION() { return getToken(gpslParser.DISJUNCTION, 0); }
		public TerminalNode EQUIVALENCE() { return getToken(gpslParser.EQUIVALENCE, 0); }
		public TerminalNode EVENTUALLY() { return getToken(gpslParser.EVENTUALLY, 0); }
		public TerminalNode FALSE() { return getToken(gpslParser.FALSE, 0); }
		public TerminalNode GLOBALLY() { return getToken(gpslParser.GLOBALLY, 0); }
		public TerminalNode IMPLICATION() { return getToken(gpslParser.IMPLICATION, 0); }
		public TerminalNode IN() { return getToken(gpslParser.IN, 0); }
		public TerminalNode LET() { return getToken(gpslParser.LET, 0); }
		public TerminalNode NEGATION() { return getToken(gpslParser.NEGATION, 0); }
		public TerminalNode NEXT() { return getToken(gpslParser.NEXT, 0); }
		public TerminalNode SUNTIL() { return getToken(gpslParser.SUNTIL, 0); }
		public TerminalNode WUNTIL() { return getToken(gpslParser.WUNTIL, 0); }
		public TerminalNode SRELEASE() { return getToken(gpslParser.SRELEASE, 0); }
		public TerminalNode WRELEASE() { return getToken(gpslParser.WRELEASE, 0); }
		public TerminalNode TRUE() { return getToken(gpslParser.TRUE, 0); }
		public TerminalNode XOR() { return getToken(gpslParser.XOR, 0); }
		public ReservedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_reserved; }
	}

	public final ReservedContext reserved() throws RecognitionException {
		ReservedContext _localctx = new ReservedContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_reserved);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1048568L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 3:
			return formula_sempred((FormulaContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean formula_sempred(FormulaContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 6);
		case 1:
			return precpred(_ctx, 5);
		case 2:
			return precpred(_ctx, 4);
		case 3:
			return precpred(_ctx, 3);
		case 4:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001&\u009e\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0001\u0000\u0004\u0000\u001e\b\u0000\u000b"+
		"\u0000\f\u0000\u001f\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003"+
		"\u0001&\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002+\b\u0002"+
		"\n\u0002\f\u0002.\t\u0002\u0001\u0002\u0003\u00021\b\u0002\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003D\b\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003U\b\u0003\n\u0003\f\u0003"+
		"X\t\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0005\u0007f\b\u0007\n\u0007\f\u0007i\t\u0007\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0005\bo\b\b\n\b\f\br\t\b\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0005\tx\b\t\n\t\f\t{\t\t\u0001\n\u0001\n\u0003\n\u007f\b\n\u0001\n"+
		"\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0003\u000b\u0087\b\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0005\u000b\u0092\b\u000b\n\u000b"+
		"\f\u000b\u0095\t\u000b\u0001\f\u0003\f\u0098\b\f\u0001\f\u0001\f\u0001"+
		"\r\u0001\r\u0001\r\u0000\u0001\u0006\u000e\u0000\u0002\u0004\u0006\b\n"+
		"\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u0000\b\u0001\u0000\u001e"+
		"\u001f\u0002\u0000\u0006\u0006\b\b\u0001\u0000\u000e\u0011\u0002\u0000"+
		"\u0005\u0005\t\t\u0002\u0000\u0007\u0007\u0012\u0012\u0003\u0000\u0007"+
		"\u0007\u0012\u0012\u001d\u001d\u0001\u0000\u0014\u0015\u0001\u0000\u0003"+
		"\u0013\u00a6\u0000\u001d\u0001\u0000\u0000\u0000\u0002!\u0001\u0000\u0000"+
		"\u0000\u0004\'\u0001\u0000\u0000\u0000\u0006C\u0001\u0000\u0000\u0000"+
		"\bY\u0001\u0000\u0000\u0000\n[\u0001\u0000\u0000\u0000\f]\u0001\u0000"+
		"\u0000\u0000\u000ea\u0001\u0000\u0000\u0000\u0010j\u0001\u0000\u0000\u0000"+
		"\u0012s\u0001\u0000\u0000\u0000\u0014|\u0001\u0000\u0000\u0000\u0016\u0086"+
		"\u0001\u0000\u0000\u0000\u0018\u0097\u0001\u0000\u0000\u0000\u001a\u009b"+
		"\u0001\u0000\u0000\u0000\u001c\u001e\u0003\u0002\u0001\u0000\u001d\u001c"+
		"\u0001\u0000\u0000\u0000\u001e\u001f\u0001\u0000\u0000\u0000\u001f\u001d"+
		"\u0001\u0000\u0000\u0000\u001f \u0001\u0000\u0000\u0000 \u0001\u0001\u0000"+
		"\u0000\u0000!\"\u0005\u001c\u0000\u0000\"%\u0007\u0000\u0000\u0000#&\u0003"+
		"\u0006\u0003\u0000$&\u0003\u0018\f\u0000%#\u0001\u0000\u0000\u0000%$\u0001"+
		"\u0000\u0000\u0000&\u0003\u0001\u0000\u0000\u0000\',\u0003\u0002\u0001"+
		"\u0000()\u0005 \u0000\u0000)+\u0003\u0002\u0001\u0000*(\u0001\u0000\u0000"+
		"\u0000+.\u0001\u0000\u0000\u0000,*\u0001\u0000\u0000\u0000,-\u0001\u0000"+
		"\u0000\u0000-0\u0001\u0000\u0000\u0000.,\u0001\u0000\u0000\u0000/1\u0005"+
		" \u0000\u00000/\u0001\u0000\u0000\u000001\u0001\u0000\u0000\u00001\u0005"+
		"\u0001\u0000\u0000\u000023\u0006\u0003\uffff\uffff\u00003D\u0003\b\u0004"+
		"\u00004D\u0005\u001c\u0000\u00005D\u0003\n\u0005\u000067\u0005\"\u0000"+
		"\u000078\u0003\u0006\u0003\u000089\u0005#\u0000\u00009D\u0001\u0000\u0000"+
		"\u0000:;\u0005\f\u0000\u0000;D\u0003\u0006\u0003\t<=\u0005\r\u0000\u0000"+
		"=D\u0003\u0006\u0003\b>?\u0007\u0001\u0000\u0000?D\u0003\u0006\u0003\u0007"+
		"@A\u0003\f\u0006\u0000AB\u0003\u0006\u0003\u0001BD\u0001\u0000\u0000\u0000"+
		"C2\u0001\u0000\u0000\u0000C4\u0001\u0000\u0000\u0000C5\u0001\u0000\u0000"+
		"\u0000C6\u0001\u0000\u0000\u0000C:\u0001\u0000\u0000\u0000C<\u0001\u0000"+
		"\u0000\u0000C>\u0001\u0000\u0000\u0000C@\u0001\u0000\u0000\u0000DV\u0001"+
		"\u0000\u0000\u0000EF\n\u0006\u0000\u0000FG\u0007\u0002\u0000\u0000GU\u0003"+
		"\u0006\u0003\u0006HI\n\u0005\u0000\u0000IJ\u0005\u0003\u0000\u0000JU\u0003"+
		"\u0006\u0003\u0006KL\n\u0004\u0000\u0000LM\u0005\u0004\u0000\u0000MU\u0003"+
		"\u0006\u0003\u0005NO\n\u0003\u0000\u0000OP\u0005\u0013\u0000\u0000PU\u0003"+
		"\u0006\u0003\u0004QR\n\u0002\u0000\u0000RS\u0007\u0003\u0000\u0000SU\u0003"+
		"\u0006\u0003\u0002TE\u0001\u0000\u0000\u0000TH\u0001\u0000\u0000\u0000"+
		"TK\u0001\u0000\u0000\u0000TN\u0001\u0000\u0000\u0000TQ\u0001\u0000\u0000"+
		"\u0000UX\u0001\u0000\u0000\u0000VT\u0001\u0000\u0000\u0000VW\u0001\u0000"+
		"\u0000\u0000W\u0007\u0001\u0000\u0000\u0000XV\u0001\u0000\u0000\u0000"+
		"YZ\u0007\u0004\u0000\u0000Z\t\u0001\u0000\u0000\u0000[\\\u0005\u0019\u0000"+
		"\u0000\\\u000b\u0001\u0000\u0000\u0000]^\u0005\u000b\u0000\u0000^_\u0003"+
		"\u0004\u0002\u0000_`\u0005\n\u0000\u0000`\r\u0001\u0000\u0000\u0000ab"+
		"\u0005\u0016\u0000\u0000bg\u0005\u001c\u0000\u0000cd\u0005 \u0000\u0000"+
		"df\u0005\u001c\u0000\u0000ec\u0001\u0000\u0000\u0000fi\u0001\u0000\u0000"+
		"\u0000ge\u0001\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000h\u000f\u0001"+
		"\u0000\u0000\u0000ig\u0001\u0000\u0000\u0000jk\u0005\u0017\u0000\u0000"+
		"kp\u0005\u001c\u0000\u0000lm\u0005 \u0000\u0000mo\u0005\u001c\u0000\u0000"+
		"nl\u0001\u0000\u0000\u0000or\u0001\u0000\u0000\u0000pn\u0001\u0000\u0000"+
		"\u0000pq\u0001\u0000\u0000\u0000q\u0011\u0001\u0000\u0000\u0000rp\u0001"+
		"\u0000\u0000\u0000st\u0005\u0018\u0000\u0000ty\u0005\u001c\u0000\u0000"+
		"uv\u0005 \u0000\u0000vx\u0005\u001c\u0000\u0000wu\u0001\u0000\u0000\u0000"+
		"x{\u0001\u0000\u0000\u0000yw\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000"+
		"\u0000z\u0013\u0001\u0000\u0000\u0000{y\u0001\u0000\u0000\u0000|~\u0005"+
		"\u001c\u0000\u0000}\u007f\u0007\u0005\u0000\u0000~}\u0001\u0000\u0000"+
		"\u0000~\u007f\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000\u0000\u0000"+
		"\u0080\u0081\u0005\u0001\u0000\u0000\u0081\u0082\u0003\u0006\u0003\u0000"+
		"\u0082\u0083\u0005\u0002\u0000\u0000\u0083\u0084\u0005\u001c\u0000\u0000"+
		"\u0084\u0015\u0001\u0000\u0000\u0000\u0085\u0087\u0007\u0006\u0000\u0000"+
		"\u0086\u0085\u0001\u0000\u0000\u0000\u0086\u0087\u0001\u0000\u0000\u0000"+
		"\u0087\u0088\u0001\u0000\u0000\u0000\u0088\u0089\u0003\u000e\u0007\u0000"+
		"\u0089\u008a\u0005!\u0000\u0000\u008a\u008b\u0003\u0010\b\u0000\u008b"+
		"\u008c\u0005!\u0000\u0000\u008c\u008d\u0003\u0012\t\u0000\u008d\u008e"+
		"\u0005!\u0000\u0000\u008e\u0093\u0003\u0014\n\u0000\u008f\u0090\u0005"+
		"!\u0000\u0000\u0090\u0092\u0003\u0014\n\u0000\u0091\u008f\u0001\u0000"+
		"\u0000\u0000\u0092\u0095\u0001\u0000\u0000\u0000\u0093\u0091\u0001\u0000"+
		"\u0000\u0000\u0093\u0094\u0001\u0000\u0000\u0000\u0094\u0017\u0001\u0000"+
		"\u0000\u0000\u0095\u0093\u0001\u0000\u0000\u0000\u0096\u0098\u0003\f\u0006"+
		"\u0000\u0097\u0096\u0001\u0000\u0000\u0000\u0097\u0098\u0001\u0000\u0000"+
		"\u0000\u0098\u0099\u0001\u0000\u0000\u0000\u0099\u009a\u0003\u0016\u000b"+
		"\u0000\u009a\u0019\u0001\u0000\u0000\u0000\u009b\u009c\u0007\u0007\u0000"+
		"\u0000\u009c\u001b\u0001\u0000\u0000\u0000\u000e\u001f%,0CTVgpy~\u0086"+
		"\u0093\u0097";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}