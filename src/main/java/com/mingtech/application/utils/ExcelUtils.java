package com.mingtech.application.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ExcelUtils {
	private static final String XLSX = ".xlsx";
    private static final String XLS = ".xls";
    public static final String ROW_MERGE = "row_merge";
    public static final String COLUMN_MERGE = "column_merge";
    private static final String ROW_NUM = "rowNum";
    private static final String ROW_DATA = "rowData";
    private static final String ROW_TIPS = "rowTips";
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();
  
  
    public static <T> List<T> readFile(File file, Class<T> clazz) throws Exception {
        JSONArray array = readFile(file);
        return getBeanList(array, clazz);
    }
  
    public static <T> List<T> readMultipartFile(MultipartFile mFile, Class<T> clazz) throws Exception {
        JSONArray array = readMultipartFile(mFile);
        return getBeanList(array, clazz);
    }
  
    public static JSONArray readFile(File file) throws Exception {
        return readExcel(null, file);
    }
  
    public static JSONArray readMultipartFile(MultipartFile mFile) throws Exception {
        return readExcel(mFile, null);
    }
  
    private static <T> List<T> getBeanList(JSONArray array, Class<T> clazz) throws Exception {
        List<T> list = new ArrayList<T>();
        Map<Integer, String> uniqueMap = new HashMap<Integer, String>(16);
        for (int i = 0; i < array.size(); i++) {
            list.add(getBean(clazz, array.getJSONObject(i), uniqueMap));
        }
        return list;
    }
  
    /**
     * ???????????????????????????
     */
    private static <T> T getBean(Class<T> c, JSONObject obj, Map<Integer, String> uniqueMap) throws Exception {
        T t = c.newInstance();
        Field[] fields = c.getDeclaredFields();
        List<String> errMsgList = new ArrayList<String>();
        boolean hasRowTipsField = false;
        StringBuilder uniqueBuilder = new StringBuilder();
        int rowNum = 0;
        for (Field field : fields) {
            // ??????
            if (field.getName().equals(ROW_NUM)) {
                rowNum = obj.getInteger(ROW_NUM);
                field.setAccessible(true);
                field.set(t, rowNum);
                continue;
            }
            // ??????????????????????????????
            if (field.getName().equals(ROW_TIPS)) {
                hasRowTipsField = true;
                continue;
            }
            // ????????????
            if (field.getName().equals(ROW_DATA)) {
                field.setAccessible(true);
                field.set(t, obj.toString());
                continue;
            }
            // ?????????????????????
            setFieldValue(t,field, obj, uniqueBuilder, errMsgList);
        }
        // ?????????????????????
        if (uniqueBuilder.length() > 0) {
            if (uniqueMap.containsValue(uniqueBuilder.toString())) {
                Set<Integer> rowNumKeys = uniqueMap.keySet();
                for (Integer num : rowNumKeys) {
                    if (uniqueMap.get(num).equals(uniqueBuilder.toString())) {
                        errMsgList.add(String.format("???????????????????????????,(%s)??????%s?????????)", uniqueBuilder, num));
                    }
                }
            } else {
                uniqueMap.put(rowNum, uniqueBuilder.toString());
            }
        }
        // ????????????
        if (errMsgList.isEmpty() && !hasRowTipsField) {
            return t;
        }
        StringBuilder sb = new StringBuilder();
        int size = errMsgList.size();
        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                sb.append(errMsgList.get(i));
            } else {
                sb.append(errMsgList.get(i)).append(";");
            }
        }
        // ??????????????????
        for (Field field : fields) {
            if (field.getName().equals(ROW_TIPS)) {
                field.setAccessible(true);
                field.set(t, sb.toString());
            }
        }
        return t;
    }
  
    private static <T> void setFieldValue(T t, Field field, JSONObject obj, StringBuilder uniqueBuilder, List<String> errMsgList) {
        // ?????? ExcelImport ????????????
        ExcelImport annotation = field.getAnnotation(ExcelImport.class);
        if (annotation == null) {
            return;
        }
        String cname = annotation.value();
        if (cname.trim().length() == 0) {
            return;
        }
        // ???????????????
        String val = null;
        if (obj.containsKey(cname)) {
            val = getString(obj.getString(cname));
        }
        if (val == null) {
            return;
        }
        field.setAccessible(true);
        // ??????????????????
        boolean require = annotation.required();
        if (require && val.isEmpty()) {
            errMsgList.add(String.format("[%s]????????????", cname));
            return;
        }
        // ?????????????????????
        boolean unique = annotation.unique();
        if (unique) {
            if (uniqueBuilder.length() > 0) {
                uniqueBuilder.append("--").append(val);
            } else {
                uniqueBuilder.append(val);
            }
        }
        // ??????????????????????????????
        int maxLength = annotation.maxLength();
        if (maxLength > 0 && val.length() > maxLength) {
            errMsgList.add(String.format("[%s]??????????????????%s?????????(??????%s?????????)", cname, maxLength, val.length()));
        }
        // ???????????????????????????????????????
        LinkedHashMap<String, String> kvMap = getKvMap(annotation.kv());
        if (!kvMap.isEmpty()) {
            boolean isMatch = false;
            for (String key : kvMap.keySet()) {
                if (kvMap.get(key).equals(val)) {
                    val = key;
                    isMatch = true;
                    break;
                }
            }
            if (!isMatch) {
                errMsgList.add(String.format("[%s]???????????????(????????????%s)", cname, val));
                return;
            }
        }
        // ??????????????????????????????
        String fieldClassName = field.getType().getSimpleName();
        try {
            if ("String".equalsIgnoreCase(fieldClassName)) {
                field.set(t, val);
            } else if ("boolean".equalsIgnoreCase(fieldClassName)) {
                field.set(t, Boolean.valueOf(val));
            } else if ("int".equalsIgnoreCase(fieldClassName) || "Integer".equals(fieldClassName)) {
                try {
                    field.set(t, Integer.valueOf(val));
                } catch (NumberFormatException e) {
                    errMsgList.add(String.format("[%s]?????????????????????(????????????%s)", cname, val));
                }
            } else if ("double".equalsIgnoreCase(fieldClassName)) {
                field.set(t, Double.valueOf(val));
            } else if ("long".equalsIgnoreCase(fieldClassName)) {
                field.set(t, Long.valueOf(val));
            } else if ("BigDecimal".equalsIgnoreCase(fieldClassName)) {
                field.set(t, new BigDecimal(val));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
    private static JSONArray readExcel(MultipartFile mFile, File file) throws IOException {
        boolean fileNotExist = (file == null || !file.exists());
        if (mFile == null && fileNotExist) {
            return new JSONArray();
        }
        // ??????????????????
        InputStream in;
        String fileName;
        if (mFile != null) {
            // ??????????????????
            in = mFile.getInputStream();
            fileName = getString(mFile.getOriginalFilename()).toLowerCase();
        } else {
            // ??????????????????
            in = new FileInputStream(file);
            fileName = file.getName().toLowerCase();
        }
        Workbook book;
        if (fileName.endsWith(XLSX)) {
            book = new XSSFWorkbook(in);
        } else if (fileName.endsWith(XLS)) {
            POIFSFileSystem poifsFileSystem = new POIFSFileSystem(in);
            book = new HSSFWorkbook(poifsFileSystem);
        } else {
            return new JSONArray();
        }
        JSONArray array = read(book);
        book.close();
        in.close();
        return array;
    }
  
    private static JSONArray read(Workbook book) {
        // ?????? Excel ??????????????? Sheet ??????
        Sheet sheet = book.getSheetAt(0);
        return readSheet(sheet);
    }
  
    private static JSONArray readSheet(Sheet sheet) {
        // ????????????
        int rowStart = sheet.getFirstRowNum();
        // ????????????
        int rowEnd = sheet.getLastRowNum();
        // ???????????????
        Row headRow = sheet.getRow(rowStart);
        if (headRow == null) {
            return new JSONArray();
        }
        int cellStart = headRow.getFirstCellNum();
        int cellEnd = headRow.getLastCellNum();
        Map<Integer, String> keyMap = new HashMap<Integer, String>();
        for (int j = cellStart; j < cellEnd; j++) {
            // ??????????????????
            String val = getCellValue(headRow.getCell(j));
            if (val != null && val.trim().length() != 0) {
                keyMap.put(j, val);
            }
        }
        // ??????????????????????????????????????????
        if (keyMap.isEmpty()) {
            return (JSONArray) Collections.emptyList();
        }
        // ????????????JSON????????????
        JSONArray array = new JSONArray();
        // ?????????????????????????????????????????????????????????????????????
        if (rowStart == rowEnd) {
            JSONObject obj = new JSONObject();
            // ????????????
            obj.put(ROW_NUM, 1);
            for (int i : keyMap.keySet()) {
                obj.put(keyMap.get(i), "");
            }
            array.add(obj);
            return array;
        }
        for (int i = rowStart + 1; i <= rowEnd; i++) {
            Row eachRow = sheet.getRow(i);
            JSONObject obj = new JSONObject();
            // ????????????
            obj.put(ROW_NUM, i + 1);
            StringBuilder sb = new StringBuilder();
            for (int k = cellStart; k < cellEnd; k++) {
                if (eachRow != null) {
                    String val = getCellValue(eachRow.getCell(k));
                    // ????????????????????????????????????????????????????????????
                    sb.append(val);
                    obj.put(keyMap.get(k), val);
                }
            }
            if (sb.length() > 0) {
                array.add(obj);
            }
        }
        return array;
    }
  
    private static String getCellValue(Cell cell) {
        // ????????????
        if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
            return "";
        }
        // String??????
        if (cell.getCellTypeEnum() == CellType.STRING) {
            String val = cell.getStringCellValue();
            if (val == null || val.trim().length() == 0) {
                return "";
            }
            return val.trim();
        }
        // ????????????
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            // ?????????????????????
            return NUMBER_FORMAT.format(cell.getNumericCellValue()) + "";
        }
        // ???????????????
        if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
            return cell.getBooleanCellValue() + "";
        }
        // ????????????
        return cell.getCellFormula();
    }
  
    private static LinkedHashMap<String, String> getKvMap(String kv) {
        LinkedHashMap<String, String> kvMap = new LinkedHashMap<String, String>();
        if (kv.isEmpty()) {
            return kvMap;
        }
        String[] kvs = kv.split(";");
        if (kvs.length == 0) {
            return kvMap;
        }
        for (String each : kvs) {
            String[] eachKv = getString(each).split("-");
            if (eachKv.length != 2) {
                continue;
            }
            String k = eachKv[0];
            String v = eachKv[1];
            if (k.isEmpty() || v.isEmpty()) {
                continue;
            }
            kvMap.put(k, v);
        }
        return kvMap;
    }
    
    private static String getString(String s) {
        if (s == null) {
            return "";
        }
        if (s.isEmpty()) {
            return s;
        }
        return s.trim();
    }
  
}
