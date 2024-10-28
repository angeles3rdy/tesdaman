package layout.textgenerator;

import java.util.HashMap;
import java.util.Map;

public class AsciiGenerator {
    private static final Map<Character, String[]> asciiMap = new HashMap<>();

    static {
        asciiMap.put('A', new String[] {
                "╔═╗",
                "╠═╣",
                "╩ ╩"
        });
        asciiMap.put('B', new String[] {
                "╔╗ ",
                "╠╩╗",
                "╚═╝"
        });
        asciiMap.put('C', new String[] {
                "╔═╗",
                "║  ",
                "╚═╝"
        });
        asciiMap.put('D', new String[] {
                "╔╦╗",
                " ║║",
                "═╩╝"
        });
        asciiMap.put('E', new String[] {
                "╔═╗",
                "╠═ ",
                "╩═╝"
        });
        asciiMap.put('F', new String[] {
                "╔═╗",
                "╠═ ",
                "╩  "
        });
        asciiMap.put('G', new String[] {
                "╔═╗",
                "║ ╦",
                "╚═╝"
        });
        asciiMap.put('H', new String[] {
                "╦ ╦",
                "╠═╣",
                "╩ ╩"
        });
        asciiMap.put('I', new String[] {
                " ╦ ",
                " ║ ",
                " ╩ "
        });
        asciiMap.put('J', new String[] {
                "  ╦",
                "  ║",
                "╚═╝"
        });
        asciiMap.put('K', new String[] {
                "╦╔═",
                "╠╩╗",
                "╩ ╩"
        });
        asciiMap.put('L', new String[] {
                "╦  ",
                "║  ",
                "╩═╝"
        });
        asciiMap.put('M', new String[] {
                "╔╦╗",
                "║║║",
                "╩ ╩"
        });
        asciiMap.put('N', new String[] {
                "╔╗╔",
                "║║║",
                "╝╚╝"
        });
        asciiMap.put('O', new String[] {
                "╔═╗",
                "║ ║",
                "╚═╝"
        });
        asciiMap.put('P', new String[] {
                "╔═╗",
                "╠═╝",
                "╩  "
        });
        asciiMap.put('Q', new String[] {
                "╔═╗ ",
                "║ ║ ",
                "╚═╚╝"
        });
        asciiMap.put('R', new String[] {
                "╦═╗",
                "╠╦╝",
                "╩╚═"
        });
        asciiMap.put('S', new String[] {
                "╔═╗",
                "╚═╗",
                "╚═╝"
        });
        asciiMap.put('T', new String[] {
                "╔═╗",
                " ║ ",
                " ╩ "
        });
        asciiMap.put('U', new String[] {
                "╦ ╦",
                "║ ║",
                "╚═╝"
        });
        asciiMap.put('V', new String[] {
                "╦  ╦",
                "╚╗╔╝",
                " ╚╝ "
        });
        asciiMap.put('W', new String[] {
                "╦ ╦",
                "║║║",
                "╚╩╝"
        });
        asciiMap.put('X', new String[] {
                "═╗ ╦",
                "╔╩╦╝",
                "╩ ╚═"
        });
        asciiMap.put('Y', new String[] {
                "╦ ╦",
                "╚╦╝",
                " ╩ "
        });
        asciiMap.put('Z', new String[] {
                "╔═╗",
                "╔═╝",
                "╚═╝"
        });
        asciiMap.put(' ', new String[] {
                "   ",
                "   ",
                "   "
        });
    }

    // Method to print ASCII art for a given text with padding adjustments
    public String printAsciiArt(String text) {
        text = text.toUpperCase();
        StringBuilder[] lines = new StringBuilder[3];

        for (int i = 0; i < 3; i++) {
            lines[i] = new StringBuilder();
        }

        for (char ch : text.toCharArray()) {
            String[] asciiChars = asciiMap.getOrDefault(ch, new String[] { "   ", "   ", "   " });
            for (int i = 0; i < 3; i++) {
                lines[i].append(asciiChars[i]).append("  ");
            }
        }

        // Combine all lines into a single string
        StringBuilder result = new StringBuilder();
        for (StringBuilder line : lines) {
            result.append(line).append("\n");
        }
        return result.toString();
    }

    public void printDisplay(String tabs, String text) {
        String asciiArt = printAsciiArt(text);
        String[] lines = asciiArt.split("\n");

        System.out.println(
                "█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗");
        System.out.println("");

        for (String line : lines) {
            System.out.println(tabs + line);
        }

        System.out.println("");
        System.out.println(
                "█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗");
    }
}
