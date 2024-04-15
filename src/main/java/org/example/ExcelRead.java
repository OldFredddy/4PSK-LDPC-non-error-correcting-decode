package org.example;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelRead {
    public static List<String> readFromExcel(String path,int colNum) throws IOException {
        List<String> decodeTableWalsh = new ArrayList<>();
        HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(path));
        HSSFSheet myExcelSheet = myExcelBook.getSheet(myExcelBook.getSheetName(0));
        HSSFRow row;
        for (int i = 0; i < myExcelSheet.getLastRowNum(); i++) {
            row = myExcelSheet.getRow(i);
            if(row.getCell(colNum).getCellType() == HSSFCell.CELL_TYPE_STRING){
                decodeTableWalsh.add(row.getCell(colNum).getStringCellValue());

            }
        }

        myExcelBook.close();
      return decodeTableWalsh;
    }
}
