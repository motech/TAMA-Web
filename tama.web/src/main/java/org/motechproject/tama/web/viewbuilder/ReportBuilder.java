package org.motechproject.tama.web.viewbuilder;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.List;

public abstract class ReportBuilder<T> {

    public static final int TITLE_ROW_HEIGHT = 500;
    public static final int TITLE_FONT_HEIGHT = 280;
    public static final int HEADER_ROW_HEIGHT = 500;

    protected List<T> objects;
    protected List<ExcelColumn> columns = new ArrayList<ExcelColumn>();

    protected int currentRowIndex = 0;
    private int firstColumnIndex = 0;
    private int lastColumnIndex;

    public ReportBuilder(List<T> objects){
        this.objects = objects;
        initializeColumns();
        lastColumnIndex = columns.size();
    }

    protected abstract void initializeColumns();

    protected abstract String getWorksheetName();

    protected abstract String getTitle();

    protected abstract List<Object> getRowData(Object object);

    public HSSFWorkbook getExcelWorkbook() {

        HSSFWorkbook workbook = new HSSFWorkbook();

        addSheet(workbook);

        return workbook;
    }

    private void addSheet(HSSFWorkbook workbook) {
        HSSFSheet worksheet = workbook.createSheet(getWorksheetName());

        buildReportLayout(worksheet);

        fillReport(worksheet);
    }

    private void buildReportLayout(HSSFSheet worksheet) {
        setColumnWidths(worksheet);
        buildTitle(worksheet);
        buildSummary(worksheet);
        buildColumnHeaders(worksheet);
        worksheet.createFreezePane(lastColumnIndex, currentRowIndex);
    }

    protected void buildSummary(HSSFSheet worksheet) {

    }

    private void setColumnWidths(HSSFSheet worksheet) {
        int columnIndex = 0;
        for (ExcelColumn column : columns) {
            worksheet.setColumnWidth(columnIndex, column.getWidth());
            columnIndex++;
        }
    }

    private void buildTitle(HSSFSheet worksheet) {
        HSSFRow rowTitle = worksheet.createRow((short) currentRowIndex);
        rowTitle.setHeight((short) TITLE_ROW_HEIGHT);
        HSSFCell cellTitle = rowTitle.createCell(firstColumnIndex);
        cellTitle.setCellValue(getTitle());
        cellTitle.setCellStyle(getTitleCellStyle(worksheet));

        worksheet.addMergedRegion(new CellRangeAddress(currentRowIndex, currentRowIndex, firstColumnIndex, lastColumnIndex));
        currentRowIndex++;
    }

    private HSSFCellStyle getTitleCellStyle(HSSFSheet worksheet) {
        Font fontTitle = worksheet.getWorkbook().createFont();
        fontTitle.setBoldweight(Font.BOLDWEIGHT_BOLD);
        fontTitle.setFontHeight((short) TITLE_FONT_HEIGHT);

        HSSFCellStyle cellStyleTitle = worksheet.getWorkbook().createCellStyle();
        cellStyleTitle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyleTitle.setWrapText(true);
        cellStyleTitle.setFont(fontTitle);
        return cellStyleTitle;
    }

    private void buildColumnHeaders(HSSFSheet worksheet) {
        HSSFCellStyle headerCellStyle = getHeaderCellStyle(worksheet);

        HSSFRow headerRow = worksheet.createRow((short) currentRowIndex);
        headerRow.setHeight((short) HEADER_ROW_HEIGHT);

        int columnIndex = firstColumnIndex;
        for (ExcelColumn column : columns) {
            HSSFCell cell = headerRow.createCell(columnIndex);
            cell.setCellValue(column.getHeader());
            cell.setCellStyle(headerCellStyle);
            columnIndex++;
        }
        currentRowIndex++;
    }

    private HSSFCellStyle getHeaderCellStyle(HSSFSheet worksheet) {
        Font font = worksheet.getWorkbook().createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);

        HSSFCellStyle headerCellStyle = worksheet.getWorkbook().createCellStyle();
        headerCellStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
        headerCellStyle.setFillPattern(CellStyle.NO_FILL);
        headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headerCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        headerCellStyle.setWrapText(true);
        headerCellStyle.setFont(font);
        headerCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        return headerCellStyle;
    }


    private void fillReport(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForColumns(worksheet);
        for (T object : objects) {
            HSSFRow row = worksheet.createRow((short) currentRowIndex);
            buildRowData(row, getRowData(object), cellStyles);
            currentRowIndex++;
        }
    }

    private List<HSSFCellStyle> buildCellStylesForColumns(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = new ArrayList<HSSFCellStyle>();
        for(ExcelColumn column: columns) {
            HSSFCellStyle cellStyle = worksheet.getWorkbook().createCellStyle();
            cellStyle.setAlignment((short) column.getTextAlignment());
            cellStyle.setWrapText(true);
            cellStyles.add(cellStyle);
        }
        return cellStyles;
    }

    protected void buildRowData(HSSFRow row, List<Object> rowData, List<HSSFCellStyle> cellStyles)  {
        int columnIndex = firstColumnIndex;
        for (Object columnData : rowData) {
            HSSFCell cell = row.createCell(columnIndex);
            cell.setCellStyle(cellStyles.get(columnIndex));
            setCellTypeAndValue(getCellType(columnIndex), columnData, cell);
            columnIndex++;
        }
    }

    private int getCellType(int columnIndex) {
        return columns.get(columnIndex).getCellType();
    }

    private void setCellTypeAndValue(int cellType, Object columnData, HSSFCell cell) {
        if (cellType == Cell.CELL_TYPE_NUMERIC && columnData != null) {
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(Double.valueOf(columnData.toString()));
        } else {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(sanitize(columnData));
        }
    }

    private String sanitize(Object o) {
        return o == null ? "NA" : o.toString();
    }

}
