package com.stubio.util;

public class DataGenerator {

    public class IncludeOptions {
        private boolean alphabets;
        private boolean numbers;
        private boolean specialChar;

        public boolean isAlphabets() {
            return alphabets;
        }

        public void setAlphabets(boolean alphabets) {
            this.alphabets = alphabets;
        }

        public boolean isNumbers() {
            return numbers;
        }

        public void setNumbers(boolean numbers) {
            this.numbers = numbers;
        }

        public boolean isSpecialChar() {
            return specialChar;
        }

        public void setSpecialChar(boolean specialChar) {
            this.specialChar = specialChar;
        }
    }

    private String type; // "Random Number" | "Sequential Number" | "Random String"

    // Numeric range
    private Long startNumber; // up to 99999999999999
    private Long endNumber;

    // Sequential increment
    private Integer increment;

    // Random string options
    private Integer length;
    private String variable;
    private String prefix;
    private IncludeOptions include; // alphabets/numbers/specialChar

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getStartNumber() {
        return startNumber;
    }

    public void setStartNumber(Long startNumber) {
        this.startNumber = startNumber;
    }

    public Long getEndNumber() {
        return endNumber;
    }

    public void setEndNumber(Long endNumber) {
        this.endNumber = endNumber;
    }

    public Integer getIncrement() {
        return increment;
    }

    public void setIncrement(Integer increment) {
        this.increment = increment;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public IncludeOptions getInclude() {
        return include;
    }

    public void setInclude(IncludeOptions include) {
        this.include = include;
    }
}
