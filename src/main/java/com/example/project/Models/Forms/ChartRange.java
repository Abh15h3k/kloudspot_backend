package com.example.project.Models.Forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartRange {
    int startYear;
    int startMonth;
    int endYear;
    int endMonth;

    public Boolean ZeroGap() {
        return (this.startMonth == this.endMonth && this.startYear == this.endYear);
    }
}
