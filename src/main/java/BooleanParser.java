import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;

class BooleanParser{

    private String[] expressions;

    private boolean full;

    public BooleanParser(){
        expressions = new String[10];
        full = false;
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
        save_expression(expression);
        return parserOrExpression(tokens, d);
    }

    private void save_expression(String expression){
        if (hasExpression(expression)) {
            boolean found = false;
            for (int i = 9; i > 1; --i) {
                if(!found) {
                    if (expressions[i]!=null)
                        if(expressions[i].equals(expression)) {
                            found = true;
                            expressions[i] = expressions[i - 1];
                        }
                }
                else {
                    expressions[i] = expressions[i - 1];
                }
            }
            expressions[0] = expression;
        }
        else {
            for (int i = 9; i > 0; --i) {
                expressions[i] = expressions[i - 1];
            }
            expressions[0] = expression;
            if (expressions[9]!= null) full = true;
        }
    }

    private Boolean hasExpression(String expression) {
        for (int i = 0; i < 10; i++)
            if (expressions[i] != null)
                if (expressions[i].equals(expression)) return true;
        return false;
    }

    public String[] get_recent_searches() {
        String[] result;
        if (!full) {
            ArrayList<String> tempt = new ArrayList<String>();
            for (int i = 0; i < 10; ++i) {
                if (expressions[i] != null) {
                    tempt.add(expressions[i]);
                }
            }
            result = new String[tempt.size()];
            for (int i = 0; i < tempt.size(); ++i) result[i] = tempt.get(i);
        }
        else {
            result = expressions;
        }
        return result;
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