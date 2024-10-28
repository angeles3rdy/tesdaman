package layout.enums;

public enum DateEnum {
    JANUARY(31),
    FEBRUARY(28), 
    MARCH(31),
    APRIL(30),
    MAY(31),
    JUNE(30),
    JULY(31),
    AUGUST(31),
    SEPTEMBER(30),
    OCTOBER(31),
    NOVEMBER(30),
    DECEMBER(31);

    private final int daysInMonth;

    DateEnum(final int daysInMonth) {
        this.daysInMonth = daysInMonth;
    }

    public int getDaysInMonth() {
        return daysInMonth;
    }

    public int getDaysInMonth(int year) {
        if (this == FEBRUARY && isLeapYear(year)) { //"THIS" REFERS TO DATEENUM
            return 29;
        }
        
        return daysInMonth;
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}
