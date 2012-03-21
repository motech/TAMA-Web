package org.motechproject.tama.web.resportbuilder.abstractbuilder;

import com.google.gson.Gson;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.deser.BeanDeserializer;
import org.codehaus.jackson.map.deser.BeanDeserializerFactory;
import org.ektorp.ViewResult;
import org.motechproject.tama.web.resportbuilder.model.ExcelColumn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BatchReportBuilder extends ReportBuilder {

    private ViewResult objects;
    private Class reportClass;

    public BatchReportBuilder(ViewResult objects, Class reportClass) {
        super();
        this.objects = objects;
        this.reportClass = reportClass;
        initializeColumns();
    }

    @Override
    protected void fillReport(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForColumns(worksheet);
        for (ViewResult.Row databaseRow : objects.getRows()) {
            Object object = new Gson().fromJson(databaseRow.getDocAsNode().toString(), reportClass);
            HSSFRow row = worksheet.createRow((short) currentRowIndex);
            buildRowData(row, getRowData(object), cellStyles);
            object = null;
            System.gc();
            currentRowIndex++;
        }
    }
}
