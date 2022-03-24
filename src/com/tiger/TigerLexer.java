package com.tiger;
// Generated from Tiger.g4 by ANTLR 4.9.3
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TigerLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ARRAY=1, BEGIN=2, BREAK=3, DO=4, ELSE=5, END=6, ENDDO=7, ENDIF=8, FLOAT=9, 
		FOR=10, FUNCTION=11, IF=12, INT=13, LET=14, OF=15, PROGRAM=16, RETURN=17, 
		STATIC=18, THEN=19, TO=20, TYPE=21, VAR=22, WHILE=23, COMMA=24, DOT=25, 
		COLON=26, SEMICOLON=27, OPENPAREN=28, CLOSEPAREN=29, OPENBRACK=30, CLOSEBRACK=31, 
		OPENCURLY=32, CLOSECURLY=33, PLUS=34, MINUS=35, MULT=36, DIV=37, POW=38, 
		EQUAL=39, NEQUAL=40, LESS=41, GREAT=42, LESSEQ=43, GREATEQ=44, AND=45, 
		OR=46, ASSIGN=47, TASSIGN=48, COMMENT=49, ID=50, INTLIT=51, FLOATLIT=52, 
		WHITESPACE=53;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"ARRAY", "BEGIN", "BREAK", "DO", "ELSE", "END", "ENDDO", "ENDIF", "FLOAT", 
			"FOR", "FUNCTION", "IF", "INT", "LET", "OF", "PROGRAM", "RETURN", "STATIC", 
			"THEN", "TO", "TYPE", "VAR", "WHILE", "COMMA", "DOT", "COLON", "SEMICOLON", 
			"OPENPAREN", "CLOSEPAREN", "OPENBRACK", "CLOSEBRACK", "OPENCURLY", "CLOSECURLY", 
			"PLUS", "MINUS", "MULT", "DIV", "POW", "EQUAL", "NEQUAL", "LESS", "GREAT", 
			"LESSEQ", "GREATEQ", "AND", "OR", "ASSIGN", "TASSIGN", "COMMENT", "ID", 
			"INTLIT", "DIGIT", "FLOATLIT", "WHITESPACE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'array'", "'begin'", "'break'", "'do'", "'else'", "'end'", "'enddo'", 
			"'endif'", "'float'", "'for'", "'function'", "'if'", "'int'", "'let'", 
			"'of'", "'program'", "'return'", "'static'", "'then'", "'to'", "'type'", 
			"'var'", "'while'", "','", "'.'", "':'", "';'", "'('", "')'", "'['", 
			"']'", "'{'", "'}'", "'+'", "'-'", "'*'", "'/'", "'**'", "'=='", "'!='", 
			"'<'", "'>'", "'<='", "'>='", "'&'", "'|'", "':='", "'='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ARRAY", "BEGIN", "BREAK", "DO", "ELSE", "END", "ENDDO", "ENDIF", 
			"FLOAT", "FOR", "FUNCTION", "IF", "INT", "LET", "OF", "PROGRAM", "RETURN", 
			"STATIC", "THEN", "TO", "TYPE", "VAR", "WHILE", "COMMA", "DOT", "COLON", 
			"SEMICOLON", "OPENPAREN", "CLOSEPAREN", "OPENBRACK", "CLOSEBRACK", "OPENCURLY", 
			"CLOSECURLY", "PLUS", "MINUS", "MULT", "DIV", "POW", "EQUAL", "NEQUAL", 
			"LESS", "GREAT", "LESSEQ", "GREATEQ", "AND", "OR", "ASSIGN", "TASSIGN", 
			"COMMENT", "ID", "INTLIT", "FLOATLIT", "WHITESPACE"
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


	public TigerLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Tiger.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\67\u014d\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\4\67\t\67\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6"+
		"\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3"+
		"\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\20\3\20\3\20"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\25"+
		"\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\30\3\30\3\30"+
		"\3\30\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36"+
		"\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3"+
		"\'\3(\3(\3(\3)\3)\3)\3*\3*\3+\3+\3,\3,\3,\3-\3-\3-\3.\3.\3/\3/\3\60\3"+
		"\60\3\60\3\61\3\61\3\62\3\62\3\62\3\62\7\62\u0124\n\62\f\62\16\62\u0127"+
		"\13\62\3\62\3\62\3\62\3\63\3\63\7\63\u012e\n\63\f\63\16\63\u0131\13\63"+
		"\3\64\3\64\7\64\u0135\n\64\f\64\16\64\u0138\13\64\3\64\5\64\u013b\n\64"+
		"\3\65\3\65\3\66\3\66\3\66\7\66\u0142\n\66\f\66\16\66\u0145\13\66\3\67"+
		"\6\67\u0148\n\67\r\67\16\67\u0149\3\67\3\67\3\u0125\28\3\3\5\4\7\5\t\6"+
		"\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24"+
		"\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K"+
		"\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\2k\66m\67\3\2\7\4\2C\\c|\6"+
		"\2\62;C\\aac|\3\2\63;\3\2\62;\5\2\13\f\17\17\"\"\2\u0151\2\3\3\2\2\2\2"+
		"\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2"+
		"\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2"+
		"\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2"+
		"\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2"+
		"\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2"+
		"\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2"+
		"K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3"+
		"\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2"+
		"\2\2e\3\2\2\2\2g\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\3o\3\2\2\2\5u\3\2\2\2\7"+
		"{\3\2\2\2\t\u0081\3\2\2\2\13\u0084\3\2\2\2\r\u0089\3\2\2\2\17\u008d\3"+
		"\2\2\2\21\u0093\3\2\2\2\23\u0099\3\2\2\2\25\u009f\3\2\2\2\27\u00a3\3\2"+
		"\2\2\31\u00ac\3\2\2\2\33\u00af\3\2\2\2\35\u00b3\3\2\2\2\37\u00b7\3\2\2"+
		"\2!\u00ba\3\2\2\2#\u00c2\3\2\2\2%\u00c9\3\2\2\2\'\u00d0\3\2\2\2)\u00d5"+
		"\3\2\2\2+\u00d8\3\2\2\2-\u00dd\3\2\2\2/\u00e1\3\2\2\2\61\u00e7\3\2\2\2"+
		"\63\u00e9\3\2\2\2\65\u00eb\3\2\2\2\67\u00ed\3\2\2\29\u00ef\3\2\2\2;\u00f1"+
		"\3\2\2\2=\u00f3\3\2\2\2?\u00f5\3\2\2\2A\u00f7\3\2\2\2C\u00f9\3\2\2\2E"+
		"\u00fb\3\2\2\2G\u00fd\3\2\2\2I\u00ff\3\2\2\2K\u0101\3\2\2\2M\u0103\3\2"+
		"\2\2O\u0106\3\2\2\2Q\u0109\3\2\2\2S\u010c\3\2\2\2U\u010e\3\2\2\2W\u0110"+
		"\3\2\2\2Y\u0113\3\2\2\2[\u0116\3\2\2\2]\u0118\3\2\2\2_\u011a\3\2\2\2a"+
		"\u011d\3\2\2\2c\u011f\3\2\2\2e\u012b\3\2\2\2g\u013a\3\2\2\2i\u013c\3\2"+
		"\2\2k\u013e\3\2\2\2m\u0147\3\2\2\2op\7c\2\2pq\7t\2\2qr\7t\2\2rs\7c\2\2"+
		"st\7{\2\2t\4\3\2\2\2uv\7d\2\2vw\7g\2\2wx\7i\2\2xy\7k\2\2yz\7p\2\2z\6\3"+
		"\2\2\2{|\7d\2\2|}\7t\2\2}~\7g\2\2~\177\7c\2\2\177\u0080\7m\2\2\u0080\b"+
		"\3\2\2\2\u0081\u0082\7f\2\2\u0082\u0083\7q\2\2\u0083\n\3\2\2\2\u0084\u0085"+
		"\7g\2\2\u0085\u0086\7n\2\2\u0086\u0087\7u\2\2\u0087\u0088\7g\2\2\u0088"+
		"\f\3\2\2\2\u0089\u008a\7g\2\2\u008a\u008b\7p\2\2\u008b\u008c\7f\2\2\u008c"+
		"\16\3\2\2\2\u008d\u008e\7g\2\2\u008e\u008f\7p\2\2\u008f\u0090\7f\2\2\u0090"+
		"\u0091\7f\2\2\u0091\u0092\7q\2\2\u0092\20\3\2\2\2\u0093\u0094\7g\2\2\u0094"+
		"\u0095\7p\2\2\u0095\u0096\7f\2\2\u0096\u0097\7k\2\2\u0097\u0098\7h\2\2"+
		"\u0098\22\3\2\2\2\u0099\u009a\7h\2\2\u009a\u009b\7n\2\2\u009b\u009c\7"+
		"q\2\2\u009c\u009d\7c\2\2\u009d\u009e\7v\2\2\u009e\24\3\2\2\2\u009f\u00a0"+
		"\7h\2\2\u00a0\u00a1\7q\2\2\u00a1\u00a2\7t\2\2\u00a2\26\3\2\2\2\u00a3\u00a4"+
		"\7h\2\2\u00a4\u00a5\7w\2\2\u00a5\u00a6\7p\2\2\u00a6\u00a7\7e\2\2\u00a7"+
		"\u00a8\7v\2\2\u00a8\u00a9\7k\2\2\u00a9\u00aa\7q\2\2\u00aa\u00ab\7p\2\2"+
		"\u00ab\30\3\2\2\2\u00ac\u00ad\7k\2\2\u00ad\u00ae\7h\2\2\u00ae\32\3\2\2"+
		"\2\u00af\u00b0\7k\2\2\u00b0\u00b1\7p\2\2\u00b1\u00b2\7v\2\2\u00b2\34\3"+
		"\2\2\2\u00b3\u00b4\7n\2\2\u00b4\u00b5\7g\2\2\u00b5\u00b6\7v\2\2\u00b6"+
		"\36\3\2\2\2\u00b7\u00b8\7q\2\2\u00b8\u00b9\7h\2\2\u00b9 \3\2\2\2\u00ba"+
		"\u00bb\7r\2\2\u00bb\u00bc\7t\2\2\u00bc\u00bd\7q\2\2\u00bd\u00be\7i\2\2"+
		"\u00be\u00bf\7t\2\2\u00bf\u00c0\7c\2\2\u00c0\u00c1\7o\2\2\u00c1\"\3\2"+
		"\2\2\u00c2\u00c3\7t\2\2\u00c3\u00c4\7g\2\2\u00c4\u00c5\7v\2\2\u00c5\u00c6"+
		"\7w\2\2\u00c6\u00c7\7t\2\2\u00c7\u00c8\7p\2\2\u00c8$\3\2\2\2\u00c9\u00ca"+
		"\7u\2\2\u00ca\u00cb\7v\2\2\u00cb\u00cc\7c\2\2\u00cc\u00cd\7v\2\2\u00cd"+
		"\u00ce\7k\2\2\u00ce\u00cf\7e\2\2\u00cf&\3\2\2\2\u00d0\u00d1\7v\2\2\u00d1"+
		"\u00d2\7j\2\2\u00d2\u00d3\7g\2\2\u00d3\u00d4\7p\2\2\u00d4(\3\2\2\2\u00d5"+
		"\u00d6\7v\2\2\u00d6\u00d7\7q\2\2\u00d7*\3\2\2\2\u00d8\u00d9\7v\2\2\u00d9"+
		"\u00da\7{\2\2\u00da\u00db\7r\2\2\u00db\u00dc\7g\2\2\u00dc,\3\2\2\2\u00dd"+
		"\u00de\7x\2\2\u00de\u00df\7c\2\2\u00df\u00e0\7t\2\2\u00e0.\3\2\2\2\u00e1"+
		"\u00e2\7y\2\2\u00e2\u00e3\7j\2\2\u00e3\u00e4\7k\2\2\u00e4\u00e5\7n\2\2"+
		"\u00e5\u00e6\7g\2\2\u00e6\60\3\2\2\2\u00e7\u00e8\7.\2\2\u00e8\62\3\2\2"+
		"\2\u00e9\u00ea\7\60\2\2\u00ea\64\3\2\2\2\u00eb\u00ec\7<\2\2\u00ec\66\3"+
		"\2\2\2\u00ed\u00ee\7=\2\2\u00ee8\3\2\2\2\u00ef\u00f0\7*\2\2\u00f0:\3\2"+
		"\2\2\u00f1\u00f2\7+\2\2\u00f2<\3\2\2\2\u00f3\u00f4\7]\2\2\u00f4>\3\2\2"+
		"\2\u00f5\u00f6\7_\2\2\u00f6@\3\2\2\2\u00f7\u00f8\7}\2\2\u00f8B\3\2\2\2"+
		"\u00f9\u00fa\7\177\2\2\u00faD\3\2\2\2\u00fb\u00fc\7-\2\2\u00fcF\3\2\2"+
		"\2\u00fd\u00fe\7/\2\2\u00feH\3\2\2\2\u00ff\u0100\7,\2\2\u0100J\3\2\2\2"+
		"\u0101\u0102\7\61\2\2\u0102L\3\2\2\2\u0103\u0104\7,\2\2\u0104\u0105\7"+
		",\2\2\u0105N\3\2\2\2\u0106\u0107\7?\2\2\u0107\u0108\7?\2\2\u0108P\3\2"+
		"\2\2\u0109\u010a\7#\2\2\u010a\u010b\7?\2\2\u010bR\3\2\2\2\u010c\u010d"+
		"\7>\2\2\u010dT\3\2\2\2\u010e\u010f\7@\2\2\u010fV\3\2\2\2\u0110\u0111\7"+
		">\2\2\u0111\u0112\7?\2\2\u0112X\3\2\2\2\u0113\u0114\7@\2\2\u0114\u0115"+
		"\7?\2\2\u0115Z\3\2\2\2\u0116\u0117\7(\2\2\u0117\\\3\2\2\2\u0118\u0119"+
		"\7~\2\2\u0119^\3\2\2\2\u011a\u011b\7<\2\2\u011b\u011c\7?\2\2\u011c`\3"+
		"\2\2\2\u011d\u011e\7?\2\2\u011eb\3\2\2\2\u011f\u0120\7\61\2\2\u0120\u0121"+
		"\7,\2\2\u0121\u0125\3\2\2\2\u0122\u0124\13\2\2\2\u0123\u0122\3\2\2\2\u0124"+
		"\u0127\3\2\2\2\u0125\u0126\3\2\2\2\u0125\u0123\3\2\2\2\u0126\u0128\3\2"+
		"\2\2\u0127\u0125\3\2\2\2\u0128\u0129\7,\2\2\u0129\u012a\7\61\2\2\u012a"+
		"d\3\2\2\2\u012b\u012f\t\2\2\2\u012c\u012e\t\3\2\2\u012d\u012c\3\2\2\2"+
		"\u012e\u0131\3\2\2\2\u012f\u012d\3\2\2\2\u012f\u0130\3\2\2\2\u0130f\3"+
		"\2\2\2\u0131\u012f\3\2\2\2\u0132\u0136\t\4\2\2\u0133\u0135\5i\65\2\u0134"+
		"\u0133\3\2\2\2\u0135\u0138\3\2\2\2\u0136\u0134\3\2\2\2\u0136\u0137\3\2"+
		"\2\2\u0137\u013b\3\2\2\2\u0138\u0136\3\2\2\2\u0139\u013b\7\62\2\2\u013a"+
		"\u0132\3\2\2\2\u013a\u0139\3\2\2\2\u013bh\3\2\2\2\u013c\u013d\t\5\2\2"+
		"\u013dj\3\2\2\2\u013e\u013f\5g\64\2\u013f\u0143\7\60\2\2\u0140\u0142\5"+
		"i\65\2\u0141\u0140\3\2\2\2\u0142\u0145\3\2\2\2\u0143\u0141\3\2\2\2\u0143"+
		"\u0144\3\2\2\2\u0144l\3\2\2\2\u0145\u0143\3\2\2\2\u0146\u0148\t\6\2\2"+
		"\u0147\u0146\3\2\2\2\u0148\u0149\3\2\2\2\u0149\u0147\3\2\2\2\u0149\u014a"+
		"\3\2\2\2\u014a\u014b\3\2\2\2\u014b\u014c\b\67\2\2\u014cn\3\2\2\2\t\2\u0125"+
		"\u012f\u0136\u013a\u0143\u0149\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}