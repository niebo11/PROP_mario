import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

class BooleanParser{

    public BooleanParser(){
    }


    public String preprocess(String expression) {
        expression = expression.replace("(", "( ").replace(")", " )");
        expression = expression.replace("\"", " \" ");
        expression = expression.replace("{", "{ ").replace("}", " }");
        return expression;
    }

    public String[] preprocessTokens(String[] tokens) {
        ArrayList<String> filter = new ArrayList<String>();
        for (String token: tokens) {
            if (!token.equals("")) filter.add(token);
        }
        return filter.toArray(new String[filter.size()]);
    }

    public Boolean parserBooleanExpresion(String expression, Document d) {
        expression = preprocess(expression);
        String[] tokens = expression.split(" ");
        tokens = preprocessTokens(tokens);
        return parserOrExpression(tokens, d);
    }

    public Boolean parserOrExpression(String[] tokens, Document d) {
        int parenthesis = 0;
        for (int i = 0; i < tokens.length; ++i) {
            if (tokens[i].equals("(")) ++parenthesis;
            else if(tokens[i].equals(")")) --parenthesis;
            else if(tokens[i].equals("|") && parenthesis==0) {
                return (
                    parserOrExpression(Arrays.copyOfRange(tokens, 0, i), d) ||
                   parserOrExpression(Arrays.copyOfRange(tokens, i+1, tokens.length), d)
                );
            }
        }
        return parserAndExpression(tokens, d);
    }

    public Boolean parserAndExpression(String[] tokens, Document d) {
        int parenthesis = 0;
        for (int i = 0; i < tokens.length; ++i) {
            if (tokens[i].equals("(")) ++parenthesis;
            else if(tokens[i].equals(")")) --parenthesis;
            else if(tokens[i].equals("&") && parenthesis==0) {
                return (
                    parserOrExpression(Arrays.copyOfRange(tokens, 0, i), d) &&
                    parserOrExpression(Arrays.copyOfRange(tokens, i + 1, tokens.length), d)
                );
            }
        }
        return parserParenthesisExpression(tokens, d);
    }

    public Boolean parserParenthesisExpression(String[] tokens, Document d) {
        if (tokens[0].equals("(")) {
            parserOrExpression(Arrays.copyOfRange(tokens, 1, tokens.length), d);
        }
        return parserExpression(tokens, d);
    }

    public Boolean parserExpression(String[] tokens, Document d) {
        String firstString = tokens[0];
        if (tokens.length == 1) return parserExpression(firstString, d);
        switch (firstString) {
            case "\"":
                return parserFullSentence(String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length-1)), d);
            case "{":
                return parserSetWords(Arrays.copyOfRange(tokens, 1, tokens.length - 1), d);
        }
        return false;
    }

    private Boolean parserExpression(String token, Document d) {
        if (token.startsWith("!")) return !d.hasToken(token.substring(1));
        return d.hasToken(token);
    }

    private Boolean parserFullSentence(String sentence, Document d) {
        sentence = sentence.replace("\"", "");
        return d.hasSentence(sentence);
    }

    private Boolean parserSetWords(String[] words, Document d) {
        for (String w: words) {
            if (!d.hasToken(w)) return false;
        }
        return true;
    }
}