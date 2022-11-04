package com.mingtech.application.utils;

/**
 * @author silen
 * @date 2021/10/31 15:02
 */

public class DraftRange {

    /**
     * 子票区间起始
     */
    private String beginDraftRange;

    /**
     * 子票区间截止
     */
    private String endDraftRange;


    public DraftRange(String beginDraftRange, String endDraftRange) {
        this.beginDraftRange = beginDraftRange;
        this.endDraftRange = endDraftRange;
    }

    public String getBeginDraftRange() {
        return beginDraftRange;
    }


    public String getEndDraftRange() {
        return endDraftRange;
    }
}
