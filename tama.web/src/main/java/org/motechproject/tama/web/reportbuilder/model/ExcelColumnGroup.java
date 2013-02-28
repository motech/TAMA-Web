package org.motechproject.tama.web.reportbuilder.model;

import lombok.Data;

@Data
public class ExcelColumnGroup extends ExcelColumn {
    private int level;
    private int startColumnIndex;
    private int endColumnIndex;

    public ExcelColumnGroup(String header, int cellType, int level, int startColumnIndex, int endColumnIndex) {
        super(header, cellType);
        this.level = level;
        this.startColumnIndex = startColumnIndex;
        this.endColumnIndex = endColumnIndex;
    }
}
