package com.zjicm.calculation;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Calculator {

    private String expression;

    Calculator(String expression) {
        this.expression = expression;
    }

    String getResult() {
        if (!isIllegal()) {
            return "非法表达式";
        }
        ArrayList<String> split = splitExpression();
        if (split.size() == 1) {
            return split.get(0);
        }
        return getAnswer(split);
    }

    private boolean isIllegal() {
        String pattern = "^\\d+([-+*/]\\d+)*$";
        return Pattern.matches(pattern, expression);
    }

    private ArrayList<String> splitExpression() {
        ArrayList<String> split = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();

        for (int i = 0, j = 0; i < expression.length(); i++) {
            if (expression.charAt(i) >= '0' && expression.charAt(i) <= '9') {
                buffer.append(expression.charAt(i));
            } else {
                split.add(buffer.toString());
                buffer = new StringBuilder();
                j++;
                split.add(expression.charAt(i) + "");
                j++;
            }
        }
        split.add(buffer.toString());
        return split;
    }

    private String getAnswer(ArrayList<String> splitExpression) {
        if (splitExpression.size() == 3) {
            return doOperation(splitExpression.get(0), splitExpression.get(2), splitExpression.get(1));
        } else {
            for (int i = 0; i < splitExpression.size(); i++) {
                if (splitExpression.get(i).equals("*") || splitExpression.get(i).equals("/")) {
                    String a = doOperation(splitExpression.get(i - 1), splitExpression.get(i + 1), splitExpression.get(i));
                    splitExpression.remove(i - 1);
                    splitExpression.remove(i - 1);
                    splitExpression.remove(i - 1);
                    splitExpression.add(i - 1, a);
                    return getAnswer(splitExpression);
                }
            }

            for (int i = 0; i < splitExpression.size(); i++) {
                if (splitExpression.get(i).equals("+") || splitExpression.get(i).equals("-")) {
                    String a = doOperation(splitExpression.get(i - 1), splitExpression.get(i + 1), splitExpression.get(i));
                    splitExpression.remove(i - 1);
                    splitExpression.remove(i - 1);
                    splitExpression.remove(i - 1);
                    splitExpression.add(i - 1, a);
                    return getAnswer(splitExpression);
                }
            }
        }
        return null;
    }

    private String doOperation(String a, String b, String c) {
        switch (c) {
            case "+":
                return String.valueOf((Double.valueOf(a) + Double.valueOf(b)));
            case "-":
                return String.valueOf((Double.valueOf(a) - Double.valueOf(b)));
            case "*":
                return String.valueOf((Double.valueOf(a) * Double.valueOf(b)));
            case "/":
                return String.valueOf(((Double.valueOf(a) * 1.0) / Double.valueOf(b)));
            default:
                return "";
        }
    }
}
