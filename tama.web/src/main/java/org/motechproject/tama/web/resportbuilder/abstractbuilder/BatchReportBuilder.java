package org.motechproject.tama.web.resportbuilder.abstractbuilder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.List;

public abstract class BatchReportBuilder<T> extends ReportBuilder<T> {

    private int pageNumber = 0;
    protected final int pageSize;

    public BatchReportBuilder() {
        super();
        pageSize = 1000;
        initializeColumns();
    }

    @Override
    protected boolean fillReportData(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForColumns(worksheet);
        List data = null;
        do {
            data = fetchData(pageNumber);
            for (Object dataObject : data) {
                HSSFRow row = worksheet.createRow((short) currentRowIndex);
                buildRowData(row, getRowData(dataObject), cellStyles);
                boolean successfullyIncremented = incrementRowIndex();
                if (!successfullyIncremented) {
                    // Have more data to fill
                    pageNumber++;
                    return false;
                }
            }
            pageNumber++;
        } while (CollectionUtils.isNotEmpty(data));
        //Done filling data
        return true;
    }

    private boolean incrementRowIndex() {
        if (currentRowIndex <= MAX_ROWS_PER_SHEET) {
            currentRowIndex++;
            return true;
        } else {
            currentRowIndex = 0;
            return false;
        }
    }

    protected abstract List fetchData(int pageNumber);
}
