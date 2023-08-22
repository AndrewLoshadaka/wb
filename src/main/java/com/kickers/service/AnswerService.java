package com.kickers.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * для генерации ответов на отзывы
 * получаем из таблицы excel и отправляем
 * получить количество строк и столбцов
 * и рандомом к ячейке обращаться
 */

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final String path;
    private final InputStream inputStream;
    private final Workbook workbook;
    private final Sheet sheet;
    public AnswerService() {
        path = "book1.xlsx";
        try {
            inputStream = new FileInputStream(path);
            workbook = new XSSFWorkbook(inputStream);
            sheet = workbook.getSheetAt(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String createAnswer() {
        String answer = "";
        int rowCount = sheet.getPhysicalNumberOfRows();
        for(int i = 0; i < rowCount; i++){
            int columnNumber = (int) (1 + Math.random() * (getSizeRow(i) - 1));
            Cell cell = sheet.getRow(i)
                    .getCell(columnNumber);

            assert false;
            answer += (cell.getStringCellValue()) + " ";
            }

        return answer;
    }

    private int getSizeRow(int numberRow){
        return sheet.getRow(numberRow).getPhysicalNumberOfCells();
    }
}
