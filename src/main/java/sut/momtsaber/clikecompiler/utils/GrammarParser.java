package sut.momtsaber.clikecompiler.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import sut.momtsaber.clikecompiler.cfg.CFG;
import sut.momtsaber.clikecompiler.cfg.CFGNonTerminal;
import sut.momtsaber.clikecompiler.cfg.CFGProduction;
import sut.momtsaber.clikecompiler.cfg.CFGSymbol;
import sut.momtsaber.clikecompiler.cfg.CFGTerminal;

public class GrammarParser
{
    private int pCount = 0;
    private HashMap<String, Integer> nonTerminalIds = new HashMap<>();
    private List<List<List<String>>> rawProductions = new ArrayList<>();

    public void parseAndAddProduction(String production)
    {
        {
            Pattern p = Pattern.compile("(\\w+)\\s*->\\s*");
            Matcher m = p.matcher(production);
            if (!m.find())
                throw new InvalidProductionException("Left hand side not found.");
            nonTerminalIds.put(m.group(1), pCount);
            pCount++;
            production = production.substring(m.end(0));
        }

        List<List<String>> rightHand = new LinkedList<>();
        {
            LinkedList<String> current = new LinkedList<>();
            for (Iterator<String> iter = Pattern.compile("\\s+").splitAsStream(production).iterator();
                 iter.hasNext(); )
            {
                String symbol = iter.next();
                if (symbol.equals("|"))
                {
                    rightHand.add(current);
                    current = new LinkedList<>();
                }
                else
                    current.add(symbol);
            }
            rightHand.add(current);
        }

        rawProductions.add(rightHand);
    }

    public CFG closeAndProduce()
    {
        CFG grammar = new CFG();
        ArrayList<CFGNonTerminal> nonTerminals = IntStream.range(0, rawProductions.size())
                .mapToObj(CFGNonTerminal::new)
                .collect(Collectors.toCollection(ArrayList::new));

        for (int i = 0; i < rawProductions.size(); i++)
        {
            CFGNonTerminal left = nonTerminals.get(i);
            LinkedList<LinkedList<CFGSymbol>> right = rawProductions.get(i).stream()
                    .map(raws -> raws.stream()
                            .filter(r -> !(r.equals("ϵ") || r.equals("EPS"))) //empty list for epsilon
                            .map(r ->
                            {
                                Integer id = nonTerminalIds.get(r);
                                if (id == null)
                                    return CFGTerminal.parse(r);
                                else
                                    return nonTerminals.get(id);
                            })
                            .collect(Collectors.toCollection(LinkedList::new)))
                    .collect(Collectors.toCollection(LinkedList::new));
            grammar.addProduction(new CFGProduction(left, right));
        }

        nonTerminalIds.forEach((name, id) -> grammar.putNonTerminalName(nonTerminals.get(id), name));

        return grammar;
    }

    public static class InvalidProductionException extends RuntimeException
    {
        public InvalidProductionException(String message)
        {
            super(message);
        }
    }

    private static class JsonBuilder
    {
        public static void main(String[] args)
        {
            Scanner scanner = new Scanner(System.in);
            GrammarParser parser = new GrammarParser();
            while (scanner.hasNextLine())
                parser.parseAndAddProduction(scanner.nextLine());
            System.out.println(new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create().toJson(parser.closeAndProduce()));
        }
    }
}