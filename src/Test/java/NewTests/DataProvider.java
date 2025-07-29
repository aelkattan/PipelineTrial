package NewTests;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class DataProvider {
    public static String[] getTestData() throws IOException {
        FileInputStream fis = new FileInputStream("src\\test\\TestData\\Booking TestData - Copy.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheet("TestData");
        String checkIn = sheet.getRow(1).getCell(0).getStringCellValue();
        String checkOut = sheet.getRow(1).getCell(1).getStringCellValue();
        String location = sheet.getRow(1).getCell(2).getStringCellValue();

//        workbook.close();
        fis.close();

        return new String[]{checkIn, checkOut, location};
    }
}
