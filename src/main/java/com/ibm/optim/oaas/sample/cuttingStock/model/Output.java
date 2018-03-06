package com.ibm.optim.oaas.sample.cuttingStock.model;

import java.util.Map;

public class Output {
    private Integer pattern;
    private    Double number;
    //private Map<Integer, String> patternSlices;
    private String patternSlices;

    public Output(Integer pattern, Double number, String patternSlices) {
        this.pattern = pattern;
        this.number = number;
        this.patternSlices = patternSlices;
    }

    public Integer getPattern() {
        return pattern;
    }

    public void setPattern(Integer pattern) {
        this.pattern = pattern;
    }

    public Double getNumber() {
        return number;
    }

    public void setNumber(Double number) {
        this.number = number;
    }

    public String getPatternSlices() {
        return patternSlices;
    }

    public void setPatternSlices(String patternSlices) {
        this.patternSlices = patternSlices;
    }
}