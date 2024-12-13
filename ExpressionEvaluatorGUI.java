import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Stack;
import javax.swing.*;

public class ExpressionEvaluatorGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Expression Evaluator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JTextField inputField = new JTextField();
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        JButton evaluateButton = new JButton("Evaluate");
        JComboBox<String> modeSelector = new JComboBox<>(new String[]{"Postfix", "Prefix", "Infix"});

        evaluateButton.addActionListener((ActionEvent e) -> {
            String expression = inputField.getText().trim();
            String mode = (String) modeSelector.getSelectedItem();

            if (expression.isEmpty()) {
                outputArea.setText("Please enter an expression.");
                return;
            }

            try {
                int result = switch (mode) {
                    case "Postfix" -> evaluatePostfix(expression);
                    case "Prefix" -> evaluatePrefix(expression);
                    case "Infix" -> evaluateInfix(expression);
                    default -> 0;
                };
                outputArea.setText("Result: " + result);
            } catch (Exception ex) {
                outputArea.setText("Error: Invalid Expression");
            }
        });

        frame.setLayout(new GridLayout(4, 1));
        frame.add(new JLabel("Enter Expression:"));
        frame.add(inputField);
        frame.add(modeSelector);
        frame.add(evaluateButton);
        frame.add(new JScrollPane(outputArea));

        frame.setVisible(true);
    }

    private static int evaluatePostfix(String expr) {
        Stack<Integer> stack = new Stack<>();
        for (char c : expr.toCharArray()) {
            if (Character.isDigit(c)) {
                stack.push(c - '0');
            } else {
                int b = stack.pop();
                int a = stack.pop();
                stack.push(applyOp(c, a, b));
            }
        }
        return stack.pop();
    }

    private static int evaluatePrefix(String expr) {
        Stack<Integer> stack = new Stack<>();
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (Character.isDigit(c)) {
                stack.push(c - '0');
            } else {
                int a = stack.pop();
                int b = stack.pop();
                stack.push(applyOp(c, a, b));
            }
        }
        return stack.pop();
    }

    private static int evaluateInfix(String expr) {
        Stack<Integer> values = new Stack<>();
        Stack<Character> ops = new Stack<>();
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (Character.isDigit(c)) {
                values.push(c - '0');
            } else if (c == '(') {
                ops.push(c);
            } else if (c == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.pop();
            } else if ("+-*/^".indexOf(c) != -1) {
                while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(c)) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(c);
            }
        }
        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }
        return values.pop();
    }

    private static int applyOp(char op, int a, int b) {
        return switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> a / b;
            case '^' -> (int) Math.pow(a, b);
            default -> 0;
        };
    }

    private static int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        if (op == '^') return 3;
        return 0;
    }
}
