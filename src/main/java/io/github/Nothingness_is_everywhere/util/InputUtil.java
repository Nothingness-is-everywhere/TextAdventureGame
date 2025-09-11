package io.github.Nothingness_is_everywhere.util;

import java.util.Scanner;

public class InputUtil {
    private static final Scanner scanner = new Scanner(System.in);

    // 获取用户输入
    public static String getInput() {
        return scanner.nextLine();
    }
}