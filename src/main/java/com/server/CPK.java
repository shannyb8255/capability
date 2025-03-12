package com.server;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CPK {

    public static void main(String[] args) throws FileNotFoundException {

        String sourceFilePath = "C:/Users/sbreault/OneDrive - Hutchinson/Documents/Automating Capability/Test Template for Automating Capability/T90 Capability Full Study TEST.xlsx";
        String targetFilePath = "C:/Users/sbreault/OneDrive - Hutchinson/Documents/Automating Capability/Test Template for Automating Capability/CPK REPORTS TEST.xlsx";

        try {
            // Open the source workbook
            FileInputStream fisSource = new FileInputStream(new File(sourceFilePath));
            XSSFWorkbook sourceWorkbook = new XSSFWorkbook(fisSource);

            // Open the target workbook
            FileInputStream fisTarget = new FileInputStream(new File(targetFilePath));
            XSSFWorkbook targetWorkbook = new XSSFWorkbook(fisTarget);

            // Grab capability sheet from source workbook
            Sheet sourceSheet = sourceWorkbook.getSheet("Capability");

            for (int c = 0; c < 12; c++) {
                // Extract the data points from cells C4 to C10
                List<Double> data = new ArrayList<>();
                for (int i = 3; i <= 9; i++) {  // C4 to C10 are row 3 to 9 (0-indexed)
                    Row row = sourceSheet.getRow(i);
                    if (row != null) {
                        Cell cell = row.getCell(c);
                        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                            data.add(cell.getNumericCellValue());
                        }
                    }
                }
                // Export results to target workbook
                try {
                    exportToExcel(targetWorkbook, data, c);  // Pass target workbook, data, and column index to export function
                } catch (IOException e) {
                    System.out.println("Error exporting data to Excel: " + e.getMessage());
                }
            }

            // Save the target workbook after all iterations
            try (FileOutputStream fileOut = new FileOutputStream(targetFilePath)) {
                targetWorkbook.write(fileOut);
            }
            sourceWorkbook.close();
            fisSource.close();
            targetWorkbook.close();
            fisTarget.close();

        } catch (IOException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }

    // Method to export results to Excel in the specific file and cells
    public static void exportToExcel(XSSFWorkbook workbook, List<Double> data, int column) throws IOException {
        // Get the "Honda" sheet from target workbook
        Sheet sheet = workbook.getSheet("Honda");
        // Write the data values into the specified cells
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.getRow(13 + i); // Adjust the row index as needed
            if (row == null) row = sheet.createRow(13 + i);
            Cell cell = row.getCell(column);
            if (cell == null) cell = row.createCell(column);
            cell.setCellValue(data.get(i));
        }
    }
}
