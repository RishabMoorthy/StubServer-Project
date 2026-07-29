package org.framework.datasource;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelDataSource extends AbstractDataSource implements SequentialDataSource, RandomWindowDataSource {

    public static final String PROP_WORKSHEET = "worksheet";
    public static final String PROP_HEADER_ROW_INDEX = "headerRowIndex";
    public static final String PROP_DATA_START_ROW_INDEX = "dataStartRowIndex";
    public static final String PROP_SKIP_EMPTY_ROWS = "skipEmptyRows";
    public static final String PROP_WINDOW_SIZE = "windowSize";

    private List<Map<String, String>> cachedRows;
    private final AtomicInteger sequencePointer = new AtomicInteger(0);
    public static final String PROP_ACCESS_MODE = "accessMode";

    private final AtomicInteger windowPointer = new AtomicInteger(0);
    private final Random random = new Random();

    private final Path filePath;

    private Workbook workbook;
    private Sheet sheet;
    private FormulaEvaluator formulaEvaluator;
    private final DataFormatter dataFormatter = new DataFormatter();

    private final Map<String, Integer> headerIndexMap = new LinkedHashMap<>();
    private Row currentRow;
    private int currentRowIndex = -1;
    private boolean loaded = false;

    public ExcelDataSource(String filePath) {
        this(Path.of(filePath));
    }

    public ExcelDataSource(Path filePath) {
        if (filePath == null) {
            throw new DataSourceException("Excel file path cannot be null.");
        }
        this.filePath = filePath;

        // Defaults
        setPropertyValue(PROP_HEADER_ROW_INDEX, 0);
        setPropertyValue(PROP_SKIP_EMPTY_ROWS, true);
    }

    @Override
    public void resetAndLoad() {
        close();

        validateFile();

        try {
            InputStream inputStream = Files.newInputStream(filePath);
            workbook = WorkbookFactory.create(inputStream);
            formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            resolveSheet();
            buildHeaderMap();

            AccessMode mode = getAccessMode();
            if (mode == AccessMode.SEQUENTIAL
                    || mode == AccessMode.RANDOM
                    || mode == AccessMode.RANDOM_WINDOW) {
                loadAllRowsIntoCache();
            } else {
                initCursorMode();
            }

            loaded = true;

        } catch (IOException e) {
            throw new DataSourceException("Failed to load Excel file: " + filePath, e);
        } catch (Exception e) {
            throw new DataSourceException("Failed to initialize Excel datasource from file: " + filePath, e);
        }
    }

    private void initCursorMode() {
        int headerRowIndex = getHeaderRowIndex();
        int dataStartRowIndex = getDataStartRowIndex(headerRowIndex);
        currentRowIndex = dataStartRowIndex - 1;
        currentRow = null;
    }

    private AccessMode getAccessMode() {
        Object val = getPropertyValue(PROP_ACCESS_MODE);
        return val == null ? AccessMode.QUERY
                : AccessMode.valueOf(val.toString());
    }

    private void loadAllRowsIntoCache() {

        cachedRows = new ArrayList<>();

        int headerRowIndex = getHeaderRowIndex();
        int dataStartRowIndex = getDataStartRowIndex(headerRowIndex);

        int lastRowNum = sheet.getLastRowNum();
        boolean skipEmpty = getBooleanProperty(PROP_SKIP_EMPTY_ROWS, true);

        for (int rowIndex = dataStartRowIndex; rowIndex <= lastRowNum; rowIndex++) {

            Row row = sheet.getRow(rowIndex);
            if (row == null || (skipEmpty && isRowEmpty(row))) {
                continue;
            }

            Map<String, String> rowMap = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : headerIndexMap.entrySet()) {
                Cell cell = row.getCell(entry.getValue(), MissingCellPolicy.RETURN_BLANK_AS_NULL);
                rowMap.put(entry.getKey(), getCellValueAsString(cell));
            }
            cachedRows.add(rowMap);
        }

        if (cachedRows.isEmpty()) {
            throw new DataSourceException("No data rows found for sequential access.");
        }
    }

    @Override
    public String getNextValue(String columnName) {

        ensureLoaded();

        if (cachedRows == null) {
            throw new DataSourceException(
                    "Sequential access not enabled. Set accessMode=SEQUENTIAL");
        }

        int index = Math.abs(sequencePointer.getAndIncrement());
        Map<String, String> row = cachedRows.get(index % cachedRows.size());

        return row.get(columnName);
    }

    @Override
    public boolean next() {
        ensureLoaded();

        int lastRowNum = sheet.getLastRowNum();
        boolean skipEmptyRows = getBooleanProperty(PROP_SKIP_EMPTY_ROWS, true);

        for (int rowIndex = currentRowIndex + 1; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);

            if (row == null) {
                if (skipEmptyRows) {
                    continue;
                } else {
                    currentRow = row;
                    currentRowIndex = rowIndex;
                    return true;
                }
            }

            if (skipEmptyRows && isRowEmpty(row)) {
                continue;
            }

            currentRow = row;
            currentRowIndex = rowIndex;
            return true;
        }

        currentRow = null;
        currentRowIndex = lastRowNum + 1;
        return false;
    }

    @Override
    public String getDataPropertyValue(String columnName) {
        ensureLoaded();
        ensureCurrentRow();

        if (columnName == null || columnName.trim().isEmpty()) {
            throw new DataSourceException("Column name cannot be null or empty.");
        }

        Integer columnIndex = headerIndexMap.get(columnName.trim());
        if (columnIndex == null) {
            throw new DataSourceException("Column not found in header row: " + columnName);
        }

        if (currentRow == null) {
            return "";
        }

        Cell cell = currentRow.getCell(columnIndex, MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return getCellValueAsString(cell);
    }

    @Override
    public Map<String, String> getCurrentRow() {
        ensureLoaded();
        ensureCurrentRow();

        Map<String, String> rowMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : headerIndexMap.entrySet()) {
            Cell cell = currentRow.getCell(entry.getValue(), MissingCellPolicy.RETURN_BLANK_AS_NULL);
            rowMap.put(entry.getKey(), getCellValueAsString(cell));
        }
        return rowMap;
    }

    @Override
    public void close() {

        if (getAccessMode() == AccessMode.SEQUENTIAL) {
            return;
        }

        currentRow = null;
        currentRowIndex = -1;
        headerIndexMap.clear();
        sheet = null;
        formulaEvaluator = null;
        loaded = false;

        if (workbook != null) {
            try {
                workbook.close();
            } catch (IOException e) {
                throw new DataSourceException("Failed to close workbook.", e);
            } finally {
                workbook = null;
            }
        }
    }

    private void validateFile() {
        if (!Files.exists(filePath)) {
            throw new DataSourceException("Excel file does not exist: " + filePath);
        }
        if (!Files.isRegularFile(filePath)) {
            throw new DataSourceException("Path is not a valid file: " + filePath);
        }
    }

    private void resolveSheet() {
        String worksheetName = getStringProperty(PROP_WORKSHEET);

        if (worksheetName == null || worksheetName.trim().isEmpty()) {
            sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
        } else {
            sheet = workbook.getSheet(worksheetName.trim());
        }

        if (sheet == null) {
            throw new DataSourceException("Worksheet not found: " + worksheetName);
        }
    }

    private void buildHeaderMap() {
        headerIndexMap.clear();

        int headerRowIndex = getHeaderRowIndex();
        Row headerRow = sheet.getRow(headerRowIndex);

        if (headerRow == null) {
            throw new DataSourceException("Header row not found at index: " + headerRowIndex);
        }

        short firstCellNum = headerRow.getFirstCellNum();
        short lastCellNum = headerRow.getLastCellNum();

        if (firstCellNum < 0 || lastCellNum < 0) {
            throw new DataSourceException("Header row is empty at index: " + headerRowIndex);
        }

        for (int cellIndex = firstCellNum; cellIndex < lastCellNum; cellIndex++) {
            Cell cell = headerRow.getCell(cellIndex, MissingCellPolicy.RETURN_BLANK_AS_NULL);
            String headerName = getCellValueAsString(cell).trim();

            if (headerName.isEmpty()) {
                continue;
            }

            if (headerIndexMap.containsKey(headerName)) {
                throw new DataSourceException("Duplicate header found: " + headerName);
            }

            headerIndexMap.put(headerName, cellIndex);
        }

        if (headerIndexMap.isEmpty()) {
            throw new DataSourceException("No headers found in header row index: " + headerRowIndex);
        }
    }

    private int getHeaderRowIndex() {
        int headerRowIndex = getIntProperty(PROP_HEADER_ROW_INDEX, 0);
        if (headerRowIndex < 0) {
            throw new DataSourceException("headerRowIndex cannot be negative.");
        }
        return headerRowIndex;
    }

    private int getDataStartRowIndex(int headerRowIndex) {
        int defaultDataStart = headerRowIndex + 1;
        int dataStartRowIndex = getIntProperty(PROP_DATA_START_ROW_INDEX, defaultDataStart);

        if (dataStartRowIndex < 0) {
            throw new DataSourceException("dataStartRowIndex cannot be negative.");
        }

        if (dataStartRowIndex <= headerRowIndex) {
            throw new DataSourceException(
                    "dataStartRowIndex must be greater than headerRowIndex. " +
                            "headerRowIndex=" + headerRowIndex + ", dataStartRowIndex=" + dataStartRowIndex);
        }

        return dataStartRowIndex;
    }

    private void ensureLoaded() {
        if (!loaded || workbook == null || sheet == null) {
            throw new DataSourceException("Datasource is not loaded. Call resetAndLoad() first.");
        }
    }

    private void ensureCurrentRow() {
        if (currentRow == null) {
            throw new DataSourceException("No current row selected. Call next() first.");
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        short firstCellNum = row.getFirstCellNum();
        short lastCellNum = row.getLastCellNum();

        if (firstCellNum < 0 || lastCellNum < 0) {
            return true;
        }

        for (int cellIndex = firstCellNum; cellIndex < lastCellNum; cellIndex++) {
            Cell cell = row.getCell(cellIndex, MissingCellPolicy.RETURN_BLANK_AS_NULL);
            String value = getCellValueAsString(cell);
            if (!value.trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public String getRandomWindowValue(String columnName) {

        ensureLoaded();

        if (cachedRows == null) {
            throw new DataSourceException(
                    "Random window access not enabled");
        }

        int windowSize = getIntProperty(PROP_WINDOW_SIZE, -1);
        if (windowSize <= 0) {
            throw new DataSourceException("windowSize must be > 0");
        }

        int totalRows = cachedRows.size();

        // Which window are we in?
        int windowIndex = windowPointer.getAndIncrement();

        int windowStart = windowIndex * windowSize;

        // Wrap when exceeding data
        if (windowStart >= totalRows) {
            windowPointer.set(1); // move to 2nd window next
            windowStart = 0;
        }

        int windowEnd = Math.min(windowStart + windowSize, totalRows);

        int randomIndex = windowStart + random.nextInt(windowEnd - windowStart);

        return cachedRows
                .get(randomIndex)
                .get(columnName);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        try {
            return dataFormatter.formatCellValue(cell, formulaEvaluator);
        } catch (Exception e) {
            // fallback safety
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue();
                case NUMERIC -> String.valueOf(cell.getNumericCellValue());
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                case FORMULA -> cell.getCellFormula();
                case BLANK, _NONE, ERROR -> "";
            };
        }
    }
}
