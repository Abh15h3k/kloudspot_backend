package com.example.project.Models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.YearMonth;
import java.util.ArrayList;

@Data
@AllArgsConstructor
public class ChartData {
    ArrayList<YearMonth> yearMonths;
    ArrayList<Long> counts;
    ArrayList<Long> newCounts;

    public void addPoint(YearMonth yearMonth, long count, long newCount) {
        this.yearMonths.add(yearMonth);
        this.counts.add(count);
        this.newCounts.add(newCount);
    }

    public void addYearMonthsCount(YearMonth yearMonth, long count) {
        this.yearMonths.add(yearMonth);
        this.counts.add(count);
    }

    public void addYearMonthsNewCount(YearMonth yearMonth, long newCount) {
        this.yearMonths.add(yearMonth);
        this.newCounts.add(newCount);
    }
}
