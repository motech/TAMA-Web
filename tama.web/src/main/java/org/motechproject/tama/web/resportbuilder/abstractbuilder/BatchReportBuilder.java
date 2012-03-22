package org.motechproject.tama.web.resportbuilder.abstractbuilder;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.List;

public abstract class BatchReportBuilder<T> extends ReportBuilder<T> {

    private int pageNumber = 0;
    protected final int pageSize;

    public BatchReportBuilder() {
        super();
        pageSize = 100;
        initializeColumns();
    }

    @Override
    protected void fillReport(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForColumns(worksheet);
        List data = null;
        do {
            data = fetchData(pageNumber);
            for (Object dataObject : data) {
                HSSFRow row = worksheet.createRow((short) currentRowIndex);
                buildRowData(row, getRowData(dataObject), cellStyles);
                currentRowIndex++;
            }
            pageNumber++;
        } while (!data.isEmpty());
    }

    protected abstract List fetchData(int pageNumber);
}
