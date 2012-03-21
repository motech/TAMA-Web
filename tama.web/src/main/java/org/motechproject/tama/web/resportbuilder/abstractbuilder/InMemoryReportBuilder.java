package org.motechproject.tama.web.resportbuilder.abstractbuilder;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.motechproject.tama.web.resportbuilder.model.ExcelColumn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class InMemoryReportBuilder<T> extends ReportBuilder<T> {

    protected List<T> objects;
    protected int currentRowIndex = 0;

    public InMemoryReportBuilder(List<T> objects) {
        super();
        this.objects = objects;
        initializeColumns();
    }

    @Override
    protected void fillReport(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForColumns(worksheet);
        for (T object : objects) {
            HSSFRow row = worksheet.createRow((short) currentRowIndex);
            buildRowData(row, getRowData(object), cellStyles);
            currentRowIndex++;
        }
    }
}