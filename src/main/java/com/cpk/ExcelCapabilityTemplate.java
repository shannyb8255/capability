package com.cpk;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExcelCapabilityTemplate {

    public void processFile(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            for (Sheet sheet : workbook) {
                if (!sheet.getSheetName().contains("Data")) continue;
                System.out.println("Processing sheet: " + sheet.getSheetName());

                int colCount = detectColumnCount(sheet);
                int entryCount = detectEntryCount(sheet, 1); // Check column B (index 1)
                String targetSheet = sheet.getSheetName().contains("FD") ? "Capability (FD)" : "Capability (RD)";
                int rowCount = sheet.getSheetName().contains("RH") ? 13 : 3;

                for (int c = 1; c < colCount; c++) {
                    String toleranceInput = sheet.getRow(3).getCell(c).getStringCellValue().trim();
                    double tolerance = Double.parseDouble(toleranceInput.split(" ")[1]);

                    List<Double> data = new ArrayList<>();
                    for (int i = 4; i < 4 + entryCount; i++) {
                        Cell cell = sheet.getRow(i).getCell(c);
                        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                            data.add(cell.getNumericCellValue());
                        }
                    }

                    double mean = CapabilityCalculator.calculateMean(data);
                    double cpStd = CapabilityCalculator.calculateCPStandardDeviation(data, mean);
                    double ppStd = CapabilityCalculator.calculatePPStandardDeviation(data, mean);

                    double usl = 0 + tolerance;
                    double lsl = 0 - tolerance;

                    double cp = CapabilityCalculator.calculateCp(usl, lsl, cpStd);
                    double cpk = CapabilityCalculator.calculateCpk(usl, lsl, mean, cpStd);
                    double pp = CapabilityCalculator.calculatePp(usl, lsl, ppStd);
                    double ppk = CapabilityCalculator.calculatePpk(usl, lsl, mean, ppStd);
                    double min = CapabilityCalculator.calculateMin(data);
                    double max = CapabilityCalculator.calculateMax(data);

                    writeToSheet(workbook.getSheet(targetSheet), rowCount, c, mean, cp, cpk, pp, ppk, min, max);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            System.out.println("Processing complete and saved: " + file.getName());

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private int detectColumnCount(Sheet sheet) {
        Row row = sheet.getRow(3); // Row 4 (0-indexed)
        int count = 0;
        for (Cell cell : row) {
            if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().contains("±")) {
                count++;
            }
        }
        return count;
    }

    private int detectEntryCount(Sheet sheet, int colIndex) {
        int count = 0;
        for (int i = 4; ; i++) {
            Row row = sheet.getRow(i);
            if (row == null || row.getCell(colIndex) == null) break;
            if (row.getCell(colIndex).getCellType() == CellType.NUMERIC) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    private void writeToSheet(Sheet sheet, int baseRow, int col,
                              double mean, double cp, double cpk,
                              double pp, double ppk, double min, double max) {
        double[] values = {mean, cp, cpk, pp, ppk, min, max};
        for (int i = 0; i < values.length; i++) {
            Row row = sheet.getRow(baseRow + i);
            if (row == null) row = sheet.createRow(baseRow + i);
            Cell cell = row.getCell(col + 1);
                if (cell == null) cell = row.createCell(col + 1);

            cell.setCellValue(values[i]);
        }
    }
}