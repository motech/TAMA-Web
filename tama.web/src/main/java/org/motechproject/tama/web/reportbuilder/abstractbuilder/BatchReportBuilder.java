package org.motechproject.tama.web.reportbuilder.abstractbuilder;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.List;

public abstract class BatchReportBuilder<T> extends ReportBuilder<T> {

    protected final int pageSize;
    private static final int HEADER_ROW_COUNT = 4;

    public BatchReportBuilder() {
        super();
        pageSize = 10000;
        initializeColumns();
    }

    @Override
    protected boolean fillReportData(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForColumns(worksheet);
        List data = null;
        do {
            data = fetchData();
            for (Object dataObject : data) {
                HSSFRow row = worksheet.createRow((short) currentRowIndex);
                buildRowData(row, getRowData(dataObject), cellStyles);
                currentRowIndex++;
                if (currentRowIndex == (MAX_ROWS_PER_SHEET + HEADER_ROW_COUNT)) return false; // Have more data to fill
            }
        } while (data.size() == pageSize);
        return true;
    }

    protected abstract List fetchData();
}
