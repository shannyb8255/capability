package com.server;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JLInner {

    // JL Inner RD LH / RD RH CAPABILITY
    static int rowCount;

    public static void main(String[] args) {
        // Path to the existing Excel file
        String filePath = "C:\\Users\\shann\\Documents\\Automating Capability\\Test Template for Automating Capability\\JL IB Full Study TEST.xlsx";

        String[] sheetNames = {"RH Data", "LH Data"};

        try {
            // Open the existing workbook once
            FileInputStream fis = new FileInputStream(new File(filePath));
            XSSFWorkbook workbook = new XSSFWorkbook(fis);

            // Loop through sheet names
            for (String sheetName : sheetNames) {
                Sheet sheet = workbook.getSheet(sheetName);

                if (sheetName.equals("RH Data")) {
                    rowCount = 13;
                } else {
                    rowCount = 3;
                }

                for (int c = 1; c < 4; c++) { // Loop 3 times (3 columns in RH Data)
                    // Extract the tolerance value from cell B4 (ensure the correct format of the cell)
                    String toleranceInput = sheet.getRow(3).getCell(c).getStringCellValue().trim();  // B4 = row 3, column 1
                    double tolerance = Double.parseDouble(toleranceInput.split(" ")[1]); // Ensure this splits as expected

                    // Extract the data points from cells B5 to B34
                    List<Double> data = new ArrayList<>();
                    for (int i = 4; i <= 33; i++) {  // B5 to B34 are row 4 to 33 (0-indexed)
                        if (sheet.getRow(i) != null && sheet.getRow(i).getCell(c).getCellType() == CellType.NUMERIC) {
                            data.add(sheet.getRow(i).getCell(c).getNumericCellValue());
                        }
                    }

                    // Calculate mean and standard deviation
                    double mean = calculateMean(data);
                    double CPstdDev = calculateCPStandardDeviation(data, mean);
                    double PPstdDev = calculatePPStandardDeviation(data, mean);

                    // Calculate USL and LSL based on tolerance and mean
                    double usl = 0 + tolerance;
                    double lsl = 0 - tolerance;

                    // Calculate Cp and Cpk
                    double cp = calculateCp(usl, lsl, CPstdDev);
                    double cpk = calculateCpk(usl, lsl, mean, CPstdDev);
                    // Calculate Pp and Ppk
                    double pp = calculatePp(usl, lsl, PPstdDev);
                    double ppk = calculatePpk(usl, lsl, mean, PPstdDev);
                    // Calculate min and max
                    double min = calculateMin(data);
                    double max = calculateMax(data);

                    // Output results to console
                    System.out.println("\nResults for iteration " + (c + 1) + ":");
                    System.out.println("Mean: " + mean);
                    System.out.println("Cp: " + cp);
                    System.out.println("Cpk: " + cpk);

                    // Export results to Excel
                    try {
                        exportToExcel(workbook, mean, cp, cpk, pp, ppk, min, max, c);  // Pass workbook to export function
                    } catch (IOException e) {
                        System.out.println("Error exporting data to Excel: " + e.getMessage());
                    }
                }
            }

            // Save the workbook after all iterations
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            workbook.close();
            fis.close();

        } catch(IOException e){
            System.out.println("Error reading the Excel file: " + e.getMessage());
        }
    }

    // Method to calculate mean
    public static double calculateMean(List<Double> data) {
        double sum = 0;
        for (double value : data) {
            sum += value;
        }
        return Math.round((sum / data.size()) * 100) / 100.0;
    }

    public static double calculateMin(List<Double> data) {
        double min = 100;
        for (double value : data) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    public static double calculateMax(List<Double> data) {
        double max = -100;
        for (double value : data) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    // Method to calculate standard deviation
    public static double calculateCPStandardDeviation(List<Double> data, double mean) {
        double sum = 0;
        for (double value : data) {
            sum += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sum / (data.size())); // Sample standard deviation (n)
    }

    // Method to calculate standard deviation
    public static double calculatePPStandardDeviation(List<Double> data, double mean) {
        double sum = 0;
        for (double value : data) {
            sum += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sum / (data.size() - 1)); // Sample standard deviation (n-1)
    }

    // Method to calculate Cp
    public static double calculateCp(double usl, double lsl, double stdDev) {
        double cp = (usl - lsl) / (6 * stdDev);
        return Math.round(cp * 100.0) / 100.0;
    }

    // Method to calculate Cpk
    public static double calculateCpk(double usl, double lsl, double mean, double stdDev) {
        double cpu = (usl - mean) / (3 * stdDev);
        double cpl = (mean - lsl) / (3 * stdDev);
        return Math.round(Math.min(cpu, cpl) * 100.0) / 100.0;
    }

    // Method to calculate Pp
    public static double calculatePp(double usl, double lsl, double stdDev) {
        double cp = (usl - lsl) / (6 * stdDev);
        return Math.round(cp * 100.0) / 100.0;
    }

    // Method to calculate Ppk
    public static double calculatePpk(double usl, double lsl, double mean, double stdDev) {
        double cpu = (usl - mean) / (3 * stdDev);
        double cpl = (mean - lsl) / (3 * stdDev);
        return Math.round(Math.min(cpu, cpl) * 100.0) / 100.0;
    }


    // Method to export results to Excel in the specific file and cells
    public static void exportToExcel(XSSFWorkbook workbook, double mean, double cp, double cpk, double pp, double ppk, double min, double max, int c) throws IOException {
        // Get the "Capability" sheet
        Sheet sheet = workbook.getSheet("Capability");

        // Write the Mean, Cp, Cpk, Pp, Ppk values into the specified cells
        Row row;
        // Writing Mean to C4
        row = sheet.getRow(rowCount); // Row 4 (0-indexed), C4 = column 2
        if (row == null) row = sheet.createRow(rowCount);
        row.createCell(c + 1).setCellValue(mean);

        // Writing Cp to C5
        row = sheet.getRow(rowCount + 1); // Row 5 (0-indexed), C5 = column 2
        if (row == null) row = sheet.createRow(rowCount + 1);
        row.createCell(c + 1).setCellValue(cp);

        // Writing Cpk to C6
        row = sheet.getRow(rowCount + 2); // Row 6 (0-indexed), C6 = column 2
        if (row == null) row = sheet.createRow(rowCount + 2);
        row.createCell(c + 1).setCellValue(cpk);

        // Writing Pp to C7
        row = sheet.getRow(rowCount + 3); // Row 7 (0-indexed), C7 = column 2
        if (row == null) row = sheet.createRow(rowCount + 3);
        row.createCell(c + 1).setCellValue(pp);

        // Writing Ppk to C8
        row = sheet.getRow(rowCount + 4); // Row 8 (0-indexed), C8 = column 2
        if (row == null) row = sheet.createRow(rowCount + 4);
        row.createCell(c + 1).setCellValue(ppk);

        // Writing min to C9
        row = sheet.getRow(rowCount + 5); // Row 8 (0-indexed), C8 = column 2
        if (row == null) row = sheet.createRow(rowCount + 5);
        row.createCell(c + 1).setCellValue(min);

        // Writing max to C10
        row = sheet.getRow(rowCount + 6);
        if (row == null) row = sheet.createRow(rowCount + 6);
        row.createCell(c + 1).setCellValue(max);
    }
}
