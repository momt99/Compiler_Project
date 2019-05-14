package sut.momtsaber.clikecompiler.cfg;

import sut.momtsaber.clikecompiler.lexicalanalysis.Token;
import sut.momtsaber.clikecompiler.lexicalanalysis.TokenType;

public class CFGTerminal extends CFGSymbol
{
    private TokenType tokenType;
    private String value;

    private CFGTerminal() {}

    public static CFGTerminal parse(String raw)
    {
        CFGTerminal retVal = new CFGTerminal();
        TokenType type;
        try { type = TokenType.valueOf(raw); }
        catch (Exception ignored)
        {
            if (Token.KEYWORDS.contains(raw))
                type = TokenType.KEYWORD;
            else if (Token.SYMBOLS.contains(raw))
                type = TokenType.SYMBOL;
            else
                throw new IllegalArgumentException("No token type found for: " + raw);

            retVal.value = raw;
        }
        retVal.tokenType = type;

        return retVal;
    }

    public static final CFGTerminal EPSILON = new CFGTerminal();
    public static final CFGTerminal EOF;
    static
    {
        EOF = new CFGTerminal();
        EOF.tokenType = TokenType.EOF;
    }
    public TokenType getTokenType()
    {
        return tokenType;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "CFGTerminal{" +
                "tokenType=" + tokenType +
                ", value='" + value + '\'' +
                '}';
    }
}