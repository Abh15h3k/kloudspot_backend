package com.example.project.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripsData {
    long lessThanTwo;
    long lessThanFourGreaterThanTwo;
    long lessThanEigthGreaterThanFour;
    long lessThanSixteenGreaterThanEight;
    long greaterThanSixteen;
}
