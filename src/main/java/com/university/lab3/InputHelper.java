package com.university.lab3;

import java.util.Scanner;

/**
 * Exercise 3.2 - Safe keyboard input. Every method loops until the user types
 * something valid, so bad input never crashes the program.
 * We always read whole lines (nextLine) and parse them ourselves - mixing
 * nextInt() and nextLine() is the classic Scanner bug.
 */
public class InputHelper {

    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public String readNonBlank(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) return value;
            System.out.println("  This field cannot be empty.");
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            String value = readLine(prompt);
            try {
                int number = Integer.parseInt(value);
                if (number >= min && number <= max) return number;
                System.out.println("  Enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  '" + value + "' is not a whole number. Try again.");
            }
        }
    }

    public double readDouble(String prompt, double min, double max) {
        while (true) {
            String value = readLine(prompt);
            try {
                double number = Double.parseDouble(value);
                if (number >= min && number <= max) return number;
                System.out.println("  Enter a value between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  '" + value + "' is not a number. Try again.");
            }
        }
    }

    public String readEmail(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (value.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) return value;
            System.out.println("  That doesn't look like a valid email (e.g. name@uni.edu).");
        }
    }

    public boolean confirm(String prompt) {
        String value = readLine(prompt + " (y/n): ");
        return value.equalsIgnoreCase("y") || value.equalsIgnoreCase("yes");
    }
}
