package org.motechproject.tama.web.resportbuilder.abstractbuilder;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.List;

public abstract class InMemoryReportBuilder<T> extends ReportBuilder<T> {

    protected List<T> objects;

    public InMemoryReportBuilder(List<T> objects) {
        super();
        this.objects = objects;
        initializeColumns();
    }

    @Override
    protected boolean fillReportData(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForColumns(worksheet);
        for (T object : objects) {
            HSSFRow row = worksheet.createRow((short) currentRowIndex);
            buildRowData(row, getRowData(object), cellStyles);
            currentRowIndex++;
        }
        // Done filling data
        return true;
    }
}
