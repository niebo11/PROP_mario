import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

class BooleanParser{
    private Document d;

    public BooleanParser(Document d){
        this.d = d;
    }

    public void setDocument(Document d) {
        this.d = d;
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

    public Boolean parserBooleanExpresion(String expression) {
        expression = preprocess(expression);
        String[] tokens = expression.split(" ");
        tokens = preprocessTokens(tokens);
        return parserOrExpression(tokens);
    }

    public Boolean parserOrExpression(String[] tokens) {
        int parenthesis = 0;
        for (int i = 0; i < tokens.length; ++i) {
            if (tokens[i].equals("(")) ++parenthesis;
            else if(tokens[i].equals(")")) --parenthesis;
            else if(tokens[i].equals("|") && parenthesis==0) {
                return (
                    parserOrExpression(Arrays.copyOfRange(tokens, 0, i)) ||
                   parserOrExpression(Arrays.copyOfRange(tokens, i+1, tokens.length))
                );
            }
        }
        return parserAndExpression(tokens);
    }

    public Boolean parserAndExpression(String[] tokens) {
        int parenthesis = 0;
        for (int i = 0; i < tokens.length; ++i) {
            if (tokens[i].equals("(")) ++parenthesis;
            else if(tokens[i].equals(")")) --parenthesis;
            else if(tokens[i].equals("&") && parenthesis==0) {
                return (
                    parserOrExpression(Arrays.copyOfRange(tokens, 0, i)) &&
                    parserOrExpression(Arrays.copyOfRange(tokens, i + 1, tokens.length))
                );
            }
        }
        return parserParenthesisExpression(tokens);
    }

    public Boolean parserParenthesisExpression(String[] tokens) {
        if (tokens[0].equals("(")) {
            parserOrExpression(Arrays.copyOfRange(tokens, 1, tokens.length));
        }
        return parserExpression(tokens);
    }

    public Boolean parserExpression(String[] tokens) {
        String firstString = tokens[0];
        if (tokens.length == 1) return parserExpression(firstString);
        switch (firstString) {
            case "\"":
                return parserFullSentence(String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length-1)));
            case "{":
                return parserSetWords(Arrays.copyOfRange(tokens, 1, tokens.length - 1));
        }
        return false;
    }

    private Boolean parserExpression(String token) {
        if (token.startsWith("!")) return !d.hasToken(token.substring(1));
        return d.hasToken(token);
    }

    private Boolean parserFullSentence(String sentence) {
        sentence = sentence.replace("\"", "");
        return d.hasSentence(sentence);
    }

    private Boolean parserSetWords(String[] words) {
        for (String w: words) {
            if (!d.hasToken(w)) return false;
        }
        return true;
    }
}